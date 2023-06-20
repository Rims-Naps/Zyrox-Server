package com.zenyte.game.world.entity.npc.drop.matrix;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 05/10/2019 | 19:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public class ItemDrop {

    private final int npcId;
    private final NPCDrops.DisplayedNPCDrop drop;
}
