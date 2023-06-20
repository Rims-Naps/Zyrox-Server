package com.zenyte.game.world.region.area.bobsisland.plugin;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

public class FishlikeThingOnBob implements ItemOnNPCAction
{
    @Override
    public void handleItemOnNPCAction(final Player player, final Item item, final int slot, final NPC npc) {
        val correct = item.getId() == 6200;
        player.getInventory().deleteItem(item);
        if (!correct) {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    npc("What was that? That was absolutely disgusting!");
                    npc("Don't you know what kind of fish I like? Talk to my other servants for advice.");
                }
            });
            return;
        }
        player.getAttributes().put("evil bob complete", true);
        player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("Mmm, mmm...that's delicious.");
                npc("Now, let me take...a little...catnap.").executeAction(() -> npc.setForceTalk(new ForceTalk("ZZZzzz")));
            }
        });
    }

    @Override
    public Object[] getItems() {
        return new Object[] {
                6200, 6202, 6204, 6206
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                390
        };
    }
}