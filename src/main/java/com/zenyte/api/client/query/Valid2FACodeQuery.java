package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;

/**
 * @author Corey
 * @since 01/06/19
 */
@Slf4j
@Data
public class Valid2FACodeQuery {
    
    private final int memberId;
    private final String code;
    
    public boolean execute() {
        val url = APIClient.urlBuilder()
                          .addPathSegment("user")
                          .addPathSegment(String.valueOf(memberId))
                          .addPathSegment("check2fa")
                          .addQueryParameter("code", code)
                          .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .get()
                              .build();
        
        try (val response = APIClient.CLIENT.newCall(request).execute()) {
            val responseBody = response.body();
            if (responseBody == null || !response.isSuccessful()) {
                return false;
            }
            val string = responseBody.string();
            if (!"true".equals(string)) {
                log.error("[member_id=" + memberId + ", code=" + code + "] Invalid response or code; response: " + string);
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
            return false;
        }
    }
    
}
