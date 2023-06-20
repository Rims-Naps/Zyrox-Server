package com.zenyte.game.ui.testinterfaces;

import com.google.common.eventbus.Subscribe;
import com.zenyte.Constants;
import com.zenyte.GameEngine;
import com.zenyte.game.BonusCoxManager;
import com.zenyte.game.BonusTobManager;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.GameClock;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.content.event.DoubleDropsManager;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.*;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import com.zenyte.plugins.events.LoginEvent;
import com.zenyte.plugins.events.LogoutEvent;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 2-12-2018 | 16:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GameNoticeboardInterface extends Interface {
    
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final Privilege[] STAFF = {
            Privilege.SPAWN_ADMINISTRATOR, Privilege.ADMINISTRATOR, Privilege.GLOBAL_MODERATOR, Privilege.MODERATOR, Privilege.FORUM_MODERATOR, Privilege.JUNIOR_MODERATOR
    };
    
    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        val p = event.getPlayer();
        p.getPacketDispatcher().sendClientScript(3501, 162 << 16 | 2, 701 << 16 | 11, 701 << 16 | 16, 701 << 16 | 31, 701 << 16 | 32, 701 << 16 | 33, 701 << 16 | 43);
        p.getVarManager().sendVar(3500, (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - GameEngine.SERVER_START_TIME));
        p.getVarManager().sendVar(3501, (int) (p.getVariables().getPlayTime() * 0.6));
        p.getVarManager().sendVar(3506, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusXpManager.expirationDate - System.currentTimeMillis())));
        p.getVarManager().sendVar(3507, (int) (p.getVariables().getRaidsBoost() * 0.6F));
        p.getVarManager().sendVar(3801, (int) (p.getVariables().getBonusXP() * 0.6F));
        p.getVarManager().sendVar(3804, Constants.BOOSTED_COX ? (int) TimeUnit.MILLISECONDS.toSeconds(BonusCoxManager.expirationDateCox - System.currentTimeMillis()) : 0);
        p.getVarManager().sendVar(3510, (int) (p.getVariables().getTobBoost() * 0.6F));
        p.getVarManager().sendVar(3805, Constants.BOOSTED_TOB ? (int) TimeUnit.MILLISECONDS.toSeconds(BonusTobManager.expirationDateTob - System.currentTimeMillis()) : 0);
        refreshCounters(true);
        
        val optionalPlugin = GameInterface.GAME_NOTICEBOARD.getPlugin();
        if (optionalPlugin.isPresent()) {
            val plugin = optionalPlugin.get();
            p.getPacketDispatcher().sendComponentText(plugin.getInterface(), plugin.getComponent("Time"), "Time: <col=ffffff>" + GameClock.gameTime());
        }
    }
    
    public static final void refreshXericsWisdom(@NotNull final Player player) {
        player.getVarManager().sendVar(3507, (int) (player.getVariables().getRaidsBoost() * 0.6F));
    }

    public static final void refreshVerziksWill(@NotNull final Player player) {
        player.getVarManager().sendVar(3510, (int) (player.getVariables().getTobBoost() * 0.6F));
    }

    public static final void refreshBonusCox() {
        for (val player : World.getPlayers()) {
            player.getVarManager().sendVar(3804, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusCoxManager.expirationDateCox - System.currentTimeMillis())));
        }
    }

    public static final void refreshBonusTob() {
        for (val player : World.getPlayers()) {
            player.getVarManager().sendVar(3805, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusTobManager.expirationDateTob - System.currentTimeMillis())));
        }
    }

    public static final void refreshBonusXP() {
        for (val player : World.getPlayers()) {
            player.getVarManager().sendVar(3506, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusXpManager.expirationDate - System.currentTimeMillis())));
        }
    }
    
    public static final void refreshCounters(final boolean all) {
        val players = World.getPlayers();
        val total = players.size();
        val staff = new MutableInt();
        val mobile = new MutableInt();
        val wilderness = new MutableInt();
        for (val player : players) {
            if (all) {
                if (player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR) && player.getSocialManager().getStatus().equals(SocialManager.PrivateStatus.ALL)) {
                    staff.increment();
                }
                if (player.getPlayerInformation().getDevice() == Device.MOBILE) {
                    mobile.increment();
                }
            }
            if (player.getVarManager().getBitValue(WildernessArea.IN_WILDERNESS_VARBIT_SPECIAL_UNCLICKABLE) == 1) {
                wilderness.increment();
            }
        }
        for (val player : players) {
            if (all) {
                player.getVarManager().sendVar(3502, total);
                player.getVarManager().sendVar(3503, staff.intValue());
                player.getVarManager().sendVar(3508, mobile.intValue());
            }
            player.getVarManager().sendVar(3509, wilderness.intValue());
        }
    }
    
    @Subscribe
    public static final void onLogout(final LogoutEvent event) {
        refreshCounters(true);
    }
    
    private static List<Player> getStaff(final Player requester, final Privilege privilege) {
        return World.getPlayers().stream()
                       .filter(p -> p.getPrivilege().equals(privilege)
                                            && (!isHidden(p)
                                                        || requester.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)))
                       .collect(Collectors.toList());
    }

    public enum StaffStatus {
        NOT_STAFF,
        PUBLIC,
        NOT_PUBLIC
    }

    public static final StaffStatus getStaffStatus(final Player player) {
        if (!player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)) {
            return StaffStatus.NOT_STAFF;
        }

        return player.getSocialManager().getStatus().equals(SocialManager.PrivateStatus.ALL) ? StaffStatus.PUBLIC : StaffStatus.NOT_PUBLIC;
    }
    
    public static void showStaffOnline(final Player player) {
        val lines = new ArrayList<String>();
        int count = 0;
        for (val privilege : STAFF) {
            val members = getStaff(player, privilege);
            count += members.size();
            lines.add(privilege.getCrown() + " <col=00080>" + privilege + (privilege == Privilege.SPAWN_ADMINISTRATOR ? "" : "s") + "</col>");
            if (members.isEmpty()) {
                lines.add("- Nobody");
            } else {
                members.forEach(p -> lines.add(p.getName() + (isHidden(p) ? " (" + Colour.MAROON.wrap("Hidden") + ")" : "")));
            }
            lines.add("\n");
        }
        Diary.sendJournal(player, "Staff online: " + count, lines);
    }

    public static void showPlayerList(final Player p) {
        if(!p.getPrivilege().eligibleTo(Privilege.GLOBAL_MODERATOR)) {
            return;
        }
        val entries = new ArrayList<String>();

        val players = new ArrayList<Player>(World.getPlayers().size());
        players.addAll(World.getPlayers());
        players.sort((a, b) -> a.getPlayerInformation().getDisplayname().compareToIgnoreCase(b.getPlayerInformation().getDisplayname()));
        int ij = 0;
        lSimLargeCount:
        for (final Player player : players) {
            if (player == null) {
                continue;
            }

            val sb = new StringBuilder(player.getPrivilege().getCrown())
                    .append(player.getPlayerInformation().getDisplayname());

            val area = player.getArea();
            sb.append(" - ")
                    .append(area != null ? area.name() : "Unknown");

            if (p.getPrivilege().eligibleTo(Privilege.GLOBAL_MODERATOR)) {
                sb.append(" - (").append(player.getX()).append(", ").append(player.getY()).append(", ").append(player.getPlane()).append(")");
            }

            if (player.isOnMobile()) {
                sb.append(" - Mobile");
            }

            entries.add(sb.toString().trim());
        }
        if(p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
            p.getDialogueManager().start(new OptionsMenuD(p, "Players online: " + World.getPlayers().size(), entries.toArray(new String[0])) {
                @Override
                public void handleClick(int slotId) {
                    if(player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
                        Player p = players.get(slotId);
                        WorldTasksManager.schedule(() -> {
                            if(!player.isHidden()) {
                                player.getAppearance().setInvisible(true);
                                player.setHidden(true);
                                player.setMaximumTolerance(true);
                                player.sendMessage(Colour.RED.wrap("You are now hidden."));
                            }
                            player.setLocation(p.getLocation());
                        });
                    }
                }

                @Override
                public boolean cancelOption() {
                    return true;
                }
            });
        } else {
            Diary.sendJournal(p, "Players online: " + World.getPlayers().size(), entries);
        }
    }

    public static void showDoubleDropsList(final Player p) {
        ArrayList<String> statuses = new ArrayList<String>();
        DoubleDropsManager.DOUBLE_DROPS.forEach((id, doubled) -> {
            statuses.add(id  + " (" + ItemDefinitions.get(id).getName() + ") : " + (doubled ? Colour.RS_GREEN.wrap("doubled") : Colour.RED.wrap("not doubled")));
        });
        Diary.sendJournal(p, "Double drops: " + DoubleDropsManager.DOUBLE_DROPS.size(), statuses);
    }
    
    private static boolean isHidden(final Player player) {
        return !player.getSocialManager().getStatus().equals(SocialManager.PrivateStatus.ALL);
    }
    
    @Override
    protected void attach() {
        put(8, "Players online");
        put(9, "Staff online");
        put(10, "Wilderness players");
        put(11, "Up-time");
        put(12, "Time");
        put(14, "2FA");
        put(15, "XP rate");
        put(16, "Time played");
        put(17, "Register date");
        put(18, "Privilege");
        put(19, "Game Mode");
        put(20, "Member Rank");
        put(21, "Loyalty points");
        put(22, "Total donated");
        put(23, "Vote credits");
        put(25, "Game Settings");
        put(27, "Drop Viewer");
        put(29, "Daily Challenges");
        put(30, "Boosts");
        put(31, "Bonus XP");
        put(32, "CoX Boost");
        put(36, "Website");
        put(38, "Forums");
        put(40, "Discord");
        put(42, "Store");
    }
    
    @Override
    public void open(Player player) {
        val time = player.getNumericAttribute("forum registration date").longValue();
        val date = FORMATTER.format(Instant.ofEpochMilli(time == 0 ? System.currentTimeMillis() : time).atZone(ZoneId.systemDefault()).toLocalDate());
        val yearIndex = !date.contains("201") ? date.indexOf("202") : date.indexOf("201");
        val formatted = date.substring(3, yearIndex - 1) + " " + Utils.suffixOrdinal(Integer.parseInt(date.substring(0, 2))) + " " + date.substring(yearIndex, yearIndex + 4);
        
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Time"), "Time: <col=ffffff>" + GameClock.gameTime());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("2FA"), "Two-Factor Authentication");
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("XP rate"), "XP: <col=ffffff>" + ((player.getSkillingXPRate() == 1) ? "-" : (player.getCombatXPRate() + "x Combat & " + player.getSkillingXPRate() + "x Skilling</col>")));
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Register date"), "Registered on: <col=ffffff>" + formatted + "</col>");
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Privilege"), "Privilege: <col=ffffff>" + player.getPrivilege().getCrown() + player.getPrivilege().toString() + "</col>");
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Game Mode"), "Mode: <col=ffffff>" + player.getGameMode().getCrown() + player.getGameMode().toString() + "</col>");
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Member Rank"), "Member: <col=ffffff>" + player.getMemberRank().getCrown() + player.getMemberRank().toString().replace(" Member", "") + "</col>");
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Loyalty points"), "Loyalty points: <col=ffffff>" + player.getLoyaltyManager().getLoyaltyPoints() + "</col>");
        val totalDonated = "Total donated: <col=ffffff>$" + player.getNumericAttribute("total donated online").doubleValue() + "</col>";
        player.log(LogLevel.INFO, totalDonated);
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Total donated"), totalDonated);
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Vote credits"), "Vote credits: <col=ffffff>" +
                                                                                                             player.getNumericAttribute("vote_points").intValue() + "</col>");
        
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("2FA"), -1, 0, AccessMask.CLICK_OP1);
    }
    
    @Override
    protected void build() {
        bind("Staff online", GameNoticeboardInterface::showStaffOnline);
        bind("Game Settings", GameInterface.GAME_SETTINGS::open);
        bind("Drop Viewer", GameInterface.DROP_VIEWER::open);
        bind("Daily Challenges", GameInterface.DAILY_CHALLENGES_OVERVIEW::open);
        bind("Boosts", GameNoticeboardInterface::showDoubleDropsList);
        bind("2FA", player -> player.getPacketDispatcher().sendURL("https://forums.zenyte.com/topic/362-two-factor-authentication-guide/"));
        bind("Website", player -> player.getPacketDispatcher().sendURL("https://zenyte.com/"));
        bind("Forums", player -> player.getPacketDispatcher().sendURL("https://zenyte.com/community/"));
        bind("Discord", player -> player.getPacketDispatcher().sendURL("https://zenyte.com/discord/"));
        bind("Store", player -> player.getPacketDispatcher().sendURL("https://zenyte.com/store/"));
    }
    
    @Override
    public GameInterface getInterface() {
        return GameInterface.GAME_NOTICEBOARD;
    }
}
