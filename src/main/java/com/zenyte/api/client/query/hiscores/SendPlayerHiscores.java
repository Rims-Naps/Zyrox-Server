package com.zenyte.api.client.query.hiscores;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.SkillHiscore;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author Corey
 * @since 05/05/19
 */
@Slf4j
public class SendPlayerHiscores {
    
    private final String username;
    private final List<SkillHiscore> hiscores;
    
    public SendPlayerHiscores(final String username, final List<SkillHiscore> hiscores) {
        this.username = username;
        this.hiscores = hiscores;
    }
    
    public void execute() {
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(hiscores);
        
        val url = APIClient.urlBuilder()
                          .addPathSegment("hiscores")
                          .addPathSegment("user")
                          .addPathSegment(username.replaceAll("_", " "))
                          .addPathSegment("update").build();
        
        val request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .build();
        
        try {
            val response = http.newCall(request).execute();
            response.close();
            log.info("Sent hiscores data to api for '" + username + "'");

        } catch (final SocketException | SocketTimeoutException ignored) {
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
    
}
