package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.ClaimBondRequest;
import com.zenyte.game.world.entity.player.Player;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
@Slf4j
public class BondClaimRequest {

    private final ClaimBondRequest entry;

    public BondClaimRequest(final Player player, final int bond) {
        this.entry = new ClaimBondRequest(player.getPlayerInformation().getUserIdentifier(), player.getUsername().replaceAll("_", " "), bond, player.getIP());
    }

    public boolean execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(entry);
        val url = APIClient.urlBuilder()
                .addPathSegment("account")
                .addPathSegment("bond").build();

        val request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Make the API request
        try {
            try (val response = http.newCall(request).execute()) {
                val responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return false;
                }

                return Boolean.parseBoolean(responseBody.string());
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }

        return false;
    }

}
