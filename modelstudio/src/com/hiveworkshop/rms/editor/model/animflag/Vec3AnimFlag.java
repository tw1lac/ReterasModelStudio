package com.hiveworkshop.rms.editor.model.animflag;

import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;
import com.hiveworkshop.rms.util.Vec3;

import java.util.List;

/**
 * A java class for MDL "motion flags," such as Alpha, Translation, Scaling, or
 * Rotation. AnimFlags are not "real" things from an MDL and are given this name
 * by me, as an invented java class to simplify the programming
 *
 * Eric Theller 11/5/2011
 */
public class Vec3AnimFlag extends AnimFlag2<Vec3> {


	public Vec3AnimFlag(MdlxTimeline<Float[]> timeline) {
		super(timeline);
	}

	public Vec3AnimFlag(String title, List<Integer> times, List<Vec3> values) {
		super(title, times, values);
	}

	public Vec3AnimFlag(String title) {
		super(title);
	}

	public Vec3AnimFlag(AnimFlag2<Vec3> af) {
		super(af);
	}

	@Override
	public MdlxTimeline<Float[]> toMdlx(TimelineContainer container) {
		return null;
	}
}
