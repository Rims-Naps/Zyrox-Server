package com.zenyte.game.world.region.area.bobsisland.object;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Emote;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

public class BobPortal implements ObjectAction
{
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.getAttributes().get("evil bob complete") == null) {
            World.findNPC(390, player.getLocation(), 20).ifPresent(bob -> player.getDialogueManager().start(new Dialogue(player, bob) {
                @Override
                public void buildDialogue() {
                    npc("You're going nowhere, human!");
                }
            }));
            return;
        }
        val lamp = player.getAttributes().get("observing random event") == null;

        long endTime = System.currentTimeMillis();
        long startTime = player.getNumericAttribute(EvilBobIsland.ATTR_EB_START_TIME).longValue();
        long total = endTime - startTime;
        if(lamp)
        {
            if (player.getNumericAttribute(EvilBobIsland.ATTR_EB_FASTEST_TIME).intValue() == 0)
            {
                player.addAttribute(EvilBobIsland.ATTR_EB_FASTEST_TIME, total);
            } else
            {
                long fastestTime = player.getNumericAttribute(EvilBobIsland.ATTR_EB_FASTEST_TIME).longValue();
                if(total < fastestTime)
                {
                    player.addAttribute(EvilBobIsland.ATTR_EB_FASTEST_TIME, total);
                }
            }
            long pbLong = player.getNumericAttribute(EvilBobIsland.ATTR_EB_FASTEST_TIME).longValue();
            player.sendMessage("Evil Bob - Total time: " + Colour.RED.wrap(String.format("%.2f", (float) total / 1000 )) + " Personal best: " + Colour.RED.wrap(String.format("%.2f", (float) pbLong / 1000)));
        }
        player.getAttributes().remove(EvilBobIsland.ATTR_EB_START_TIME);
        player.lock();
        player.addWalkSteps(object.getX(), object.getY(), 1, false);

        WorldTasksManager.schedule(() -> {
            World.findNPC(390, player.getLocation(), 20).ifPresent(bob -> player.setFaceLocation(bob.getLocation()));
            player.setAnimation(Emote.RASPBERRY.getAnimation());
            WorldTasksManager.schedule(() -> new Teleport() {

                @Override
                public TeleportType getType() {
                    return TeleportType.RANDOM_EVENT_TELEPORT;
                }

                @Override
                public Location getDestination() {
                    return EvilBobIsland.getEntryLocation(player);
                }

                @Override
                public int getLevel() {
                    return 0;
                }

                @Override
                public double getExperience() {
                    return 0;
                }

                @Override
                public void onArrival(final Player player) {
                    if (lamp) {
                        WorldTasksManager.schedule(() -> {
                            player.getInventory().addOrDrop(new Item(7498));
                            player.getDialogueManager().start(new ItemChat(player, new Item(7498), "You find an antique lamp on your way off the island."));
                        });
                    }
                }

                @Override
                public int getRandomizationDistance() {
                    return 0;
                }

                @Override
                public Item[] getRunes() {
                    return new Item[0];
                }

                @Override
                public int getWildernessLevel() {
                    return 0;
                }

                @Override
                public boolean isCombatRestricted() {
                    return false;
                }
            }.teleport(player), 4);
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                23115
        };
    }
}