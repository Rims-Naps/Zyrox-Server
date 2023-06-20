package com.zenyte.cores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.Constants;
import com.zenyte.game.*;
import com.zenyte.game.content.partyroom.FaladorPartyRoom;
import com.zenyte.game.shop.Shop;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.ui.testinterfaces.TournamentViewerInterface;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.plugins.object.WellOfGoodwill;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
public final class WorldThread implements Runnable {

    private final Logger tickLogger = LoggerFactory.getLogger("Tick logger");
    private final List<String> publicStaff = new ObjectArrayList<>();
    private final List<String> privateStaff = new ObjectArrayList<>();
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private int dayOfYear;

    public static long WORLD_CYCLE;
    private static int pidSwapDelay = Utils.random(100, 150);
    @Override
    public final void run() {
        try {
            WORLD_CYCLE++;
            val nano = System.nanoTime();
            boolean resetDailies = false;
            try {
                val currentDayOfYear = dayOfYear;
                dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                if (currentDayOfYear != dayOfYear) {
                    resetDailies = true;
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                publicStaff.clear();
                privateStaff.clear();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                Container.resetContainer();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val shopNano = System.nanoTime();
            try {
                Shop.process();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val areaManagerNano = System.nanoTime();
            try {
                GlobalAreaManager.process();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            //World.processLogins();
            val worldTaskProcessNano = System.nanoTime();
            try {
                WorldTasksManager.processTasks();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val gameClockNano = System.nanoTime();
            try {
                GameClock.process();
                BonusXpManager.checkIfFlip();
                BonusCoxManager.checkIfFlip();
                BonusTobManager.checkIfFlip();
                WellOfGoodwill.checkIfFlip();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            //System.out.println("test: " + World.USED_PIDS.values());
            val npcProcessNano = System.nanoTime();
            try {
                for (final NPC npc : World.getNPCs()) {
                    try {
                        if (npc == null) {
                            continue;
                        }
                        npc.processEntity();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val npcRemovalNano = System.nanoTime();
            try {
                for (final NPC removed : World.pendingRemovedNPCs) {
                    try {
                        if (removed == null) {
                            continue;
                        }
                        World.getNPCs().remove(removed);
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                World.pendingRemovedNPCs.clear();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }

            try {
                NPC.clearPendingAggressions();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val playerProcessNano = System.nanoTime();
            try {
                for (final Player player : World.USED_PIDS.values()) {
                    try {
                        if (player == null || player.isNulled() || !player.isRunning() || player.isFinished()) {
                            continue;
                        }
                        player.processEntity();
                        val status = GameNoticeboardInterface.getStaffStatus(player);
                        if (status == GameNoticeboardInterface.StaffStatus.PUBLIC) {
                            publicStaff.add(player.getName());
                        } else if (status == GameNoticeboardInterface.StaffStatus.NOT_PUBLIC) {
                            privateStaff.add(player.getName());
                        }
                        if (resetDailies) {
                            RuneDate.checkDate(player);
                        }
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                for (final Player player : World.USED_PIDS.values()) {
                    try {
                        if (player == null || player.isNulled() || !player.isRunning() || player.isFinished()) {
                            continue;
                        }
                        player.postProcess();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val partyRoomNano = System.nanoTime();
            try {
                val area = GlobalAreaManager.getOptionalArea(FaladorPartyRoom.class);
                area.ifPresent(FaladorPartyRoom::processBalloons);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val playerFlushNano = System.nanoTime();
            try {
                GlobalAreaManager.postProcess();
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }

            try {
                World.USED_PIDS.values().parallelStream().forEach(player -> {
                    try {
                        if (player == null || player.isNulled() || !player.isRunning() || player.isFinished()) {
                            return;
                        }
                        if (player.getTemporaryAttributes().containsKey("tournament_spectating")) {
                            TournamentViewerInterface.refreshSpectator(player);
                        }
                        player.processEntityUpdate();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                });
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val playerLogoutNano = System.nanoTime();
            try {
                val milliseconds = System.currentTimeMillis();
                for (final Player player : World.USED_PIDS.values()) {
                    try {
                        val channel = player.getSession().getChannel();
                        val inWildy = player.getArea() instanceof WildernessArea;
                        val playerExpirationTime = milliseconds - TimeUnit.TICKS.toMillis(inWildy ? 100 : 25);
                        val isExpired = player.getLastReceivedPacket() < playerExpirationTime;
                        if (isExpired || player.isLoggedOut() || !channel.isActive() || !channel.isOpen()) {
                            if (player.getLastDisconnectionTime() == 0) {
                                player.setLastDisconnectionTime(Utils.currentTimeMillis());
                            }
                            if (!isExpired && (player.isUnderCombat() || player.isLocked()) && player.getLogoutCount() < (inWildy ? 100 : 25)) {
                                player.setLogoutCount(player.getLogoutCount() + 1);
                                continue;
                            }
                            player.logout(true);
                            player.getSession().getChannel().flush();
                            player.getSession().getChannel().closeFuture();
                            World.unregisterPlayer(player);
                        }
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val maskResetNano = System.nanoTime();
            try {
                World.getNPCs().parallelStream().forEach(npc -> {
                    try {
                        if (npc == null || npc.isFinished()) {
                            return;
                        }
                        npc.resetMasks();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                });
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                World.USED_PIDS.values().parallelStream().forEach(player -> {
                    try {
                        if (player == null || player.isFinished()) return;
                        player.resetMasks();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                });
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            try {
                if (--pidSwapDelay == 0) {
                    try {
                        World.shufflePids();
                    } catch (final Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                    pidSwapDelay = Utils.random(100, 150);
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val purgeNano = System.nanoTime();
            try {
                if (Constants.PURGING_CHUNKS) {
                    if (WORLD_CYCLE % 500 == 0) {
                        World.purgeChunks();
                    }
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val playerSaveNano = System.nanoTime();
            try {
                val loginManager = CoresManager.getLoginManager();
                val awaitingSave = loginManager.getAwaitingSave();
                if (!awaitingSave.isEmpty()) {
                    for (val player : awaitingSave) {
                        loginManager.save(player);
                    }
                    awaitingSave.clear();
                }
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val finishNano = System.nanoTime();


            val tickLog = new TickLog(new Date(), WorldThread.WORLD_CYCLE, shopNano - nano, areaManagerNano - shopNano, worldTaskProcessNano - areaManagerNano,
                    gameClockNano - worldTaskProcessNano, npcProcessNano - gameClockNano, npcRemovalNano - npcProcessNano,
                    playerProcessNano - npcRemovalNano, partyRoomNano - playerProcessNano, playerFlushNano - partyRoomNano,
                    playerLogoutNano - playerFlushNano, maskResetNano - playerLogoutNano, purgeNano - maskResetNano,
                    WORLD_CYCLE % 500 == 0 ? (playerSaveNano - purgeNano) : 0, finishNano - playerSaveNano, finishNano - nano, World.getPlayers().size(),
                    World.getNPCs().size(), WorldTasksManager.count(), publicStaff, privateStaff);

            tickLogger.info(gson.toJson(tickLog));


            if (Constants.CYCLE_DEBUG) {
                System.out.println("Cycle took: " + ((System.nanoTime() - nano) / 1000000f) + " ms." + " Players: " + World.getPlayers().size() + ". NPCs: " + World.getNPCs().size());
            }
            if (CoresManager.isShutdown()) {
                try {
                    World.shutdown();
                } catch (Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class TickLog {
        private final Date date;
        private final long tick, containerT, shopT, areaT, worldTaskT, gameClockT, npcProcessT, npcRemovalT, playerProcessT, partyRoomT, playerFlushT, playerLogoutT,
                maskResetT, purgeT, playerSaveT, totalT;
        private final int players, npcs, tasks;
        private final List<String> publicStaff, privateStaff;
    }

}
