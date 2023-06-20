package com.zenyte.game.world.entity.npc.combatdefs;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

/**
 * @author Kris | 05/11/2018 01:22
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MonsterExamineDefinition {

    @Getter private String name;
    @Getter private Int2ObjectOpenHashMap<String> definitions;



}
