package com.zenyte.game.content.chambersofxeric.rewards;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import lombok.val;

/**
 * @author Kris | 27/07/2019 07:26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RaidRewardsInterface extends Interface {
    @Override
    protected void attach() {
        put(5, "Take item");
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Take item"), 0,
                ContainerType.RAID_REWARDS.getSize(), AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
    }

    @Override
    protected void build() {
        bind("Take item", (player, slotId, itemId, option) -> player.getRaid().ifPresent(raid -> {
            val rewards = raid.getRewards();
            if (rewards == null) {
                return;
            }
            val container = rewards.getRewardMap().get(player);
            if (container == null) {
                return;
            }

            if (option == 10) {
                Examine.sendItemExamine(player, itemId);
                return;
            }

            if (slotId == 4) {
                if (!rewards.getPlayers().contains(player)) {
                    return;
                }
                if (player.getInventory().hasFreeSlots()) {
                    rewards.getPlayers().remove(player);
                    player.getVarManager().sendBit(5457, 0);
                    player.getInventory().addItem(new Item(20899));
                } else {
                    player.sendMessage("You need some more free inventory space to take this.");
                }
                return;
            }
            val item = container.get(slotId);
            if (item == null) {
                return;
            }
            container.withdraw(player, player.getInventory().getContainer(), slotId, item.getAmount());
            container.refresh(player);
            player.getInventory().getContainer().refresh(player);
            if (container.isEmpty()) {
                player.getVarManager().sendBit(5456, 0);
            }
        }));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.RAID_REWARDS;
    }
}
