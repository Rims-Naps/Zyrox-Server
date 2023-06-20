package com.zenyte.game.content.minigame.duelarena.area;

import com.zenyte.game.content.consumables.Drinkable;
import com.zenyte.game.content.consumables.Edible;
import com.zenyte.game.content.minigame.duelarena.Duel;
import com.zenyte.game.content.minigame.duelarena.DuelSetting;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.DuelArenaArea;
import com.zenyte.game.world.region.area.plugins.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Objects;

import static com.zenyte.game.content.minigame.duelarena.DuelSetting.*;

/**
 * @author Tommeh | 29-11-2018 | 21:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public abstract class ArenaArea extends DuelArenaArea implements DeathPlugin, PlayerCombatPlugin, TradePlugin, TeleportPlugin, EntityAttackPlugin, EquipmentPlugin, PrayerPlugin,
        DropPlugin, EdiblePlugin, DrinkablePlugin, LogoutPlugin, LoginPlugin, RandomEventRestrictionPlugin {

    @Override
    public RSPolygon[] polygons() {
        return null;
    }

    @Override
    public void enter(Player player) {
        if (player.getDuel() == null) {
            return;
        }
        player.getDuel().resetAttributes(player);
        player.setCantInteract(false);
        player.setCanPvp(true, true);
    }

    @Override
    public void leave(Player player, boolean logout) {
        player.setCanPvp(false);
        val duel = player.getDuel();
        if (duel == null) {
            return;
        }
        if (!duel.isCompleted()) {
            val opponent = Objects.requireNonNull(duel.getOpponent());
            duel.finishDuel(opponent, player);
            duel.sendSpoils(opponent);
            duel.registerDuelHistory(opponent, player);
            player.setDuel(null);
            opponent.setDuel(null);
            player.getPacketDispatcher().sendComponentText(Duel.WINNINGS_INTERFACE, 16, "You forfeit!");
        }
    }

    @Override
    public boolean sendDeath(final Player player, final Entity source) {
        val duel = player.getDuel();
        if (duel == null) {
            return false;
        }
        val opponent = duel.getOpponent();
        if (opponent == null) {
            return false;
        }
        opponent.lock();
        player.setAnimation(Animation.STOP);
        player.lock();
        player.stopAll();
        if (player.getPrayerManager().isActive(Prayer.RETRIBUTION)) {
            player.getPrayerManager().applyRetributionEffect(source);
        }
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (ticks == 1) {
                    player.setAnimation(Player.DEATH_ANIMATION);
                } else if (ticks == 3) {
                    player.sendMessage("Oh dear, you are dead!");
                    if (opponent.getDuel() != null && !duel.isCompleted()) {
                        opponent.sendMessage("You have defeated " + player.getName() + ".");
                        duel.finishDuel(opponent, player);
                        duel.sendSpoils(opponent);
                        duel.registerDuelHistory(opponent, player);
                        player.setDuel(null);
                        opponent.setDuel(null);
                    }
                } else if (ticks == 4) {
                    player.getAppearance().resetRenderAnimation();
                    player.setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 1);
        return true;
    }

    @Override
    public String name() {
        return "Arena Area";
    }

    @Override
    public boolean isSafe() {
        return true;
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
    public boolean attack(final Player player, final Entity entity) {
        if (entity instanceof NPC) {
            return true;
        }
        val duel = player.getDuel();
        if (duel == null) {
            return false;
        }
        val target = (Player) entity;
        val opponent = duel.getOpponent();
        if (opponent == null) {
            return false;
        }
        if (!target.getName().equals(opponent.getName())) {
            player.sendMessage("You have a different target!");
            return false;
        }
        if (duel.isCountdown()) {
            player.sendMessage("The duel has not started yet!");
            return false;
        }
        return true;
    }

    @Override
    public boolean processCombat(final Player player, final Entity entity, final String style) {
        val duel = player.getDuel();
        if (duel == null) {
            return true;
        }
        if (style.equals("Melee") && duel.hasRule(DuelSetting.NO_MELEE)) {
            player.sendMessage("Melee has been turned off for this duel.");
            return false;
        } else if (style.equals("Ranged") && duel.hasRule(DuelSetting.NO_RANGED)) {
            player.sendMessage("Ranged has been turned off for this duel.");
            return false;
        } else if (style.equals("Magic") && duel.hasRule(DuelSetting.NO_MAGIC)) {
            player.sendMessage("Magic has been turned off for this duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean manualLogout(final Player player) {
        val duel = player.getDuel();
        if (duel == null) {
            return true;
        }
        if (duel.isCountdown()) {
            player.sendMessage("You cannot logout during the countdown.");
            return false;
        }
        return true;
    }

    @Override
    public void onLogout(final Player player) {
        val duel = player.getDuel();
        if (duel == null) {
            return;
        }
        val opponent = duel.getOpponent();
        if (opponent == null) {
            return;
        }
        if (duel.inDuel()) {
            duel.finishDuel(opponent, player);
            duel.sendSpoils(opponent);
            duel.registerDuelHistory(opponent, player);
            player.setDuel(null);
            opponent.setDuel(null);
        } else {
            duel.close(true);
        }
    }

    @Override
    public void login(Player player) {
        for (val area : Utils.concatenate(Duel.NORMAL_ARENA, Duel.OBSTACLES_ARENA, Duel.NO_MOVEMENT_ARENA)) {
            if (area.polygons()[0].contains(player.getX(), player.getY())) {
                player.setLocation(Duel.getRandomPoint(Duel.LOBBY.polygons()[0], 0, location -> World.isFloorFree(location, 1)));
            }
        }
    }

    @Override
    public boolean activatePrayer(final Player player, final Prayer prayer) {
        val duel = player.getDuel();
        if (duel == null) {
            return true;
        }
        if (duel.inDuel() && duel.hasRule(DuelSetting.NO_PRAYER)) {
            player.sendMessage("Use of prayer has been turned off for this duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean eat(final Player player, final Edible food) {
        val duel = player.getDuel();
        if (duel == null) {
            return true;
        }
        if (duel.inDuel() && duel.hasRule(DuelSetting.NO_FOOD)) {
            player.sendMessage("Use of food has been turned off for this duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean drink(final Player player, final Drinkable potion) {
        val duel = player.getDuel();
        if (duel == null) {
            return true;
        }
        if (duel.inDuel() && duel.hasRule(DuelSetting.NO_DRINKS)) {
            player.sendMessage("Use of drinks has been turned off for this duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean drop(final Player player, final Item item) {
        val duel = player.getDuel();
        if (duel == null) {
            return true;
        }
        if (duel.inDuel()) {
            player.sendMessage("You cannot drop items whilst in a duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean equip(final Player player, final Item item, final int slot) {
        val duel = player.getDuel();
        if (duel == null || !duel.inDuel()) {
            return true;
        }
        for (int i = HEAD.ordinal(); i <= AMMUNITION.ordinal(); i++) {
            if (Utils.getShiftedBoolean(duel.getSettings(), VALUES[i].getBit())) {
                val index = i == LEG.ordinal() ? 7 : i == HAND.ordinal() ? 9 : i == FEET.ordinal() ? 10 : i == RING.ordinal() ? 12 : i == AMMUNITION.ordinal() ? 13 : i - 13;
                if (index == slot) {
                    player.sendMessage("You can't equip that during this duel.");
                    return false;
                }
            }
        }
        if (duel.hasRule(DuelSetting.NO_FUN_WEAPONS)) {
            for (val id : Duel.FUN_WEAPONS) {
                if (id == item.getId()) {
                    player.sendMessage("You can't equip that during this duel.");
                    return false;
                }
            }
        }
        if (duel.hasRule(DuelSetting.NO_WEAPON_SWITCH) && slot == EquipmentSlot.WEAPON.getSlot()) {
            player.sendMessage("Weapon switching has been disabled for this duel!");
            return false;
        }
        if (duel.hasRule(DuelSetting.RIGHT_HAND) && slot == EquipmentSlot.WEAPON.getSlot() && item.getDefinitions().isTwoHanded()) {
            player.sendMessage("You can't equip that during this duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean canTeleport(final Player player, final Teleport teleport) {
        val duel = player.getDuel();
        if (duel != null) {
            player.sendMessage("You cannot teleport during a duel.");
            return false;
        }
        return true;
    }

    @Override
    public boolean canTrade(final Player player, final Player partner) {
        val duel = player.getDuel();
        if (duel != null) {
            player.sendMessage("You cannot trade during a duel.");
            return false;
        }
        return true;
    }
}
