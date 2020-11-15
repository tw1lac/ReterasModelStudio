package com.hiveworkshop.wc3.gui;

import javax.swing.ImageIcon;

public class GlobalIcons {

	private static ImageIcon createIcon(String s) {
		return new ImageIcon(GlobalIcons.class.getResource(s));
	}
	public static final ImageIcon MDL_ICON = createIcon("ImageBin/MDLIcon_16.png");
	public static final ImageIcon ANIM_ICON = createIcon("ImageBin/Anim.png");
	public static final ImageIcon ANIM_ICON_SMALL = createIcon("ImageBin/anim_small.png");
	public static final ImageIcon BONE_ICON = createIcon("ImageBin/Bone_small.png");
	public static final ImageIcon GEO_ICON = createIcon("ImageBin/geo_small.png");
	public static final ImageIcon GEO_ICON_BIG = createIcon("ImageBin/Geo.png");
	public static final ImageIcon GREEN_ICON = createIcon("ImageBin/Blank_small.png");
	public static final ImageIcon RED_ICON = createIcon("ImageBin/BlankRed_small.png");
	public static final ImageIcon ORANGE_ICON = createIcon("ImageBin/BlankOrange_small.png");
	public static final ImageIcon CYAN_ICON = createIcon("ImageBin/BlankCyan_small.png");
	public static final ImageIcon RED_X_ICON = createIcon("ImageBin/redX.png");
	public static final ImageIcon GREEN_ARROW_ICON = createIcon("ImageBin/greenArrow.png");
	public static final ImageIcon MOVE_UP_ICON = createIcon("ImageBin/moveUp.png");
	public static final ImageIcon MOVE_DOWN_ICON = createIcon("ImageBin/moveDown.png");
	public static final ImageIcon SET_KEYFRAME_ICON = createIcon("ImageBin/setkey.png");
	public static final ImageIcon SET_TIME_BOUNDS_ICON = createIcon("ImageBin/setbounds.png");
	public static final ImageIcon UV_MAP = createIcon("ImageBin/UVMap.png");
	public static final ImageIcon PLAY = createIcon("ImageBin/btn_play.png");
	public static final ImageIcon PAUSE = createIcon("ImageBin/btn_pause.png");
	public static final ImageIcon PLUS = createIcon("ImageBin/Plus.png");
	public static final ImageIcon MINUS = createIcon("ImageBin/Minus.png");
	public static final ImageIcon ARROW_UP = createIcon("ImageBin/ArrowUp.png");
	public static final ImageIcon ARROW_DOWN = createIcon("ImageBin/ArrowDown.png");
	public static final ImageIcon ARROW_LEFT = createIcon("ImageBin/ArrowLeft.png");
	public static final ImageIcon ARROW_RIGHT = createIcon("ImageBin/ArrowRight.png");
}
