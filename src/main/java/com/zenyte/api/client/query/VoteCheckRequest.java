package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;

/**
 * @author Corey
 * @since 07/06/19
 */
@Slf4j
@Data
public class VoteCheckRequest {
    
    private final String username;
    
    public int execute() throws RuntimeException {
        val http = APIClient.CLIENT;
        
        val url = APIClient.urlBuilder()
                          .addPathSegment("account")
                          .addPathSegment("vote")
                          .addPathSegment(username.replaceAll("_", " "))
                          .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .get()
                              .build();
        
        try {
            try (val response = http.newCall(request).execute()) {
                val responseBody = response.body();
                if (responseBody == null) {
                    throw new RuntimeException("Response body is not present.");
                }
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Response is not successful.");
                }
                val body = responseBody.string();
                //If empty besides {}
                if (body.length() == 0) {
                    throw new RuntimeException("Response body is empty.");
                }
                return Integer.parseInt(body);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
