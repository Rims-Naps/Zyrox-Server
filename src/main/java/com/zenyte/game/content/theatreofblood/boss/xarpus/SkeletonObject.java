package com.zenyte.game.content.theatreofblood.boss.xarpus;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

public class SkeletonObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.setAnimation(Animation.GRAB);
        for (val p : VerSinhazaArea.getParty(player).getPlayers()) {
            if (p.getInventory().containsItem(ItemId.DAWNBRINGER) || p.getEquipment().containsItem(ItemId.DAWNBRINGER)) {
                return;
            }
        }
        player.getInventory().addOrDrop(new Item(ItemId.DAWNBRINGER));
        player.getDialogueManager().start(new ItemChat(player, new Item(ItemId.DAWNBRINGER), "You find the Dawnbringer; you feel a pulse of energy burst through it."));
        World.removeObject(object);
        object.setId(object.getId() + 1);
        World.spawnObject(object);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 32741 };
    }
}
