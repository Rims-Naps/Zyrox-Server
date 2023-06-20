package com.zenyte.game.content.grandexchange;

import com.zenyte.GameEngine;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.zenyte.game.content.grandexchange.GrandExchange.OFFER_TIMEOUT_DELAY;

/**
 * @author Kris | 13/01/2019 15:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
class GrandExchangeOfferExecutor {

    private static final Logger transactionLogger = LoggerFactory.getLogger("Grand Exchange Transactions Logger");

    static final void refresh(final ExchangeOffer offer) {
        check(offer);
        //CoresManager.getGrandExchangeExecutor().submit(() -> check(offer));
    }

    private static final void check(final ExchangeOffer offer) {
        //synchronized(GrandExchange.LOCK) {
            val matchingOffers = getMatchingOffers(offer);
            if (matchingOffers.isEmpty()) return;
            val type = offer.getType();
            sort(type, matchingOffers);
            if (type.equals(ExchangeType.BUYING)) {
                processPurchaseOffer(offer, matchingOffers);
            } else {
                processSellOffer(offer, matchingOffers);
            }
        //}
    }

    private static final void processPurchaseOffer(final ExchangeOffer buyOffer, final List<ExchangeOffer> matchingOffers) {
        val originalAmount = buyOffer.getRemainder();
        int amount = originalAmount;
        val id = buyOffer.getItem().getId();
        for (int i = matchingOffers.size() - 1; i >= 0; i--) {
            val sellOffer = matchingOffers.get(i);
            if (sellOffer == null) {
                continue;
            }
            val offerAmount = Math.min(amount, sellOffer.getRemainder());
            if (offerAmount == 0) {
                continue;
            }
            amount -= offerAmount;
            val exchangePrice = sellOffer.getPrice();
            sellOffer.setAmount(sellOffer.getAmount() + offerAmount);
            sellOffer.refreshUpdateTime();
            buyOffer.refreshUpdateTime();
            val result = sellOffer.getContainer().add(new Item(995, (offerAmount * exchangePrice)));
            GrandExchangePriceManager.post(id, offerAmount, exchangePrice);
            sellOffer.setTotalPrice(sellOffer.getTotalPrice() + result.getSucceededAmount());
            WorldTasksManager.schedule(sellOffer::updateAndInform);
            buyOffer.setAmount(buyOffer.getAmount() + offerAmount);
            buyOffer.getContainer().add(new Item(id, offerAmount));
            val returnedAmount = (offerAmount * buyOffer.getPrice()) - result.getSucceededAmount();
            if (returnedAmount > 0) {
                buyOffer.getContainer().add(new Item(995, returnedAmount));
            }
            buyOffer.setTotalPrice(buyOffer.getTotalPrice() + result.getSucceededAmount());
            try {
                transactionLogger.info(Utils.formatString(buyOffer.getUsername()) + " bought " + offerAmount + " x " + ItemDefinitions.getOrThrow(id).getName() + "(" + id + ") for " + Utils.format(exchangePrice) +
                        " each from " + Utils.formatString(sellOffer.getUsername()) + " for a total of " + Utils.format(offerAmount * exchangePrice) + ". Offer progress: " + buyOffer.getAmount() + "/" + buyOffer.getItem().getAmount() +
                        ".");
            } catch (Exception e) {
                GameEngine.logger.error(Strings.EMPTY, e);
            }
            if (amount <= 0) {
                break;
            }
        }
        if (originalAmount == amount) {
            return;
        }
        WorldTasksManager.schedule(buyOffer::updateAndInform);
    }

    private static final void processSellOffer(final ExchangeOffer sellOffer, final List<ExchangeOffer> matchingOffers) {
        val originalAmount = sellOffer.getRemainder();
        int amount = originalAmount;
        val id = sellOffer.getItem().getId();
        for (int i = matchingOffers.size() - 1; i >= 0; i--) {
            val buyOffer = matchingOffers.get(i);
            if (buyOffer == null) {
                continue;
            }
            val offerAmount = Math.min(amount, buyOffer.getRemainder());
            if (offerAmount == 0) {
                continue;
            }
            amount -= offerAmount;
            val exchangePrice = buyOffer.getPrice();
            sellOffer.refreshUpdateTime();
            buyOffer.refreshUpdateTime();
            sellOffer.setAmount(sellOffer.getAmount() + offerAmount);
            val result = sellOffer.getContainer().add(new Item(995, (offerAmount * exchangePrice)));
            GrandExchangePriceManager.post(id, offerAmount, exchangePrice);
            sellOffer.setTotalPrice(sellOffer.getTotalPrice() + result.getSucceededAmount());
            WorldTasksManager.schedule(buyOffer::updateAndInform);
            buyOffer.setAmount(buyOffer.getAmount() + offerAmount);
            buyOffer.getContainer().add(new Item(id, offerAmount));
            val returnedAmount = (offerAmount * buyOffer.getPrice()) - result.getSucceededAmount();
            if (returnedAmount > 0) {
                buyOffer.getContainer().add(new Item(995, returnedAmount));
            }
            buyOffer.setTotalPrice(buyOffer.getTotalPrice() + result.getSucceededAmount());
            try {
                transactionLogger.info(Utils.formatString(sellOffer.getUsername()) + " sold " + offerAmount + " x " + ItemDefinitions.getOrThrow(id).getName() + "(" + id + ") for " + Utils.format(exchangePrice) +
                        " each to " + Utils.formatString(buyOffer.getUsername()) + " for a total of " + Utils.format(offerAmount * exchangePrice) + ". Offer progress: " + sellOffer.getAmount() + "/" + sellOffer.getItem().getAmount() + ".");
            } catch (Exception e) {
                GameEngine.logger.error(Strings.EMPTY, e);
            }
            if (amount <= 0) {
                break;
            }
        }
        if (amount == originalAmount) {
            return;
        }
        WorldTasksManager.schedule(sellOffer::updateAndInform);
    }

    private static final void sort(final ExchangeType type, final List<ExchangeOffer> matchingOffers) {
        if (matchingOffers.isEmpty())
            return;
        if (type.equals(ExchangeType.BUYING)) {
            matchingOffers.sort((a, b) -> {
                val offset = Integer.compare(b.getPrice(), a.getPrice());
                return offset == 0 ? Long.compare(b.getTime(), a.getTime()) : offset;
            });
        } else {
            matchingOffers.sort((a, b) -> {
                val offset = Integer.compare(a.getPrice(), b.getPrice());
                return offset == 0 ? Long.compare(b.getTime(), a.getTime()) : offset;
            });
        }
    }

    private static final List<ExchangeOffer> getMatchingOffers(final ExchangeOffer offer) {
        val offers = GrandExchangeHandler.getAllOffers();
        val matchingOffers = new ArrayList<ExchangeOffer>();
        val iterator = offers.entrySet().iterator();
        val id = offer.getItem().getId();
        val type = offer.getType();
        val price = offer.getPrice();
        val lowestAcceptableTime = System.currentTimeMillis() - OFFER_TIMEOUT_DELAY;
        while (iterator.hasNext()) {
            val next = iterator.next();
            val username = next.getKey();
            if (username.equals(offer.getUsername())) {
                continue;
            }

            val pendingOffers = next.getValue();
            pendingOffers.values().forEach(o -> {
                if (o == null || o.isAborted() || o.isCancelled()) {
                    return;
                }
                val item = o.getItem();
                if (item == null || item.getId() != id || type.equals(o.getType())) {
                    return;
                }
                if (type.equals(ExchangeType.BUYING) ? (price < o.getPrice()) : (price > o.getPrice()))
                    return;
                if (o.getLastUpdateTime() < lowestAcceptableTime) {
                    return;
                }
                matchingOffers.add(o);
            });
        }
        return matchingOffers;
    }


}
