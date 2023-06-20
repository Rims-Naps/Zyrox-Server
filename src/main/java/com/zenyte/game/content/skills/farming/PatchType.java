package com.zenyte.game.content.skills.farming;

import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Kris | 03/02/2019 02:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
@Getter
public enum PatchType {

    ALLOTMENT(0, new Animation(830), new SoundEffect(1470), null, false),
    FLOWER_PATCH(5, new Animation(2292), null, null,false),
    HERB_PATCH(6, new Animation(2282), null, new SoundEffect(2581), false),
    HOPS_PATCH(1, new Animation(830), new SoundEffect(1470), null, false),
    BUSH_PATCH(4, new Animation(2281), null, new SoundEffect(2437), false),
    TREE_PATCH(2, new Animation(830), new SoundEffect(1470), null, true),
    FRUIT_TREE_PATCH(3, new Animation(2280),null, new SoundEffect(2437), true),
    SPIRIT_TREE_PATCH(7, new Animation(2281),null, null, true),
    HARDWOOD_TREE_PATCH(7, new Animation(2281),null, null, true),
    GIANT_SEAWEED_PATCH(7, new Animation(2281),null, null, false),
    GRAPEVINE_PATCH(7, new Animation(2281),null, new SoundEffect(2437), false),
    MUSHROOM_PATCH(7, new Animation(2292),null, new SoundEffect(2437), false),
    CACTUS_PATCH(7, new Animation(2281),null, new SoundEffect(2437), false),
    BELLADONNA_PATCH(7, new Animation(2282),null, new SoundEffect(2437), false),
    CALQUAT_PATCH(7, new Animation(2281),null, null, true),
    COMPOST_BIN(-1, null,null, null, false),
    REDWOOD_PATCH(7, null,null, null, true),
    ANIMA_PATCH(7, null,null, null, false),
    CELASTRUS_PATCH(7, null,null, null, true),
    HESPORI_PATCH(7, null,null, null, false);


    private final int varbit;
    private final Animation harvestAnimation;
    private final SoundEffect harvestSoundEffect;
    private final SoundEffect pickSoundEffect;
    private final boolean tree;
    private final String sanitizedName = toString();

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", " ");
    }

}