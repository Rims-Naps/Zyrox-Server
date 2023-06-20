package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.packet.out.MessagePrivate;
import com.zenyte.game.packet.out.MessagePrivateEcho;
import com.zenyte.game.packet.out.UpdateFriendList;
import com.zenyte.game.packet.out.UpdateIgnoreList;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.ChatMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Tommeh | 2 dec. 2017 : 22:56:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class SocialManager {
    
    private static final int MAX_FRIENDS_COUNT = 200, MAX_IGNORES_COUNT = 100;
    private static int messageCounter = (int) System.currentTimeMillis();
    private final transient Player player;
    @Expose
    @Getter
    @Setter
    private List<String> friends;
    @Expose
    @Getter
    @Setter
    private List<String> ignores;
    
    
    SocialManager(final Player player) {
        this.player = player;
        friends = new ObjectArrayList<>();
        ignores = new ObjectArrayList<>();
    }
    
    public final void initalize(final SocialManager manager) {
        friends = manager.friends;
        ignores = manager.ignores;
    }
    
    void loadFriends() {
        player.getPacketDispatcher().initFriendsList();
    }
    
    void loadIgnores() {
        player.getPacketDispatcher().initIgnoreList();
    }
    
    public boolean isOffline() {
        return player.getTemporaryAttributes().containsKey("private_status_offline");
    }
    
    public void updateStatus() {
        val privateStatus = SocialManager.PrivateStatus.fromId(player.getSettings().valueOf(Setting.PRIVATE_FILTER));
        
        if (privateStatus.equals(PrivateStatus.OFF)) {
            player.putBooleanTemporaryAttribute("private_status_offline", true);
        } else {
            player.getTemporaryAttributes().remove("private_status_offline");
        }
    
        val username = player.getPlayerInformation().getUsername();
        val entry = new UpdateFriendList.FriendEntry(username, false);
        val list = Collections.singletonList(entry);
        for (val p : World.getPlayers()) {
            if (p == null || p == player)
                continue;
            if (p.getSocialManager().containsFriend(username)) {
                p.send(new UpdateFriendList(p, list));
            }
        }
    
        if (player.getPrivilege().eligibleTo(Privilege.FORUM_MODERATOR)) {
            GameNoticeboardInterface.refreshCounters(true);
        }
    }
    
    public void addFriend(@NotNull final String name) {
        CoresManager.getLoginManager().load(name, true, optional -> {
            if (!optional.isPresent()) {
                player.sendMessage("Could not find player.");
                return;
            }
            val target = optional.get();
            val formattedUsername = target.getUsername();
            if (friends.size() >= MAX_FRIENDS_COUNT) {
                player.sendMessage("Your friend list is currently full.");
                return;
            }
            if (friends.contains(formattedUsername)) {
                player.sendMessage(target.getName() + " is already on your friend list.");
                return;
            }
            friends.add(formattedUsername);
            refreshForSinglePlayer(player, formattedUsername, true);
    
            val loggedInPlayer = World.getPlayer(formattedUsername);
            loggedInPlayer.ifPresent(otherPlayer -> {
                refreshForSinglePlayer(otherPlayer, player.getUsername(), false);
        
                val opChannel = ClanManager.getChannel(player.getUsername());
                opChannel.ifPresent(channel -> {
                    if (channel.getMembers().contains(otherPlayer)) {
                        ClanManager.refreshPartial(channel, otherPlayer, true, false);
                    }
                });
            });

        });
    }
    
    public boolean isVisible(final String username) {
        return World.getPlayer(username).filter(this::isVisible).isPresent();
    }
    
    public boolean isVisible(final Player otherPlayer) {
        if (otherPlayer == null) {
            return false;
        }
        val privateStatus = SocialManager.PrivateStatus.fromId(otherPlayer.getSettings().valueOf(Setting.PRIVATE_FILTER));
        if (privateStatus.equals(SocialManager.PrivateStatus.OFF)) {
            return false;
        } else
            return !privateStatus.equals(PrivateStatus.FRIENDS) || otherPlayer.getSocialManager().containsFriend(player.getUsername());
    }
    
    public PrivateStatus getStatus() {
        return SocialManager.PrivateStatus.fromId(player.getSettings().valueOf(Setting.PRIVATE_FILTER));
    }
    
    private void refreshForSinglePlayer(final Player playerToRefresh, final String userToRefresh, final boolean added) {
        if (!playerToRefresh.getSocialManager().containsFriend(userToRefresh)) {
            return;
        }
        val entry = new UpdateFriendList.FriendEntry(userToRefresh, added);
        val list = Collections.singletonList(entry);
        playerToRefresh.send(new UpdateFriendList(playerToRefresh, list));
    }
    
    public void removeFriend(@NotNull final String requestedName) {
        val name = Utils.formatUsername(requestedName);
        if (!friends.remove(name)) {
            return;
        }
        World.getPlayer(requestedName).ifPresent(target -> {
            refreshForSinglePlayer(target, player.getUsername(), false);
            ClanManager.getChannel(player.getUsername())
                    .ifPresent(channel -> {
                        if (channel.getMembers().contains(target)) {
                            ClanManager.refreshPartial(channel, target, true, false);
                        }
                    });
        });
    }
    
    public void addIgnore(@NotNull final String requestedName) {
        if (ignores.size() >= MAX_IGNORES_COUNT) {
            player.sendMessage("Your ignore list is currently full.");
            return;
        }
        val name = Utils.formatUsername(requestedName);
        if (containsFriend(name)) {
            player.sendMessage("Remove them from your friend list first!");
            return;
        }
        if (containsIgnore(name)) {
            player.sendMessage(Utils.formatString(name) + " is already on your ignore list.");
            return;
        }
        CoresManager.getLoginManager().load(name, true, optional -> {
            if (!optional.isPresent()) {
                player.sendMessage("Player could not be found.");
                return;
            }
            
            val target = optional.get();
            ignores.add(name);
            val entry = new UpdateIgnoreList.IgnoreEntry(target.getUsername(), true);
            val list = Collections.singletonList(entry);
            player.send(new UpdateIgnoreList(list));
        });
    }
    
    public void removeIgnore(@NotNull final String requestedName) {
        ignores.remove(Utils.formatUsername(requestedName));
    }
    
    public void sendMessage(@NotNull final String name, @NotNull final ChatMessage message) {
        val friend = World.getPlayer(name);
        if (friend.isPresent() && isVisible(friend.get())) {
            if (isOffline()) {
                updateStatus(); // if the client sets private to Friends from Off
            }
            
            val icon = player.getPrimaryIcon() | (player.getSecondaryIcon() << 5) | (player.getTertiaryIcon() << 10);
            val friendPlayer = friend.get();
            player.send(new MessagePrivateEcho(friendPlayer.getPlayerInformation().getDisplayname(), message));
            friendPlayer.send(new MessagePrivate(player.getPlayerInformation().getDisplayname(), message, icon));
        } else {
            player.sendMessage("That player is currently offline.");
        }
    }
    
    public boolean containsFriend(final String name) {
        return friends.contains(name);
    }
    
    boolean containsIgnore(final String name) {
        return ignores.contains(name);
    }
    
    public int getNextUniqueId() {
        return messageCounter++;
    }
    
    @Getter
    @AllArgsConstructor
    public enum PrivateStatus {
        ALL(0),
        FRIENDS(1),
        OFF(2);
        
        private static final PrivateStatus[] values = values();
        private static final Int2ObjectOpenHashMap<PrivateStatus> byIds = new Int2ObjectOpenHashMap<>(values.length);
        
        static {
            for (PrivateStatus status : values) {
                byIds.put(status.getId(), status);
            }
        }
        
        private final int id;
        
        public static PrivateStatus fromId(final int option) {
            return byIds.get(option);
        }
    }
    
}