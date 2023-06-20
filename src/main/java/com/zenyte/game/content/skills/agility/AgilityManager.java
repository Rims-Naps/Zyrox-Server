package com.zenyte.game.content.skills.agility;

import com.zenyte.game.content.skills.agility.alkharidrooftop.AlKharidRooftopCourse;
import com.zenyte.game.content.skills.agility.ardougnerooftop.ArdougneRooftopCourse;
import com.zenyte.game.content.skills.agility.barbariancourse.BarbarianOutpostCourse;
import com.zenyte.game.content.skills.agility.canifisrooftop.CanifisRooftopCourse;
import com.zenyte.game.content.skills.agility.draynorrooftop.DraynorRooftopCourse;
import com.zenyte.game.content.skills.agility.faladorrooftop.FaladorRooftopCourse;
import com.zenyte.game.content.skills.agility.gnomecourse.GnomeCourse;
import com.zenyte.game.content.skills.agility.pollnivneach.PollnivneachRooftopCourse;
import com.zenyte.game.content.skills.agility.pyramid.AgilityPyramid;
import com.zenyte.game.content.skills.agility.rellekkarooftop.RellekkaRooftopCourse;
import com.zenyte.game.content.skills.agility.seersrooftop.SeersRooftopCourse;
import com.zenyte.game.content.skills.agility.varrockrooftop.VarrockRooftopCourse;
import com.zenyte.game.content.skills.agility.wildernesscourse.WildernessCourse;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kris | 21. dets 2017 : 17:39.51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class AgilityManager {

	private static final Class<?>[] COURSES = new Class<?>[]{
			GnomeCourse.class, BarbarianOutpostCourse.class, WildernessCourse.class,
			DraynorRooftopCourse.class, AlKharidRooftopCourse.class, VarrockRooftopCourse.class,
			CanifisRooftopCourse.class, FaladorRooftopCourse.class, SeersRooftopCourse.class,
			RellekkaRooftopCourse.class, ArdougneRooftopCourse.class, AgilityPyramid.class, PollnivneachRooftopCourse.class
	};

	public static final IntSet courseStarters = new IntOpenHashSet();
	
	public static final Map<Integer, CourseObstacle> COURSE_OBSTACLES = new HashMap<>();
	
	public static boolean calculateSuccess(final Player player, final WorldObject object, final Obstacle obstacle) {
		if(!(obstacle instanceof Failable))
			return true;
		if (obstacle instanceof Irreversible) {
			val irreversibleObstacle = (Irreversible) obstacle;
			if (irreversibleObstacle.failOnReverse() && irreversibleObstacle.checkForReverse(player, object)) {
				return false;
			}
		}
		return obstacle.successful(player, object);
	}
	
	public static void init() {
		for (final Class<?> courseClass : COURSES) {
			try {
				int index = 0;
				final AgilityCourse course = (AgilityCourse) courseClass.newInstance();
				for (final Class<?> obstacleClass : course.getObstacles()) {
					final Obstacle obstacle = (Obstacle) obstacleClass.newInstance();
					final CourseObstacle courseObstacle = new CourseObstacle(course, obstacle, ++index);
					for (final int objectId : obstacle.getObjectIds()) {
					    if (COURSE_OBSTACLES.containsKey(objectId)) {
					        throw new IllegalStateException();
                        }
					    if (index == 1) {
                            courseStarters.add(objectId);
                        }
                        COURSE_OBSTACLES.put(objectId, courseObstacle);
                    }
				}
			} catch (final Exception e) {
                log.error(Strings.EMPTY, e);
			}
		}
	}
	
}
