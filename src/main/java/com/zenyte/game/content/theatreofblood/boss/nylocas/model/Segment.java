package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tommeh | 6/7/2020 | 3:56 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public class Segment {
    private final List<Spawn> spawns;

    public Segment(final Spawn... spawns) {
        this.spawns = new ArrayList<>();
        this.spawns.addAll(Arrays.asList(spawns));
    }

    public static Segment of(final Spawn... spawns) {
        return new Segment(spawns);
    }
}
