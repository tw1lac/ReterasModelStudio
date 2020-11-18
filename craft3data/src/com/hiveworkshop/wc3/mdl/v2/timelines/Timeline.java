package com.hiveworkshop.wc3.mdl.v2.timelines;

import java.util.SortedMap;

public interface Timeline<KEY_TYPE> {
	InterpolationType getInterpolationType();

	SortedMap<Integer, KEY_TYPE> getTimeToKey();
}
