package com.hiveworkshop.wc3.gui;

public enum GUITheme {
	FOREST_GREEN("Forest Green"), DARK("Noire"), WINDOWS("Windows"), WINDOWS_CLASSIC("Windows Classic"),
	SOFT_GRAY("Soft Gray"), JAVA_DEFAULT("Java Default"), BLUE_ICE("Blue Ice"), DARK_BLUE_GREEN("Dark Blue-Green"),
	GRAY("Gray"), HIFI("HiFi"), ACRYL("Acryl"), ALUMINIUM("Aluminium");

	private String displayName;

	GUITheme(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
