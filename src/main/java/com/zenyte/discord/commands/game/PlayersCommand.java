package com.zenyte.discord.commands.game;

import com.zenyte.discord.CommandLog;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;

import de.btobastian.javacord.entities.User;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class PlayersCommand implements CommandExecutor {
	
	@Command(aliases = { "::players", ";;players" }, description = "Returns the amount of players currently on the server.")
	
	public String onCommand(User user) {
		if(CommandLog.LOGS.get(user.getIdAsString()) != null)
			if(CommandLog.check(user.getIdAsString()))
				return CommandLog.warn(user.getIdAsString());
		
		int players = World.getPlayers().size();
		CommandLog.log(user.getIdAsString(), Utils.currentTimeMillis());
		return "```There " + (players != 1 ? "are" : "is") + " currently " + players + " " + (players != 1 ? "players" : "player") + " on Zenyte!```";
	}

}
