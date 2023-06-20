package com.zenyte.game.content.skills.magic.spells.teleports.structures;

import com.zenyte.game.world.entity.masks.Animation;

/**
 * @author Kris | 11/06/2019 16:11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EctophialStructure implements TeleportStructure {

    private static final Animation animation = new Animation(878);

    @Override
    public Animation getStartAnimation() {
        return animation;
    }
}
