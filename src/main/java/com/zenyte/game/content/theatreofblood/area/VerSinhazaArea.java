package com.zenyte.game.content.theatreofblood.area;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.Morytania;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import lombok.val;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tommeh | 5/21/2020 | 4:31 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class VerSinhazaArea extends Morytania implements CycleProcessPlugin {

    public static final Map<Integer, RaidingParty> raidingParties = new LinkedHashMap<>();
    private static int raidCounter; // TODO refactor

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{new RSPolygon(new int[][]{
                {3643, 3239},
                {3658, 3239},
                {3662, 3237},
                {3664, 3237},
                {3668, 3239},
                {3683, 3239},
                {3687, 3235},
                {3687, 3225},
                {3688, 3224},
                {3689, 3224},
                {3693, 3228},
                {3696, 3228},
                {3698, 3226},
                {3698, 3225},
                {3697, 3224},
                {3697, 3223},
                {3696, 3222},
                {3697, 3221},
                {3698, 3221},
                {3699, 3220},
                {3699, 3219},
                {3698, 3218},
                {3697, 3218},
                {3696, 3217},
                {3697, 3216},
                {3697, 3215},
                {3698, 3214},
                {3698, 3213},
                {3696, 3211},
                {3693, 3211},
                {3689, 3215},
                {3688, 3215},
                {3687, 3214},
                {3687, 3204},
                {3683, 3200},
                {3668, 3200},
                {3666, 3198},
                {3665, 3198},
                {3664, 3199},
                {3662, 3199},
                {3661, 3198},
                {3660, 3198},
                {3658, 3200},
                {3643, 3200},
                {3638, 3205},
                {3638, 3211},
                {3637, 3210},
                {3636, 3210},
                {3635, 3211},
                {3635, 3213},
                {3634, 3214},
                {3632, 3214},
                {3631, 3215},
                {3631, 3216},
                {3633, 3218},
                {3633, 3221},
                {3631, 3223},
                {3631, 3224},
                {3632, 3225},
                {3634, 3225},
                {3635, 3226},
                {3635, 3228},
                {3636, 3229},
                {3637, 3229},
                {3638, 3228},
                {3638, 3234}
        })};
    }

    @Override
    public void enter(final Player player) {
        PartyOverlayInterface.refresh(player, null);
        PartyOverlayInterface.fade(player, 200, 0, "");
    }

    @Override
    public void leave(final Player player, final boolean logout) {
        val nextArea = GlobalAreaManager.getArea(player.getLocation());
        if (nextArea instanceof TheatreArea || nextArea instanceof VerSinhazaArea) {
            return;
        } else {
            player.getInterfaceHandler().closeInterface(GameInterface.TOB_PARTY);
            var party = getParty(player, true);
            if (party != null) {
                if (party.getMembers().contains(player.getUsername()) || party.getApplicants().contains(player.getUsername())) {
                    if (party.getMembers().contains(player.getUsername())) {
                        party.removeMember(player);
                    } else {
                        party.getApplicants().remove(player.getUsername());
                    }
                }
            }
        }


    }

    public static RaidingParty createParty(final Player host) {
        val party = new RaidingParty(raidCounter++, host);
        raidingParties.put(party.getId(), party);
        return party;
    }

    public static void removeParty(final RaidingParty party) {
        raidingParties.remove(party.getId());
    }

    public static RaidingParty getParty(final int id) {
        return raidingParties.get(id);
    }

    public static RaidingParty getPartyByIndex(final int index) {
        val ids = raidingParties.keySet().toArray(new Integer[0]);
        if (index >= ids.length) {
            return null;
        }
        return raidingParties.get(ids[index]);
    }

    public static RaidingParty getParty(final Player player, final boolean checkMembers, final boolean checkViewers, final boolean checkSpectators, final boolean checkApplicants) {
        for (val entry : raidingParties.entrySet()) {
            val party = entry.getValue();
            if (checkMembers && party.getMembers().contains(player.getUsername())
                    || (checkViewers && player.getNumericTemporaryAttribute("tob_viewing_party_id").intValue() == party.getId())
                    || (checkSpectators && party.getRaid() != null && party.getRaid().getSpectators().contains(player.getUsername()))
                    || (checkApplicants && party.getApplicants().contains(player.getUsername()))) {
                return party;
            }
        }
        return null;
    }

    public static RaidingParty getParty(final Player player, final boolean checkMembers, final boolean checkViewers, final boolean checkSpectators) {
        return getParty(player, checkMembers, checkViewers, checkSpectators, false);
    }

    public static RaidingParty getParty(final Player player, final boolean checkViewers) {
        return getParty(player, false, checkViewers, false);
    }

    public static RaidingParty getParty(final Player player) {
        return getParty(player, true, false, false);
    }

    public static Map<Integer, RaidingParty> getParties() {
        return raidingParties;
    }

    @Override
    public String name() {
        return "Ver Sinhaza";
    }

    @Override
    public void process() {
    }
}
