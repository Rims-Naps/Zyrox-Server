package com.zenyte.game.content.treasuretrails.challenges;

import lombok.Data;

/**
 * @author Kris | 08/04/2019 20:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class LightBoxRequest implements ClueChallenge {

    private final int[] validNPCs;

}
