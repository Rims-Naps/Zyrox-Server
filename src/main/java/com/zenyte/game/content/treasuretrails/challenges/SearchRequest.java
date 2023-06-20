package com.zenyte.game.content.treasuretrails.challenges;

import lombok.Data;

/**
 * @author Kris | 07/04/2019 13:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class SearchRequest implements ClueChallenge {
    private final GameObject[] validObjects;
}
