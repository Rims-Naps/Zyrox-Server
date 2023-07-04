package com.zenyte.game.content.grandexchange;

import com.google.gson.annotations.Expose;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.GameTab;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ContainerResult;
import com.zenyte.game.world.entity.player.container.RequestResult;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * @author Tommeh | 26 nov. 2017 : 18:58:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class GrandExchange {

    public static final int INTERFACE = 465;
    public static final int INVENTORY_INTERFACE = 467;
    public static final int TYPE_VARPBIT = 4397;
    public static final int ITEM_VARP = 1151;
    //static final Object LOCK = new Object();
    public static final int SLOT_VARPBIT = 4439;
    private static final int QUANTITY_VARPBIT = 4396;
    private static final int PRICE_VARP = 1043;
    public static final long OFFER_TIMEOUT_DELAY = TimeUnit.DAYS.toMillis(7);
    private final transient Player player;
    @Expose
    @Getter
    private LinkedList<ExchangeHistory> history = new LinkedList<>();

    public GrandExchange(final Player player) {
        this.player = player;
    }

    public final void initialize(final GrandExchange exchange) {
        if (exchange == null) {
            return;
        }
        if (exchange.history != null) {
            history = exchange.history;
        }
        cleanHistory();
    }

    private final void cleanHistory() {
        while (history.size() > 20) {
            history.poll();
        }
    }

    public void updateOffers() {
        for (val offer : getOffers().values()) {
            if (offer == null) {
                continue;
            }
            player.getPacketDispatcher().sendGrandExchangeOffer(offer);
        }
    }

    public void openOffersInterface() {
        if (player.isIronman()) {
            player.sendMessage("As an Iron Man, you cannot use the Grand Exchange.");
            return;
        }
        val handler = player.getInterfaceHandler();
        val dispatcher = player.getPacketDispatcher();
        reset();
        player.getTemporaryAttributes().put("GrandExchange", true);
        player.getVarManager().sendBit(SLOT_VARPBIT, 0);
        dispatcher.sendClientScript(828, 1);
        handler.closeInput();
        handler.sendInterface(InterfacePosition.CENTRAL, INTERFACE);
        handler.sendInterface(InterfacePosition.INVENTORY_TAB, INVENTORY_INTERFACE);
        handler.openGameTab(GameTab.INVENTORY_TAB);
        for (int i = 7; i <= 14; i++) {
            dispatcher.sendComponentSettings(INTERFACE, i, 2, 2, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2);
            dispatcher.sendComponentSettings(INTERFACE, i, 3, 4, AccessMask.CLICK_OP1);
        }
        dispatcher.sendComponentSettings(INTERFACE, 22, 0, 0, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(INTERFACE, 23, 2, 3, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP10);
        dispatcher.sendComponentSettings(INTERFACE, 6, 0, 0, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2);
        dispatcher.sendComponentSettings(INTERFACE, 24, 0, 13, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(INVENTORY_INTERFACE, 0, 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
        refreshOffers();
        player.setCloseInterfacesEvent(() -> {
            handler.sendInterface(InterfacePosition.INVENTORY_TAB, Inventory.INTERFACE);
            handler.closeInput();
            player.getTemporaryAttributes().remove("GrandExchange");
            reset();
        });
    }

    public void refreshOffers() {
        for (val offer : getOffers().values()) {
            if (offer == null) {
                continue;
            }
            offer.refreshItems();
        }
    }

    public void openHistoryInterface() {
        GameInterface.GRAND_EXCHANGE_HISTORY.open(player);
    }

    public void openItemSetsInterface() {
        GameInterface.ITEM_SETS.open(player);
    }

    public void sell(final int slot, final boolean dialogue) {
        reset();
        viewOffer(slot);
        val dispatcher = player.getPacketDispatcher();
        player.getVarManager().sendBit(TYPE_VARPBIT, 1);
        if (dialogue) {
            dispatcher.sendComponentText(INTERFACE, 25, "Choose an item from your inventory to sell.");
            dispatcher.sendComponentText(INTERFACE, 26, "");
            dispatcher.sendComponentText(INTERFACE, 16, "");
            dispatcher.sendComponentText(INTERFACE, 17, "");
        }
        player.getInterfaceHandler().openGameTab(GameTab.INVENTORY_TAB);
    }

    public boolean sell(Item item) {
        for(ExchangeOffer offer : getOffers().values())
        {
            if(offer != null)
            {
                if(offer.getType() == ExchangeType.BUYING && offer.getItem().getId() == item.getId())
                {
                    player.sendMessage("You may not have a sell offer for this item until you finish the buy offer you have for it.");
                    player.getVarManager().sendBit(SLOT_VARPBIT, 1);
                    player.getVarManager().sendBit(SLOT_VARPBIT, -1);
                    return false;
                }
            }
        }
        if (!item.isTradable()) {
            player.sendMessage("This item is untradeable.");
            player.getVarManager().sendBit(SLOT_VARPBIT, 1);
            player.getVarManager().sendBit(SLOT_VARPBIT, -1);
            return false;
        }
        if (item.getId() == 995 || item.getId() == 13224 || !item.getDefinitions().isGrandExchange()) {
            player.sendMessage("This item cannot be sold.");
            player.getVarManager().sendBit(SLOT_VARPBIT, 1);
            player.getVarManager().sendBit(SLOT_VARPBIT, -1);
            return false;
        }
        item = new Item(item);
        val dispatcher = player.getPacketDispatcher();
        if (item.getDefinitions().isNoted()) {
            item.setId(item.getDefinitions().getNotedId());
        }
        reset();
        setQuantity(item.getAmount());
        setItem(item.getId());
        setPrice(item.getSellPrice());
        player.getVarManager().sendBit(TYPE_VARPBIT, 1);
        dispatcher.sendComponentText(INTERFACE, 25, item.getDefinitions().getExamine());
        dispatcher.sendComponentText(INTERFACE, 26, item.getDefinitions().getName());
        return true;
    }

    public void buy(final int slot, final boolean dialogue) {
        val dispatcher = player.getPacketDispatcher();
        reset();
        viewOffer(slot);
        player.getVarManager().sendBit(TYPE_VARPBIT, 0);
        if (dialogue) {
            dispatcher.sendComponentText(INTERFACE, 25, "Click the icon on the left to search for items.");
            dispatcher.sendComponentText(INTERFACE, 16, "");
            dispatcher.sendComponentText(INTERFACE, 17, "");
            player.sendInputItem("What would you like to buy?", this::buy);
        }
    }

    public void buy(final Item item) {
        for(ExchangeOffer offer : getOffers().values())
        {
            if(offer != null)
            {
                if(offer.getType() == ExchangeType.SELLING && offer.getItem().getId() == item.getId())
                {
                    player.sendMessage("You may not have a buy offer for this item until you finish the sell offer you have for it.");
                    return;
                }
            }
        }
        val dispatcher = player.getPacketDispatcher();
        setItem(item.getId());
        setPrice(item.getSellPrice());
        setQuantity(1);
        player.getInterfaceHandler().closeInput();
        player.getVarManager().sendBit(TYPE_VARPBIT, 0);
        dispatcher.sendComponentText(INTERFACE, 25, item.getDefinitions().getExamine());
        dispatcher.sendComponentText(INTERFACE, 26, item.getDefinitions().getName());
    }

    public void viewOffer(final int slot) {
        val offer = getOffers().get(slot);
        if (offer != null) {
            offer.refreshItems();
            val item = offer.getItem();
            val dispatcher = player.getPacketDispatcher();
            val offerExpirationTime = System.currentTimeMillis() - OFFER_TIMEOUT_DELAY;
            val offerUpdateTime = offer.getLastUpdateTime();
            val builder = new StringBuilder();
            if (!offer.isCompleted() && !offer.isCancelled() && !offer.isAborted()) {
                if (offerUpdateTime < offerExpirationTime) {
                    builder.append(Colour.RED.wrap("Offer has expired."));
                } else {
                    val validDelay = offerUpdateTime - offerExpirationTime;
                    val days = TimeUnit.MILLISECONDS.toDays(validDelay);
                    val hours = TimeUnit.MILLISECONDS.toHours(validDelay) % 24;
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(validDelay) % 60;
                    builder.append(Colour.GREEN.toString()).append("Offer valid for ");
                    if (days > 0) {
                        builder.append(days).append(" day").append(days == 1 ? "" : "s");
                        if (hours > 0 && minutes <= 0) {
                            builder.append(" and ");
                        } else {
                            builder.append(", ");
                        }
                    }
                    if (hours > 0) {
                        builder.append(hours).append(" hour").append(hours == 1 ? "" : "s");
                        if (minutes > 0) {
                            builder.append(" and ");
                        } else {
                            builder.append(", ");
                        }
                    }
                    if (minutes > 0) {
                        builder.append(minutes).append(" minute").append(minutes == 1 ? "" : "s").append(", ");
                    }
                    if (builder.length() >= 2) {
                        builder.delete(builder.length() - 2, builder.length());
                    }
                    builder.append('.');
                    builder.append("<col>");
                }
            }
            dispatcher.sendComponentText(INTERFACE, 16, item.getDefinitions().getExamine() + "<br>" + builder.toString());
            dispatcher.sendComponentText(INTERFACE, 17, item.getDefinitions().getName());
        }
        player.getVarManager().sendBit(GrandExchange.SLOT_VARPBIT, slot + 1);
    }

    public void abortOffer(final int slot) {
        if (slot <= -1) {
            return;
        }
        val offer = getOffers().get(slot);
        if (offer == null || offer.isAborted()) {
            return;
        }
        player.sendMessage("Sending Abort request...");
        offer.cancel();
        offer.update();
        /*CoresManager.getGrandExchangeExecutor().submit(() -> {
            synchronized (GrandExchange.LOCK) {*/

        /*    }
            WorldTasksManager.schedule(offer::update);
        });*/
    }

    public void abortOffer() {
        abortOffer(player.getVarManager().getBitValue(GrandExchange.SLOT_VARPBIT) - 1);
    }

    public void collectItems(final int option, int slotId) {
        val slot = player.getVarManager().getBitValue(GrandExchange.SLOT_VARPBIT) - 1;
        if (slot <= -1) {
            return;
        }
        collectFromBox(true, slot, option, slotId - 2);
        /*val offer = getOffers().get(slot);
        if (offer == null) {
            return;
        }
        slotId -= 2;
        val item = offer.getContainer().get(slotId);
        if (item == null) {
            return;
        }

        val op = option == 1 || option == 2 ? (item.getAmount() > 1 ? (option == 1 ? 2 : 1) : option) : option;
        val offerItem = op == 2 ? item.toNote() : item;
        if (option == 1 || option == 2) {
            val result = player.getInventory().addItem(offerItem);
            if (result.getResult().equals(RequestResult.SUCCESS)) {
                offer.getContainer().remove(item);
            } else {
                if (result.getSucceededAmount() > 0) {
                    offer.getContainer().set(slotId, new Item(item.getId(), item.getAmount() - result.getSucceededAmount()));
                }
                player.sendMessage("Not enough space in your inventory.");
            }
        } else if (option == 3) {
            val result = player.getBank().add(offerItem);
            if (result.getResult().equals(RequestResult.SUCCESS)) {
                offer.getContainer().remove(item);
            } else {
                if (result.getSucceededAmount() > 0) {
                    offer.getContainer().set(slotId, new Item(item.getId(), item.getAmount() - result.getSucceededAmount()));
                }
                player.sendMessage("Not enough space in your bank.");
            }
        }
        offer.refreshItems();
        if (offer.getContainer().getFreeSlotsSize() == 2 && (offer.isCompleted() || offer.isCancelled() || offer.isAborted())) {
            offer.setCancelled(true);
            player.getPacketDispatcher().sendGrandExchangeOffer(offer);
            GrandExchangeHandler.remove(player.getUsername(), offer.getSlot());
            openOffersInterface();
            if (offer.getAmount() != 0) {
                history.add(new ExchangeHistory(offer.getItem().getId(), offer.getAmount(), offer.getTotalPrice(), offer.getType()));
            }
            cleanHistory();
        }*/
    }

    public void collectFromBox(final boolean offersInterface, final int slot, final int option, final int slotId) {
        val offer = getOffers().get(slot);
        if (offer == null) {
            return;
        }
        val item = offer.getContainer().get(slotId);
        if (item == null) {
            return;
        }

        val op = option == 1 || option == 2 ? (item.getAmount() > 1 ? (option == 1 ? 2 : 1) : option) : option;
        val offerItem = op == 2 ? item.toNote() : item;
        if (option == 1 || option == 2) {
            val result = player.getInventory().addItem(offerItem);
            if (result.getResult().equals(RequestResult.SUCCESS)) {
                offer.getContainer().remove(item);
            } else {
                if (result.getSucceededAmount() > 0) {
                    offer.getContainer().set(slotId, new Item(item.getId(), item.getAmount() - result.getSucceededAmount()));
                }
                player.sendMessage("Not enough space in your inventory.");
            }
        } else if (option == 3) {
            val result = player.getBank().add(offerItem);
            if (result.getResult().equals(RequestResult.SUCCESS)) {
                offer.getContainer().remove(item);
            } else {
                if (result.getSucceededAmount() > 0) {
                    offer.getContainer().set(slotId, new Item(item.getId(), item.getAmount() - result.getSucceededAmount()));
                }
                player.sendMessage("Not enough space in your bank.");
            }
        }
        offer.refreshItems();
        if (offer.getContainer().getFreeSlotsSize() == 2 && (offer.isCompleted() || offer.isCancelled() || offer.isAborted())) {
            offer.setCancelled(true);
            player.getPacketDispatcher().sendGrandExchangeOffer(offer);
            GrandExchangeHandler.remove(player.getUsername(), offer.getSlot());
            if (offersInterface) {
                openOffersInterface();
            }
            if (offer.getAmount() != 0) {
                history.add(new ExchangeHistory(offer.getItem().getId(), offer.getAmount(), offer.getTotalPrice(), offer.getType()));
            }
            cleanHistory();
        }
    }

    public void collectAll(final boolean inventory, final boolean openOffers) {
        val offers = getOffers().values();
        if (offers.isEmpty()) {
            return;
        }
        val notEnoughSpace = new MutableBoolean(false);
        offers.forEach(offer -> offer.getContainer().getItems().int2ObjectEntrySet().fastForEach(entry -> {
            val item = entry.getValue();
            val slot = entry.getIntKey();
            val toAdd = new Item(item.getDefinitions().getNotedOrDefault(), item.getAmount());
            ContainerResult result = inventory ? player.getInventory().addItem(toAdd) : player.getBank().add(toAdd);
            var remove = result.getResult().equals(RequestResult.SUCCESS);
            if (!remove) {
                notEnoughSpace.setTrue();
                if (result.getSucceededAmount() > 0) {
                    offer.getContainer().set(slot, new Item(item.getId(), item.getAmount() - result.getSucceededAmount()));
                }
            } else {
                offer.getContainer().remove(item);
            }
            offer.refreshItems();
            if (offer.getContainer().getFreeSlotsSize() == 2 && (offer.isCompleted() || offer.isCancelled() || offer.isAborted())) {
                offer.setCancelled(true);
                player.getPacketDispatcher().sendGrandExchangeOffer(offer);
                GrandExchangeHandler.remove(player.getUsername(), offer.getSlot());
                if (openOffers) {
                    openOffersInterface();
                }
                if (offer.getAmount() != 0) {
                    history.add(new ExchangeHistory(offer.getItem().getId(), offer.getAmount(), offer.getTotalPrice(), offer.getType()));
                }
                cleanHistory();
            }
        }));
        if (notEnoughSpace.isTrue()) {
            player.sendMessage("Not enough space in your " + (inventory ? "inventory" : "bank") + ".");
        }
    }

    public void setItem(final int id) {
        player.getVarManager().sendVar(ITEM_VARP, id);
    }

    public void modifyPrice(int price) {
        if (price < 1) {
            price = 1;
        }
        setPrice(price);
    }

    public void modifyQuantity(int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }
        setQuantity(quantity);
    }

    public int getPrice() {
        return player.getVarManager().getValue(PRICE_VARP);
    }

    public void setPrice(final int price) {
        player.getVarManager().sendVar(PRICE_VARP, price);
    }

    public int getQuantity() {
        return player.getVarManager().getBitValue(QUANTITY_VARPBIT);
    }

    public void setQuantity(final int quantity) {
        player.getVarManager().sendBit(QUANTITY_VARPBIT, quantity);
    }

    public void reset() {
        val dispatcher = player.getPacketDispatcher();
        setQuantity(1);
        setPrice(0);
        setItem(-1);
        dispatcher.sendComponentText(INTERFACE, 25, "");
        dispatcher.sendComponentText(INTERFACE, 26, "");
        dispatcher.sendComponentText(INTERFACE, 16, "");
        dispatcher.sendComponentText(INTERFACE, 17, "");
    }

    public void createOffer() {
        val manager = player.getVarManager();
        val price = manager.getValue(GrandExchange.PRICE_VARP);
        int quantity = manager.getBitValue(GrandExchange.QUANTITY_VARPBIT);
        if (quantity <= 0) {
            player.sendMessage("You must set the quantity to a positive number.");
            return;
        }

        //If varp id is 0(dwarf remains) or below that, we interrupt the process.
        val id = manager.getValue(GrandExchange.ITEM_VARP);
        if (id <= 0) {
            player.sendMessage("You must choose an item.");
            return;
        }
        if (!ItemDefinitions.getOrThrow(id).isGrandExchange()) {
            return;
        }
        val item = new Item(id);
        val slot = manager.getBitValue(SLOT_VARPBIT) - 1;
        if (slot == -1) {
            return;
        }
        val type = manager.getBitValue(TYPE_VARPBIT) == 0 ? ExchangeType.BUYING : ExchangeType.SELLING;
        val total = (long) price * (long) quantity;
        if (total > Integer.MAX_VALUE) {
            player.sendMessage("Too much money!");
            return;
        }
        if(type.equals(ExchangeType.BUYING))
        {
            for(int i = 0; i < getOffers().size() - 1; i++)
            {
                ExchangeOffer offer = getOffers().get(i);
                if(offer != null)
                {
                    if(offer.getType() == ExchangeType.SELLING && offer.getItem().getId() == item.getId())
                    {
                        player.sendMessage("You may not have a buy offer for this item until you finish the sell offer you have for it.");
                        return;
                    }
                }
            }
        }
        if (type.equals(ExchangeType.BUYING)) {
            if (player.getInventory().getAmountOf(995) < total) {
                player.sendMessage("That offer costs " + Utils.format(total) + " coins. You haven't got enough.");
                return;
            }
            player.getInventory().deleteItem(new Item(995, (int) total));
        } else {
            val regularAmount = player.getInventory().getAmountOf(item.getId());
            val notedAmount = player.getInventory().getAmountOf(item.getDefinitions().getNotedId());
            val notedId = item.getDefinitions().getNotedId();

            if (quantity > regularAmount + notedAmount) {
                player.sendMessage("You don't have enough of this item to put in an offer.");
                return;
            }
            val success = player.getInventory().deleteItem(notedId, quantity).getSucceededAmount();
            int q = quantity;
            q -= success;
            if (q > 0) {
                val deleted = player.getInventory().deleteItem(item.getId(), q).getSucceededAmount();
                q -= deleted;
                if (q > 0) {
                    quantity -= q;
                }
            }
        }
        if (quantity < 1 || price < 1) {
            return;
        }
        val offer = new ExchangeOffer(player.getUsername(), new Item(item.getId(), quantity), price, slot, type);
        player.getPacketDispatcher().sendGrandExchangeOffer(offer);
        addOffer(offer);
        getOffers().put(slot, offer);
        manager.sendBit(SLOT_VARPBIT, 0);
    }

    public void resetExistingOffers() {
        history.clear();
        val offers = GrandExchangeHandler.getAllOffers().get(player.getUsername());
        offers.forEach((id, offer) -> {
            offer.setCancelled(true);
            player.getPacketDispatcher().sendGrandExchangeOffer(offer);
        });
        GrandExchangeHandler.getAllOffers().remove(player.getUsername());
        reset();
        player.getInterfaceHandler().closeInterfaces();
        refreshOffers();
    }

    private void addOffer(final ExchangeOffer offer) {
        GrandExchangeHandler.addOffer(player.getUsername(), offer);
        GrandExchangeOfferExecutor.refresh(offer);
    }

    @NotNull
    public Int2ObjectOpenHashMap<ExchangeOffer> getOffers() {
        return GrandExchangeHandler.getOffers(player.getUsername());
    }

    public int getFreeSlot() {
        for (int i = 0; i < 8; i++) {
            val offer = getOffers().get(i);
            if (offer == null) {
                return i;
            }
        }
        return 0;
    }

}
