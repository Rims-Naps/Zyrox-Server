package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.skills.herblore.actions.CombineExtendedAntifire;
import com.zenyte.game.content.skills.herblore.actions.CombineExtendedSuperAntifire;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.plugins.dialogue.SkillDialogue;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.val;

/**
 * @author Tommeh | 12/06/2019 | 16:54
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class LavaScaleOnAntifireAction implements ItemOnItemAction {

    public static final Int2IntOpenHashMap POTS = new Int2IntOpenHashMap(
            new int[] { 2452, 2454, 2456, 2458 },
            new int[] { 11951, 11953, 11955, 11957 }
    );

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        if (player.getSkills().getLevel(Skills.HERBLORE) < 84) {
            player.sendMessage("You need a Herblore level of at least 84 to brew extended antifire potions.");
            return;
        }
        val potion = from.getId() >= 2452 && from.getId() <= 2458 ? from : to;
        val slot = player.getInventory().getItem(fromSlot).getId() == potion.getId() ? fromSlot : toSlot;
        val dose = Integer.valueOf(potion.getName().substring(16, 17));
        if (!player.getInventory().containsItem(11994, dose)) {
            player.sendMessage("You don't have enough lava scale shards.");
            return;
        }
        //player.getInventory().deleteItem(11994, dose);
        //player.getInventory().set(slot, new Item(POTS.get(potion.getId())));
        //player.getSkills().addXp(Skills.HERBLORE, 27.5 * dose);
        player.getDialogueManager().start(new LavaScaleOnAntifireAction.ExtendedAntifireCreationDialogue(player, new Item(potion.getId())));
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(2452, 11994), ItemPair.of(2454, 11994),
                ItemPair.of(2456, 11994), ItemPair.of(2458, 11994)
        };
    }

    @Override
    public int[] getItems() {
        return null;
    }

    private static class ExtendedAntifireCreationDialogue extends SkillDialogue {
        private final Item potionToUpgradeItem;

        public ExtendedAntifireCreationDialogue(Player player, Item... items) {
            super(player, "How many would you like to make?", new Item(CombineExtendedAntifire.POTS.get(items[0].getId())));
            potionToUpgradeItem = items[0];
        }

        @Override
        public void run(int slotId, int amount) {
            player.getActionManager().setAction(new CombineExtendedAntifire(amount, potionToUpgradeItem.getId()));
        }
    }

}

