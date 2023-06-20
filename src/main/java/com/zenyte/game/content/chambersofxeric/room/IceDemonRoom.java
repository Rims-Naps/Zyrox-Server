package com.zenyte.game.content.chambersofxeric.room;

import com.zenyte.game.content.chambersofxeric.CombatPointCapCalculator;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.map.RaidArea;
import com.zenyte.game.content.chambersofxeric.map.RaidRoom;
import com.zenyte.game.content.chambersofxeric.npc.IceDemon;
import com.zenyte.game.content.chambersofxeric.npc.IcefiendNPC;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Kris | 16. nov 2017 : 2:35.33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class IceDemonRoom extends RaidArea {

    /**
     * The animation that's performed when depositing the kindling.
     */
    public static final Animation depositAnimation = new Animation(3687);
    /**
     * A 2D array containing locations of the demon - the storm's coordinates, the demon's coordinates and the location to which the demon
     * walks to after being defrozen.
     */
    private static final Location[][] iceDemonLocations = new Location[][]{
            new Location[]{new Location(3271, 5365, 0), new Location(3272, 5365, 0), new Location(3278, 5365, 0)},
            new Location[]{new Location(3310, 5368, 0), new Location(3310, 5367, 0), new Location(3310, 5361, 0)},
            new Location[]{new Location(3349, 5362, 0), new Location(3348, 5362, 0), new Location(3342, 5363, 0)}
    };
    /**
     * A 2D array containing coordinates to all four of the braziers.
     */
    private static final Location[][] brazierSpawnLocations = new Location[][]{
            new Location[]{
                    new Location(3272, 5362, 0), new Location(3275, 5364, 0), new Location(3275, 5367, 0),
                    new Location(3272, 5369, 0)
            },
            new Location[]{
                    new Location(3307, 5368, 0), new Location(3309, 5365, 0), new Location(3312, 5365, 0),
                    new Location(3314, 5368, 0)
            },
            new Location[]{
                    new Location(3349, 5366, 0), new Location(3346, 5364, 0), new Location(3346, 5361, 0),
                    new Location(3349, 5359, 0)
            }
    };
    /**
     * A 2D array containing locations to all four of the possible icefiends, matched up with braziers.
     */
    private static final Location[][] icefiendSpawnLocations = new Location[][]{
            new Location[]{
                    new Location(3273, 5362, 0), new Location(3275, 5365, 0), new Location(3275, 5366, 0),
                    new Location(3273, 5369, 0)
            },
            new Location[]{
                    new Location(3307, 5367, 0), new Location(3310, 5365, 0), new Location(3311, 5365, 0),
                    new Location(3314, 5367, 0)
            },
            new Location[]{
                    new Location(3348, 5366, 0), new Location(3346, 5363, 0), new Location(3346, 5362, 0),
                    new Location(3348, 5359, 0)
            }
    };

    /**
     * The locations to the storage units in the ice demon room.
     */
    private static final Location[] storageUnits = new Location[]{
            new Location(3285, 5366, 0), new Location(3310, 5356, 0),
            new Location(3336, 5362, 0)
    };
    /**
     * The sound effect played when the ice demon defreezes.
     */
    private static final SoundEffect defreezeSound = new SoundEffect(1930, 10, 0);
    /**
     * The graphics that are performed when the demon extinguishes the fires.
     */
    private static final Graphics poofGraphics = new Graphics(86, 0, 90);
    /**
     * A list of spawned icefiends.
     */
    @Getter
    private final List<IcefiendNPC> icefiends = new ObjectArrayList<>(4);
    /**
     * A list of spawned braziers; Concurrent as it's modified mid-iterations.
     */
    @Getter
    private final List<Brazier> braziers = new CopyOnWriteArrayList<>();
    /**
     * The ice demon boss.
     */
    @Getter
    private IceDemon demon;
    /**
     * The storm object around the ice demon.
     */
    private WorldObject storm;

    public IceDemonRoom(final RaidRoom type, final Raid raid, final int rotation, final int size, final int regionX, final int regionY,
                        final int chunkX, final int chunkY, final int fromPlane, final int toPlane) {
        super(type, raid, rotation, size, regionX, regionY, chunkX, chunkY, fromPlane, toPlane);
    }

    @Override
    public Location[] getStorageUnitLocations() {
        return storageUnits;
    }

    @Override
    public CombatPointCapCalculator buildPointsCap() {
        return new CombatPointCapCalculator().appendNPC(demon);
    }


    /**
     * Processes all the active braziers in the room.
     */
    public void processBraziers() {
        for (val brazier : braziers) {
            brazier.process();
        }
    }

    /**
     * Defreezes the ice demon, forces the demon to walk to the center of the arena, extinguishes the fires, kills the icefiends and
     * initiates the fight.
     */
    public void defreeze() {
        demon.setStage(1);
        final Location chosen = iceDemonLocations[index][2];
        final int[] destOffsets = getDestinationOffsets();
        final Location tile = getLocation(chosen.getX(), chosen.getY(), 0);
        demon.addWalkSteps(tile.getX() + destOffsets[0], tile.getY() + destOffsets[1], -1, false);
        for (final Brazier brazier : braziers) {
            World.spawnObject(new WorldObject(29747, brazier.getType(), brazier.getRotation(), brazier));
            World.sendGraphics(poofGraphics, brazier);
        }
        World.sendSoundEffect(demon.getLocation(), defreezeSound);
        braziers.clear();
        for (final IcefiendNPC icefiend : icefiends) {
            icefiend.sendDeath();
        }
        icefiends.clear();
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                stop();
                demon.setTransformation(7585);
                demon.setForceAggressive(true);
                demon.setStage(2);
            }
        }, 7);
    }

    /**
     * Removes the storm object blocking the exit.
     */
    public void removeStorm() {
        World.removeObject(storm);
    }

    /*@Override
    public boolean canPass(final Player player, final WorldObject object) {
        if (!demon.isDead()) {
            player.sendMessage("You need to defeat the demon before you may pass.");
            return false;
        }
        return true;
    }*/

    @Override
    public void loadRoom() {
        val stormTile = getNPCLocation(iceDemonLocations[index][0], 2);
        World.spawnObject(storm = new WorldObject(29876, 10, 0, stormTile));
        demon = new IceDemon(this, NpcId.ICE_DEMON, new Location(stormTile));
        demon.spawn();
        demon.setFaceLocation(getNPCLocation(iceDemonLocations[index][1], 2));
        val count = Math.min(raid.getOriginalPlayers().size(), 4);
        val usedSpawns = new boolean[4];
        while (icefiends.size() != count) {
            val random = Utils.random(3);
            if (usedSpawns[random]) {
                continue;
            }
            usedSpawns[random] = true;
            val icefiend = new IcefiendNPC(getLocation(icefiendSpawnLocations[index][random]));
            icefiend.spawn();
            icefiends.add(icefiend);
            icefiend.setFaceLocation(getLocation(brazierSpawnLocations[index][random]));
        }
    }

    /**
     * Extinguishes the brazier within the parameters.
     *
     * @param brazier the brazier to which to extinguish.
     */
    private void extinguishBrazier(final Brazier brazier) {
        braziers.remove(brazier);
        World.spawnObject(new WorldObject(29747, brazier.getType(), brazier.getRotation(), brazier));
    }

    /**
     * An array of coordinate offsets to which the demon walks.
     */
    private int[] getDestinationOffsets() {
        if (index == 0 && getRotation() == 2 || index == 1 && getRotation() == 1 || index == 2 && getRotation() == 0) {
            return new int[]{0, -1};
        } else if (index == 0 && getRotation() == 3 || index == 1 && getRotation() == 2 || index == 2 && getRotation() == 1) {
            return new int[]{-1, 0};
        }
        return new int[]{0, 0};
    }

    @Override
    public String name() {
        return "Chambers of Xeric: Ice Demon room";
    }

    /**
     * The brazier object.
     */
    @EqualsAndHashCode(callSuper = true)
    public static final class Brazier extends WorldObject {

        /**
         * The animation that the icefiends perform when trying to extinguish a brazier.
         */
        private static final Animation defreeze = new Animation(1582);

        /**
         * The sound effect that is played when the ice demon is being unfrozen.
         */
        private static final SoundEffect defreezeSound = new SoundEffect(531, 5, 0);
        /**
         * The icefiend guarding this brazier.
         */
        private final IcefiendNPC icefiend;
        /**
         * The ice demon room.
         */
        private final IceDemonRoom room;
        /**
         * The amount of kindling within this brazier.
         */
        @Getter
        @Setter
        private int kindling;
        /**
         * A dummy variable used to properly delay the frequency of the extinguishing.
         */
        private int ticks;

        public Brazier(final IceDemonRoom room, final int kindling, final IcefiendNPC icefiend, final int id, final int type,
                       final int rotation, final Location tile) {
            super(id, type, rotation, tile);
            this.kindling = kindling;
            this.icefiend = icefiend;
            this.room = room;
        }

        /**
         * Processes the brazier fire, removing kindling from it every time, until the fire eventually runs out of it and extinguishes.
         */
        public void process() {
            val amount = Math.min(room.getRaid().getOriginalPlayers().size(), kindling);
            kindling -= amount;
            if (icefiend != null && ticks++ % 4 == 0) {
                icefiend.setAnimation(defreeze);
                World.sendSoundEffect(icefiend.getLocation(), defreezeSound);
            }
            room.demon.removeLifepoints(amount * (icefiend == null ? 2F : 1.5F));
            if (kindling <= 0) {
                room.extinguishBrazier(this);
                ticks = 0;
            }
        }

    }

}
