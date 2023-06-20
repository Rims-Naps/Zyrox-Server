package com.zenyte.game.content.minigame.castlewars;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.agility.shortcut.CastlewarsSteppingStone;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.content.minigame.castlewars.CastleWarsOverlay.CWarsOverlayVarbit;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.plugins.object.CastleWarsFlagObject;
import lombok.Getter;
import lombok.val;

import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.MAIN_LOBBY_SPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.SARADOMIN_CLOAK;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.ZAMORAK_CLOAK;
import static com.zenyte.game.content.skills.agility.shortcut.CastlewarsSteppingStone.*;
import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;

public class CastleWarsArea extends Area implements LoginPlugin, CannonRestrictionPlugin, RandomEventRestrictionPlugin, EntityAttackPlugin, DeathPlugin, EquipmentPlugin,
        LayableTrapRestrictionPlugin, CycleProcessPlugin, TeleportPlugin {

    public static final Location SARADOMIN_RESPAWN = new Location(2427, 3076, 1);
    public static final Location ZAMORAK_RESPAWN = new Location(2372, 3131, 1);

    @Getter private static int ticks;

    @Override
    public void login(final Player player) {
        player.setLocation(MAIN_LOBBY_SPAWN);
    }

    @Override
    public void enter(final Player player) {
        if (!Constants.CASTLE_WARS) {
            return;
        }
        if (GlobalAreaManager.getArea(player.getLastLocation()) instanceof CastleWarsArea) {
            return;
        }
        CastleWarsOverlay.processVarbits(player);
        GameInterface.CASTLE_WARS_OVERLAY.open(player);
        player.getEquipment().set(EquipmentSlot.CAPE, CastleWars.getTeam(player).equals(CastleWarsTeam.SARADOMIN) ? SARADOMIN_CLOAK : ZAMORAK_CLOAK);
        player.setPlayerOption(5, "Take-from", false);

        //Forced appearance basically hides the player's hair, necessary to prevent hair clipping.
        player.getAppearance().forceAppearance(EquipmentSlot.HELMET.getSlot(), (CastleWars.getTeam(player).equals(CastleWarsTeam.SARADOMIN) ? SARADOMIN_CLOAK : ZAMORAK_CLOAK).getId());
        player.getEquipment().refresh();
        player.setCanPvp(true);
    }

    @Override
    public void leave(final Player player, final boolean logout) {
        if (!Constants.CASTLE_WARS) {
            return;
        }
        if (GlobalAreaManager.getArea(player.getLocation()) instanceof CastleWarsArea) {
            return;
        }
        if(hasFlag(player)) {
            handleFlagDrop(player, true);
        }

        player.setPlayerOption(5, "null", false);
        player.setCanPvp(false);
        CastleWars.removeCwarsItems(player, true);
        player.getInterfaceHandler().closeInterface(GameInterface.CASTLE_WARS_OVERLAY);
        (CastleWars.getTeam(player).equals(CastleWarsTeam.SARADOMIN) ? CastleWars.SARADOMIN_TEAM : CastleWars.ZAMORAK_TEAM).remove(player);
        player.getAppearance().clearForcedAppearance();
        player.getReceivedDamage().clear();
        player.getReceivedHits().clear();
        player.setHitpoints(player.getMaxHitpoints());
        if(CastleWars.SARADOMIN_TEAM.size() == 0 && CastleWars.ZAMORAK_TEAM.size() == 0) {
            CastleWars.finish();
        }
    }

    @Override
    public void process() {
        if(!CastleWars.isActive()) {
            return;
        }

        if(CastleWars.isActive() && !CastleWars.isTimerStarted()) {
            ticks = 1500;
            CastleWars.setTimerStarted(true);
        }

        if(ticks % 100 == 0) {
            for (final Player player : getPlayers()) {
                player.getVarManager().sendVar(CastleWarsLobbyOverlay.TIMER_VARP, (ticks / 100));
            }
        }

        if(ticks == 0) {
            CastleWars.finish();
        } else {
            ticks--;
        }
    }

    @Override
    public String name() {
        return "Castle Wars";
    }

    @Override
    public boolean sendDeath(final Player player, final Entity source) {
        player.setAnimation(null);
        player.lock();
        player.stopAll();
        if (player.getPrayerManager().isActive(Prayer.RETRIBUTION)) {
            player.getPrayerManager().applyRetributionEffect(source);
        }
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (player.isFinished() || player.isNulled()) {
                    stop();
                    return;
                }
                if (ticks == 1) {
                    player.setAnimation(DEATH_ANIMATION);
                } else if (ticks == 3) {
                    if(hasFlag(player)) {
                        handleFlagDrop(player, false);
                    }
                } else if (ticks == 4) {
                    player.reset();
                    player.sendMessage("Oh dear, you have died.");
                    player.setAnimation(Animation.STOP);
                    player.getVariables().setSkull(false);
                    player.setLocation(CastleWars.getTeam(player).equals(CastleWarsTeam.SARADOMIN) ? SARADOMIN_RESPAWN : ZAMORAK_RESPAWN);
                } else if (ticks == 5) {
                    player.unlock();
                    player.setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
        return true;
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return "You will not lose items on death, but may not leave Castle Wars with Castle Wars-only items.";
    }

    @Override
    public Location getRespawnLocation() {
        return SARADOMIN_RESPAWN;
    }

    @Override
    public boolean attack(final Player player, final Entity entity) {
        if (!(entity instanceof Player))
            return true;
        val opponent = (Player) entity;
        if (CastleWars.getTeam(player) == CastleWars.getTeam(opponent)) {
            player.sendMessage("You can't attack players on your own team!");
           return false;
        }
        return true;
    }

    @Override
    public boolean acceptedTarget(final Player player, final Entity entity) {
        return entity instanceof NPC || (CastleWars.getTeam(player) != CastleWars.getTeam((Player) entity));
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{
            new RSPolygon(new int[][]{
                {2368, 3136}, {2368, 3088}, {2384, 3072}, {2432, 3072}, {2432, 3120}, {2416, 3136}
            }),

            new RSPolygon(new int[][]{
                    { 2376, 9535 }, { 2403, 9519 }, { 2436, 9487 }, { 2430, 9475 }, { 2425, 9475 }, { 2389, 9497 }, { 2365, 9513 }, { 2368, 9529 }
            }),
        };
    }

    @Override
    public boolean equip(final Player player, final Item item, final int slot) {
        if (slot == EquipmentSlot.CAPE.getSlot() || slot == EquipmentSlot.HELMET.getSlot()) {
            player.sendMessage("You can't remove your team's colours.");
            return false;
        }

        if (slot == EquipmentSlot.WEAPON.getSlot() || slot == EquipmentSlot.SHIELD.getSlot()) {
            val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
            if(!hasFlag(player)) {
                return true;
            } else {
                handleFlagDrop(player, false);
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean unequip(final Player player, final Item item, final int slot) {
        if (slot == EquipmentSlot.CAPE.getSlot()) {
            player.sendMessage("You can't remove your team's colours.");
            return false;
        }

        if (slot == EquipmentSlot.WEAPON.getSlot() || slot == EquipmentSlot.SHIELD.getSlot()) {
            val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
            if(!hasFlag(player)) {
                return true;
            } else {
                handleFlagDrop(player, false);
                return false;
            }
        }

        return true;
    }

    public static boolean hasFlag(final Player player) {
        return player.getEquipment().getId(EquipmentSlot.WEAPON) == CastleWarsFlagObject.SARADOMIN_FLAG.getId()
                || player.getEquipment().getId(EquipmentSlot.WEAPON) == CastleWarsFlagObject.ZAMORAK_FLAG.getId();
    }

    public static void handleFlagDrop(final Player player, final boolean leave) {
        val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
        val objectId = weapon == CastleWarsFlagObject.SARADOMIN_FLAG.getId() ? CastleWarsFlagObject.SARADOMIN_FLAG_GROUND : CastleWarsFlagObject.ZAMORAK_FLAG_GROUND;
        val object = getDroppedFlagObject(player, leave, objectId);
        val varbit = objectId == CastleWarsFlagObject.SARADOMIN_FLAG_GROUND ? CWarsOverlayVarbit.SARADOMIN_FLAG : CWarsOverlayVarbit.ZAMORAK_FLAG;
        player.getEquipment().set(EquipmentSlot.WEAPON, null);

        // doesn't matter what team we input here as its a universal varbit
        CastleWars.setVarbit(CastleWarsTeam.SARADOMIN, varbit, 2);
        World.spawnObject(object);
    }

    public static WorldObject getDroppedFlagObject(final Player player, final boolean leave, final int objectId) {
        Location tile = leave ? player.getLastLocation() : player.getLocation();
        val saradomin = CastleWars.getTeam(player).equals(CastleWarsTeam.SARADOMIN);
        if(World.containsObjectWithId(player.getLocation(), 4411)) {
            val west = player.getLocation().getX() < 2390;
            tile = saradomin ? (west ? WEST_FINISH_FLAG : EAST_FINISH_FLAG) : (west ? WEST_START_FLAG : EAST_START_FLAG);

            if(player.getLocation() == CastlewarsSteppingStone.WEST_START) {
                tile = WEST_START_FLAG;
            }

            if(player.getLocation() == CastlewarsSteppingStone.WEST_FINISH) {
                tile = WEST_FINISH_FLAG;
            }

            if(player.getLocation() == EAST_FINISH) {
                tile = EAST_FINISH_FLAG;
            }

            if(player.getLocation() == EAST_START) {
                tile = EAST_START_FLAG;
            }
        }

        return new WorldObject(objectId, 10, 0, tile);
    }

    static {
        /* Spawn prayer altars for each team */
        World.spawnObject(new WorldObject(61, 10, 3,  new Location(2368, 3128, 1)));
        World.spawnObject(new WorldObject(409, 10, 3,  new Location(2431, 3078, 1)));

        /* Populate castlewars patch data */
        for(final CastlewarsRockPatch patch : CastlewarsRockPatch.VALUES) {
            CastlewarsRockPatch.PATCHES.put(patch.getPatch().getPositionHash(), patch);
            for(final Location wall : patch.getWalls())
                CastlewarsRockPatch.PATCHES.put(wall.getPositionHash(), patch);
        }
    }

    @Override
    public boolean canTeleport(final Player player, final Teleport teleport) {
        player.sendMessage("You cannot teleport out of here.");
        return false;
    }
}
