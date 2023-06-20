package com.zenyte.game.util;

import java.awt.*;

/**
 * @author Kris | 7. mai 2018 : 14:44:02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum Colour {

	RED("FF0000"),
	GREEN("00FF00"),
	BLUE("0000FF"),
	DARK_BLUE("00080"),
	RS_PINK("FC02E7"),
	RS_PURPLE("93069E"),
	RS_GREEN("006000"),
	TURQOISE("006d62"),
	RS_RED("600000"),
	WHITE("FFFFFF"),
	MAROON("800000"),
	BRICK("B22222"),
	YELLOW("ffff00"),
	GREY("808080"),
	ORANGE("ffa500");

	public static final String END = "</col>";
	
	private final String hex;
	
	Colour(final String hex) {
		this.hex = "<col=" + hex + ">";
	}

	@Override
	public String toString() {
		return hex;
	}

	public String wrap(final String input) {
		return hex + input + END;
	}

	public String wrap(final int i) {
		return wrap(String.valueOf(i));
	}

	public static final Color getColour(final int color) {
		float r = ((color >> 16) & 0xff) / 255.0f;
		float g = ((color >> 8) & 0xff) / 255.0f;
		float b = ((color) & 0xff) / 255.0f;
		float a = 0;//((color >> 24) & 0xff) / 255.0f;
		return new Color(r, g, b, a);
	}

}
