package com.zenyte.game.content.treasuretrails.challenges;

import lombok.Data;

/**
 * @author Kris | 04/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class SongRequest implements ClueChallenge {
    private final String song;
}
