package com.zenyte.game.content.skills.magic.spells.teleports;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 3-2-2019 | 16:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum ItemTeleport implements Teleport {

    MAX_CAPE_WARRIORS_GUILD(new Location(2865, 3546, 0)),
    MAX_CAPE_CRAFTING_GUILD(new Location(2931, 3286, 0)),
    MAX_CAPE_FISHING_GUILD(new Location(2604, 3401, 0)),
    MAX_CAPE_FARMING_GUILD(new Location(1248, 3724, 0)),
    MAX_CAPE_BLACK_CHINCHOMPAS(new Location(3147, 3758, 0)),
    MAX_CAPE_CARNIVEROUS_CHINCHOMPAS(new Location(2557, 2908, 0)),
    MAX_CAPE_OTTOS_GROTTO(new Location(2504, 3484, 0)),
    DIARY_CAPE_TWO_PINTS(new Location(2574,3321,0)),
    DIARY_CAPE_JARR(new Location(3300,3122,0)),
    DIARY_CAPE_SIR_REBRAL(new Location(2978,3342,0)),
    DIARY_CAPE_THORODIN(new Location(2660,3628,0)),
    DIARY_CAPE_FLAX_KEEPER(new Location(2748,3441,0)),
    DIARY_CAPE_PIRATE_JACKIE_THE_FRUIT(new Location(2810,3192,0)),
    DIARY_CAPE_KALEB_PARAMAYA(new Location(2864,2996,1)),
    DIARY_CAPE_JUNGLE_FORESTER(new Location(2800,2945,0)),
    DIARY_CAPE_TZHAAR_MEJ(new Location(2455,5134,0)),
    DIARY_CAPE_ELISE(new Location(1648,3665,0)),
    DIARY_CAPE_HATIUS_CONSAINTUS(new Location(3238,3218,0)),
    DIARY_CAPE_LE_SABRE(new Location(3465,3478,0)),
    DIARY_CAPE_TOBY(new Location(3224,3415,0)),
    DIARY_CAPE_LESSER_FANATIC(new Location(3120,3519,0)),
    DIARY_CAPE_ELDER_GNOME_CHILD(new Location(2466,3459,0)),
    DIARY_CAPE_TWIGGY_O_KORN(new Location(3096,3228,0)),
    MUSIC_CAPE_FALO(new Location(2689,3547,0))
    ;


    private final Location destination;

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public TeleportType getType() {
        return TeleportType.REGULAR_TELEPORT;
    }

    @Override
    public double getExperience() {
        return 0;
    }

    @Override
    public int getRandomizationDistance() {
        return 0;
    }

    @Override
    public int getWildernessLevel() {
        return 20;
    }

    @Override
    public boolean isCombatRestricted() {
        return false;
    }

    @Override
    public Item[] getRunes() {
        return new Item[0];
    }

}
