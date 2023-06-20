package com.zenyte.game.content.skills.runecrafting.abyss;

import static com.zenyte.game.content.skills.runecrafting.abyss.ObstacleType.BOIL;
import static com.zenyte.game.content.skills.runecrafting.abyss.ObstacleType.EYES;
import static com.zenyte.game.content.skills.runecrafting.abyss.ObstacleType.GAP;
import static com.zenyte.game.content.skills.runecrafting.abyss.ObstacleType.ROCKS;
import static com.zenyte.game.content.skills.runecrafting.abyss.ObstacleType.TENDRILS;
import static com.zenyte.game.content.skills.runecrafting.abyss.ObstacleType.PASSAGE;

import java.util.HashMap;
import java.util.Map;

import com.zenyte.game.world.entity.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 29 jul. 2018 | 21:30:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public enum AbyssObstacle {
	
	TENDRILS1(26253, new Location(3051, 4823, 0), TENDRILS),
	TENDRILS2(26189, new Location(3028, 4824, 0), TENDRILS),
	ROCKS1(26188, new Location(3031, 4820, 0), ROCKS),
	ROCKS2(26574, new Location(3049, 4821, 0), ROCKS),
	BOIL1(26190, new Location(3024, 4834, 0), BOIL),
	BOIL2(26252, new Location(3054, 4831, 0), BOIL),
	EYES1(26191, new Location(3029, 4842, 0), EYES),
	EYES2(26251, new Location(3051, 4839, 0), EYES),
	GAP1(26192, new Location(3029, 4842, 0), GAP),
	GAP2(26250, new Location(3048, 4842, 0), GAP),
	PASSAGE1(26208, new Location(3038, 4846, 0), PASSAGE);
	
	@Getter private final int id;
	@Getter private final Location destination;
	@Getter private final ObstacleType type;
	
	public static final AbyssObstacle[] VALUES = values();
	private static final Map<Integer, AbyssObstacle> OBSTACLES = new HashMap<>(VALUES.length);
	
	static {
		for (val obstacle : VALUES) {
			OBSTACLES.put(obstacle.getId(), obstacle);
		}
	}
	
	public static AbyssObstacle get(final int id) {
		return OBSTACLES.get(id);
	}

}
