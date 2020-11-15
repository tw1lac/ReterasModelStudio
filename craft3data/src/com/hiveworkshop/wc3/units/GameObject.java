package com.hiveworkshop.wc3.units;

import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

public interface GameObject {

	void setField(String field, String value);

	void setField(String field, String value, int index);

	String getField(String field);

	String getField(String field, int index);

	int getFieldValue(String field);

	int getFieldValue(String field, int index);

	List<? extends GameObject> getFieldAsList(String field, ObjectData objectData);

	String getId();

	ObjectData getTable();

	String getName();

	Set<String> keySet();

	ImageIcon getScaledIcon(final double amt);

	ImageIcon getScaledTintedIcon(final Color tint, final double amt);

	Image getImage();
}
