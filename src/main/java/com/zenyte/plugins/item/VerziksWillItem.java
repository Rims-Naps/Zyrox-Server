package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

/**
 * @author Kris | 30/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class VerziksWillItem extends ItemPlugin {

    private static final Animation READ_ANIM = new Animation(7403);

    @Override
    public void handle() {
        bind("Read", (player, item, slotId) -> {
            val name = item.getName();
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    item(item, "You can make out some faded words on the ancient parchment. It appears to be an archaic invocation of the gods! Would you like to absorb its power?");
                    options("This will consume the scroll.", new DialogueOption("Learn " + name + ".", () -> readScroll(player, item, slotId)), new DialogueOption("Cancel."));
                }
            });
        });
    }

    private final void readScroll(final Player player, final Item item, final int slotId) {
        val inventory = player.getInventory();
        val inSlot = inventory.getItem(slotId);
        if (inSlot != item) {
            return;
        }
        val name = inSlot.getName();
        player.lock(5);
        player.setAnimation(READ_ANIM);
        inventory.deleteItem(slotId, item);
        player.getVariables().setTobBoost(player.getVariables().getTobBoost() + (int) TimeUnit.HOURS.toTicks(5));
        GameNoticeboardInterface.refreshVerziksWill(player);
        player.getDialogueManager().start(new ItemChat(player, item, "You study the scroll and gain 5 hours of: <col=FF0040>" + name + "</col>"));
    }

    @Override
    public int[] getItems() {
        return new int[] {
                19783
                //32325
        };
    }
}
