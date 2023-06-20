package com.zenyte.game.content.skills.thieving.tables;

import com.zenyte.game.content.drops.table.DropTable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import mgi.types.config.items.ItemDefinitions;

/**
 * @author Corey
 * @since 11/01/2020
 */
public class RoguesChestTable {
    
    private static final int COMMON = 6;
    private static final int UNCOMMON = 4;
    private static final int RARE = 1;
    private static final DropTable table = new DropTable();
    
    static {
        table.append(ItemId.COINS_995, COMMON, 8000)
                .append(ItemDefinitions.getOrThrow(ItemId.UNCUT_SAPPHIRE).getNotedId(), COMMON, 6)
                .append(ItemDefinitions.getOrThrow(ItemId.UNCUT_EMERALD).getNotedId(), COMMON, 5)
                .append(ItemDefinitions.getOrThrow(ItemId.UNCUT_RUBY).getNotedId(), UNCOMMON, 4)
                .append(ItemDefinitions.getOrThrow(ItemId.UNCUT_DIAMOND).getNotedId(), UNCOMMON , 4)
                .append(ItemDefinitions.getOrThrow(ItemId.RAW_TUNA).getNotedId(), COMMON, 25)
                .append(ItemDefinitions.getOrThrow(ItemId.RAW_LOBSTER).getNotedId(), COMMON, 25)
                .append(ItemDefinitions.getOrThrow(ItemId.RAW_SWORDFISH).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.RAW_MONKFISH).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.RAW_SHARK).getNotedId(), RARE, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.OAK_PLANK).getNotedId(), COMMON, 25)
                .append(ItemDefinitions.getOrThrow(ItemId.TEAK_PLANK).getNotedId(), UNCOMMON, 25)
                .append(ItemDefinitions.getOrThrow(ItemId.MAHOGANY_PLANK).getNotedId(), RARE, 25)
                .append(ItemDefinitions.getOrThrow(ItemId.MAGIC_LOGS).getNotedId(), RARE, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.MORT_MYRE_FUNGUS).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.RED_SPIDERS_EGGS).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.WHITE_BERRIES).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.SNAPE_GRASS).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.LIMPWURT_ROOT).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.WINE_OF_ZAMORAK).getNotedId(), UNCOMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.JANGERBERRIES).getNotedId(), UNCOMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.POISON_IVY_BERRIES).getNotedId(), UNCOMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.GREEN_DRAGONHIDE).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.BLUE_DRAGONHIDE).getNotedId(), COMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.RED_DRAGONHIDE).getNotedId(), UNCOMMON, 15)
                .append(ItemDefinitions.getOrThrow(ItemId.BLACK_DRAGONHIDE).getNotedId(), UNCOMMON, 15)
                .append(ItemId.MIND_RUNE, COMMON, 25)
                .append(ItemId.CHAOS_RUNE, UNCOMMON, 75)
                .append(ItemId.DEATH_RUNE, UNCOMMON, 75)
                .append(ItemId.BLOOD_RUNE, UNCOMMON, 75)
                .append(ItemDefinitions.getOrThrow(ItemId.DRAGONSTONE).getNotedId(), RARE, 2);
    }
    
    public static Item roll() {
        return table.rollItem();
    }
    
}
