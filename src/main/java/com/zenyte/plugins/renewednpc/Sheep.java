package com.zenyte.plugins.renewednpc;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.PlayerChat;

/**
 * @author Kris | 25/11/2018 16:34
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Sheep extends NPCPlugin {

    private static final Item SHEARS = new Item(1735);
    private static final Item WOOL = new Item(1737);

    private static final Animation SHEAR = new Animation(893);
    private static final Animation WADDLE = new Animation(3570);

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new PlayerChat(player, "That's a sheep...I" +
                " think. I can't talk to sheep.")));
        bind("Shear", (player, npc) -> {
            if(!player.getInventory().containsItem(SHEARS)) {
                player.sendMessage("You need a pair of shears to do this.");
                return;
            }

            player.faceEntity(npc);
            player.setAnimation(SHEAR);
            player.sendSound(761);
            player.lock();
            npc.lock();

            if(npc.getId() == 731) {
                WorldTasksManager.schedule(new WorldTask() {

                    private int ticks;

                    @Override
                    public void run() {
                        if(ticks == 0) {
                            npc.setAnimation(WADDLE);
                        } else if(ticks == 1) {
                            player.lock(1);
                            npc.addWalkSteps(npc.getX()-3, npc.getY()-3);
                            player.sendFilteredMessage("The... whatever it is... manages to get away from you!");
                        } else if(ticks == 2) {
                            stop();
                        }
                        ticks++;
                    }
                }, 0, 0);
                return;
            }

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks;

                @Override
                public void run() {
                    if(ticks == 1) {
                        npc.setTransformation(2698);
                        player.sendFilteredMessage("You get some wool.");
                        player.getInventory().addItem(WOOL);
                    } else if(ticks == 2) {
                        player.lock(1);
                        npc.lock(1);
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                731, 2697, 2698, 2699, 2786, 2787, 2788, 2789, 2790, 2791, 2792, 2793, 2794, 2795, 2796,
                2797, 2798, 2799, 2800, 2801, 2802, 2803, 2804, 5726, 5843, 5844, 5845, 5846
        };
    }
}
