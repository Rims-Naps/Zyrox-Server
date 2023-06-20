package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class AbandonedMinedLiftObject implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equals("Go-up")) {
            player.lock(6);
            WorldTasksManager.schedule(new WorldTask(){
                int ticks;
                @Override
                public void run() {
                    if (ticks == 0) {
                        player.setRunSilent(3);
                        player.addWalkSteps(player.getX(), player.getY() + 4, -1, false);
                    } else if(ticks == 1) {
                        player.getAppearance().setRenderAnimation(new RenderAnimation(777, 776, -1));
                        player.getPacketDispatcher().sendGraphics(new Graphics(68), new Location(2725, 4453, 0));
                        player.sendSound(new SoundEffect(1610));
                    }else if(ticks == 4) {
                        player.setLocation(new Location(2807, 4493, 0));
                        player.getAppearance().setRenderAnimation(new RenderAnimation(808, 819, 824));
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        } else if(option.equals("Go-down")) {
            player.lock(6);
            WorldTasksManager.schedule(new WorldTask(){
                int ticks;
                @Override
                public void run() {
                    if(ticks == 0) {
                        player.sendMessage("The lift descends further into the mines...", MessageType.FILTERABLE);
                        player.sendSound(new SoundEffect(1610));
                        player.setFaceLocation(new Location(player.getX(), player.getY() - 4, 0));
                    } else if(ticks == 1) {
                        player.setLocation(new Location(2725, 4456, 0));
                        player.getAppearance().setRenderAnimation(new RenderAnimation(777, 776, -1));
                    } else if (ticks == 2) {
                        player.sendMessage("...plunging you straight into the middle of a chamber flooded with water.", MessageType.FILTERABLE);
                        player.setRunSilent(4);
                        player.addWalkSteps(player.getX(), player.getY() - 4, -1, false);
                    } else if(ticks == 5) {
                        player.getAppearance().setRenderAnimation(new RenderAnimation(808, 819, 824));
                        stop();
                    }
                    ticks++;
                }
            }, 0, 0);
        }
    }

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId,
                       final String option) {
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object,
                object.getId() == 4942 ? 3 : 0), getRunnable(player, object, name, optionId,
                option), object.getId() == 4942 ? 1 : getDelay()));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4942, 4937, 4938, 4940 };
    }
}
