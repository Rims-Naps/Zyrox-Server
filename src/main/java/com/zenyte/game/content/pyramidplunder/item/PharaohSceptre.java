package com.zenyte.game.content.pyramidplunder.item;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemChain;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class PharaohSceptre extends ItemPlugin {
    private static final ItemChain CHAIN = ItemChain.PHARAOH_SCEPTRE;

    @Override
    public void handle() {
        bind("Jalsavrah", (player, item, container, slotId) -> handleTeleport(player, container, slotId,
                PharaohSceptreDestination.JALSAVRAH));
        bind("Jaleustrophos", (player, item, container, slotId) -> handleTeleport(player, container, slotId,
                PharaohSceptreDestination.JALEUSTROPHOS));
        bind("Enakhra's Temple", (player, item, container, slotId) -> handleTeleport(player, container, slotId,
                PharaohSceptreDestination.QUARRY));
    }

    private void handleTeleport(final Player player, final Container container, final int slotId,
                                final PharaohSceptreDestination destination) {
        val sceptre = container.get(slotId);
        if (checkCharges(player, sceptre)) {
            val teleport = new PharaohSceptreTeleport(destination, container, sceptre, slotId);
            teleport.teleport(player);
        }
    }

    private boolean checkCharges(@NotNull final Player player, @NotNull final Item sceptreItem) {
        if (sceptreItem.getId() == ItemChain.PHARAOH_SCEPTRE.first()) {
            player.getDialogueManager().start(new PlainChat(player, "This sceptre has no charges, talk to the " +
                    "Guardian Mummy in the Jalsavrah Pyramid to recharge it."));
            return false;
        }
        return true;
    }

    @Override
    public int[] getItems() {
        return CHAIN.getItems().toIntArray();
    }

    @RequiredArgsConstructor
    private static class PharaohSceptreTeleport implements Teleport {
        private final PharaohSceptreDestination destination;
        private final Container container;
        private final Item currentSceptre;
        private final int slotId;

        @Override
        public void onUsage(final Player player) {
            val newSceptreId = CHAIN.before(currentSceptre.getId());
            container.set(slotId, new Item(newSceptreId));
            container.refresh(player);
            sendMessage(player, newSceptreId);
        }

        private void sendMessage(final Player player, final int newSceptreId) {
            val emptyMessage = "The sceptre has run out of charges. Talk to the Guardian Mummy in the Jalsavrah Pyramid " +
                    "to recharge it.";
            val charges = CHAIN.getItems().indexOf(newSceptreId);
            val remainingMessage = "The sceptre has " + charges + " charge" + (charges == 1 ? "" : "s") + " left.";
            val messageToSend = newSceptreId == CHAIN.first() ? emptyMessage : remainingMessage;
            player.sendMessage(Colour.RS_PURPLE.wrap(messageToSend));
        }

        @Override
        public TeleportType getType() {
            return TeleportType.PHARAOH_SCEPTRE;
        }

        public ImmutableLocation getDestination() {
            return destination.getLocation();
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public double getExperience() {
            return 0;
        }

        @Override
        public int getRandomizationDistance() {
            return 0;
        }

        @Override
        public Item[] getRunes() {
            return null;
        }

        @Override
        public int getWildernessLevel() {
            return 30;
        }

        @Override
        public boolean isCombatRestricted() {
            return UNRESTRICTED;
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum PharaohSceptreDestination {
        JALSAVRAH(new ImmutableLocation(1933, 4427, 3)),
        JALEUSTROPHOS(new ImmutableLocation(3341, 2827, 0)),
        QUARRY(new ImmutableLocation(3191, 2924, 0));
        private final ImmutableLocation location;
    }
}
