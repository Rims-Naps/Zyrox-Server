package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.FormBody;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;

/**
 * @author Corey
 * @since 01/05/19
 */
@Slf4j
public class ApiPing {
    
    public boolean execute() {
        val url = APIClient.urlBuilder()
                          .addPathSegment("ping").build();
        
        val body = new FormBody.Builder()
                           .add("payload", "ping")
                           .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .build();
        
        try (val response = APIClient.CLIENT.newCall(request).execute()) {
            val responseBody = response.body();
            if (responseBody == null || !response.isSuccessful()) {
                return false;
            }
            val string = responseBody.string();
            if (!"pong".equals(string)) {
                log.error("Returned invalid response: " + string);
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
            return false;
        }
    }
    
}
