package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.PlayerInformation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @author Corey
 * @since 20:24 - 25/06/2019
 */
@Slf4j
@Data
public class SubmitPlayerInformation {
    
    private final PlayerInformation info;
    
    public void execute() {
        val body = APIClient.jsonBody(info);
        val url = APIClient.urlBuilder()
                          .addPathSegment("user")
                          .addPathSegment("info")
                          .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .build();
        
        try {
            val response = APIClient.CLIENT.newCall(request).execute();
            response.close();
            log.info("Sent player information to api for '" + info.getUsername() + "'");
            
        } catch (final SocketException | SocketTimeoutException ignored) {
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
}
