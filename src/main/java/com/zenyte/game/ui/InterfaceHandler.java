package com.zenyte.game.ui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.Expose;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Device;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.PostWindowStatusEvent;
import com.zenyte.utils.Ordinal;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.types.component.ComponentDefinitions;
import mgi.types.config.enums.EnumDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

import static com.zenyte.game.constants.GameInterface.EMOTE_TAB;

/**
 * @author Tommeh | 28 jan. 2018 : 21:24:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class InterfaceHandler {

	private static final int[] EXPANDED_INTERFACES = new int[] { 12, 139, 400, 345, 310, 700, 704, 709 };

	private static final Object[] EXPANDED_ARGS = new Object[] { -1, -2 };
	private static final Object[] DEFAULT_ARGS = new Object[] { -1, -1 };

	private static final Map<Integer, Integer[]> BACKGROUND_SCRIPT_ARGS = ImmutableMap.<Integer, Integer[]>builder()
			.put(25, new Integer[] { 5066031, 125 }).put(52, new Integer[] { 3612928, 0 })
			.put(57, new Integer[] { 4535323, 0 }).put(398, new Integer[] { 4212288, 50 })
			.put(224, new Integer[] { 4404769, 0 }).put(116, new Integer[] { 4404769, 0 })
			.put(267, new Integer[] { 65792, 0 }).put(299, new Integer[] { 2760198, 0 })
			.put(134, new Integer[] { 0, 195 }).build();

	@Ordinal
	public enum Journal {
		QUEST_TAB,
		ACHIEVEMENT_DIARIES,
		MINIGAMES,
		KOUREND
    }

	@Getter
	private final transient BiMap<Integer, Integer> visible;
	@Expose
	@Getter
	private PaneType pane;
	@Getter
	private Journal journal;
	@Expose
	@Setter
	@Getter
	private boolean resizable;
	private final transient Player player;

	public InterfaceHandler(final Player player) {
		this.player = player;
		//journal = Journal.QUEST_TAB;
		//TODO:
        journal = Journal.ACHIEVEMENT_DIARIES;
		visible = HashBiMap.create();
	}

	public final void initialize(final InterfaceHandler handler) {
		pane = handler.pane == null ? (player.getPlayerInformation().getDevice().equals(Device.DESKTOP) ? PaneType.RESIZABLE : PaneType.MOBILE) : handler.pane;
		resizable = handler.resizable;
		journal = handler.journal == null ? Journal.QUEST_TAB : handler.journal;
		//TODO:
		if (journal == Journal.QUEST_TAB) {
		    journal = Journal.ACHIEVEMENT_DIARIES;
        }
		visible.put(pane.getId() << 16, pane.getId());
	}

	public void sendPane(final PaneType fromPane, final PaneType toPane) {
		player.getPacketDispatcher().sendPane(toPane);
		visible.remove(fromPane.getId() << 16);
		visible.forcePut(pane.getId() << 16, toPane.getId());
		val pairs = InterfacePosition.getPairs(fromPane, toPane);
		pairs.forEach((k, v) -> moveInterface(fromPane, k, toPane, v));
	}

	public void sendGameFrame() {
		val pane = player.getPlayerInformation().getDevice().equals(Device.MOBILE) ? PaneType.MOBILE : resizable ? player.getSettings().isSidePanels() ? PaneType.SIDE_PANELS : PaneType.RESIZABLE : PaneType.FIXED;
		val map = player.getTemporaryAttributes();
		val dispatcher = player.getPacketDispatcher();
		val gameframe = map.get("gameframe_sent");
		if (gameframe == null) {
			map.put("gameframe_sent", true);
            if (player.getPlayerInformation().getDevice().equals(Device.DESKTOP) && resizable && player.getSettings().isSidePanels()) {
                player.getSettings().refreshSetting(Setting.SIDE_PANELS);
            } else {
                dispatcher.sendPane(pane);
            }
			for (val position : InterfacePosition.VALUES) {
				if (position.getGameframeInterfaceId() == -1 || position.equals(InterfacePosition.FRIENDS_TAB) || position.equals(InterfacePosition.JOURNAL_TAB_HEADER) || position.equals(InterfacePosition.ACCOUNT_MANAGEMENT)) {
					continue;
				}
                val gameInter = GameInterface.get(position.getGameframeInterfaceId());
				if (gameInter.isPresent()) {
				    gameInter.get().open(player);
                } else {
					sendInterface(position, position.getGameframeInterfaceId());
				}
			}
			player.getSettings().refreshSetting(Setting.FRIEND_LIST_TOGGLE);
			dispatcher.sendClientScript(2494, 1);
			player.getVarManager().sendBit(8119, 1);
			player.onLobbyClose();
			sendMisc();
		} else {
			if (player.getPlayerInformation().getDevice().equals(Device.DESKTOP) && resizable && player.getSettings().isSidePanels()) {
				player.getSettings().refreshSetting(Setting.SIDE_PANELS);
			} else {
				sendPane(this.pane, pane);
			}
			sendMisc();
		}
		openJournal();
		GameInterface.GAME_NOTICEBOARD.open(player);
		if (player.isOnMobile()) {
			player.getSettings().refreshSetting(Setting.MINIMIZE_MINIMAP);
		}
		PluginManager.post(new PostWindowStatusEvent(player));
	}

	public void openJournal() {
		GameInterface.JOURNAL_HEADER_TAB.open(player);
        switch (journal) {
            case QUEST_TAB:
                GameInterface.QUEST_TAB.open(player);
                break;
            case ACHIEVEMENT_DIARIES:
                GameInterface.ACHIEVEMENT_DIARY_TAB.open(player);
                break;
            case MINIGAMES:
                GameInterface.MINIGAME_TAB.open(player);
                break;
            case KOUREND:
                GameInterface.KOUREND_FAVOUR_TAB.open(player);
                break;
        }
    }

	public void sendWelcomeScreen() {
		val dispatcher = player.getPacketDispatcher();
		dispatcher.sendComponentText(378, 70, "You do not have a Bank PIN.<br>Please visit a bank if you would like one.");
		dispatcher.sendComponentText(378, 4, "<col=1f1fff>Cromperty</col> got a <col=003600>right-click option</col> to claim <col=ffffff>essence</col>,<br>and if you <col=ffff00>enable the ESC key</col> for closing menus,<br>it will work on the <col=3f007f>World Map</col> and <col=6f0000>this screen</col>.");
		/**
		 * Second arg is the model id
		 */
		dispatcher.sendClientScript(233, 24772660, 34024, 0, 0, 225, 1296, 0, 348, -1);
		dispatcher.sendClientScript(233, 24772661, 23446, 0, 50, 165, 1717, 0, 1116, -1);
		dispatcher.sendPane(PaneType.FULL_SCREEN);
		dispatcher.sendClientScript(2494, 1);
		sendInterface(InterfacePosition.CHATBOX, 162);
		sendInterface(InterfacePosition.PRIVATE_CHAT, 163);
		sendInterface(378, 27, PaneType.FULL_SCREEN, false);
		dispatcher.sendClientScript(1080, "");
		sendInterface(InterfacePosition.SKILLS_TAB, 320);

		dispatcher.sendComponentSettings(399, 7, 0, EnumDefinitions.get(1374).getSize(), AccessMask.CLICK_OP1);
		dispatcher.sendComponentSettings(399, 8, 0, EnumDefinitions.get(1375).getSize(), AccessMask.CLICK_OP1);
		dispatcher.sendComponentSettings(399, 9, 0, EnumDefinitions.get(1376).getSize(), AccessMask.CLICK_OP1);
		sendInterface(InterfacePosition.INVENTORY_TAB, 149);
		sendInterface(InterfacePosition.EQUIPMENT_TAB, 387);
		sendInterface(InterfacePosition.PRAYER_TAB, 541);
		sendInterface(InterfacePosition.FRIENDS_TAB, 432);
		sendInterface(InterfacePosition.ACCOUNT_MANAGEMENT, 429);
		sendInterface(InterfacePosition.LOGOUT_TAB, 182);

		GameInterface.SPELLBOOK.open(player);

		GameInterface.SETTINGS.open(player);

		EMOTE_TAB.open(player);
		dispatcher.sendComponentSettings(216, 1, 0, 47, AccessMask.CLICK_OP1);
		//sendInterface(InterfacePosition.MUSIC_TAB, 239);
		//dispatcher.sendComponentSettings(239, 2, 0, 581, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2);
		GameInterface.MUSIC_TAB.open(player);
        sendInterface(InterfacePosition.CLAN_TAB, 7);
		GameInterface.COMBAT_TAB.open(player);
		dispatcher.sendClientScript(2014, 0, 0, 0, 0, 0, 0);
		dispatcher.sendClientScript(2015, 0);
	}

	private void sendMisc() {
		player.getSettings().refreshSetting(Setting.DATA_ORBS);
		val dispatcher = player.getPacketDispatcher();
		dispatcher.closeInterface(165 << 16 | 27);// unlock chatbox typing.
		dispatcher.sendComponentSettings(216, 1, 0, 47, AccessMask.CLICK_OP1);
		dispatcher.sendComponentSettings(261, 89, 1, 4, AccessMask.CLICK_OP1);
		dispatcher.sendComponentSettings(261, 90, 1, 4, AccessMask.CLICK_OP1);
		player.getVarManager().sendBit(4070, player.getCombatDefinitions().getSpellbook().ordinal());
	}

	public int getInterfaceComponent(final int interfaceId) {
		return visible.inverse().getOrDefault(interfaceId, -1) & 0xFFFF;
		// return visible.entrySet().stream().filter(e -> e.getValue() ==
		// interfaceId).map(Map.Entry::getKey).findAny().orElse(-1) & 0xFFFF;
	}

	public int getInterfacePane(final int interfaceId) {
		return visible.inverse().getOrDefault(interfaceId, -1) >> 16;
	}

	public int getInterface(final PaneType pane, final int paneComponent) {
		return visible.getOrDefault(pane.getId() << 16 | paneComponent, -1);
	}

	public boolean containsInterface(final InterfacePosition position) {
		if (position == InterfacePosition.CHATBOX || position == InterfacePosition.DIALOGUE) {
			return visible.containsKey(PaneType.CHATBOX.getId() << 16 | InterfacePosition.DIALOGUE.getComponent(PaneType.RESIZABLE));
		}
		return visible.containsKey(PaneType.FIXED.getId() << 16 | position.getFixedComponent()) || visible.containsKey(PaneType.SIDE_PANELS.getId() << 16 | position.getSidepanelsComponent()) || visible.containsKey(PaneType.RESIZABLE.getId() << 16 | position.getResizableComponent()) || visible.containsKey(PaneType.MOBILE.getId() << 16 | position.getMobileComponent());
	}

	public void sendInterface(final int interfaceId, final int paneComponent, final PaneType pane, final boolean walkable) {
		player.getPacketDispatcher().sendInterface(interfaceId, paneComponent, pane, walkable);
		visible.forcePut(pane.getId() << 16 | paneComponent, interfaceId);
	}

	public void sendInterface(final GameInterface gameInterface) {
		sendInterface(gameInterface.getPosition(), gameInterface.getId());
	}

    public void sendInterface(final Interface inter) {
	    val gameInterface = inter.getInterface();
        sendInterface(gameInterface.getPosition(), gameInterface.getId());
    }

	public void sendInterface(final InterfacePosition position, final int id) {
		sendInterface(position, id, position.isWalkable());
	}

	public void sendInterface(final InterfacePosition position, final int id, final boolean walkable) {
		if (!ComponentDefinitions.containsInterface(id)) {
			return;
		}
		if (position.equals(InterfacePosition.DIALOGUE)) {
		    closeInterface(InterfacePosition.CENTRAL);
        } else if (position.equals(InterfacePosition.CENTRAL)) {
		    closeInterface(InterfacePosition.DIALOGUE);
        }
		val pane = position.equals(InterfacePosition.DIALOGUE) ? PaneType.CHATBOX : this.pane;
		val paneToObtainComponentFrom = pane == PaneType.CHATBOX ? PaneType.RESIZABLE : pane;
		if (position.equals(InterfacePosition.CENTRAL)) {
			player.getPacketDispatcher().sendClientScript(2524, ArrayUtils.contains(EXPANDED_INTERFACES, id) ? EXPANDED_ARGS : DEFAULT_ARGS);
			if (BACKGROUND_SCRIPT_ARGS.containsKey(id)) {
				player.getPacketDispatcher().sendClientScript(917, BACKGROUND_SCRIPT_ARGS.get(id));
			}
		}
        val current = GameInterface.get(id);
        val previous = visible.forcePut(pane.getId() << 16 | position.getComponent(paneToObtainComponentFrom), id);
        if (previous != null) {
            GameInterface.get(previous).ifPresent(gameInterface -> closePlugin(player, gameInterface, current));
        }
		player.getPacketDispatcher().sendInterface(id, position.getComponent(paneToObtainComponentFrom), pane, walkable);
	}

	public void closeInterface(final InterfacePosition position, final boolean removeFromMap, final boolean closeEvent) {
		val contains = containsInterface(position);
		val dialogue = position.equals(InterfacePosition.DIALOGUE);
		val pane = dialogue ? PaneType.RESIZABLE : this.pane;
		val hash = (dialogue ? PaneType.CHATBOX.getId() : (pane == null ? 0 : pane.getId())) << 16 | (position == null ? 0 : position.getComponent(pane));
		closeInput();
		Integer previous = null;
        if (removeFromMap) {
            previous = visible.remove(hash);
        }

		if (contains) {
			player.getPacketDispatcher().closeInterface(hash);
			if (dialogue) {
				val dial = player.getDialogueManager().getLastDialogue();
				if (dial != null) {
					if (dial.getNpc() != null) {
						dial.getNpc().finishInteractingWith(player);
					}
				}
			}
		}

        if (contains && previous != null) {
            val prevGameInterface = GameInterface.get(previous);
            prevGameInterface.ifPresent(gameInterface -> closePlugin(player, gameInterface, Optional.empty()));
        }

		if (closeEvent && contains && position.equals(InterfacePosition.CENTRAL)) {
			val runnable = player.getCloseInterfacesEvent();
			if (runnable == null) {
				return;
			}
			runnable.run();
			player.setCloseInterfacesEvent(null);
		}
	}

	private final void closePlugin(@NotNull final Player player, @NotNull final GameInterface inter, @NotNull final Optional<GameInterface> replacement) {
		player.log(LogLevel.INFO, "Closing interface: " + inter + ", replacement: " + replacement);
		val plugin = NewInterfaceHandler.INTERFACES.get(inter.getId());
		if (plugin != null) {
			plugin.close(player, replacement);
		}
	}

	public void closeInterface(final InterfacePosition position) {
		closeInterface(position, true, true);
	}

	public void closeInterface(final Interface inter) {
		val gameInterface = inter.getInterface();
		closeInterface(gameInterface.getPosition());
	}

	public void closeInterface(final GameInterface gameInterface) {
	    this.closeInterface(gameInterface.getPosition());
    }

	public void closeInterface(final int interfaceId) {
		val componentId = getInterfaceComponent(interfaceId);
		val position = InterfacePosition.getPosition(componentId, pane);
		if (position == null) {
			return;
		}
		closeInterface(position, true, true);
	}

	public void closeInterfaces() {
		closeInterface(InterfacePosition.CENTRAL);
		closeInterface(InterfacePosition.SINGLE_TAB);
		closeInterface(InterfacePosition.DIALOGUE);
	}

	public boolean isPresent(final GameInterface inter) {
	    return this.isVisible(inter.getId());
    }

	public void closeInput() {
		final Map<Object, Object> attributes = player.getTemporaryAttributes();
		final Object input = attributes.get("interfaceInput");
		if (input != null) {
			player.getPacketDispatcher().sendClientScript(299, 1, 1);
			attributes.remove("interfaceInput");
		}
	}

	private void moveInterface(final PaneType fromPane, final int fromComponent, final PaneType toPane, final int toComponent) {
		if (isVisible(214)) {
			closeInterface(InterfacePosition.CENTRAL);
		}
		final int interfaceId = getInterface(fromPane, fromComponent);
		player.getPacketDispatcher().sendMoveInterface(fromPane.getId(), fromComponent, toPane.getId(), toComponent);
		visible.remove(fromPane.getId() << 16 | fromComponent);
		visible.forcePut(toPane.getId() << 16 | toComponent, interfaceId);
	}

	public boolean isVisible(final int id) {
		if (pane != null && pane.getId() == id) {
			return true;
		}
		return visible.inverse().containsKey(id);
	}

	public boolean isVisible(final int pane, final int paneComponent) {
		return visible.containsKey(pane << 16 | paneComponent);
	}

	public void openGameTab(final GameTab tab) {
		player.getPacketDispatcher().sendClientScript(915, tab.getId());
	}

	public void setPane(final PaneType pane) {
		this.pane = pane;
		visible.forcePut(-1, pane.getId());
	}

	public void setJournal(final Journal journal) {
		this.journal = journal;
		VarCollection.ACTIVE_JOURNAL.update(player);
		switch (journal) {
			case QUEST_TAB:
				GameInterface.QUEST_TAB.open(player);
				break;
			case ACHIEVEMENT_DIARIES:
				GameInterface.ACHIEVEMENT_DIARY_TAB.open(player);
				break;
			case MINIGAMES:
			    val inRaid = player.getRaid().isPresent();
			    if (inRaid) {
                    GameInterface.RAID_PARTY_TAB.open(player);
                } else {
                    GameInterface.MINIGAME_TAB.open(player);
                }
				break;
			case KOUREND:
				GameInterface.KOUREND_FAVOUR_TAB.open(player);
				break;
		}
	}

}