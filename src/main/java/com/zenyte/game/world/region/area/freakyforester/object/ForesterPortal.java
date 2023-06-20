package com.zenyte.game.world.region.area.freakyforester.object;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Emote;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.area.freakyforester.FreakyForesterArea;
import com.zenyte.game.world.region.area.freakyforester.npc.FreakyForester;
import com.zenyte.plugins.dialogue.ItemChat;

public class ForesterPortal implements ObjectAction
{
    private static final int[] POSSIBLE_ITEMS = { ItemId.LEDERHOSEN_TOP, ItemId.LEDERHOSEN_SHORTS, ItemId.LEDERHOSEN_HAT};

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option)
    {
        if (player.getAttributes().get(FreakyForesterArea.ATTR_FF_COMPLETE) == null)
        {
            World.findNPC(NpcId.FREAKY_FORESTER, player.getLocation(), 20).ifPresent(freaky -> player.getDialogueManager().start(FreakyForester.getStartedDialogue(player, freaky)));
            return;
        }
        boolean lamp = player.getAttributes().get("observing random event") == null;
        long endTime = System.currentTimeMillis();
        long startTime = player.getNumericAttribute(FreakyForesterArea.ATTR_FF_START_TIME).longValue();
        long total = endTime - startTime;
        if(lamp)
        {
            if (player.getNumericAttribute(FreakyForesterArea.ATTR_FF_FASTEST_TIME).intValue() == 0)
            {
                player.addAttribute(FreakyForesterArea.ATTR_FF_FASTEST_TIME, total);
            } else
            {
                long fastestTime = player.getNumericAttribute(FreakyForesterArea.ATTR_FF_FASTEST_TIME).longValue();
                if(total < fastestTime)
                {
                    player.addAttribute(FreakyForesterArea.ATTR_FF_FASTEST_TIME, total);
                }
            }
            long pbLong = player.getNumericAttribute(FreakyForesterArea.ATTR_FF_FASTEST_TIME).longValue();
            player.sendMessage("Freaky Forester - Total time: " + Colour.RED.wrap(String.format("%.2f", (float) total / 1000 )) + " Personal best: " + Colour.RED.wrap(String.format("%.2f", (float) pbLong / 1000)));
        }
        player.getAttributes().remove(FreakyForesterArea.ATTR_FF_START_TIME);

        player.lock();
        player.addWalkSteps(object.getX(), object.getY(), 1, false);

        WorldTasksManager.schedule(() -> {
            World.findNPC(NpcId.FREAKY_FORESTER, player.getLocation(), 20).ifPresent(freaky -> player.setFaceLocation(freaky.getLocation()));
            player.setAnimation(Emote.RASPBERRY.getAnimation());
            WorldTasksManager.schedule(() -> new Teleport() {

                @Override
                public TeleportType getType() {
                    return TeleportType.RANDOM_EVENT_TELEPORT;
                }

                @Override
                public Location getDestination() {
                    return FreakyForesterArea.getEntryLocation(player);
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
                            player.getInventory().addOrDrop(new Item(ItemId.ANTIQUE_LAMP_7498));
                            if(Utils.random(3) == 0)
                            {
                                player.getInventory().addOrDrop(POSSIBLE_ITEMS[Utils.random(POSSIBLE_ITEMS.length - 1)]);
                                player.getDialogueManager().start(new ItemChat(player, new Item(ItemId.LEDERHOSEN_TOP), "You find an antique lamp along with a piece of lederhosen on your way out. Nice."));
                            } else
                            {
                                player.getDialogueManager().start(new ItemChat(player, new Item(ItemId.ANTIQUE_LAMP_7498), "You find an antique lamp on your way out of the area."));
                            }
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
    public Object[] getObjects()
    {
        return new Object[] {
                ObjectId.EXIT_PORTAL_20843
        };
    }
}
