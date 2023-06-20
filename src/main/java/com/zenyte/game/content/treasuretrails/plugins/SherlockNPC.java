package com.zenyte.game.content.treasuretrails.plugins;

import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import lombok.val;

/**
 * @author Kris | 19/04/2019 22:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SherlockNPC extends NPCPlugin implements ItemOnNPCAction {

    @Override
    public void handleItemOnNPCAction(final Player player, final Item item, final int slot, final NPC npc) {
        val list = TreasureTrail.getCluesList(item);
        assert list != null;
        assert list.size() >= 1;
        val constantName = list.get(0);
        val clueScroll = TreasureTrail.getClues().get(constantName);
        if (!(clueScroll instanceof SherlockTask)) {
            player.sendMessage("Nothing interesting happens.");
            return;
        }
        val stage = item.getNumericAttribute("Sherlock Stage").intValue();
        if (stage == 0) {
            item.setAttribute("Sherlock Stage", 1);
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Ah, just what I was looking for. I have a challenge for you. If you complete it, I'll give you something."));
        } else if (stage == 1) {
            player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You haven't completed the challenge yet, come back when you have."));
        } else if (stage == 2) {
            player.getDialogueManager().start(new Dialogue(player, 6586) {
                @Override
                public void buildDialogue() {
                    npc("Fantastic! Here, have this.").executeAction(() -> {
                        if (player.getInventory().getItem(slot) != item) {
                            return;
                        }
                        player.getDialogueManager().finish();
                        TreasureTrail.continueSherlockTask(player, item, slot);
                    });
                }
            });
        }
    }

    @Override
    public Object[] getItems() {
        return new Object[] {
                ClueItem.ELITE.getClue(), ClueItem.MASTER.getClue()
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                6586
        };
    }

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                val entry = TreasureTrail.findSherlockClue(player);
                if (!entry.isPresent()) {
                    npc("Good day, " + player.getName() + ".");
                    return;
                }
                val item = entry.get().getItem();
                val list = TreasureTrail.getCluesList(item);
                assert list != null;
                assert list.size() >= 1;
                val constantName = list.get(0);
                val clueScroll = TreasureTrail.getClues().get(constantName);
                if (!(clueScroll instanceof SherlockTask)) {
                    npc("Good day, " + player.getName() + ".");
                    return;
                }
                val stage = item.getNumericAttribute("Sherlock Stage").intValue();
                if (stage == 0) {
                    item.setAttribute("Sherlock Stage", 1);
                    npc("Ah, just what I was looking for. I have a challenge for you. If you complete it, I'll give you something.");
                } else if (stage == 1) {
                    npc("You haven't completed the challenge yet, come back when you have.");
                } else {
                    npc("Fantastic! Here, have this.").executeAction(() -> {
                        player.getDialogueManager().finish();
                        TreasureTrail.continueSherlockTask(player, item, entry.get().getSlot());
                    });
                }
            }
        }));

    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                6586
        };
    }
}
