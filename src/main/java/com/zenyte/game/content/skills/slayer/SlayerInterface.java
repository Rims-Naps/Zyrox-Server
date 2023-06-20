package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.RequestResult;
import mgi.types.config.enums.Enums;
import mgi.types.config.items.ItemDefinitions;
import com.zenyte.plugins.Plugin;
import com.zenyte.plugins.item.HerbSack;
import lombok.val;

import static com.zenyte.game.content.skills.slayer.Slayer.LUMBRIDGE_ELITE_DIARY_COMPLETED_BIT;

/**
 * @author Kris | 27/03/2019 16:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Plugin
public class SlayerInterface extends Interface {

    private static final int SIZE = Enums.TASK_NAMES_ENUM.getSize();
    private static final int BROAD_BOLTS = 11875;
    private static final int BROAD_ARROWS = 4160;
    private static final int HERB_SACK = 13226;
    private static final int RUNE_POUCH = 12791;
    private static final int FIGHTER_TORSO = 10551;
    private static final int ANCHOR = 10887;

    @Override
    protected void attach() {
        put(8, "Unlock/extend perk | Manage task");
        put(23, "Buy reward");
    }

    @Override
    public void open(Player player) {
        val slayer = player.getSlayer();
        val settings = player.getSettings();
        settings.refreshSetting(Setting.BIGGER_AND_BADDER_SLAYER_REWARD);
        settings.refreshSetting(Setting.STOP_THE_WYVERN_SLAYER_REWARD);
        val varManager = player.getVarManager();
        varManager.sendBit(LUMBRIDGE_ELITE_DIARY_COMPLETED_BIT, DiaryReward.EXPLORERS_RING4.eligibleFor(player) ? 1 : 0);
        slayer.refreshCurrentAssignment();
        slayer.refreshSlayerPoints();
        slayer.refreshRewards();
        slayer.refreshBlockedTasks();
        player.getInterfaceHandler().sendInterface(this);
        val dispatcher = player.getPacketDispatcher();
        dispatcher.sendComponentSettings(getInterface(), getComponent("Unlock/extend perk | Manage task"), 0, SIZE + 9, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Buy reward"), 0, Enums.SLAYER_ITEM_REWARDS_ENUM.getSize(), AccessMask.CLICK_OP2, AccessMask.CLICK_OP3,
                AccessMask.CLICK_OP4, AccessMask.CLICK_OP5, AccessMask.CLICK_OP10);
    }

    @Override
    protected void build() {
        bind("Unlock/extend perk | Manage task", (player, slotId, itemId, option) -> {
            val slayer = player.getSlayer();
            if (slotId < SIZE) {
                val unlocked = slayer.isUnlocked(slotId);
                if (!unlocked) {
                    slayer.confirmPurchase(slotId);
                } else {
                    slayer.disable(slotId);
                }
            } else if (slotId == SIZE) {
                slayer.confirmTaskCancellation();
            } else if (slotId == SIZE + 1) {
                slayer.confirmTaskBlock();
            } else if (slotId >= (SIZE + 2) && slotId <= (SIZE + 7)) {
                slayer.confirmTaskUnblock(slotId - (SIZE + 2));
            } else if (slotId == SIZE + 8) {
                slayer.confirmFullExtensionUnlock();
            } else {
                throw new IllegalStateException("Slot: " + slotId);
            }
        });

        bind("Buy reward", (player, slotId, itemId, option) -> {
            val slayer = player.getSlayer();
            if (option == 10) {
                Examine.sendItemExamine(player, itemId);
                return;
            }
            if (Enums.SLAYER_ITEM_REWARDS_ENUM.getValue(slotId).orElseThrow(RuntimeException::new) != itemId) {
                return;
            }
            val definitions = ItemDefinitions.get(itemId);
            if (definitions == null) {
                return;
            }
            if (itemId == BROAD_BOLTS || itemId == BROAD_ARROWS) {
                val skills = player.getSkills();
                if (skills.getLevelForXp(Skills.SLAYER) < 55 || skills.getLevelForXp(Skills.RANGED) < (itemId == BROAD_BOLTS ? 61 : 50)) {
                    player.sendMessage("You need a Slayer and a Ranged level of at least 55 and " + (itemId == BROAD_BOLTS ? 61 : 50)
                            + " respectively to purchase " + definitions.getName().toLowerCase() + ".");
                    return;
                }
            } else if (itemId == HERB_SACK) {
                if (player.getSkills().getLevel(Skills.HERBLORE) < 58) {
                    player.sendMessage("You need a Herblore level of at least 58 to buy a herb sack.");
                    return;
                }
                if (player.containsItem(HerbSack.HERB_SACK)) {
                    player.sendMessage("You can only own one herb sack at a time!");
                    return;
                }
            } else if (itemId == RUNE_POUCH) {
                if (player.containsItem(RUNE_POUCH)) {
                    player.sendMessage("You can only own one rune pouch at a time!");
                    return;
                }
            } else if (itemId == FIGHTER_TORSO) {
                if (player.getSkills().getLevelForXp(Skills.DEFENCE) < 40) {
                    player.sendMessage("You need a Defence level of at least 40 to purchase a fighter torso.");
                    return;
                }
            } else if (itemId == ANCHOR) {
                if (player.getSkills().getLevelForXp(Skills.ATTACK) < 60 || player.getSkills().getLevelForXp(Skills.STRENGTH) < 40) {
                    player.sendMessage("You need an Attack level of at least 60 & a Strength level of at least 40 to purchaze the barrelchest anchor.");
                    return;
                }
            }
            val amount = getAmount(option);
            val cost = Enums.SLAYER_REWARDS_COST.getValue(itemId).orElseThrow(RuntimeException::new);
            val slayerPoints = slayer.getSlayerPoints();
            val affordableAmount = Math.min(slayerPoints / cost, amount);
            if (affordableAmount <= 0) {
                player.sendMessage("You don't have enough Slayer points to purchase this.");
                return;
            } else if (affordableAmount < amount) {
                player.sendMessage("You don't have enough Slayer points to purchase this many.");
            }
            val result = player.getInventory().addItem(new Item(itemId, affordableAmount * (definitions.isStackable() ? 250 : 1)));
            val amountBought = result.getSucceededAmount() / (definitions.isStackable() ? 250 : 1);
            slayer.addSlayerPoints(-(amountBought * cost));
            if (result.getResult() == RequestResult.NOT_ENOUGH_SPACE) {
                player.sendMessage("Not enough space in your inventory.");
            }
        });
    }

    private int getAmount(final int id) {
        return id == 2 ? 1 : id == 3 ? 5 : id == 4 ? 10 : 50;
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.SLAYER_REWARDS;
    }
}
