package com.zenyte.game.world.entity.player.update.mask;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.masks.UpdateFlags;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.UpdateMask;
import com.zenyte.network.io.RSBuffer;
import mgi.types.config.npcs.NPCDefinitions;
import lombok.val;

/**
 * @author Kris | 24/11/2018 22:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class OptionMask extends UpdateMask {

    @Override
    public UpdateFlag getFlag() {
        return UpdateFlag.OPTION;
    }

    private static final String empty = "";

    @Override
    public boolean apply(Player player, final Entity entity, final UpdateFlags flags, final boolean added) {
        if (!Constants.SPAWN_MODE) {
            return false;
        }
        val definitions = NPCDefinitions.get(player.getTransmogrifiedId(((NPC) entity).getDefinitions(),
                ((NPC) entity).getId()));
        if (definitions == null)
            return false;
        return definitions.getFilterFlag() > 0;
    }

    @Override
    public void writeNPC(final RSBuffer buffer, final Player player, final NPC npc) {
        val definitions = NPCDefinitions.get(player.getTransmogrifiedId(npc.getDefinitions(),
                npc.getId()));
        assert definitions != null;
        val mask = definitions.getFilterFlag();

        buffer.writeByte(mask);

        for (int i = 0; i < 5; i++) {
            if ((mask >> i & 0x1) != 0) {
                buffer.writeString(Utils.getOrDefault(definitions.getFilteredOptions()[i], empty));
            }
        }
    }
}
