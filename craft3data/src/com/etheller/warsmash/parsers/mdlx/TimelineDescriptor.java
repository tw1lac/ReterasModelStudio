package com.etheller.warsmash.parsers.mdlx;

import com.etheller.warsmash.parsers.mdlx.timeline.FloatArrayTimeline;
import com.etheller.warsmash.parsers.mdlx.timeline.FloatTimeline;
import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.parsers.mdlx.timeline.UInt32Timeline;

public interface TimelineDescriptor {
	Timeline createTimeline();

	TimelineDescriptor UINT32_TIMELINE = UInt32Timeline::new;

	TimelineDescriptor FLOAT_TIMELINE = FloatTimeline::new;

	TimelineDescriptor VECTOR3_TIMELINE = () -> new FloatArrayTimeline(3);

	TimelineDescriptor VECTOR4_TIMELINE = () -> new FloatArrayTimeline(4);
}
