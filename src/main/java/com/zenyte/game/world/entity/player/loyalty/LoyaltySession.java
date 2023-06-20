package com.zenyte.game.world.entity.player.loyalty;

import lombok.Data;

import java.util.Date;

/**
 * @author Kris | 29/04/2019 13:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class LoyaltySession {

    private final Date login;
    private final Date logout;

}
