package com.zenyte.game.content.tog;

import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Chris
 * @since September 13 2020
 */
public enum TearsOfGuthixMemberPerk {
    SAPPHIRE_MEMBER(MemberRank.SAPPHIRE_MEMBER, 5),
    EMERALD_MEMBER(MemberRank.EMERALD_MEMBER, 10),
    RUBY_MEMBER(MemberRank.RUBY_MEMBER, 15),
    DIAMOND_MEMBER(MemberRank.DIAMOND_MEMBER, 20),
    DRAGONSTONE_MEMBER(MemberRank.DRAGONSTONE_MEMBER, 25),
    ONYX_MEMBER(MemberRank.ONYX_MEMBER, 30),
    ZENYTE_MEMBER(MemberRank.ZENYTE_MEMBER, 35);

    private static final List<TearsOfGuthixMemberPerk> PERKS = ObjectLists.unmodifiable(new ObjectArrayList<>(values()));
    /**
     * The member rank a player must have to receive the bonus time.
     */
    private final MemberRank rank;
    /**
     * The amount of extra ticks to append to the Tear Of Guthix timer.
     */
    private final int bonusTime;

    TearsOfGuthixMemberPerk(@NotNull final MemberRank rank, final int bonusSeconds) {
        this.rank = rank;
        this.bonusTime = (int) TimeUnit.SECONDS.toTicks(bonusSeconds);
    }

    /**
     * Returns the extra amount of ticks for the highest member rank a player is eligible for.
     * If player has no member rank then no extra time is .
     */
    public static int ticks(@NotNull final Player player) {
        for (int index = PERKS.size() - 1; index >= 0; index--) {
            val perk = PERKS.get(index);
            if (player.getMemberRank().eligibleTo(perk.rank)) {
                return perk.bonusTime;
            }
        }
        return 0;
    }
}
