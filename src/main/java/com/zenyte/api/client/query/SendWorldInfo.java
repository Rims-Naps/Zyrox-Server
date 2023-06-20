package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.World;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Corey
 * @since 01/05/19
 */
@Slf4j
public class SendWorldInfo {
    
    private final World worldInfo;
    
    public SendWorldInfo(final World world) {
        this.worldInfo = world;
    }
    
    public void execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(worldInfo);
        
        val url = APIClient.urlBuilder()
                          .addPathSegment("worldinfo")
                          .addPathSegment("world")
                          .addPathSegment("update").build();
        
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
