package com.zenyte.discord.commands.game;

import com.zenyte.discord.CommandLog;
import com.zenyte.game.util.Utils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.user.User;

public class StatsCommand implements CommandExecutor {
	
	@Command(aliases = { "::stats", ";;stats" }, description = "used to check the stats of other players on Zenyte")
	public String onCommand(User user, Object... args) {
		if(CommandLog.LOGS.get(user.getIdAsString()) != null)
			if(CommandLog.check(user.getIdAsString()))
				return CommandLog.warn(user.getIdAsString());
		
		final String name = args[0].toString();
		
		CommandLog.log(user.getIdAsString(), Utils.currentTimeMillis());
		return name;
	}
}
