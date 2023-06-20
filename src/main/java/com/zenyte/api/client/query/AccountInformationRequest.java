package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 03/05/2019 21:22
 * @author Corey
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
@Data
public class AccountInformationRequest {
    
    private final String username;
    
    public AccountInformationRequestResults execute() {
        val http = APIClient.CLIENT;
        
        val url = APIClient.urlBuilder()
                          .addPathSegment("user")
                          .addPathSegment("columns")
                          .addPathSegment(username.replaceAll("_", " "))
                          .addQueryParameter("columns", "joined,member_id,msg_count_new,members_pass_hash,mfa_details")
                          .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .get()
                              .build();
        
        try {
            try (val response = http.newCall(request).execute()) {
                val responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return null;
                }
                val body = responseBody.string();
                //If empty besides {}
                if (body.length() == 2) {
                    return null;
                }
                return new AccountInformationRequestResults(body);
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return null;
    }
    
}
