package com.zenyte;

import com.zenyte.database.DatabaseTopology;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.info.WorldProfile;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Kris | 5. march 2018 : 17:05.26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
public class Constants {

	/**
	 * The current cache revision Zenyte is running on.
	 */
	public static final double REVISION = 179.3;

	/**
	 * The instance world profile.
	 */
	public static WorldProfile WORLD_PROFILE;

	/**
	 * Used to identify whether or not different SQL and Discord connections should be
	 * made!
	 */
	public static boolean DISCORD_ENABLED;

	public static boolean SQL_ENABLED = false;
	public static boolean STORE_ENABLED = true && SQL_ENABLED;
	public static boolean VOTE_ENABLED = true && SQL_ENABLED;

	public static boolean SPAWN_MODE = false;
	public static boolean ISAAC = true;
	public static boolean CYCLE_DEBUG = false;
	public static final boolean CHRISTMAS = true;
	public static boolean CASTLE_WARS = false;

	public static boolean CHECK_HUNTER_TRAPS_QUANTITY = true;

	public static boolean ANTIKNOX = false;
	public static boolean WHITELISTING = false;
	public static boolean DUEL_ARENA = true;
	public static boolean GROTESQUE_GUARDIANS = true;
	public static final boolean HALLOWEEN = false;
	public static boolean PURGING_CHUNKS = true;

	public static final Set<String> whitelistedUsernames = new ObjectOpenHashSet<>();

	public static final boolean BOUNTY_HUNTER = false;

	public static final boolean PRIVATE_BETA = false;

	public static double defenceMultiplier = 0.825;

	public static int randomEvent = (int) TimeUnit.HOURS.toTicks(5);

	public static final boolean CONSTRUCTION = true;
	public static boolean CHAMBERS_OF_XERIC = true;
	public static boolean ALCHEMICAL_HYDRA = true;

	public static boolean OPEN_BETA = true;

	//!Case sensitive usernames
	public static final String[] owners = new String[] {
			"gepan", "jade_dev", "jade"
	};

	static {
		whitelistedUsernames.addAll(Arrays.asList(owners));
	}

	public static final boolean isOwner(final Player player) {
		return ArrayUtils.contains(owners, player.getUsername());
	}

	public static final String[] spawn_admins = new String[] {
			"gepan", "jade_dev"
	};

	public static final boolean isSpawnAdmin(final Player player) {
		return ArrayUtils.contains(spawn_admins, player.getUsername()) || isOwner(player);
	}

	public static final DatabaseTopology FAILOVER = DatabaseTopology.LOCAL;

	/**
	 * Whether the game should connect the forums database and validate the
	 * login on a new registration or not.
	 */
	public static final boolean REGISTER_ON_FORUMS = false;

	public static final float TICK = 600;

	public static final float CLIENT_CYCLE = 20;

	public static final float CYCLES_PER_TICK = TICK / CLIENT_CYCLE;

	public static final int MAXIMUM_NUMBER_OF_HANDSHAKE_CONNECTIONS = 25;

	public static final int MAX_CLIENT_BUFFER_SIZE = 5000;
	public static final int MAX_SERVER_BUFFER_SIZE = 40000;

	public static final int MAX_DUPLICATE_FILE_REQUESTS = 10;

	public static final int UNIQUE_PACKET_LIMIT = 10;
	public static final int CUMULATIVE_PACKETS_LIMIT = 0xFF;

	public static boolean BOOSTED_XP = false;
	public static boolean BOOSTED_COX = false;
	public static boolean BOOSTED_TOB = false;

	public static boolean EVENTSET = false;
	public static Location EVENTHOSTLOCATION = new Location(0,0,0);

	public static boolean BOOSTED_SKILLING_PETS = false;
	public static final double BOOSTED_SKILLING_PET_RATE = 0.15; // this is a 15% boost
	
	public static boolean BOOSTED_BOSS_PETS = false;
	public static final double BOOSTED_BOSS_PET_RATE = 0.15; // this is a 15% boost
	
	public static int BOOSTED_XP_MODIFIER = 50;
	
	public static boolean FILTERING_DUPLICATE_JS5_REQUESTS = false;
	
	public static final Object2IntMap<String> starterIPMap = new Object2IntOpenHashMap<>();
	
	public static String UPDATE_LOG_BROADCAST = "The arrival of the Theatre of Blood and way more [READ HERE]";

	public static String UPDATE_LOG_URL = "https://forums.zenyte.com/topic/11820-december-2022-theatre-of-blood/";
	
}
