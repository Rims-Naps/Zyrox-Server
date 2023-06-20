package com.zenyte.game.world.entity.player.punishments;

import com.zenyte.Constants;
import com.zenyte.GameEngine;
import com.zenyte.api.client.query.SubmitUserPunishment;
import com.zenyte.api.client.webhook.RevokePunishmentWebhook;
import com.zenyte.api.model.PunishmentLog;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * @author Kris | 09/03/2019 19:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PunishmentManager implements ScheduledExternalizable {

    public static final String PATH = "data/punishments.json";

    private static final Map<String, List<Punishment>> punishments = new Object2ObjectOpenHashMap<>();

    private static final Function<String, List<Punishment>> function = (name) -> new ObjectArrayList<>();

    private static final Optional<List<Punishment>> getPunishments(@NotNull final String identifier) {
        return Optional.ofNullable(punishments.get(identifier));
    }

    public static Optional<Punishment> isPunishmentActive(@NotNull final Player player,
                                                          @NotNull final PunishmentType type) {
        return isPunishmentActive(player.getUsername(), player.getIP(), player.getMACAddress(), type);
    }

    public static final Optional<Punishment> isPunishmentActive(final String username,
                                                                final String ip, final String mac,
                                                                @NotNull final PunishmentType type) {
        val regularPunishment = getPunishment(username, type);
        if (regularPunishment != null) {
            return Optional.of(regularPunishment);
        }
        val ipPunishment = getPunishment(ip, type);
        if (ipPunishment != null) {
            return Optional.of(ipPunishment);
        }
        return Optional.ofNullable(getPunishment(mac, type));
    }

    private static final Punishment getPunishment(final String identifier,
                                                            @NotNull final PunishmentType type) {
        if (identifier == null) {
            return null;
        }
        val optionalPunishments = getPunishments(identifier);
        if (!optionalPunishments.isPresent()) {
            return null;
        }
        val category = type.getCategory();
        val punishments = optionalPunishments.get();
        Punishment longestPunishment = null;
        for (val punishment : punishments) {
            val punishmentType = punishment.getType();
            if (category.equals(punishmentType.getCategory()) && !punishment.isExpired() && punishmentType.ordinal() >= type.ordinal()) {
                val date = longestPunishment == null ? null : longestPunishment.getExpirationDate();
                val newDate = punishment.getExpirationDate();
                if (longestPunishment == null || date != null && (newDate == null || newDate.after(date))) {
                    longestPunishment = punishment;
                }
            }
        }
        return longestPunishment;
    }

    public static final void requestPunishment(@NotNull final Player reporter, @NotNull final PunishmentType type) {
        reporter.sendInputName("Enter name of the player to " + type.getFormattedString() + ":", n -> {
            val name = n.trim();
            requestPunishment(reporter, name, type);
        });
    }

    public static final void requestPunishment(final Player reporter, final String targetName, final PunishmentType type) {
        CoresManager.getLoginManager().load(targetName, true, target -> {
            if (!target.isPresent()) {
                reporter.sendMessage("Request to " + type.getFormattedString() + " user " + Utils.formatString(targetName) + " " + Colour.RED.wrap("failed") + " - " + "Account does not exist.");
                return;
            }
            val offender = target.get();
            reporter.sendInputInt("Enter the duration of the " + type.getFormattedString() + " in hours(0 for " + "permanent): ", hours -> {
                reporter.sendInputString("Enter the reasoning: ", reason -> {
                    val punishment = new Punishment(type, reporter.getUsername(), Utils.formatUsername(targetName), offender.getIP(), offender.getMACAddress(), new Date(), hours,
                            hours == 0 ? null : Date.from(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(hours))), reason);
                    PunishmentManager.appendPunishment(reporter, targetName, punishment, offender);
                });
            });
        });
    }

    public static final void revokePunishments(@NotNull Player reporter) {
        reporter.sendInputName("Enter name of the player whose punishment(s) to revoke:", n -> {
            val name = n.trim();

            CoresManager.getLoginManager().load(name, true, optionalOffender -> {
                if (!optionalOffender.isPresent()) {
                    reporter.sendMessage("Request to revoke the punishments of user " + Utils.formatString(name) + " " + Colour.RED.wrap("failed") + " - Account does not exist.");
                    return;
                }
                val offender = optionalOffender.get();
                val list = new ArrayList<Punishment>();
                val listOfPunishmentLists = new ArrayList<List<Punishment>>();
                val regularPunishments = punishments.get(Utils.formatUsername(name));
                if (regularPunishments != null) {
                    listOfPunishmentLists.add(regularPunishments);
                }
                val ipPunishments = punishments.get(offender.getIP());
                if (ipPunishments != null) {
                    listOfPunishmentLists.add(ipPunishments);
                }
                val macPunishments = punishments.get(offender.getMACAddress());
                if (macPunishments != null) {
                    listOfPunishmentLists.add(macPunishments);
                }
                for (val punishmentList : listOfPunishmentLists) {
                    for (val punishment : punishmentList) {
                        if (!punishment.isExpired()) {
                            list.add(punishment);
                        }
                    }
                }
                if (list.isEmpty()) {
                    reporter.sendMessage("Request to revoke the punishments of user " + Utils.formatString(name) + " " + Colour.RED.wrap("failed") + " - No active punishments found.");
                    return;
                }

                val optionsList = new ArrayList<String>(list.size());
                for (val activePunishment : list) {
                    optionsList.add(activePunishment.toString());
                }

                reporter.getDialogueManager().start(new OptionsMenuD(reporter, "Select the punishment to revoke", optionsList.toArray(new String[0])) {

                    @Override
                    public void handleClick(int slotId) {
                        val punishment = Objects.requireNonNull(list.get(slotId));
                        if (punishment.getType().getCategory().equals(PunishmentCategory.BAN)) {
                            if (!reporter.getPrivilege().eligibleTo(Privilege.MODERATOR)) {
                                reporter.sendMessage("Supports cannot revoke ban-category punishments.");
                                return;
                            }
                        }
                        reporter.sendInputString("Enter revoke reason:", reason -> {
                            val previousSize = listOfPunishmentLists.size();
                            listOfPunishmentLists.removeIf(list -> list.remove(punishment));
                            if (previousSize == listOfPunishmentLists.size()) {
                                reporter.sendMessage("Request to revoke the punishment '" + punishment.toString() + "' " + Colour.RED.wrap("failed") + " - Punishment has already been revoked.");
                            } else {
                                reporter.sendMessage("Request to revoke the punishment '" + punishment.toString() + "' " + Colour.RS_GREEN.wrap("succeeded") + ".");
                                new RevokePunishmentWebhook(reporter, reason, punishment).execute();
                            }
                        });
                    }

                    @Override
                    public boolean cancelOption() {
                        return true;
                    }
                });
            });


        });
    }

    private static final void appendPunishment(@NotNull final Player punisher, @NotNull final String target,
                                               @NotNull final Punishment punishment, Player offlinePlayer) {
        val type = punishment.getType();
        val list = punishments.computeIfAbsent(isUserTargeted(type) ? punishment.getOffender()
                : isIPTargeted(type) ? punishment.getIp()
                : punishment.getMacAddress(), function);
        val requestedDate = punishment.getExpirationDate();
        val onlineOffender = World.getPlayer(target);

        for (val previousPunishment : list) {
            if (previousPunishment.getType().equals(type)) {
                val date = previousPunishment.getExpirationDate();
                if (date == null || requestedDate != null && date.after(requestedDate)) {
                    punisher.sendMessage("Request to " + punishment.getType().getFormattedString() + " user " + Utils.formatString(target) + " " + Colour.RED.wrap("failed") + " - " + "Existing punishment outlasts requested.");
                    return;
                }
            }
        }
        if (isMACTargeted(type)) {
            if (punishment.getMacAddress() == null || punishment.getMacAddress().length() <= 0) {
                punisher.sendMessage("Unable to punish the user as their mac address is not valid.");
                return;
            }
        }
        val category = punishment.getType().getCategory();
        if (category == PunishmentCategory.BAN) {
            try {
                val setOfPeopleToKick = new ObjectOpenHashSet<Player>();
                onlineOffender.ifPresent(setOfPeopleToKick::add);
                if (type == PunishmentType.IP_BAN) {
                    val ip = onlineOffender.map(Player::getIP).orElse(offlinePlayer == null ? Strings.EMPTY : offlinePlayer.getIP());
                    if (!ip.isEmpty()) {
                        for (val player : World.getPlayers()) {
                            if (player == null || player.isNulled() || player.isFinished() || !player.getIP().equals(ip)) {
                                continue;
                            }
                            setOfPeopleToKick.add(player);
                        }
                    }
                } else if (type == PunishmentType.MAC_BAN) {
                    val mac = onlineOffender.map(Player::getMACAddress).orElse(offlinePlayer == null ? Strings.EMPTY : offlinePlayer.getMACAddress());
                    if (!mac.isEmpty()) {
                        for (val player : World.getPlayers()) {
                            if (player == null || player.isNulled() || player.isFinished() || !player.getMACAddress().equals(mac)) {
                                continue;
                            }
                            setOfPeopleToKick.add(player);
                        }
                    }
                }
                for (val player : setOfPeopleToKick) {
                    if (player == null || player.isFinished() || player.isNulled()) {
                        continue;
                    }
                    if(type == PunishmentType.BANHAMMER) {
                        WorldTasksManager.schedule(new WorldTask() {
                            int ticks = 0;
                            Item originalWeapon = null;
                            @Override
                            public void run() {
                                if(ticks == 0) {
                                    player.lock();
                                    punisher.lock();
                                    originalWeapon = punisher.getEquipment().getItem(EquipmentSlot.WEAPON);
                                }
                                if(ticks == 1) {
                                    player.setLocation(new Location(punisher.getLocation().getX(), punisher.getLocation().getY() + 1, punisher.getPlane()));
                                }
                                if(ticks == 2) {
                                    player.setFaceEntity(punisher);
                                    player.setAnimation(new Animation(2836));
                                    punisher.setFaceEntity(player);
                                }
                                if(ticks == 5) {
                                    punisher.getEquipment().set(EquipmentSlot.WEAPON, new Item(ItemId.DRAGON_WARHAMMER));
                                    punisher.setAnimation(new Animation(1378));
                                    punisher.setGraphics(new Graphics(1292));
                                    player.getVariables().setSkull(true);
                                }
                                if(ticks == 6) {
                                    player.applyHit(new Hit(punisher,9999, HitType.REGULAR));
                                    player.unlock();
                                }
                                if(ticks == 7) {
                                    player.setGraphics(new Graphics(287));
                                    punisher.getEquipment().set(EquipmentSlot.WEAPON, originalWeapon);
                                }
                                if(ticks == 8) {
                                    punisher.unlock();
                                }

                                if(ticks == 10) {
                                    player.logout(true);
                                    stop();
                                }

                                ticks++;
                            }
                        },0, 1);
                    } else {
                        player.logout(true);
                    }
                }
                setOfPeopleToKick.clear();
            } catch (Exception e) {
                GameEngine.logger.error(Strings.EMPTY, e);
            }
        }
        list.removeIf(p -> p.getType().equals(type) && (requestedDate == null || requestedDate.after(p.getExpirationDate())));
        list.add(punishment);
        val formattedExpiry = requestedDate == null ? "Never" : requestedDate.toString();
        punisher.sendMessage("Request to " + punishment.getType().getFormattedString() + " user " + Utils.formatString(target) + " " + Colour.RS_GREEN.wrap("succeeded") + " - " + "expires: " + formattedExpiry + ".");
    
        submitPunishment(new PunishmentLog(
                punisher.getPlayerInformation().getUserIdentifier(),
                punisher.getName(),
                onlineOffender.map(p -> p.getPlayerInformation().getUserIdentifier()).orElse(offlinePlayer == null ? -1 : offlinePlayer.getPlayerInformation().getUserIdentifier()),
                target,
                onlineOffender.map(Player::getIP).orElse(offlinePlayer == null ? Strings.EMPTY : offlinePlayer.getIP()),
                onlineOffender.map(Player::getMACAddress).orElse(offlinePlayer == null ? Strings.EMPTY : offlinePlayer.getMACAddress()),
                punishment.getType().getFormattedString(),
                formattedExpiry,
                punishment.getReason()
        ));
    }

    private static boolean isUserTargeted(final PunishmentType type) {
        return type == PunishmentType.BAN || type == PunishmentType.MUTE || type == PunishmentType.YELL_MUTE;
    }

    private static boolean isIPTargeted(final PunishmentType type) {
        return type == PunishmentType.IP_BAN || type == PunishmentType.IP_MUTE || type == PunishmentType.IP_YELL_MUTE;
    }

    private static boolean isMACTargeted(final PunishmentType type) {
        return type == PunishmentType.MAC_BAN || type == PunishmentType.MAC_MUTE || type == PunishmentType.MAC_YELL_MUTE;
    }

    @Override
    public int writeInterval() {
        return 5;
    }

    @Override
    public void read(final BufferedReader reader) {
        val punishmentsArray = World.getGson().fromJson(reader, Punishment[].class);
        for (val punishment : punishmentsArray) {
            val identifier = punishment.getOffender();
            val list = punishments.computeIfAbsent(identifier, function);
            punishments.put(identifier, list);
            list.add(punishment);
        }
    }

    @Override
    public void write() {
        punishments.forEach((name, list) -> list.removeIf(Punishment::isExpired));
        punishments.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        val list = new ArrayList<Punishment>(punishments.size());
        punishments.forEach((key, l) -> list.addAll(l));
        out(World.getGson().toJson(list.toArray()));
    }

    @Override
    public String path() {
        return PATH;
    }
    
    private static void submitPunishment(final PunishmentLog punishment) {
        if (!Constants.WORLD_PROFILE.getApi().isEnabled() || Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
            return;
        }
        CoresManager.getServiceProvider().submit(() -> {
            new SubmitUserPunishment(punishment).execute();
        });
    }
    
}
