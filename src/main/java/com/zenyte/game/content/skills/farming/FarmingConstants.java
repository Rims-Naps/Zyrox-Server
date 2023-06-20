package com.zenyte.game.content.skills.farming;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;

/**
 * @author Kris | 08/02/2019 17:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FarmingConstants {

    public static final Item GARDENING_TROWEL = new Item(5325), PLANT_CURE = new Item(6036), COMPOST = new Item(6032),
            RAKE = new Item(5341), PLANT_POT = new Item(5350), WATERING_CAN = new Item(5331), SEED_DIBBER = new Item(5343);

    public static final Item SALTPETRE = new Item(13421);

    public static final Item SPADE = new Item(952);

    public static final Item SECATEURS = new Item(5329), MAGIC_SECATEURS = new Item(7409);

    public static final int WEEDS = 6055;
    public static final int VOLCANIC_ASH = 21622;

    /**
     * The time in minutes for fruit to regenerate one after another in fruit trees.
     * For cactus spines: 25 minutes
     * For potato cacti: 1-2 hours to grow the entire plant of 6 products.
     */
    static final int FRUIT_REGENERATION_TIMER = 30;

    /**
     * The multiplier for farming timers, {@code 0.5F} would halve the time it takes for each stage.
     */
    static final float FARMING_TIMER_MULTIPLIER = 0.5F;

    public static boolean hasSecateurs(final Player player) {
        val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        return weapon == MAGIC_SECATEURS.getId() || player.getInventory().containsItem(MAGIC_SECATEURS) || player.getInventory().containsItem(SECATEURS);
    }

}
