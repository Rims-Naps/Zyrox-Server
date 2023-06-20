package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.godwars.instance.InstanceConstants;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kris | 10/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DyingKnight extends NPCPlugin {

    private static final String key = "godwars instance guide";

    public static final boolean canUsePortal(@NotNull final Player player) {
        return player.getNumericAttribute(DyingKnight.key).intValue() >= (0x1 | 0x2 | 0x4);
    }

    @AllArgsConstructor
    private enum KnightLocation {
        BANDOS(new ImmutableLocation(2856, 5364, 2), KillcountNPC.GodType.BANDOS),
        ZAMORAK(new ImmutableLocation(2936, 5351, 2), KillcountNPC.GodType.ZAMORAK),
        SARADOMIN(new ImmutableLocation(2923, 5259, 0), KillcountNPC.GodType.SARADOMIN),
        ARMADYL(new ImmutableLocation(2828, 5292, 2), KillcountNPC.GodType.ARMADYL);

        private final Location location;
        private final KillcountNPC.GodType god;

        private static final List<KnightLocation> values = Collections.unmodifiableList(Arrays.asList(values()));
    }

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                if (player.getNumericAttribute(DyingKnight.key).intValue() == 0) {
                    player("What are you doing here? It's not safe!");
                    npc("Dying Knight", "I fell into the deep hole above.");
                    npc("Dying Knight", "A pile of snow eased my fall. After that, I ran for my life, trying to dodge the creatures deep within.");
                    npc("Dying Knight", "Eventually I came across a crevice that looked large enough to go through. I've been dwelling in here since.").executeAction(() -> {
                        player.addAttribute(DyingKnight.key, player.getNumericAttribute(DyingKnight.key).intValue() | 0x1);
                    });
                }

                options(new DialogueOption("What's that portal over there?", key(100)),
                        new DialogueOption("What happens if I die within the instance?", key(200)),
                        new DialogueOption("Nothing."));

                player(100, "What's that portal over there?");
                npc("Dying Knight", "Ah yes, the portal. It lets you and your clan mates create an instance of this area.");
                npc("Dying Knight", "The instance will cost you " + Utils.format(InstanceConstants.INSTANCE_COST) + " coins to start. Any clan members " +
                        "will be able to join free-of-charge after that.");
                val gods = new ObjectArrayList<>(KnightLocation.values);
                gods.sort((k1, k2) -> k1.location.getTileDistance(k2.location));
                val closestGod = gods.get(0);
                npc("Dying Knight", "Inside the instance, you'll mainly find creatures of " + Utils.formatString(closestGod.god.name()) + ", " +
                        "whom you may devour to gain access to the main chamber. I dread to think of the horrors inside there.");
                npc("Dying Knight", "Should every one of you leave the instance, it will collapse. Any items left in it will vanish.").executeAction(() -> {
                    if (!canUsePortal(player)) {
                        player.addAttribute(DyingKnight.key, player.getNumericAttribute(DyingKnight.key).intValue() | 0x2);
                        if (canUsePortal(player)) {
                            setKey(300);
                            if (player.getTemporaryAttributes().get("last hint arrow") != null) {
                                player.getPacketDispatcher().resetHintArrow();
                            }
                        }
                    }
                });
                options(new DialogueOption("What happens if I die within the instance?", key(200)),
                        new DialogueOption("Nothing."));

                player(200, "What happens if I die within the instance?");
                npc("Dying Knight", "Should you die in the instance, regardless of whether you were in the chamber or not, you'll find your items with me.");
                npc("Dying Knight", "I won't give them back for free though, I'll want " + Utils.format(ItemRetrievalService.RetrievalServiceType.GODWARS.getCost()) + " " +
                        "coins for putting my neck out there. I won't be picking up anything you leave on the ground on your own will though.").executeAction(() -> {
                    if (!canUsePortal(player)) {
                        player.addAttribute(DyingKnight.key, player.getNumericAttribute(DyingKnight.key).intValue() | 0x4);
                        if (canUsePortal(player)) {
                            setKey(300);
                            if (player.getTemporaryAttributes().get("last hint arrow") != null) {
                                player.getPacketDispatcher().resetHintArrow();
                            }
                        }
                    }
                });
                options(new DialogueOption("What's that portal over there?", key(100)),
                        new DialogueOption("Nothing."));

                plain(300, "You may now enter the portal.");
            }
        }));

        bind("Collect", (player, npc) -> {
            if (player.getRetrievalService().getType() != ItemRetrievalService.RetrievalServiceType.GODWARS || player.getRetrievalService().getContainer().isEmpty()) {
                player.getDialogueManager().start(new Dialogue(player, npc) {
                    @Override
                    public void buildDialogue() {
                        npc("There's nothing to collect at this time.");
                    }
                });
                return;
            }
            GameInterface.ITEM_RETRIEVAL_SERVICE.open(player);
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                10023
        };
    }
}
