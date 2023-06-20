package com.zenyte.game.world.entity.npc.drop;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 19. dets 2017 : 14:27.42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class NPCDrop {

	@Getter @Setter private int itemId;
	@Getter @Setter private int minAmount;
	@Getter @Setter private int maxAmount;
	@Getter @Setter private float chance;
	
}
