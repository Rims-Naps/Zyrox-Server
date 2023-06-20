package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.NotificationSettings;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.dialogue.RingOfWealthD;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Kris | 26. aug 2018 : 16:32:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class RingOfWealthItem extends ItemPlugin implements ItemOnItemAction {
    
    private static final int RING_OF_WEALTH_IMBUE_SCROLL = 12783;
    private static final int RING_OF_WEALTH_IMBUE_GP_COST = 50_000;
    
    public static final boolean isRingOfWealth(final Item item) {
        return item != null && RingOfWealth.values.containsKey(item.getId());
    }
    
    @Override
    public void handle() {
        bind("Features", (player, item, slotId) -> player.getDialogueManager().start(new RingOfWealthD(player)));
        bind("Rub", (player, item, container, slotId) -> {
            player.sendMessage("You rub the amulet...");
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Where would you like to teleport to?",
                            new DialogueOption("Miscellania", () -> new RingTeleport(item, container, slotId, new Location(2538, 3862, 0)).teleport(player)),
                            new DialogueOption("Grand Exchange", () -> new RingTeleport(item, container, slotId, new Location(3164, 3469, 0)).teleport(player)),
                            new DialogueOption("Falador", () -> new RingTeleport(item, container, slotId, new Location(2995, 3375, 0)).teleport(player)),
                            new DialogueOption("Dondakan", () -> new RingTeleport(item, container, slotId, new Location(2830, 10166, 0)).teleport(player)),
                            new DialogueOption("Nowhere"));
                }
            });
        });
        bind("Miscellania", (player, item, container, slotId) -> new RingTeleport(item, container, slotId, new Location(2538, 3862, 0)).teleport(player));
        bind("Grand Exchange", (player, item, container, slotId) -> new RingTeleport(item, container, slotId, new Location(3164, 3469, 0)).teleport(player));
        bind("Falador", (player, item, container, slotId) -> new RingTeleport(item, container, slotId, new Location(2995, 3375, 0)).teleport(player));
        bind("Dondakan", (player, item, container, slotId) -> new RingTeleport(item, container, slotId, new Location(2830, 10166, 0)).teleport(player));
        bind("Boss Log", (player, item, container, slotId) -> player.getNotificationSettings().sendKillLog(NotificationSettings.BOSS_NPC_NAMES, true));
        bind("Coin Collection", (player, item, container, slotId) -> {
            player.getSettings().setSetting(Setting.ROW_CURRENCY_COLLECTOR, player.getBooleanSetting(Setting.ROW_CURRENCY_COLLECTOR) ? 0 : 1);
            player.getDialogueManager().start(new PlainChat(player, "Currency collector has been turned " + (player.getBooleanSetting(Setting.ROW_CURRENCY_COLLECTOR) ? "off." : "on" +
                                                                                                                                                                                          ".")));
        });
    }
    
    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        if (player.getInventory().getAmountOf(995) < RING_OF_WEALTH_IMBUE_GP_COST) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("You need " + Utils.format(RING_OF_WEALTH_IMBUE_GP_COST) + "gp to imbue your Ring of Wealth.");
                }
            });
            return;
        }
        
        val unimbuedRing = RingOfWealth.get(from.getId() == RING_OF_WEALTH_IMBUE_SCROLL ? to : from);
        
        val ringSlot = player.getInventory().getItem(fromSlot).getId() == RING_OF_WEALTH_IMBUE_SCROLL ? toSlot : fromSlot;
        val scrollSlot = fromSlot == ringSlot ? toSlot : fromSlot;
        
        Item imbuedRing = null;
        for (val ring : RingOfWealth.values.values()) {
            if (ring.imbued && ring.charges == unimbuedRing.charges) {
                imbuedRing = ring.item;
                break;
            }
        }
        Item finalImbuedRing = imbuedRing;
        
        if (finalImbuedRing == null) {
            throw new RuntimeException("Null imbued ring of wealth for '" + player.getName() + "'");
        }
        
        player.getInventory().set(scrollSlot, null);
        player.getInventory().set(ringSlot, finalImbuedRing);
        player.getInventory().deleteItem(995, RING_OF_WEALTH_IMBUE_GP_COST);
        
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                item(finalImbuedRing, "You have imbued your Ring of Wealth.");
            }
        });
    }
    
    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[]{new ItemPair(RingOfWealth.UNCHARGED.item.getId(), RING_OF_WEALTH_IMBUE_SCROLL),
                new ItemPair(RingOfWealth.ONE.item.getId(), RING_OF_WEALTH_IMBUE_SCROLL),
                new ItemPair(RingOfWealth.TWO.item.getId(), RING_OF_WEALTH_IMBUE_SCROLL),
                new ItemPair(RingOfWealth.THREE.item.getId(), RING_OF_WEALTH_IMBUE_SCROLL),
                new ItemPair(RingOfWealth.FOUR.item.getId(), RING_OF_WEALTH_IMBUE_SCROLL),
                new ItemPair(RingOfWealth.FIVE.item.getId(), RING_OF_WEALTH_IMBUE_SCROLL)};
    }
    
    @Override
    public int[] getItems() {
        return RingOfWealth.values.keySet().toIntArray();
    }
    
    @Getter
    @RequiredArgsConstructor
    private enum RingOfWealth {
        UNCHARGED(new Item(2572), null, 0),
        ONE(new Item(11988), new Item(2572), 1),
        TWO(new Item(11986), new Item(11988), 2),
        THREE(new Item(11984), new Item(11986), 3),
        FOUR(new Item(11982), new Item(11984), 4),
        FIVE(new Item(11980), new Item(11982), 5),
        IMBUED_UNCHARGED(new Item(12785), null, 0, true),
        IMBUED_ONE(new Item(20790), new Item(12785), 1, true),
        IMBUED_TWO(new Item(20789), new Item(20790), 2, true),
        IMBUED_THREE(new Item(20788), new Item(20789), 3, true),
        IMBUED_FOUR(new Item(20787), new Item(20788), 4, true),
        IMBUED_FIVE(new Item(20786), new Item(20787), 5, true);
        
        private static final Int2ObjectMap<RingOfWealth> values = new Int2ObjectOpenHashMap<>();
        
        static {
            for (val value : values()) {
                values.put(value.item.getId(), value);
            }
        }
        
        private final Item item, result;
        private final int charges;
        private final boolean imbued;
        
        RingOfWealth(final Item item, final Item result, final int charges) {
            this.item = item;
            this.result = result;
            this.charges = charges;
            this.imbued = false;
        }
        
        @NotNull
        private static final RingOfWealth get(@NotNull final Item item) {
            return Objects.requireNonNull(values.get(item.getId()));
        }
        
    }
    
    
    @AllArgsConstructor
    private static final class RingTeleport implements Teleport {
        private final Item item;
        private final Container container;
        private final int slotId;
        private final Location destination;
        
        @Override
        public TeleportType getType() {
            return TeleportType.REGULAR_TELEPORT;
        }
        
        @Override
        public void teleport(final Player player) {
            final RingOfWealth ring = RingOfWealth.get(item);
            if (ring.getCharges() <= 0) {
                player.sendMessage("Your ring hasn't got any charges left.");
                return;
            }
            Teleport.super.teleport(player);
        }
        
        @Override
        public Location getDestination() {
            return destination;
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
        
        @Override
        public void onArrival(final Player player) {
            val ring = RingOfWealth.get(item);
            val charges = ring.getCharges();
            val message = charges == 1 ?
                          "<col=7F00FF>You use your amulet's last charge." :
                          charges == 2 ?
                          "<col=7F00FF>Your ring of wealth has one charge left..</col>" :
                          "<col=7F00FF>Your ring of wealth has " + Utils.format(charges - 1) + " charges left.</col>";

            player.sendMessage(message);
            val result = ring.getResult();
            container.set(slotId, result);
            container.refresh(player);
        }
    }
    
}
