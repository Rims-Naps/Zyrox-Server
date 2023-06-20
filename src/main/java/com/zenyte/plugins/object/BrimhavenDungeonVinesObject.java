package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.diaries.KaramjaDiary;
import com.zenyte.game.content.skills.woodcutting.actions.Woodcutting;
import com.zenyte.game.packet.out.LocAdd;
import com.zenyte.game.packet.out.LocDel;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import lombok.var;

/**
 * @author Tommeh | 24-3-2019 | 23:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class BrimhavenDungeonVinesObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val level = object.getId() == 21731 ? 10 : 34;
        if (player.getSkills().getLevel(Skills.WOODCUTTING) < level) {
            player.sendMessage("You need a Woodcutting level of at least " + level + " to cut the vines.");
            return;
        }
        if (player.isUnderCombat()) {
            player.sendMessage("You need to be out of combat to do this.");
            return;
        }
        val optionalAxeResult = Woodcutting.getAxe(player);
        if (!optionalAxeResult.isPresent()) {
            player.sendMessage("You need an axe to cut the vines.");
            return;
        }
        val rotation = object.getRotation();
        val axe = optionalAxeResult.get().getDefinitions();
        player.lock();
        player.setAnimation(axe.getEmote());
        player.setRunSilent(true);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                var toX = player.getX();
                var toY = player.getY();
                if (rotation == 1 || rotation == 3) {
                    if (player.getX() < object.getX()) {
                        toX += 2;
                    } else {
                        toX -= 2;
                    }
                }
                if (rotation == 0 || rotation == 2) {
                    if (player.getY() < object.getY()) {
                        toY += 2;
                    } else {
                        toY -= 2;
                    }
                }
                if (ticks == 3) {
                    if (Utils.random(5) == 0) {
                        player.applyHit(new Hit(1, HitType.REGULAR));
                        player.sendMessage("You fail to cut the vines.");
                    } else {
                        player.sendZoneUpdate(object.getX(), object.getY(), new LocDel(object));
                        player.addWalkSteps(toX, toY, 2,false);
                        player.sendMessage("You cut through the vines.");
                    }
                } else if (ticks == 4) {
                    player.setRunSilent(false);
                    player.getAchievementDiaries().update(KaramjaDiary.CHOP_THE_BRIMHAVEN_DUNGEON_VINES);
                    player.unlock();
                } else if (ticks == 6) {
                    player.sendZoneUpdate(object.getX(), object.getY(), new LocAdd(object));
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 21731, 21732, 21733, 21734, 21735 };
    }
}
