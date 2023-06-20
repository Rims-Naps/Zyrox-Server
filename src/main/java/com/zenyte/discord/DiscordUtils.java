package com.zenyte.discord;

import java.util.Arrays;
import java.util.List;

public class DiscordUtils {

	private static final String[] admins = {
			"215640141400637440", // Noele
			"734129235530219540" // Gepan
	};
	

	public static final List<String> ADMINS = Arrays.asList(admins);
	
	public static boolean isAdmin(String id) {
		return ADMINS.contains(id);
	}
}
