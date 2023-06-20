package com.zenyte.game.content.event.christmas2019;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Corey
 * @since 14/12/2019
 */
public class ChristmasUtils {
    
    private static final String IMP_NAME_KEY = "christmas_warble_imp_name";
    private static final String IMP_ID_KEY = "christmas_warble_imp_id";
    
    public static String getImpName(final Player player) {
        return player.getAttributeOrDefault(IMP_NAME_KEY, "Snow Imp");
    }
    
    public static String getFrozenGuestOrder(final Player player) {
        return ChristmasConstants.FROZEN_GUEST_ORDERS[player.getPlayerInformation().getUserIdentifier() % 10];
    }
    
    public static String getImpNpcName(final Player player, final NPC npc) {
        return ChristmasConstants.SNOW_IMP_NAMES[(player.getPlayerInformation().getUserIdentifier() + npc.getIndex()) % 10];
    }
    
    public static void saveImpName(final Player player, final String name) {
        player.addAttribute(IMP_NAME_KEY, name);
    }
    
    public static void saveImpId(final Player player, final int id) {
        player.addAttribute(IMP_ID_KEY, id);
    }
    
    public static PersonalSnowImp.Imp getImp(final Player player) {
        return PersonalSnowImp.Imp.forId(player.getNumericAttribute(IMP_ID_KEY).intValue());
    }
    
    public static boolean wearingGhostCostume(final Player player) {
        return player.getEquipment().containsItems(new Item(ChristmasConstants.GHOST_HOOD_ID),
                new Item(ChristmasConstants.GHOST_TOP_ID),
                new Item(ChristmasConstants.GHOST_BOTTOMS_ID));
    }
    
}
