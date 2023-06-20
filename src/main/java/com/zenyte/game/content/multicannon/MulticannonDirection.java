package com.zenyte.game.content.multicannon;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.region.RSPolygon;
import lombok.Getter;
import lombok.val;

import java.util.EnumMap;
import java.util.Map;

/**
 * An enumeration containing all the animations for each direction
 * as well as the angle of the next rotation.
 * @author Kris | 13. okt 2017 : 13:05.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public enum MulticannonDirection {

	NORTH_WEST(new Animation(514), new int[][]{{-18, 18}, {-18, 7}, {-7, 7}, {-7, 5}, {-6, 5}, {-6, 4}, {-5, 4}, {-5, 3}, {-3, 3}, {-3, 1}, {-3, 1}, {0, 1}, {0, 4}, {-2, 4}, {-2, 6}, {-3, 6}, {-3, 7}, {-4, 7}, {-4, 8}, {-6, 8}, {-6, 18}}),
	NORTH(new Animation(515), new int[][]{{-5, 19}, {-5, 8}, {-2, 8}, {-2, 7}, {-4, 7}, {-4, 6}, {-3, 6}, {-3, 5}, {-2, 5}, {-2, 4}, {-1, 4}, {-1, 2}, {2, 2}, {2, 4}, {3, 4}, {3, 5}, {4, 5}, {4, 6}, {5, 6}, {5, 7}, {3, 7}, {3, 8}, {6, 8}, {6, 19}}),
    NORTH_EAST(new Animation(516), new int[][]{{18, 19}, {7, 19}, {7, 8}, {5, 8}, {5, 7}, {4, 7}, {4, 6}, {3, 6}, {3, 4}, {1, 4}, {1, 4}, {1, 1}, {4, 1}, {4, 3}, {6, 3}, {6, 4}, {7, 4}, {7, 5}, {8, 5}, {8, 7}, {18, 7}}),
	EAST(new Animation(517), new int[][]{{19, 6}, {8, 6}, {8, 3}, {7, 3}, {7, 5}, {6, 5}, {6, 4}, {5, 4}, {5, 3}, {4, 3}, {4, 2}, {2, 2}, {2, -1}, {4, -1}, {4, -2}, {5, -2}, {5, -3}, {6, -3}, {6, -4}, {7, -4}, {7, -2}, {8, -2}, {8, -5}, {19, -5}}),
    SOUTH_EAST(new Animation(518), new int[][]{{19, -17}, {19, -6}, {8, -6}, {8, -4}, {7, -4}, {7, -3}, {6, -3}, {6, -2}, {4, -2}, {4, 0}, {4, 0}, {1, 0}, {1, -3}, {3, -3}, {3, -5}, {4, -5}, {4, -6}, {5, -6}, {5, -7}, {7, -7}, {7, -17}}),
	SOUTH(new Animation(519), new int[][]{{6, -18}, {6, -7}, {3, -7}, {3, -6}, {5, -6}, {5, -5}, {4, -5}, {4, -4}, {3, -4}, {3, -3}, {2, -3}, {2, -1}, {-1, -1}, {-1, -3}, {-2, -3}, {-2, -4}, {-3, -4}, {-3, -5}, {-4, -5}, {-4, -6}, {-2, -6}, {-2, -7}, {-5, -7}, {-5, -18}}),
    SOUTH_WEST(new Animation(520), new int[][]{{-17, -18}, {-6, -18}, {-6, -7}, {-4, -7}, {-4, -6}, {-3, -6}, {-3, -5}, {-2, -5}, {-2, -3}, {0, -3}, {0, -3}, {0, 0}, {-3, 0}, {-3, -2}, {-5, -2}, {-5, -3}, {-6, -3}, {-6, -4}, {-7, -4}, {-7, -6}, {-17, -6}}),
	WEST(new Animation(521), new int[][]{{-18, -5}, {-7, -5}, {-7, -2}, {-6, -2}, {-6, -4}, {-5, -4}, {-5, -3}, {-4, -3}, {-4, -2}, {-3, -2}, {-3, -1}, {-1, -1}, {-1, 2}, {-3, 2}, {-3, 3}, {-4, 3}, {-4, 4}, {-5, 4}, {-5, 5}, {-6, 5}, {-6, 3}, {-7, 3}, {-7, 6}, {-18, 6}});

    @Getter private final Animation animation;
	@Getter private final int[][] polygonPoints;

	public static final MulticannonDirection[] values = values();

    MulticannonDirection(final Animation animation, final int[][] polygonPoints) {
		this.animation = animation;
		this.polygonPoints = polygonPoints;
	}

	static Map<MulticannonDirection, RSPolygon> create(final Location center) {
        val map = new EnumMap<MulticannonDirection, RSPolygon>(MulticannonDirection.class);

        val x = center.getX();
        val y = center.getY();
        val z = center.getPlane();
        for (int i = values.length - 1; i >= 0; i--) {
            val direction = values[i];
            val translatedPoints = translate(x, y, direction.getPolygonPoints());
            map.put(direction, new RSPolygon(translatedPoints, z));
        }
        return map;
    }

    private static int[][] translate(final int xOffset, final int yOffset, final int[][] coordinates) {
        val translated = new int[coordinates.length][2];
        for (int i = translated.length - 1; i >= 0; i--) {
            translated[i][0] = coordinates[i][0] + xOffset;
            translated[i][1] = coordinates[i][1] + yOffset;
        }
        return translated;
    }

}
