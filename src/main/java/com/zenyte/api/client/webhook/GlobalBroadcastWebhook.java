package com.zenyte.api.client.webhook;

import com.zenyte.api.client.webhook.model.DiscordWebhook;
import com.zenyte.api.client.webhook.model.EmbedObject;
import com.zenyte.game.world.entity.player.GameMode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.regex.Pattern;

@Slf4j
public class GlobalBroadcastWebhook extends Webhook {
    
    private static final String ICON_URL_TEMPLATE = "https://zenyte.com/img/adventure-icons/%s";
    
    @Getter
    @Setter
    private static boolean disabled;
    
    private final String title;
    private final String icon;
    private final String message;
    
    public GlobalBroadcastWebhook(final String icon, final String message, final String title) {
        super("764402161508679710", "s-_fXgjNN6CuqFmy_YMBYawuxD9cVNtJ7jpTGWkObv8gdQ49s3c--5JmIGScawpx0x5H");
        this.title = title;
        this.icon = String.format(ICON_URL_TEMPLATE, icon);
        this.message = message;
    }
    
    public GlobalBroadcastWebhook(final int icon, final String message, final String title) {
        this(icon + ".png", message, title);
    }
    
    private static String emojiFromCrownId(final int crownId) {
        if (crownId == GameMode.STANDARD_IRON_MAN.getIcon()) {
            return "<:ironman:763824179526107228>";
        } else if (crownId == GameMode.ULTIMATE_IRON_MAN.getIcon()) {
            return "<:ultimate_ironman:763843920168747038>";
        } else if (crownId == GameMode.HARDCORE_IRON_MAN.getIcon()) {
            return "<:hardcore_ironman:763824179833077760>";
        }
        return "";
    }
    
    private static String replaceImgWithEmoji(String toReplace) {
        val pattern = "(<img=(\\d+)>)";
        val r = Pattern.compile(pattern);
        val m = r.matcher(toReplace);
    
        while (m.find()) {
            val emoji = emojiFromCrownId(NumberUtils.toInt(m.group(2), -1));
            toReplace = toReplace.replace(m.group(1), emoji);
        }
    
        return toReplace.replaceAll("<(shad|img|col)=.*>", ""); // remove any tags left over that we haven't already replaced
    }
    
    @Override
    public DiscordWebhook buildMessage() {
        return DiscordWebhook.builder()
                       .embed(EmbedObject.builder()
                                      .title(title)
                                      .description(replaceImgWithEmoji(message))
                                      .color(Webhook.EMBED_COLOUR)
                                      .thumbnail(new EmbedObject.Thumbnail(icon))
                                      .build())
                       .build();
    }
    
}
