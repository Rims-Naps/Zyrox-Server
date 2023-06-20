package com.zenyte.game.content.tog;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumSet;

/**
 * @author Chris
 * @since September 07 2020
 */
@RequiredArgsConstructor
@Getter
public enum TearsOfGuthixSkillMessage {
    ATTACK(Skills.ATTACK, "You feel a brief surge of aggression."),
    STRENGTH(Skills.STRENGTH, "Your muscles bulge."),
    DEFENCE(Skills.DEFENCE, "You feel more able to defend yourself."),
    RANGED(Skills.RANGED, "Your aim improves."),
    PRAYER(Skills.PRAYER, "You suddenly feel very close to the gods."),
    MAGIC(Skills.MAGIC, "You feel the power of the runes surging through you."),
    HITPOINTS(Skills.HITPOINTS, "You feel very healthy."),
    AGILITY(Skills.AGILITY, "You feel very nimble."),
    HERBLORE(Skills.HERBLORE, "You gain a deep understanding of all kinds of strange plants."),
    THIEVING(Skills.THIEVING, "You feel your respect for others' property slipping away."),
    CRAFTING(Skills.CRAFTING, "Your fingers feel nimble and suited to delicate work."),
    FLETCHING(Skills.FLETCHING, "You gain a deep understanding of wooden sticks."),
    MINING(Skills.MINING, "You gain a deep understanding of the stones of the earth."),
    SMITHING(Skills.SMITHING, "You gain a deep understanding of all types of metal."),
    FISHING(Skills.FISHING, "You gain a deep understanding of the creatures of the sea."),
    COOKING(Skills.COOKING, "You have a brief urge to cook some food."),
    FIREMAKING(Skills.FIREMAKING, "You have a brief urge to set light to something."),
    WOODCUTTING(Skills.WOODCUTTING, "You gain a deep understanding of the trees in the wood."),
    RUNECRAFT(Skills.RUNECRAFTING, "You gain a deep understanding of runes."),
    SLAYER(Skills.SLAYER, "You gain a deep understanding of many strange creatures."),
    FARMING(Skills.FARMING, "You gain a deep understanding of the cycles of nature."),
    CONSTRUCTION(Skills.CONSTRUCTION, "You feel homesick."),
    HUNTER(Skills.HUNTER, "You briefly experience the joy of the hunt.");
    private static final ImmutableSet<TearsOfGuthixSkillMessage> MESSAGES = Sets.immutableEnumSet(EnumSet.allOf(TearsOfGuthixSkillMessage.class));
    private final int skillId;
    private final String message;

    public static TearsOfGuthixSkillMessage of(final int skillId) {
        for (val message : MESSAGES) {
            if (message.skillId == skillId) {
                return message;
            }
        }
        throw new IllegalArgumentException("Could not find TOG skill message for skill id: " + skillId);
    }
}
