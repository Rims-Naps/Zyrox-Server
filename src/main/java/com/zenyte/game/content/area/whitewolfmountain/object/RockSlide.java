package com.zenyte.game.content.area.whitewolfmountain.object;

import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class RockSlide implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Investigate")) {
            player.sendMessage("These rocks contain nothing interesting. They are just in the way.");
            return;
        }
        if (option.equals("Mine")) {
            if (!MiningDefinitions.PickaxeDefinitions.get(player, true).isPresent()) {
                player.sendMessage("You need a pickaxe to clear the rockslide. You do not have one that you have the Mining level to use.");
                return;
            }
            val pickaxe = MiningDefinitions.PickaxeDefinitions.get(player, true).get();
            WorldTasksManager.schedule(new TickTask() {
                @Override
                public void run() {
                    if (ticks == 0) {
                        final Animation miningAnim = pickaxe.getDefinitions().getAnim();
                        player.setAnimation(miningAnim);
                    }
                    if (ticks == 2) {
                        World.removeObject(object);
                        World.spawnObject(new WorldObject(ObjectId.ROCKSLIDE_11191,10,1,object.getPosition()));
                    }
                    if (ticks == 4) {
                        World.removeObject(World.getObjectWithId(object.getPosition(), ObjectId.ROCKSLIDE_11191));
                        World.spawnObject(new WorldObject(ObjectId.ROCKSLIDE_11193,10,1,object.getPosition()));
                    }
                    if (ticks == 6) {
                        World.removeObject(World.getObjectWithId(object.getPosition(), ObjectId.ROCKSLIDE_11193));
                        player.setAnimation(Animation.STOP);
                        player.setRun(false);
                        if (player.getX() <= 2837) {
                            player.addWalkSteps(2840, 3517);
                        } else if (player.getX() == 2839) {
                            player.addWalkSteps(2839, 3517);
                            player.addWalkSteps(2837, 3518);
                        } else {
                            player.addWalkSteps(2837, 3518);
                        }
                    }
                    if (ticks == 10) {
                        player.setRun(true);
                        World.spawnObject(new WorldObject(ObjectId.ROCK_SLIDE,10,1,object.getPosition()));
                        stop();
                    }
                    ticks++;
                }
            },0,0);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {2634};
    }
}