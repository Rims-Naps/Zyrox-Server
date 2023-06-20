package com.zenyte.plugins.dialogue;

import com.zenyte.game.content.minigame.tithefarm.TitheFarmManager;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFruitSpoilD extends Dialogue {

    public TitheFruitSpoilD(final Player player, final int npcId) {
        super(player, npcId);
    }

    @Override
    public void buildDialogue() {
        npc("Deposit your fruit into the sacks before you come out here, otherwise it'll go rotten.");
        options(2, TITLE, "Go back and deposit your fruit.", "Leave anyway, and let the fruit spoil.")
                .onOptionTwo(() -> TitheFarmManager.getSingleton().selectRandomInstance().leaveInstance(player));
    }
}
