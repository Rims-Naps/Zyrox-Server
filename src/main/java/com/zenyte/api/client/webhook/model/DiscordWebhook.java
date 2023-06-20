package com.zenyte.api.client.webhook.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb
 */
@Data
@Builder
public class DiscordWebhook {
    
    private List<EmbedObject> embeds;
    private String content;
    private String username;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private boolean tts;
    
    public static class DiscordWebhookBuilder {
        public DiscordWebhookBuilder embed(final EmbedObject embed) {
            if (this.embeds == null) {
                this.embeds = new ArrayList<>();
            }
            this.embeds.add(embed);
            return this;
        }
    }
    
}
