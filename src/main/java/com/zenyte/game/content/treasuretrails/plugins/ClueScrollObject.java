package com.zenyte.game.content.treasuretrails.plugins;

import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.content.treasuretrails.clues.CrypticClue;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.object.ClosedChestObject;
import com.zenyte.plugins.object.DrawersObject;
import com.zenyte.plugins.object.WardrobeObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 07/04/2019 16:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ClueScrollObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!TreasureTrail.search(player, object, option) && !TreasureTrail.searchWithkey(player, object, option)) {
            if(object.getId() == ObjectId.HAYSTACK && option.equals("Search")) {
                player.lock();
                WorldTasksManager.schedule(new TickTask() {
                    int ticks = 0;
                    @Override
                    public void run() {
                        if(ticks == 0) {
                            player.setAnimation(new Animation(827));
                            player.sendMessage(String.format("You search the %s...", object.getName().toLowerCase()));
                        }
                        if(ticks == 3) {
                            if(Utils.random(0, 9) == 0) {
                                player.getInventory().addOrDrop(new Item(ItemId.NEEDLE));
                                player.getDialogueManager().start(new Dialogue(player) {
                                    @Override
                                    public void buildDialogue() {
                                        player("Wow! A needle!<br> Now what are the chances of finding that?");
                                    }
                                });
                            } else if(Utils.random(0, 49) == 0) {
                                player.applyHit(new Hit(1, HitType.DEFAULT));
                            } else {
                                player.sendMessage("You find nothing of interest.");
                            }
                        }
                        if(ticks == 4) {
                            player.unlock();
                            stop();
                        }
                        ticks++;
                    }
                }, 0, 0);
            } else {
                player.sendMessage("You find nothing.");
            }

        }
    }

    @Override
    public Object[] getObjects() {
        val set = new IntOpenHashSet(CrypticClue.objectMap.keySet());
        set.removeAll(DrawersObject.map.keySet());
        set.removeAll(DrawersObject.map.values());
        set.removeAll(WardrobeObject.map.keySet());
        set.removeAll(WardrobeObject.map.values());
        set.removeAll(ClosedChestObject.map.keySet());
        set.removeAll(ClosedChestObject.map.values());
        set.remove(357);
        return set.toArray();
    }
}
