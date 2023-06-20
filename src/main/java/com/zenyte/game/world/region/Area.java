package com.zenyte.game.world.region;

import com.zenyte.game.world.Position;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.area.plugins.LoginPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Kris | 16. mai 2018 : 14:30:00
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public abstract class Area {

    public static final Map<String, Consumer<Player>> onLoginAttachments = new HashMap<>();

	public static final int CHUNK_SIZE = 8;

	public Area() {
		players = new HashSet<>();
		val polys = polygons();
		if (polys != null) {
			addPolygons(polys);
		}
	}

	@Getter
	protected Set<Player> players;
	@Getter
	protected List<RSPolygon> polygons = new ArrayList<>();
	@Getter protected Area superArea;
	@Getter protected List<Area> extendAreas = new ArrayList<>();
	@Getter private final MutableInt areaTimer = new MutableInt();

	public void addSuper(final Area area) {
		if (superArea != null) {
			throw new RuntimeException("Super area is already set!");
		}
		superArea = area;
	}

	public void addExtends(final Area area) {
		if (extendAreas.contains(area)) {
			return;
		}
		extendAreas.add(area);
	}

	public abstract RSPolygon[] polygons();

	public void addPolygons(final RSPolygon... polygons) {
		if (!this.polygons.isEmpty()) {
			this.polygons.clear();
		}
        this.polygons.addAll(Arrays.asList(polygons));
	}

    public void add(final Player player) {
        players.add(player);
        player.getAreaManager().setArea(this);
        enter(player);
        if (this instanceof DynamicArea) {
            player.getAttributes().put("LAST_DYNAMIC_AREA", this.name());
            val onEnter = ((DynamicArea) this).onLoginLocation();
            if (onEnter != null)
                player.getAttributes().put("ON_ENTER_LOCATION", onEnter.getPositionHash());
        }
    }

    public void remove(final Player player, final boolean logout) {
        players.remove(player);
        leave(player, logout);
        player.getAreaManager().setArea(null);
    }

    public abstract void enter(final Player player);

	public abstract void leave(final Player player, boolean logout);

	public abstract String name();

	public boolean inside(final Location location) {
		if (polygons.isEmpty()) {
			return false;
		}

		for (int i = polygons.size() - 1; i >= 0; i--) {
		    if (polygons.get(i).contains(location))
		        return true;
        }

		return false;
	}

	public Area getExtension(final Position tile) {
        Area extension = this;

        while (extension.superArea != null && extension.superArea.inside(tile.getPosition())) {
            extension = extension.superArea;
        }

        Area area;
        while(!extension.extendAreas.isEmpty()) {
            area = extension;
            for (int i = extension.extendAreas.size() - 1; i >= 0; i--) {
                Area nextPick = extension.extendAreas.get(i);
                if (nextPick.inside(tile.getPosition())) {
                    extension = nextPick;
                    break;
                }
            }
            if (extension == area) {
                break;
            }
        }
        return extension;
    }

	public boolean updateLocation(final Player player, final boolean login, final boolean logout) {
		val extension = getExtension(player);
		if (extension != this) {
		    removePlayer(player, logout);
		    return true;
		}
		return false;
	}

    void removePlayer(final Player player, final boolean logout) {
        if (!logout) {
            player.getAttributes().remove("ON_ENTER_LOCATION");
        }
        remove(player, logout);
    }

    void addPlayer(final Player player, final boolean login) {
        if (inside(player.getLocation())) {
            try {
                if (login && this instanceof LoginPlugin) {
                    ((LoginPlugin) this).login(player);
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            add(player);
        }
    }

	@Override
	public String toString() {
		return name();
	}

}
