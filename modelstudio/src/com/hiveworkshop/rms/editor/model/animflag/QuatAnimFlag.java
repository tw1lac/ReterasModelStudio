package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;
import com.hiveworkshop.rms.util.Quat;

import java.util.List;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class QuatAnimFlag extends AnimFlag2<Quat> {


	public QuatAnimFlag(MdlxTimeline<Float[]> timeline) {
		super(timeline);
	}

	public QuatAnimFlag(String title, List<Integer> times, List<Quat> values) {
		super(title, times, values);
	}

	public QuatAnimFlag(String title) {
		super(title);
	}

	public QuatAnimFlag(AnimFlag2<Quat> af) {
		super(af);
	}

	@Override
	public MdlxTimeline<Float[]> toMdlx(TimelineContainer container) {
		return null;
	}
}
