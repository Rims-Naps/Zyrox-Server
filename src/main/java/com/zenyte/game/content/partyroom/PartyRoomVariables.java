package com.zenyte.game.content.partyroom;

import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Kris | 03/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter @Setter
public class PartyRoomVariables {

    /**
     * Whether or not the entire party room functionality is disabled for all.
     */
    private boolean disabled;

    /**
     * Whether or not the NPCs across game should announce about party room events.
     */
    private boolean announcements;

    /**
     * Whether or not the doors to enter the party room are closeable.
     */
    private boolean doorsCloseable;

    /**
     * Whether or not the chest to deposit items into is closeable.
     */
    private boolean chestCloseable;

    /**
     * The cap how many balloons can be queued up at a time.
     */
    private int balloonQuantityCap;

    private int announcementFrequency;

    /**
     * The minimum value an item stack can have to be depositable into the chest. By default, there is no limit.
     */
    private int minimumItemStackValueThreshold;

    /**
     * The minimum player privilege to pull the lever and drop the balloons.
     */
    private Privilege minimumPrivilegeToDropBalloons;

    /**
     * The minimum player privilege to perform the Nightly Dance.
     */
    private Privilege minimumPrivilegeToPerformDance;

    /**
     * The minimum player privilege to deposit items into the party chest.
     */
    private Privilege minimumPrivilegeToDepositItems;

    /**
     * A map that pairs the countdown speed constants with a tick value. Each speed that is faster than the last must have a higher value than the last.
     */
    private final Map<PartyRoomSpeed, Integer> countdownTickSpeedMap = new EnumMap<>(PartyRoomSpeed.class);

    /**
     * A map that pairs the countdown speed constants with the chest value thresholds for each of the speeds to be met.
     */
    private final Map<PartyRoomSpeed, Integer> countdownThresholdMap = new EnumMap<>(PartyRoomSpeed.class);

    /**
     * A map that pairs the announcement speed constants with a tick value. Each speed that is faster than the last must have a higher value than the last.
     */
    private final Map<PartyRoomSpeed, Integer> announcementTickSpeedMap = new EnumMap<>(PartyRoomSpeed.class);

    /**
     * A map that pairs the announcement speed constants with the chest value thresholds for each of the speeds to be met.
     */
    private final Map<PartyRoomSpeed, Integer> announcementThresholdMap = new EnumMap<>(PartyRoomSpeed.class);

    /**
     * The current countdown timer of the party room. This will be set once the lever is pulled for the balloons to drop.
     */
    private int countdown;

    /**
     * The current queued balloons quantity which will be deposited.
     */
    private int queuedBalloonsQuantity;

    public void setQueuedBalloonsQuantity(final int amount) {
        this.queuedBalloonsQuantity = Math.max(0, Math.min(balloonQuantityCap, amount));
    }

    public void pull() {
        val room = FaladorPartyRoom.getPartyRoom();
        val currentQueuedBalloons = getQueuedBalloonsQuantity();
        setQueuedBalloonsQuantity(getQueuedBalloonsQuantity() + 200);
        if (getCountdown() <= 0) {
            setCountdown(getCountdownSpeed(room.calculateChestValue()).orElseThrow(RuntimeException::new));
        }
        room.rebuildAnnouncementsList();
        if (currentQueuedBalloons <= 0) {
            room.setPartyPeteCountdown(true);
        }
    }

    public OptionalInt getAnnouncementSpeed(final long value) {
        for (val speed : PartyRoomSpeed.values) {
            val threshold = announcementThresholdMap.get(speed);
            if (value >= threshold) {
                return OptionalInt.of(announcementTickSpeedMap.get(speed));
            }
        }
        return OptionalInt.empty();
    }

    public OptionalInt getCountdownSpeed(final long value) {
        for (int i = PartyRoomSpeed.values.size() - 1; i >= 0; i--) {
            val speed = PartyRoomSpeed.values.get(i);
            val threshold = countdownThresholdMap.get(speed);
            if (value >= threshold) {
                return OptionalInt.of(countdownTickSpeedMap.get(speed));
            }
        }
        return OptionalInt.of(countdownTickSpeedMap.get(PartyRoomSpeed.FAST));
    }

