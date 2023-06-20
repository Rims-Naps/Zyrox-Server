package com.zenyte.game.content.skills.hunter.object;

import com.zenyte.game.content.skills.hunter.BirdHousePosition;
import com.zenyte.game.content.skills.hunter.node.BirdHouseState;
import com.zenyte.game.content.skills.hunter.node.BirdHouseType;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.types.config.ObjectDefinitions;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * @author Kris | 24/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Birdhouse {

    /**
     * A weak transient reference to the owner of the bird house.
     */
    private transient WeakReference<Player> playerWeakReference;

    /**
     * The epoch time when the bird house was first fully filled. The time is 0 if it hasn't been filled.
     */
    @Getter @Setter
    private long fillTime;

    /**
     * The information integer contains bitpacked information about the birdhouse, it's location and number of seeds.
     * The bit distribution is as follows
     * <p>Bits 0-8 are used for defining the current state of the house.</p>
     * <p>Bits 8-16 are used for defining the current type of the birdhouse.</p>
     * <p>Bits 16-24 are used for defining the position of the birdhouse.</p>
     * <p>Bits 24-32 are used for defining the number of seeds currently in the birdhouse.</p>
     */
    private int information;

    public Birdhouse(@NotNull final Player player) {
        setPlayerReference(player);
    }

    public BirdHouseState getState() {
        val stateOrdinal = information & 0xFF;
        if (stateOrdinal >= BirdHouseState.getValues().size()) {
            throw new IllegalStateException();
        }
        return BirdHouseState.getValues().get(stateOrdinal);
    }

    public Birdhouse setState(@NotNull final BirdHouseState state) {
        information &= ~0xFF;
        information |= state.ordinal() & 0xFF;
        return this;
    }

    public BirdHouseType getType() {
        val stateOrdinal = (information >> 8) & 0xFF;
        if (stateOrdinal >= BirdHouseType.getValues().size()) {
            throw new IllegalStateException();
        }
        return BirdHouseType.getValues().get(stateOrdinal);
    }

    public Birdhouse setType(@NotNull final BirdHouseType birdhouseType) {
        information &= ~(0xFF << 8);
        information |= (birdhouseType.ordinal() & 0xFF) << 8;
        return this;
    }

    public BirdHousePosition getPosition() {
        val stateOrdinal = (information >> 16) & 0xFF;
        if (stateOrdinal >= BirdHousePosition.getValues().size()) {
            throw new IllegalStateException();
        }
        return BirdHousePosition.getValues().get(stateOrdinal);
    }

    public Birdhouse setPosition(@NotNull final BirdHousePosition birdHousePosition) {
        information &= ~(0xFF << 16);
        information |= (birdHousePosition.ordinal() & 0xFF) << 16;
        return this;
    }

    public int getSeedsFilled() {
        return (information >> 24) & 0xFF;
    }

    public Birdhouse setSeedsFilled(final int amount) {
        information &= ~(0xFF << 24);
        information |= (amount & 0xFF) << 24;
        return this;
    }

    public int getLengthInMinutes() {
        val player = playerWeakReference.get();
        if (player == null) {
            return 50;
        }
        val memberRank = player.getMemberRank();
        if (memberRank.eligibleTo(MemberRank.ONYX_MEMBER)) {
            return 20;
        } else if (memberRank.eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 25;
        } else if (memberRank.eligibleTo(MemberRank.DIAMOND_MEMBER)) {
            return 30;
        } else if (memberRank.eligibleTo(MemberRank.RUBY_MEMBER)) {
            return 35;
        } else if (memberRank.eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 40;
        } else if (memberRank.eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
            return 45;
        }
        return 50;
    }

    public void checkCompletion() {
        //If the bird house hasn't been filled yet, is done, skip it.
        if (fillTime == 0 || fillTime == Long.MAX_VALUE) {
            return;
        }
        val timeInMillisecondsUntilCompleted = TimeUnit.MINUTES.toMillis(getLengthInMinutes());
        if (fillTime + timeInMillisecondsUntilCompleted < System.currentTimeMillis()) {
            setState(BirdHouseState.FINISHED);
            fillTime = Long.MAX_VALUE;
            refreshVarbits();
        }
    }

    public void refreshVarbits() {
        val player = playerWeakReference.get();
        if (player == null) {
            return;
        }
        val state = getState();
        val type = getType();
        val position = getPosition();
        val varp = ObjectDefinitions.getOrThrow(position.getObjectId()).getVarp();
        player.getVarManager().sendVar(varp, 1 + (type.ordinal() * 3) + state.ordinal());
    }

    public void setPlayerReference(@NotNull final Player player) {
        this.playerWeakReference = new WeakReference<>(player);
    }

}
