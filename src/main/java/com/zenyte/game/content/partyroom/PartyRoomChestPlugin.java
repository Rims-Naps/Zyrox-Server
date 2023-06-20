package com.zenyte.game.content.partyroom;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 25/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PartyRoomChestPlugin implements ObjectAction, ItemOnObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!player.inArea(FaladorPartyRoom.class)) {
            return;
        }
        switch (option) {
            case "Open":
                if (object.isLocked()) {
                    return;
                }
                object.setLocked(true);
                player.setAnimation(new Animation(832));
                player.sendSound(new SoundEffect(52));
                WorldTasksManager.schedule(() -> {
                    val obj = new WorldObject(object);
                    obj.setId(2418);
                    World.spawnObject(obj);
                    object.setLocked(false);
                });
                return;
            case "Shut":
                if (object.isLocked()) {
                    return;
                }
                if (!FaladorPartyRoom.getPartyRoom().getVariables().isChestCloseable()) {
                    player.sendMessage("The chest may not currently be closed.");
                    return;
                }
                object.setLocked(true);
                player.setAnimation(new Animation(832));
                player.sendSound(new SoundEffect(51));
                WorldTasksManager.schedule(() -> {
                    val obj = new WorldObject(object);
                    obj.setId(26193);
                    World.spawnObject(obj);
                    object.setLocked(false);
                });
                return;
            case "Deposit":
                GameInterface.PARTY_DROP_CHEST.open(player);
        }
    }

    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
        if (object.getId() == 26193) {
            player.sendFilteredMessage("Nothing interesting happens.");
            return;
        }
        if (!player.inArea(FaladorPartyRoom.class)) {
            return;
        }
        GameInterface.PARTY_DROP_CHEST.open(player);
    }

    @Override
    public Object[] getItems() {
        return null;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                2418, 26193
        };
    }
}
