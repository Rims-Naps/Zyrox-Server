package com.zenyte.game.world.entity.player.var;

import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import static com.zenyte.game.world.entity.player.var.EventType.POST_LOGIN;
import static com.zenyte.game.world.entity.player.var.VarType.VAR;
import static com.zenyte.game.world.entity.player.var.VarType.VARBIT;

/**
 * @author Kris | 27. juuli 2018 : 18:05:44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum VarCollection {

	GROTESQUE_GUARDIAN_KC_FOR_BELL(VAR, 1669, p -> p.getNotificationSettings().getKillcount("grotesque guardians"), POST_LOGIN),
	BRITTLE_ENTRANCE_UNLOCKED(VARBIT, 2468, p -> attribute(p, "brittle-entrance_unlocked"), POST_LOGIN),
	TWO_FACTOR_AUTHENTICATION_ENABLED(VAR, 3505, p -> p.getAuthenticator().isEnabled() ? 1 : 0, POST_LOGIN),
	LEVEL_UP_DIALOGUES(VAR, 3601, p -> attribute(p, "LEVEL_UP_DIALOGUES", 99), POST_LOGIN),
	RARE_DROP_BROADCASTS(VAR, 3602, p -> attribute(p, "RARE_DROP_BROADCASTS"), POST_LOGIN),
	LEVEL_99_BROADCASTS(VAR, 3603, p -> attribute(p, "LEVEL_99_BROADCASTS"), POST_LOGIN),
	MAXED_PLAYER_BROADCASTS(VAR, 3604, p -> attribute(p, "MAXED_PLAYER_BROADCASTS"), POST_LOGIN),
	MAX_SKILL_XP_BROADCASTS(VAR, 3605, p -> attribute(p, "MAX_SKILL_XP_BROADCASTS"), POST_LOGIN),
	PET_BROADCASTS(VAR, 3606, p -> attribute(p, "PET_BROADCASTS"), POST_LOGIN),

	HARDCORE_IRONMAN_DEATH_BROADCASTS(VAR, 3607, p -> attribute(p, "HARDCORE_IRONMAN_DEATH_BROADCASTS"), POST_LOGIN),
	CONFIRMATION_WHEN_NOTING_OR_UNNOTING(VAR, 3608, p -> attribute(p, "CONFIRMATION_WHEN_NOTING_OR_UNNOTING"), POST_LOGIN),
    REMEMBER_COMBAT_SETTINGS(VAR, 3609, p -> attribute(p, "REMEMBER_COMBAT_STYLES"), POST_LOGIN),
	DAILY_CHALLENGE_NOTIFICATIONS(VAR, 3610, p -> attribute(p, "DAILY_CHALLENGE_NOTIFICATIONS"), POST_LOGIN),
	HELPFUL_TIPS(VAR, 3614, p -> attribute(p, "HELPFUL_TIPS"), POST_LOGIN),
    HIDE_ITEMS(VAR, 3615, p -> attribute(p, "HIDE_ITEMS"), POST_LOGIN),
    MYSTER_BOX_RARES(VAR, 3616, p -> attribute(p, "MYSTER_BOX_RARES"), POST_LOGIN),
    SMASH_VIALS(VAR, 3617, p -> attribute(p, "SMASH_VIALS"), POST_LOGIN),
	FILTER_YELLS(VAR, 3618, p -> attribute(p, "YELL_FILTER"), POST_LOGIN),
	UPDATE_MESSAGE(VAR, 3619, p -> attribute(p, "UPDATE_MESSAGE"), POST_LOGIN),
	EXAMINE_NPCS(VAR, 3621, p -> attribute(p, "EXAMINE_NPCS"), POST_LOGIN),
	BROADCAST_TREASURE_TRAILS(VAR, 3623, p -> attribute(p, "BROADCAST_TREASURE_TRAILS"), POST_LOGIN),

	//var 3611-3613 are used for the tournament overlay
	//var 3622 is used for wheel of fortune
	//var 3625 is used for gwd instances
	TZHAAR_UNLOCKS(VARBIT, 5646, p -> p.getNumericAttribute("infernoVar").intValue(), POST_LOGIN),
	ACTIVE_JOURNAL(VARBIT, 8168, p -> p.getInterfaceHandler().getJournal().ordinal(), POST_LOGIN),
	DAILY_BATTLESTAVES_COLLECTED(VARBIT, 4539, p -> p.getVariables().isClaimedBattlestaves() ? 1 : 0, POST_LOGIN),
	DROP_VIEWER_FRACTIONS(VAR, 3600, p -> attribute(p, "drop_viewer_fractions", 1)),
	FLOUR_BIN(VARBIT, 5325, p -> attribute(p, "flourbin", 1), POST_LOGIN),
	BLAST_FURNACE_DISPENSER(VARBIT, 936, p -> attribute(p, "blast_furnace_dispenser", 3), POST_LOGIN),
	BLAST_FURNACE_COFFER(VARBIT, 5356, p -> attribute(p, "blast_furnace_coffer", 1), POST_LOGIN),
	DESERTED_KEEP_LEVER(VARBIT, 4470, p -> DiaryReward.WILDERNESS_SWORD1.eligibleFor(p) ? 1 : 0, POST_LOGIN),
	PHASMATYS_CHARTER_OPTION(VAR, 302, p -> 61, POST_LOGIN),
	PORT_TYRAS_CHARTER_OPTION(VAR, 328, p -> 15, POST_LOGIN),
	SHIPYARD_CHARTER_OPTION(VAR, 365, p -> 2, POST_LOGIN),
	MOS_LE_HARMLESS_CHARTER_OPTION(VAR, 655, p -> 140, POST_LOGIN),
	IRONMAN_MODE(VARBIT, 1777, p -> p.getGameMode().ordinal(), POST_LOGIN),
	PIN_IRONMAN_MODE(VARBIT, 1776, p -> 1, POST_LOGIN), //send as 1 until we got pins
	UNKNOWN_IRONMAN(VAR, 281, p -> 1000, POST_LOGIN),
	FOUNTAIN_OF_RUNE(VARBIT, 4145, p -> 1),
	ZEAH_FAIRY_RING(VARBIT, 4885, p -> 1, POST_LOGIN),
	DIALOGUE_FULL_SIZE(VARBIT, 5983, p -> 1),
    VYRE_WELL(VARBIT, 6455, p -> attribute(p, "vyre well charges") > 0 ? 1 : 0, POST_LOGIN),
	ACHIEVEMENT_DIARY_TAB(VARBIT, 3612, p -> 1),
	MINIGAMES_TAB(VARBIT, 3217, p -> 1),
	KOUREND_TAB(VARBIT, 618, p -> 1),
    FARMING_EQUIPMENT_STORAGE_DEFAULT(VARBIT, 7792, p -> attribute(p, "farming equipment default"), POST_LOGIN),
    ZULRAH_RECLAIM(VARBIT, 4391, p -> p.getRetrievalService().is(ItemRetrievalService.RetrievalServiceType.ZUL_GWENWYNIG) ? 3 : 0, POST_LOGIN),
    VORKATH_RECLAIM(VARBIT, 6108, p -> p.getRetrievalService().is(ItemRetrievalService.RetrievalServiceType.TORFINN) ? 25 : 24, POST_LOGIN),
	;

	VarCollection(final VarType type, final int id, final VarValueFunction function, final EventType... eventTypes) {
		this.type = type;
		this.id = id;
		this.function = function;
		this.eventTypes = eventTypes;
	}

	private static final VarCollection[] VALUES = values();

	private final VarType type;
	@Getter private final int id;
	private final VarValueFunction function;
	private final EventType[] eventTypes;
	
	private static final int attribute(final Player player, final String key) {
		return player.getNumericAttribute(key).intValue();
	}

	private static final int attribute(final Player player, final String key, final int maxValue) {
		return Math.min(player.getNumericAttribute(key).intValue(), maxValue);
	}

	public final void update(final Player player) {
		update(player, function.getValue(player));
	}

	public final void update(final Player player, final int value) {
		if (type == VAR) {
			player.getVarManager().sendVar(id, value);
		} else {
			player.getVarManager().sendBit(id, value);
		}
	}

	public static final void updateType(final Player player, final EventType type) {
		for (val var : VALUES) {
			if (ArrayUtils.contains(var.eventTypes, type)) {
				var.update(player);
			}
		}
	}

}
