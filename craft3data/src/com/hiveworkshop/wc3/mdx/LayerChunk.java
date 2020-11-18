package com.hiveworkshop.wc3.mdx;

import java.io.IOException;

import com.hiveworkshop.wc3.mdl.AnimFlag;
import com.hiveworkshop.wc3.mdl.Vertex;
import com.hiveworkshop.wc3.util.ModelUtils;

import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

public class LayerChunk {
	public Layer[] layer = new Layer[0];

	public static final String key = "LAYS";

	public void load(final BlizzardDataInputStream in, final int version) throws IOException {
		MdxUtils.checkId(in, "LAYS");
		final int nrOfLayers = in.readInt();
		layer = new Layer[nrOfLayers];
		for (int i = 0; i < nrOfLayers; i++) {
			layer[i] = new Layer();
			layer[i].load(in, version);
		}
	}

	public void save(final BlizzardDataOutputStream out, final int version) throws IOException {
		final int nrOfLayers = layer.length;
		out.writeNByteString("LAYS", 4);
		out.writeInt(nrOfLayers);
        for (Layer value : layer) {
            value.save(out, version);
        }

	}

	public int getSize(final int version) {
		int a = 0;
		a += 4;
		a += 4;
        for (Layer value : layer) {
            a += value.getSize(version);
        }

		return a;
	}

	public class Layer {
		public int filterMode;
		public int shadingFlags;
		public int textureId;
		public int textureAnimationId;
		public int coordID;
		public float alpha = 1;
		public float emissiveGain = Float.NaN;
		public float[] fresnelColor;
		public float fresnelOpacity;
		public float fresnelTeamColor;
		public MaterialAlpha materialAlpha;
		public MaterialTextureId materialTextureId;
		public MaterialEmissiveGain materialEmissions;
		public MaterialFresnelColor materialFresnelColor;
		public MaterialFresnelOpacity materialFresnelOpacity;
		public MaterialFresnelTeamColor materialFresnelTeamColor;

		public void load(final BlizzardDataInputStream in, final int version) throws IOException {
			final int inclusiveSize = in.readInt();
			filterMode = in.readInt();
			shadingFlags = in.readInt();
			textureId = in.readInt();
			textureAnimationId = in.readInt();
			coordID = in.readInt();
			alpha = in.readFloat();
			if (ModelUtils.isEmissiveLayerSupported(version)) {
				emissiveGain = in.readFloat();
			}
			if (ModelUtils.isFresnelColorLayerSupported(version)) {
				fresnelColor = MdxUtils.loadFloatArray(in, 3);
				fresnelOpacity = in.readFloat();
				fresnelTeamColor = in.readFloat();
			}
			for (int i = 0; i < 6; i++) {
				if (MdxUtils.checkOptionalId(in, MaterialAlpha.key)) {
					materialAlpha = new MaterialAlpha();
					materialAlpha.load(in);
				} else if (MdxUtils.checkOptionalId(in, MaterialTextureId.key)) {
					materialTextureId = new MaterialTextureId();
					materialTextureId.load(in);
				} else if (MdxUtils.checkOptionalId(in, MaterialEmissiveGain.key)) {
					materialEmissions = new MaterialEmissiveGain();
					materialEmissions.load(in);
				} else if (MdxUtils.checkOptionalId(in, MaterialFresnelColor.key)) {
					materialFresnelColor = new MaterialFresnelColor();
					materialFresnelColor.load(in);
				} else if (MdxUtils.checkOptionalId(in, MaterialFresnelOpacity.key)) {
					materialFresnelOpacity = new MaterialFresnelOpacity();
					materialFresnelOpacity.load(in);
				} else if (MdxUtils.checkOptionalId(in, MaterialFresnelTeamColor.key)) {
					materialFresnelTeamColor = new MaterialFresnelTeamColor();
					materialFresnelTeamColor.load(in);
				}
			}
		}

