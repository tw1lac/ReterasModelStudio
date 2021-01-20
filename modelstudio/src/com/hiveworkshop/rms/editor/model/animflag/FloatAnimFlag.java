package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatTimeline;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;
import com.hiveworkshop.rms.ui.application.edit.animation.BasicTimeBoundProvider;
import com.hiveworkshop.rms.ui.application.viewer.AnimatedRenderEnvironment;
import com.hiveworkshop.rms.util.MathUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class FloatAnimFlag extends AnimFlag<Float> {


//	public FloatAnimFlag(MdlxTimeline<Float[]> timeline) {
//		super(timeline);
//	}
	public FloatAnimFlag(final MdlxFloatTimeline timeline) {
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
				float valueAsObject = 0f;
				Float inTanAsObject = null;
				Float outTanAsObject = null;

				if (isFloat) {
					final float[] valueAsArray = (float[]) value;

					if (vectorSize == 1) {
						valueAsObject = valueAsArray[0];

						if (hasTangents) {
							inTanAsObject = ((float[]) inTans[i])[0];
							outTanAsObject = ((float[]) outTans[i])[0];
						}
					}
				}

				addEntry((int) frames[i], valueAsObject, inTanAsObject, outTanAsObject);
			}
		}
	}

	public FloatAnimFlag(String title, List<Integer> times, List<Float> values) {
		super(title, times, values);
	}

	public FloatAnimFlag(String title) {
		super(title);
	}

	public FloatAnimFlag(AnimFlag<Float> af) {
		super(af);
	}

	public FloatAnimFlag(FloatAnimFlag af) {
		super(af);
	}

	public Float interpolateAt(final AnimatedRenderEnvironment animatedRenderEnvironment) {
//		System.out.println(name + ", interpolateAt");
		if ((animatedRenderEnvironment == null) || (animatedRenderEnvironment.getCurrentAnimation() == null)) {
//			System.out.println("~~ animatedRenderEnvironment == null");
			if (values.size() > 0) {
				return values.get(0);
			}
			return (Float) identity(typeid);
		}
		int localTypeId = typeid;
//		System.out.println("typeId 1: " + typeid);
		if ((localTypeId == ROTATION) && (size() > 0) && (values.get(0) != null)) {
			localTypeId = ALPHA; // magic Camera rotation!
		}
		if (times.isEmpty()) {
//			System.out.println(name + ", ~~ no times");
			return (Float) identity(localTypeId);
		}
		// TODO ghostwolf says to stop using binary search, because linear walking is faster for the small MDL case
		final int time;
		int ceilIndex;
		final int floorIndex;
		Float floorOutTan;
		Float floorValue;
		Float ceilValue;
		Integer floorIndexTime;
		Integer ceilIndexTime;
		final float timeBetweenFrames;
		if (hasGlobalSeq() && (getGlobalSeq() >= 0)) {
//			System.out.println(name + ", ~~ hasGlobalSeq");
			time = animatedRenderEnvironment.getGlobalSeqTime(getGlobalSeq());
			final int floorAnimStartIndex = Math.max(0, floorIndex(1));
			floorIndex = Math.max(0, floorIndex(time));
			ceilIndex = Math.max(floorIndex, ceilIndex(time));

			floorValue = values.get(floorIndex);
			floorOutTan = tans() ? outTans.get(floorIndex) : null;
			ceilValue = values.get(ceilIndex);
			floorIndexTime = times.get(floorIndex);
			ceilIndexTime = times.get(ceilIndex);
			timeBetweenFrames = ceilIndexTime - floorIndexTime;
			if (ceilIndexTime < 0) {
				return (Float) identity(localTypeId);
			}
			if (floorIndexTime > getGlobalSeq()) {
				if (values.size() > 0) {
					// out of range global sequences end up just using the higher value keyframe
					return values.get(floorIndex);
				}
				return (Float) identity(localTypeId);
			}
			if ((floorIndexTime < 0) && (ceilIndexTime > getGlobalSeq())) {
				return (Float) identity(localTypeId);
			} else if (floorIndexTime < 0) {
				floorValue = (Float) identity(localTypeId);
				floorOutTan = (Float) identity(localTypeId);
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
			ceilIndex = Math.max(floorIndex, ceilIndex(time));
			ceilIndexTime = times.get(ceilIndex);


			final int lookupFloorIndex = Math.max(0, floorIndex);
			floorIndexTime = times.get(lookupFloorIndex);

			if (ceilIndexTime < animation.getStart() || floorIndexTime > animation.getEnd() || (floorIndexTime < animation.getStart() && ceilIndexTime > animation.getEnd())) {
//				System.out.println(name + ", ~~~~ identity(localTypeId)1 " + localTypeId + " id: " + identity(localTypeId));
				return (Float) identity(localTypeId);
			}

			ceilValue = values.get(ceilIndex);
			floorValue = values.get(lookupFloorIndex);
			floorOutTan = tans() ? outTans.get(lookupFloorIndex) : null;

			if ((floorIndex == -1) || (floorIndexTime < animation.getStart())) {
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
		if (localTypeId == ALPHA || localTypeId == OTHER_TYPE) {
			return switch (interpolationType) {
				case BEZIER -> MathUtils.bezier(floorValue, floorOutTan, inTans.get(ceilIndex), ceilValue, timeFactor);
				case DONT_INTERP -> floorValue;
				case HERMITE -> MathUtils.hermite(floorValue, floorOutTan, inTans.get(ceilIndex), ceilValue, timeFactor);
				case LINEAR -> MathUtils.lerp(floorValue, ceilValue, timeFactor);
			};
		}
		throw new IllegalStateException();
	}

	@Override
//	public MdlxTimeline<Float[]> toMdlx(TimelineContainer container) {
//		return null;
//	}

	public MdlxFloatTimeline toMdlx(final TimelineContainer container) {
		final MdlxFloatTimeline timeline = new MdlxFloatTimeline();
		setVectorSize(values.get(0));

		timeline.name = getWar3ID(container);
		timeline.interpolationType = interpolationType;
		timeline.globalSequenceId = getGlobalSeqId();

		final List<Integer> times = getTimes();
		final List<Float> values = getValues();
		final List<Float> inTans = getInTans();
		final List<Float> outTans = getOutTans();

		final long[] tempFrames = new long[times.size()];
		final float[][] tempValues = new float[times.size()][];
		final float[][] tempInTans = new float[times.size()][];
		final float[][] tempOutTans = new float[times.size()][];

		final boolean hasTangents = timeline.interpolationType.tangential();

		for (int i = 0, l = times.size(); i < l; i++) {
			final Float value = values.get(i);

			tempFrames[i] = times.get(i).longValue();

			if (isFloat) {
				if (vectorSize == 1) {
					tempValues[i] = new float[] {value};

					if (hasTangents) {
						tempInTans[i] = new float[] {inTans.get(i)};
						tempOutTans[i] = new float[] {outTans.get(i)};
					} else {
						tempInTans[i] = new float[] {0};
						tempOutTans[i] = new float[] {0};
					}
				}
			}
		}

		timeline.frames = tempFrames;
		timeline.values = tempValues;
		timeline.inTans = tempInTans;
		timeline.outTans = tempOutTans;

		return timeline;
	}

	public FloatAnimFlag getMostVisible(final FloatAnimFlag partner) {
		if (partner != null) {
			if ((typeid == 0) && (partner.typeid == 0)) {
				final List<Integer> selfTimes = new ArrayList<>(times);
				final List<Integer> partnerTimes = new ArrayList<>(partner.times);
				final List<Float> selfValues = new ArrayList<>(values);
				final List<Float> partnerValues = new ArrayList<>(partner.values);

				FloatAnimFlag mostVisible = null;
				mostVisible = getMostVissibleAnimFlag(selfTimes, partnerTimes, selfValues, partnerValues, mostVisible, partner, this);
				if (mostVisible == null) return null;

				mostVisible = getMostVissibleAnimFlag(partnerTimes, selfTimes, partnerValues, selfValues, mostVisible, this, partner);
				if (mostVisible == null) return null;

				// partner has priority!
				return mostVisible;
			} else {
				JOptionPane.showMessageDialog(null,
						"Error: Program attempted to compare visibility with non-visibility animation component.\nThis... probably means something is horribly wrong. Save your work, if you can.");
			}
		}
		return null;
	}

	private FloatAnimFlag getMostVissibleAnimFlag(List<Integer> atimes, List<Integer> btimes, List<Float> avalues, List<Float> bvalues, FloatAnimFlag mostVisible, FloatAnimFlag flag1, FloatAnimFlag flag2) {
		for (int i = atimes.size() - 1; i >= 0; i--)
		// count down from top, meaning that removing the current value causes no harm
		{
			final Integer currentTime = atimes.get(i);
			final Float currentVal = avalues.get(i);

			if (btimes.contains(currentTime)) {
				final Float partVal = bvalues.get(btimes.indexOf(currentTime));
				if (partVal > currentVal) {
					mostVisible = getMostVis(flag1, flag2, mostVisible);
					if (mostVisible == null) return null;
				} else if (partVal < currentVal) {
					mostVisible = getMostVis(flag2, flag1, mostVisible);
					if (mostVisible == null) return null;
				}
			} else if (currentVal < 1) {
				mostVisible = getMostVis(flag1, flag2, mostVisible);
				if (mostVisible == null) return null;
			}
		}
		return mostVisible;
	}

	private static FloatAnimFlag getMostVis(FloatAnimFlag flag1, FloatAnimFlag flag2, FloatAnimFlag mostVisible) {
		if (mostVisible == null) {
			mostVisible = flag1;
		} else if (mostVisible == flag2) {
			return null;
		}
		return mostVisible;
	}
}
