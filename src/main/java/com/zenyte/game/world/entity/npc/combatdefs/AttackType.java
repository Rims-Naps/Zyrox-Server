package com.zenyte.game.world.entity.npc.combatdefs;

/**
 * @author Kris | 18/11/2018 02:52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum AttackType {
    STAB,
    SLASH,
    CRUSH,
    RANGED,
    MAGIC,

    /**
     * Melee will redirect to either stab, slash or crush, depending on what it is defined as in the npc's definitions.
     */
    MELEE;

    public boolean isMelee() {
        return equals(STAB) || equals(SLASH) || equals(CRUSH) || equals(MELEE);
    }

    public boolean isRanged() {
        return equals(RANGED);
    }

    public boolean isMagic() {
        return equals(MAGIC);
    }
}
