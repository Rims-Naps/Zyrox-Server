package com.zenyte.game.content.boss.skotizo.instance;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.boss.skotizo.npc.AwakenedAltar;
import com.zenyte.game.content.boss.skotizo.npc.Skotizo;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.area.plugins.LogoutPlugin;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Collections;
import java.util.List;

/**
 * @author Tommeh | 05/03/2020 | 19:30
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public class SkotizoInstance extends DynamicArea implements LogoutPlugin, CycleProcessPlugin, LootBroadcastPlugin {

    public static final Location outsideLocation = new Location(1665, 10048, 0);
    private static final Location[] altarLocations = {
            new Location(1678, 9888, 0), new Location(1714, 9888, 0),
            new Location(1696, 9871, 0), new Location(1694, 9904, 0),
    };

    private transient Player player;
    private final Location bossLocation;
    @Getter private final List<AwakenedAltar> altars;
    private final Skotizo skotizo;
    @Getter @Setter
    private AwakenedAltar lastSlainAltar;
    @Getter @Setter private int altarSpawnCounter, altarSpawnCap;
    public static final Location southWesternTile = new ImmutableLocation(1680, 9873, 0);
    public static final Location northEasternTile = new ImmutableLocation(1712, 9902, 0);

    public static final Location southWesternSkotizoTile = new ImmutableLocation(1682, 9875, 0);
    public static final Location northEasternSkotizoTile = new ImmutableLocation(1707, 9896, 0);

    public static final Location southWesternPlayerTile = new ImmutableLocation(1682, 9874, 0);
    public static final Location northEasternPlayerTile = new ImmutableLocation(1710, 9890, 0);

    public SkotizoInstance(final Player player, final AllocatedArea area) {
        super(area, 208, 1232);
        this.player = player;
        bossLocation = getLocation(new Location(Utils.random(southWesternSkotizoTile.getX(), northEasternSkotizoTile.getX()),
                Utils.random(southWesternSkotizoTile.getY(), northEasternSkotizoTile.getY()), 0));
        val altars = new ObjectArrayList<AwakenedAltar>(4);
        for (int index = 0; index < 4; index++) {
            val altar = new AwakenedAltar(index, getLocation(altarLocations[index]), this);
            altars.add(altar);
            altar.resetAwakeningDelay();
        }
        this.altars = Collections.unmodifiableList(altars);
        skotizo = (Skotizo) new Skotizo(this).spawn();
        this.altarSpawnCap = Utils.random(6, 10);
    }

    @Override
    public void constructed() {
        player.lock(1);
        player.setAnimation(Animation.STOP);
        player.setLocation(calculatePlayerSpawnLocation());
        skotizo.setTarget(player);
        player.setViewDistance(Player.SCENE_DIAMETER);
    }

    private final Location calculatePlayerSpawnLocation() {
        for (int i = 0; i < 100; i++) {
            val tile = getLocation(new Location(Utils.random(southWesternPlayerTile.getX(), northEasternPlayerTile.getX()),
                    Utils.random(southWesternPlayerTile.getY(), northEasternPlayerTile.getY()), 0));
            if (tile.getDistance(bossLocation) > 7) {
                return tile;
            }
        }
        //If no tile was found by the above calculation, we just ignore the checks and return a randomized tile regardless of whether or not it collides.
        return getLocation(new Location(Utils.random(southWesternPlayerTile.getX(), northEasternPlayerTile.getX()),
                Utils.random(southWesternPlayerTile.getY(), northEasternPlayerTile.getY()), 0));
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {
        // Extra check to make sure it doesn't mess with the screen fading when leaving with the portal
        if (player.getInterfaceHandler().isPresent(GameInterface.SKOTIZO_OVERLAY)) {
            player.getInterfaceHandler().closeInterface(GameInterface.SKOTIZO_OVERLAY);
        }
        player.setViewDistance(Player.SMALL_VIEWPORT_RADIUS);
    }

    @Override
    public Location onLoginLocation() {
        return outsideLocation;
    }

    public void refreshOverlay() {
        if (skotizo.isDead() || skotizo.isFinished() || player.isDead() || player.isTeleported()) {
            player.getInterfaceHandler().closeInterface(GameInterface.SKOTIZO_OVERLAY);
            return;
        }
        val args = new Object[4];
        for (int index = 0; index < altars.size(); index++) {
            args[index] = altars.get(index).isAwakened() ? 1 : 0;
        }
        if (!player.getInterfaceHandler().isPresent(GameInterface.SKOTIZO_OVERLAY)) {
            GameInterface.SKOTIZO_OVERLAY.open(player);
        }
        player.getPacketDispatcher().sendClientScript(1313, args);
    }

    public int getAwakenedAltars() {
        int count = 0;
        for (val altar : altars) {
            if (altar.isAwakened()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String name() {
        return player.getName() + "'s Skotizo Instance";
    }

    @Override
    public void process() {
        if (skotizo.isDead() || skotizo.isFinished() || skotizo.isDisableAltarRespawning()) {
            return;
        }
        for (val altar : altars) {
            if (altar.isAwakened() || altar.getAltarAwakeningDelay() > Utils.currentTimeMillis()) {
                continue;
            }
            altar.awaken();
        }
    }
}
