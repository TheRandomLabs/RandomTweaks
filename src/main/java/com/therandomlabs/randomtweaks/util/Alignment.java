package com.therandomlabs.randomtweaks.util;

//Taken and adapted from:
//https://github.com/Lunatrius/InGame-Info-XML/blob/master/src/main/java/com/github/lunatrius/
//ingameinfo/Alignment.java
//License: https://github.com/Lunatrius/InGame-Info-XML/blob/master/LICENSE
public enum Alignment {
	TOP_LEFT("topLeft", 2, 2),
	TOP_CENTER("topCenter", 0, 2),
	TOP_RIGHT("topRight", -2, 2),
	MIDDLE_LEFT("middleLeft", 2, 0),
	MIDDLE_CENTER("middleCenter", 0, 0),
	MIDDLE_RIGHT("middleRight", -2, 0),
	BOTTOM_LEFT("bottomLeft", 2, -2),
	BOTTOM_CENTER("bottomCenter", 0, -45),
	BOTTOM_RIGHT("bottomRight", -2, -2);

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
		TOP_LEFT.alignment = TOP | LEFT;
		TOP_CENTER.alignment = TOP | CENTER;
		TOP_RIGHT.alignment = TOP | RIGHT;
		MIDDLE_LEFT.alignment = MIDDLE | LEFT;
		MIDDLE_CENTER.alignment = MIDDLE | CENTER;
		MIDDLE_RIGHT.alignment = MIDDLE | RIGHT;
		BOTTOM_LEFT.alignment = BOTTOM | LEFT;
		BOTTOM_CENTER.alignment = BOTTOM | CENTER;
		BOTTOM_RIGHT.alignment = BOTTOM | RIGHT;
	}

	Alignment(String translationKey, int x, int y) {
		this.translationKey = "randomtweaks.config.client.timeOfDay.alignment." + translationKey;
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
