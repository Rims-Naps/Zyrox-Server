package com.zenyte.plugins.item;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;

/**
 * @author Kris | 16/03/2019 02:35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ZenyteDonatorRing extends ItemPlugin {
    @Override
    public void handle() {
        bind("Teleport", (player, item, slotId) -> {
            if(player.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR))
            {
                GameInterface.TELEPORT_MENU.open(player);
            }
            else
            {
                if(!player.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER))
                {
                   player.sendMessage("You shouldn't have this!");
                    World.sendMessage(MessageType.GLOBAL_BROADCAST, String.format("[Server Message]: %s was caught with an item they shouldn't have! Public shame!", player.getUsername()));
                   if(player.getInventory().containsItem(ItemId.ZENYTE_DONATOR_RING))
                   {
                       player.getInventory().deleteItem(ItemId.ZENYTE_DONATOR_RING, 1);
                       player.getInventory().refresh();
                   }
                   if(player.getEquipment().isWearing(new Item(ItemId.ZENYTE_DONATOR_RING, 1)))
                   {
                       player.getEquipment().set(EquipmentSlot.RING, null);
                       player.getEquipment().refresh();
                   }
                }
                if(!WildernessArea.isWithinWilderness(player.getX(), player.getY()))
                {
                    GameInterface.TELEPORT_MENU.open(player);
                }
                else
                {
                    player.sendMessage("You can't use this in the wilderness!");
                }
            }
        });
    }

    @Override
    public int[] getItems() {
        return new int[] { ItemId.ZENYTE_DONATOR_RING };
    }
}

