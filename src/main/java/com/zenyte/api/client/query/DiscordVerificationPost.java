package com.zenyte.api.client.query;

import com.google.gson.JsonParser;
import com.zenyte.api.client.APIClient;
import com.zenyte.game.world.entity.player.Player;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.FormBody;
import okhttp3.Request;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;

/**
 * @author Corey
 * @since 31/05/19
 */
@Slf4j
public class DiscordVerificationPost {
    
    private final Player player;
    private final String verificationCode;
    
    public DiscordVerificationPost(final Player player, final String verificationCode) {
        this.player = player;
        this.verificationCode = verificationCode;
    }
    
    public String execute() {
        val url = APIClient.urlBuilder()
                          .addPathSegment("discord")
                          .addPathSegment("verify")
                          .addPathSegment(String.valueOf(player.getPlayerInformation().getUserIdentifier()))
                          .build();
        
        val body = new FormBody.Builder()
                           .add("verificationCode", verificationCode)
                           .build();
        
        val request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .build();
        
        try (val response = APIClient.CLIENT.newCall(request).execute()) {
            if (response.code() == 200) {
                return "OK";
            }
            try {
                if (response.body() != null) {
                    val responseBody = response.body().string();
                    val jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
                    return jsonObject.get("message").getAsString();
                } else {
                    log.warn("Invalid response from server; response: " + response.toString());
                    return "Invalid response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Invalid response from server";
            }
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
            return e.getMessage();
        }
    }
}
