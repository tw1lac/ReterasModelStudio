package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;

import java.util.List;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class FloatAnimFlag extends AnimFlag<Float> {


	public FloatAnimFlag(MdlxTimeline<Float[]> timeline) {
		super(timeline);
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

	@Override
	public MdlxTimeline<Float[]> toMdlx(TimelineContainer container) {
		return null;
	}
}
