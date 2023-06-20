package com.zenyte.plugins.events;

import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.plugins.Event;
import lombok.Data;

/**
 * @author Kris | 21/03/2019 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class FloorItemSpawnEvent implements Event {

    private final FloorItem item;

}
