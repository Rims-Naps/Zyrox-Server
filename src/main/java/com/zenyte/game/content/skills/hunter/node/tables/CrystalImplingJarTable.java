package com.zenyte.game.content.skills.hunter.node.tables;

import com.zenyte.game.content.drops.table.DropTable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import mgi.types.config.items.ItemDefinitions;

/**
 * @author Corey
 * @see <a href=https://oldschool.runescape.wiki/w/Dragon_impling_jar>Dragon impling jar</a>
 * @since 11/01/2019
 */
public class CrystalImplingJarTable {
    
    public static final DropTable table = new DropTable();
    
    static {
        table.append(ItemId.RANARR_SEED, 1, 3, 8)
                .append(ItemId.DRAGON_DART_TIP, 1, 10, 15)
                .append(ItemId.CRYSTAL_SHARD, 1, 100, 125)
                .append(ItemId.YEW_SEED, 1)
                .append(ItemId.DRAGONSTONE_AMULET, 1)
                .append(ItemDefinitions.getOrThrow(ItemId.BABY_DRAGON_BONE).getNotedId(), 1, 75, 125)
                .append(ItemDefinitions.getOrThrow(ItemId.RUNE_SCIMITAR).getNotedId(), 1, 3,6)
                .append(ItemDefinitions.getOrThrow(ItemId.DRAGON_DAGGER).getNotedId(), 1, 2)
                .append(ItemId.ONYX_BOLT_TIPS, 1, 6, 10)
                .append(ItemId.RUBY_BOLT_TIPS, 1, 50, 125)
                .append(ItemId.RUNE_DART, 1, 50, 100)
                .append(ItemId.RUNE_DART_TIP, 1, 25, 75)
                .append(ItemId.RUNE_JAVELIN_HEADS, 1, 20, 60)
                .append(ItemDefinitions.getOrThrow(ItemId.DRAGONSTONE).getNotedId(), 1, 2)
                .append(ItemId.RUNE_ARROWTIPS, 1, 150, 250)
                .append(ItemId.RUNE_ARROW, 1, 400, 750)
                .append(ItemDefinitions.getOrThrow(ItemId.AMULET_OF_POWER).getNotedId(), 1, 5, 7)
                .append(32309,1,1,1);

    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
