package com.zenyte.game.world.entity;

import com.zenyte.game.world.entity.masks.Hit;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 20/08/2019 20:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public class HitEntry {

    public HitEntry(final Entity source, final int delay, final Hit hit) {
        this.source = source;
        this.delay = delay;
        this.hit = hit;
        this.freshEntry = true;
    }

    private final Entity source;
    private int delay;
    private Hit hit;
    @Getter @Setter
    private boolean freshEntry;
    @Getter @Setter private transient HitEntry next, previous;

    int getAndDecrement() {
        return delay--;
    }

}
