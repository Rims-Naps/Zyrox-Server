package com.zenyte.api.client.query.hiscores;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.ExpMode;
import com.zenyte.api.model.ExpModeUpdate;
import com.zenyte.game.world.entity.player.Player;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Corey
 * @since 05/05/19
 */
@Slf4j
@Data
public class UpdateHiscoreExpMode {
    
    private final Player player;
    private final ExpMode oldMode;
    private final ExpMode newMode;
    
    public void execute() {
        if (player.inArea("Tutorial Island")) {
            log.info("User '" + player.getName() + "' in tutorial island, holding off sending hiscores data");
            return;
        }
        
        val username = player.getUsername();
        val http = APIClient.CLIENT;
        val body = APIClient.jsonBody(new ExpModeUpdate(oldMode, newMode, player.getGameMode().getApiRole()));
        
        val url = APIClient.urlBuilder()
                          .addPathSegment("hiscores")
                          .addPathSegment("user")
                          .addPathSegment(username.replaceAll("_", " "))
                          .addPathSegment("update")
                          .addPathSegment("expmode")
                          .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .build();
        
        try {
            val response = http.newCall(request).execute();
            response.close();
            log.info("Sent exp mode update to the api for '" + username + "'");
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
    
}
