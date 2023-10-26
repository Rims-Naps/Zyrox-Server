package com.zenyte.discord.commands.general;

import com.zenyte.discord.CommandLog;
import com.zenyte.game.util.Utils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.time.Instant;

public class CommandsCommand implements CommandExecutor {

	@Command(aliases = { "::commands", ";;commands"}, description="Returns a list of all ZenyteBot commands.")

	public String onCommand(User user) {
		if(CommandLog.LOGS.get(user.getIdAsString()) != null)
			if(CommandLog.check(user.getIdAsString()))
				return CommandLog.warn(user.getIdAsString());
		
		EmbedBuilder commands = new EmbedBuilder();
		//commands.setThumbnail(DiscordUtils.ICON);
		commands.setColor(Color.ORANGE);
		commands.setTimestamp(Instant.now());
		commands.setTitle("Please use :: or ;; before the command");
		commands.addField("::players", "returns how many players are on Ruin!", false);
		commands.addField("::faq", "returns a brief list of FAQ", false);
		commands.addField("::commands", "shows you this list", false);
		commands.addField("::refer", "used to track and show off your referrals", false);
		
		CommandLog.log(user.getIdAsString(), Utils.currentTimeMillis());
		user.sendMessage(commands);
		return "";
	}
	
}