    @AllArgsConstructor
    private enum PartyRoomSpeed {
        FAST(10, 0, 30, 500_000_000),
        MEDIUM(100, 500_000, 50, 250_000_000),
        SLOW(500, 1_500_000, 100, 100_000_000),
        VERY_SLOW(1000, 10_000_000, 150, 10_000_000);

        private final int delay;
        private final int threshold;
        private final int announcementDelay;
        private final int announcementThreshold;

        private static final List<PartyRoomSpeed> values = ObjectLists.unmodifiable(new ObjectArrayList<>(values()));
    }

    PartyRoomVariables() {
        setDefaults();
    }

    private void setDefaults() {
        this.disabled = false;
        this.announcements = true;
        this.doorsCloseable = true;
        this.chestCloseable = true;
        this.balloonQuantityCap = 2000;
        this.minimumItemStackValueThreshold = 0;
        this.minimumPrivilegeToDropBalloons = Privilege.PLAYER;
        this.minimumPrivilegeToPerformDance = Privilege.PLAYER;
        this.minimumPrivilegeToDepositItems = Privilege.PLAYER;
        for (val speed : PartyRoomSpeed.values) {
            this.countdownTickSpeedMap.put(speed, speed.delay);
            this.countdownThresholdMap.put(speed, speed.threshold);
            this.announcementTickSpeedMap.put(speed, speed.announcementDelay);
            this.announcementThresholdMap.put(speed, speed.announcementThreshold);
        }
    }

    /**
     * The default variables which we do not change, used to obtain the default values.
     */
    private static final PartyRoomVariables defaultVariables = new PartyRoomVariables();

