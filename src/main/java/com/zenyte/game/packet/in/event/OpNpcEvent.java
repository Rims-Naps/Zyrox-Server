package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.actions.NPCHandler;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 19:27
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpNpcEvent implements ClientProtEvent {

    private final int index, option;
    private final boolean run;

    @Override
    public void log(@NotNull final Player player) {
        val npc = World.getNPCs().get(index);
        if (npc == null) {
            log(player,
                    "Index: " + index + ", option: " + option + ", run: " + run + "; null");
            return;
        }
        val tile = npc.getLocation();
        log(player,
                "Index: " + index + ", option: " + option + ", run: " + run + "; id: " + npc.getId() + ", name: " + npc.getName(player) + ", x: " + tile.getX() + ", y: " + tile.getY() + ", z: " + tile.getPlane());
    }

    @Override
    public void handle(Player player) {
        val npc = World.getNPCs().get(index);
        if (npc != null) {
            NPCHandler.handle(player, npc, run, option);
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
