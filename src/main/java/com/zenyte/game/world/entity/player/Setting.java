package com.zenyte.game.world.entity.player;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.PaneType;
import lombok.Getter;
import lombok.val;

import static com.zenyte.game.constants.GameInterface.ORBS;

/**
 * An enum containing the different saved settings for player. If the setting
 * constant has a runnable attached to it, it won't be refreshed within the loop
 * in {@link Settings#refresh()}.
 * 
 * @author Kris | 16. veebr 2018 : 21:22.33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
public enum Setting {

	CHATBOX_TRANSPARENT(4608, false),
	CHATBOX_SCROLLBAR(6374, false),
	DISPLAY_REMAINING_XP(4181, false),
	DISPLAY_PRAYER_TOOLTIPS(5711, false),
	DISPLAY_SPECIAL_BAR_TOOLTIPS(5712, false),
	CLOSE_TABS_WITH_HOTKEYS(4611, false),
	TABS_TRANSPARENT(4609, false),
	CLICK_THROUGH_CHATBOX(2570, false),
	DATA_ORBS(4084, false, (player, state) -> {
		if (!state) {
			ORBS.open(player);
			if (player.getSettings().valueOf(Setting.valueOf("EXPERIENCE_TRACKER")) == 1) {
                GameInterface.EXPERIENCE_DROPS_WINDOW.open(player);
			}
		} else {
			player.getInterfaceHandler().closeInterface(InterfacePosition.XP_TRACKER);
			player.getInterfaceHandler().closeInterface(InterfacePosition.ORBS);
		}
	}),
	SIDE_PANELS(4607, false, (player, state) -> {
		  val toPane = player.getInterfaceHandler().isResizable() ? state ? PaneType.SIDE_PANELS : PaneType.RESIZABLE : PaneType.FIXED;
		  if (player.getInterfaceHandler().isResizable()) {
			player.getInterfaceHandler().sendPane(player.getInterfaceHandler().getPane(), toPane);
		  }
		  player.getVarManager().sendBit(4607, state ? 1 : 0);
	}),
	WILDERNESS_KD(4143, false, (player, state) -> player.getVarManager().sendBit(4143, state ? 1 : 0)),
    LOOT_DROP_NOTIFICATIONS(5399, false),
	UNTRADEABLE_DROP_NOTIFICATIONS(5402, false),
    LOOT_DROP_WARNING_NOTIFICATIONS(5411, false),
    TRESHOLD_DROP_WARNING_VALUE(5412, false),
	FILTERED_BOSS_KILLCOUNT_UPDATES(4930, false),
	THRESHOLD_DROP_VALUE(5400, false),
	SPLIT_PRIVATE_CHAT(287, true, (player, state) -> player.getPacketDispatcher().sendClientScript(83, new Object[0])),
	HIDE_PRIVATE_CHAT(4089, false),
	CHAT_EFFECTS(171, true),
	PROFANITY_FILTER(1074, true),
	MOUSE_BUTTONS(170, true),
	LOGIN_NOTIFICATIONS(1627, false),
	SHIFT_CLICK_DROPPING(-1, false, (player, state) -> player.getVarManager().sendBit(5542, state ? 1 : 0)),
    MOBILE_SHIFT_CLICK_DROPPING(5542, false, (player, state) -> player.getVarManager().sendBit(5542, state ? 1 : 0)),
	TOGGLE_FUNCTION_BUTTON(6257, false),
	TOGGLE_STORE_ORB(6506, false),
	PET_OPTIONS(5599, false),
	MIDDLE_MOUSE_BUTTON(4134, false),
	SCREEN_BRIGHTNESS(166, true),
	FPS_CONTROL(6364, false),
	MOBILE_DEPOSIT_BOX_AMOUNT(4430, false),
    MINIMIZE_MINIMAP(6254, false, (player, state) -> {
        player.getVarManager().sendVarInstant(1021, player.getVarManager().getValue(1021));
        ORBS.open(player);
    }),
	MUSIC_VOLUME(168, true),
	SOUND_EFFECT_VOLUME(169, true),
	AREA_SOUND_VOLUME(872, true),
	PLAYER_ATTACK_OPTION(1107, true),
	NPC_ATTACK_OPTION(1306, true),
	GODWARS_ENTRANCE_ROPE(3966, false),
	SARADOMIN_TOP_ROPE(3967, false),
	SARADOMIN_BOTTOM_ROPE(3968, false),
	SARADOMIN_LIGHT(4733, false),
    EXPERIENCE_TRACKER(4702, false, (player, state) -> {
        if (state) {
            GameInterface.EXPERIENCE_DROPS_WINDOW.open(player);
            //player.getPacketDispatcher().sendClientScript(10400, 5, 40); tourny overlay
        } else {
            player.getInterfaceHandler().closeInterface(InterfacePosition.XP_TRACKER);
			//player.getPacketDispatcher().sendClientScript(10400, 5, 5); tourny overlay
        }
    }),
	WILDERNESS_DITCH,
	EDGEVILLE_RESPAWN_POINT,
	PUBLIC_FILTER,
	PRIVATE_FILTER,
	TRADE_FILTER,
	GAME_FILTER(26, false),
	CLAN_FILTER(1054, true),
	ACCEPT_AID(4180, false),
	HARD_LEATHER_TAN,
	ROW_CURRENCY_COLLECTOR,
    BONECRUSHING_INACTIVE,
	ASH_SANCTIFIER_INACTIVE,
	AUTO_MUSIC(18, true),
	LOOP_MUSIC(4137, false),
	BOUNTY_TARGET_TELEPORT_WARNING(236, false),
	DAREEYAK_TELEPORT_WARNING(6284, false),
	CARRALLANGAR_TELEPORT_WARNING(6285, false),
	ANNAKARL_TELEPORT_WARNING(6286, false),
	GHORROCK_TELEPORT_WARNING(6287, false),
	VARROCK_TELEPORT_CONFIGURATION(4585, false),
	CAMELOT_TELEPORT_CONFIGURATION(4560, false),
	WATCHTOWER_TELEPORT_CONFIGURATION(4548, false),
	KALPHITE_LAIR_TUNNEL_ENTRANCE(4586, false),
	KALPHITE_QUEEN_TUNNEL_ENTRANCE(4587, false),
	BIGGER_AND_BADDER_SLAYER_REWARD(5362, false),
	STOP_THE_WYVERN_SLAYER_REWARD(6251, false),
    SHOW_COMBAT_SPELLS(6605, false),
    SHOW_TELEPORT_SPELLS(6609, false),
    SHOW_UTILITY_SPELLS(6606, false),
    SHOW_SPELLS_YOU_LACK_THE_MAGIC_LEVEL_TO_CAST(6607, false),
    SHOW_SPELLS_YOU_LACK_THE_RUNES_TO_CAST(6608, false),
    SPELL_FILTERING(6718, false),
	UNLOCKED_RUNEFEST_HOME_TELEPORT_ANIMATION(1771, false),
	USING_RUNEFEST_TELEPORT_ANIMATION,
	RIGOUR(5451, false),
	AUGURY(5452, false),
	PRESERVE(5453, false),
	FRIEND_LIST_TOGGLE(6516, false, (player, state) -> player.getInterfaceHandler().sendInterface(InterfacePosition.FRIENDS_TAB, state ? 432 : 429)),
	MOBILE_FUNCTION_BUTTON_ENABLED(6256, false),
	MOBILE_FUNCTION_BUTTON_SETTING(6255, false),
	MOBILE_SHOP_QUANTITY(6348, false);
	
	@Getter private final int id;
	@Getter
	private final boolean varp;
	@Getter
	private final SettingRunnable runnable;

	public static final Setting[] SETTINGS = values();

	Setting() {
		this(-1, false, null);
	}

    Setting(final SettingRunnable runnable) {
        this(-1, false, runnable);
    }

	Setting(final int varbitId, final boolean varp) {
		this(varbitId, varp, null);
	}

	Setting(final int varbitId, final boolean varp, final SettingRunnable runnable) {
		id = varbitId;
		this.varp = varp;
		this.runnable = runnable;
	}

	@Override
	public String toString() {
		return name();
	}

	interface SettingRunnable {

		void run(final Player player, final boolean state);

	}

}
