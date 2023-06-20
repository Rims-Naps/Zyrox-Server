package com.zenyte.game.content.skills.farming.seedvault;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.*;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.bank.PlaceholderRedirections;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.utils.StaticInitializer;
import lombok.Getter;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
@StaticInitializer
public class SeedVault {
    private transient final Player player;
    private final SeedVaultContainer container;

    @Subscribe
    public static final void onInit(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val vault = player.getSeedVault();
        if (savedPlayer == null) {
            return;
        }
        val savedVault = savedPlayer.getSeedVault();
        if (savedVault == null)
            return;
        vault.container.setContainer(savedVault.container);
    }

    public SeedVault(@NotNull final Player player) {
        this.player = player;
        this.container = new SeedVaultContainer(this, ContainerPolicy.ALWAYS_STACK, ContainerType.SEED_VAULT, Optional.of(player));
    }

    public static boolean canDeposit(@NotNull final Item item) {
       return SeedVaultInterface.getCategoryString(item).isPresent();
    }

    public void switchItem(final int fromSlot, final int toSlot) {
        val fromItem = container.get(fromSlot);
        val toItem = container.get(toSlot);
        container.set(fromSlot, toItem);
        container.set(toSlot, fromItem);
        container.setFullUpdate(true);
        container.refresh(player);
    }

    public ContainerResult add(final Item requestedItem) {
        val item = new Item(requestedItem);
        val defs = item.getDefinitions();

        if (defs == null) {
            val result = new ContainerResult(item, ContainerState.ADD);
            result.setResult(RequestResult.FAILURE);
            return result;
        }
        if (defs.isNoted()) {
            item.setId(defs.getNotedId());
        }
        if (!canDeposit(item)) {
            val result = new ContainerResult(item, ContainerState.ADD);
            result.setResult(RequestResult.FAILURE);
            return result;
        }
        val defId = defs.getUnnotedOrDefault();
        val placeholderId =
                ItemDefinitions.getOrThrow(PlaceholderRedirections.builder.getOrDefault(defId, defId)).getPlaceholderId();
        val placeholderSlot = container.getSlotOf(placeholderId);
        if (item.hasAttributes() || container.getSlotOf(item.getId()) == -1 && (placeholderId == -1 || placeholderSlot == -1)) {
            val result = new ContainerResult(item, ContainerState.ADD);
            if (container.getFreeSlotsSize() == 0) {
                if (placeholderSlot != -1 && item.hasAttributes()) {
                    container.set(placeholderSlot, new Item(requestedItem.getId(), 1, requestedItem.getAttributes()));
                    result.setSucceededAmount(1);
                    result.setResult(RequestResult.SUCCESS);
                    return result;
                }
                result.setResult(RequestResult.NOT_ENOUGH_SPACE);
                return result;
            }
            if (placeholderSlot != -1) {
                container.set(placeholderSlot, item);
                result.setSucceededAmount(item.getAmount());
                result.setResult(RequestResult.SUCCESS);
                return result;
            }
            val index = container.getAvailableSlots().firstInt();
            if (container.get(index) != null) {
                result.setResult(RequestResult.FAILURE);
                return result;
            }
            container.set(index, item);
            result.setSucceededAmount(item.getAmount());
            result.setResult(RequestResult.SUCCESS);
            return result;
        }
        return container.add(item);
    }

    public ContainerResult remove(final Item item) {
        return remove(container.getSlotOf(item.getId()), item, false);
    }

    public ContainerResult remove(final Item item, final boolean placeholder) {
        return remove(container.getSlotOf(item.getId()), item, placeholder);
    }

    public ContainerResult remove(final int slot, final int amount, final boolean placeholder) {
        val item = container.get(slot);
        return remove(slot, new Item(item == null ? -1 : item.getId(), amount), placeholder);
    }

    public ContainerResult remove(final int slot, final Item item, final boolean placeholder) {
        return container.remove(item, placeholder, player);
    }

    public void deposit(final Container fromContainer, final int fromSlot, final int amount, boolean notifyOnFailure) {
        if (amount <= 0) {
            return;
        }
        val item = fromContainer.get(fromSlot);
        if (!canDeposit(item)) {
            if (notifyOnFailure) {
                player.sendMessage("You cannot deposit that item in the Seed vault.");
            }
            return;
        }
        container.deposit(player, fromContainer, fromSlot, amount);
    }

    public void releasePlaceholders() {
        val length = container.getContainerSize();
        for (int i = 0; i < length; i++) {
            val item = container.get(i);
            if (item == null) {
                continue;
            }
            val defs = item.getDefinitions();
            if (defs == null) {
                continue;
            }
            if (defs.isPlaceholder()) {
                this.remove(i--, 1, false);
            }
        }
        container.refresh(player);
    }

    public void releasePlaceholder(final int slot) {
        val item = container.get(slot);
        val defs = item.getDefinitions();
        if (defs.isPlaceholder()) {
            this.remove(slot, 1, false);
        }
        container.refresh(player);
    }
}