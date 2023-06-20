package com.zenyte.plugins.item;

import com.zenyte.game.content.treasuretrails.challenges.GameObject;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.npc.impl.slayer.Molanisk;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.area.darkcaves.DorgeshKaanSouthernDungeonArea;

import java.util.List;

public class SlayerBell extends ItemPlugin {

    private Location[] MOLANISK_SPAWNS = {
            new Location(2732, 5221),
            new Location(2747, 5206),
            new Location(2740, 5209),
            new Location(2730, 5205),
            new Location(2712, 5193),
            new Location(2713, 5213),
            new Location(2704, 5215),
            new Location(2703, 5227),
            new Location(2727, 5234),
            new Location(2742, 5231),

    };

    private Location[] MOLANISK_PROJECTILES = {
            new Location(2732, 5222),
            new Location(2746, 5206),
            new Location(2740, 5208),
            new Location(2731, 5205),
            new Location(2713, 5193),
            new Location(2712, 5213),
            new Location(2704, 5216),
            new Location(2703, 5228),
            new Location(2727, 5235),
            new Location(2742, 5232),
    };

    @Override
    public void handle() {
        //22545 , 3522 sound
        bind("Ring", (player, item, slotId) -> {
            player.setAnimation(new Animation(6083));
            player.sendSound(3522);
            player.sendMessage("You ring the bell.");
            if (player.getArea() instanceof DorgeshKaanSouthernDungeonArea) {
                player.sendMessage("The bell resounds loudly throughout the cavern");
                for (int i = 0; i < MOLANISK_SPAWNS.length; i++) {
                    if (player.getLocation().withinDistance(MOLANISK_SPAWNS[i].getX(), MOLANISK_SPAWNS[i].getY(), 2)) {
                        final Location molanisk = MOLANISK_SPAWNS[i];
                        WorldObject obj = World.getObjectWithType(molanisk, 4);
                        if(obj != null && obj.getId() == 22545)
                        {
                            long nextSpawn = player.getNumericAttribute("nextMolaniskSpawn").longValue();
                            System.out.println("nextSpawn: " + nextSpawn + " currentMillis:" + System.currentTimeMillis());
                            if(nextSpawn == 0 || System.currentTimeMillis() > nextSpawn)
                            {
                                System.out.println("Before attribute added: " +  player.getNumericAttribute("nextMolaniskSpawn").longValue());
                                player.addAttribute("nextMolaniskSpawn", (long)System.currentTimeMillis() + 3000);
                                System.out.println("After attribute added: " +  player.getNumericAttribute("nextMolaniskSpawn").longValue());
                            } else {
                                player.sendMessage("You have recently lured a molanisk off the wall.");
                                return;
                            }
                        } else {
                            return;
                        }
                        if(player.isUnderCombat())
                        {
                            player.sendMessage("You're already fighting something!");
                            return;
                        }
                        final Location projectile = MOLANISK_PROJECTILES[i];
                        WorldTasksManager.schedule(new TickTask() {
                            int ticks = 0;
                            @Override
                            public void run() {
                                if(ticks == 0) {
                                    //remove old wall object final int id, final int type, final int rotation, final Location tile
                                    final WorldObject object = new WorldObject(22544, obj.getType(), obj.getRotation(), obj.getPosition());
                                    World.spawnObject(object);
                                    //send projectile
                                    World.sendProjectile(molanisk, projectile, new Projectile(1031, 40, 1, 0, 0,60,  1, 2));
                                    //schedule world task
                                    WorldTasksManager.schedule(new TickTask() {
                                        @Override
                                        public void run() {
                                            final WorldObject object = new WorldObject(22545, obj.getType(), obj.getRotation(), obj.getPosition());
                                            World.spawnObject(object);
                                            stop();
                                        }
                                    }, 20, 0);
                                }
                                if(ticks == 3) {
                                    NPC molanisk = new Molanisk(1, projectile, Direction.SOUTH, 3);
                                    molanisk.spawn();
                                    molanisk.setTarget(player);
                                    stop();
                                }
                             ticks++;
                            }
                        }, 0, 0);
                    }
                }
            }
        });

    }
    //LocAdd(id = 22544, slot = 1, rotation = 3, Location(x = 2703, y = 5227, z = 0)0)
    @Override
    public int[] getItems() {
        return new int[] {10952};
    }
}
