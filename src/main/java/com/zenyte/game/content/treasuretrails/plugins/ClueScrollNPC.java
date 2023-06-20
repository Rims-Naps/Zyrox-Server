package com.zenyte.game.content.treasuretrails.plugins;

import com.zenyte.game.content.skills.farming.FarmingPatch;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.content.treasuretrails.clues.Anagram;
import com.zenyte.game.content.treasuretrails.clues.CipherClue;
import com.zenyte.game.content.treasuretrails.clues.CrypticClue;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 07/04/2019 22:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ClueScrollNPC extends NPCPlugin {

    private static final String[] genericResponses = new String[] {
            "Good morning.", "Hello.", "How do you do?", "Hello, %s.", "Good morning, %s."
    };

    @Override
    public void handle() {
        bind("Talk-to", this::talk);
        bind("Talk", this::talk);
    }

    private final void talk(@NotNull final Player player, @NotNull final NPC npc) {
        if (!TreasureTrail.talk(player, npc)) {
            if (npc.getId() == 5735) { // Immenizz - Puro-Puro Impling
                player.getDialogueManager().start(new Dialogue(player, npc) {
                    @Override
                    public void buildDialogue() {
                        player("Hello. So, what is this place?");
                        npc("Immenizz", "This is my home, mundane human! What do you have in your pockets? Something tasty?");
                        player("Stay out of my pockets! I don't have anything that you want.");
                        npc("Immenizz", "Ah, but do you have anything that *you* want?");
                        player("Of course I do!");
                        npc("Immenizz", "Then you have something that implings want.");
                        player("Eh?");
                        npc("Immenizz", "We want things you people want. They are tasty to us! The more you want them, the tastier they are!");
                        player("So, you collect things that humans want? Interesting... So, what would happen if I caught an impling in a butterfly net?");
                        npc("Immenizz", "Don't do that! That would be cruel. But chase us, yes! That is good. Implings are not easy to catch. Especially ones with really tasty food.");
                        player("So, some of these implings have things that I will really want? Hmm, maybe it would be worth my while trying to catch some.");
                    }
                });
            } else {
                player.getDialogueManager().start(new NPCChat(player, npc.getId(), String.format(Utils.random(genericResponses), player.getName())));
            }
        }
    }

    @Override
    public int[] getNPCs() {
        val set = new IntOpenHashSet(CrypticClue.npcMap.keySet());
        set.addAll(Anagram.npcMap.keySet());
        set.addAll(CipherClue.npcMap.keySet());
        set.removeAll(new IntOpenHashSet(new int[] { 3253, 13, 1773, 2461, 1120, 2457, 1305, 3077, 5870, 1165, 1166, 1167, 1168, 1169, 401, 402, 403, 404, 405, 490, 6797, 3644, 3645,
                3646, 5034, 822, 5789, 1315, 1316,
                3231, 6526, 0, 757, 7757, 8480, 4913, 4626, 3341, 3342, 3343, 3344, 311, 2914, 1328, 1329, 1330, 1331, 1332, 1333, 1334, 2979, 1878, 8480, 8481, 6562, 5792, 5522}));
        set.removeAll(FarmingPatch.getPatchSetByGardeners().keySet());
        return set.toIntArray();
    }
}
