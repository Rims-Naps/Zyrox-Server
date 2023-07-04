package com.zenyte.game.ui;

import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.component.ComponentDefinitions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class ButtonAction {

	private static final Logger logger = LogManager.getLogger(ButtonAction.class);

	public static final Int2ObjectOpenHashMap<UserInterface> INTERFACES = new Int2ObjectOpenHashMap<>();
	
	public static final void handleComponentAction(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId, final int option, final int format) {
	    if (!player.getInterfaceHandler().getVisible().inverse().containsKey(interfaceId)) {
			return;
		}
		if (!player.getControllerManager().canButtonClick(interfaceId, componentId, slotId)) {
			return;
		}
		player.getInterfaceHandler().closeInput();
        val defs = ComponentDefinitions.get(interfaceId, componentId);
        val op = defs.getActions() == null || defs.getActions().length <= (option - 1) ? "null" : defs.getActions()[option - 1];

		/*
		 * Temporarily
		 */

		{

			val plugin = NewInterfaceHandler.INTERFACES.get(interfaceId);
			if (plugin != null) {
			    var opt = plugin.getComponentName(componentId, slotId);
			    if (!opt.isPresent()) {
			        opt = plugin.getComponentName(componentId, -1);
                }
			    log.info("[" + plugin.getClass().getSimpleName() + "] IF" + format + ": " + opt.orElse("Absent") + "(" + interfaceId + "::" + componentId + ") | Slot: " + slotId + " | Option: " + option + " | Item: " + itemId);
				plugin.click(player, componentId, slotId, itemId, option);
				return;
			}
		}

		final UserInterface inter = INTERFACES.get(interfaceId);
		if (inter != null) {
			if (GameConstants.DEV_DEBUG) {
				logger.info("Interface(IF" + format +"):" + inter.getClass().getSimpleName() + ", interfaceId=" + interfaceId + ", component=" + componentId + ", slot=" + slotId + ", item=" + itemId + ", option=" + op + "(" + option + ")");
			}
			inter.handleComponentClick(player, interfaceId, componentId, slotId, itemId, option, op);
			return;
		}
		if (GameConstants.DEV_DEBUG) {
			logger.info("Unhandled(IF" + format + "): " + "interfaceId=" + interfaceId + ", component=" + componentId + ", slot=" + slotId + ", item=" + itemId + ", option=" + op + "(" + option + ")");
		}
	}
	
	public static final void add(final Class<?> c) {
		try {
			if (c.isAnonymousClass()) {
				return;
			}
			if (c.isMemberClass()) {
				return;
			}
			
			final Object o = c.newInstance();
			if (!(o instanceof UserInterface)) {
				return;
			}
			final UserInterface userInterface = (UserInterface) o;
			for (final int key : userInterface.getInterfaceIds()) {
				if (INTERFACES.containsKey(key)) {
					logger.error("<col=ff0000>FATAL: Overriding an interface handler. ID: " + key + ", Class: " + userInterface.getClass().getSimpleName());
				}
				INTERFACES.put(key, userInterface);
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
