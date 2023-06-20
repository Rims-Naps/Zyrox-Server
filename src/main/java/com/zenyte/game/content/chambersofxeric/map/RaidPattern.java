package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kris | 8. mai 2018 : 20:35:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RaidPattern {

    /**
     * Do NOT change the order of the constants - serialized to players.
     */
    @Getter private static final String[] layoutCodes = {
            "#FSCCP*#PCSCF* - #WNWSWN#ESEENW",
            "#FSCCS*#PCPSF* - #WSEEEN#WSWNWS",
            "#FSCPC*#CSCPF* - #WNWWSE#EENWWW",
            "#SCCFC*#PSCSF* - #EEENWW#WSEEEN",
            "#SCCFP*#CCSPF* - #NESEEN#WSWNWS",
            "#SCFCP*#CCSPF* - #ESEENW#ESWWNW",
            "#SCFCP*#CSCFS* - #ENEESW#ENWWSW",
            "#SCFPC*#CSPCF* - #WSWWNE#WSEENE",
            "#SCFPC*#PCCSF* - #WSEENE#WWWSEE",
            "#SCFPC*#SCPCF* - #NESENE#WSWWNE",
            "#SCPFC*#CCPSF* - #NWWWSE#WNEESE",
            "#SCPFC*#CSPCF* - #NEEESW#WWNEEE",
            "#SCPFC*#CSPSF* - #WWSEEE#NWSWWN",
            "#SCSPF*#CCSPF* - #ESWWNW#ESENES",
            "#SFCCP*#CSCPF* - #WNEESE#NWSWWN",
            "#SFCCS*#PCPSF* - #ENWWSW#ENESEN",
            "#SPCFC*#CSPCF* - #WWNEEE#WSWNWS",
            "#SPCFC*#SCCPF* - #ESENES#WWWNEE",
            "#SPSFP*#CCCSF* - #NWSWWN#ESEENW",
            "#SCFCP*#CSCPF* - #ENESEN#WWWSEE",
            "#SCPFC*#PCSCF* - #WNEEES#NWSWNW",
            "#FSPCC*#PSCCF* - #WWWSEE#ENWWSW",
            "#FSCCP*#PCSCF* - #ENWWWS#NEESEN",
            "#SCPFC*#CCSSF* - #NEESEN#WSWWNE",
            "#SFCCPC*#PCSCPF* - #WSEENES#WWWNEEE",
            "#SCFCPC*#CSPCSF* - #ESWWNWS#NESENES",
    };

    private static final RaidPattern[] values;

    static {
        val patternList = new ObjectArrayList<RaidPattern>();

        for (val code : layoutCodes) {
            val split = code.split(" - ");
            val rooms = split[0];
            val directions = split[1];
            val l = new ObjectArrayList<LayoutTypeRoom>(16);
            for (int i = 0; i < rooms.length(); i++) {
                val c = rooms.charAt(i);
                val type = c == '#' ? RoomType.START : c == '*' ? RoomType.END : c == 'S' ? RoomType.SCAVENGERS : c == 'C' ? RoomType.COMBAT : c == 'F' ? RoomType.FARMING :
                                                                                                                                               RoomType.PUZZLE;
                val d = directions.charAt(i);
                val direction = d == '#' ? null : d == 'N' ? Direction.NORTH : d == 'E' ? Direction.EAST : d == 'S' ? Direction.SOUTH : Direction.WEST;
                l.add(new LayoutTypeRoom(type, direction));
            }
            patternList.add(new RaidPattern(l, code));
        }
        values = patternList.toArray(new RaidPattern[0]);
    }

    @Getter
    private final List<LayoutTypeRoom> pattern;
    @Getter private final String layout;
    @Getter
    private final int combatRooms;
    private RaidPattern(@NotNull final List<LayoutTypeRoom> pattern, final String layout) {
        this.pattern = pattern;
        this.layout = layout;
        this.combatRooms = (int) pattern.stream().filter(type -> type.getType() == RoomType.COMBAT).count();
    }

    /**
     * Picks a random raid pattern out of the 26 available options.
     * @return a random raid pattern.
     */
    @NotNull public static final RaidPattern random(@NotNull final String partyOwner) {
        val owner = World.getPlayer(partyOwner);
        if (owner.isPresent()) {
            val player = owner.get();
            val disabledSettings = player.getNumericAttribute("disabled raids layouts").intValue();;
            if (disabledSettings != 0) {
                val listOfAvailablePatterns = new ObjectArrayList<RaidPattern>();
                for (int i = 0; i < values.length; i++) {
                    val isEnabled = ((disabledSettings >> i) & 0x1) == 0;
                    if (isEnabled) {
                        listOfAvailablePatterns.add(values[i]);
                    }
                }
                if (!listOfAvailablePatterns.isEmpty()) {
                    return listOfAvailablePatterns.get(Utils.random(listOfAvailablePatterns.size() - 1));
                }
            }
        }
        return values[Utils.random(values.length - 1)];
    }
}
