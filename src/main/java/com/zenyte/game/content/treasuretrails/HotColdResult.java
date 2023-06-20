package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.content.treasuretrails.challenges.HotColdChallenge;
import com.zenyte.game.item.Item;
import lombok.Data;

/**
 * @author Kris | 10/04/2019 19:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class HotColdResult {

    private final int slotId;
    private final Item clue;
    private final HotColdChallenge challenge;
    private final int distance;
    private final DeviceTemperature temperature;

}
