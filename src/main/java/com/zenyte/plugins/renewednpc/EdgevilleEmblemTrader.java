package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import mgi.types.config.items.ItemDefinitions;
import com.zenyte.plugins.dialogue.PlainChat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 07/05/2019 19:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EdgevilleEmblemTrader extends NPCPlugin {
    @Override
    public void handle() {
        bind("Trade", (player, npc) -> GameInterface.BOUNTY_HUNTER_STORE.open(player));
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("Hello, wanderer.");
                npc("Don't suppose you've come across any strange... emblems or artifacts along your journey?");
                if (hasEmblem(player)) {
                    player("Yes, yes I have.");
                    npc("Would you like to sell them all to me for " + Utils.format(getEmblemsCost(player)) + " Bounty Hunter points?");
                    options("Sell all mysterious emblems?",
                            new DialogueOption("Sell all emblems.", () -> sellEmblems(player)),
                            new DialogueOption("No, keep the emblems."));
                    return;
                }
                player("Not that I've seen.");
                npc("If you do, please do let me know. I'll reward you handsomely.");
                val optionList = new ObjectArrayList<DialogueOption>();
                optionList.add(new DialogueOption("What rewards have you got?", () -> GameInterface.BOUNTY_HUNTER_STORE.open(player)));
                if (player.getVariables().isSkulled()) {
                    optionList.add(new DialogueOption("Can you make my PK skull last longer?", () -> promptSkull(player)));
                } else {
                    optionList.add(new DialogueOption("Can I have a PK skull, please?", () -> promptSkull(player)));
                }
                optionList.add(new DialogueOption("Can you note the potions I buy from you?", key(50)));
                optionList.add(new DialogueOption("That's nice.", key(25)));
                options(TITLE, optionList.toArray(new DialogueOption[0]));
                player(25, "That's nice.");
                player(50, "Can you note the potions I buy from you?").executeAction(() -> {
                    player.putBooleanAttribute("notedBH", !player.getBooleanAttribute("notedBH"));
                });
                npc("Sure, I will give you your potions " + (player.getBooleanAttribute("notedBH") ? "unnoted" : "noted") + " from now on.");
            }
        }));
        bind("Skull", (player, npc) -> promptSkull(player));
        bind("Toggle-notes", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                player("Can you note the potions I buy from you?").executeAction(() -> {
                    player.putBooleanAttribute("notedBH", !player.getBooleanAttribute("notedBH"));
                });
                npc("Sure, I will give you your potions " + (player.getBooleanAttribute("notedBH") ? "unnoted" : "noted") + " from now on.");
            }
        }));
    }

    private final void promptSkull(@NotNull final Player player) {
        player.getDialogueManager().finish();
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Obtain a PK skull?", new DialogueOption("Yes, skull me.", () -> player.getVariables().setSkull(true)), new DialogueOption("No, don't skull me."));
            }
        });
    }

    private final boolean hasEmblem(@NotNull final Player player) {
        val inventory = player.getInventory();
        for (int i = 0; i < 28; i++) {
            val item = inventory.getItem(i);
            if (item == null || !(item.getId() >= 12746 && item.getId() <= 12756)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private final int getEmblemsCost(@NotNull final Player player) {
        val inventory = player.getInventory();
        int cost = 0;
        for (int i = 0; i < 28; i++) {
            val item = inventory.getItem(i);
            if (item == null || !(item.getId() >= 12746 && item.getId() <= 12756)) {
                continue;
            }
            cost += item.getAmount() * Emblem.getCost(item.getId());
        }
        return cost;
    }

    private final void sellEmblems(@NotNull final Player player) {
        val inventory = player.getInventory();
        val bounty = player.getBountyHunter();
        for (int i = 0; i < 28; i++) {
            val item = inventory.getItem(i);
            if (item == null || !(item.getId() >= 12746 && item.getId() <= 12756)) {
                continue;
            }
            val count = inventory.deleteItem(item).getSucceededAmount();
            val cost = count * Emblem.getCost(item.getId());
            bounty.setPoints(bounty.getPoints() + cost);
        }
        player.getDialogueManager().start(new PlainChat(player, "You exchange all your mysterious emblems for Bounty Hunter points. You now have " + Utils.format(bounty.getPoints()) + " Bounty " +
                "Hunter points."));
    }

    @Override
    public int[] getNPCs() {
        return new int[]{
                315, 316
        };
    }

    @AllArgsConstructor
    private enum Emblem {
        TIER_ONE(12746, 50_000),
        TIER_TWO(12748, 100_000),
        TIER_THREE(12749, 200_000),
        TIER_FOUR(12750, 400_000),
        TIER_FIVE(12751, 750_000),
        TIER_SIX(12752, 1_200_000),
        TIER_SEVEN(12753, 1_750_000),
        TIER_EIGHT(12754, 2_500_000),
        TIER_NINE(12755, 3_500_000),
        TIER_TEN(12756, 5_000_000);

        private static final Emblem[] values = values();
        private final int id, cost;

        private static final int getCost(final int item) {
            val unnoted = ItemDefinitions.getOrThrow(item).getUnnotedOrDefault();
            for (val value : values) {
                if (value.id == unnoted) {
                    return value.cost;
                }
            }
            throw new IllegalStateException();
        }
    }
}
