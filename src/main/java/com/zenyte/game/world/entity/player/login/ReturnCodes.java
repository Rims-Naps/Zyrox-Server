package com.zenyte.game.world.entity.player.login;

/**
 * @author Tommeh | 26 mei 2018 | 15:35:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ReturnCodes {
	
    /**
	 * Anything else is 'unexpected response, please try again'.
	 */
    public static final int DISPLAY_ADVERTISEMENT = 1;
    public static final int LOGIN_OK = 2;
    public static final int INVALID_USERNAME_OR_PASSWORD = 3;
    public static final int BANNED = 4;
    public static final int ALREADY_ONLINE = 5;
    public static final int GAME_UPDATED_RELOAD = 6;
    public static final int WORLD_FULL = 7;
    public static final int LOGIN_SERVER_OFFLINE = 8;
    public static final int LOGIN_LIMIT_EXCEEDED = 9;
    public static final int BAD_SESSION_ID = 10;
    public static final int FORCE_CHANGE_PASSWORD = 11;
    public static final int MEMBERS_WORLD = 12;
    public static final int COULD_NOT_COMPLETE = 13;
    public static final int UPDATE_IN_PROGRESS = 14;
    public static final int TOO_MANY_ATTEMPTS = 16;
    public static final int MEMBERS_ONLY_AREA = 17;
    public static final int ACCOUNT_LOCKED = 18;
    public static final int CLOSED_BETA = 19;
    public static final int INVALID_LOGINSERVER_REQUESTED = 20;
    public static final int MALFORMED_LOGIN_PACKET = 22;
    public static final int NO_REPLY_FROM_LOGINSERVER = 23;
    public static final int ERROR_LOADING_PROFILE = 24;
    public static final int UNEXPECTED_LOGINSERVER_RESPONSE = 25;
    public static final int COMPUTER_ADDRESS_BLOCKED = 26;
    public static final int SERVICE_UNAVAILABLE = 27;
    
}