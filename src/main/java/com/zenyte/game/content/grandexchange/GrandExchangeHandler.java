package com.zenyte.game.content.grandexchange;

import com.zenyte.game.parser.Parse;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tommeh | 26 nov. 2017 : 21:36:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@Slf4j
public class GrandExchangeHandler implements Parse {

	@Getter private static Map<String, Int2ObjectOpenHashMap<ExchangeOffer>> offers;
	public static final String OFFERS_FILE_DIRECTORY = "./data/grandexchange/offers.json";
    public static final String PRICES_FILE_DIRECTORY = "./data/grandexchange/prices.json";

    private static boolean loaded;

	public static final MutableBoolean status = new MutableBoolean();

	public static void init() {
		try {
			new GrandExchangeHandler().parse();
			new JSONGEItemDefinitionsLoader().parse();
			loaded = true;
		} catch (final Throwable e) {
            log.error(Strings.EMPTY, e);
		}
	}

	public static void save() {
	    try {
            if (!loaded)
                return;
            status.setTrue();
            //synchronized (GrandExchange.LOCK) {
                val gson = World.getGson();
                val list = new ArrayList<ExchangeOffer>(offers.size());
                for (val map : offers.values()) {
                    if (map.isEmpty()) {
                        continue;
                    }
                    list.addAll(map.values());
                }
                val toJson = gson.toJson(list);
                try {
                    final PrintWriter pw = new PrintWriter(OFFERS_FILE_DIRECTORY);
                    pw.println(toJson);
                    pw.close();
                } catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
                }
                status.setFalse();
            //}
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}

	@NotNull
	static final Int2ObjectOpenHashMap<ExchangeOffer> getOffers(final String username) {
	    //synchronized(GrandExchange.LOCK) {
            var offers = GrandExchangeHandler.offers.get(username);
            if (offers == null) {
                offers = new Int2ObjectOpenHashMap<>(8);
                GrandExchangeHandler.offers.put(username, offers);
            }
            return offers;
        //}
    }

    @NotNull
    static final Map<String, Int2ObjectOpenHashMap<ExchangeOffer>> getAllOffers() {
	    //synchronized(GrandExchange.LOCK) {
            return offers;
        //}
    }

	static void addOffer(final String username, final ExchangeOffer offer) {
	    //synchronized(GrandExchange.LOCK) {
	        getOffers(username).put(offer.getSlot(), offer);
        //}
	}

	static void remove(final String username, final int slot) {
	    //synchronized(GrandExchange.LOCK) {
	        getOffers(username).remove(slot);
        //}
	}

	@Override
	public void parse() throws Throwable {
		offers = new HashMap<>();
		final BufferedReader br = new BufferedReader(new FileReader(OFFERS_FILE_DIRECTORY));
		val loadedOffers = World.getGson().fromJson(br, ExchangeOffer[].class);
		for (val offer : loadedOffers) {
			Int2ObjectOpenHashMap<ExchangeOffer> currentMap = offers.get(offer.getUsername());
			if (currentMap == null) {
				currentMap = new Int2ObjectOpenHashMap<>(8);
				offers.put(offer.getUsername(), currentMap);
			}
			val container = offer.getContainer();
			offer.setContainer(new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.GE_COLLECTABLES_CONTAINERS[offer.getSlot()], Optional.empty()));
			offer.getContainer().setContainer(container);
			offer.getContainer().setFullUpdate(true);
			if (offer.getLastUpdateTime() <= 0) {
                offer.refreshUpdateTime();
            }
			currentMap.put(offer.getSlot(), offer);
		}
	}

}
