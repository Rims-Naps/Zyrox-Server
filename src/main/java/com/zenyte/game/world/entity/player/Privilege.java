package com.zenyte.game.world.entity.player;

import com.zenyte.game.util.TextUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
/**
 * @author Tommeh | 5-4-2019 | 16:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum
Privilege {

	PLAYER(0, "", "000000"),
	BOOSTER(0, "<img=21>", "BA55D3"), //change icon back to 0
    YOUTUBER(6, "<img=7>", "ff0000")
			{
				@Override
				public String toString() {
					return "QA Team";
				}
			},
	HIDDEN_ADMINISTATOR(0, "", "000000"),
	MEMBER(0, "", ""),
	JUNIOR_MODERATOR(7, "<img=4>", "00b8ff") {
		@Override
		public String toString() {
			return "Junior Moderator";
		}
	},
    FORUM_MODERATOR(9, "<img=6>", "cc6eee"),
    MODERATOR(1, "<img=0>", "c6cad1"),

	GLOBAL_MODERATOR(8, "<img=5>", "5bf45b") {
	    @Override
        public String toString() {
	        return "Global Moderator";
        }
    },
	ADMINISTRATOR(2, "<img=1>", "e4df28"),
    SPAWN_ADMINISTRATOR(2, "<img=1>", "3ded97") {
	    @Override
        public String toString() {
	        return "Management";
        }
    },

	;

	private final int icon; //client ordinal
	private final String crown;
	private final String yellColor;

	public boolean eligibleTo(final Privilege p) {
		return ordinal() >= p.ordinal();
	}
	
	@Override
	public String toString() {
		return TextUtils.formatName(name().toLowerCase().replaceAll("_", " "));
	}
}