    private static final String[] buildMenuArray() {
        val variables = FaladorPartyRoom.getPartyRoom().getVariables();
        return new String[] {
                "Status: " + (variables.disabled ? Colour.RED : Colour.RS_GREEN).wrap(variables.disabled ? "Disabled" : "Enabled"),
                "Currently queued balloons: " + (variables.queuedBalloonsQuantity <= 0 ? Colour.RED : Colour.RS_GREEN).wrap(Integer.toString(variables.queuedBalloonsQuantity)),
                "Currently countdown: " + (variables.countdown <= 0 ? Colour.RED : Colour.RS_GREEN).wrap(Integer.toString(variables.countdown)),
                "Global NPC announcements: " + (!variables.announcements ? Colour.RED : Colour.RS_GREEN).wrap(variables.announcements ? "Enabled" : "Disabled"),
                "Entrance doors closeable: " + (!variables.doorsCloseable ? Colour.RED : Colour.RS_GREEN).wrap(variables.doorsCloseable ? "Enabled" : "Disabled"),
                "Party chest closeable: " + (!variables.chestCloseable ? Colour.RED : Colour.RS_GREEN).wrap(variables.chestCloseable ? "Enabled" : "Disabled"),
                "Maximum queueable balloons amount: " + Colour.RS_GREEN.wrap(variables.balloonQuantityCap + " (Default: " + defaultVariables.balloonQuantityCap + ")"),
                "Minimum item stack value to deposit: " + Colour.RS_GREEN.wrap(variables.minimumItemStackValueThreshold + " (Default: " + defaultVariables.minimumItemStackValueThreshold + ")"),
                "Minimum privilege to Balloon Bonanza: " + Colour.RS_GREEN.wrap(variables.minimumPrivilegeToDropBalloons + " (Default: " + defaultVariables.minimumPrivilegeToDropBalloons + ")"),
                "Minimum privilege to Nightly Dance: " + Colour.RS_GREEN.wrap(variables.minimumPrivilegeToPerformDance + " (Default: " + defaultVariables.minimumPrivilegeToPerformDance + ")"),
                "Minimum privilege to deposit items: " + Colour.RS_GREEN.wrap(variables.minimumPrivilegeToDepositItems + " (Default: " + defaultVariables.minimumPrivilegeToDepositItems + ")"),

                "Fast balloon drop speed: " + Colour.RS_GREEN.wrap(variables.countdownTickSpeedMap.get(PartyRoomSpeed.FAST) + " ticks (Default: " + defaultVariables.countdownTickSpeedMap.get(PartyRoomSpeed.FAST) + ")"),
                "Medium balloon drop speed: " + Colour.RS_GREEN.wrap(variables.countdownTickSpeedMap.get(PartyRoomSpeed.MEDIUM) + " ticks (Default: " + defaultVariables.countdownTickSpeedMap.get(PartyRoomSpeed.MEDIUM) + ")"),
                "Slow balloon drop speed: " + Colour.RS_GREEN.wrap(variables.countdownTickSpeedMap.get(PartyRoomSpeed.SLOW) + " ticks (Default: " + defaultVariables.countdownTickSpeedMap.get(PartyRoomSpeed.SLOW) + ")"),
                "Very slow balloon drop speed: " + Colour.RS_GREEN.wrap(variables.countdownTickSpeedMap.get(PartyRoomSpeed.VERY_SLOW) + " ticks (Default: " + defaultVariables.countdownTickSpeedMap.get(PartyRoomSpeed.VERY_SLOW) + ")"),

                "Medium balloon value threshold: " + Colour.RS_GREEN.wrap(variables.countdownThresholdMap.get(PartyRoomSpeed.MEDIUM) + " coins (Default: " + defaultVariables.countdownThresholdMap.get(PartyRoomSpeed.MEDIUM) + ")"),
                "Slow balloon value threshold: " + Colour.RS_GREEN.wrap(variables.countdownThresholdMap.get(PartyRoomSpeed.SLOW) + " coins (Default: " + defaultVariables.countdownThresholdMap.get(PartyRoomSpeed.SLOW) + ")"),
                "Very slow balloon value threshold: " + Colour.RS_GREEN.wrap(variables.countdownThresholdMap.get(PartyRoomSpeed.VERY_SLOW) + " coins (Default: " + defaultVariables.countdownThresholdMap.get(PartyRoomSpeed.VERY_SLOW) + ")"),

                "Fast announcement speed: " + Colour.RS_GREEN.wrap(variables.announcementTickSpeedMap.get(PartyRoomSpeed.FAST) +
                        " ticks (Default: " + defaultVariables.announcementTickSpeedMap.get(PartyRoomSpeed.FAST) + ")"),
                "Medium announcement speed: " + Colour.RS_GREEN.wrap(variables.announcementTickSpeedMap.get(PartyRoomSpeed.MEDIUM) +
                        " ticks (Default: " + defaultVariables.announcementTickSpeedMap.get(PartyRoomSpeed.MEDIUM) + ")"),
                "Slow announcement speed: " + Colour.RS_GREEN.wrap(variables.announcementTickSpeedMap.get(PartyRoomSpeed.SLOW) +
                        " ticks (Default: " + defaultVariables.announcementTickSpeedMap.get(PartyRoomSpeed.SLOW) + ")"),
                "Very slow announcement speed: " + Colour.RS_GREEN.wrap(variables.announcementTickSpeedMap.get(PartyRoomSpeed.VERY_SLOW) +
                        " ticks (Default: " + defaultVariables.announcementTickSpeedMap.get(PartyRoomSpeed.VERY_SLOW) + ")"),

                "Fast balloon value threshold: " + Colour.RS_GREEN.wrap(variables.announcementThresholdMap.get(PartyRoomSpeed.FAST) + " coins (Default: " + defaultVariables.announcementThresholdMap.get(PartyRoomSpeed.FAST) + ")"),
                "Medium balloon value threshold: " + Colour.RS_GREEN.wrap(variables.announcementThresholdMap.get(PartyRoomSpeed.MEDIUM) + " coins (Default: " + defaultVariables.announcementThresholdMap.get(PartyRoomSpeed.MEDIUM) + ")"),
                "Slow balloon value threshold: " + Colour.RS_GREEN.wrap(variables.announcementThresholdMap.get(PartyRoomSpeed.SLOW) + " coins (Default: " + defaultVariables.announcementThresholdMap.get(PartyRoomSpeed.SLOW) + ")"),
                Colour.RED.wrap("Reset all settings to default")
        };
    }