		public void save(final BlizzardDataOutputStream out, final int version) throws IOException {
			out.writeInt(getSize(version));// InclusiveSize
			out.writeInt(filterMode);
			out.writeInt(shadingFlags);
			out.writeInt(textureId);
			out.writeInt(textureAnimationId);
			out.writeInt(coordID);
			out.writeFloat(alpha);
			if (ModelUtils.isEmissiveLayerSupported(version)) {
				out.writeFloat(emissiveGain);
			}
			if (ModelUtils.isFresnelColorLayerSupported(version)) {
				if (fresnelColor != null) {
					MdxUtils.saveFloatArray(out, fresnelColor);
				} else {
					out.writeFloat(1f);
					out.writeFloat(1f);
					out.writeFloat(1f);
				}
				out.writeFloat(fresnelOpacity);
				out.writeFloat(fresnelTeamColor);
			}
			if (materialAlpha != null) {
				materialAlpha.save(out);
			}
			if (materialTextureId != null) {
				materialTextureId.save(out);
			}
			if (materialEmissions != null) {
				materialEmissions.save(out);
			}
			if (materialFresnelColor != null) {
				materialFresnelColor.save(out);
			}
			if (materialFresnelOpacity != null) {
				materialFresnelOpacity.save(out);
			}
			if (materialFresnelTeamColor != null) {
				materialFresnelTeamColor.save(out);
			}

		}

		public int getSize(final int version) {
			int a = 0;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			a += 4;
			if (ModelUtils.isEmissiveLayerSupported(version)) {
				a += 4;
			}
			if (ModelUtils.isFresnelColorLayerSupported(version)) {
				a += 20;
			}
			if (materialAlpha != null) {
				a += materialAlpha.getSize();
			}
			if (materialTextureId != null) {
				a += materialTextureId.getSize();
			}
			if (materialEmissions != null) {
				a += materialEmissions.getSize();
			}
			if (materialFresnelColor != null) {
				a += materialFresnelColor.getSize();
			}
			if (materialFresnelOpacity != null) {
				a += materialFresnelOpacity.getSize();
			}
			if (materialFresnelTeamColor != null) {
				a += materialFresnelTeamColor.getSize();
			}

			return a;
		}

		public Layer() {

		}

