package com.zenyte.game.packet.in.event;

import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.SpellDefinitions;
import com.zenyte.game.content.skills.magic.spells.ItemSpell;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpHeldTEvent implements ClientProtEvent {

    private final int fromInterfaceId, fromComponentId, toInterfaceId, toComponentId, fromSlot, toSlot;

    @Override
    public void handle(Player player) {
        val item = player.getInventory().getItem(toSlot);
        if (item == null) {
            return;
        }
        val spell = Magic.getSpell(player.getCombatDefinitions().getSpellbook(), SpellDefinitions.getSpellName(fromComponentId), ItemSpell.class);
        if (spell == null) {
            return;
        }
        spell.execute(player, item, toSlot);
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + fromInterfaceId + " -> " + toInterfaceId + ", component: " + fromComponentId + " -> " + toComponentId + ", slot: " + fromSlot + " -> " + toSlot);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}