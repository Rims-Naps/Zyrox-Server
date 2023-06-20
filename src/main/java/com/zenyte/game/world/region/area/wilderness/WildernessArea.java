package com.zenyte.game.world.region.area.wilderness;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.*;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;
import java.util.Set;

/**
 * @author Kris | 29. mai 2018 : 01:20:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WildernessArea extends Area implements DeathPlugin, EntityAttackPlugin,
        PlayerCombatPlugin, RandomEventRestrictionPlugin, LootBroadcastPlugin {

    public static final int IN_WILDERNESS_VARBIT_SPECIAL_UNCLICKABLE = 8121;

    private static final RSPolygon mainlandWildernessPolygon = new RSPolygon(new int[][]{
            { 2944, 3968 }, { 2944, 3681 }, { 2947, 3681 }, { 2947, 3676 }, { 2944, 3676 },
            { 2944, 3525 }, { 2994, 3525 }, { 2997, 3528 }, { 2998, 3535 }, { 2999, 3536 },
            { 3007, 3546 }, { 3023, 3546 }, { 3028, 3537 }, { 3032, 3529 }, { 3037, 3525 },
            { 3391, 3525 }, { 3391, 3968 }
    });

    private static final RSPolygon dungeonsWildernessPolygon = new RSPolygon(new int[][]{{2944, 9920}, {2944, 10879}, {3391, 10879}, {3391, 9920},
            {3264, 9920}, {3264, 9984}, {3200, 9984}, {3200, 9920}, {3072, 9920},
            {3072, 9984}, {3008, 9984}, {3008, 9920}});

    private static final Set<RSPolygon> wildernessPolygons = new ObjectLinkedOpenHashSet<>();

    static {
        wildernessPolygons.add(mainlandWildernessPolygon);
        wildernessPolygons.add(dungeonsWildernessPolygon);
    }

    /**
     * Checks if the coordinates are within the attackable part of the wilderness(starting from 2 few tiles north of
     * the ditch)
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return whether or not the coordinates are in actual wilderness.
     */
    public static final boolean isWithinWilderness(final int x, final int y) {
        for (val polygon : wildernessPolygons) {
            if (polygon.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int broadcastValueThreshold() {
        return 40_000;
    }

    private static void refreshKDR(final Player player) {
        player.getVarManager().sendVar(1102, player.getNumericAttribute("WildernessDeaths").intValue());
        player.getVarManager().sendVar(1103, player.getNumericAttribute("WildernessKills").intValue());
    }

    @NotNull
    public static final OptionalInt getWildernessLevel(@NotNull final Location tile) {
        val x = tile.getX();
        val y = tile.getY();
        if (!isWithinWilderness(x, y)) {
            return OptionalInt.empty();
        }
        if (x >= 2944 && x <= 3391 && y >= 3520 && y <= 4351) {
            return OptionalInt.of(((y - 3520) >> 3) + 1);
        } else if (x >= 3008 && x <= 3071 && y >= 10112 && y <= 10175) {
            return OptionalInt.of(((y - 9920) >> 3) - 1);
        } else if (x >= 2944 && x <= 3391 && y >= 9920 && y <= 10879) {
            return OptionalInt.of(((y - 9920) >> 3) + 1);
        }
        return OptionalInt.empty();
    }

    @Override
    public RSPolygon[] polygons() {
        return wildernessPolygons.toArray(new RSPolygon[0]);
    }

    @Override
    public void enter(final Player player) {
        val canPvp = player.isCanPvp();
        if (!canPvp) {
            setAttackable(player);
        }
        player.getVarManager().sendBit(5963, 1);
        player.getVariables().cancel(TickVariable.BOUNTY_HUNTER_TARGET_LOSS);
        if (player.isXPDropsMultiplied()) {
            if (player.isXPDropsWildyOnly()) {
                player.getVarManager().sendVar(3504, player.getCombatXPRate());
            }
        }
    }

    @Override
    public void leave(final Player player, boolean logout) {
        if (!player.inArea("Wilderness")) {
            setUnattackable(player);
            player.getVarManager().sendBit(5963, 0);
            if (player.isXPDropsMultiplied()) {
                if (player.isXPDropsWildyOnly()) {
                    player.getVarManager().sendVar(3504, 1);
                }
            }
        }
    }

    @Override
    public String name() {
        return "Wilderness";
    }

    private void setAttackable(final Player player) {
        refreshKDR(player);
        player.setCanPvp(true);
        //Sets the special attack orb unclickable.
        player.getVarManager().sendBit(IN_WILDERNESS_VARBIT_SPECIAL_UNCLICKABLE, 1);
        GameNoticeboardInterface.refreshCounters(false);
        //Supposed to clear received damage when re-entering wilderness.
        player.getReceivedDamage().clear();
        GameInterface.WILDERNESS_OVERLAY.open(player);
        if (Constants.BOUNTY_HUNTER) {
            player.getBountyHunter().searchTarget();
        }
    }

    private void setUnattackable(final Player player) {
        player.getInterfaceHandler().closeInterface(GameInterface.WILDERNESS_OVERLAY);
        player.setCanPvp(false);
        player.getVariables().resetTeleblock();
        player.getVarManager().sendBit(IN_WILDERNESS_VARBIT_SPECIAL_UNCLICKABLE, 0);
        //Reset the recieved hits on the player upon leaving Wilderness so that PvM deaths don't register as PvP deaths.
        player.getReceivedDamage().clear();
        GameNoticeboardInterface.refreshCounters(false);
        if (Constants.BOUNTY_HUNTER) {
            player.getBountyHunter().startTargetRemovalCountdown();
        }
    }

    @Override
    public boolean isSafe() {
        return false;
    }

    @Override
    public boolean sendDeath(Player player, Entity source) {
        if (source instanceof Player) {
            val killer = (Player) source;
            killer.getAttributes().put("WildernessKills", killer.getNumericAttribute("WildernessKills").intValue() + 1);
            player.getAttributes().put("WildernessDeaths", player.getNumericAttribute("WildernessDeaths").intValue() + 1);
            refreshKDR(player);
            refreshKDR(killer);
        }
        return false;
    }

    @Override
    public String getDeathInformation() {
        return null;
    }

    @Override
    public Location getRespawnLocation() {
        return null;
    }

    @Override
    public boolean attack(Player player, Entity entity) {
        if (entity instanceof Player) {
            val target = (Player) entity;
            if (player.isCanPvp() && !target.isCanPvp()) {
                player.sendMessage("That player is not in the wilderness.");
                return false;
            }
            val level = getWildernessLevel(player.getLocation()).orElse(-1);
            val otherLevel = getWildernessLevel(entity.getLocation()).orElse(-1);
            val minimumLevel = Math.min(level, otherLevel);
            if (minimumLevel >= 1) {
                if (Math.abs(player.getSkills().getCombatLevel() - target.getSkills().getCombatLevel()) > minimumLevel) {
                    player.sendMessage("The difference between your Combat level and the Combat level of " + target.getPlayerInformation().getDisplayname() + " is too great.");
                    player.sendMessage((target.getAppearance().isMale() ?
                                        "He" :
                                        "She") + " needs to move deeper into the" + " Wilderness before you can attack him.");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean processCombat(Player player, Entity entity, String style) {
        return true;
    }

    public void onAttack(Player player, Entity entity, String style) {
        if (entity instanceof NPC) {
            return;
        }
        val target = (Player) entity;
        if (target != null) {

            if (player.getAttackedByPlayers().getLong(target.getUsername()) < Utils.currentTimeMillis()
                    && target.getAttackedByPlayers().getLong(player.getUsername()) < Utils.currentTimeMillis()) {
                player.getVariables().setSkull(true);
                target.getAttackedByPlayers().put(player.getUsername(), Utils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20));
            }


        }
    }

}
