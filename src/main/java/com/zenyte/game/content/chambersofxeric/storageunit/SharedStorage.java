package com.zenyte.game.content.chambersofxeric.storageunit;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import mgi.types.config.enums.Enums;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * @author Kris | 4. mai 2018 : 18:59:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public final class SharedStorage implements Storage {

    /**
     * The container holding all the items in this shared storage.
     */
    private final Container container = new Container(ContainerPolicy.NORMAL, ContainerType.SHARED_STORAGE, Optional.empty());

    /**
     * The hashset containing all the players who have the shared storage interface currently open.
     */
    private final Set<Player> viewingPlayers = new ObjectOpenHashSet<>();

    /**
     * Opens the shared storage interface for the player.
     *
     * @param player the player who is opening the shared storage.
     */
    public void open(@NotNull final Player player) {
        GameInterface.RAIDS_SHARED_STORAGE.open(player);
    }

    @Override
    public void refresh() {
        for (val player : viewingPlayers) {
            if (player == null || player.isNulled()) {
                continue;
            }
            container.refresh(player);
        }
    }

    @Override
    public void deposit(@NotNull final Player player, final int slotId, final int amount) {
        val inventory = player.getInventory().getContainer();
        val item = inventory.get(slotId);
        if (item == null) {
            return;
        }
        val field = Enums.RAIDS_ONLY_ITEMS.getKey(item.getId());
        if (!field.isPresent()) {
            player.sendMessage("The shared storage unit cannot hold that item.");
            return;
        }
        container.deposit(player, inventory, slotId, amount);
        player.getInventory().refresh();
        refresh();
    }

    @Override
    public void withdraw(@NotNull final Player player, final int slot, final int amount, final boolean sendMessages) {
        container.withdraw(sendMessages ? player : null, player.getInventory().getContainer(), slot, amount);
        player.getInventory().refresh();
        refresh();
    }

}
