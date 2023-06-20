package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.parser.impl.NPCExamineLoader;
import com.zenyte.game.ui.testinterfaces.DropViewerInterface;
import com.zenyte.game.world.entity.player.GameSetting;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 19:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpNpcExamineEvent implements ClientProtEvent {

    private final int npcId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Id: " + npcId);
    }

    @Override
    public void handle(Player player) {
        if (player.getNumericAttribute(GameSetting.EXAMINE_NPCS_DROP_VIEWER.toString()).intValue() == 1) {
            if (!player.isUnderCombat(6)) {
                DropViewerInterface.open(player, npcId);
            } else {
                player.sendFilteredMessage("You need to be out of combat to use the drop viewer.");
            }
        }
        val examine = NPCExamineLoader.get(npcId);
        if (examine == null) {
            return;
        }
        player.sendMessage(examine.getExamine(), MessageType.EXAMINE_NPC);
        System.err.println("Examined: " + npcId);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
