package com.zenyte.game.content.area.tarnslair.object;

import com.zenyte.game.world.entity.Location;
import lombok.Getter;

public class PassageObject {

    @Getter private final int id;
    @Getter private final Location location;

    public PassageObject(final int id, final Location location) {
        this.id = id;
        this.location = location;
    }
}
