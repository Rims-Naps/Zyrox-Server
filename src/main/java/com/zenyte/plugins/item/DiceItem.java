package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.ProjectileUtils;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Kris | 02/09/2019 06:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DiceItem extends ItemPlugin {

    public static final String GAMBLE_WARNING = "All forms of gambling are at your own " + Colour.RED.wrap("risk") + "!<br>" +
            Colour.RED.wrap("Always") + " record all gambles and use trusted members or middlemen!<br>" +
            "The hub for gambling is " + Colour.RED.wrap("Castle-Wars") + ", which can be accessed from the Zenyte " +
            "portal at home, under misc teleports.";

    @Override
    public void handle() {
        bind("Private-roll", (player, item, slotId) -> {
            if (player.getDiceDelay() > System.currentTimeMillis() || (player.getDuel() != null && player.getDuel().inDuel())) {
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You need to be out of combat to do that.");
                return;
            }
            roll(player, false);
        });
        bind("Clan-roll", (player, item, slotId) -> {
            if (player.getDiceDelay() > System.currentTimeMillis() || (player.getDuel() != null && player.getDuel().inDuel())) {
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You need to be out of combat to do that.");
                return;
            }
            val channel = player.getSettings().getChannel();
            if (channel == null) {
                player.sendMessage("You must be in a clan channel to do this.");
                return;
            }
            if (channel.getOwner().equalsIgnoreCase("zenyte")) {
                player.sendMessage("You may not use the dice bag within the official Zenyte clan channel.");
                return;
            }
            roll(player, true);
        });
    }

    private static final SoundEffect[] sounds = new SoundEffect[] {
            new SoundEffect(7609, 3, 33),
            new SoundEffect(7608, 3, 42),
            new SoundEffect(7610, 3, 45),
            new SoundEffect(7611, 3, 51)
    };

    /**
     * Rolls the percentile dice for a value from 1 to 100, both ends included.
     * @param player the player rolling the dice.
     */
    private void roll(@NotNull final Player player, final boolean clan) {
        val direction = getAvailableDirection(player);
        val tile = player.getLocation().transform(direction, 1);
        val roll = 1 + Utils.secureRandom(99);
        //Another bit of OSRS engine limitations - graphics not played on-top of an entity will not play sound effects, therefore..
        for (val sound : sounds) {
            World.sendSoundEffect(tile, sound);
        }
        player.setDiceDelay(System.currentTimeMillis() + TimeUnit.TICKS.toMillis(3));
        player.forceAnimation(new Animation(15249));
        World.sendGraphics(new Graphics(getGraphics(direction)), tile);
        player.setFaceLocation(tile);
        player.lock(2);
        player.resetWalkSteps();
        WorldTasksManager.schedule(() -> {
            player.sendMessage("You rolled " + Colour.MAROON.wrap(Integer.toString(roll)) + " on the percentile dice.");
            if (clan) {
                val channel = Objects.requireNonNull(player.getSettings().getChannel());
                for (val member : channel.getMembers()) {
                    if (member == player) {
                        continue;
                    }
                    member.sendMessage("<img=13>Clan chat member " + Colour.MAROON.wrap(player.getName()) + " rolled " + Colour.MAROON.wrap(Integer.toString(roll)) + " on "
                            + "the percentile dice.");
                }
            }
        });
    }

    /**
     * Gets the main direction based on where the user is facing right now, rounding it up to one of the main four directions. If the die cannot be thrown on that position, it seeks
     * the next available tile based on the {@link Direction#mainDirections} array.
     * @param player the player throwing the die.
     * @return the direction to which the player can freely throw the die; Falls back to whichever direction the player is facing.
     */
    private final Direction getAvailableDirection(@NotNull final Player player) {
        var mainDirection = getMainDirection(player.getDirection());
        val tile = player.getLocation().transform(mainDirection, 1);
        if (!ProjectileUtils.isProjectileClipped(null, null, player.getLocation(), tile, true)) {
            return mainDirection;
        }
        for (val dir : Direction.mainDirections) {
            tile.setLocation(player.getLocation().transform(dir, 1));
            if (!ProjectileUtils.isProjectileClipped(null, null, player.getLocation(), tile, true)) {
                return dir;
            }
        }
        return mainDirection;
    }

    /**
     * Gets one of the main four directions based on the input value which ranges from 0 to 2048.
     * @param direction the int value of the player's direction.
     * @return the closest main direction constant.
     */
    private final Direction getMainDirection(final int direction) {
        if (direction <= 255 || direction >= 1793) {
            return Direction.SOUTH;
        } else if (direction <= 767) {
            return Direction.WEST;
        } else if (direction <= 1279) {
            return Direction.NORTH;
        }
        return Direction.EAST;
    }

    /**
     * Gets the correct graphics id based on the direction it has to roll in. As the graphics aren't attached to the player, there are four graphics, all doing the same thing and
     * rolling in four main individual directions.
     * @param direction the direction to which the graphics should roll.
     * @return the id of the graphics.
     */
    private final int getGraphics(@NotNull final Direction direction) {
        return direction == Direction.SOUTH ? 2518 : direction == Direction.NORTH ? 2519 : direction == Direction.EAST ? 2520 : 2521;
    }

    @Override
    public int[] getItems() {
        return new int[] {
                30050
        };
    }
}
