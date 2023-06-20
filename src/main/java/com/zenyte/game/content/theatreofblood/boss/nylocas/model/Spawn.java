package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Tommeh | 6/12/2020 | 7:33 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public class Spawn {

    public static int globalIndex;

    @Setter
    private int index;

    private NylocasType type;
    private boolean aggressive, large;
    private PillarLocation target;
    private List<NylocasType> transformations;

    public Spawn(final Spawn spawn) {
        index = spawn.index;
        type = spawn.type;
        aggressive = spawn.aggressive;
        large = spawn.large;
        target = spawn.target;
        if (spawn.transformations != null) {
            transformations = new ArrayList<>(spawn.transformations);
        }
    }

    public static Spawn of(final NylocasType type) {
        val spawn = new Spawn();
        spawn.index = globalIndex++;
        spawn.type = type;
        return spawn;
    }

    public Spawn large() {
        this.large = true;
        return this;
    }

    public Spawn aggressive() {
        this.aggressive = true;
        return this;
    }

    public Spawn target(final PillarLocation location) {
        this.target = location;
        return this;
    }

    public Spawn transformations(final NylocasType... transformations) {
        this.transformations = new ArrayList<>(Arrays.asList(transformations));
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Spawn)) {
            return false;
        }
        val spawn = (Spawn) obj;
        return spawn.index == index;
    }
}
