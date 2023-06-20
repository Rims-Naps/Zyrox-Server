package com.zenyte.game.content.kebos.konar.plugins.item;

import com.zenyte.game.content.skills.prayer.ectofuntus.Bonecrusher;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Tommeh | 26/10/2019 | 16:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class BonecrusherNecklace extends ItemPlugin implements ItemOnItemAction {

    public static final Item bonecrusher = new Item(ItemId.BONECRUSHER);
    public static final Item dragonBoneNecklace = new Item(ItemId.DRAGONBONE_NECKLACE);
    public static final Item hydraTail = new Item(ItemId.HYDRA_TAIL);
    public static final Item bonecrusherNecklace = new Item(ItemId.BONECRUSHER_NECKLACE);

    @Override
    public void handle() {
        bind("Check", (player, item, slotId) -> {
            val charges = player.getNumericAttribute("bonecrusher charges").intValue();
            player.sendMessage("Your bonecrusher has " + charges + " charge" + (charges == 1 ? "" : "s") + " remaining.");
        });
        bind("Wear", (player, item, slotId) -> {
            player.getEquipment().wear(slotId);
            player.addTemporaryAttribute("bonecrusher_necklace_effect_delay", Utils.currentTimeMillis() + 9000);
        });
        bind("Activity", (player, item, container, slotId) -> {
            player.getSettings().toggleSetting(Setting.BONECRUSHING_INACTIVE);
            player.sendMessage(!Bonecrusher.enabled(player) ? "Your bonecrusher necklace is no longer crushing bones." : "Your bonecrusher necklace is now crushing bones.");
        });
        bind("Check/Uncharge", (player, item, slotId) -> {
            val charges = Bonecrusher.getCharges(player);
            val ectotokens = (int) (charges / 25F);
            if (ectotokens > 0) {
                player.getInventory().addOrDrop(new Item(4278, ectotokens));
                player.sendMessage("You uncharge the bonecrusher necklace and receive " + ectotokens + " ecto-tokens.");
            } else {
                player.sendMessage("Your bonecrusher necklace has no charges remaining.");
            }
            Bonecrusher.addCharges(player, -charges);
        });
        bind("Uncharge", (player, item, slotId) -> {
            val charges = Bonecrusher.getCharges(player);
            val ectotokens = (int) (charges / 25F);
            if (ectotokens > 0) {
                player.getInventory().addOrDrop(new Item(4278, ectotokens));
                player.sendMessage("You uncharge the bonecrusher necklace and receive " + ectotokens + " ecto-tokens.");
            } else {
                player.sendMessage("Your bonecrusher necklace has no charges remaining.");
            }
            Bonecrusher.addCharges(player, -charges);
        });
        bind("Dismantle", (player, item, slotId) -> {
            if (!player.getInventory().checkSpace(1)) {
                return;
            }
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    item(bonecrusherNecklace, "Are you sure you wish to dismantle the Bonecrusher Necklace into a Hydra Tail, Dragonbone necklace and Bonecrusher?");
                    options("Dismantle the Bonecrusher necklace?", "Yes.", "No.")
                            .onOptionOne(() -> {
                                player.getInventory().deleteItemsIfContains(new Item[]{bonecrusherNecklace}, () -> {
                                    player.getInventory().addOrDrop(bonecrusher, hydraTail, dragonBoneNecklace);
                                    player.sendMessage("You have successfully dismantled your Bonecrusher necklace into a Hydra Tail, Dragonbone necklace and Bonecrusher.");
                                });
                            });
                }
            });
        });
    }

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val tokens = from.getId() == ItemId.ECTOTOKEN ? from : to;
        int eligibleCharges = tokens.getAmount() * 25;
        val crusherCharges = Bonecrusher.getCharges(player);
        if (crusherCharges + eligibleCharges < 0) {
            eligibleCharges = Integer.MAX_VALUE - crusherCharges;
            eligibleCharges -= eligibleCharges % 25;
        }
        if (eligibleCharges <= 0) {
            player.sendMessage("Your bonecrusher necklace can't hold anymore charges.");
            return;
        }
        player.getInventory().deleteItem(new Item(tokens.getId(), eligibleCharges / 25));
        Bonecrusher.addCharges(player, eligibleCharges);
        player.sendMessage("You add " + eligibleCharges + " charges in your bonecrusher necklace. It now holds " + Bonecrusher.getCharges(player) + " charges total.");
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {ItemPair.of(ItemId.ECTOTOKEN, ItemId.BONECRUSHER_NECKLACE)};
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.BONECRUSHER_NECKLACE};
    }
}
