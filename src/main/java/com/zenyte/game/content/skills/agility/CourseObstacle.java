package com.zenyte.game.content.skills.agility;

import com.zenyte.game.content.skills.agility.barbariancourse.BarbarianOutpostCourse;
import com.zenyte.game.content.skills.agility.pyramid.AgilityPyramid;
import com.zenyte.game.content.skills.agility.pyramid.Doorway;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 21. dets 2017 : 18:54.18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class CourseObstacle {

    public CourseObstacle(final AgilityCourse course, final Obstacle obstacle, final int index) {
        this.course = course;
        this.obstacle = obstacle;
        this.index = index;
    }

    @Getter
    private final Obstacle obstacle;
    @Getter
    private final int index;
    @Getter
    private final AgilityCourse course;

    public void run(final Player player, final WorldObject object) {
        int stage = player.getNumericAttribute("courseStage").intValue();
        int tempStage = 0;
        if (index == 1)
            tempStage = 1;
        else if (stage == index - 1)
            tempStage = index;
        else
            tempStage = 0;
        player.getTemporaryAttributes().put("courseStage", tempStage);
        if (course instanceof BarbarianOutpostCourse) {
            obstacle.handle(player, object,
					index == course.getObstacles().length && (object.getX() == 2542 && object.getId() == 1948) ?
							course.getAdditionalCompletionXP() : 0);
        } else if (course instanceof AgilityPyramid) {
            val completionExp = Math.min(AgilityPyramid.MAX_COMPLETION_BONUS,
                    300 + player.getSkills().getLevelForXp(Skills.AGILITY) * 8);
            obstacle.handle(player, object,
					(obstacle instanceof Doorway) ? completionExp : 0);
        } else
            obstacle.handle(player, object, index == course.getObstacles().length && (stage == index - 1) ? course.getAdditionalCompletionXP() : 0);
    }

}
