package com.zenyte.api.client.query.adventurerslog;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.SubmitGamelogRequest;
import com.zenyte.game.world.entity.player.Player;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 15/05/2019 14:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ApiAdventurersLogRequest {


    private final SubmitGamelogRequest entry;

    public ApiAdventurersLogRequest(final Player player, final AdventurersLogIcon icon, final String message) {
        entry = new SubmitGamelogRequest(player.getUsername(), icon.getLink(), message, false);
    }
    
    public ApiAdventurersLogRequest(final Player player, final String icon, final String message) {
        entry = new SubmitGamelogRequest(player.getUsername(), icon, message, false);
    }

    public ApiAdventurersLogRequest(final Player player, final AdventurersLogIcon icon, final String message, final boolean pvp) {
        entry = new SubmitGamelogRequest(player.getUsername(), icon.getLink(), message, pvp);
    }

    public String execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(entry);

        val url = APIClient.urlBuilder()
                .addPathSegment("account")
                          .addPathSegment("submitGamelog").build();

        val request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Make the API request
        try {
            try (val response = http.newCall(request).execute()) {
                val responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return "empty";
                }
                return responseBody.string();
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return "empty";
    }

}
