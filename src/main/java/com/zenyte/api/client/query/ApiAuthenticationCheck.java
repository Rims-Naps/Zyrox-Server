package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.LoginRequest;
import com.zenyte.game.util.AES;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
@Slf4j
@Deprecated
public class ApiAuthenticationCheck {
    
    private final LoginRequest request;

    /*public ApiAuthenticationCheck(final PlayerInformation info) {
        request = new LoginRequest(info.getUsername().replaceAll("_", " "), Objects.requireNonNull(AES.encrypt(info.getPlainPassword(), AES.TEMP_KEY)));
    }*/

    public ApiAuthenticationCheck(final String username, final String password) {
        request = new LoginRequest(username.replaceAll("_", " "), Objects.requireNonNull(AES.encrypt(password, AES.TEMP_KEY)));
    }

    public String execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(request);
    
        val url = APIClient.urlBuilder()
                .addPathSegment("account")
                .addPathSegment("login").build();

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
