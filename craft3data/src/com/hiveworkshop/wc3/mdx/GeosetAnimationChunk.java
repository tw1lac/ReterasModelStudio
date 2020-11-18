package com.hiveworkshop.wc3.mdx;

import com.hiveworkshop.wc3.mdl.AnimFlag;
import com.hiveworkshop.wc3.mdl.GeosetAnim;
import com.hiveworkshop.wc3.mdl.Vertex;
import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeosetAnimationChunk {
	public GeosetAnimation[] geosetAnimation = new GeosetAnimation[0];

	public static final String key = "GEOA";

	public void load(final BlizzardDataInputStream in) throws IOException {
		MdxUtils.checkId(in, "GEOA");
		final int chunkSize = in.readInt();
		final List<GeosetAnimation> geosetAnimationList = new ArrayList<>();
		int geosetAnimationCounter = chunkSize;
		while (geosetAnimationCounter > 0) {
			final GeosetAnimation tempgeosetAnimation = new GeosetAnimation();
			geosetAnimationList.add(tempgeosetAnimation);
			tempgeosetAnimation.load(in);
			geosetAnimationCounter -= tempgeosetAnimation.getSize();
		}
		geosetAnimation = geosetAnimationList.toArray(new GeosetAnimation[0]);
	}

	public void save(final BlizzardDataOutputStream out) throws IOException {
		final int nrOfGeosetAnimations = geosetAnimation.length;
		out.writeNByteString("GEOA", 4);
		out.writeInt(getSize() - 8);// ChunkSize
        for (GeosetAnimation animation : geosetAnimation) {
            animation.save(out);
        }

	}

	public int getSize() {
		int a = 0;
		a += 4;
		a += 4;
        for (GeosetAnimation animation : geosetAnimation) {
            a += animation.getSize();
        }

		return a;
	}

	public class GeosetAnimation {
		public float alpha;
		public int flags;
		public float[] color = new float[3];
		public int geosetId;
		public GeosetAlpha geosetAlpha;
		public GeosetColor geosetColor;

		public void load(final BlizzardDataInputStream in) throws IOException {
			final int inclusiveSize = in.readInt();
			alpha = in.readFloat();
			flags = in.readInt();
			color = MdxUtils.loadFloatArray(in, 3);
			geosetId = in.readInt();
			for (int i = 0; i < 2; i++) {
				if (MdxUtils.checkOptionalId(in, GeosetAlpha.key)) {
					geosetAlpha = new GeosetAlpha();
					geosetAlpha.load(in);
				} else if (MdxUtils.checkOptionalId(in, GeosetColor.key)) {
					geosetColor = new GeosetColor();
					geosetColor.load(in);
				}

			}
		}

		public void save(final BlizzardDataOutputStream out) throws IOException {
			out.writeInt(getSize());// InclusiveSize
			out.writeFloat(alpha);
			out.writeInt(flags);
			if (color.length % 3 != 0) {
				throw new IllegalArgumentException(
						"The array color needs either the length 3 or a multiple of this number. (got " + color.length
								+ ")");
			}
			MdxUtils.saveFloatArray(out, color);
			out.writeInt(geosetId);
			if (geosetAlpha != null) {
				geosetAlpha.save(out);
			}
			if (geosetColor != null) {
				geosetColor.save(out);
			}

		}

		public int getSize() {
			int a = 0;
			a += 4;
			a += 4;
			a += 4;
			a += 12;
			a += 4;
			if (geosetAlpha != null) {
				a += geosetAlpha.getSize();
			}
			if (geosetColor != null) {
				a += geosetColor.getSize();
			}

			return a;
		}

		public GeosetAnimation() {

		}

		public GeosetAnimation(final GeosetAnim mdlGeoAnim) {
			boolean alphaFound = false;
			boolean colorFound = false;
			for (final AnimFlag af : mdlGeoAnim.getAnimFlags()) {
				if (af.getName().equals("Alpha") && af.size() > 0) {
					geosetAlpha = new GeosetAlpha();
					geosetAlpha.globalSequenceId = af.getGlobalSeqId();
					geosetAlpha.interpolationType = af.getInterpType();
					geosetAlpha.scalingTrack = new GeosetAlpha.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final GeosetAlpha.ScalingTrack mdxEntry = geosetAlpha.new ScalingTrack();
						geosetAlpha.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.alpha = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
					alphaFound = true;
				} else if (af.getName().equals("Color") && af.size() > 0) {
					geosetColor = new GeosetColor();
					geosetColor.globalSequenceId = af.getGlobalSeqId();
					geosetColor.interpolationType = af.getInterpType();
					geosetColor.scalingTrack = new GeosetColor.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final GeosetColor.ScalingTrack mdxEntry = geosetColor.new ScalingTrack();
						geosetColor.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.color = ((Vertex) mdlEntry.value).toFloatArray();
						// ========== RGB for some reason, mdl is BGR
						// ==============
						// final float blue = mdxEntry.color[0];
						// mdxEntry.color[0] = mdxEntry.color[2];
						// mdxEntry.color[2] = blue;
						// ========== RGB for some reason, mdl is BGR
						// ==============
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Vertex) mdlEntry.inTan).toFloatArray();
							mdxEntry.outTan = ((Vertex) mdlEntry.outTan).toFloatArray();
						}
					}
					colorFound = true;
				} else {
					if (Node.LOG_DISCARDED_FLAGS) {
						System.err.println("discarded flag " + af.getName());
					}
				}
			}
			if (alphaFound || Math.abs(mdlGeoAnim.getStaticAlpha() - (-1)) <= 0.001) {
				alpha = 1.0f;
			} else {
				alpha = (float) mdlGeoAnim.getStaticAlpha();
			}
			if (mdlGeoAnim.isDropShadow()) {
				flags |= 1;
			}
			if (mdlGeoAnim.getStaticColor() != null || colorFound) {
				flags |= 2;
			}
			if (mdlGeoAnim.getStaticColor() != null) {
				color = mdlGeoAnim.getStaticColor().toFloatArray();
				final float blue = color[0];
				color[0] = color[2];
				color[2] = blue;
				// this chunk is RGB, mdl is BGR
			} else {
				color = new float[] { 1.0f, 1.0f, 1.0f };
			}
			geosetId = mdlGeoAnim.getGeosetId();
		}
	}
}
