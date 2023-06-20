package com.zenyte.game.world.region.area.bobsisland;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.bobsisland.npc.BobTheCat;
import com.zenyte.game.world.region.area.freakyforester.FreakyForesterArea;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.TeleportPlugin;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 25/06/2019 18:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EvilBobIsland extends Area implements DeathPlugin, TeleportPlugin, RandomEventRestrictionPlugin
{

    public static final String ATTR_EB_FASTEST_TIME = "evil bob fastest time";
    public static final String ATTR_EB_START_TIME = "evil bob start time";

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 2496, 4805 },
                        { 2496, 4748 },
                        { 2560, 4748 },
                        { 2560, 4800 },
                        { 2553, 4800 },
                        { 2553, 4805 }
                })
        };
    }

    public static final Location getEntryLocation(@NotNull final Player player) {
        val object = player.getNumericAttribute("evil bob before entering position").intValue();
        if (object == 0) {
            return new Location(3087, 3487, 0);
        }
        return new Location(object);
    }

    public static final void teleport(@NotNull final Player player) {
        if(player.getArea() instanceof EvilBobIsland || player.getArea() instanceof FreakyForesterArea)
        {
            return;
        }
        TeleportCollection.EVIL_BOB_ISLAND.teleport(player);
        player.addAttribute("evil bob before entering position", player.getLocation().getPositionHash());
        player.addAttribute(EvilBobIsland.ATTR_EB_START_TIME, System.currentTimeMillis());
        player.getAttributes().remove("evil bob fish direction");
        //Sets and retrieves the next random direction object.
        getDirection(player);
    }

    @Override
    public void enter(final Player player) {
        player.sendMessage("Speak to the servant in order to complete the random event.");
        World.findNPC(390, player.getLocation(), 20).ifPresent(bob -> {
            player.setFaceLocation(bob.getLocation());
            player.getDialogueManager().start(BobTheCat.getDialogue(player, bob));
        });
    }

    public static final FishSpot getDirection(@NotNull final Player player) {
        FishSpot direction = Utils.getRandomElement(FishSpot.values());
        val attribute = player.getAttributes().get("evil bob fish direction");
        if (attribute == null) {
            player.addAttribute("evil bob fish direction", direction.toString());
        } else {
            try { direction = FishSpot.valueOf(attribute.toString()); } catch (Exception ignored) {}
        }
        return direction;
    }

    @Override
    public void leave(final Player player, final boolean logout) {
        if (!logout) {
            player.getAttributes().remove("evil bob fish direction");
            player.getAttributes().remove("evil bob complete");
            player.getAttributes().remove("evil bob before entering position");
            if (player.getAttributes().remove("observing random event") == null) {
                player.getAttributes().put("last random event", System.currentTimeMillis());
            }
            val inventory = player.getInventory();
            for (int i = 0; i < 28; i++) {
                val item = inventory.getItem(i);
                if (item == null || !(item.getId() >= 6200 && item.getId() <= 6206)) {
                    continue;
                }
                inventory.deleteItem(item);
            }
        }
    }

    @Override
    public String name() {
        return "Evil Bob's Island";
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return "You will not lose any items if you die on the random event islands.";
    }

    @Override
    public Location getRespawnLocation() {
        return new Location(2526, 4778, 0);
    }

    @Override
    public boolean canTeleport(final Player player, final Teleport teleport) {
        World.findNPC(390, player.getLocation(), 20).ifPresent(bob -> player.getDialogueManager().start(new Dialogue(player, bob) {
            @Override
            public void buildDialogue() {
                npc("You're going nowhere, human!");
            }
        }));
        return false;
    }
}
