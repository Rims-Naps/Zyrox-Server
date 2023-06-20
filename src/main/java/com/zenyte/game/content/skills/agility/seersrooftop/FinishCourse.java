package com.zenyte.game.content.skills.agility.seersrooftop;

import com.zenyte.game.content.achievementdiary.diaries.KandarinDiary;
import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class FinishCourse implements Obstacle {

    private static final Location FINISH = new Location(2704, 3464, 0);

    @Override
    public int getLevel(WorldObject object) {
        return 60;
    }

    @Override
    public int[] getObjectIds() {
        return new int[] { 11377 };
    }

    @Override
    public int getDuration(boolean success, WorldObject object) {
        return 2;
    }

    @Override
    public void startSuccess(Player player, WorldObject object) {
        player.setFaceLocation(FINISH);
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks;

            @Override
            public void run() {
                if (ticks == 0)
                    player.setAnimation(Animation.LEAP);
                else if (ticks == 1) {
                    player.getDailyChallengeManager().update(SkillingChallenge.COMPLETE_LAPS_SEERS_COURSE);
                    player.getAchievementDiaries().update(KandarinDiary.COMPLETE_SEERS_VILLAGE_AGILITY_COURSE_LAP);
                    player.setAnimation(Animation.LAND);
                    player.setLocation(FINISH);
                    MarkOfGrace.spawn(player, SeersRooftopCourse.MARK_LOCATIONS, 60, 20);
                    player.addAttribute("Seers_lap_count", player.getNumericAttribute("Seers_lap_count").intValue() + 1);
                    val laps = player.getNumericAttribute("Seers_lap_count").intValue();
                    player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Seers Rooftop Course.");
                    if (Utils.random(4000) == 0) {
                        val item = new Item(ItemId.RING_OF_ENDURANCE_UNCHARGED);
                        player.getInventory().addOrDrop(item);
                        WorldBroadcasts.broadcast(player, BroadcastType.RINGOFENDURANCE, item.getId(), "Seers' Village");
                    }
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public double getSuccessXp(WorldObject object) {
        return 435;
    }
}
