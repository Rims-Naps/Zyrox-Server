package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.Ordinal;
import it.unimi.dsi.fastutil.ints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;
import java.util.function.Function;

import static com.zenyte.game.item.ItemId.*;

/**
 * @author Kris | 07/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Ordinal
@AllArgsConstructor
public enum ClueItem {
    BEGINNER(CLUE_SCROLL_BEGINNER, REWARD_CASKET_BEGINNER, SCROLL_BOX_BEGINNER, CLUE_BOTTLE_BEGINNER, CLUE_GEODE_BEGINNER, CLUE_NEST_BEGINNER, ClueLevel.BEGINNER, 4, 103),
    EASY(CLUE_SCROLL_EASY, REWARD_CASKET_EASY, SCROLL_BOX_EASY, CLUE_BOTTLE_EASY, CLUE_GEODE_EASY, CLUE_NEST_EASY, ClueLevel.EASY, 4, 22305),
    MEDIUM(CLUE_SCROLL_MEDIUM, REWARD_CASKET_MEDIUM, SCROLL_BOX_MEDIUM, CLUE_BOTTLE_MEDIUM, CLUE_GEODE_MEDIUM, CLUE_NEST_MEDIUM, ClueLevel.MEDIUM, 3, -32320),
    HARD(CLUE_SCROLL_HARD, REWARD_CASKET_HARD, SCROLL_BOX_HARD, CLUE_BOTTLE_HARD, CLUE_GEODE_HARD, CLUE_NEST_HARD,  ClueLevel.HARD, 2, -13641),
    ELITE(CLUE_SCROLL_ELITE, REWARD_CASKET_ELITE, SCROLL_BOX_ELITE, CLUE_BOTTLE_ELITE, CLUE_GEODE_ELITE, CLUE_NEST_ELITE, ClueLevel.ELITE, 1, 9156),
    MASTER(CLUE_SCROLL_MASTER, REWARD_CASKET_MASTER, SCROLL_BOX_MASTER, -1, -1, -1, ClueLevel.MASTER, 0, 821);
    private final int clue, casket, scrollBox, clueBottle, clueGeode, clueNest;
    private final ClueLevel level;
    private final int skillingChance, replacementColour;
    private static final ClueItem[] values = values();
    @Getter private static final Int2ObjectMap<ClueItem> map =
            Int2ObjectMaps.unmodifiable((Int2ObjectMap<ClueItem>) Utils.populateMap(values, new Int2ObjectOpenHashMap<>(), ClueItem::getClue, ClueItem::getCasket, ClueItem::getScrollBox,
                    ClueItem::getClueBottle, ClueItem::getClueGeode, ClueItem::getClueNest));
    @Getter private static final int[] cluesArray = new IntLinkedOpenHashSet(new IntArrayList(map.int2ObjectEntrySet().stream().mapToInt(entry -> entry.getValue().getClue()).toArray())).toIntArray();
    @Getter private static final int[] boxesArray = new IntLinkedOpenHashSet(new IntArrayList(map.int2ObjectEntrySet().stream().mapToInt(entry -> entry.getValue().getScrollBox()).toArray())).toIntArray();

    public static final void roll(@NotNull final Player player, final int roll, final int level, @NotNull final Function<ClueItem, Integer> fun) {
        if (roll < 0) {
            return;
        }
        val rate = (int) (1D / ((100D + level) / (double) roll));
        if (Utils.random(rate - 1) == 0) {
            var typeRand = Utils.random(13);
            for (val item : values) {
                if ((typeRand -= item.skillingChance) < 0) {
                    if (item == MASTER) {
                        throw new IllegalStateException();
                    }
                    val itemId = fun.apply(item);
                    if (itemId == -1) {
                        throw new IllegalStateException();
                    }
                    val skillingItem = new Item(itemId);
                    player.getInventory().addOrDrop(skillingItem);
                    val name = skillingItem.getName().toLowerCase();
                    val itemTypeName = name.substring(0, name.indexOf(" ("));
                    val clueTypeName = name.substring(name.indexOf(" (") + 2, name.length() - 1);
                    val prefix = Utils.getAOrAn(clueTypeName);
                    player.sendMessage(Colour.RED.wrap("You find " + prefix + " " + clueTypeName + " " + itemTypeName + "!"));
                    return;
                }
            }
            throw new IllegalStateException();
        }
    }

    public static final OptionalInt pseudoRandomNestConstant() {
        var typeRand = Utils.random(13);
        for (val item : values) {
            if ((typeRand -= item.skillingChance) < 0) {
                return OptionalInt.of(item.clueNest);
            }
        }
        return OptionalInt.empty();
    }

}