package com.zenyte.game.util;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

/**
 * @author Kris | 28. aug 2018 : 00:30:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public enum Currency {

	THOUSAND(1_000),
	MILLION(1_000_000),
	BILLION(1_000_000_000);

	private int value;

	public int get(final int amount) {
		return amount * value;
	}

	public static final int get(@NonNull final String amount) {
		val builder = new StringBuilder(10);
		for (val c : amount.toCharArray()) {
			if (c == 'k' || c == 'K') {
				builder.append("000");
			} else if (c == 'm' || c == 'M') {
				builder.append("000000");
			} else if (c == 'b' || c == 'B') {
				builder.append("000000000");
			} else if (c >= 30 && c <= 39) {
				builder.append(c);
			}
		}
		return Integer.valueOf(builder.toString());
	}

}
