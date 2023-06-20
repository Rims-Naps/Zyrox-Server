package com.zenyte.game.content.minigame.fightcaves;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.KaramjaDiary;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.event.DoubleDropsManager;
import com.zenyte.game.content.follower.impl.BossPet;
import com.zenyte.game.content.minigame.fightcaves.npcs.FightCavesNPC;
import com.zenyte.game.content.minigame.fightcaves.npcs.TzTokJad;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.area.plugins.LogoutPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import com.zenyte.processor.Listener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kris | 26/10/2018 01:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class FightCaves extends DynamicArea implements LogoutPlugin, DeathPlugin, CycleProcessPlugin, CannonRestrictionPlugin {

    private static final int WIDTH = 8, HEIGHT = 8;
    private static final Item FIRE_CAPE = new Item(6570);
    private static final ImmutableLocation ENTRANCE = new ImmutableLocation(2438, 5168, 0);
    private static final Location[] path = new Location[]{new Location(2404, 5105, 0), new Location(2404, 5104, 0), new Location(2400, 5088, 0)};
    @Getter
    private final List<NPC> monsters = new ArrayList<>();
    private final List<FightCavesNPC.TzHaarNPC> npcs = new ArrayList<>();
    @Getter
    private final Player player;
    private int wave = 1;
    private int rotation = Utils.random(14);
    private int cycle;
    private State state = State.STOPPED;
    private long milliseconds;
    private final boolean isOnJadAssignment;

    private FightCaves(final AllocatedArea allocatedArea, final Player player) {
        super(allocatedArea, 296, 632);
        this.player = player;
        val task = player.getSlayer().getAssignment();
        this.isOnJadAssignment = task != null && task.getTask() == RegularTask.TZTOK_JAD;
    }

    @Listener(type = Listener.ListenerType.LOGIN)
    static void login(final Player player) {
        val attribute = player.getAttributes().get("Fight caves progress");
        if (attribute == null) {
            return;
        }
        player.lock();
        start(player);
    }

    public static void start(final Player player) {
        try {
            val area = MapBuilder.findEmptyChunk(WIDTH, HEIGHT);
            val caves = new FightCaves(area, player);
            caves.constructRegion();
        } catch (OutOfSpaceException e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public Location onLoginLocation() {
        return ENTRANCE;
    }

    public void skip(final int waveId) {
        if (waveId < 1 || waveId > 63) {
            throw new RuntimeException("Wave is must be between 1 and 63.");
        }
        wave = waveId;
        for (val npc : monsters) {
            npc.finish();
        }
        monsters.clear();
        informWave();
        state = State.NEXT_WAVE;
        cycle = 3;
    }

    @Override
    public String name() {
        return "Fight caves";
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return "You're in a " + Colour.RED + "safe area" + Colour.END + ".<br><br>" + "If you die you will keep all the items you<br>" + "brought with you.";
    }

    @Override
    public Location getRespawnLocation() {
        return new Location(2438, 5170, 0);
    }

    public void checkWave(final FightCavesNPC npc) {
        if (npc.getId() == FightCavesNPC.TzHaarNPC.TZ_TOK_JAD.getId()) {
            player.getMusic().playJingle(76);
            WorldTasksManager.schedule(this::leave, 3);
            return;
        }
        if (monsters.remove(npc) && monsters.isEmpty()) {
            if (state == State.LOGGING_OUT) return;
            state = State.NEXT_WAVE;
            cycle = 5;
            player.getMusic().playJingle(76);
            incrementWave();
            informWave();
        }
    }

    private void incrementWave() {
        wave++;
        if (!players.contains(player))
            return;
        player.addAttribute("Fight caves progress", wave << 16 | rotation);
        player.addAttribute("Fight caves duration", System.currentTimeMillis() - milliseconds);
    }

    private void informWave() {
        player.sendMessage(Colour.RED + "Wave: " + wave);
        if (wave == 63) {
            player.sendMessage(Colour.RED + "Final Challenge!");
            player.getDialogueManager().start(new NPCChat(player, 2180, "Look out, here comes TzTok-Jad!"));
        }
    }

    private void spawnNextWave() {
        if (!npcs.isEmpty()) {
            npcs.clear();
        }
        FightCavesNPC.TzHaarNPC.populate(npcs, wave);
        for (val npc : npcs) {
            spawnNPC(npc);
        }
    }

    public void spawnNPC(final FightCavesNPC.TzHaarNPC npc) {
        val mob = npc.instantiate(getLocation(FightCaveSpawn.getNextLocation(rotation)), this).spawn();
        if (!FightCavesNPC.TzHaarNPC.isAlternative(mob)) {
            rotation++;
        }
        if (npc == FightCavesNPC.TzHaarNPC.YT_HUR_KOT) {
            return;
        }
        mob.getCombat().setTarget(player);
    }

    public Optional<TzTokJad> getJad() {
        if (monsters.isEmpty()) {
            return Optional.empty();
        }
        for (val npc : monsters) {
            if (npc instanceof TzTokJad) {
                return Optional.of((TzTokJad) npc);
            }
        }
        return Optional.empty();
    }

    public void finishTracking() {
        val duration = System.currentTimeMillis() - milliseconds;
        player.getBossTimer().inform("Fight Caves", duration);
        if (duration < 1500000 && !player.getBooleanAttribute("master-combat-achievement44")) {
            player.putBooleanAttribute("master-combat-achievement44", true);
            MasterTasks.sendMasterCompletion(player, 44);
        }
        if (duration < 1200000 && !player.getBooleanAttribute("grandmaster-combat-achievement27")) {
            player.putBooleanAttribute("grandmaster-combat-achievement27", true);
            GrandmasterTasks.sendGrandmasterCompletion(player, 27);
        }
    }

    @Override
    public boolean sendDeath(final Player player, Entity source) {
        player.setAnimation(Animation.STOP);
        player.lock();
        player.stopAll();
        if (player.getPrayerManager().isActive(Prayer.RETRIBUTION)) {
            player.getPrayerManager().applyRetributionEffect(source);
        }
        //player.getItemsKeptOnDeath().setContainers(source);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (player.isFinished() || player.isNulled()) {
                    stop();
                    return;
                }
                if (ticks == 0) {
                    player.setAnimation(Player.DEATH_ANIMATION);
                } else if (ticks == 2) {
                    player.getDeathMechanics().death(player.getMostDamagePlayer(), null);
                    player.sendMessage("Oh dear, you have died.");
                    player.reset();
                    player.setAnimation(Animation.STOP);
                    if (player.getVariables().isSkulled()) {
                        player.getVariables().setSkull(false);
                    }
                    val area = player.getArea();
                    val plugin = area instanceof DeathPlugin ? (DeathPlugin) area : null;
                    val respawnLocation = plugin == null ? null : plugin.getRespawnLocation();
                    player.setLocation(respawnLocation != null ? respawnLocation : player.getRespawnPoint().getLocation());
                } else if (ticks == 3) {
                    player.unlock();
                    player.getAppearance().resetRenderAnimation();
                    player.setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 1);
        return true;
    }

    @Override
    public void constructed() {
        player.unlock();
        state = State.NEXT_WAVE;
        cycle = 16;
        milliseconds = System.currentTimeMillis();
        val tile = getLocation(2400, 5088, 0);
        val attribute = player.getNumericAttribute("Fight caves progress");
        if (attribute.intValue() != 0) {
            val duration = player.getNumericAttribute("Fight caves duration");
            milliseconds -= duration.longValue();
            val bitpacked = attribute.intValue();
            wave = bitpacked >> 16;
            rotation = bitpacked & 0xFFFF;
            player.setLocation(tile);
            sendStartDialogue();
            return;
        }
        if (player.getMemberRank().eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
            wave = 31;
        }
        player.putBooleanAttribute("hitInFightcavesWasNotMelee", false);
        player.putBooleanAttribute("wasHitByBat", false);
        player.putBooleanAttribute("ytmejkothealed", false);
        player.putBooleanAttribute("jadhealed", false);
        player.putBooleanAttribute("drainedPrayerInFightCaves", false);
        player.setLocation(getLocation(2412, 5114, 0));
        player.lock(1);
        WorldTasksManager.schedule(() -> {
            player.getAchievementDiaries().update(KaramjaDiary.ATTEMPT_FIGHT_PITS_OR_CAVES);
            sendStartDialogue();
            player.resetWalkSteps();
            if (player.isRun()) {
                player.setRun(false);
                player.getVarManager().sendVar(173, 0);
            }
            for (val location : path) {
                val alteredLocation = getLocation(location);
                player.addWalkSteps(alteredLocation.getX(), alteredLocation.getY(), 25, true);
            }
        });
    }

    public void leave() {
        player.blockIncomingHits();
        player.setLocation(ENTRANCE);
        val boosted = player.getMemberRank().eligibleTo(MemberRank.SAPPHIRE_MEMBER);
        int wave = monsters.isEmpty() ? this.wave : Math.max(1, this.wave - 1);
        //Forcibly set the wave to 63 if jad has died.
        if (this.wave == 63) {
            for (val monster : monsters) {
                if (monster.getClass() == TzTokJad.class && (monster.isDead() || monster.isFinished())) {
                    wave = 63;
                    break;
                }
            }
        }
        val roundsCompleted = boosted ? (wave - 30) : wave;
        player.getDialogueManager().start(new NPCChat(player, 2180, wave >= 63 ? "You even defeated Tz Tok-Jad, I am most impressed! Please accept this gift as a reward." : roundsCompleted <= 1 ? "Well I suppose you tried... better luck next time." : "Well done in the cave, here, take Tokkul as reward."));
        if (roundsCompleted > 1) {
            val amount = boosted ? (roundsCompleted * 16064 / 63) : (roundsCompleted * 8032 / 63);
           val tokkul = new Item(6529, amount);
           if (DiaryReward.KARAMJA_GLOVES4.eligibleFor(player)) {
               tokkul.setAmount(tokkul.getAmount() * 2);
           }
           player.getInventory().addItem(tokkul).onFailure(cape -> World.spawnFloorItem(tokkul, player, ENTRANCE));
           if (wave >= 63) {
               val pet = BossPet.TZREK_JAD;
               player.sendMessage("You were victorious!");
               player.getInventory().addItem(FIRE_CAPE).onFailure(cape -> World.spawnFloorItem(cape, player, ENTRANCE));
               player.getCollectionLog().add(FIRE_CAPE);
               pet.roll(player, isOnJadAssignment ? 100 : 200);
               if (TzTokJad.hasBeenHitOffPrayer() && !player.getBooleanAttribute("elite-combat-achievement34")) {
                   player.putBooleanAttribute("elite-combat-achievement34", true);
                   EliteTasks.sendEliteCompletion(player, 34);
               }
               if (!player.getBooleanAttribute("hitInFightcavesWasNotMelee") && !player.getBooleanAttribute("elite-combat-achievement58")) {
                   player.putBooleanAttribute("elite-combat-achievement58", true);
                   EliteTasks.sendEliteCompletion(player, 58);
               }
               if (!player.getBooleanAttribute("wasHitByBat") && !player.getBooleanAttribute("master-combat-achievement20")) {
                   player.putBooleanAttribute("master-combat-achievement20", true);
                   MasterTasks.sendMasterCompletion(player, 20);
               }
               if (!player.getBooleanAttribute("ytmejkothealed") && !player.getBooleanAttribute("master-combat-achievement21")) {
                   player.putBooleanAttribute("master-combat-achievement21", true);
                   MasterTasks.sendMasterCompletion(player, 21);
               }
               if (!player.getBooleanAttribute("jadhealed") && !player.getBooleanAttribute("grandmaster-combat-achievement4")) {
                   player.putBooleanAttribute("grandmaster-combat-achievement4", true);
                   GrandmasterTasks.sendGrandmasterCompletion(player, 4);
               }
               if (!player.getBooleanAttribute("drainedPrayerInFightCaves") && !player.getBooleanAttribute("grandmaster-combat-achievement13")) {
                   player.putBooleanAttribute("grandmaster-combat-achievement13", true);
                   GrandmasterTasks.sendGrandmasterCompletion(player, 13);
               }
           }
       }

    }

    private void sendStartDialogue() {
        WorldTasksManager.schedule(() ->{
            if (players.contains(player)) {
                informWave();
            }
        }, 10);
        player.getDialogueManager().start(new Dialogue(player, 2180) {
            @Override
            public void buildDialogue() {
                npc("You're on your own now JalYt, prepare to fight for your life!");
            }
        });
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {
        if (logout) {
            player.addAttribute("Fight caves progress", wave << 16 | rotation);
            player.addAttribute("Fight caves duration", System.currentTimeMillis() - milliseconds);
        } else {
            player.getAttributes().remove("Fight caves progress");
            player.getAttributes().remove("Fight caves duration");
            val task = player.getSlayer().getAssignment();
            if (task == null || task.getTask() != RegularTask.TZTOK_JAD) {
                return;
            }
            if (wave < 63) {
                if (player.getSlayer().getAssignment().getAmount() == 1) {
                    player.getSlayer().removeTask();
                } else {
                    player.getSlayer().getAssignment().setAmount(player.getSlayer().getAssignment().getAmount() - 1);
                }
            }
        }
    }

    @Override
    public boolean manualLogout(final Player player) {
        if (state == State.PENDING_LOGOUT || state == State.LOGGING_OUT || monsters.isEmpty()) {
            player.logout(false);
            return false;
        }
        player.sendMessage(Colour.RED + "Your logout request has been received. The minigame will be paused at the " + "end of this wave.");
        player.sendMessage(Colour.RED + "If you try to log out before that, you will have to repeat this wave.");
        state = State.LOGGING_OUT;
        return false;
    }

    @Override
    public void process() {
        if (state == State.STOPPED || state == State.PENDING_LOGOUT) return;
        if (state == State.LOGGING_OUT) {
            if (!monsters.isEmpty()) {
                return;
            }
            incrementWave();
            player.sendMessage(Colour.RED + "The Fight Cave has been paused. You may now log out.");
            state = State.PENDING_LOGOUT;
            return;
        }
        if (--cycle <= 0) {
            if (state == State.NEXT_WAVE) {
                if (player.getInterfaceHandler().isVisible(231)) return;
                spawnNextWave();
                state = State.STOPPED;
            }
        }
    }

    private enum State {

        NEXT_WAVE, LOGGING_OUT, PENDING_LOGOUT, STOPPED

    }
}
