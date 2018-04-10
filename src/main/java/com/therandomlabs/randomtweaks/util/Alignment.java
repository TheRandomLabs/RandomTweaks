package com.therandomlabs.randomtweaks.util;

//Taken and adapted from:
//https://github.com/Lunatrius/InGame-Info-XML/blob/master/src/main/java/com/github/lunatrius/
//ingameinfo/Alignment.java
//License: https://github.com/Lunatrius/InGame-Info-XML/blob/master/LICENSE
public enum Alignment {
	TOPLEFT(2, 2),
	TOPCENTER(0, 2),
	TOPRIGHT(-2, 2),
	MIDDLELEFT(2, 0),
	MIDDLECENTER(0, 0),
	MIDDLERIGHT(-2, 0),
	BOTTOMLEFT(2, -2),
	BOTTOMCENTER(0, -45),
	BOTTOMRIGHT(-2, -2);

	private static final int MASK_X = 0x0C;
	private static final int MASK_Y = 0x03;

	private static final int TOP = 0x01;
	private static final int MIDDLE = 0x03;
	private static final int BOTTOM = 0x02;

	private static final int LEFT = 0x04;
	private static final int CENTER = 0x0C;
	private static final int RIGHT = 0x08;

	private int alignment;

	private final int defaultX;
	private final int defaultY;

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

	Alignment(int x, int y) {
		defaultX = x;
		defaultY = y;
	}

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
