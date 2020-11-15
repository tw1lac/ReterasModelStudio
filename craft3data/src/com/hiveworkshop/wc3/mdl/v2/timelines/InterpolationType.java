package com.hiveworkshop.wc3.mdl.v2.timelines;

public enum InterpolationType {
	DONT_INTERP("DontInterp"), LINEAR("Linear"), BEZIER("Bezier"), HERMITE("Hermite");

	private String name;

	InterpolationType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
