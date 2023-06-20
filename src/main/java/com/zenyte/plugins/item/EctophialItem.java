package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import lombok.AllArgsConstructor;

/**
 * @author Kris | 11/06/2019 16:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EctophialItem extends ItemPlugin {
    @Override
    public void handle() {
        bind("Empty", (player, item, container, slotId) -> new EctophialTeleport(container, slotId).teleport(player));
    }

    @AllArgsConstructor
    private static final class EctophialTeleport implements Teleport {
        private final Container container;

        @Override
        public void onUsage(final Player player) {
            container.set(slotId, new Item(4252));
            container.refresh(player);
        }

        private final int slotId;

        @Override
        public TeleportType getType() {
            return TeleportType.ECTOPHIAL;
        }

        @Override
        public Location getDestination() {
            return new Location(3659, 3524, 0);
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
            return 20;
        }

        @Override
        public boolean isCombatRestricted() {
            return UNRESTRICTED;
        }

        @Override
        public void onArrival(final Player player) {
            container.set(slotId, new Item(4251));
            container.refresh(player);
        }
    }


    @Override
    public int[] getItems() {
        return new int[] {
                4251
        };
    }
}
