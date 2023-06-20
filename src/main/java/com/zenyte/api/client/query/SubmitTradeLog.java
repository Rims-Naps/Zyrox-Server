package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.TradeLog;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Corey
 * @since 19/06/19
 */
@Slf4j
@Data
public class SubmitTradeLog {
    
    private final TradeLog transaction;
    
    public void execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(transaction);
        
        val url = APIClient.urlBuilder()
                          .addPathSegment("user")
                          .addPathSegment("log")
                          .addPathSegment("trade")
                          .build();
        
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