package com.zenyte.game.content.minigame.tithefarm;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.val;
import mgi.types.config.enums.Enums;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmRewardsInterface extends Interface {

    private static final int SCORE_VARBIT = 4893;
    private static final int BLESSING_VARBIT = 5370;
    private static final int AUTOWEED_VARBIT = 5557;

    private final static String BUY_FAILURE = "You do not have enough points to buy this reward!";

    private final static Item GRICOLLERS_CAN = new Item(ItemId.GRICOLLERS_CAN, 1);
    private final static Item SEED_BOX = new Item(ItemId.SEED_BOX, 1);

    private final static Item MALE_FARMERS_HAT = new Item(13646, 1);
    private final static Item FEMALE_FARMERS_HAT = new Item(13647, 1);

    private final static Item MALE_FARMERS_TORSO = new Item(13642, 1);
    private final static Item FEMALE_FARMERS_TORSO = new Item(13643, 1);

    private final static Item MALE_FARMERS_LEGS = new Item(13640, 1);
    private final static Item FEMALE_FARMERS_LEGS = new Item(13641, 1);

    private final static Item MALE_FARMERS_BOOTS = new Item(13644, 1);
    private final static Item FEMALE_FARMERS_BOOTS = new Item(13645, 1);

    @Override
    protected void attach() {
        put(8, "Inner container");
        put(4, "Gricoller's can");
        put(10, "Seedbox");
        put(14, "Auto-weed");

        put(21, "Farmer's hat");
        put(25, "Farmer's torso");
        put(29, "Farmer's legs");
        put(33, "Farmer's boots");
    }

    @Override
    protected void build() {
        bind("Gricoller's can", (player, slotId, itemId, option) -> assessClick(player, GRICOLLERS_CAN, 200, option));
        bind("Seedbox", (player, slotId, itemId, option) -> assessClick(player, SEED_BOX, 250, option));

        bind("Farmer's hat", (player, slotId, itemId, option) -> assessClick(player,
                player.getAppearance().getGender().toLowerCase().contains("woman") ? FEMALE_FARMERS_HAT : MALE_FARMERS_HAT, 75, option));

        bind("Farmer's torso", (player, slotId, itemId, option) -> assessClick(player,
                player.getAppearance().getGender().toLowerCase().contains("woman") ? FEMALE_FARMERS_TORSO : MALE_FARMERS_TORSO, 150, option));

        bind("Farmer's legs", (player, slotId, itemId, option) -> assessClick(player,
                player.getAppearance().getGender().toLowerCase().contains("woman") ? FEMALE_FARMERS_LEGS : MALE_FARMERS_LEGS, 125, option));

        bind("Farmer's boots", (player, slotId, itemId, option) -> assessClick(player,
                player.getAppearance().getGender().toLowerCase().contains("woman") ? FEMALE_FARMERS_BOOTS : MALE_FARMERS_BOOTS, 50, option));

        bind("Inner container", (player, slotId, itemId, option) -> {

            if(option == 10) {
                examineItem(player, new Item(itemId, 1));
                return;
            }

            if(option == 6) {
                player.sendInputInt("How many would you like to purchase?", value -> {
                    if(value < 0) {
                        player.sendMessage("Please enter a number higher than 0!");
                        return;
                    }

                    conventionalBuyItem(player, new Item(itemId, 1), option, value);
                });
                return;
            }

            conventionalBuyItem(player, new Item(itemId, 1), option, 0);
        });
        // currently disabled auto-weed reward, needs custom method impl
        bind("Auto-weed", (player, slotId, itemId, option) -> {
            if(option == 6) {
                // if the player has auto-weed unlocked
                if (player.getBooleanAttribute("autoweed")) {
                    player.putBooleanAttribute("autoweed_enabled", !player.getBooleanAttribute("autoweed_enabled"));
                } else {
                    // purchasing auto-weed, currently disabled.
                    player.sendMessage("Autoweed is currently disabled and will be available upon release.");
                    return;
                }

                process(player);
                return;
            }

            if(option == 10) {
                player.sendMessage("When enabled, weeds no longer grow in your Farming patches. This doesn't remove weeds that have already grown there, but it stops them growing back.");
                player.sendMessage("Once purchased, the ability can be turned on and off freely.");
                return;
            }
        });
    }

    private void conventionalBuyItem(final Player player, final Item reward, final int option, int buyAmount) {
        val score = player.getNumericAttribute("tithe_farm_points").intValue();
        val rewardEntry = TitheFarmReward.REWARD_MAP.get(reward.getId());

        if(rewardEntry == null) {
            player.sendMessage("Invalid item purchase, please report this to an administrator!");
            return;
        }

        val cost = rewardEntry.getCost();

        // this is the "value" option
        if(option == 1) {
            val name = reward.getId() == ItemId.BOLOGAS_BLESSING ? "10x " + reward.getName() : reward.getName();
            player.sendMessage(name+ " costs " + cost + " Tithe Farm point" + (cost > 1 ? "s" : "") + ".");
            return;
        }

        switch(option) {
            case 2:
                buyAmount = 1;
                break;
            case 3:
                buyAmount = 5;
                break;
            case 4:
                buyAmount = 10;
                break;
            case 5:
                buyAmount = 50;
                break;
            default:
                break;
        }

        if(score < (cost * buyAmount)) {
            player.sendMessage("You don't have enough Tithe Farm points to buy this!");
            return;
        }

        // if player doesnt have the required level for bologas blessings.
        if(reward.getId() == ItemId.BOLOGAS_BLESSING && (player.getSkills().getLevel(Skills.PRAYER) < 50 || player.getSkills().getLevel(Skills.COOKING) < 65)) {
            player.sendMessage("You need 50 Prayer and 65 Cooking to use Bologa's blessings.");
            return;
        }

        // player doesnt have enough room for this transaction
        if(!player.getInventory().hasFreeSlots() || player.getInventory().getFreeSlots() < buyAmount && !reward.isStackable()) {
            player.sendMessage("You don't have enough free space to make this purchase.");
            return;
        }

        // remove amt of points = cost * buyAmount from total score
        player.addAttribute("tithe_farm_points", score - (cost * buyAmount));
        player.getInventory().addItem(reward.getId(), reward.getId() == ItemId.BOLOGAS_BLESSING ? buyAmount * 10 : buyAmount);
        this.close(player); //should work
    }

    private void assessClick(final Player player, final Item reward, final int cost, final int option) {
        switch(option) {
            case 10:
                examineItem(player, reward);
                break;
            case 6:
                attemptPurchase(player, reward, cost);
                break;
        }
    }

    private void attemptPurchase(final Player player, final Item reward, final int cost) {
        val score = player.getNumericAttribute("tithe_farm_points").intValue();
        if(score < cost) {
            player.sendMessage(BUY_FAILURE);
            return;
        }

        if(!player.getInventory().hasFreeSlots()) {
            player.sendMessage("You don't have enough inventory slots to buy anything!");
            return;
        }

        if(player.containsItem(reward)) {
            player.sendMessage("You already own this reward!");
            return;
        }

        player.addAttribute("tithe_farm_points", score - cost);
        player.getInventory().addItem(reward);
        process(player);

    }

    private void examineItem(final Player player, final Item reward) {
        player.sendMessage(reward.getDefinitions().getExamine());
    }

    private void process(final Player player) {
        val score = player.getNumericAttribute("tithe_farm_points").intValue();

        if(score >= 0) {
            player.getVarManager().sendBit(SCORE_VARBIT, score);
        }

        if(player.getBooleanAttribute("autoweed")) {
            player.getVarManager().sendBit(AUTOWEED_VARBIT, player.getBooleanAttribute("autoweed_enabled") ? 2 : 1);
        }
    }

    @Override
    public void open(final Player player) {
        // set access masks for buying items.
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Inner container"), 0, Enums.GRICOLLERS_REWARDS.getSize(),
                AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP5, AccessMask.CLICK_OP6, AccessMask.CLICK_OP10);

        // enable the BOLOGA BLESSING sales by default
        player.getVarManager().sendBit(BLESSING_VARBIT, 2);

        if(player.getBooleanAttribute("autoweed")) {
            player.getVarManager().sendBit(AUTOWEED_VARBIT, player.getBooleanAttribute("autoweed_enabled") ? 2 : 1);
        }

        player.getInterfaceHandler().sendInterface(this);
        this.process(player);
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TITHE_FARM_REWARDS;
    }
}
