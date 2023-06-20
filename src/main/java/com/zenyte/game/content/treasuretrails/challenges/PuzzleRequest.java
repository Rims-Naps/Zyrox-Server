package com.zenyte.game.content.treasuretrails.challenges;

import lombok.Data;

/**
 * @author Kris | 07/04/2019 13:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class PuzzleRequest implements ClueChallenge {
    private final int[] validNPCs;
}
