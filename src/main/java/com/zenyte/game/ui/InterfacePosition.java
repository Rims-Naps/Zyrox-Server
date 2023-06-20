package com.zenyte.game.ui;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 28 jan. 2018 : 23:38:10 | @author Kris | 22. sept 2018 : 20:34:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public enum InterfacePosition {

	CHATBOX(29, 162, true),
	PRIVATE_CHAT(9, 163, true),
	WILDERNESS_OVERLAY(4, true),
	ORBS(28, true),
	SKILLS_TAB(69, 320, true),
	JOURNAL_TAB_HEADER(70, 629, true),
	INVENTORY_TAB(71, 149, true),
	EQUIPMENT_TAB(72, 387, true),
	PRAYER_TAB(73, 541, true),
	SPELLBOOK_TAB(74, 218, true),
	ACCOUNT_MANAGEMENT(76, 109, true),
	FRIENDS_TAB(77, 429, true),
	LOGOUT_TAB(78, 182, true),
	SETTINGS_TAB(79, 261, true),
	EMOTE_TAB(80, 216, true),
	MUSIC_TAB(81, 239, true),
	CLAN_TAB(75, 7, true),
	COMBAT_TAB(68, 593, true),
	CENTRAL(13, false),
	DIALOGUE(561, false),
	MINIGAME_OVERLAY(6, true),
	OVERLAY(3, true),
	SINGLE_TAB(66, false),
	WORLD_MAP(14, true),
	UNKNOWN_OVERLAY(8, true),
	XP_TRACKER(7, true);

	/**
	 * An array containing all of the component types.
	 */
	public static final InterfacePosition[] VALUES = values();

	/**
	 * Gets the component pairs when moving from one game pane to another.
	 * 
	 * @param fromPane
	 *            the pane we're moving from.
	 * @param toPane
	 *            the pane we're moving to.
	 * @return a primitive int map containing all the pairs.
	 */
	public static Int2IntOpenHashMap getPairs(final PaneType fromPane, final PaneType toPane) {
		val pairs = new Int2IntOpenHashMap(VALUES.length);
		for (val position : VALUES) {
			if (position.equals(DIALOGUE)) {
				continue;
			}
			val from = position.getComponent(fromPane);
			val to = position.getComponent(toPane);
			if (from != -1 && to != -1) {
				pairs.put(from, to);
			}
		}
		return pairs;
	}

	/**
	 * Gets the component type for the respective component id and pane.
	 * 
	 * @param componentId
	 *            the resizable component id.
	 * @param pane
	 *            the pane to search.
	 * @return the respective component type, or null if not found.
	 */
	public static InterfacePosition getPosition(final int componentId, final PaneType pane) {
		for (val position : VALUES) {
			val e = pane.getEnum();
			val bitpacked = e.getIntValue(161 << 16 | componentId);
			if (componentId == (bitpacked & 0xFFFF)) {
				return position;
			}
		}
		return null;
	}

	@Getter
	private final int resizableComponent;

	@Getter
	private final int gameframeInterfaceId;
	
	@Getter
	private final boolean walkable;
	
	InterfacePosition(final int resizableComponent, final boolean walkable) {
		this(resizableComponent, -1, walkable);
	}

	/**
	 * Constructs the component types with the seed component of resizable, used to search the other types.
	 * 
	 * @param resizableComponent
	 *            the resizable component id, used as a seed.
	 * @param walkable
	 *            whether the component is walkable or not.
	 */
	InterfacePosition(final int resizableComponent, final int gameframeInterfaceId, final boolean walkable) {
		this.resizableComponent = resizableComponent;
		this.gameframeInterfaceId = gameframeInterfaceId;
		this.walkable = walkable;
	}

	/**
	 * Gets the component id for the respective pane based on the resizable type.
	 * 
	 * @param pane
	 *            the pane to seek.
	 * @return the component id
	 */
	public final int getComponent(final PaneType pane) {
		if (pane == null) {
			return -1;
		} else if (pane == PaneType.RESIZABLE) {
			return resizableComponent;
		}
		val e = pane.getEnum();
		val bitpacked = e.getIntValue(161 << 16 | resizableComponent);
		return bitpacked == 0 ? -1 : bitpacked & 0xFFFF;
	}

	/**
	 * Gets the fixed component based on the resizable one.
	 * 
	 * @return the fixed component's id.
	 */
	public final int getFixedComponent() {
		val e = PaneType.FIXED.getEnum();
		val bitpacked = e.getIntValue(161 << 16 | resizableComponent);
		return bitpacked == 0 ? -1 : bitpacked & 0xFFFF;
	}

	/**
	 * Gets the fullscreen component based on the resizable one.
	 * 
	 * @return the fullscreen component's id.
	 */
	public final int getFullScreenComponent() {
		val e = PaneType.FULL_SCREEN.getEnum();
		val bitpacked = e.getIntValue(161 << 16 | resizableComponent);
		return bitpacked == 0 ? -1 : bitpacked & 0xFFFF;
	}

	/**
	 * Gets the mobile component based on the resizable one.
	 * 
	 * @return the mobile component's id.
	 */
	public final int getMobileComponent() {
		val e = PaneType.MOBILE.getEnum();
		val bitpacked = e.getIntValue(161 << 16 | resizableComponent);
		return bitpacked == 0 ? -1 : bitpacked & 0xFFFF;
	}

	/**
	 * Gets the sidepanels component based on the resizable one.
	 * 
	 * @return the sidepanels component's id.
	 */
	public final int getSidepanelsComponent() {
		val e = PaneType.SIDE_PANELS.getEnum();
		val bitpacked = e.getIntValue(161 << 16 | resizableComponent);
		return bitpacked == 0 ? -1 : bitpacked & 0xFFFF;
	}

}