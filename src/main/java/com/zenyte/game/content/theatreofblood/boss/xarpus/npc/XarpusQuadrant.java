package com.zenyte.game.content.theatreofblood.boss.xarpus.npc;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ImmutableLocation;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.BiPredicate;

/**
 * @author Chris
 * @since September 04 2020
 */
@Getter
public enum XarpusQuadrant {
    SOUTH_EAST(3177, 4380, ((target, center) -> target.getX() > center.getX() && target.getY() <= center.getY())),
    SOUTH_WEST(3163, 4380, ((target, center) -> target.getX() <= center.getX() && target.getY() <= center.getY())),
    NORTH_WEST(3163, 4394, ((target, center) -> target.getX() <= center.getX() && target.getY() > center.getY())),
    NORTH_EAST(3177, 4394, ((target, center) -> target.getX() > center.getX() && target.getY() > center.getY()));

    private static final ImmutableLocation CENTER_TILE = new ImmutableLocation(3170, 4387, 1);
    private static final ImmutableSet<XarpusQuadrant> QUADRANTS = Sets.immutableEnumSet(EnumSet.allOf(XarpusQuadrant.class));
    private final ImmutableLocation cornerTile;
    private final BiPredicate<ImmutableLocation, ImmutableLocation> insidePredicate;

    XarpusQuadrant(final int x, final int y, @NotNull final BiPredicate<ImmutableLocation, ImmutableLocation> insidePredicate) {
        this.cornerTile = new ImmutableLocation(x, y, 1);
        this.insidePredicate = insidePredicate;
    }

    public static XarpusQuadrant random() {
        val possibleQuadrants = Lists.newArrayList(QUADRANTS);
        return possibleQuadrants.get(Utils.random(possibleQuadrants.size() - 1));
    }

    public static XarpusQuadrant of(@NotNull final ImmutableLocation faceLocation) {
        for (val quadrant : QUADRANTS) {
            if (faceLocation.equals(quadrant.cornerTile)) {
                return quadrant;
            }
        }
        throw new IllegalArgumentException("Could not find Xarpus quadrant for tile: " + faceLocation);
    }

    public XarpusQuadrant randomOther() {
        val possibleQuadrants = Lists.newArrayList(QUADRANTS);
        possibleQuadrants.remove(this);
        return possibleQuadrants.get(Utils.random(possibleQuadrants.size() - 1));
    }

    public boolean isInside(@NotNull final ImmutableLocation location, @NotNull final Xarpus xarpus) {
        val instanceCenterTile = new ImmutableLocation(xarpus.getRoom().getLocation(CENTER_TILE));
        return insidePredicate.test(location, instanceCenterTile);
    }
}
