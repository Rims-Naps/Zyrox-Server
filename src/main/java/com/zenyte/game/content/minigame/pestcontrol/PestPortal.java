package com.zenyte.game.content.minigame.pestcontrol;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.Getter;

/**
 * @author Kris | 26. juuni 2018 : 19:12:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum PestPortal {

    WESTERN(NpcId.PORTAL, NpcId.PORTAL_1743, "Western shield", "Western health",
            new Location(2628, 2591, 0), new Location(2631, 2591, 0), "The purple, western portal shield has dropped!"),
    SOUTH_WESTERN(NpcId.PORTAL_1742, NpcId.PORTAL_1746, "South-Western shield", "South-Western health",
            new Location(2645, 2569, 0), new Location(2645, 2572, 0), "The red, south-western portal shield has dropped!"),
    SOUTH_EASTERN(NpcId.PORTAL_1741, NpcId.PORTAL_1745, "South-Eastern shield", "South-Eastern health",
            new Location(2669, 2570, 0), new Location(2669, 2573, 0), "The yellow, south-eastern portal shield has dropped!"),
    EASTERN(NpcId.PORTAL_1740, NpcId.PORTAL_1744, "Eastern shield", "Eastern health",
            new Location(2680, 2588, 0), new Location(2677, 2588, 0), "The blue, eastern portal shield has dropped!");

    public static final PestPortal[] VALUES = values();
    @Getter
    private final int id, protectedId;
    private final String shieldComponentName, healthComponentName;
    @Getter
    private final Location tile, npcSpawnTile;
    @Getter
    private String dropMessage;

    public int getShieldComponentName() {
        return GameInterface.PEST_CONTROL_GAME_OVERLAY.getPlugin().orElseThrow(IllegalArgumentException::new).getComponent(shieldComponentName);
    }

    public int getHealthComponentName() {
        return GameInterface.PEST_CONTROL_GAME_OVERLAY.getPlugin().orElseThrow(IllegalArgumentException::new).getComponent(healthComponentName);
    }

    PestPortal(final int id, final int protectedId, final String shieldComponentName, final String healthComponentName, final Location tile, final Location npcSpawnTile, final String dropMessage) {
        this.id = id;
        this.protectedId = protectedId;
        this.shieldComponentName = shieldComponentName;
        this.healthComponentName = healthComponentName;
        this.tile = tile;
        this.npcSpawnTile = npcSpawnTile;
        this.dropMessage = dropMessage;
    }

}
