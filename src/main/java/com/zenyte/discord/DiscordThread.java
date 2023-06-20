package com.zenyte.discord;

import com.zenyte.game.util.Utils;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

@Slf4j
public class DiscordThread extends Thread {

	public static volatile boolean ENABLED = true;	
	private static String auth = "NDE3MjkxODQyNjc4MjkyNDgw.DXRPIQ.pcDAjV6s5aMyTz15YLpy74jHcnQ";
	
	public static DiscordApi api;
	public static CommandHandler handler;

	
	@Override
	public void run() {
		try {
			this.init();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}
	
	public void init() {
		final DiscordApi api = new DiscordApiBuilder().setToken(auth).login().join();
		handler = new JavacordHandler(api);
		this.commands();
	}
	
	public void commands() {
		try {
			final Class<?>[] classes = Utils.getClasses("com.zenyte.discord.commands");
			for(final Class<?> c : classes) {
				if(c.isAnonymousClass() || c.isMemberClass())
					continue;
				final Object o = c.newInstance();
				if(!(o instanceof CommandExecutor))
					continue;
				final CommandExecutor command = (CommandExecutor) o;
				handler.registerCommand(command);
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
	}
	
}
