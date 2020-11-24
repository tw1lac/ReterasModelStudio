package com.hiveworkshop.wc3.mdl;

import com.hiveworkshop.wc3.gui.animedit.BasicTimeBoundProvider;
import com.hiveworkshop.wc3.gui.modelviewer.AnimatedRenderEnvironment;
import com.hiveworkshop.wc3.mdl.v2.timelines.InterpolationType;
import com.hiveworkshop.wc3.mdx.*;
import com.hiveworkshop.wc3.util.MathUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class AnimFlag {
	public static String getInterpType(final int id) {
		switch (id) {
		case 0:
			return "DontInterp";
		case 1:
			return "Linear";
		case 2:
			return "Hermite";
		case 3:
			return "Bezier";
		default:
			return "DontInterp";
		}
	}

	public int getInterpType() {
		for (final String tag : tags) {
			switch (tag) {
			case "DontInterp":
				return 0;
			case "Linear":
				return 1;
			case "Hermite":
				return 2;
			case "Bezier":
				return 3;
			default:
				break;
			}
		}
		return 0;
	}

	public InterpolationType getInterpTypeAsEnum() {
		switch (getInterpType()) {
		case 0:
			return InterpolationType.DONT_INTERP;
		case 1:
			return InterpolationType.LINEAR;
		case 2:
			return InterpolationType.HERMITE;
		case 3:
			return InterpolationType.BEZIER;
		}
		throw new IllegalStateException();
	}
	// 0: none
	// 1: linear
	// 2: hermite
	// 3: bezier

	// Types of AnimFlags:
	// 0 Alpha
	public static final int ALPHA = 0;
	// 1 Scaling
	public static final int SCALING = 1;
	// 2 Rotation
	public static final int ROTATION = 2;
	// 3 Translation
	public static final int TRANSLATION = 3;
	// 4 Color
	public static final int COLOR = 4;
	// 5 TextureID
	public static final int TEXTUREID = 5;

	/**
	 * Use for titles like "Intensity", "AmbIntensity", and other extraneous things
	 * not included in the options above.
	 */
	public static final int OTHER_TYPE = 0;

	ArrayList<String> tags = new ArrayList<>();
	String title;
	Integer globalSeq;
	int globalSeqId = -1;
	boolean hasGlobalSeq = false;
	ArrayList<Integer> times = new ArrayList<>();
	ArrayList<Object> values = new ArrayList<>();
	ArrayList<Object> inTans = new ArrayList<>();
	ArrayList<Object> outTans = new ArrayList<>();
	int typeid = 0;

	public boolean equals(final AnimFlag o) {
		boolean does;
		if (o == null) {
			return false;
		}
		does = title.equals(o.title);
		does = hasGlobalSeq == o.hasGlobalSeq;
		does = values.equals(o.values) && (Objects.equals(globalSeq, o.globalSeq))
				&& (Objects.equals(tags, o.tags))
				&& (Objects.equals(inTans, o.inTans))
				&& (Objects.equals(outTans, o.outTans)) && (typeid == o.typeid);
		return does;
	}

	public void setGlobSeq(final Integer integer) {
		globalSeq = integer;
		hasGlobalSeq = integer != null;
	}

	public Integer getGlobalSeq() {
		return globalSeq;
	}

	public void setGlobalSeq(final Integer globalSeq) {
		this.globalSeq = globalSeq;
	}

	// begin constructors from ogre-lord's API:
	private static Double box(final float f) {
		return (double) f;
	}

	public AnimFlag(final MaterialTextureId source) {
		title = "TextureID";
		checkForGlobasSequence(source.interpolationType, source.globalSequenceId);
		final boolean tans = source.interpolationType > 1;
		for (final MaterialTextureId.ScalingTrack track : source.scalingTrack) {
			if (tans) {
				addEntry(track.time, track.textureId, track.inTan, track.outTan);
			} else {
				addEntry(track.time, track.textureId);
			}
		}
	}

	public AnimFlag(final MaterialAlpha source) {
		title = "Alpha";
		checkForGlobasSequence(source.interpolationType, source.globalSequenceId);
		final boolean tans = source.interpolationType > 1;
		for (final MaterialAlpha.ScalingTrack track : source.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.alpha), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.alpha));
			}
		}
	}

	public AnimFlag(final MaterialEmissiveGain source) {
		title = "Emissive";
		checkForGlobasSequence(source.interpolationType, source.globalSequenceId);
		final boolean tans = source.interpolationType > 1;
		for (final MaterialEmissiveGain.ScalingTrack track : source.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.emission), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.emission));
			}
		}
	}

	public AnimFlag(final MaterialFresnelColor cornColor) {
		title = "FresnelColor";
		checkForGlobasSequence(cornColor.interpolationType, cornColor.globalSequenceId);
		final boolean tans = cornColor.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???
		for (final MaterialFresnelColor.ScalingTrack track : cornColor.scalingTrack) {
			addTrackEntry(tans, track.time, track.color, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final MaterialFresnelOpacity source) {
		title = "FresnelOpacity";
		checkForGlobasSequence(source.interpolationType, source.globalSequenceId);
		final boolean tans = source.interpolationType > 1;
		for (final MaterialFresnelOpacity.ScalingTrack track : source.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.fresnelOpacity), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.fresnelOpacity));
			}
		}
	}

	public AnimFlag(final MaterialFresnelTeamColor source) {
		title = "FresnelTeamColor";
		checkForGlobasSequence(source.interpolationType, source.globalSequenceId);
		final boolean tans = source.interpolationType > 1;
		for (final MaterialFresnelTeamColor.ScalingTrack track : source.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.fresnelTeamColor), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.fresnelTeamColor));
			}
		}
	}

	public AnimFlag(final TextureRotation textureData) {
		title = "Rotation";
		checkForGlobasSequence(textureData.interpolationType, textureData.globalSequenceId);
		final boolean tans = textureData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final TextureRotation.TranslationTrack track : textureData.translationTrack) {
			if (tans) {
				addEntry(track.time, new QuaternionRotation(track.rotation), new QuaternionRotation(track.inTan),
						new QuaternionRotation(track.outTan));
			} else {
				addEntry(track.time, new QuaternionRotation(track.rotation));
			}
		}
	}

	public AnimFlag(final TextureScaling textureData) {
		title = "Scaling";
		checkForGlobasSequence(textureData.interpolationType, textureData.globalSequenceId);
		final boolean tans = textureData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final TextureScaling.TranslationTrack track : textureData.translationTrack) {
			addTrackEntry(tans, track.time, track.scaling, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final TextureTranslation textureData) {
		title = "Translation";
		checkForGlobasSequence(textureData.interpolationType, textureData.globalSequenceId);
		final boolean tans = textureData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final TextureTranslation.TranslationTrack track : textureData.translationTrack) {
			addTrackEntry(tans, track.time, track.translation, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final GeosetAlpha geosetAlpha) {
		title = "Alpha";
		checkForGlobasSequence(geosetAlpha.interpolationType, geosetAlpha.globalSequenceId);
		final boolean tans = geosetAlpha.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final GeosetAlpha.ScalingTrack track : geosetAlpha.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.alpha), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.alpha));
			}
		}
	}

	public AnimFlag(final GeosetColor geosetColor) {
		title = "Color";
		checkForGlobasSequence(geosetColor.interpolationType, geosetColor.globalSequenceId);
		final boolean tans = geosetColor.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final GeosetColor.ScalingTrack track : geosetColor.scalingTrack) {
			addTrackEntry(tans, track.time, track.color, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final GeosetTranslation geosetTranslation) {
		title = "Translation";
		checkForGlobasSequence(geosetTranslation.interpolationType, geosetTranslation.globalSequenceId);
		final boolean tans = geosetTranslation.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final GeosetTranslation.TranslationTrack track : geosetTranslation.translationTrack) {
			addTrackEntry(tans, track.time, track.translation, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final GeosetRotation geosetData) {
		title = "Rotation";
		checkForGlobasSequence(geosetData.interpolationType, geosetData.globalSequenceId);
		final boolean tans = geosetData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final GeosetRotation.RotationTrack track : geosetData.rotationTrack) {
			if (tans) {
				addEntry(track.time, new QuaternionRotation(track.rotation), new QuaternionRotation(track.inTan),
						new QuaternionRotation(track.outTan));
			} else {
				addEntry(track.time, new QuaternionRotation(track.rotation));
			}
		}
	}

	public AnimFlag(final GeosetScaling geosetData) {
		title = "Scaling";
		checkForGlobasSequence(geosetData.interpolationType, geosetData.globalSequenceId);
		final boolean tans = geosetData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final GeosetScaling.ScalingTrack track : geosetData.scalingTrack) {
			addTrackEntry(tans, track.time, track.scaling, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final LightVisibility trackData) {
		title = "Visibility";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightVisibility.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.visibility), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.visibility));
			}
		}
	}

	public AnimFlag(final LightColor trackData) {
		title = "Color";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightColor.ScalingTrack track : trackData.scalingTrack) {
			addTrackEntry(tans, track.time, track.color, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final LightIntensity trackData) {
		title = "Intensity";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightIntensity.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.intensity), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.intensity));
			}
		}
	}

	public AnimFlag(final LightAmbientIntensity trackData) {
		title = "AmbIntensity";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightAmbientIntensity.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.ambientIntensity), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.ambientIntensity));
			}
		}
	}

	public AnimFlag(final LightAttenuationStart trackData) {
		title = "AttenuationStart";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightAttenuationStart.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.attenuationStart), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.attenuationStart));
			}
		}
	}

	public AnimFlag(final LightAttenuationEnd trackData) {
		title = "AttenuationEnd";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightAttenuationEnd.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.attenuationEnd), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.attenuationEnd));
			}
		}
	}

	public AnimFlag(final LightAmbientColor trackData) {
		title = "AmbColor";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final LightAmbientColor.ScalingTrack track : trackData.scalingTrack) {
			addTrackEntry(tans, track.time, track.ambientColor, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final AttachmentVisibility trackData) {
		title = "Visibility";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final AttachmentVisibility.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.visibility), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.visibility));
			}
		}
	}

	public AnimFlag(final ParticleEmitterEmissionRate trackData) {
		title = "EmissionRate";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final ParticleEmitterEmissionRate.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.emissionRate), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.emissionRate));
			}
		}
	}

	public AnimFlag(final ParticleEmitterGravity trackData) {
		title = "Gravity";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final ParticleEmitterGravity.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.gravity), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.gravity));
			}
		}
	}

	public AnimFlag(final ParticleEmitterLatitude trackData) {
		title = "Latitude";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final ParticleEmitterLatitude.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.latitude), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.latitude));
			}
		}
	}

	public AnimFlag(final ParticleEmitterLifeSpan trackData) {
		title = "LifeSpan";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final ParticleEmitterLifeSpan.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.lifeSpan), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.lifeSpan));
			}
		}
	}

	public AnimFlag(final ParticleEmitterLongitude trackData) {
		title = "Longitude";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final ParticleEmitterLongitude.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.longitude), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.longitude));
			}
		}
	}

	public AnimFlag(final ParticleEmitterSpeed trackData) {
		title = "InitVelocity";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final ParticleEmitterSpeed.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.speed), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.speed));
			}
		}
	}

	public AnimFlag(final ParticleEmitterVisibility trackData) {
		title = "Visibility";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitterVisibility.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.visibility), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.visibility));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2Visibility trackData) {
		title = "Visibility";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Visibility.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.visibility), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.visibility));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2Variation trackData) {
		title = "Variation";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Variation.VariationTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.variation), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.variation));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2Gravity trackData) {
		title = "Gravity";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Gravity.VariationTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.gravity), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.gravity));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2EmissionRate trackData) {
		title = "EmissionRate";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2EmissionRate.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.emissionRate), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.emissionRate));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2Latitude trackData) {
		title = "Latitude";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Latitude.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.speed), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.speed));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2Length trackData) {
		title = "Length";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Length.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.length), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.length));
			}
		}
	}

	public AnimFlag(final ParticleEmitter2Speed trackData) {
		title = "Speed";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Speed.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.speed), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.speed));
			}
		}
	}

	private void checkForGlobasSequence(int interpolationType, int globalSequenceId) {
		generateTypeId();
		addTag(AnimFlag.getInterpType(interpolationType));
		if (globalSequenceId >= 0) {
			setGlobalSeqId(globalSequenceId);
			setHasGlobalSeq(true);
		}
	}

	public AnimFlag(final ParticleEmitter2Width trackData) {
		title = "Width";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final ParticleEmitter2Width.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.width), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.width));
			}
		}
	}

	public AnimFlag(final CornEmissionRate trackData) {
		title = "EmissionRate";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final CornEmissionRate.EmissionRateTrack track : trackData.emissionRateTrack) {
			if (tans) {
				addEntry(track.time, box(track.emissionRate), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.emissionRate));
			}
		}
	}

	public AnimFlag(final CornAlpha trackData) {
		title = "Alpha";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final CornAlpha.AlphaTrack track : trackData.alphaTrack) {
			if (tans) {
				addEntry(track.time, box(track.alpha), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.alpha));
			}
		}
	}

	public AnimFlag(final CornVisibility trackData) {
		title = "Visibility";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final CornVisibility.VisibilityTrack track : trackData.visibilityTrack) {
			if (tans) {
				addEntry(track.time, box(track.visibility), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.visibility));
			}
		}
	}

	public AnimFlag(final CornLifeSpan trackData) {
		title = "LifeSpan";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final CornLifeSpan.LifeSpanTrack track : trackData.lifeSpanTrack) {
			if (tans) {
				addEntry(track.time, box(track.lifeSpan), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.lifeSpan));
			}
		}
	}

	public AnimFlag(final CornSpeed trackData) {
		title = "Speed";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final CornSpeed.SpeedTrack track : trackData.speedTrack) {
			if (tans) {
				addEntry(track.time, box(track.speed), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.speed));
			}
		}
	}

	public AnimFlag(final CornColor cornColor) {
		title = "Color";
		checkForGlobasSequence(cornColor.interpolationType, cornColor.globalSequenceId);
		final boolean tans = cornColor.interpolationType > 1;
		// NOTE: autoreplaced from a > 0 check, Linear shouldn't have 'tans'???
		for (final CornColor.ScalingTrack track : cornColor.scalingTrack) {
			addTrackEntry(tans, track.time, track.color, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final RibbonEmitterVisibility trackData) {
		title = "Visibility";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final RibbonEmitterVisibility.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.visibility), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.visibility));
			}
		}
	}

	public AnimFlag(final RibbonEmitterHeightAbove trackData) {
		title = "HeightAbove";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final RibbonEmitterHeightAbove.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.heightAbove), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.heightAbove));
			}
		}
	}

	public AnimFlag(final RibbonEmitterHeightBelow trackData) {
		title = "HeightBelow";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final RibbonEmitterHeightBelow.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.heightBelow), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.heightBelow));
			}
		}
	}

	public AnimFlag(final RibbonEmitterAlpha trackData) {
		title = "Alpha";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final RibbonEmitterAlpha.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, box(track.alpha), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.alpha));
			}
		}
	}

	public AnimFlag(final RibbonEmitterColor trackData) {
		title = "Color";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final RibbonEmitterColor.ScalingTrack track : trackData.scalingTrack) {
			addTrackEntry(tans, track.time, track.color, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final RibbonEmitterTextureSlot trackData) {
		title = "TextureSlot";
		checkForGlobasSequence(trackData.interpolationType, trackData.globalSequenceId);
		final boolean tans = trackData.interpolationType > 1;
		for (final RibbonEmitterTextureSlot.ScalingTrack track : trackData.scalingTrack) {
			if (tans) {
				addEntry(track.time, track.textureSlot, track.inTan, track.outTan);
			} else {
				addEntry(track.time, track.textureSlot);
			}
		}
	}

	public AnimFlag(final CameraPositionTranslation translation) {
		title = "Translation";
		checkForGlobasSequence(translation.interpolationType, translation.globalSequenceId);
		final boolean tans = translation.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final CameraPositionTranslation.TranslationTrack track : translation.translationTrack) {
			addTrackEntry(tans, track.time, track.translation, track.inTan, track.outTan);
		}
	}

	public AnimFlag(final CameraTargetTranslation translation) {
		title = "Translation";
		checkForGlobasSequence(translation.interpolationType, translation.globalSequenceId);
		final boolean tans = translation.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final CameraTargetTranslation.TranslationTrack track : translation.translationTrack) {
			addTrackEntry(tans, track.time, track.translation, track.inTan, track.outTan);
		}
	}

	private void addTrackEntry(boolean tans, int time, float[] translation2, float[] inTan, float[] outTan) {
		if (tans) {
			addEntry(time, new Vertex(translation2), new Vertex(inTan), new Vertex(outTan));
		} else {
			addEntry(time, new Vertex(translation2));
		}
	}

	public AnimFlag(final CameraRotation translation) {
		title = "Rotation";
		checkForGlobasSequence(translation.interpolationType, translation.globalSequenceId);
		final boolean tans = translation.interpolationType > 1;
		// NOTE: auto replaced from a > 0 check, Linear shouldn't have 'tans'???

		for (final CameraRotation.TranslationTrack track : translation.translationTrack) {
			if (tans) {
				addEntry(track.time, box(track.rotation), box(track.inTan), box(track.outTan));
			} else {
				addEntry(track.time, box(track.rotation));
			}
		}
	}

	// end special constructors
	public AnimFlag(final String title, final ArrayList<Integer> times, final ArrayList values) {
		this.title = title;
		this.times = times;
		this.values = values;
	}

	public AnimFlag(final String title) {
		this.title = title;
		tags.add("DontInterp");
	}

	public static AnimFlag createEmpty2018(final String title, final InterpolationType interpolationType,
			final Integer globalSeq) {
		final AnimFlag flag = new AnimFlag();
		flag.title = title;
		switch (interpolationType) {
			case BEZIER:
				flag.tags.add("Bezier");
				break;
			case HERMITE:
				flag.tags.add("Hermite");
				break;
			case LINEAR:
				flag.tags.add("Linear");
				break;
			default:
			case DONT_INTERP:
				flag.tags.add("DontInterp");
				break;
		}
		flag.generateTypeId();
		flag.setGlobSeq(globalSeq);
		return flag;
	}

	public void setInterpType(final InterpolationType interpolationType) {
		System.err.println("Unsafe call to setInterpType, please rewrite code in AnimFlag class");
		tags.clear();// we're pretty sure this is just interp type now
		switch (interpolationType) {
			case BEZIER:
				tags.add("Bezier");
				break;
			case HERMITE:
				tags.add("Hermite");
				break;
			case LINEAR:
				tags.add("Linear");
				break;
			default:
			case DONT_INTERP:
				tags.add("DontInterp");
				break;
		}
	}

	public int size() {
		return times.size();
	}

	public int length() {
		return times.size();
	}

	public AnimFlag(final AnimFlag af) {
		title = af.title;
		tags = af.tags;
		globalSeq = af.globalSeq;
		globalSeqId = af.globalSeqId;
		hasGlobalSeq = af.hasGlobalSeq;
		typeid = af.typeid;
		times = new ArrayList<>(af.times);
		values = deepCopy(af.values);
		inTans = deepCopy(af.inTans);
		outTans = deepCopy(af.outTans);
	}

	public static AnimFlag buildEmptyFrom(final AnimFlag af) {
		final AnimFlag na = new AnimFlag(af.title);
		na.tags = af.tags;
		na.globalSeq = af.globalSeq;
		na.globalSeqId = af.globalSeqId;
		na.hasGlobalSeq = af.hasGlobalSeq;
		na.typeid = af.typeid;
		return na;
	}

	public void addTag(final String tag) {
		tags.add(tag);
	}

	public void generateTypeId() {
		typeid = 0;
		if (title.equals("Scaling")) {
			typeid = 1;
		} else if (title.equals("Rotation")) {
			typeid = 2;
		} else if (title.equals("Translation")) {
			typeid = 3;
		} else if (title.equals("TextureID"))
			// aflg.title.equals("Visibility") || -- 100.088% visible in  UndeadCampaign3D OutTans! Go look!
		{
			typeid = 5;
		} else if (title.contains("Color"))// AmbColor
		{
			typeid = 4;
		}
	}

	public int getGlobalSeqId() {
		return globalSeqId;
	}

	public void setGlobalSeqId(final int globalSeqId) {
		this.globalSeqId = globalSeqId;
	}

	public boolean hasGlobalSeq() {
		return hasGlobalSeq;
	}

	public void setHasGlobalSeq(final boolean hasGlobalSeq) {
		this.hasGlobalSeq = hasGlobalSeq;
	}

	public void addEntry(final Integer time, final Object value) {
		times.add(time);
		values.add(value);
	}

	public void addEntry(final Integer time, final Object value, final Object inTan, final Object outTan) {
		times.add(time);
		values.add(value);
		inTans.add(inTan);
		outTans.add(outTan);
	}

	public void setEntry(final Integer time, final Object value) {
		for (int index = 0; index < times.size(); index++) {
			if (times.get(index).equals(time)) {
				values.set(index, value);
				if (tans()) {
					inTans.set(index, value);
					outTans.set(index, value);
				}
			}
		}
	}

	/**
	 * This class is a small shell of an example for how my "AnimFlag" class
	 * should've been implemented. It's currently only used for the
	 * {@link AnimFlag#getEntry(int)} function.
	 *
	 * @author Eric
	 *
	 */
	public static class Entry {
		public Integer time;
		public Object value, inTan, outTan;

		public Entry(final Integer time, final Object value, final Object inTan, final Object outTan) {
			super();
			this.time = time;
			this.value = value;
			this.inTan = inTan;
			this.outTan = outTan;
		}

		public Entry(final Integer time, final Object value) {
			super();
			this.time = time;
			this.value = value;
		}

		public void set(final Entry other) {
			time = other.time;
			value = other.value;
			inTan = other.inTan;
			outTan = other.outTan;
		}
	}

	public Entry getEntry(final int index) {
		if (tans()) {
			return new Entry(times.get(index), values.get(index), inTans.get(index), outTans.get(index));
		} else {
			return new Entry(times.get(index), values.get(index));
		}
	}

	public static Object cloneValue(final Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return value;
		} else if (value instanceof Double) {
			return value;
		} else if (value instanceof Vertex) {
			final Vertex vertex = (Vertex) value;
			final Vertex clonedVertex = new Vertex(vertex);
			return clonedVertex;
		} else if (value instanceof QuaternionRotation) {
			final QuaternionRotation vertex = (QuaternionRotation) value;
			final QuaternionRotation clonedVertex = new QuaternionRotation(vertex);
			return clonedVertex;
		} else {
			throw new IllegalStateException(value.getClass().getName());
		}
	}

	public static Object cloneValueAsEmpty(final Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return 0;
		} else if (value instanceof Double) {
			return 0.0;
		} else if (value instanceof Vertex) {
			final Vertex vertex = (Vertex) value;
			return new Vertex(0, 0, 0);
		} else if (value instanceof QuaternionRotation) {
			final QuaternionRotation vertex = (QuaternionRotation) value;
			return new QuaternionRotation(0, 0, 0, 1);
		} else {
			throw new IllegalStateException(value.getClass().getName());
		}
	}

	public Object valueAt(final Integer time) {
		for (int i = 0; i < times.size(); i++) {
			if (times.get(i).equals(time)) {
				return values.get(i);
			}
		}
		return null;
	}

	public Object inTanAt(final Integer time) {
		for (int i = 0; i < times.size(); i++) {
			if (times.get(i).equals(time)) {
				return inTans.get(i);
			}
		}
		return null;
	}

	public Object outTanAt(final Integer time) {
		for (int i = 0; i < times.size(); i++) {
			if (times.get(i).equals(time)) {
				return outTans.get(i);
			}
		}
		return null;
	}

	public void setValuesTo(final AnimFlag af) {
		title = af.title;
		tags = af.tags;
		globalSeq = af.globalSeq;
		globalSeqId = af.globalSeqId;
		hasGlobalSeq = af.hasGlobalSeq;
		typeid = af.typeid;
		times = new ArrayList<>(af.times);
		values = deepCopy(af.values);
		inTans = deepCopy(af.inTans);
		outTans = deepCopy(af.outTans);
	}

	private <T> ArrayList<T> deepCopy(final ArrayList<T> source) {

		final ArrayList<T> copy = new ArrayList<>();
		for (final T item : source) {
			T toAdd = item;
			if (item instanceof Vertex) {
				final Vertex v = (Vertex) item;
				toAdd = (T) v;
			} else if (item instanceof QuaternionRotation) {
				final QuaternionRotation r = (QuaternionRotation) item;
				toAdd = (T) r;
			}
			copy.add(toAdd);
		}
		return copy;
	}

	public String getName() {
		return title;
	}

	public int getTypeId() {
		return typeid;
	}

	private AnimFlag() {

	}

	public static AnimFlag find(final Map<String, AnimFlag> flags, final String name, final Integer globalSeq) {
		// TODO make flags be a map and remove this method, this is 2018
		// not 2012 anymore, and I learned basic software dev
		AnimFlag animFlag = flags.get(name);
		if (((globalSeq == null) && (animFlag.globalSeq == null)) || ((globalSeq != null) && globalSeq.equals(animFlag.globalSeq))){
			return animFlag;
		}
//		for (final AnimFlag flag : flags) {
//			if (flag.getName().equals(name)
//					&& (((globalSeq == null) && (flag.globalSeq == null))
//						|| ((globalSeq != null) && globalSeq.equals(flag.globalSeq)))) {
//				return flag;
//			}
//		}
		return null;
	}

	public static AnimFlag find(final List<AnimFlag> flags, final String name) {
		// TODO make flags be a map and remove this method, this is 2018
		// not 2012 anymore, and I learned basic software dev
		for (final AnimFlag flag : flags) {
			if (flag.getName().equals(name)) {
				return flag;
			}
		}
		return null;
	}

	public static AnimFlag parseText(final String[] line) {

		final AnimFlag animFlag = new AnimFlag();
		animFlag.title = MDLReader.readIntTitle(line[0]);
		// Types of AnimFlags:
		// 0 Alpha
		// 1 Scaling
		// 2 Rotation
		// 3 Translation
		int typeid = 0;
		if (animFlag.title.equals("Scaling")) {
			typeid = 1;
		} else if (animFlag.title.equals("Rotation")) {
			typeid = 2;
		} else if (animFlag.title.equals("Translation")) {
			typeid = 3;
		} else if (!animFlag.title.equals("Alpha")) {
			JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),
					"Unable to parse \"" + animFlag.title + "\": Missing or unrecognized open statement.");
		}
		animFlag.typeid = typeid;
		for (int i = 1; i < line.length; i++) {
			if (line[i].contains("Tan")) {
				ArrayList target = null;
				if (line[i].contains("In"))// InTan
				{
					target = animFlag.inTans;
				} else// OutTan
				{
					target = animFlag.outTans;
				}
				switch (typeid) {
				case 0: // Alpha
					// A single double is used to store alpha data
					target.add(MDLReader.readDouble(line[i]));
					break;
				case 1: // Scaling
					// A vertex is used to store scaling data
					target.add(Vertex.parseText(line[i]));
					break;
				case 2: // Rotation
					// A quaternion set of four values is used to store rotation
					// data
					target.add(QuaternionRotation.parseText(line[i]));
					break;
				case 3: // Translation
					// A vertex is used to store translation data
					target.add(Vertex.parseText(line[i]));
					break;
				}
			} else if (line[i].contains(":")) {
				switch (typeid) {
				case 0: // Alpha
					// A single double is used to store alpha data
					animFlag.times.add(MDLReader.readBeforeColon(line[i]));
					animFlag.values.add(MDLReader.readDouble(line[i]));
					break;
				case 1: // Scaling
					// A vertex is used to store scaling data
					animFlag.times.add(MDLReader.readBeforeColon(line[i]));
					animFlag.values.add(Vertex.parseText(line[i]));
					break;
				case 2: // Rotation
					// A quaternion set of four values is used to store rotation
					// data
					animFlag.times.add(MDLReader.readBeforeColon(line[i]));
					animFlag.values.add(QuaternionRotation.parseText(line[i]));
					break;
				case 3: // Translation
					// A vertex is used to store translation data
					animFlag.times.add(MDLReader.readBeforeColon(line[i]));
					animFlag.values.add(Vertex.parseText(line[i]));
					break;
				}
			} else if (line[i].contains("GlobalSeqId")) {
				if (!animFlag.hasGlobalSeq) {
					animFlag.globalSeqId = MDLReader.readInt(line[i]);
					animFlag.hasGlobalSeq = true;
				} else {
					JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(), "Error while parsing " + animFlag.title
							+ ": More than one Global Sequence Id is present in the same " + animFlag.title + "!");
				}
			} else {
				animFlag.tags.add(MDLReader.readFlag(line[i]));
			}
		}
		return animFlag;
	}

	public static AnimFlag read(final BufferedReader mdl) {

		final AnimFlag animFlag = new AnimFlag();
		animFlag.title = MDLReader.readIntTitle(MDLReader.nextLine(mdl));
		// Types of AnimFlags:
		// 0 Alpha
		// 1 Scaling
		// 2 Rotation
		// 3 Translation
		int typeid = 0;
		if (animFlag.title.equals("Scaling")) {
			typeid = 1;
		} else if (animFlag.title.equals("Rotation")) {
			typeid = 2;
		} else if (animFlag.title.equals("Translation")) {
			typeid = 3;
		} else if (animFlag.title.equals("TextureID"))
			// animFlag.title.equals("Visibility") || -- 100.088% visible in UndeadCampaign3D OutTans! Go look!
		{
			typeid = 5;
		} else if (animFlag.title.contains("Color"))// AmbColor
		{
			typeid = 4;
		} else if (!animFlag.title.equals("Alpha")) {
			// JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(),"Unable
			// to parse \""+animFlag.title+"\": Missing or unrecognized open statement.");
			// Having BS random AnimFlags is okay now, they all use double entries
		}
		animFlag.typeid = typeid;
		String line = "";
		while (!(line = MDLReader.nextLine(mdl)).contains("\t}")) {
			if (line.contains("Tan")) {
				ArrayList target = null;
				if (line.contains("In"))// InTan
				{
					target = animFlag.inTans;
				} else// OutTan
				{
					target = animFlag.outTans;
				}
				switch (typeid) {
				case 0: // Alpha
					// A single double is used to store alpha data
					target.add(MDLReader.readDouble(line));
					break;
				case 1: // Scaling
					// A vertex is used to store scaling data
					target.add(Vertex.parseText(line));
					break;
				case 2: // Rotation
					// A quaternion set of four values is used to store rotation data
					try {
						target.add(QuaternionRotation.parseText(line));
					} catch (final Exception e) {
						// typeid = 0;//Yay! random bad model.
						target.add(MDLReader.readDouble(line));
					}
					break;
				case 3: // Translation
					// A vertex is used to store translation data
					target.add(Vertex.parseText(line));
					break;
				case 4: // Translation
					// A vertex is used to store translation data
					target.add(Vertex.parseText(line));
					break;
				case 5: // Alpha
					// A single double is used to store alpha data
					target.add(MDLReader.readInt(line));
					break;
				}
			} else if (line.contains(":")) {
				switch (typeid) {
				case 0: // Alpha
					// A single double is used to store alpha data
					animFlag.times.add(MDLReader.readBeforeColon(line));
					animFlag.values.add(MDLReader.readDouble(line));
					break;
				case 1: // Scaling
					// A vertex is used to store scaling data
					animFlag.times.add(MDLReader.readBeforeColon(line));
					animFlag.values.add(Vertex.parseText(line));
					break;
				case 2: // Rotation
					// A quaternion set of four values is used to store rotation data
					try {

						animFlag.times.add(MDLReader.readBeforeColon(line));
						animFlag.values.add(QuaternionRotation.parseText(line));
					} catch (final Exception e) {
						// JOptionPane.showMessageDialog(null,e.getStackTrace());
						// typeid = 0;
						// animFlag.times.add(new
						// Integer(MDLReader.readBeforeColon(line)));
						animFlag.values.add(MDLReader.readDouble(line));
					}
					break;
				case 3: // Translation
					// A vertex is used to store translation data
					animFlag.times.add(MDLReader.readBeforeColon(line));
					animFlag.values.add(Vertex.parseText(line));
					break;
				case 4: // Color
					// A vertex is used to store translation data
					animFlag.times.add(MDLReader.readBeforeColon(line));
					animFlag.values.add(Vertex.parseText(line));
					break;
				case 5: // Visibility
					// A vertex is used to store translation data
					animFlag.times.add(MDLReader.readBeforeColon(line));
					animFlag.values.add(MDLReader.readInt(line));
					break;
				}
			} else if (line.contains("GlobalSeqId")) {
				if (!animFlag.hasGlobalSeq) {
					animFlag.globalSeqId = MDLReader.readInt(line);
					animFlag.hasGlobalSeq = true;
				} else {
					JOptionPane.showMessageDialog(MDLReader.getDefaultContainer(), "Error while parsing " + animFlag.title
							+ ": More than one Global Sequence Id is present in the same " + animFlag.title + "!");
				}
			} else {
				animFlag.tags.add(MDLReader.readFlag(line));
			}
		}
		return animFlag;
	}

	public void updateGlobalSeqRef(final EditableModel mdlr) {
		if (hasGlobalSeq) {
			globalSeq = mdlr.getGlobalSeq(globalSeqId);
		}
	}

	public void updateGlobalSeqId(final EditableModel mdlr) {
		if (hasGlobalSeq) {
			globalSeqId = mdlr.getGlobalSeqId(globalSeq);
		}
	}

	public String flagToString(final Object o) {
		if (o.getClass() == double.class) {
			return MDLReader.doubleToString((Double) o);
		} else if (o.getClass() == Double.class) {
			return MDLReader.doubleToString((Double) o);
		} else {
			return o.toString();
		}
	}

	public void flipOver(final byte axis) {
		if (typeid == 2) {
			// Rotation
			quartFlip(axis, values);
			quartFlip(axis, inTans);
			quartFlip(axis, outTans);
		} else if (typeid == 3) {
			// Translation
			switchAxis(axis, values);
			switchAxis(axis, inTans);
			switchAxis(axis, outTans);
		}
	}

	private void quartFlip(byte axis, ArrayList<Object> objects) {
		for (int k = 0; k < objects.size(); k++) {
			final QuaternionRotation rot = (QuaternionRotation) objects.get(k);
			final Vertex euler = rot.toEuler();
			eulerSetCoordSwitch(axis, euler);
			objects.set(k, new QuaternionRotation(euler));
		}
	}

	private void switchAxis(byte axis, ArrayList<Object> objects) {
		for (Object obj : objects) {
			final Vertex trans = (Vertex) obj;
			// trans.setCoord(axis,-trans.getCoord(axis));
			axisTrensSwitch(axis, trans);
		}
	}

	private void axisTrensSwitch(byte axis, Vertex trans) {
		switch (axis) {
			// case 0:
			// trans.setCoord((byte)2,-trans.getCoord((byte)2));
			// break;
			// case 1:
			// trans.setCoord((byte)0,-trans.getCoord((byte)0));
			// break;
			// case 2:
			// trans.setCoord((byte)1,-trans.getCoord((byte)1));
			// break;
			case 0:
				trans.setCoord((byte) 0, -trans.getCoord((byte) 0));
				break;
			case 1:
				trans.setCoord((byte) 1, -trans.getCoord((byte) 1));
				break;
			case 2:
				trans.setCoord((byte) 2, -trans.getCoord((byte) 2));
				break;
		}
	}

	private void eulerSetCoordSwitch(byte axis, Vertex euler) {
		switch (axis) {
			case 0:
				euler.setCoord((byte) 0, -euler.getCoord((byte) 0));
				euler.setCoord((byte) 1, -euler.getCoord((byte) 1));
				break;
			case 1:
				euler.setCoord((byte) 0, -euler.getCoord((byte) 0));
				euler.setCoord((byte) 2, -euler.getCoord((byte) 2));
				break;
			case 2:
				euler.setCoord((byte) 1, -euler.getCoord((byte) 1));
				euler.setCoord((byte) 2, -euler.getCoord((byte) 2));
				break;
		}
	}

	public void printTo(final PrintWriter writer, final int tabHeight) {
		if (size() > 0) {
			sort();
			StringBuilder tabs = new StringBuilder();
			for (int i = 0; i < tabHeight; i++) {
				tabs.append("\t");
			}
			writer.println(tabs + title + " " + times.size() + " {");
			for (String tag : tags) {
				writer.println(tabs + "\t" + tag + ",");
			}
			if (hasGlobalSeq) {
				writer.println(tabs + "\tGlobalSeqId " + globalSeqId + ",");
			}
			boolean tans = false;
			if (inTans.size() > 0) {
				tans = true;
			}
			for (int i = 0; i < times.size(); i++) {
				writer.println(tabs + "\t" + times.get(i) + ": " + flagToString(values.get(i)) + ",");
				if (tans) {
					writer.println(tabs + "\t\tInTan " + flagToString(inTans.get(i)) + ",");
					writer.println(tabs + "\t\tOutTan " + flagToString(outTans.get(i)) + ",");
				}
			}
			// switch (typeid )
			// {
			// case 0: //Alpha
			// //A single double is used to store alpha data
			// for( int i = 0; i < times.size(); i++ )
			// {
			// writer.println(tabs+"\t"+times.get(i)+":
			// "+((Double)values.get(i)).doubleValue()+",");
			// if( tans )
			// {
			// writer.println(tabs+"\t\tInTan
			// "+((Double)inTans.get(i)).doubleValue()+",");
			// writer.println(tabs+"\t\tOutTan
			// "+((Double)outTans.get(i)).doubleValue()+",");
			// }
			// }
			// break;
			// case 1: //Scaling
			// //A vertex is used to store scaling data
			// for( int i = 0; i < times.size(); i++ )
			// {
			// writer.println(tabs+"\t"+times.get(i)+":
			// "+((Vertex)values.get(i)).toString()+",");
			// if( tans )
			// {
			// writer.println(tabs+"\t\tInTan
			// "+((Vertex)inTans.get(i)).toString()+",");
			// writer.println(tabs+"\t\tOutTan
			// "+((Double)outTans.get(i)).doubleValue()+",");
			// }
			// }
			// break;
			// case 2: //Rotation
			// //A quaternion set of four values is used to store rotation data
			// aflg.times.add(new Integer(MDLReader.readBeforeColon(line)));
			// aflg.values.add(QuaternionRotation.parseText(line));
			// break;
			// case 3: //Translation
			// //A vertex is used to store translation data
			// aflg.times.add(new Integer(MDLReader.readBeforeColon(line)));
			// aflg.values.add(Vertex.parseText(line));
			// break;
			// case 4: //Color
			// //A vertex is used to store translation data
			// aflg.times.add(new Integer(MDLReader.readBeforeColon(line)));
			// aflg.values.add(Vertex.parseText(line));
			// break;
			// case 5: //Visibility
			// //A vertex is used to store translation data
			// aflg.times.add(new Integer(MDLReader.readBeforeColon(line)));
			// aflg.values.add(new Integer(MDLReader.readInt(line)));
			// break;
			// }
			writer.println(tabs + "}");
		}
	}

	public AnimFlag getMostVisible(final AnimFlag partner) {
		if (partner != null) {
			if ((typeid == 0) && (partner.typeid == 0)) {
				final ArrayList<Integer> atimes = new ArrayList<>(times);
				final ArrayList<Integer> btimes = new ArrayList<>(partner.times);
				final ArrayList<Double> avalues = new ArrayList(values);
				final ArrayList<Double> bvalues = new ArrayList(partner.values);
				AnimFlag mostVisible = null;
				// count down from top, meaning that removing the current value causes no harm
				for (int i = atimes.size() - 1; i >= 0; i--)
				{
					final Integer currentTime = atimes.get(i);
					final Double currentVal = avalues.get(i);

					if (btimes.contains(currentTime)) {
						final Double partVal = bvalues.get(btimes.indexOf(currentTime));
						if (partVal > currentVal) {
							if (mostVisible == null) {
								mostVisible = partner;
							} else if (mostVisible == this) {
								return null;
							}
						} else if (partVal < currentVal) {
							if (mostVisible == null) {
								mostVisible = this;
							} else if (mostVisible == partner) {
								return null;
							}
						} else {
							// System.out.println("Equal entries spell success");
						}
						// btimes.remove(currentTime);
						// bvalues.remove(partVal);
					} else if (currentVal < 1) {
						if (mostVisible == null) {
							mostVisible = partner;
						} else if (mostVisible == this) {
							return null;
						}
					}
				}
				for (int i = btimes.size() - 1; i >= 0; i--)
				// count down from top, meaning that removing the current value
				// causes no harm
				{
					final Integer currentTime = btimes.get(i);
					final Double currentVal = bvalues.get(i);

					if (atimes.contains(currentTime)) {
						final Double partVal = avalues.get(atimes.indexOf(currentTime));
						if (partVal > currentVal) {
							if (mostVisible == null) {
								mostVisible = this;
							} else if (mostVisible == partner) {
								return null;
							}
						} else if (partVal < currentVal) {
							if (mostVisible == null) {
								mostVisible = partner;
							} else if (mostVisible == this) {
								return null;
							}
						}
					} else if (currentVal < 1) {
						if (mostVisible == null) {
							mostVisible = this;
						} else if (mostVisible == partner) {
							return null;
						}
					}
				}
				if (mostVisible == null) {
					return partner;// partner has priority!
				} else {
					return mostVisible;
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Error: Program attempted to compare visibility with non-visibility animation component.\nThis... probably means something is horribly wrong. Save your work, if you can.");
			}
		}
		return null;
	}

	public boolean tans() {
		return tags.contains("Bezier") || tags.contains("Hermite") || (inTans.size() > 0);
	}

	public void linearize() {
		if (tags.remove("Bezier") || tags.remove("Hermite")) {
			tags.add("Linear");
			inTans.clear();
			outTans.clear();
		}
	}

	public void copyFrom(final AnimFlag source) {
		times.addAll(source.times);
		values.addAll(source.values);
		final boolean source_tans = source.tans();
		final boolean mtans = tans();
		if (source_tans && mtans) {
			inTans.addAll(source.inTans);
			outTans.addAll(source.outTans);
		} else if (mtans) {
			JOptionPane.showMessageDialog(null,
					"Some animations will lose complexity due to transfer incombatibility. There will probably be no visible change.");
			inTans.clear();
			outTans.clear();
			tags = source.tags;
			// Probably makes this flag linear, but certainly makes it more like
			// the copy source
		}
	}

	public void deleteAnim(final Animation anim) {
		if (!hasGlobalSeq) {
			final boolean tans = tans();
			for (int index = times.size() - 1; index >= 0; index--) {
				final Integer inte = times.get(index);
				final int i = inte;
				// int index = times.indexOf(inte);
				if ((i >= anim.getStart()) && (i <= anim.getEnd())) {
					// If this "i" is a part of the anim being removed

					times.remove(index);
					values.remove(index);
					if (tans) {
						inTans.remove(index);
						outTans.remove(index);
					}
				}
			}
		} else {
			System.out.println("KeyFrame deleting was blocked by a GlobalSequence");
		}

		// BOOM magic happens
	}

	public void deleteAt(final int index) {
		times.remove(index);
		values.remove(index);
		if (tans()) {
			inTans.remove(index);
			outTans.remove(index);
		}
	}

	/**
	 * Copies time track data from a certain interval into a different, new
	 * interval. The AnimFlag source of the data to copy cannot be same AnimFlag
	 * into which the data is copied, or else a ConcurrentModificationException will
	 * be thrown.
	 */
	public void copyFrom(final AnimFlag source, final int sourceStart, final int sourceEnd, final int newStart,
			final int newEnd) {
		// Timescales a part of the AnimFlag from the source into the new time
		// "newStart" to "newEnd"
		boolean tans = source.tans();
		if (tans && tags.contains("Linear")) {
			final int x = JOptionPane.showConfirmDialog(null,
					"ERROR! A source was found to have Linear and Nonlinear motion simultaneously. Does the following have non-zero data? "
							+ source.inTans,
					"Help This Program!", JOptionPane.YES_NO_OPTION);
			if (x == JOptionPane.NO_OPTION) {
				tans = false;
			}
		}
		for (final Integer inte : source.times) {
			final int i = inte;
			final int index = source.times.indexOf(inte);
			if ((i >= sourceStart) && (i <= sourceEnd)) {
				// If this "i" is a part of the anim being rescaled
				final double ratio = (double) (i - sourceStart) / (double) (sourceEnd - sourceStart);
				times.add((int) (newStart + (ratio * (newEnd - newStart))));
				values.add(cloneValue(source.values.get(index)));
				if (tans) {
					inTans.add(cloneValue(source.inTans.get(index)));
					outTans.add(cloneValue(source.outTans.get(index)));
				}
			}
		}

		sort();

		// BOOM magic happens
	}

	public void timeScale(final int start, final int end, final int newStart, final int newEnd) {
		// Timescales a part of the AnimFlag from section "start" to "end" into
		// the new time "newStart" to "newEnd"
		// if( newEnd > newStart )
		// {
		for (int z = 0; z < times.size(); z++)// Integer inte: times )
		{
			final Integer integer = times.get(z);
			final int i = integer;
			if ((i >= start) && (i <= end)) {
				// If this "i" is a part of the anim being rescaled
				final double ratio = (double) (i - start) / (double) (end - start);
				times.set(z, (int) (newStart + (ratio * (newEnd - newStart))));
			}
		}
		// }
		// else
		// {
		// for( Integer inte: times )
		// {
		// int i = inte.intValue();
		// if( i >= end && i <= start )
		// {
		// //If this "i" is a part of the anim being rescaled
		// double ratio = (double)(i-start)/(double)(end-start);
		// times.set(times.indexOf(inte),new Integer((int)(newStart + ( ratio *
		// ( newStart - newEnd ) ) ) ) );
		// }
		// }
		// }

		sort();

		// BOOM magic happens
	}

	public void sort() {
		final int low = 0;
		final int high = times.size() - 1;
		if (size() > 1) {
			quicksort(low, high);
		}
	}

	private void quicksort(final int low, final int high) {
		// Thanks to Lars Vogel for the quicksort concept code (something to
		// look at), found on google
		// (re-written by Eric "Retera" for use in AnimFlags)
		int i = low, j = high;
		final Integer pivot = times.get(low + ((high - low) / 2));

		while (i <= j) {
			while (times.get(i) < pivot) {
				i++;
			}
			while (times.get(j) > pivot) {
				j--;
			}
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}

		if (low < j) {
			quicksort(low, j);
		}
		if (i < high) {
			quicksort(i, high);
		}
	}

	private void exchange(final int i, final int j) {
		final Integer iTime = times.get(i);
		final Object iValue = values.get(i);

		times.set(i, times.get(j));
		try {
			values.set(i, values.get(j));
		} catch (final Exception e) {
			e.printStackTrace();
			// System.out.println(getName()+":
			// "+times.size()+","+values.size());
			// System.out.println(times.get(0)+": "+values.get(0));
			// System.out.println(times.get(1));
		}

		times.set(j, iTime);
		values.set(j, iValue);

		if (inTans.size() > 0)// if we have to mess with Tans
		{
			final Object iInTan = inTans.get(i);
			final Object iOutTan = outTans.get(i);

			inTans.set(i, inTans.get(j));
			outTans.set(i, outTans.get(j));

			inTans.set(j, iInTan);
			outTans.set(j, iOutTan);
		}
	}

	public ArrayList getValues() {
		return values;
	}

	public ArrayList<Integer> getTimes() {
		return times;
	}

	public ArrayList getInTans() {
		return inTans;
	}

	public ArrayList getOutTans() {
		return outTans;
	}

	public int ceilIndex(final int time) {
		if (times.size() == 0) {
			return 0;
		}
		final int ceilIndex = ceilIndex(time, 0, times.size() - 1);
		if (ceilIndex == -1) {
			return times.size() - 1;
		}
		return ceilIndex;
	}

	/*
	 * Rather than spending time visualizing corner cases for these, I borrowed
	 * logic from: https://www.geeksforgeeks.org/ceiling-in-a-sorted-array/
	 */
	private int ceilIndex(final int time, final int lo, final int hi) {
		if (time <= times.get(lo)) {
			return lo;
		}
		if (time > times.get(hi)) {
			return -1;
		}
		final int mid = (lo + hi) / 2;
		final Integer midTime = times.get(mid);
		if (midTime == time) {
			return mid;
		} else if (midTime < time) {
			if (((mid + 1) <= hi) && (time <= times.get(mid + 1))) {
				return mid + 1;
			} else {
				return ceilIndex(time, mid + 1, hi);
			}
		} else {
			if (((mid - 1) >= lo) && (time > times.get(mid - 1))) {
				return mid;
			} else {
				return ceilIndex(time, lo, mid - 1);
			}
		}
	}

	public int floorIndex(final int time) {
		if (times.size() == 0) {
			return -1;
		}
		final int floorIndex = floorIndex(time, 0, times.size() - 1);
		return floorIndex;
	}

	/*
	 * Rather than spending time visualizing corner cases for these, I borrowed
	 * logic from: https://www.geeksforgeeks.org/floor-in-a-sorted-array/
	 */
	private int floorIndex(final int time, final int lo, final int hi) {
		if (lo > hi) {
			return -1;
		}
		if (time >= times.get(hi)) {
			return hi;
		}
		final int mid = (lo + hi) / 2;
		final Integer midTime = times.get(mid);
		if (times.get(mid) == time) {
			return mid;
		}
		if ((mid > 0) && (times.get(mid - 1) <= time) && (time < midTime)) {
			return mid - 1;
		}
		if (time > midTime) {
			return floorIndex(time, mid + 1, hi);
		} else {
			return floorIndex(time, lo, mid - 1);
		}
	}

	public static final QuaternionRotation ROTATE_IDENTITY = new QuaternionRotation(0, 0, 0, 1);
	public static final Vertex SCALE_IDENTITY = new Vertex(1, 1, 1);
	public static final Vertex TRANSLATE_IDENTITY = new Vertex(0, 0, 0);

	private Object identity(final int typeid) {
		switch (typeid) {
		case ALPHA | OTHER_TYPE: {
			return 1.;
		}
		case TRANSLATION:
			return TRANSLATE_IDENTITY;
		case SCALING:
		case COLOR: {
			return SCALE_IDENTITY;
		}
		case ROTATION: {
			return ROTATE_IDENTITY;
		}
		case TEXTUREID:
		// Integer
		{
			System.err.println("Texture identity used in renderer... TODO make this function more intelligent.");
			return 0;
		}
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Interpolates at a given time. The lack of generics on this function is
	 * abysmal, but currently this is how the codebase is.
	 */
	public Object interpolateAt(final AnimatedRenderEnvironment animatedRenderEnvironment) {
		if ((animatedRenderEnvironment == null) || (animatedRenderEnvironment.getCurrentAnimation() == null)) {
			if (values.size() > 0) {
				return values.get(0);
			}
			return identity(typeid);
		}
		int localTypeId = typeid;
		if ((localTypeId == ROTATION) && (size() > 0) && (values.get(0) instanceof Double)) {
			localTypeId = ALPHA; // magic Camera rotation!
		}
		if (times.isEmpty()) {
			return identity(localTypeId);
		}
		// TODO ghostwolf says to stop using binary search, because linear walking is
		// faster for the small MDL case
		int time;
		int ceilIndex;
		int floorIndex;
		Object floorInTan;
		Object floorOutTan;
		Object floorValue;
		Object ceilValue;
		Integer floorIndexTime;
		Integer ceilIndexTime;
		float timeBetweenFrames;
		if (hasGlobalSeq() && (getGlobalSeq() >= 0)) {
			time = animatedRenderEnvironment.getGlobalSeqTime(getGlobalSeq());
			final int floorAnimStartIndex = Math.max(0, floorIndex(1));
			final int floorAnimEndIndex = Math.max(0, floorIndex(getGlobalSeq()));
			floorIndex = Math.max(0, floorIndex(time));
			ceilIndex = ceilIndex(time);
			if (ceilIndex < floorIndex) {
				// retarded repeated keyframes issue, see Peasant's Bone_Chest
				// at time 18300
				ceilIndex = floorIndex;
			}
			floorValue = values.get(floorIndex);
			floorInTan = tans() ? inTans.get(floorIndex) : null;
			floorOutTan = tans() ? outTans.get(floorIndex) : null;
			ceilValue = values.get(ceilIndex);
			floorIndexTime = times.get(floorIndex);
			ceilIndexTime = times.get(ceilIndex);
			timeBetweenFrames = ceilIndexTime - floorIndexTime;
			if (ceilIndexTime < 0) {
				return identity(localTypeId);
			}
			if (floorIndexTime > getGlobalSeq()) {
				if (values.size() > 0) {
					// out of range global sequences end up just using the higher value keyframe
					return values.get(floorIndex);
				}
				return identity(localTypeId);
			}
			if ((floorIndexTime < 0) && (ceilIndexTime > getGlobalSeq())) {
				return identity(localTypeId);
			} else if (floorIndexTime < 0) {
				floorValue = identity(localTypeId);
				floorInTan = floorOutTan = identity(localTypeId);
			} else if (ceilIndexTime > getGlobalSeq()) {
				ceilValue = values.get(floorAnimStartIndex);
				ceilIndex = floorAnimStartIndex;
			}
			if (floorIndex == ceilIndex) {
				return floorValue;
			}
		} else {
			final BasicTimeBoundProvider animation = animatedRenderEnvironment.getCurrentAnimation();
			time = animation.getStart() + animatedRenderEnvironment.getAnimationTime();
			final int floorAnimStartIndex = Math.max(0, floorIndex(animation.getStart() + 1));
			final int floorAnimEndIndex = Math.max(0, floorIndex(animation.getEnd()));
			floorIndex = floorIndex(time);
			ceilIndex = ceilIndex(time);
			if (ceilIndex < floorIndex) {
				// retarded repeated keyframes issue, see Peasant's Bone_Chest
				// at time 18300
				ceilIndex = floorIndex;
			}
			ceilValue = values.get(ceilIndex);
			ceilIndexTime = times.get(ceilIndex);
			if (ceilIndexTime < animation.getStart()) {
				return identity(localTypeId);
			}
			final int lookupFloorIndex = Math.max(0, floorIndex);
			floorValue = values.get(lookupFloorIndex);
			floorInTan = tans() ? inTans.get(lookupFloorIndex) : null;
			floorOutTan = tans() ? outTans.get(lookupFloorIndex) : null;
			floorIndexTime = times.get(lookupFloorIndex);
			if (floorIndexTime > animation.getEnd()) {
				return identity(localTypeId);
			}
			if ((floorIndexTime < animation.getStart()) && (ceilIndexTime > animation.getEnd())) {
				return identity(localTypeId);
			} else if ((floorIndex == -1) || (floorIndexTime < animation.getStart())) {
				floorValue = values.get(floorAnimEndIndex);
				floorIndexTime = times.get(floorAnimStartIndex);
				if (tans()) {
					floorInTan = inTans.get(floorAnimEndIndex);
					floorOutTan = inTans.get(floorAnimEndIndex);
//						floorIndexTime = times.get(floorAnimEndIndex);
				}
				timeBetweenFrames = times.get(floorAnimEndIndex) - animation.getStart();
			} else if ((ceilIndexTime > animation.getEnd())
					|| ((ceilIndexTime < time) && (times.get(floorAnimEndIndex) < time))) {
				if (times.get(floorAnimStartIndex) == animation.getStart()) {
					ceilValue = values.get(floorAnimStartIndex);
					ceilIndex = floorAnimStartIndex;
					ceilIndexTime = animation.getEnd();
					timeBetweenFrames = ceilIndexTime - floorIndexTime;
				} else {
					ceilIndex = ceilIndex(animation.getStart());
					ceilValue = values.get(ceilIndex);
					ceilIndexTime = animation.getEnd();// times.get(ceilIndex);
					timeBetweenFrames = animation.getEnd() - animation.getStart();
				}
				// NOTE: we just let it be in this case, based on
				// Water Elemental's birth
			} else {
				timeBetweenFrames = ceilIndexTime - floorIndexTime;
			}
			if (floorIndex == ceilIndex) {
				return floorValue;
			}
		}
		switch (localTypeId) {
		case ALPHA | OTHER_TYPE: {
			// Double
			final Double previous = (Double) floorValue;
			final Double next = (Double) ceilValue;
			switch (getInterpTypeAsEnum()) {
			case BEZIER: {
				final Double previousOutTan = (Double) floorOutTan;
				final Double nextInTan = (Double) inTans.get(ceilIndex);
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final double bezier = MathUtils.bezier(previous, previousOutTan, nextInTan, next,
						(time - floorTime) / timeBetweenFrames);
				return bezier;
			}
			case DONT_INTERP:
				return floorValue;
			case HERMITE: {
				final Double previousOutTan = (Double) floorOutTan;
				final Double nextInTan = (Double) inTans.get(ceilIndex);
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final double hermite = MathUtils.hermite(previous, previousOutTan, nextInTan, next,
						(time - floorTime) / timeBetweenFrames);
				return hermite;
			}
			case LINEAR:
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final double lerp = MathUtils.lerp(previous, next, (time - floorTime) / timeBetweenFrames);
				return lerp;
			default:
				throw new IllegalStateException();
			}
		}
		case TRANSLATION:
		case SCALING:
		case COLOR: {
			// Vertex
			final Vertex previous = (Vertex) floorValue;
			final Vertex next = (Vertex) ceilValue;
			switch (getInterpTypeAsEnum()) {
			case BEZIER: {
				final Vertex previousOutTan = (Vertex) floorOutTan;
				final Vertex nextInTan = (Vertex) inTans.get(ceilIndex);
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final float timeFactor = (time - floorTime) / timeBetweenFrames;
				final Vertex bezier = new Vertex(
						MathUtils.bezier(previous.x, previousOutTan.x, nextInTan.x, next.x, timeFactor),
						MathUtils.bezier(previous.y, previousOutTan.y, nextInTan.y, next.y, timeFactor),
						MathUtils.bezier(previous.z, previousOutTan.z, nextInTan.z, next.z, timeFactor));
				return bezier;
			}
			case DONT_INTERP:
				return floorValue;
			case HERMITE: {
				final Vertex previousOutTan = (Vertex) floorOutTan;
				final Vertex nextInTan = (Vertex) inTans.get(ceilIndex);
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final float timeFactor = (time - floorTime) / timeBetweenFrames;
				final Vertex hermite = new Vertex(
						MathUtils.hermite(previous.x, previousOutTan.x, nextInTan.x, next.x, timeFactor),
						MathUtils.hermite(previous.y, previousOutTan.y, nextInTan.y, next.y, timeFactor),
						MathUtils.hermite(previous.z, previousOutTan.z, nextInTan.z, next.z, timeFactor));
				return hermite;
			}
			case LINEAR:
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final float timeFactor = (time - floorTime) / timeBetweenFrames;
				final Vertex lerp = new Vertex(MathUtils.lerp(previous.x, next.x, timeFactor),
						MathUtils.lerp(previous.y, next.y, timeFactor), MathUtils.lerp(previous.z, next.z, timeFactor));
				return lerp;
			default:
				throw new IllegalStateException();
			}
		}
		case ROTATION: {
			// Quat
			final QuaternionRotation previous = (QuaternionRotation) floorValue;
			final QuaternionRotation next = (QuaternionRotation) ceilValue;
			switch (getInterpTypeAsEnum()) {
			case BEZIER: {
				final QuaternionRotation previousOutTan = (QuaternionRotation) floorOutTan;
				final QuaternionRotation nextInTan = (QuaternionRotation) inTans.get(ceilIndex);
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final float timeFactor = (time - floorTime) / timeBetweenFrames;
				final QuaternionRotation result = new QuaternionRotation(0, 0, 0, 0);
				return QuaternionRotation.ghostwolfSquad(result, previous, previousOutTan, nextInTan, next, timeFactor);
			}
			case DONT_INTERP:
				return floorValue;
			case HERMITE: {
				final QuaternionRotation previousOutTan = (QuaternionRotation) floorOutTan;
				final QuaternionRotation nextInTan = (QuaternionRotation) inTans.get(ceilIndex);
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final float timeFactor = (time - floorTime) / timeBetweenFrames;
				final QuaternionRotation result = new QuaternionRotation(0, 0, 0, 0);
				return QuaternionRotation.ghostwolfSquad(result, previous, previousOutTan, nextInTan, next, timeFactor);
			}
			case LINEAR:
				final Integer floorTime = floorIndexTime;
				final Integer ceilTime = ceilIndexTime;
				final float timeFactor = (time - floorTime) / timeBetweenFrames;
				final QuaternionRotation result = new QuaternionRotation(0, 0, 0, 0);
				return QuaternionRotation.slerp(result, previous, next, timeFactor);
			default:
				throw new IllegalStateException();
			}
		}
		case TEXTUREID:
		// Integer
		{
			final Integer previous = (Integer) floorValue;
			switch (getInterpTypeAsEnum()) {
			case DONT_INTERP:
			case BEZIER: // dont use bezier on these, does that even make any sense?
			case HERMITE: // dont use hermite on these, does that even make any sense?
			case LINEAR: // dont use linear on these, does that even make any sense?
				return previous;
			default:
				throw new IllegalStateException();
			}
		}
		}
		throw new IllegalStateException();
	}

	public void removeKeyframe(final int trackTime) {
		final int keyframeIndex = floorIndex(trackTime);
		if ((keyframeIndex >= size()) || (times.get(keyframeIndex) != trackTime)) {
			throw new IllegalStateException("Attempted to remove keyframe, but no keyframe was found (" + keyframeIndex
					+ " @ time " + trackTime + ")");
		} else {
			times.remove(keyframeIndex);
			values.remove(keyframeIndex);
			if (tans()) {
				inTans.remove(keyframeIndex);
				outTans.remove(keyframeIndex);
			}
		}
	}

	public void addKeyframe(final int trackTime, final Object value) {
		int keyframeIndex = ceilIndex(trackTime);
		if (keyframeIndex == (times.size() - 1)) {
			if (times.isEmpty()) {
				keyframeIndex = 0;
			} else if (trackTime > times.get(times.size() - 1)) {
				keyframeIndex = times.size();
			}
		}
		times.add(keyframeIndex, trackTime);
		values.add(keyframeIndex, value);
	}

	public void addKeyframe(final int trackTime, final Object value, final Object inTan, final Object outTan) {
		int keyframeIndex = ceilIndex(trackTime);
		if (keyframeIndex == (times.size() - 1)) {
			if (times.isEmpty()) {
				keyframeIndex = 0;
			} else if (trackTime > times.get(times.size() - 1)) {
				keyframeIndex = times.size();
			}
		}
		times.add(keyframeIndex, trackTime);
		values.add(keyframeIndex, value);
		inTans.add(keyframeIndex, inTan);
		outTans.add(keyframeIndex, outTan);
	}

	public void setKeyframe(final Integer time, final Object value) {
		if (tans()) {
			throw new IllegalStateException();
		}
		// TODO maybe binary search, ghostwolf says it's not worth it
		for (int index = 0; index < times.size(); index++) {
			if (times.get(index).equals(time)) {
				values.set(index, value);
			}
		}
	}

	public void setKeyframe(final Integer time, final Object value, final Object inTan, final Object outTan) {
		if (!tans()) {
			throw new IllegalStateException();
		}
		for (int index = 0; index < times.size(); index++) {
			if (times.get(index).equals(time)) {
				values.set(index, value);
				inTans.set(index, inTan);
				outTans.set(index, outTan);
			}
		}
	}

	public void slideKeyframe(final int startTrackTime, final int endTrackTime) {
		if (times.size() < 1) {
			throw new IllegalStateException("Unable to slide keyframe: no frames exist");
		}
		final int startIndex = floorIndex(startTrackTime);
		final int endIndex = floorIndex(endTrackTime);
		if (times.get(endIndex) == endTrackTime) {
			throw new IllegalStateException("Sliding this keyframe would create duplicate entries at one time!");
		}
		times.set(startIndex, endTrackTime);
		sort();
	}

	public void setName(final String title) {
		this.title = title;
	}
}
