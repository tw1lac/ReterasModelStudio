package com.hiveworkshop.wc3.mdx;

import com.hiveworkshop.wc3.mdl.AnimFlag;
import com.hiveworkshop.wc3.mdl.Vertex;
import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CornChunk {
	public ParticleEmitterPopcorn[] corns = new ParticleEmitterPopcorn[0];

	public static final String key = "CORN";

	public void load(final BlizzardDataInputStream in) throws IOException {
		MdxUtils.checkId(in, key);
		final int chunkSize = in.readInt();
		final List<ParticleEmitterPopcorn> cornList = new ArrayList<>();
		int lightCounter = chunkSize;
		while (lightCounter > 0) {
			final ParticleEmitterPopcorn tempcorn = new ParticleEmitterPopcorn();
			cornList.add(tempcorn);
			tempcorn.load(in);
			lightCounter -= tempcorn.getSize();
		}
		corns = cornList.toArray(new ParticleEmitterPopcorn[0]);
	}

	public void save(final BlizzardDataOutputStream out) throws IOException {
		final int nrOfLights = corns.length;
		out.writeNByteString(key, 4);
		out.writeInt(getSize() - 8);// ChunkSize
		for (ParticleEmitterPopcorn corn : corns) {
			corn.save(out);
		}

	}

	public int getSize() {
		int a = 0;
		a += 4;
		a += 4;
		for (ParticleEmitterPopcorn corn : corns) {
			a += corn.getSize();
		}

		return a;
	}

	public class ParticleEmitterPopcorn {
		public Node node = new Node();
		public int replaceableId;
		public float alpha;
		public float[] color;
		public float speed;
		public float emissionRate;
		public float lifeSpan;
		public String path;
		public String flags;
		public CornAlpha cornAlpha;
		public CornEmissionRate cornEmissionRate;
		public CornVisibility cornVisibility;
		public CornSpeed cornSpeed;
		public CornLifeSpan cornLifeSpan;
		public CornColor cornColor;

		public void load(final BlizzardDataInputStream in) throws IOException {
			final int inclusiveSize = in.readInt();
			node = new Node();
			node.load(in);
			lifeSpan = in.readFloat();
			emissionRate = in.readFloat();
			speed = in.readFloat();
			color = MdxUtils.loadFloatArray(in, 3);
			alpha = in.readFloat();
			replaceableId = in.readInt();
			path = in.readCharsAsString(260);
			flags = in.readCharsAsString(260);
			for (int i = 0; i < 6; i++) {
				if (MdxUtils.checkOptionalId(in, CornAlpha.key)) {
					cornAlpha = new CornAlpha();
					cornAlpha.load(in);
				} else if (MdxUtils.checkOptionalId(in, CornEmissionRate.key)) {
					cornEmissionRate = new CornEmissionRate();
					cornEmissionRate.load(in);
				} else if (MdxUtils.checkOptionalId(in, CornVisibility.key)) {
					cornVisibility = new CornVisibility();
					cornVisibility.load(in);
				} else if (MdxUtils.checkOptionalId(in, CornSpeed.key)) {
					cornSpeed = new CornSpeed();
					cornSpeed.load(in);
				} else if (MdxUtils.checkOptionalId(in, CornLifeSpan.key)) {
					cornLifeSpan = new CornLifeSpan();
					cornLifeSpan.load(in);
				} else if (MdxUtils.checkOptionalId(in, CornColor.key)) {
					cornColor = new CornColor();
					cornColor.load(in);
				}

			}
		}

		public void save(final BlizzardDataOutputStream out) throws IOException {
			out.writeInt(getSize());// InclusiveSize
			node.save(out);
			out.writeFloat(lifeSpan);
			out.writeFloat(emissionRate);
			out.writeFloat(speed);
			MdxUtils.saveFloatArray(out, color);
			out.writeFloat(alpha);
			out.writeInt(replaceableId);
			out.writeNByteString(path, 260);
			out.writeNByteString(flags, 260);
			if (cornAlpha != null) {
				cornAlpha.save(out);
			}
			if (cornEmissionRate != null) {
				cornEmissionRate.save(out);
			}
			if (cornVisibility != null) {
				cornVisibility.save(out);
			}
			if (cornSpeed != null) {
				cornSpeed.save(out);
			}
			if (cornLifeSpan != null) {
				cornLifeSpan.save(out);
			}
			if (cornColor != null) {
				cornColor.save(out);
			}

		}

		public int getSize() {
			int a = 0;
			a += 4;
			a += node.getSize();
			a += 32;
			a += 260;
			a += 260;
			if (cornAlpha != null) {
				a += cornAlpha.getSize();
			}
			if (cornEmissionRate != null) {
				a += cornEmissionRate.getSize();
			}
			if (cornVisibility != null) {
				a += cornVisibility.getSize();
			}
			if (cornSpeed != null) {
				a += cornSpeed.getSize();
			}
			if (cornLifeSpan != null) {
				a += cornLifeSpan.getSize();
			}
			if (cornColor != null) {
				a += cornColor.getSize();
			}

			return a;
		}

		public ParticleEmitterPopcorn() {

		}

		public ParticleEmitterPopcorn(final com.hiveworkshop.wc3.mdl.ParticleEmitterPopcorn light) {
			node = new Node(light);
//			node.flags |= 0x200;
			node.flags |= 0x1000;
//			if (light.isTeamColor()) {
//				node.flags |= Node.NodeFlag.TEAM_COLORED_CORN.getValue();
//			}
			// more to do here
			for (final AnimFlag af : light.getAnimFlags()) {
				if (af.getName().equals("Visibility")) {
					cornVisibility = new CornVisibility();
					cornVisibility.globalSequenceId = af.getGlobalSeqId();
					cornVisibility.interpolationType = af.getInterpType();
					cornVisibility.visibilityTrack = new CornVisibility.VisibilityTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final CornVisibility.VisibilityTrack mdxEntry = cornVisibility.new VisibilityTrack();
						cornVisibility.visibilityTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.visibility = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
				} else if (af.getName().equals("EmissionRate")) {
					cornEmissionRate = new CornEmissionRate();
					cornEmissionRate.globalSequenceId = af.getGlobalSeqId();
					cornEmissionRate.interpolationType = af.getInterpType();
					cornEmissionRate.emissionRateTrack = new CornEmissionRate.EmissionRateTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final CornEmissionRate.EmissionRateTrack mdxEntry = cornEmissionRate.new EmissionRateTrack();
						cornEmissionRate.emissionRateTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.emissionRate = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
				} else if (af.getName().equals("Alpha")) {
					cornAlpha = new CornAlpha();
					cornAlpha.globalSequenceId = af.getGlobalSeqId();
					cornAlpha.interpolationType = af.getInterpType();
					cornAlpha.alphaTrack = new CornAlpha.AlphaTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final CornAlpha.AlphaTrack mdxEntry = cornAlpha.new AlphaTrack();
						cornAlpha.alphaTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.alpha = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
				} else if (af.getName().equals("Speed")) {
					cornSpeed = new CornSpeed();
					cornSpeed.globalSequenceId = af.getGlobalSeqId();
					cornSpeed.interpolationType = af.getInterpType();
					cornSpeed.speedTrack = new CornSpeed.SpeedTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final CornSpeed.SpeedTrack mdxEntry = cornSpeed.new SpeedTrack();
						cornSpeed.speedTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.speed = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
				} else if (af.getName().equals("LifeSpan")) {
					cornLifeSpan = new CornLifeSpan();
					cornLifeSpan.globalSequenceId = af.getGlobalSeqId();
					cornLifeSpan.interpolationType = af.getInterpType();
					cornLifeSpan.lifeSpanTrack = new CornLifeSpan.LifeSpanTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final CornLifeSpan.LifeSpanTrack mdxEntry = cornLifeSpan.new LifeSpanTrack();
						cornLifeSpan.lifeSpanTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.lifeSpan = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
				} else if (af.getName().equals("Color") && (af.size() > 0)) {
					cornColor = new CornColor();
					cornColor.globalSequenceId = af.getGlobalSeqId();
					cornColor.interpolationType = af.getInterpType();
					cornColor.scalingTrack = new CornColor.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final CornColor.ScalingTrack mdxEntry = cornColor.new ScalingTrack();
						cornColor.scalingTrack[i] = mdxEntry;
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
				} else {
					if (Node.LOG_DISCARDED_FLAGS) {
						System.err.println("discarded flag " + af.getName());
					}
				}
			}

			lifeSpan = light.getLifeSpan();
			emissionRate = light.getEmissionRate();
			speed = light.getSpeed();
			if (light.getColor() != null) {
				color = light.getColor().toFloatArray();
				final float blue = color[0];
				color[0] = color[2];
				color[2] = blue;
				// TODO: COPIED FROM ELSEWHERE, HOPING IT MATCHES REFORGED: this chunk is RGB,
				// mdl is BGR
			} else {
				color = new float[] { 1.0f, 1.0f, 1.0f };
			}
			alpha = light.getAlpha();
			replaceableId = light.getReplaceableId();
			path = light.getPath();
			flags = light.getAnimVisibilityGuide();
		}
	}
}
