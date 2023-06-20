package com.zenyte.game.content.clans;

import com.google.gson.annotations.Expose;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.content.chambersofxeric.party.RaidParty;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.processor.Listener;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Kris | 22. march 2018 : 23:40.36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ClanChannel {

	/**
	 * The owner of the clan channel, this variable can never change.
	 */
	@Expose
	@Getter
	private final String owner;

	/**
	 * The prefix AKA name of the clan channel.
	 */
	@Expose
	@Getter
	@Setter
	private String prefix;

	/**
	 * Whether the clan is currently disabled or not.
	 */
	@Expose
	@Getter
	private boolean disabled;

	/**
	 * The rank requirments for entering, talking and kicking.
	 */
	@Expose
	@Getter
	@Setter
	private ClanRank enterRank, talkRank, kickRank;

	/**
	 * A map of ranked members' usernames and their respective ranks.
	 */
	@Expose
	@Getter
	private final Map<String, ClanRank> rankedMembers = new HashMap<>(25);

	/**
	 * A set of players who are currently in this clan channel.
	 */
	@Getter private transient Object2LongOpenHashMap<String> bannedMembers = new Object2LongOpenHashMap<>();

	private Set<String> permBannedMembers = new ObjectOpenHashSet<>();

	@Getter private transient Set<Player> members = new ObjectOpenHashSet<>(25);

	public void setTransientVariables() {
	    members = new ObjectOpenHashSet<>(25);
        bannedMembers = new Object2LongOpenHashMap<>();
    }

    public Set<String> getPermBannedMembers() {
	    if (permBannedMembers == null) {
	        permBannedMembers = new ObjectOpenHashSet<>();
        }
	    return permBannedMembers;
    }

	@Listener(type = Listener.ListenerType.LOGOUT)
	public static final void onLogout(final Player player) {
	    val channel = player.getSettings().getChannel();
	    if (channel == null) {
	        return;
        }
	    channel.members.remove(player);
    }

	/**
	 * The raid party of the clan.
	 */
	@Setter
	@Getter
	private transient RaidParty raidParty;

    /**
     * Lazy-loads the owner character if it hasn't been already loaded. Executes the consumer instantly otherwise.
     * @param consumer the consumer that accepts the loaded player.
     */
    void loadOwner(@NotNull final Consumer<Player> consumer) {
        /*if (ownerPlayer == null) {
            val optional = World.getPlayer(owner);
            val optPlayer = optional.orElse(null);
            if (optPlayer != null && !optPlayer.isNulled()) {
                this.ownerPlayer = optPlayer;
            } else {
                CoresManager.getLoginManager().load(owner, true, optionalPlayer -> consumer.accept(ownerPlayer =
                        optionalPlayer.orElseThrow(RuntimeException::new)));
                return;
            }
        }
        consumer.accept(Objects.requireNonNull(ownerPlayer));*/
        CoresManager.getLoginManager().load(owner, true, optionalPlayer -> consumer.accept(optionalPlayer.orElseThrow(RuntimeException::new)));
    }

	public void setDisabled(final boolean value) {
	    this.disabled = value;
	    if (value){
	        if (!bannedMembers.isEmpty()) {
	            bannedMembers.clear();
            }
        }
    }

	public ClanChannel(final String owner) {
		this.owner = owner;
		disabled = true;
		enterRank = ClanRank.ANYONE;
		talkRank = ClanRank.ANYONE;
		kickRank = ClanRank.OWNER;
	}

}
