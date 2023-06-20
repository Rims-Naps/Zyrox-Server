package com.zenyte.game.world.region.area.freakyforester;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import com.zenyte.game.world.region.area.freakyforester.npc.FreakyForester;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.area.plugins.ExperiencePlugin;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.TeleportPlugin;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class FreakyForesterArea extends Area implements DeathPlugin, TeleportPlugin, RandomEventRestrictionPlugin, ExperiencePlugin
{
    public static final String ATTR_FF_START_TIME = "freaky forester start time";
    public static final String ATTR_FF_FASTEST_TIME = "freaky forester fastest time";
    public static final String ATTR_FF_TAIL_AMOUNT = "freaky forester tails";
    public static final String ATTR_FF_BEFORE_LOC = "freaky forester before entering position";
    public static final String ATTR_FF_COMPLETE = "freaky forester complete";
    private static final String FF_WELCOME_MESSAGE = "Speak to the freaky forester to complete the random event.";
    private static final Location ZENYTE_HOME_LOC = new Location(3087, 3487, 0);

    @Override
    public boolean isSafe()
    {
        return true;
    }

    @Override
    public String getDeathInformation()
    {
        return "You will not lose any items if you die in a random event.";
    }

    @Override
    public Location getRespawnLocation()
    {
        return new Location(2608, 4775, 0);
    }

    @Override
    public boolean canTeleport(Player player, Teleport teleport)
    {
        World.findNPC(NpcId.FREAKY_FORESTER, player.getLocation(), 20).ifPresent(freaky -> player.getDialogueManager().start(new Dialogue(player, freaky) {
        @Override
        public void buildDialogue() {
            npc("I need you to do this task before you leave!");
        }
    }));
        return false;
    }

    @Override
    public RSPolygon[] polygons()
    {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 2575, 4800 },
                        { 2575, 4736 },
                        { 2624, 4736 },
                        { 2624, 4800 },
                })
        };
    }

    public static final int getTailAmount(@NotNull final Player player) {
        int amountNeeded = Utils.random(1, 4);
        val attribute = player.getAttributes().get(ATTR_FF_TAIL_AMOUNT);
        if (attribute == null) {
            player.addAttribute(ATTR_FF_TAIL_AMOUNT, amountNeeded);
        } else {
            try { amountNeeded = Integer.parseInt(attribute.toString()); } catch (Exception ignored) {}
        }
        return amountNeeded;
    }

    public static final void teleport(@NotNull final Player player) {
        if(player.getArea() instanceof EvilBobIsland || player.getArea() instanceof FreakyForesterArea)
        {
            return;
        }
        TeleportCollection.FREAKY_FORESTER_RANDOM.teleport(player);
        player.addAttribute(ATTR_FF_BEFORE_LOC, player.getLocation().getPositionHash());
        player.addAttribute(FreakyForesterArea.ATTR_FF_START_TIME, System.currentTimeMillis());
        player.getAttributes().remove(ATTR_FF_TAIL_AMOUNT);
        getTailAmount(player);
    }

    @Override
    public void enter(Player player)
    {
        player.sendMessage(FF_WELCOME_MESSAGE);
        World.findNPC(NpcId.FREAKY_FORESTER, player.getLocation(), 20).ifPresent(freaky -> {
            player.setFaceLocation(freaky.getLocation());
            player.getDialogueManager().start(FreakyForester.getInitialDialogue(player, freaky));
        });
    }

    public static final Location getEntryLocation(@NotNull final Player player) {
        val object = player.getNumericAttribute(ATTR_FF_BEFORE_LOC).intValue();
        if (object == 0) {
            return ZENYTE_HOME_LOC;
        }
        return new Location(object);
    }

    @Override
    public void leave(Player player, boolean logout)
    {
        if (!logout) {
            player.getAttributes().remove(ATTR_FF_TAIL_AMOUNT);
            player.getAttributes().remove(ATTR_FF_COMPLETE);
            player.getAttributes().remove(ATTR_FF_BEFORE_LOC);
            if (player.getAttributes().remove("observing random event") == null) {
                player.getAttributes().put("last random event", System.currentTimeMillis());
            }
            val inventory = player.getInventory();
            for (int i = 0; i < 28; i++) {
                val item = inventory.getItem(i);
                if (item == null || !(item.getId() == ItemId.RAW_PHEASANT || item.getId() == ItemId.RAW_PHEASANT_6179 || item.getId() == ItemId.RAW_PHEASANT_11704 || item.getId() == ItemId.RAW_PHEASANT_28890)) {
                    continue;
                }
                inventory.deleteItem(item);
            }
        }
    }

    @Override
    public String name()
    {
        return "Freaky Forester Random";
    }

    @Override
    public boolean enabled()
    {
        return false;
    }
}
