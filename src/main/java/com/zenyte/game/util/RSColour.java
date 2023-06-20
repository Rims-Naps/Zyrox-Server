package com.zenyte.game.util;

import lombok.Getter;

/**
 * @author Kris | 4. juuli 2018 : 23:52:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class RSColour {

	@Getter private final int red, green, blue;
	
	public RSColour(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public RSColour(final int rgb) {
		red = rgb >> 10 & 31;
		green = rgb & 31;
		blue = rgb >> 5 & 31;
	}
	
	public int getRGB() {
		return red << 10 | green << 5 | blue;
	}
	
	@Override
	public String toString() {
		return red + ", " + green + ", " + blue;
	}
	
}