		public Layer(final com.hiveworkshop.wc3.mdl.Layer layer) {
			filterMode = com.hiveworkshop.wc3.mdl.Layer.FilterMode.nameToId(layer.getFilterModeString());
			for (final String flag : layer.getFlags()) {
				switch (flag) {
				case "Unshaded":
					shadingFlags |= 0x1;
					break;
				case "SphereEnvironmentMap":
				case "SphereEnvMap":
					shadingFlags |= 0x2;
					break;
				case "TwoSided":
					shadingFlags |= 0x10;
					break;
				case "Unfogged":
					shadingFlags |= 0x20;
					break;
				case "NoDepthTest":
					shadingFlags |= 0x40;
					break;
				case "NoDepthSet":
					shadingFlags |= 0x80;
					break;
				case "Unlit":
					shadingFlags |= 0x100;
					break;
				}
			}
			textureAnimationId = layer.getTVertexAnimId();
			coordID = layer.getCoordId();
			boolean alphaFound = false;
			boolean emissiveFound = false;
			boolean fresnelTeamColorFound = false;
			boolean fresnelOpacityFound = false;
			boolean fresnelColorFound = false;
			for (final AnimFlag af : layer.getAnims()) {
				if (af.getName().equals("Alpha")) {
					materialAlpha = new MaterialAlpha();
					materialAlpha.globalSequenceId = af.getGlobalSeqId();
					materialAlpha.interpolationType = af.getInterpType();
					materialAlpha.scalingTrack = new MaterialAlpha.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final MaterialAlpha.ScalingTrack mdxEntry = materialAlpha.new ScalingTrack();
						materialAlpha.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.alpha = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
					alphaFound = true;
				} else if (af.getName().startsWith("Emissive")) {
					materialEmissions = new MaterialEmissiveGain();
					materialEmissions.globalSequenceId = af.getGlobalSeqId();
					materialEmissions.interpolationType = af.getInterpType();
					materialEmissions.scalingTrack = new MaterialEmissiveGain.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final MaterialEmissiveGain.ScalingTrack mdxEntry = materialEmissions.new ScalingTrack();
						materialEmissions.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.emission = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
					emissiveFound = true;
				} else if (af.getName().equals("TextureID")) {
					materialTextureId = new MaterialTextureId();
					materialTextureId.globalSequenceId = af.getGlobalSeqId();
					materialTextureId.interpolationType = af.getInterpType();
					materialTextureId.scalingTrack = new MaterialTextureId.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final MaterialTextureId.ScalingTrack mdxEntry = materialTextureId.new ScalingTrack();
						materialTextureId.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.textureId = ((Number) mdlEntry.value).intValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).intValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).intValue();
						}
					}
				} else if (af.getName().equals("FresnelColor") && (af.size() > 0)) {
					materialFresnelColor = new MaterialFresnelColor();
					materialFresnelColor.globalSequenceId = af.getGlobalSeqId();
					materialFresnelColor.interpolationType = af.getInterpType();
					materialFresnelColor.scalingTrack = new MaterialFresnelColor.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final MaterialFresnelColor.ScalingTrack mdxEntry = materialFresnelColor.new ScalingTrack();
						materialFresnelColor.scalingTrack[i] = mdxEntry;
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
					fresnelColorFound = true;
				} else if (af.getName().equals("FresnelOpacity")) {
					materialFresnelOpacity = new MaterialFresnelOpacity();
					materialFresnelOpacity.globalSequenceId = af.getGlobalSeqId();
					materialFresnelOpacity.interpolationType = af.getInterpType();
					materialFresnelOpacity.scalingTrack = new MaterialFresnelOpacity.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final MaterialFresnelOpacity.ScalingTrack mdxEntry = materialFresnelOpacity.new ScalingTrack();
						materialFresnelOpacity.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.fresnelOpacity = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
					fresnelOpacityFound = true;
				} else if (af.getName().equals("FresnelTeamColor")) {
					materialFresnelTeamColor = new MaterialFresnelTeamColor();
					materialFresnelTeamColor.globalSequenceId = af.getGlobalSeqId();
					materialFresnelTeamColor.interpolationType = af.getInterpType();
					materialFresnelTeamColor.scalingTrack = new MaterialFresnelTeamColor.ScalingTrack[af.size()];
					final boolean hasTans = af.tans();
					for (int i = 0; i < af.size(); i++) {
						final MaterialFresnelTeamColor.ScalingTrack mdxEntry = materialFresnelTeamColor.new ScalingTrack();
						materialFresnelTeamColor.scalingTrack[i] = mdxEntry;
						final AnimFlag.Entry mdlEntry = af.getEntry(i);
						mdxEntry.fresnelTeamColor = ((Number) mdlEntry.value).floatValue();
						mdxEntry.time = mdlEntry.time;
						if (hasTans) {
							mdxEntry.inTan = ((Number) mdlEntry.inTan).floatValue();
							mdxEntry.outTan = ((Number) mdlEntry.outTan).floatValue();
						}
					}
					fresnelTeamColorFound = true;
				} else {
					if (Node.LOG_DISCARDED_FLAGS) {
						System.err.println("discarded flag " + af.getName());
					}
				}
			}
			if (alphaFound || (Math.abs(layer.getStaticAlpha() - (-1)) <= 0.001)) {
				alpha = 1.0f;
			} else {
				alpha = (float) layer.getStaticAlpha();
			}
			final double mdlEmissive = layer.getEmissive();
			if (!Double.isNaN(mdlEmissive) && !emissiveFound) {
				emissiveGain = (float) mdlEmissive;
			}
			if ((layer.getFresnelColor() != null)) {
				if (!fresnelColorFound) {
					fresnelColor = layer.getFresnelColor().toFloatArray();
					final float blue = fresnelColor[0];
					fresnelColor[0] = fresnelColor[2];
					fresnelColor[2] = blue;
					// TODO: COPIED FROM ELSEWHERE, HOPING IT MATCHES REFORGED: this chunk is RGB,
					// mdl is BGR
				} else {
					// encode blank data, but keep it valid, for MDX1000
					fresnelColor = new float[] { 1.0f, 1.0f, 1.0f };
				}
			}
			fresnelOpacity = fresnelOpacityFound ? 0 : (float) layer.getFresnelOpacity();
			fresnelTeamColor = fresnelTeamColorFound ? 0 : (float) layer.getFresnelTeamColor();
			textureId = layer.getTextureId() == -1 ? 0 : layer.getTextureId();
		}
	}
}
