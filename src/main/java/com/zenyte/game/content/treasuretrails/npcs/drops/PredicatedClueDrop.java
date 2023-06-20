package com.zenyte.game.content.treasuretrails.npcs.drops;

import lombok.Data;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.function.Predicate;

/**
 * @author Kris | 22/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class PredicatedClueDrop {
    private final double rate;
    private final Predicate<NPCDefinitions> predicate;
}
