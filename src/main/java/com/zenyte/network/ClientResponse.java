package com.zenyte.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 27 jul. 2018 | 19:10:00
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum ClientResponse {

    SUCCESSFUL(0),    
    DISPLAY_ADVERTISEMENT(1),
    LOGIN_OK(2),
    INVALID_USERNAME_OR_PASSWORD(3),
    BANNED(4),
    ALREADY_ONLINE(5),
    SERVER_UPDATED(6),
    WORLD_FULL(7),
    LOGIN_SERVER_OFFLINE(8),
    TOO_MANY_CONNECTIONS(9),
    BAD_SESSION_ID(10),
    CREDENTIALS_UNSECURE(11),
    MEMBERS_WORLD(12),
    LOGIN_INCOMPLETE(13),
    UPDATE_IN_PROGRESS(14),
    LOGIN_EXCEEDED(16),
    IN_MEMBERS_AREA(17),
    CLOSED_BETA(19),
    INVALID_LOGIN_SERVER_REQUEST(20),
    JUST_LEFT_WORLD(21),    
    MALFORMED_LOGIN_PACKET(22),    
    NO_REPLY_FROM_LOGINSERVER(23),    
    ERROR_LOADING_PROFILE(24),   
    UNEXPECTED_LOGINSERVER_RESPONSE(25),   
    COMPUTER_ADDRESS_BLOCKED(26),
    SERVICE_UNAVAILABLE(26),
    CLIENT_UPDATED(39),
    ACCOUNT_DOES_NOT_EXIST(40),
    AUTHENTICATOR(56),
    WRONG_AUTHENICATOR_CODE(57);
    
    private final int id;
    
}
