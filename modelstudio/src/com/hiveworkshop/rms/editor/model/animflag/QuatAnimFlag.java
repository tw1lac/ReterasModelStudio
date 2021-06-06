package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.InterpolationType;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxFloatArrayTimeline;
import com.hiveworkshop.rms.util.Quat;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class QuatAnimFlag extends AnimFlag<Quat> {

	public QuatAnimFlag(String title) {
		super(title);
	}

	public QuatAnimFlag(AnimFlag<Quat> af) {
		super(af);
	}

	public QuatAnimFlag(final MdlxFloatArrayTimeline timeline) {
		super(timeline);

		final long[] frames = timeline.frames;
		final Object[] values = timeline.values;
		final Object[] inTans = timeline.inTans;
		final Object[] outTans = timeline.outTans;

		if (frames.length > 0) {
			boolean hasTangents = interpolationType.tangential();

			for (int i = 0, l = frames.length; i < l; i++) {
				final Object value = values[i];
				Quat valueAsObject = new Quat((float[]) value);

				Quat inTanAsObject = null;
				Quat outTanAsObject = null;

				if (hasTangents) {
					inTanAsObject = new Quat((float[]) inTans[i]);
					outTanAsObject = new Quat((float[]) outTans[i]);
				}

				addEntry((int) frames[i], valueAsObject, inTanAsObject, outTanAsObject);
			}
		}
	}

	public QuatAnimFlag(String title, InterpolationType interpolationType, Integer globalSeq){
		super(title);
		this.interpolationType = interpolationType;
		setGlobSeq(globalSeq);
	}

	public AnimFlag<Quat> getEmptyCopy(){
		QuatAnimFlag newFlag = new QuatAnimFlag(name);
		newFlag.setSettingsFrom(this);
		return newFlag;
	}
	public AnimFlag<Quat> deepCopy(){
		return new QuatAnimFlag(this);
	}

	public Quat cloneValue(Object value) {
		if(value instanceof Quat){
			return new Quat((Quat) value);
		}
		return null;
	}

	@Override
	public MdlxFloatArrayTimeline toMdlx(final TimelineContainer container) {
		final MdlxFloatArrayTimeline mdlxTimeline = new MdlxFloatArrayTimeline(4);

		mdlxTimeline.name = FlagUtils.getWar3ID(name, container);
		mdlxTimeline.interpolationType = interpolationType;
		mdlxTimeline.globalSequenceId = getGlobalSeqId();

		final long[] tempFrames = new long[entryMap.size()];
		final float[][] tempValues = new float[entryMap.size()][];
		final float[][] tempInTans = new float[entryMap.size()][];
		final float[][] tempOutTans = new float[entryMap.size()][];

		boolean hasTangents = mdlxTimeline.interpolationType.tangential();


		for (int i = 0, l = entryMap.size(); i < l; i++) {
			tempFrames[i] = getTimeFromIndex(i);
			tempValues[i] = getValueFromIndex(i).toFloatArray();

			if (hasTangents) {
				tempInTans[i] = getInTanFromIndex(i).toFloatArray();
				tempOutTans[i] = getOutTanFromIndex(i).toFloatArray();
			} else {
				tempInTans[i] = (new Quat()).toFloatArray();
				tempOutTans[i] = (new Quat()).toFloatArray();
			}
		}

		mdlxTimeline.frames = tempFrames;
		mdlxTimeline.values = tempValues;
		mdlxTimeline.inTans = tempInTans;
		mdlxTimeline.outTans = tempOutTans;

		return mdlxTimeline;
	}

	protected Quat getIdentity(int typeId) {
		return (Quat) identity(typeId);
	}

	@Override
	public Quat getInterpolatedValue(Integer floorTime, Integer ceilTime, float timeFactor) {
		Entry<Quat> entryFloor = entryMap.get(floorTime);
		Entry<Quat> entryCeil = entryMap.get(ceilTime);

		return getInterpolatedValue(entryFloor, entryCeil, timeFactor);
	}

	@Override
	public Quat getInterpolatedValue(Entry<Quat> entryFloor, Entry<Quat> entryCeil, float timeFactor) {
		Quat floorValue = entryFloor.getValue();
		Quat floorOutTan = entryFloor.getOutTan();

		Quat ceilValue = entryCeil.getValue();
		Quat ceilInTan = entryCeil.getInTan();

//		System.out.println(typeid);
		if (typeid == ROTATION) {
			return switch (interpolationType) {
				case BEZIER -> Quat.getSquad(floorValue, ceilValue, floorOutTan, ceilInTan, timeFactor);
				case DONT_INTERP -> floorValue;
				case HERMITE -> Quat.getSquad(floorValue, ceilValue, floorOutTan, ceilInTan, timeFactor);
				case LINEAR -> Quat.getSlerped(floorValue, ceilValue, timeFactor);
			};
		}
		throw new IllegalStateException();
	}
}
