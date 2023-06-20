package com.zenyte.game.world.object;

import com.zenyte.game.content.skills.farming.FarmingPatch;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Tommeh | 30 sep. 2018 | 22:36:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>
 */
public enum SpiritTree {

	TREE_GNOME_VILLAGE("Tree Gnome Village", new Location(2542, 3170, 0), Optional.empty()),
	GNOME_STRONGHOLD("Gnome Stronghold", new Location(2461, 3444, 0), Optional.empty()),
    BATTLEFIELD_OF_KHAZARD("Battlefield of Khazard", new Location(2555, 3259, 0), Optional.empty()),
	GRAND_EXCHANGE("Grand Exchange", new Location(3185, 3508, 0), Optional.empty()),
    EDGEVILLE("Edgeville", new Location(3095, 3489, 0), Optional.empty()),
	FELDIP_HILLS("Feldip Hills", new Location(2488, 2850, 0), Optional.empty()),
    PORT_SARIM("Port Sarim", new Location(3059, 3261, 0),
            Optional.of(player -> player.getFarming().containsGrownSpiritTree(FarmingPatch.PORT_SARIM_SPIRIT_TREE))),
    ETCETERIA("Etceteria", new Location(2610, 3857, 0),
            Optional.of(player -> player.getFarming().containsGrownSpiritTree(FarmingPatch.ETCETERIA_SPIRIT_TREE))),
    BRIMHAVEN("Brimhaven", new Location(2799, 3204, 0),
            Optional.of(player -> player.getFarming().containsGrownSpiritTree(FarmingPatch.BRIMHAVEN_SPIRIT_TREE))),
    HOSIDIUS("Hosidius", new Location(1690, 3541, 0),
            Optional.of(player -> player.getFarming().containsGrownSpiritTree(FarmingPatch.KOUREND_SPIRIT_TREE))),
    FARMING_GUILD("Farming Guild", new Location(1250, 3749, 0),
            Optional.of(player -> player.getFarming().containsGrownSpiritTree(FarmingPatch.FARMING_GUILD_SPIRIT_TREE))),
    YOUR_HOUSE("Your house", null, Optional.of(player -> false));//Until farming.

	@Getter private final Location location;
	private final String locationName;
	@Getter private final Optional<Predicate<Player>> optionalPredicate;
	private static final SpiritTree[] VALUES = values();

	SpiritTree(String locationName, final Location location, Optional<Predicate<Player>> optionalPredicate) {
		this.location = location;
		this.locationName = locationName;
		this.optionalPredicate = optionalPredicate;
	}

	public static final String[] getAvailableOptions(final Player player) {
	    val list = new ObjectArrayList<String>(VALUES.length);
        for (val value : VALUES) {
            val predicate = value.optionalPredicate;
            if (predicate.isPresent()) {
                if (!predicate.get().test(player)) {
                    list.add("<col=777777>" + value.locationName);
                    continue;
                }
            }
            list.add(value.locationName);
        }
	    return list.toArray(new String[0]);
    }

    public static final SpiritTree get(final int slot) {
	    return VALUES[slot];
    }
	
	public static SpiritTree getTree(final Player player) {
	    for (val tree : VALUES) {
	        if (tree.getLocation().withinDistance(player, 10)) {
	            return tree;
            }
        }
		throw new IllegalStateException();
	}

}
