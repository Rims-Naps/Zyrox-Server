package com.zenyte.game.content.skills.woodcutting;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;

import lombok.Getter;

/**
 * @author Kris | 11. dets 2017 : 4:25.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum AxeDefinitions {
	NO_LEVEL(-1, -1, -1, -1),
	BRONZE(1351, 1, 6, 879),
	IRON(1349, 1, 6, 877),
	STEEL(1353, 5, 5, 875),
	BLACK(1361, 11, 5, 873),
	MITHRIL(1355, 21, 4, 871),
	ADAMANT(1357, 31, 3, 869),
	RUNE(1359, 41, 2, 867),
	GILDED(23279, 41, 2, 8303),
	DRAGON(6739, 61, -1, 2846) {
		public int getCutTime() {
			return Utils.random(1, 2);
		}
	},
	THIRD_AGE(20011, 61, -1, 7264) {
        public int getCutTime() {
            return Utils.random(1, 2);
        }
    },
	UNCHARGED_INFERNAL(13242, 61, -1, 2117) {
		public int getCutTime() {
			return Utils.random(1, 2);
		}
	},
	INFERNAL(13241, 61, -1, 2117) {
        public int getCutTime() {
            return Utils.random(1, 2);
        }
    },
	CRYSTAL(30718, 71, 1, 10001),
    ;

	@Getter private final int itemId, levelRequired, cutTime;
	@Getter private final Animation emote;
	
	private static final AxeDefinitions[] ENUM_VALUES = values();
	
	public static final AxeDefinitions[] VALUES = new AxeDefinitions[ENUM_VALUES.length - 1];
	
	static {
		int index = 0;
		for (int i = ENUM_VALUES.length - 1; i >= 1; i--) {
			VALUES[index++] = ENUM_VALUES[i];
		}
	}

	AxeDefinitions(final int itemId, final int levelRequried, final int axeTime, final int emoteId) {
		this.itemId = itemId;
		levelRequired = levelRequried;
		cutTime = axeTime;
		emote = new Animation(emoteId);
	}

}
