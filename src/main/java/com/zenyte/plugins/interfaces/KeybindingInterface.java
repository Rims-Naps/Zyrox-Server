package com.zenyte.plugins.interfaces;

import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.KeybindType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.val;

;

/**
 * @author Tommeh | 2 dec. 2017 : 19:01:28
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class KeybindingInterface implements UserInterface {

	private static final int ESC_TO_CLOSE_VARBIT = 4681;
	public static final int INTERFACE = 121;

	private static void setKeybind(final Player player, final int varbit, final int value) {
		player.getVarManager().sendBit(varbit, value);
		player.getSettings().getKeybinds().put(varbit, value);
	}
	
	public static void setDefaultKeybinds(final Player player) {
		for (val keybind : KeybindType.VALUES) {
			player.getSettings().getKeybinds().put(keybind.getVarbitId(), keybind.getDefaultValue());
		}
		setKeybind(player, ESC_TO_CLOSE_VARBIT, 0);
	}

	@Listener(type = ListenerType.LOBBY_CLOSE)
	protected void refresh(final Player player) {
		if (player.getSettings().getKeybinds() == null) {
			return;
		}
		for (val varbit : player.getSettings().getKeybinds().keySet()) {
			val value = player.getSettings().getKeybinds().get(varbit);
			player.getVarManager().sendBit(varbit, value);
		}
	}

	private static final Int2IntMap preEoc = new Int2IntOpenHashMap();

	static {
	    //"4675":5,"4676":0,"4677":0,"4678":1,"4679":2,"4680":3,"4681":0,"4682":4,"4683":7,"4684":8,"4686":10,"4687":11,"4688":12,"4689":0,"6517":9
        preEoc.put(4675, 5);
        preEoc.put(4676, 0);
        preEoc.put(4677, 0);
        preEoc.put(4678, 1);
        preEoc.put(4679, 2);
        preEoc.put(4680, 3);
        preEoc.put(4681, 0);
        preEoc.put(4682, 4);
        preEoc.put(4683, 7);
        preEoc.put(4684, 8);
        preEoc.put(4686, 10);
        preEoc.put(4687, 11);
        preEoc.put(4688, 12);
        preEoc.put(4689, 0);
        preEoc.put(6517, 9);
    }

	@Override
	public void handleComponentClick(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId, final int optionId, final String option) {
		if (componentId == 113) {
		    //Pre-eoc
            for (val k : preEoc.int2IntEntrySet()) {
                setKeybind(player, k.getIntKey(), k.getIntValue());
            }
            player.sendMessage("Pre-EOC keys loaded.");
        } else if (componentId == 111) {
			val selected = player.getNumericTemporaryAttribute("SelectedKeybind").intValue();
			for (val type : KeybindType.VALUES) {
				if (player.getSettings().getKeybinds().get(type.getVarbitId()) == null) {
					continue;
				}
				if (player.getSettings().getKeybinds().get(type.getVarbitId()) == slotId) {
					setKeybind(player, type.getVarbitId(), 0);
				}
			}
			setKeybind(player, selected, slotId);
		} else if (componentId == 103) {
			val value = player.getVarManager().getBitValue(ESC_TO_CLOSE_VARBIT);
			setKeybind(player, ESC_TO_CLOSE_VARBIT, (value == 1 ? 0 : 1));
		} else if (componentId == 104) {
			for (val k : KeybindType.VALUES) {
				setKeybind(player, k.getVarbitId(), k.getDefaultValue());
			}
			setKeybind(player, ESC_TO_CLOSE_VARBIT, 0);
			player.sendMessage("Default keys loaded.");
		} else {
			player.getPacketDispatcher().sendComponentSettings(121, 111, 0, 13, AccessMask.CLICK_OP1);
			for (val keybind : KeybindType.VALUES) {
				if (keybind.getComponentId() == componentId) {
					player.addTemporaryAttribute("SelectedKeybind", keybind.getVarbitId());
				}
			}
		}
	}

	@Override
	public int[] getInterfaceIds() {
		return new int[] { INTERFACE };
	}

}
