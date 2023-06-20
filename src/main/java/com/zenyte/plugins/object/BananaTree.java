package com.zenyte.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

/**
 * @author Kris | 27/04/2019 03:01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BananaTree implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equals("Search")) {
            player.sendMessage("The banana tree is empty of bananas right now.");
        } else if (option.equals("Pick")) {
            if (!player.getInventory().hasFreeSlots()) {
                player.getDialogueManager().start(new ItemChat(player, new Item(1963), "You need some free inventory space to pick more bananas."));
                return;
            }
            player.getInventory().addOrDrop(new Item(1963));
            player.sendMessage("You pick a banana from the tree.");
            player.sendSound(new SoundEffect(2582));
            val obj = new WorldObject(object);
            obj.setId(Math.min(2078, obj.getId() + 1));
            World.spawnObject(obj);
            if (object.getId() != 2073) {
                return;
            }
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    val o = World.getObjectWithType(object, object.getType());
                    if (o.getId() > 2073) {
                        val replacement = new WorldObject(o);
                        replacement.setId(Math.max(2073, replacement.getId() - 1));
                        World.spawnObject(replacement);
                        if (replacement.getId() == 2073) {
                            stop();
                        }
                        return;
                    }
                    stop();
                }
            }, 50, 50);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                2073, 2074, 2075, 2076, 2077, 2078
        };
    }
}
