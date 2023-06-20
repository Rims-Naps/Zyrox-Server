package com.zenyte.game.content.clans;

import com.google.common.eventbus.Subscribe;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.packet.out.ClanChannelFull;
import com.zenyte.game.packet.out.ClanChannelMember;
import com.zenyte.game.packet.out.MessageClanChannel;
import com.zenyte.game.packet.out.ResetClanChannel;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.ClanLeaveEvent;
import com.zenyte.plugins.events.LoginEvent;
import lombok.NonNull;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class that handles all I/O elements, as well as managing the clan itself.
 * 
 * @author Kris | 22. march 2018 : 23:51.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ClanManager implements ScheduledExternalizable {

	/**
	 * The directory to the file containing clans.
	 */
	public static final String CLANS_FILE_DIRECTORY = "data/clans.json";

	/**
	 * A map containing the username of the owner of the channel, and the channel object.
	 */
	public static final Map<String, ClanChannel> CLAN_CHANNELS = new HashMap<>();

	public static final Optional<ClanChannel> getChannel(@NotNull final String name) {
	    return Optional.ofNullable(CLAN_CHANNELS.get(name));
    }

	@Subscribe
	public static final void onLogin(@NotNull final LoginEvent event) {
		val player = event.getPlayer();
		val lastClan = player.getSettings().getChannelOwner();
		if (lastClan != null) {
			join(player, lastClan);
		}
	}

	/**
	 * Attempts to enqueue the player to the requested clan channel.
	 * 
	 * @param player
	 *            the player trying to join a channel.
	 * @param rq
	 *            the username of the owner of the channel.
	 */
	public static final void join(@NonNull final Player player, @NonNull final String rq) {
		val requestedName = rq.equalsIgnoreCase("help") ? "Zenyte" : rq;
		val name = Utils.formatUsername(requestedName);
		player.sendMessage("Attempting to join channel...");

		//Schedule on a task to properly introduce the "delay" that OSRS gets.
		WorldTasksManager.schedule(() -> {
            val optional = ClanManager.getChannel(name);
            if (!optional.isPresent()) {
                player.sendMessage("That player doesn't currently own a clan channel.");
                return;
            }
            val channel = optional.get();
            if (channel.isDisabled()) {
                player.sendMessage("The owner of that clan has disabled their clan channel.");
                return;
            }
            val time = channel.getBannedMembers().getLong(player.getUsername());
            if (!player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR) && (time > Utils.currentTimeMillis() || channel.getPermBannedMembers().contains(player.getUsername()))) {
                player.sendMessage("You are temporarily banned from this clan channel.");
                return;
            }
            val rank = getRank(player, channel);
            if (channel.getMembers().size() >= 2000) {
                val playerToKick = findMember(channel, rank);
                if (playerToKick == null) {
                    player.sendMessage("That clan channel is currently full.");
                    return;
                }
                kick(player, false, playerToKick, false);
            }
            channel.loadOwner(owner -> {
                if (!canEnter(player, owner, channel)) {
                    player.sendMessage("You are not high enough rank to join this clan channel.");
                    return;
                }
                channel.getMembers().add(player);
                refreshPartial(channel, player, true, true);
                player.sendMessage("Now talking in clan channel " + Utils.formatString(channel.getPrefix()));
                player.sendMessage("To talk, start each line of chat with the / symbol.");
                player.getSettings().setChannel(channel);
                player.getSettings().setChannelOwner(channel.getOwner());
            });
        });
	}

	public static final void permban(final Player player, final String username) {
	    val channel = ClanManager.getChannel(player.getUsername());
	    if (!channel.isPresent()) {
	        player.sendMessage("You do not own a clan channel.");
	        return;
        }
        CoresManager.getLoginManager().load(username, true, target -> {
            if (!target.isPresent()) {
                player.sendMessage("Account by the name of " + username + " does not exist.");
                return;
            }
            val ch = channel.get();
            val status = ch.getPermBannedMembers().add(username.replaceAll(" ", "_"));
            if (status) {
                player.sendMessage("Permanently banned user " + username + " from your clan channel.");
                val beingKicked = target.get();
                if (beingKicked.getSettings().getChannel() == ch) {
                    PluginManager.post(new ClanLeaveEvent(beingKicked));
                    beingKicked.getSettings().setChannel(null);
                    beingKicked.getSettings().setChannelOwner(null);
                    beingKicked.send(new ResetClanChannel());
                    beingKicked.sendMessage("You have been kicked from the channel.");
                }
                refreshPartial(channel.get(), beingKicked, false, true);
            } else {
                player.sendMessage("User " + username + " is already banned from your channel.");
            }
        });
    }

    public static final void permunban(final Player player, final String username) {
        val channel = ClanManager.getChannel(player.getUsername());
        if (!channel.isPresent()) {
            player.sendMessage("You do not own a clan channel.");
            return;
        }
        CoresManager.getLoginManager().load(username, true, target -> {
            if (!target.isPresent()) {
                player.sendMessage("Account by the name of " + username + " does not exist.");
                return;
            }
            val status = channel.get().getPermBannedMembers().remove(username.replaceAll(" ", "_"));
            if (status) {
                player.sendMessage("Unbanned user " + username + " from your clan channel.");
            } else {
                player.sendMessage("User " + username + " is not permanently banned from your channel.");
            }
        });
    }

	private static final Player findMember(final ClanChannel channel, final ClanRank clanRank) {
	    for (val member : channel.getMembers()) {
	        if (member.isNulled()) {
	            continue;
            }
	        val rank = getRank(member, channel);
	        if (rank.getKickId() < clanRank.getKickId()) {
	            return member;
            }
        }
        return null;
    }

	/**
	 * Attempts to remove the player from the channel. If the channel is null, returns. If the controller doesn't allow to remove - which is
	 * specifically in instances that require a clan - prevents from removing. If the channel doesn't contain the member, returns the code
	 * and doesn't do anything.
	 * 
	 * @param player
	 *            the player who's leaving the channel.
	 * @param resetLastChannel
	 *            whether to reset the last channel or not (used on login - auto join)
	 */
	public static final void leave(@NonNull final Player player, final boolean resetLastChannel) {
		final ClanChannel channel = player.getSettings().getChannel();
		if (channel == null) {
			return;
		}
		if (!player.getControllerManager().canLeaveClanChannel()) {
			return;
		}
		val members = channel.getMembers();
		if (!members.remove(player)) {
		    return;
        }
		if (members.isEmpty()) {
		    if (!channel.getBannedMembers().isEmpty()) {
                channel.getBannedMembers().clear();
            }
        }
        PluginManager.post(new ClanLeaveEvent(player));
		if (resetLastChannel) {
			player.getSettings().setChannel(null);
			player.getSettings().setChannelOwner(null);
		}
		player.send(new ResetClanChannel());
		player.sendMessage("You have left the channel.");
        refreshPartial(channel, player, false, true);
	}

	/**
	 * Attempts to kick the player from the current channel. If the player hasn't got the rights to kick, they'll be informed so.
	 *  @param player
	 *            the player attempting to kick.
	 * @param inform
	 *            whether to inform the kicked player that they have been kicked.
     * @param beingKicked
     * @param force
     */
	public static final void kick(@NonNull final Player player, final boolean inform,
                                  @NonNull final Player beingKicked, final boolean force) {
		final ClanChannel channel = player.getSettings().getChannel();
		if (channel == null || player.isNulled() || beingKicked.isNulled()) {
			return;
		}
		if (!force) {
            if (channel.getOwner().equals(beingKicked.getUsername())) {
                player.sendMessage("You can't kick the owner from the clan channel.");
                return;
            }
            if (!canKick(player, channel)) {
                player.sendMessage("You aren't high enough rank to kick from this channel.");
                return;
            }
            val rankA = getRank(player, channel);
            val rankB = getRank(beingKicked, channel);
            if (rankA.getKickId() <= rankB.getKickId()) {
                player.sendMessage("You can only kick members with a lower tier rank than you.");
                return;
            }
        }
		if (!channel.getMembers().remove(beingKicked)) {
			return;
		}
		channel.getBannedMembers().put(beingKicked.getUsername(),
                Utils.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        beingKicked.getVariables().setLastClanKick(System.currentTimeMillis());
        player.sendMessage("Your request to kick/ban this user was successful.");
        PluginManager.post(new ClanLeaveEvent(beingKicked));
		beingKicked.getSettings().setChannel(null);
		beingKicked.getSettings().setChannelOwner(null);
		beingKicked.send(new ResetClanChannel());
		if (inform) {
			beingKicked.sendMessage("You have been kicked from the channel.");
		}
        refreshPartial(channel, beingKicked, false, true);
	}

	/**
	 * Sends a message to all the members of the channel.
	 * 
	 * @param player
	 *            the player sending the message
	 * @param message
	 *            the message being sent.
	 */
	public static final void message(@NonNull final Player player, @NonNull final ChatMessage message) {
		val channel = player.getSettings().getChannel();
		if (channel == null) {
			player.sendMessage("You need to be in a clan channel to send a channel message.");
			return;
		}
		channel.loadOwner(owner -> {
            if (!canTalk(player, owner, channel)) {
                player.sendMessage("You aren't high enough rank to talk in this channel.");
                return;
            }
            val icon = player.getPrimaryIcon() | (player.getSecondaryIcon() << 5) | (player.getTertiaryIcon() << 10);
            for (val member : channel.getMembers()) {
                if (member == null || member.isNulled()) {
                    continue;
                }
                member.send(new MessageClanChannel(player, channel.getPrefix(), icon, message));
            }
        });
	}

	/**
	 * Gets the rank of the requested player in the requested channel. If it fails to obtain a rank, the rank {@link ClanRank#ANYONE} is
	 * returned.
	 * 
	 * @param player
	 *            the player whose rank to obtain.
	 * @param channel
	 *            the channel from which to obtain their rank.
	 * @return the rank of the player, or {@link ClanRank#ANYONE} is there's none.
	 */
	public static final ClanRank getRank(@NonNull final Player player, @NonNull final ClanChannel channel) {
		if (isOwner(player, channel)) {
			return ClanRank.OWNER;
		}
		final ClanRank rank = channel.getRankedMembers().get(player.getPlayerInformation().getUsername());
		if (rank == null) {
			return ClanRank.ANYONE;
		}
		return rank;
	}

	/**
	 * Gets the rank of the requested player in the players' current channel. If it fails to obtain a rank, the rank {@link ClanRank#ANYONE}
	 * is returned.
	 * 
	 * @param player
	 *            the player whose channel to check.
	 * @param username
	 *            the username of the player whose rank to check.
	 * @return the rank of the player, or {@link ClanRank#ANYONE} is there's none.
	 */
	public static final ClanRank getRank(@NonNull final Player player, @NonNull final String username) {
		val channel = ClanManager.getChannel(player.getUsername());
		if (!channel.isPresent()) {
			return ClanRank.ANYONE;
		}
		final ClanRank rank = channel.get().getRankedMembers().get(username);
		if (rank == null) {
			return ClanRank.ANYONE;
		}
		return rank;
	}

	/**
	 * Refreshes the channel settings and members list.
	 * 
	 * @param channel
	 *            the channel to refresh.
	 */
	public static final void refreshChannel(@NonNull final ClanChannel channel) {
	    channel.loadOwner(owner -> {
            val members = channel.getMembers();
            members.removeIf(member -> member.isNulled() || member.isFinished());
            val builder = new ClanChannelFullBuilder(channel, owner).build();
            members.forEach(member -> {
                try {
                    member.send(new ClanChannelFull(builder));
                } catch (Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            });
        });
	}

	public static final void refreshPartial(@NonNull final ClanChannel channel, final Player player,
                                            final boolean added, final boolean split) {
	    channel.loadOwner(owner -> {
            val members = channel.getMembers();
            members.removeIf(member -> member.isNulled() || member.isFinished());
            val builder = new ClanChannelMemberBuilder(player, owner, added, channel).build();
            for (val member : members) {
                if (split && member == player) {
                    continue;
                }
                if (member.isNulled()) {
                    continue;
                }
                member.send(new ClanChannelMember(builder));
            }
            if (split && added) {
                if (player.isNulled()) {
                    return;
                }
                player.send(new ClanChannelFull(new ClanChannelFullBuilder(channel, owner).build()));
            }
        });
    }

	public static final void setPrefix(@NonNull final Player player, final boolean active) {
		final ClanChannel channel = CLAN_CHANNELS.get(player.getPlayerInformation().getUsername());
		if (channel == null) {
			return;
		}
		if (active) {
			player.sendInputName("Enter chat prefix:", string -> {
				val prefix = string.replaceAll("[^a-zA-Z0-9 ]", "").trim();
			    if (prefix.length() == 0) {
			        player.sendMessage("Cannot set an empty prefix.");
			        return;
                }
				if (channel.isDisabled()) {
					player.sendMessage("Your clan channel has now been enabled!");
					player.sendMessage("Join your channel by clicking 'Join Chat' and typing: "
							+ TextUtils.capitalize(channel.getOwner().replace("_", " ")));
				}
				channel.setPrefix(prefix);
				channel.setDisabled(false);
				refreshChannel(channel);
				player.getPacketDispatcher().sendComponentText(94, 10, TextUtils.capitalize(prefix));
			});
		} else {
			channel.setDisabled(true);
			player.getPacketDispatcher().sendComponentText(94, 10, "Chat disabled");
			for (val p : channel.getMembers()) {
			    if (p.isNulled()) {
			        continue;
                }
				kick(player, true, p, true);
			}
		}
	}

	/**
	 * Checks whether the player is the owner of the channel or not.
	 * 
	 * @param player
	 *            the player to compare.
	 * @param channel
	 *            the channel to check.
	 * @return whether the player is owner.
	 */
	private static final boolean isOwner(@NonNull final Player player, @NonNull final ClanChannel channel) {
	    if (player.isNulled()) {
	        return false;
        }
		return channel.getOwner().equals(player.getPlayerInformation().getUsername());
	}

	/**
	 * Checks whether the requested player can enter the given channel or not. Administrators can join all clans. The player will be able to
	 * join if they're either the owner of the clan, the enter rank requirement is set to anyone, they're a friend of the owner of the
	 * clan(if requirement is friends), or their rank is eligible enough to join the channel.
	 * 
	 * Does not check for the current size of the clan.
	 * 
	 * @param player
	 *            the player to compare
	 * @param channel
	 *            the channel to check
	 * @return whether the player can enter that channel or not.
	 */
	private static final boolean canEnter(@NonNull final Player player,
                                          @NotNull final Player clanOwner, @NonNull final ClanChannel channel) {
	    if (player.isNulled() || clanOwner.isNulled()) {
	        return false;
        }
		final ClanRank rank = channel.getEnterRank();
		if (rank == ClanRank.ANYONE || isOwner(player, channel) || player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
			return true;
		}
		final String username = player.getPlayerInformation().getUsername();
		if (rank == ClanRank.FRIENDS) {
			return clanOwner.getSocialManager().containsFriend(username);
		}
		final ClanRank memberRank = channel.getRankedMembers().get(username);
		if (memberRank == null) {
			return false;
		}
		return memberRank.ordinal() >= rank.ordinal();
	}

	/**
	 * Checks whether the requested player can kick players from the channel or not. Administrators can always kick players. The player will
	 * be able to kick if they're either the owner of the clan or their rank is eligible enough to kick from the channel.
	 * 
	 * @param player
	 *            the player to compare
	 * @param channel
	 *            the channel to check
	 * @return whether the player can kick from that channel or not.
	 */
	public static final boolean canKick(@NonNull final Player player, @NonNull final ClanChannel channel) {
	    if (player.isNulled()) {
	        return false;
        }
		final ClanRank rank = channel.getKickRank();
		if (isOwner(player, channel) || player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
			return true;
		}
		final String username = player.getPlayerInformation().getUsername();
		final ClanRank memberRank = channel.getRankedMembers().get(username);
		if (memberRank == null) {
			return false;
		}
		return memberRank.ordinal() >= rank.ordinal();
	}

	/**
	 * Checks whether the requested player can talk in the given channel or not. Administrators can talk in all clans. The player will be
	 * able to talk if they're either the owner of the clan, the talk rank requirement is set to anyone, they're a friend of the owner of
	 * the clan(if requirement is friends), or their rank is eligible enough to talk in the channel.
	 * 
	 * @param player
	 *            the player attempting to talk.
	 * @param channel
	 *            the channel the player is attempting to talk in.
	 * @return whether the player can talk or not.
	 */
	private static final boolean canTalk(@NonNull final Player player,
                                         @NotNull final Player clanOwner, @NonNull final ClanChannel channel) {
        if (player.isNulled()) {
            return false;
        }
		final ClanRank rank = channel.getTalkRank();
		if (rank == ClanRank.ANYONE || isOwner(player, channel) || player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
			return true;
		}
		final String username = player.getPlayerInformation().getUsername();
		if (rank == ClanRank.FRIENDS) {
			return clanOwner.getSocialManager().containsFriend(username);
		}
		final ClanRank memberRank = channel.getRankedMembers().get(username);
		if (memberRank == null) {
			return false;
		}
		return memberRank.ordinal() >= rank.ordinal();
	}

	@Override
	public int writeInterval() {
		return 5;
	}

	@Override
	public void read(final BufferedReader reader) {
		val channels = gson.fromJson(reader, ClanChannel[].class);
		for (int i = channels.length - 1; i >= 0; i--) {
			val channel = channels[i];
			if (channel == null) {
				continue;
			}
			channel.setTransientVariables();
			CLAN_CHANNELS.put(channel.getOwner(), channel);
		}
	}

	@Override
	public void write() {
		out(gson.toJson(CLAN_CHANNELS.values()));
	}

	@Override
	public String path() {
		return CLANS_FILE_DIRECTORY;
	}

}
