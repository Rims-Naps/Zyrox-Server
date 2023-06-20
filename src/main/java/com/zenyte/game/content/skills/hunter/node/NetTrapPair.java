package com.zenyte.game.content.skills.hunter.node;

import com.zenyte.game.world.object.WorldObject;
import lombok.Data;

/**
 * @author Kris | 01/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class NetTrapPair {
    private final WorldObject net, tree;
}
