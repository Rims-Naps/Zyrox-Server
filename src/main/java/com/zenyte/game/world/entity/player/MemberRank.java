package com.zenyte.game.world.entity.player;

import com.zenyte.api.model.Role;
import com.zenyte.game.util.Utils;
import com.zenyte.utils.Ordinal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tommeh | 5-4-2019 | 16:15
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@Ordinal
@RequiredArgsConstructor
public enum MemberRank {
    
    NONE(31, null, "000000", -1),
    SAPPHIRE_MEMBER(14, Role.SAPPHIRE_MEMBER, "2188ff", 200), //10 min
    EMERALD_MEMBER(15, Role.EMERALD_MEMBER, "34d058", 100), //8 min
    RUBY_MEMBER(16, Role.RUBY_MEMBER, "ea4a5a", 75), // 6 min
    DIAMOND_MEMBER(17, Role.DIAMOND_MEMBER, "e1e4e8", 50), // 4 min
    DRAGONSTONE_MEMBER(18, Role.DRAGONSTONE_MEMBER, "9673d7", 25), //2 min
    ONYX_MEMBER(19, Role.ONYX_MEMBER, "46505c", 8),  //1 min
    ZENYTE_MEMBER(20, Role.ZENYTE_MEMBER, "f3ae59", 0); //30 sec
    
    private final int icon;
    private final Role apiRole;
    private final String yellColor;
    private final int yellDelay;
    private static final Set<MemberRank> ALL = EnumSet.allOf(MemberRank.class);
    private static final Map<Integer, MemberRank> RANKS = new HashMap<>();
    
    public String getCrown() {
        return this.equals(NONE) ? "" : "<img=" + icon + ">";
    }

    static {
        for (val rank : ALL) {
            RANKS.put(rank.ordinal(), rank);
        }
    }

    public boolean eligibleTo(final MemberRank member) {
        return ordinal() >= member.ordinal();
    }

    public static MemberRank get(final int index) {
        return RANKS.get(index);
    }

    @Override
    public String toString() {
        return Utils.formatString(name().toLowerCase().replace("_", " "));
    }

}
