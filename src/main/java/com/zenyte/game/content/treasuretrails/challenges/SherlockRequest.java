package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import lombok.Data;

/**
 * @author Kris | 07/04/2019 13:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class SherlockRequest implements ClueChallenge {

    private final SherlockTask task;

}
