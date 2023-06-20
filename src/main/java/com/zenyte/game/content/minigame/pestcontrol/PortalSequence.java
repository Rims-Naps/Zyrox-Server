package com.zenyte.game.content.minigame.pestcontrol;

import static com.zenyte.game.content.minigame.pestcontrol.PestPortal.EASTERN;
import static com.zenyte.game.content.minigame.pestcontrol.PestPortal.SOUTH_EASTERN;
import static com.zenyte.game.content.minigame.pestcontrol.PestPortal.SOUTH_WESTERN;
import static com.zenyte.game.content.minigame.pestcontrol.PestPortal.WESTERN;

import lombok.Getter;

/**
 * @author Kris | 27. juuni 2018 : 21:13:02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum PortalSequence {

	SEQUENCE_1(EASTERN, SOUTH_WESTERN, SOUTH_EASTERN, WESTERN),
	SEQUENCE_2(EASTERN, WESTERN, SOUTH_WESTERN, SOUTH_EASTERN),
	SEQUENCE_3(WESTERN, EASTERN, SOUTH_EASTERN, SOUTH_WESTERN),
	SEQUENCE_4(WESTERN, SOUTH_EASTERN, EASTERN, SOUTH_WESTERN),
	SEQUENCE_5(SOUTH_EASTERN, SOUTH_WESTERN, WESTERN, EASTERN),
	SEQUENCE_6(SOUTH_EASTERN, WESTERN, SOUTH_WESTERN, EASTERN);
	
	@Getter private final PestPortal[] portals;
	
	public static final PortalSequence[] VALUES = values();
	
	private PortalSequence(final PestPortal... portals) {
		this.portals = portals;
	}
	
}
