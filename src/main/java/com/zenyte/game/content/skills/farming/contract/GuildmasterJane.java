package com.zenyte.game.content.skills.farming.contract;

import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.VarManager;


/**
 * @author Christopher
 * @since 4/8/2020
 */
public class GuildmasterJane extends NPCPlugin {
    public static final int JANE_VARBIT = 7947;

    static {
        VarManager.appendPersistentVarbit(JANE_VARBIT);
    }

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager()
                .start(new GuildmasterJaneDialogue(player, npc, GuildmasterJaneDialogue.JaneDialogueType.FULL)));
        bind("Contract", ((player, npc) -> player.getDialogueManager()
                .start(new GuildmasterJaneDialogue(player, npc, GuildmasterJaneDialogue.JaneDialogueType.CONTRACT_OPTION))));
    }

    @Override
    public int[] getNPCs() {
        return new int[]{8628, NpcId.GUILDMASTER_JANE, NpcId.GUILDMASTER_JANE_8587};
    }
}
