package com.zenyte.game.content.minigame.castlewars;

import com.zenyte.game.world.entity.Location;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static com.zenyte.game.content.minigame.castlewars.CastleWarsArea.SARADOMIN_RESPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsArea.ZAMORAK_RESPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.SARADOMIN_LOBBY_SPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.ZAMORAK_LOBBY_SPAWN;
import static com.zenyte.plugins.object.CastleWarsLargeDoor.SARADOMIN_DOORS;
import static com.zenyte.plugins.object.CastleWarsLargeDoor.ZAMORAK_DOORS;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
@AllArgsConstructor
public enum CastleWarsTeam {



    SARADOMIN(SARADOMIN_RESPAWN, SARADOMIN_LOBBY_SPAWN, new IntArrayList(SARADOMIN_DOORS), Arrays.asList(CastlewarsRockPatch.SOUTH, CastlewarsRockPatch.EAST)),
    ZAMORAK(ZAMORAK_RESPAWN, ZAMORAK_LOBBY_SPAWN, new IntArrayList(ZAMORAK_DOORS),  Arrays.asList(CastlewarsRockPatch.NORTH, CastlewarsRockPatch.WEST)),
    ;

    @Getter private final Location respawn;
    @Getter private final Location lobbySpawn;
    @Getter private final IntList largeCastleDoors;
    @Getter private final List<CastlewarsRockPatch> rockPatches;

    @Getter @Setter private static int saraBarricades = 0;
    @Getter @Setter private static int zamBarricades = 0;

    public static final CastleWarsTeam[] VALUES = values();
}
