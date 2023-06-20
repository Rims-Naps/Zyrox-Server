package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 26/08/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TomeOfExperience extends ItemPlugin {
    private static final int TOME_OF_EXPERIENCE = 30215;

    private static final Graphics graphics = new Graphics(1176);

    @Override
    public void handle() {
        bind("Read", (player, item, container, slotId) -> player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(item, "Reading the tome will grant you five minutes of 50% bonus experience in anything you do.<br>" +
                        "This bonus experience will not stack with the global bonus experience.");
                item(item, "Once you've read the tome, the timer will count down while you are online - the timer cannot be stopped until it reaches zero.");
                options("Are you sure you wish to read it?",
                        new DialogueOption("Yes, read the tome.", () -> read(player, item, slotId, 1)), new DialogueOption("No, don't read the tome."));
            }
        }));

        bind("Read-All", (player, item, container, slotId) -> player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(item, "Reading the tomes will grant you five minutes of 50% bonus experience in anything you do, per tome.<br>" +
                        "This bonus experience will not stack with the global bonus experience.");
                item(item, "Once you've read the tomes, the timer will count down while you are online - the timer cannot be stopped until it reaches zero.");
                options("Are you sure you wish to read all your tomes?",
                        new DialogueOption("Yes, read the tomes.", () -> read(player, item, slotId, item.getAmount())), new DialogueOption("No, don't read the tomes."));
            }
        }));
    }

    private final void read(@NotNull final Player player, @NotNull final Item item, final int slot, final int amount) {
        val inventory = player.getInventory();
        if (inventory.getItem(slot) != item) {
            return;
        }
        val finalAmount = Math.min(item.getAmount(), amount);
        val usedAmount = inventory.deleteItem(new Item(item.getId(), finalAmount)).getSucceededAmount();
        player.getVariables().setBonusXP(player.getVariables().getBonusXP() + (usedAmount * (int) TimeUnit.MINUTES.toTicks(5)));
        player.getVarManager().sendVar(3801, (int) (player.getVariables().getBonusXP() * 0.6F));
        player.sendMessage(Colour.RED.wrap("You've read " + usedAmount + " tome" + (usedAmount == 1 ? "" : "s") + " of experience and gained " + (usedAmount * 5) + " minutes of private bonus " +
                "experience."));
        player.setGraphics(graphics);
    }

    @Override
    public int[] getItems() {
        return new int[] {
                TOME_OF_EXPERIENCE
        };
    }
}
