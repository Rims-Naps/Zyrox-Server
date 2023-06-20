package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.item.enums.ImbueableItem;
import com.zenyte.game.item.pluginextensions.ChargeExtension;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.IntArray;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.variables.PlayerVariables;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

/**
 * @author Cresinkel
 */
public class RingOfEndurance extends ItemPlugin implements PairedItemOnItemPlugin, ChargeExtension {

    private static final int STAMINA_POTION = 12625;
    private static final int NOTED_STAMINA_POTION = 12626;
    private static final int[] STAMINAS = new int[]{STAMINA_POTION, NOTED_STAMINA_POTION};

    private static final int RING_OF_ENDURANCE_UNCHARGED = 32234;
    private static final int RING_OF_ENDURANCE = 32236;

    private static final int[] SUFFERINGS = new int[]{RING_OF_ENDURANCE_UNCHARGED, RING_OF_ENDURANCE};

    @Override
    public void handle() {
        bind("Uncharge", this::uncharge);
        bind("Check", this::checkCharges);
    }

    private void removeStamina(final Player player, final Item item, final Container container, final int slotId) {
        player.getDialogueManager().start(new Dialogue(player) {

            @Override
            public void buildDialogue() {
                item(item, "Uncharging your <col=00080>" + item.getName() + "</col> will not grant you back the stamina potions.");
                options("Are you sure you want to uncharge your <col=00080>" + item.getName() + "</col>?", "Yes, I'm sure.", "No.")
                        .onOptionOne(() -> {
                            if (container.get(slotId) == item) {
                                container.set(slotId, new Item(RING_OF_ENDURANCE_UNCHARGED));
                                container.refresh(player);
                            }
                            setKey(5);
                        });
                item(5, item, "Your <col=00080>" + item.getName() + "</col> was successfully uncharged.");
            }
        });
    }

    private void uncharge(final Player player, final Item item, final Container container, final int slotId) {
        if (item.getId() == 32236) {
            removeStamina(player, item, container, slotId);
        }
    }

    private void checkCharges(final Player player, final Item item, final Container container, final int slotId) {
        if (item.getId() == 32236) {
            val charges = item.getCharges();
            player.sendMessage("Your ring of endurance has " + charges + " stamina charges left.");
        }
    }


    @Override
    public int[] getItems() {
        return IntArray.of(RING_OF_ENDURANCE);
    }

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val stamina = ArrayUtils.contains(STAMINAS, from.getId()) ?
                     from :
                     to;
        val staminaId = stamina.getId();
        val ringOfEndurance = stamina == from ?
                        to :
                        from;
        val charges = ringOfEndurance.getCharges();
        player.sendInputInt("How many staminas do you wish to use?", value -> {
            val inventory = player.getInventory();
            val maxAmount = (int) Math.ceil((1000 - charges) / 4F);
            if (maxAmount == 0) {
                player.sendMessage("Your ring of endurance is already fully charged.");
                return;
            }
            val inInventory = (int) (inventory.getAmountOf(STAMINAS) & 0x7FFFFFFF);
            val amount = Math.min(maxAmount, Math.min(inInventory, value));
            if (amount == 0) {
                return;
            }
            int total = 0;
            val result = inventory.deleteItem(staminaId, amount);
            total += result.getSucceededAmount();
            if (result.getSucceededAmount() != amount) {
                val remainder = amount - result.getSucceededAmount();
                val additionalResult = inventory.deleteItem(staminaId == STAMINA_POTION ? NOTED_STAMINA_POTION : STAMINA_POTION, remainder);
                total += additionalResult.getSucceededAmount();
            }
            int chargesToAdd = (total * 4);
            ringOfEndurance.setCharges(Math.min(1000, charges + chargesToAdd));
            player.sendMessage(
                    "You load your ring with " + total + " stamina potions. It now has " + ringOfEndurance.getCharges() + " stamina charges.");
            val ringOfEnduranceId = ringOfEndurance.getId();
            if (ringOfEnduranceId == RING_OF_ENDURANCE_UNCHARGED) {
                ringOfEndurance.setId(RING_OF_ENDURANCE);
            }
            player.getInventory().refreshAll();
        });
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        val list = new ArrayList<ItemPair>();
        for (int stamina : STAMINAS) {
            for (final int suffering : SUFFERINGS) {
                list.add(ItemPair.of(stamina, suffering));
            }
        }
        return list.toArray(new ItemPair[0]);
    }

    @Override
    public void removeCharges(final Player player, final Item item, final ContainerWrapper wrapper, int slotId, final int amount) {
        if (item.getCharges() == 0) {
            return;
        }
        item.setCharges(Math.max(0, item.getCharges() - amount));
        if (item.getCharges() <= 0) {
            val id = item.getId();
            if (id == RING_OF_ENDURANCE) {
                item.setId(RING_OF_ENDURANCE_UNCHARGED);
            }
            player.sendMessage("<col=ff0000>Your ring of endurance has ran out of charges.");
        }
        player.getInventory().refreshAll();
    }

}
