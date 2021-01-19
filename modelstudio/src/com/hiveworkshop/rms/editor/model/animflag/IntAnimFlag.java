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
public class IntAnimFlag extends AnimFlag<Integer> {


	public IntAnimFlag(MdlxTimeline<Long[]> timeline) {
		super(timeline);
	}

	public IntAnimFlag(String title, List<Integer> times, List<Integer> values) {
		super(title, times, values);
	}

	public IntAnimFlag(String title) {
		super(title);
	}

	public IntAnimFlag(AnimFlag<Integer> af) {
		super(af);
	}

	@Override
	public MdlxTimeline<Long[]> toMdlx(TimelineContainer container) {
		return null;
	}
}
