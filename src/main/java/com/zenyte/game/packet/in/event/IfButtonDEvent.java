package com.zenyte.game.packet.in.event;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.MethodicPluginHandler;
import com.zenyte.plugins.handlers.InterfaceSwitchHandler;
import com.zenyte.processor.Listener;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 22:15
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class IfButtonDEvent implements ClientProtEvent {

    private final int fromInterfaceId, fromComponentId, toInterfaceId, toComponentId, fromSlotId, toSlotId;

    @Override
    public void log(@NotNull final Player player) {
        log(player,
                "Interface: " + fromInterfaceId + " -> " + toInterfaceId + ", component: " + fromComponentId + " -> " + toComponentId + ", slot: " + fromSlotId + " -> " + toSlotId);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        /** Close all input dialogues when switching, to prevent potential dupes in vulnerable code. */
        player.getInterfaceHandler().closeInput();
        player.getInterfaceHandler().closeInterface(InterfacePosition.DIALOGUE);
        if (fromInterfaceId == toInterfaceId) {
            val optionalGameInterface = GameInterface.get(fromInterfaceId);
            if (optionalGameInterface.isPresent()) {
                val gameInterface = optionalGameInterface.get();
                val optionalPlugin = gameInterface.getPlugin();
                if (optionalPlugin.isPresent()) {
                    val plugin = optionalPlugin.get();
                    if (plugin instanceof SwitchPlugin) {
                        if (plugin.switchItem(player, fromComponentId, toComponentId, fromSlotId, toSlotId))
                            return;
                    }
                }
            }
        }
        val plugin = InterfaceSwitchHandler.INTERFACES.get(fromInterfaceId);
        /** If a full-script plugin exists for the interface, execute it and prevent code from going further. */
        if (plugin != null) {
            plugin.switchItem(player, fromInterfaceId, toInterfaceId, fromComponentId, toComponentId, fromSlotId, toSlotId);
            return;
        }
        /** Invoke all methodic plugins. */
        MethodicPluginHandler.invokePlugins(Listener.ListenerType.INTERFACE_SWITCH, player, fromInterfaceId, toInterfaceId, fromComponentId, toComponentId, fromSlotId, toSlotId);
    }
}
