package com.zenyte.discord.commands.general;

import com.zenyte.discord.CommandLog;
import com.zenyte.game.util.Utils;

import de.btobastian.javacord.entities.User;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class TestCommand implements CommandExecutor {

	@Command(aliases = { "::test", ";;test" }, description = "A command used for development!")
	public String onCommand(User user) {
		if(CommandLog.LOGS.get(user.getIdAsString()) != null)
			if(CommandLog.check(user.getIdAsString()))
				return CommandLog.warn(user.getIdAsString());
		
		CommandLog.log(user.getIdAsString(), Utils.currentTimeMillis());
		return "```User id: "+user.getIdAsString()+"```";
	}
	
}
