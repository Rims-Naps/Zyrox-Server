package com.zenyte.discord;

import de.btobastian.javacord.AccountType;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.DiscordApiBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DiscordAutoAdvertiser {
    
    private final static String NEW_LINE = "\n";
    private final static String EMBED_START = "```diff";
    private final static String EMBED_END = "```";
    private final static String ORANGE_START = "```fix";
    private final static String BOLD = "**";
    
    public static void main(String[] args) {
        System.out.println(getMessage(true));
        /*Thread thread = new Thread(() -> {
            try {
                getDiscord();
            } catch (InterruptedException e) {
                log.error(Strings.EMPTY, e);
            }
        }, "Advertisement");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(thread, 0, 1, TimeUnit.DAYS);*/
    }
    
    private static String getMessage(final boolean discord_link) {
        String message = BOLD +
                                 "\uD83D\uDD36 Zenyte - OSRS Mobile \uD83D\uDCF1- Crafted to Perfection \uD83D\uDD36" + NEW_LINE +
                                 ORANGE_START + NEW_LINE +
                                 "NOW WITH RAIDS AND THE ALCHEMICAL HYDRA!" + NEW_LINE +
                                 EMBED_END +
                                 NEW_LINE +
                                 "Noticeable features:" + NEW_LINE +
                                 ORANGE_START + NEW_LINE +
                                 "- Mobile App" + NEW_LINE +
                                 "- Inferno" + NEW_LINE +
                                 "- Alchemical Hydra" + NEW_LINE +
                                 "- Full Chambers of Xeric" + NEW_LINE +
                                 "- RuneLite Integration" + NEW_LINE +
                                 "- Wintertodt" + NEW_LINE +
                                 "- Blast Furnace" + NEW_LINE +
                                 "- Motherlode Mine" + NEW_LINE +
                                 "- Grotesque Guardians" + NEW_LINE +
                                 "- Quality updates and much more!" + NEW_LINE +
                                 EMBED_END + NEW_LINE +
                                 "Rune-server thread: <https://www.rune-server.ee/runescape-development/rs2-server/advertise/667308-zenyte-osrs-mobile-crafted-perfection.html>" + NEW_LINE;
        if (discord_link) {
            message += "Discord: https://discord.gg/xnsGJHZ" + NEW_LINE;
        }
        message += "Website: <https://zenyte.com/ads?r=discord>" + NEW_LINE +
                           NEW_LINE +
                           "Media:" + NEW_LINE +
                           "https://puu.sh/ETupU/f811a44780.png" + NEW_LINE +
                           "https://puu.sh/EGrau/a8023403fa.png" + NEW_LINE +
                           "https://i.imgur.com/PyBvGJJ.jpg" + NEW_LINE +
                           "https://www.youtube.com/watch?v=Liy-rj8zMf8" + NEW_LINE + BOLD;
        return message;
    }
    
    private static String getMessage() {
        return getMessage(true);
    }
    
    private static void getDiscord() throws InterruptedException {
        DiscordApi api = new DiscordApiBuilder().setAccountType(AccountType.CLIENT).setToken("NTQ5NjM4NDQyNTE0OTA3MTM4.XPWsnw.erB-mTDLM9UXk-h_sQ8xnCqgXzk").login().join();
        
        //We want to generate a random time to post on different discords so their bot will not block us from posting.
        Random random = new Random();
        
        api.getTextChannelById("476778037728378890").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // RuneBlocks
        Thread.sleep(500);
        
        api.getTextChannelById("296213235453919232").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // RSPS Chat
        Thread.sleep(500);
        
        api.getTextChannelById("361792490690445314").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // RuneSuite
        Thread.sleep(500 + random.nextInt(1000));
        
        api.getTextChannelById("482873692091711498").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // Runelocus
        Thread.sleep(5000 + random.nextInt(1000)); //Rate Limit [5 messages / 5 secs]
        
        api.getTextChannelById("460272484608901120").ifPresent(channel -> channel.sendMessage(getMessage(false)).join()); // Rune-Server
        Thread.sleep(500);
        
        api.getTextChannelById("434672806945488896").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // RSPS Central
        Thread.sleep(500);
        
        api.getTextChannelById("426123305783656449").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // RSPS Chat
        Thread.sleep(500);
        
        api.getTextChannelById("542037373655973914").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // Sohan RS
        Thread.sleep(500);
        
        api.getTextChannelById("475778833727881224").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // Diddy
        Thread.sleep(500);
        
        api.getTextChannelById("514841627211202597").ifPresent(channel -> channel.sendMessage(getMessage()).join()); // RSPS community
        Thread.sleep(500);
    }
}