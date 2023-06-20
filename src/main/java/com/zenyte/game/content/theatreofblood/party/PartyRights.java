package com.zenyte.game.content.theatreofblood.party;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 5/24/2020 | 12:27 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum PartyRights {

    CAN_APPLY(0),
    PARTY_MEMBER(1),
    LEADER(2),
    APPLICANT(3),
    BLOCKED_APPLICANT(4);
    
    private final int id;
}
