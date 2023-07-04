package com.zenyte.game.ui.testinterfaces;

import com.google.common.base.Preconditions;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.bountyhunter.BountyHunterVar;
import com.zenyte.game.content.consumables.Consumable;
import com.zenyte.game.content.consumables.Drinkable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.LootingBag;
import com.zenyte.plugins.item.TomeOfFire;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import mgi.types.config.enums.Enums;
import mgi.types.config.items.ItemDefinitions;

import java.util.Optional;

import static com.zenyte.game.util.AccessMask.*;

/**
 * @author Kris | 07/05/2019 01:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BountyHunterStoreInterface extends Interface {

    @AllArgsConstructor
    @Getter
    public enum Reward {
        DRAGON_LONGSWORD(1305, 300_000),
        DRAGON_BATTLEAXE(1377, 400_000),
        DRAGON_MACE(1434, 150_000),
        DRAGON_HALBERD(3204, 900_000),
        HELM_OF_NEITIZNOT(10828, 150_000),
        BERSERKER_HELM(3751, 234_000),
        WARRIOR_HELM(3753, 234_000),
        ARCHER_HELM(3749, 234_000),
        FARSEER_HELM(3755, 234_000),
        GREEN_DARK_BOW_PAINT(12759, 500_000),
        YELLOW_DARK_BOW_PAINT(12761, 500_000),
        WHITE_DARK_BOW_PAINT(12763, 500_000),
        BLUE_DARK_BOW_PAINT(12757, 500_000),
        PADDEWWA_TELEPORT(12781, 10_000),
        SENNTISTEN_TELEPORT(12782, 10_000),
        ANNAKARL_TELEPORT(12775, 10_000),
        CARRALLANGAR_TELEPORT(12776, 10_000),
        DAREEYAK_TELEPORT(12777, 10_000),
        GHORROCK_TELEPORT(12778, 10_000),
        KHARYRLL_TELEPORT(12779, 10_000),
        LASSAR_TELEPORT(12780, 10_000),
        VOLCANIC_WHIP_MIX(12771, 500_000),
        FROZEN_WHIP_MIX(12769, 500_000),
        STEAM_STAFF_UPGRADE_KIT(12798, 250_000),
        LAVA_STAFF_UPGRADE_KIT(21202, 250_000),
        DRAGON_PICKAXE_UPGRADE_KIT(12800, 300_000),
        WARD_UPGRADE_KIT(12802, 350_000),
        RING_OF_WEALTH_SCROLL(12783, 50_000),
        MAGIC_SHORTBOW_SCROLL(12786, 100_000),
        SARADOMINS_TEAR(12804, 15_000_000),
        RUNE_POUCH(12791, 1_200_000),
        LOOTING_BAG(11941, 10_000),
        BOLT_RACK(4740, 360),
        RUNE_ARROW(892, 600),
        ADAMANT_ARROW(890, 240),
        GRANITE_CLAMP(12849, 250_000),
        HUNTERS_HONOUR(12855, 2_500_000),
        REVENANT_CAVE_TELEPORT(21802, 75_000),
        BURNING_AMULET(21166, 10_000),
        ROYAL_SEED_POD(19564, 30_000),
        SUPER_ATTACK(2436, 7_500),
        SUPER_STRENGTH(2440, 8_500),
        SUPER_DEFENCE(2442, 6_000),
        RANGING_POTION(2444, 7_500),
        MAGIC_POTION(3040, 5_000),
        SUPER_COMBAT_POTION(12695, 25_000),
        SUPER_RESTORE(3024, 12_500),
        SANFEW_SERUM(10925, 15_000),
        PRAYER_POTION(2434, 10_000),
        SARADOMIN_BREW(6685, 13_500),
        STAMINA_POTION(12625, 17_500),
        ANTI_VENOM_PLUS(12913, 15_000),
        BASTION_POTION(22461, 18_000),
        FIGHTER_TORSO(10551, 600_000),
        MASTER_WAND(6914, 15_000_000),
        DRAGON_CROSSBOW(21902,10_000_000),
        DRAGON_THROWNAXE(20849, 5_500),
        DRAGON_KNIFE(22804, 3_000),
        DRAGON_BOLT(21905, 5_000),
        ANCIENT_MACE(11061, 550_000),
        DECORATIVE_RANGE_TOP(11899, 150_000),
        DECORATIVE_RANGE_BOTTOM(11900, 150_000),
        DECORATIVE_MAGE_TOP(11896, 150_000),
        DECORATIVE_MAGE_BOTTOM(11897, 150_000),
        SARADOMIN_HALO(12637, 300_000),
        ZAMORAK_HALO(12638, 300_000),
        GUTHIX_HALO(12639, 300_000),
        TOME_OF_FIRE(TomeOfFire.TOME_OF_FIRE_EMPTY, 15_000_000),
        CRYSTAL_SEED(4207, 540_000),
        HEAVY_BALLISTA(19481,13_000_000),
        DRAGON_JAVELIN(19484,14_500);


        private static final Reward[] values = values();
        private static final Int2ObjectMap<Reward> map = new Int2ObjectOpenHashMap<>();

        static {
            for (val value : values) {
                map.put(value.id, value);
            }
        }

        private static final Optional<Reward> get(final int id) {
            return Optional.ofNullable(map.get(id));
        }

        private final int id, cost;
    }

    @Override
    protected void attach() {
        put(2, "Item layer");
        put(3, "Scrollbar");
    }

    @Override
    public void open(final Player player) {
        val dispatcher = player.getPacketDispatcher();
        val varManager = player.getVarManager();
        val bounty = player.getBountyHunter();
        val layerEnum = Enums.BOUNTY_HUNTER_REWARDS;
        player.getInterfaceHandler().sendInterface(this);
        varManager.sendVarInstant(1137, bounty.getValue(BountyHunterVar.CURRENT_HUNTER_KILLS));
        varManager.sendVarInstant(1138, bounty.getValue(BountyHunterVar.CURRENT_ROGUE_KILLS));
        dispatcher.sendClientScript(23, id << 16 | getComponent("Item layer"), id << 16 | getComponent("Scrollbar"), layerEnum.getId());
        dispatcher.sendComponentSettings(id, getComponent("Item layer"), 0, layerEnum.getSize(), CLICK_OP1, CLICK_OP2,
                CLICK_OP3, CLICK_OP4, CLICK_OP5, CLICK_OP10);
    }

    @Override
    protected void build() {
        bind("Item layer", (player, slotId, itemId, optionId) -> {
            val layerEnum = Enums.BOUNTY_HUNTER_REWARDS;
            val optionalItem = layerEnum.getValue(slotId);
            if (!optionalItem.isPresent()) {
                return;
            }
            int item = optionalItem.getAsInt();
            val optionalReward = Reward.get(item);
            if (!optionalReward.isPresent()) {
                return;
            }
            if (Consumable.consumables.get(item) instanceof Drinkable && player.getBooleanAttribute("notedBH")) {
                item = new Item(item).toNote().getId();
            }
            val reward = optionalReward.get();
            if (reward == Reward.HUNTERS_HONOUR && player.getVarManager().getValue(1137) > player.getVarManager().getValue(1138)) {
                item = 12856;
            }
            val option = Option.get(optionId - 1);
            if (option == Option.EXAMINE) {
                Examine.sendItemExamine(player, item);
                return;
            }
            if (option == Option.VALUE) {
                val itemDefinitions = ItemDefinitions.getOrThrow(item);
                player.sendMessage(itemDefinitions.getName() + ": currently costs " + Utils.format(reward.cost) + " Bounty Hunter points.");
                return;
            }
            int amount = option.amount;
            val points = player.getBountyHunter().getPoints();
            val cost = reward.cost;
            if (amount > (points / cost)) {
                amount = points / cost;
            }
            val inventory = player.getInventory().getContainer();
            var freeSlots = inventory.getFreeSlotsSize();

            val affordableAmount = amount;

            val inInventory = inventory.getAmountOf(item);

            if (amount + inInventory < 0) {
                amount = Integer.MAX_VALUE - inInventory;
            }

            val definitions = ItemDefinitions.getOrThrow(item);
            if (definitions.isStackable()) {
                if (freeSlots == 0 && inInventory == 0) {
                    amount = 0;
                }
            } else {
                amount = Math.min(freeSlots, amount);
            }

            final Optional<String> message = affordableAmount != option.amount ? Optional.of("You don't have enough Bounty Hunter points.")
                    : amount != affordableAmount ? Optional.of("Not enough space in your inventory.") : Optional.empty();
            message.ifPresent(mes -> player.sendMessage(mes));
            if (amount <= 0) {
                return;
            }

            if (reward == Reward.LOOTING_BAG) {
                if (LootingBag.hasBag(player)) {
                    player.sendMessage("You can only have one looting bag with you at all times.");
                    return;
                }
                if (amount > 1) {
                    amount = 1;
                    player.sendMessage("You can only have one looting bag with you at all times.");
                }
            } else if (reward == Reward.RUNE_POUCH) {
                if (player.containsItem(ItemId.RUNE_POUCH)) {
                    player.sendMessage("You can only own one rune pouch at a time!");
                    return;
                }
                if (amount > 1) {
                    amount = 1;
                    player.sendMessage("You can only own one rune pouch at a time!");
                }
            }

            player.getBountyHunter().setPoints(points - (cost * amount));
            player.getInventory().addOrDrop(new Item(item, amount));

        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.BOUNTY_HUNTER_STORE;
    }

    @AllArgsConstructor
    private enum Option {
        VALUE(-1),
        BUY_1(1),
        BUY_5(5),
        BUY_10(10),
        BUY_50(50),
        EXAMINE(-1);

        private final int amount;

        private static final Option[] values = values();

        private static final Option get(final int id) {
            if (id == 9) {
                return EXAMINE;
            }
            Preconditions.checkArgument(!(id < 0 || id >= values.length));
            return values[id];
        }
    }
}
