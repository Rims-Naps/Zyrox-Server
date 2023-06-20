package com.zenyte.game.content.grandexchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tommeh | 18 sep. 2018 | 17:18:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class ExchangeHistory {
	
	@Getter private int id, quantity, price;
	@Getter private final ExchangeType type;

}
