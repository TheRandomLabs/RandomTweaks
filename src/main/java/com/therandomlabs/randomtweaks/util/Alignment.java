package com.therandomlabs.randomtweaks.util;

import com.therandomlabs.randomtweaks.common.RTLanguageMap;

//Taken and adapted from:
//https://github.com/Lunatrius/InGame-Info-XML/blob/master/src/main/java/com/github/lunatrius/
//ingameinfo/Alignment.java
//License: https://github.com/Lunatrius/InGame-Info-XML/blob/master/LICENSE
public enum Alignment {
	TOPLEFT("topLeft", 2, 2),
	TOPCENTER("topCenter", 0, 2),
	TOPRIGHT("topRight", -2, 2),
	MIDDLELEFT("middleLeft", 2, 0),
	MIDDLECENTER("middleCenter", 0, 0),
	MIDDLERIGHT("middleRight", -2, 0),
	BOTTOMLEFT("bottomLeft", 2, -2),
	BOTTOMCENTER("bottomCenter", 0, -45),
	BOTTOMRIGHT("bottomRight", -2, -2);

	private static final int MASK_X = 0x0C;
	private static final int MASK_Y = 0x03;

	private static final int TOP = 0x01;
	private static final int MIDDLE = 0x03;
	private static final int BOTTOM = 0x02;

	private static final int LEFT = 0x04;
	private static final int CENTER = 0x0C;
	private static final int RIGHT = 0x08;

	private final int defaultX;
	private final int defaultY;

	private final String translationKey;

	private int alignment;

	static {
		TOPLEFT.alignment = TOP | LEFT;
		TOPCENTER.alignment = TOP | CENTER;
		TOPRIGHT.alignment = TOP | RIGHT;
		MIDDLELEFT.alignment = MIDDLE | LEFT;
		MIDDLECENTER.alignment = MIDDLE | CENTER;
		MIDDLERIGHT.alignment = MIDDLE | RIGHT;
		BOTTOMLEFT.alignment = BOTTOM | LEFT;
		BOTTOMCENTER.alignment = BOTTOM | CENTER;
		BOTTOMRIGHT.alignment = BOTTOM | RIGHT;
	}

	Alignment(String translationKey, int x, int y) {
		this.translationKey = "randomtweaks.config.timeOfDay.alignment." + translationKey;
		defaultX = x;
		defaultY = y;
	}

	@Override
	public String toString() {
		return translationKey;
	}

	@SuppressWarnings("Duplicates")
	public int getX(int x, int screenWidth, int textWidth) {
		x += defaultX;

		switch(alignment & MASK_X) {
		case LEFT:
			return x;
		case CENTER:
			return x + (screenWidth - textWidth) / 2;
		case RIGHT:
			return x + screenWidth - textWidth;
		}

		return 0;
	}

	@SuppressWarnings("Duplicates")
	public int getY(int y, int screenHeight, int textHeight) {
		y += defaultY;

		switch(alignment & MASK_Y) {
		case TOP:
			return y;
		case MIDDLE:
			return y + (screenHeight - textHeight) / 2;
		case BOTTOM:
			return y + screenHeight - textHeight;
		}

		return 0;
	}

	public int getDefaultX() {
		return defaultX;
	}

	public int getDefaultY() {
		return defaultY;
	}
}