    public static final void openEditMode(@NotNull final Player player) {
        val variables = FaladorPartyRoom.getPartyRoom().getVariables();
        player.getDialogueManager().start(new OptionsMenuD(player, "Select the setting to change", buildMenuArray()) {
            @Override
            public void handleClick(int slotId) {
                val title = buildMenuArray()[slotId];
                switch(slotId) {
                    case 0:
                        variables.disabled = !variables.disabled;
                        break;
                    case 1:
                        player.sendInputInt(title, value -> {
                            variables.queuedBalloonsQuantity = value;
                            openEditMode(player);
                        });
                        return;
                    case 2:
                        player.sendInputInt(title, value -> {
                            variables.countdown = value;
                            openEditMode(player);
                        });
                        return;
                    case 3:
                        variables.announcements = !variables.announcements;
                        break;
                    case 4:
                        variables.doorsCloseable = !variables.doorsCloseable;
                        break;
                    case 5:
                        variables.chestCloseable = !variables.chestCloseable;
                        break;
                    case 6:
                        player.sendInputInt(title, value -> {
                            variables.balloonQuantityCap = value;
                            openEditMode(player);
                        });
                        return;
                    case 7:
                        player.sendInputInt(title, value -> {
                            variables.minimumItemStackValueThreshold = value;
                            openEditMode(player);
                        });
                        return;
                    case 8:
                        player.sendInputString(title, value -> {
                            val privilege = getPrivilege(value);
                            if (privilege.isPresent()) {
                                variables.minimumPrivilegeToDropBalloons = privilege.get();
                            } else {
                                player.sendMessage("Privilege by the name of " + value + " not found.");
                            }
                            openEditMode(player);
                        });
                        return;
                    case 9:
                        player.sendInputString(title, value -> {
                            val privilege = getPrivilege(value);
                            if (privilege.isPresent()) {
                                variables.minimumPrivilegeToPerformDance = privilege.get();
                            } else {
                                player.sendMessage("Privilege by the name of " + value + " not found.");
                            }
                            openEditMode(player);
                        });
                        return;
                    case 10:
                        player.sendInputString(title, value -> {
                            val privilege = getPrivilege(value);
                            if (privilege.isPresent()) {
                                variables.minimumPrivilegeToDepositItems = privilege.get();
                            } else {
                                player.sendMessage("Privilege by the name of " + value + " not found.");
                            }
                            openEditMode(player);
                        });
                        return;
                    case 11:
                        player.sendInputInt(title, value -> {
                            variables.countdownTickSpeedMap.put(PartyRoomSpeed.FAST, Math.min(variables.countdownTickSpeedMap.get(PartyRoomSpeed.MEDIUM), value));
                            openEditMode(player);
                        });
                        return;
                    case 12:
                        player.sendInputInt(title, value -> {
                            variables.countdownTickSpeedMap.put(PartyRoomSpeed.MEDIUM, Math.max(Math.min(variables.countdownTickSpeedMap.get(PartyRoomSpeed.SLOW), value),
                                    variables.countdownTickSpeedMap.get(PartyRoomSpeed.FAST)));
                            openEditMode(player);
                        });
                        return;
                    case 13:
                        player.sendInputInt(title, value -> {
                            variables.countdownTickSpeedMap.put(PartyRoomSpeed.SLOW, Math.max(Math.min(variables.countdownTickSpeedMap.get(PartyRoomSpeed.VERY_SLOW), value),
                                    variables.countdownTickSpeedMap.get(PartyRoomSpeed.MEDIUM)));
                            openEditMode(player);
                        });
                        return;
                    case 14:
                        player.sendInputInt(title, value -> {
                            variables.countdownTickSpeedMap.put(PartyRoomSpeed.VERY_SLOW, Math.max(variables.countdownTickSpeedMap.get(PartyRoomSpeed.SLOW), value));
                            openEditMode(player);
                        });
                        return;
                    case 15:
                        player.sendInputInt(title, value -> {
                            variables.countdownThresholdMap.put(PartyRoomSpeed.MEDIUM, Math.max(Math.min(variables.countdownThresholdMap.get(PartyRoomSpeed.SLOW), value),
                                    variables.countdownThresholdMap.get(PartyRoomSpeed.FAST)));
                            openEditMode(player);
                        });
                        return;
                    case 16:
                        player.sendInputInt(title, value -> {
                            variables.countdownThresholdMap.put(PartyRoomSpeed.SLOW, Math.max(Math.min(variables.countdownThresholdMap.get(PartyRoomSpeed.VERY_SLOW), value),
                                    variables.countdownThresholdMap.get(PartyRoomSpeed.MEDIUM)));
                            openEditMode(player);
                        });
                        return;
                    case 17:
                        player.sendInputInt(title, value -> {
                            variables.countdownThresholdMap.put(PartyRoomSpeed.MEDIUM, Math.max(value, variables.countdownThresholdMap.get(PartyRoomSpeed.FAST)));
                            openEditMode(player);
                        });
                        return;
                    case 18:
                        player.sendInputInt(title, value -> {
                            variables.announcementTickSpeedMap.put(PartyRoomSpeed.FAST, Math.min(variables.announcementTickSpeedMap.get(PartyRoomSpeed.MEDIUM), value));
                            openEditMode(player);
                        });
                        return;
                    case 19:
                        player.sendInputInt(title, value -> {
                            variables.announcementTickSpeedMap.put(PartyRoomSpeed.MEDIUM, Math.max(Math.min(variables.announcementTickSpeedMap.get(PartyRoomSpeed.SLOW), value),
                                    variables.announcementTickSpeedMap.get(PartyRoomSpeed.FAST)));
                            openEditMode(player);
                        });
                        return;
                    case 20:
                        player.sendInputInt(title, value -> {
                            variables.announcementTickSpeedMap.put(PartyRoomSpeed.SLOW, Math.max(Math.min(variables.announcementTickSpeedMap.get(PartyRoomSpeed.VERY_SLOW), value),
                                    variables.announcementTickSpeedMap.get(PartyRoomSpeed.MEDIUM)));
                            openEditMode(player);
                        });
                        return;
                    case 21:
                        player.sendInputInt(title, value -> {
                            variables.announcementTickSpeedMap.put(PartyRoomSpeed.VERY_SLOW, Math.max(variables.announcementTickSpeedMap.get(PartyRoomSpeed.SLOW), value));
                            openEditMode(player);
                        });
                        return;
                    case 22:
                        player.sendInputInt(title, value -> {
                            variables.announcementThresholdMap.put(PartyRoomSpeed.FAST, Math.max(value, variables.announcementThresholdMap.get(PartyRoomSpeed.MEDIUM)));
                            openEditMode(player);
                        });
                        return;
                    case 23:
                        player.sendInputInt(title, value -> {
                            variables.announcementThresholdMap.put(PartyRoomSpeed.MEDIUM, Math.max(Math.min(variables.announcementThresholdMap.get(PartyRoomSpeed.FAST), value),
                                    variables.announcementThresholdMap.get(PartyRoomSpeed.SLOW)));
                            openEditMode(player);
                        });
                        return;
                    case 24:
                        player.sendInputInt(title, value -> {
                            variables.announcementThresholdMap.put(PartyRoomSpeed.SLOW, Math.max(Math.min(variables.announcementThresholdMap.get(PartyRoomSpeed.MEDIUM), value),
                                    variables.announcementThresholdMap.get(PartyRoomSpeed.VERY_SLOW)));
                            openEditMode(player);
                        });
                        return;
                    case 25:
                        variables.setDefaults();
                        break;
                }
                openEditMode(player);
            }

            @Override
            public boolean cancelOption() {
                return true;
            }
        });
    }

    private static final Optional<Privilege> getPrivilege(@NotNull final String description) {
        Privilege privilege = null;
        if (description.startsWith("player") || description.startsWith("nor") || description.startsWith("reg")) {
            privilege = Privilege.PLAYER;
        } else if (description.startsWith("mod")) {
            privilege = Privilege.MODERATOR;
        } else if (description.startsWith("spawn admin")) {
            privilege = Privilege.SPAWN_ADMINISTRATOR;
        } else if (description.startsWith("admin")) {
            privilege = Privilege.ADMINISTRATOR;
        } else if (description.startsWith("global")) {
            privilege = Privilege.GLOBAL_MODERATOR;
        } else if (description.startsWith("forum")) {
            privilege = Privilege.FORUM_MODERATOR;
        } else if (description.startsWith("sup")) {
            privilege = Privilege.JUNIOR_MODERATOR;
        } else if (description.startsWith("youtube")) {
            privilege = Privilege.YOUTUBER;
        } else if (description.startsWith("hidden")) {
            privilege = Privilege.HIDDEN_ADMINISTATOR;
        }
        return Optional.ofNullable(privilege);
    }

}