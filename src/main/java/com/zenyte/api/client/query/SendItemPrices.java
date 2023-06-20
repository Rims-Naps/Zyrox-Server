package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.game.content.grandexchange.JSONGEItemDefinitions;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

/**
 * @author Kris | 16/08/2019 16:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class SendItemPrices {

    private final List<JSONGEItemDefinitions> prices;

    public SendItemPrices(final List<JSONGEItemDefinitions> prices) {
        this.prices = prices;
    }

    public void execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(prices);

        val url = APIClient.urlBuilder()
                .addPathSegment("runelite")
                .addPathSegment("items")
                .addPathSegment("prices").build();

        val request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            val response = http.newCall(request).execute();
            response.close();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

}
