package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.InterpolationType;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatArrayTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxUInt32Timeline;
import com.hiveworkshop.rms.ui.application.edit.animation.BasicTimeBoundProvider;
import com.hiveworkshop.rms.ui.application.viewer.AnimatedRenderEnvironment;
import com.hiveworkshop.rms.util.MathUtils;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;

import java.util.List;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class Vec3AnimFlag extends AnimFlag<Vec3> {


//	public Vec3AnimFlag(MdlxTimeline<Float[]> timeline) {
//		super(timeline);
//	}

	public Vec3AnimFlag(final MdlxFloatArrayTimeline timeline) {
		super(timeline);
//		name = AnimationMap.ID_TO_TAG.get(timeline.name).getMdlToken();
//		generateTypeId();
//
//		interpolationType = timeline.interpolationType;
//
//		final int globalSequenceId = timeline.globalSequenceId;
//		if (globalSequenceId >= 0) {
//			setGlobalSeqId(globalSequenceId);
//			setHasGlobalSeq(true);
//		}

		final long[] frames = timeline.frames;
		final Object[] values = timeline.values;
		final Object[] inTans = timeline.inTans;
		final Object[] outTans = timeline.outTans;

		if (frames.length > 0) {
			setVectorSize(values[0]);
			final boolean hasTangents = interpolationType.tangential();


			for (int i = 0, l = frames.length; i < l; i++) {
				final Object value = values[i];
				Vec3 valueAsObject = null;
				Vec3 inTanAsObject = null;
				Vec3 outTanAsObject = null;

				if (isFloat) {
					final float[] valueAsArray = (float[]) value;

					if (vectorSize == 3) {
						valueAsObject = new Vec3(valueAsArray);

						if (hasTangents) {
							inTanAsObject = new Vec3((float[]) inTans[i]);
							outTanAsObject = new Vec3((float[]) outTans[i]);
						}
					}
				}

				addEntry((int) frames[i], valueAsObject, inTanAsObject, outTanAsObject);
			}
		}
	}

	public Vec3AnimFlag(String title, List<Integer> times, List<Vec3> values) {
		super(title, times, values);
	}

	public Vec3AnimFlag(String title) {
		super(title);
	}

	public Vec3AnimFlag(AnimFlag<Vec3> af) {
		super(af);
	}

	public Vec3AnimFlag(Vec3AnimFlag af) {
		super(af);
	}

	public static Vec3AnimFlag createEmpty2018(final String title, final InterpolationType interpolationType, final Integer globalSeq) {
		final Vec3AnimFlag flag = new Vec3AnimFlag(title);
//		flag.name = title;
		flag.interpolationType = interpolationType;
		flag.generateTypeId();
		flag.setGlobSeq(globalSeq);
		return flag;
	}

	public Vec3 interpolateAt(final AnimatedRenderEnvironment animatedRenderEnvironment) {
//		System.out.println(name + ", interpolateAt");
		if ((animatedRenderEnvironment == null) || (animatedRenderEnvironment.getCurrentAnimation() == null)) {
//			System.out.println("~~ animatedRenderEnvironment == null");
			if (values.size() > 0) {
				return values.get(0);
			}
			return (Vec3) identity(typeid);
		}
		int localTypeId = typeid;
//		System.out.println("typeId 1: " + typeid);

		if (times.isEmpty()) {
//			System.out.println(name + ", ~~ no times");
			return (Vec3) identity(localTypeId);
		}
		// TODO ghostwolf says to stop using binary search, because linear walking is faster for the small MDL case
		final int time;
		int ceilIndex;
		final int floorIndex;
		Vec3 floorOutTan;
		Vec3 floorValue;
		Vec3 ceilValue;
		Integer floorIndexTime;
		Integer ceilIndexTime;
		final float timeBetweenFrames;
		if (hasGlobalSeq() && (getGlobalSeq() >= 0)) {
//			System.out.println(name + ", ~~ hasGlobalSeq");
			time = animatedRenderEnvironment.getGlobalSeqTime(getGlobalSeq());
			final int floorAnimStartIndex = Math.max(0, floorIndex(1));
			floorIndex = Math.max(0, floorIndex(time));

			ceilIndex = Math.max(floorIndex, ceilIndex(time)); // retarded repeated keyframes issue, see Peasant's Bone_Chest at time 18300

			floorValue = values.get(floorIndex);
			floorOutTan = tans() ? outTans.get(floorIndex) : null;
			ceilValue = values.get(ceilIndex);
			floorIndexTime = times.get(floorIndex);
			ceilIndexTime = times.get(ceilIndex);
			timeBetweenFrames = ceilIndexTime - floorIndexTime;
			if (ceilIndexTime < 0) {
				return (Vec3) identity(localTypeId);
			}
			if (floorIndexTime > getGlobalSeq()) {
				if (values.size() > 0) {
					// out of range global sequences end up just using the higher value keyframe
					return values.get(floorIndex);
				}
				return (Vec3) identity(localTypeId);
			}
			if ((floorIndexTime < 0) && (ceilIndexTime > getGlobalSeq())) {
				return (Vec3) identity(localTypeId);
			} else if (floorIndexTime < 0) {
				floorValue = (Vec3) identity(localTypeId);
				floorOutTan = (Vec3) identity(localTypeId);
			} else if (ceilIndexTime > getGlobalSeq()) {
				ceilValue = values.get(floorAnimStartIndex);
				ceilIndex = floorAnimStartIndex;
			}
			if (floorIndex == ceilIndex) {
				return floorValue;
			}
		} else {
//			System.out.println(name + ", ~~ no global seq");
			final BasicTimeBoundProvider animation = animatedRenderEnvironment.getCurrentAnimation();
			time = animation.getStart() + animatedRenderEnvironment.getAnimationTime();
			final int floorAnimStartIndex = Math.max(0, floorIndex(animation.getStart() + 1));
			final int floorAnimEndIndex = Math.max(0, floorIndex(animation.getEnd()));
			floorIndex = floorIndex(time);
			ceilIndex = Math.max(floorIndex, ceilIndex(time)); // retarded repeated keyframes issue, see Peasant's Bone_Chest at time 18300

			ceilIndexTime = times.get(ceilIndex);
			final int lookupFloorIndex = Math.max(0, floorIndex);
			floorIndexTime = times.get(lookupFloorIndex);
			if (ceilIndexTime < animation.getStart() || floorIndexTime > animation.getEnd()) {
//				System.out.println(name + ", ~~~~ identity(localTypeId)1 " + localTypeId + " id: " + identity(localTypeId));
				return (Vec3) identity(localTypeId);
			}
			ceilValue = values.get(ceilIndex);
			floorValue = values.get(lookupFloorIndex);
			floorOutTan = tans() ? outTans.get(lookupFloorIndex) : null;
			if ((floorIndexTime < animation.getStart()) && (ceilIndexTime > animation.getEnd())) {
//				System.out.println(name + ", ~~~~ identity(localTypeId)3");
				return (Vec3) identity(localTypeId);
			} else if ((floorIndex == -1) || (floorIndexTime < animation.getStart())) {
				floorValue = values.get(floorAnimEndIndex);
				floorIndexTime = times.get(floorAnimStartIndex);
				if (tans()) {
					floorOutTan = inTans.get(floorAnimEndIndex);
//					floorIndexTime = times.get(floorAnimEndIndex);
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
					timeBetweenFrames = animation.getEnd() - animation.getStart();
				}
				// NOTE: we just let it be in this case, based on Water Elemental's birth
			} else {
				timeBetweenFrames = ceilIndexTime - floorIndexTime;
			}
			if (floorIndex == ceilIndex) {
//				System.out.println(name + ", ~~~~ floorValue");
				return floorValue;
			}
		}
//		System.out.println(name + ", ~~ Something");

		final Integer floorTime = floorIndexTime;
		final float timeFactor = (time - floorTime) / timeBetweenFrames;

		// Integer
		switch (localTypeId) {
			case TRANSLATION, SCALING, COLOR -> {
				// Vertex

				return switch (interpolationType) {
					case BEZIER -> floorValue.bezier(floorOutTan, inTans.get(ceilIndex), ceilValue, timeFactor, new Vec3());
					case DONT_INTERP -> floorValue;
					case HERMITE -> floorValue.hermite(floorOutTan, inTans.get(ceilIndex), ceilValue, timeFactor, new Vec3());
					case LINEAR -> floorValue.lerp(ceilValue, timeFactor, new Vec3());
				};
			}
		}
		throw new IllegalStateException();
	}

	@Override
//	public MdlxTimeline<Float[]> toMdlx(TimelineContainer container) {
//		return null;
//	}

	public MdlxFloatArrayTimeline toMdlx(final TimelineContainer container) {
		final MdlxFloatArrayTimeline timeline = new MdlxFloatArrayTimeline(3);

		setVectorSize(values.get(0));
		timeline.name = getWar3ID(container);
		timeline.interpolationType = interpolationType;
		timeline.globalSequenceId = getGlobalSeqId();

		final List<Integer> times = getTimes();
		final List<Vec3> values = getValues();
		final List<Vec3> inTans = getInTans();
		final List<Vec3> outTans = getOutTans();

		final long[] tempFrames = new long[times.size()];
		final float[][] tempValues = new float[times.size()][];
		final float[][] tempInTans = new float[times.size()][];
		final float[][] tempOutTans = new float[times.size()][];

		final boolean hasTangents = timeline.interpolationType.tangential();

		for (int i = 0, l = times.size(); i < l; i++) {
			final Vec3 value = values.get(i);

			tempFrames[i] = times.get(i).longValue();

			tempValues[i] = value.toFloatArray();

			if (hasTangents) {
				tempInTans[i] = inTans.get(i).toFloatArray();
				tempOutTans[i] = outTans.get(i).toFloatArray();
			} else {
				tempInTans[i] = (new Vec3()).toFloatArray();
				tempOutTans[i] = (new Vec3()).toFloatArray();
			}
		}

		timeline.frames = tempFrames;
		timeline.values = tempValues;
		timeline.inTans = tempInTans;
		timeline.outTans = tempOutTans;

		return timeline;
	}
}
