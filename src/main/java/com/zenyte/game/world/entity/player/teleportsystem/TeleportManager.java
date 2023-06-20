package com.zenyte.game.world.entity.player.teleportsystem;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.processor.Listener;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Tommeh | 13-11-2018 | 17:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@Setter
public class TeleportManager {

    private static final int INTERFACE = 700;
    private static final Graphics TELEPORT_GFX = new Graphics(343);
    private static final Animation TELEPORT_ANIM = new Animation(1818);
    private static final Location WIZARD_LOCATION = new Location(3088, 3505, 0);

    private transient Player player;
    private List<PortalTeleport> favoriteTeleports;
    private Set<PortalTeleport> unlockedTeleports;
    private TeleportCategory lastCategory = TeleportCategory.VALUES[0];
    private PortalTeleport lastTeleport;

    public TeleportManager(final Player player) {
        this.player = player;
        favoriteTeleports = new ArrayList<>(5);
        unlockedTeleports = new ObjectOpenHashSet<>();
    }

    @Listener(type = Listener.ListenerType.LOBBY_CLOSE)
    private static void onLogin(final Player player) {
        val manager = player.getTeleportManager();
        val favoriteTeleports = manager.getFavoriteTeleports();
        if (!player.getBooleanAttribute("favorite_clear_for_donator_perk")) {
            favoriteTeleports.clear();
            player.addAttribute("favorite_clear_for_donator_perk", 1);
        }
        favoriteTeleports.removeIf(Objects::isNull);
        favoriteTeleports.sort(Comparator.comparingInt(PortalTeleport::ordinal));
    }

    public void initialize(final TeleportManager manager) {
        if (manager == null) {
            return;
        }
        favoriteTeleports = manager.favoriteTeleports;
        unlockedTeleports.addAll(manager.unlockedTeleports);
        lastTeleport = manager.lastTeleport;
        lastCategory = manager.lastCategory;
    }

    public void teleportTo(@NonNull final PortalTeleport teleport) {
        val level = WildernessArea.getWildernessLevel(teleport.getDestination());
        if (teleport.getCategory().equals(TeleportCategory.WILDERNESS) && level.isPresent()) {
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    plain("You're about to teleport into the Wilderness (level " + level.getAsInt() + ").");
                    options("Are you sure you want to do this?", "Yes.", "No.")
                            .onOptionOne(() -> {
                                teleport.teleport(player);
                                player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
                                lastTeleport = teleport;
                            });
                }
            });
            return;
        }
        teleport.teleport(player);
        player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
        lastTeleport = teleport;
    }

    public void populateCategories(@NonNull final TeleportCategory selectedCategory, final boolean fullRefresh) {
        for (int index = 0; index < TeleportCategory.VALUES.length; index++) {
            val category = TeleportCategory.get(index);
            player.getPacketDispatcher().sendClientScript(10001, index, category.toString(), selectedCategory.equals(category) ? 1 : 0);
        }
        if (fullRefresh) {
            player.getPacketDispatcher().sendClientScript(10003, TeleportCategory.VALUES.length);
        }
    }

    public void populateSkillingCategories() {

        val manager = player.getTeleportManager();
        player.getInterfaceHandler().closeInterface(GameInterface.TELEPORT_MENU);
        player.getInterfaceHandler().sendInterface(GameInterface.TELEPORT_MENU);
        player.getPacketDispatcher().sendComponentSettings(GameInterface.TELEPORT_MENU, 4, 0, 12, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(GameInterface.TELEPORT_MENU,
                11, 0, 4 * 20, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(GameInterface.TELEPORT_MENU, 17, 0,
                4 * 20
                , AccessMask.CLICK_OP1);
        for (int index = 0; index < TeleportCategory.SKILLING_C.length; index++) {
            val category = TeleportCategory.SKILLING_C[index];
            player.getPacketDispatcher().sendClientScript(10001, index, category.toString(), index == 0 ? 1 : 0);
        }
        player.getPacketDispatcher().sendClientScript(10003, TeleportCategory.SKILLING_C.length);
        manager.populateTeleports(TeleportCategory.AGILITY, false);
        manager.populateFavorites();
    }

    public void populateTeleports(@NonNull final TeleportCategory category, final boolean fullRefresh) {
        val teleports = PortalTeleport.get(category);
        if (teleports == null) {
            return;
        }
        val sorted = new ArrayList<PortalTeleport>(teleports);
        sorted.sort((o1, o2) -> {
            val comp = Boolean.compare(isUnlocked(o2), isUnlocked(o1));
            if (comp != 0) {
                return comp;
            }

            String x1 = o1.toString();
            String x2 = o2.toString();
            return x1.compareTo(x2);
        });
        for (int index = 0; index < sorted.size(); index++) {
            val teleport = sorted.get(index);
            player.getPacketDispatcher().sendClientScript(10004, index, teleport.toString(),
                    teleport.getSmallDescription(),
                    teleport.getLargeDescription(),
                    !isUnlocked(teleport) ? 1 :
                            favoriteTeleports.contains(teleport) ? 2 : 0, -1, -1, -1, -1);
        }
        if (fullRefresh) {
            player.getPacketDispatcher().sendClientScript(10006, sorted.size());
        }
    }

    public void populateFavorites() {
        val builder = new StringBuilder();
        for (val teleport : favoriteTeleports) {
            builder.append(teleport).append("|");
        }
        player.getPacketDispatcher().sendClientScript(10008);
        player.getPacketDispatcher().sendClientScript(10007, builder.toString());
        player.getPacketDispatcher().sendClientScript(10009);
    }

    public void toggleFavorite(@NonNull final PortalTeleport teleport) {
        if (favoriteTeleports.contains(teleport)) {
            favoriteTeleports.remove(teleport);
        } else {
            if (!isUnlocked(teleport)) {
                player.sendMessage("You haven't unlocked this teleport yet.");
                return;
            }
            if (favoriteTeleports.size() >= getMaximumFavorites()) {
                player.sendMessage("You've already used up all of the favorite spots.");
                return;
            }
            favoriteTeleports.add(teleport);
        }
        player.getPacketDispatcher().sendClientScript(10008);
        populateFavorites();
        populateTeleports(lastCategory, false);
    }

    private int getMaximumFavorites() {
        if (player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 5;
        } else if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 4;
        }
        return 3;
    }

    public boolean isUnlocked(@NotNull final PortalTeleport teleport) {
        val type = teleport.getUnlockType();
        if (type == UnlockType.DEFAULT) {
            return true;
        }
        if (type == UnlockType.VISIT) {
            return player.getSkillingXPRate() <= 10 || unlockedTeleports.contains(teleport);
        }
        return unlockedTeleports.contains(teleport);
    }

    public void unlock(@NonNull final PortalTeleport teleport) {
        if (isUnlocked(teleport)) {
            return;
        }
        if (unlockedTeleports.add(teleport)) {
            player.sendMessage(Colour.RS_GREEN.wrap("You've unlocked a new teleport: " + teleport.getSmallDescription()));
        }
    }
}
