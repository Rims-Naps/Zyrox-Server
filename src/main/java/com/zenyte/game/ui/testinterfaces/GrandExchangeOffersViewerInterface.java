package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.grandexchange.ExchangeOffer;
import com.zenyte.game.content.grandexchange.ExchangeType;
import com.zenyte.game.content.grandexchange.GrandExchangeHandler;
import com.zenyte.game.ui.GameTab;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import mgi.types.config.items.ItemDefinitions;
import lombok.val;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

import static com.zenyte.game.content.grandexchange.GrandExchange.INVENTORY_INTERFACE;
import static com.zenyte.game.content.grandexchange.GrandExchange.OFFER_TIMEOUT_DELAY;

/**
 * @author Tommeh | 16/08/2019 | 17:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class GrandExchangeOffersViewerInterface extends Interface {

    @Override
    protected void attach() {
        put(4, "Exchange");
        put(5, "Search for item");
        put(8, "Item sprite in search");
        put(11, "Item name");
        put(15, "GE Item price");
        put(16, "Select buying (Button)");
        put(17, "Select selling (Button)");
        put(18, "Select buying (Text)");
        put(19, "Select selling (Text)");
        put(29, "Sort by quantity");
        put(30, "Sort by price");
        put(31, "Sort by name");
        put(32, "Offer Entry");
    }

    @Override
    public void open(Player player) {
        if (player.isIronman()) {
            player.sendMessage("As an Iron Man, you cannot use the Grand Exchange.");
            return;
        }
        reset(player);
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendClientScript(10808, getInterface().getId() << 16 | getComponent("Sort by quantity"), 0, player.getBooleanAttribute("ge_offers_viewer_quantity_sort") ? 1 : 0);
        player.getPacketDispatcher().sendClientScript(10808, getInterface().getId() << 16 | getComponent("Sort by price"), 0, player.getBooleanAttribute("ge_offers_viewer_price_sort") ? 1 : 0);
        player.getPacketDispatcher().sendClientScript(10808, getInterface().getId() << 16 | getComponent("Sort by name"), 0, player.getBooleanAttribute("ge_offers_viewer_name_sort") ? 1 : 0);


        val handler = player.getInterfaceHandler();
        val dispatcher = player.getPacketDispatcher();
        handler.sendInterface(InterfacePosition.INVENTORY_TAB, INVENTORY_INTERFACE);
        handler.openGameTab(GameTab.INVENTORY_TAB);
        dispatcher.sendComponentSettings(INVENTORY_INTERFACE, 0, 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);

        player.sendInputItem("Check offers for:", item -> {
            player.getPacketDispatcher().sendComponentItem(id, getComponent("Item sprite in search"), item.getId(), 1);
            player.getPacketDispatcher().sendComponentText(id, getComponent("Item name"), item.getName());
            player.getPacketDispatcher().sendComponentText(id, getComponent("GE Item price"), Utils.format(item.getSellPrice()) + "<br>coins each");
            player.addTemporaryAttribute("ge_offers_selected_item", item.getId());
            search(player, item.getId(), (ExchangeType) player.getTemporaryAttributes().getOrDefault("ge_offers_selected_exchangetype", ExchangeType.BUYING), null);
        });
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        val handler = player.getInterfaceHandler();
        handler.sendInterface(InterfacePosition.INVENTORY_TAB, Inventory.INTERFACE);
        handler.closeInput();
        player.getTemporaryAttributes().remove("GrandExchange");
    }
    
    @Override
    protected void build() {
        bind("Exchange", player -> player.getGrandExchange().openOffersInterface());
        bind("Search for item", player -> player.sendInputItem("Check offers for:", item -> {
            player.getPacketDispatcher().sendComponentItem(id, getComponent("Item sprite in search"), item.getId(), 1);
            player.getPacketDispatcher().sendComponentText(id, getComponent("Item name"), item.getName());
            player.getPacketDispatcher().sendComponentText(id, getComponent("GE Item price"), Utils.format(item.getSellPrice()) + "<br>coins each");
            player.addTemporaryAttribute("ge_offers_selected_item", item.getId());
            search(player, item.getId(), (ExchangeType) player.getTemporaryAttributes().getOrDefault("ge_offers_selected_exchangetype", ExchangeType.BUYING), null);
        }));
        bind("Select buying (Button)", player -> {
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            player.getTemporaryAttributes().put("ge_offers_selected_exchangetype", ExchangeType.BUYING);
            if (itemId == 0) {
                player.sendMessage("You must select an item first before doing a search.");
                return;
            }
            search(player, itemId, ExchangeType.BUYING, null);
        });
        bind("Select selling (Button)", player -> {
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            player.getTemporaryAttributes().put("ge_offers_selected_exchangetype", ExchangeType.SELLING);
            if (itemId == 0) {
                player.sendMessage("You must select an item first before doing a search.");
                return;
            }
            search(player, itemId, ExchangeType.SELLING, null);
        });
        bind("Select buying (Text)", player -> {
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            player.getTemporaryAttributes().put("ge_offers_selected_exchangetype", ExchangeType.BUYING);
            if (itemId == 0) {
                player.sendMessage("You must select an item first before doing a search.");
                return;
            }
            search(player, itemId, ExchangeType.BUYING, null);
        });
        bind("Select selling (Text)", player -> {
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            player.getTemporaryAttributes().put("ge_offers_selected_exchangetype", ExchangeType.SELLING);
            if (itemId == 0) {
                player.sendMessage("You must select an item first before doing a search.");
                return;
            }
            search(player, itemId, ExchangeType.SELLING, null);
        });
        bind("Sort by quantity", player -> {
            player.toggleBooleanAttribute("ge_offers_viewer_quantity_sort");
            player.getPacketDispatcher().sendClientScript(10808, getInterface().getId() << 16 | getComponent("Sort by quantity"), 0, player.getBooleanAttribute("ge_offers_viewer_quantity_sort") ? 1 : 0);
            val obj = player.getTemporaryAttributes().get("ge_offers_selected_exchangetype");
            if (!(obj instanceof ExchangeType)) {
                return;
            }
            val type = (ExchangeType) obj;
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            search(player, itemId, type, SortType.QUANTITY);
        });
        bind("Sort by price", player -> {
            player.toggleBooleanAttribute("ge_offers_viewer_price_sort");
            player.getPacketDispatcher().sendClientScript(10808, getInterface().getId() << 16 | getComponent("Sort by price"), 0, player.getBooleanAttribute("ge_offers_viewer_price_sort") ? 1 : 0);
            val obj = player.getTemporaryAttributes().get("ge_offers_selected_exchangetype");
            if (!(obj instanceof ExchangeType)) {
                return;
            }
            val type = (ExchangeType) obj;
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            search(player, itemId, type, SortType.PRICE);
        });
        bind("Sort by name", player -> {
            player.toggleBooleanAttribute("ge_offers_viewer_name_sort");
            player.getPacketDispatcher().sendClientScript(10808, getInterface().getId() << 16 | getComponent("Sort by name"), 0, player.getBooleanAttribute("ge_offers_viewer_name_sort") ? 1 : 0);
            val obj = player.getTemporaryAttributes().get("ge_offers_selected_exchangetype");
            if (!(obj instanceof ExchangeType)) {
                return;
            }
            val type = (ExchangeType) obj;
            val itemId = player.getNumericTemporaryAttribute("ge_offers_selected_item").intValue();
            search(player, itemId, type, SortType.NAME);
        });
        bind("Offer Entry", (player, slotId, itemId, option) -> {
            val obj = player.getTemporaryAttributes().get("ge_offers_result");
            if (!(obj instanceof ArrayList)) {
                return;
            }
            val results = (ArrayList<ExchangeOffer>) obj;
            val index = slotId / 9;
            val offer = results.get(index);
            if (offer == null) {
                return;
            }
            val exchange = player.getGrandExchange();
            val freeSlot = exchange.getFreeSlot();
            int availableAmount = offer.getItem().getAmount() - offer.getAmount();
            val id = offer.getItem().getId();
            if (offer.getType().equals(ExchangeType.BUYING)) {
                val def = ItemDefinitions.getOrThrow(id);
                val otherId = def.isNoted() ? def.getUnnotedOrDefault() : def.getNotedId();
                val inInventory = player.getInventory().getAmountOf(otherId) + (otherId == id ? 0 : player.getInventory().getAmountOf(id));
                if (inInventory < availableAmount) {
                    availableAmount = inInventory;
                    player.sendFilteredMessage("You don't have enough of this item to put up as an offer.");
                }
                if (availableAmount <= 0) {
                    return;
                }
                exchange.openOffersInterface();
                exchange.sell(freeSlot, false);
            } else {
                exchange.openOffersInterface();
                exchange.buy(freeSlot, false);
            }



            exchange.setItem(id);
            exchange.modifyQuantity(availableAmount);
            exchange.setPrice(offer.getPrice());
        });
    }
    
    public static final void search(final Player player, final int itemId, final ExchangeType exchangeType, SortType sortType) {
        val results = new ArrayList<ExchangeOffer>();
        player.getTemporaryAttributes().put("ge_offers_selected_exchangetype", exchangeType);
        val lowestAcceptableTime = System.currentTimeMillis() - OFFER_TIMEOUT_DELAY;
        for (val group : GrandExchangeHandler.getOffers().entrySet()) {
            val offers = group.getValue();
            for (val entry : offers.int2ObjectEntrySet()) {
                val offer = entry.getValue();
                if (offer.isCancelled() || offer.isAborted() || offer.isCompleted()) {
                    continue;
                }
                if (offer.getLastUpdateTime() < lowestAcceptableTime) {
                    continue;
                }
                if (offer.getItem().getId() == itemId && offer.getType().equals(exchangeType)) {
                    results.add(offer);
                }
            }
        }
        if (sortType == null) {
            val obj = player.getTemporaryAttributes().get("ge_offers_selected_sorttype");
            if (obj instanceof SortType) {
                val savedSortType = (SortType) obj;
                sortType = savedSortType;
            }
        }
        if (sortType != null) {
            Comparator<ExchangeOffer> sorter = null;
            switch (sortType) {
                case QUANTITY:
                    sorter = ((arg0, arg1) -> {
                        if (player.getBooleanAttribute("ge_offers_viewer_quantity_sort")) {
                            return Integer.compare(arg0.getItem().getAmount(), arg1.getItem().getAmount());
                        }
                        return Integer.compare(arg1.getItem().getAmount(), arg0.getItem().getAmount());
                    });
                    break;
                case PRICE:
                    sorter = ((arg0, arg1) -> {
                        if (player.getBooleanAttribute("ge_offers_viewer_price_sort")) {
                            return Integer.compare(arg0.getPrice(), arg1.getPrice());
                        }
                        return Integer.compare(arg1.getPrice(), arg0.getPrice());
                    });
                    break;
                case NAME:
                    sorter = ((arg0, arg1) -> {
                        if (player.getBooleanAttribute("ge_offers_viewer_name_sort")) {
                            return arg0.getUsername().compareTo(arg1.getUsername());
                        }
                        return arg1.getUsername().compareTo(arg0.getUsername());
                    });
                    break;
            }
            results.sort(sorter);
            player.getTemporaryAttributes().put("ge_offers_selected_sorttype", sortType);
        }
        val size = results.size();
        if (size > 0) {
            player.getPacketDispatcher().sendClientScript(10803, "Viewing offers for: <col=00FFFF>" + ItemDefinitions.getOrThrow(itemId).getName());
            player.getPacketDispatcher().sendClientScript(10805, exchangeType.ordinal());
            for (int index = 0; index < size; index++) {
                val result = results.get(index);
                val price = result.getPrice();
                val totalPrice = result.getPrice() * result.getItem().getAmount();
                val filled = 110D / result.getItem().getAmount() * result.getAmount();
                player.getPacketDispatcher().sendClientScript(10801, index, itemId, result.getItem().getAmount() - result.getAmount(), Utils.format(price), Utils.format(totalPrice), Utils.formatString(result.getUsername()), (int) filled, result.getAmount(), result.getItem().getAmount());
            }
            player.getPacketDispatcher().sendClientScript(10802, size);
        } else {
            player.getPacketDispatcher().sendClientScript(10804, "No results were found with your search.");
        }
        player.getTemporaryAttributes().put("ge_offers_result", results);
        GameInterface.GRAND_EXCHANGE_OFFERS_VIEWER.getPlugin().ifPresent(plugin -> {
            player.getPacketDispatcher().sendComponentSettings(plugin.getInterface(), plugin.getComponent("Offer Entry"), -1, size * 9, AccessMask.CLICK_OP1);
        });
    }
    
    private void reset(final Player player) {
        player.getTemporaryAttributes().remove("ge_offers_selected_exchangetype");
        player.getTemporaryAttributes().remove("ge_offers_result");
        player.getTemporaryAttributes().remove("ge_offers_selected_item");
    }
    
    /**
     * Sorting
     * sort by name: Collections.sort(offers, Ordering.usingToString());
     * sort by price
     */
    
    private enum SortType {
        QUANTITY,
        PRICE,
        NAME
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.GRAND_EXCHANGE_OFFERS_VIEWER;
    }
}
