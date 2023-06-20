package com.zenyte.discord.commands.general;

import com.zenyte.discord.CommandLog;
import com.zenyte.game.util.Utils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.time.Instant;

public class FAQCommand implements CommandExecutor {

	@Command(aliases = { "::faq", ";;faq" }, description="A list of frequently asked questions and their answers.")
	
	public String onCommand(final User user) {
		if(CommandLog.LOGS.get(user.getIdAsString()) != null)
			if(CommandLog.check(user.getIdAsString()))
				return CommandLog.warn(user.getIdAsString());
		
		final EmbedBuilder faq = new EmbedBuilder();
		faq.setThumbnail("https://vgy.me/6iKamR.png");
		faq.setColor(Color.ORANGE);
		faq.setTitle("Zenyte #161 - FAQ");
        faq.setTimestamp(Instant.now());
        faq.addField("What on earth is Zenyte?", "Zenyte is a pure OSRS Runescape Private Server targetting the revision #156.", false);       
        faq.addField("Alright cool, but what makes you different from the rest?", "Zenyte has been developed from the ground up"
        		+ " to build a stable and extensive framework that will allow us to properly implement an unrivaled amount of OSRS's features;"
        		+ " as developers, we pay attention to details. We know that RSPS veterans are looking for an accurate, sustainable emulation of OSRS's"
        		+ " economy and gameplay. As players ourselves, we know the value of the grind that comes with competing to hold the top spot."
        		+ " Zenyte will feature unique methods for community integration that will foster competition, allowing new players to have an equal chance to earn"
        		+ " their glory against veterans.", false);
        faq.addField("When will Zenyte be released?", "We have no set ETA as of right now, but are planning on a pre-summer release."
        		+ "You can track our progress on our project thread! When the forums are released, all updates will be tracked there,"
        		+ " and past updates will be posted seperately.", false);
        faq.setColor(Color.ORANGE);
        faq.addField("Where can I view that project thread?", "Here! https://www.rune-server.ee/runescape-development/rs2-server/projects/667308-156-zenyte-crafted-perfection.html", false);
        faq.addField("I have a question about the server", "All of our staff are extremely active on discord, feel free to pm any of us or just drop"
        		+ " your question in #general and one of us will be with you ASAP.", false);
        user.sendMessage(faq);
		CommandLog.log(user.getIdAsString(), Utils.currentTimeMillis());
        return "";
	}
}
