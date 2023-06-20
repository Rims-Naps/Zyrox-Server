package com.zenyte.game.content.achievementdiary.plugins;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.ItemActionHandler;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;

/**
 * @author Tommeh | 30/05/2019 | 21:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class DiaryItem extends ItemPlugin {

    @Override
    public void handle() {

    }

    @Override
    public void setDefaultHandlers() {
        setDefault("Drop", (player, item, slotId) -> ItemActionHandler.dropItem(player, "Drop", slotId, 300, 500));
        setDefault("Destroy", (player, item, slotId) -> ItemActionHandler.dropItem(player, "Destroy", slotId, 300, 500));
        setDefault("Check", (player, item, slotId) -> player.getChargesManager().checkCharges(item));
        setDefault("Wear", (player, item, slotId) -> {
            val reward = DiaryReward.get(item.getId());
            if (!reward.eligibleFor(player)) {
                player.sendMessage("You need to complete all of the " + reward.getComplexity().toString().toLowerCase() + " " + reward.getArea().getAreaName() + " diaries to wear this.");
                return;
            }
            player.getEquipment().wear(slotId);
        });
        setDefault("Wield", (player, item, slotId) -> {
            val reward = DiaryReward.get(item.getId());
            if (!reward.eligibleFor(player)) {
                player.sendMessage("You need to complete all of the " + reward.getComplexity().toString().toLowerCase() + " " + reward.getArea().getAreaName() + " diaries to wear this.");
                return;
            }
            player.getEquipment().wear(slotId);
        });
        setDefault("Equip", (player, item, slotId) -> {
            val reward = DiaryReward.get(item.getId());
            if (!reward.eligibleFor(player)) {
                player.sendMessage("You need to complete all of the " + reward.getComplexity().toString().toLowerCase() + " " + reward.getArea().getAreaName() + " diaries to wear this.");
                return;
            }
            player.getEquipment().wear(slotId);
        });
        setDefault("Remove", (player, item, slotId) -> {
            player.stopAll(false, !player.getInterfaceHandler().isVisible(GameInterface.EQUIPMENT_STATS.getId()), true); //TODO improve this
            player.getEquipment().unequipItem(slotId);
        });
    }

    @Override
    public int[] getItems() {
        return new int[0];
    }
}
