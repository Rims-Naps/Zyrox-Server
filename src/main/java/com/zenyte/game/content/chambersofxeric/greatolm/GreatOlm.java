package com.zenyte.game.content.chambersofxeric.greatolm;

import com.google.common.eventbus.Subscribe;
import com.zenyte.cores.WorldThread;
import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.chambersofxeric.greatolm.scripts.*;
import com.zenyte.game.content.chambersofxeric.npc.RaidNPC;
import com.zenyte.game.content.chambersofxeric.rewards.RaidRewards;
import com.zenyte.game.content.chambersofxeric.score.Scoreboard;
import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.SpellState;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.NPCSpell;
import com.zenyte.game.content.skills.magic.spells.lunar.Humidify;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.BeigeProgressiveHitBar;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.CombatScriptsHandler;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.events.ServerLaunchEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kris | 14. jaan 2018 : 1:47.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * The Great Olm boss encountered at the end of every Chambers of Xeric run.
 */
@Slf4j
public final class GreatOlm extends RaidNPC<OlmRoom> implements CombatScript {

    /**
     * A constant defining the coverage of the entire Olm chamber.
     */
    public static final int ENTIRE_CHAMBER = 3;
    /**
     * Constants defining the possible phase types of Acid, Flame and Crystals.
     */
    static final int ACID = 0, FLAME = 1, CRYSTAL = 2;
    /**
     * A constant defining the southern part of the Olm room.
     */
    private static final int SOUTH = 0;

    /**
     * A constant defining the middle part of the Olm room.
     */
    private static final int MIDDLE = 1;

    /**
     * A constant defining the northern part of the Olm room.
     */
    private static final int NORTH = 2;
    /**
     * The crystal disappear animation upon Olm's death.
     */
    private static final Animation crystalDisappearAnimation = new Animation(7506);
    /**
     * The position of the blocking crystal object.
     */
    private static final Location blockingCrystalPosition = new Location(3232, 5749, 0);
    /**
     * The position of the crystal chest object.
     */
    private static final Location crystalChestPosition = new Location(3233, 5751, 0);
    /**
     * An array of combat spells which are able to douse the fire walls.
     */
    private static final CombatSpell[] waterSpells = new CombatSpell[]{
            CombatSpell.WATER_STRIKE, CombatSpell.WATER_BOLT, CombatSpell.WATER_BLAST, CombatSpell.WATER_WAVE, CombatSpell.WATER_SURGE, CombatSpell.ICE_BARRAGE
    };
    /**
     * An instance of humidify spell that is used to douse Olm's firewall on left click
     */
    private static NPCSpell humidifySpell;

    @Subscribe
    public static final void onServerLaunch(final ServerLaunchEvent event) {
        humidifySpell = Objects.requireNonNull(Magic.getSpell(Spellbook.LUNAR, "humidify", Humidify.class));
    }

    /**
     * The regular attack sequence that the Olm uses.
     */
    private static final OlmAttackType[] attackSequence = new OlmAttackType[]{
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.CRYSTAL_BURST,
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.LIGHTNING,
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.PORTALS,
    };
    /**
     * The special attack sequence that the Olm uses during the penultimate phase.
     */
    private static final OlmAttackType[] penultimateAttackSequence = new OlmAttackType[]{
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.CRYSTAL_BURST,
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.LIGHTNING,
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.PORTALS,
            OlmAttackType.AUTO_ATTACK, OlmAttackType.SKIPPED_ATTACK, OlmAttackType.AUTO_ATTACK,
            OlmAttackType.LEFT_CLAW_PROTECTION
    };
    /**
     * A list that contains currently on-going scripts for the olm.
     * At the end of each script, it's removed from this list.
     */
    @Getter
    private final List<Class<? extends OlmCombatScript>> scripts = new ArrayList<>();
    /**
     * The lower corner where the lightning is meant to start out at.
     */
    @Getter
    private final Location lightningLowerCorner;
    /**
     * The upper corner where the lightning is meant to start out at.
     */
    @Getter
    private final Location lightningUpperCorner;
    /**
     * The direction that Olm is currently facing.
     */
    @Getter
    private int direction = MIDDLE;
    /**
     * The number of ticks that the Olm has been alive for, used for the purpose of healing Olm every 5 ticks while its claws are alive.
     */
    private int ticks;
    /**
     * The claw restoration stage, used to determine what the status of the opposite claw is, whether it's alive, healing or both are compeletely dead.
     */
    @Getter
    @Setter
    private int restoreStage;
    /**
     * The falling crystals script that occurs between-phases and during penultimate phase, made into a local variable to be able to properly terminate when Olm falls.
     */
    private TransitionalFallingCrystals crystalScript;
    /**
     * The number of attacks the Olm has performed, goes in circles through the defined {@link GreatOlm#attackSequence} & {@link GreatOlm#penultimateAttackSequence}.
     */
    private int attackCounter;
    /**
     * Whether or not the Olm is currently using ranged as its attack; This is because Olm has an odd mechanic where it is more likely to use the last attack style, than to switch to
     * the opposite one.
     */
    @Getter
    @Setter
    private boolean ranging;
    /**
     * A variable which determines whether or not Olm was forced to turn its head due to everyone moving out of its sight. If this happens, and the next attack is not skipped, Olm
     * will fire out two attacks instead, to try and catch up with the sequence of attacks.
     */
    private boolean usedCycleScript;
    /**
     * The script of the last attack that the Olm performed.
     */
    private Class<? extends OlmCombatScript> lastAttack;
    /**
     * Whether or not the Olm skipped a basic attack due to everyone moving out of its visibility.
     */
    private boolean skippedBasic;
    /**
     * The deep burn attack, made into a local variable to remove players from the list of entities being burnt when they leave the Olm room.
     */
    @Getter
    @Setter
    private DeepBurn burnAttack;
    /**
     * A variable used to block Olm from dying more than once within a small time window, while one of the claws falls down but isn't quite dead yet.
     */
    private transient long deathDelay;
    /**
     * A variable used to delay cycle scripts, so that they do not occur back-to-back with no time in-between them. A 10-second delay is given to Olm before it can attack the players
     * with a special script.
     */
    private transient long cycleScriptDelay;

    public GreatOlm(final OlmRoom room, final Location tile) {
        super(room.getRaid(), room, NpcId.GREAT_OLM_7554, tile);
        this.room.getRaid().setOlm(this);
        lightningLowerCorner = room.getLocation(new Location(3228, 5731, 0));
        lightningUpperCorner = room.getLocation(new Location(3228, 5748, 0));
        setAggressionDistance(20);
        setAttackDistance(20);
        this.setForceAggressive(true);
        this.getTemporaryAttributes().put("special attacks during second to last phase", 0);
        for (val player : raid.getPlayers()) {
            player.putBooleanAttribute("PerfectOlm", true);
        }
        this.combat = new NPCCombat(this) {
            @Override
            public boolean process() {
                if (combatDelay > 0) {
                    combatDelay--;
                }
                if (combatDelay <= 0) {
                    val targets = everyone(ENTIRE_CHAMBER);
                    if (targets.isEmpty()) {
                        combatDelay = 4;
                        return true;
                    }
                    combatDelay = CombatScriptsHandler.specialAttack(npc, null);
                }
                return true;
            }
        };
    }

    /**
     * Attempts to douse the fire wall with one of the five available water spells defined in {@link GreatOlm#waterSpells} array
     * or using{@link GreatOlm#humidifySpell}, going from worst to best spell in order.
     *
     * @param player the player trying to douse the fire wall.
     * @param npc    the fire wall npc being doused.
     */
    public static final void douseFirewall(final Player player, final NPC npc) {
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player.getLocation(), OlmRoom.class, room -> {
            if (!room.inChamber(player.getLocation())) {
                player.sendMessage("You can't reach that.");
                return;
            }
            if (player.getEquipment().getId(EquipmentSlot.WEAPON) == 21015) {
                player.sendMessage("Your bulwark gets in the way.");
                return;
            }
            val spellbook = player.getCombatDefinitions().getSpellbook();
            if (!spellbook.equals(Spellbook.NORMAL) && !spellbook.equals(Spellbook.LUNAR)) {
                player.sendMessage("Only water spells or humidify can douse this flame!");
                return;
            }
            if (spellbook.equals(Spellbook.LUNAR)) {
                val humidify = Objects.requireNonNull(humidifySpell);
                val state = new SpellState(player, humidify.getLevel(), humidify.getRunes());
                val castSuccessfully = state.check(false);
                humidify.execute(player, npc);
                if (castSuccessfully) {
                    return;
                }
            } else {
                for (val spell : waterSpells) {
                    val state = new SpellState(player, spell);
                    if (!state.check(false)) {
                        continue;
                    }
                    douse(player, npc, spell);
                    return;
                }
            }
            player.sendMessage("Only water spells or humidify can douse this flame!");
        }));
    }

    /**
     * Douses the fire wall npc using the specified combat spell.
     *
     * @param player the player dousing the fire wall.
     * @param npc    the fire wall being doused.
     * @param spell  the combat spell being used to douse the fire wall.
     */
    public static final void douse(@NotNull final Player player, @NotNull final NPC npc, @NotNull final CombatSpell spell) {
        player.faceEntity(npc);
        if (!ArrayUtils.contains(waterSpells, spell)) {
            player.sendMessage("Only water spells can douse this flame!");
            return;
        }
        val state = new SpellState(player, spell);
        if (!state.check(true)) {
            return;
        }
        state.remove();
        val sp = spell.getProjectile();
        val projectile = new Projectile(sp.getGraphicsId(), sp.getStartHeight(), sp.getEndHeight(), sp.getDelay(), sp.getAngle(), 9, sp.getDistanceOffset(), 0);
        World.sendProjectile(player, npc, projectile);
        player.getSkills().addXp(Skills.MAGIC, spell.getExperience());
        if (spell.getCastSound() != null) {
            player.getPacketDispatcher().sendSoundEffect(spell.getCastSound());
        }
        val animation = spell.getAnimation();
        val graphics = spell.getCastGfx();
        if (animation != null) {
            player.setAnimation(animation);
        }
        if (graphics != null) {
            player.setGraphics(graphics);
        }
        val gfx = spell.getHitGfx();
        if (gfx != null) {
            World.sendGraphics(new Graphics(gfx.getId(), 60, gfx.getHeight()), new Location(npc.getLocation()));
        }

        val hitSound = spell.getHitSound();
        if (hitSound != null) {
            World.sendSoundEffect(npc.getLocation(), new SoundEffect(hitSound.getId(), hitSound.getRadius(), 60));
        }
        WorldTasksManager.schedule(() -> {
            player.sendMessage("You douse the flame.");
            npc.finish();
        }, 1);
    }

    @Override
    protected void setStats() {
        super.setStats();
        hitpointsMultiplier = getRaid().getOriginalPlayers().size();
        combatDefinitions.setHitpoints((int) Math.floor(400 + (hitpointsMultiplier * 400)));
        setHitpoints(combatDefinitions.getHitpoints());
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        return false;
    }

    @Override
    public boolean checkProjectileClip(final Player player) {
        return false;
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.2;
    }

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.2;
    }

    /**
     * Olm heals every 5th tick if it has been damaged, unless it is the final stand phase, where you're actually fighting the Olm.
     */
    @Override
    public void processNPC() {
        super.processNPC();
        if (getHitpoints() < getMaxHitpoints() && !isFinalStand() && ++ticks % 5 == 0) {
            val damage = getMaxHitpoints() - getHitpoints();
            setHitpoints(getMaxHitpoints());
            applyHit(new Hit(damage, HitType.HEALED));
        }
    }

    public void applyHit(final Hit hit) {
        if (hit.getHitType() == HitType.REGULAR) {
            hit.setDamage(0);
        }
        super.applyHit(hit);
    }

    public float getXpModifier(Hit hit) {
        return hit.getHitType() == HitType.REGULAR ? 0 : 1;
    }

    public float getPointsMultiplier(final Hit hit) {
        return !isFinalStand() ? 0F : getXpModifier(hit);
    }

    /**
     * Schedules a restoration task for the claw NPC if applicable; The players then have 20 seconds to defeat the other claw, before this restores to full health. Only occurs during
     * last phase.
     *
     * @param npc the claw npc which died and is being restored.
     */
    void scheduleClawRestoreTask(final NPC npc) {
        if (!isPenultimatePhase()) {
            return;
        }
        if (getRoom().getLeftClaw() == null && getRoom().getRightClaw() == null) {
            return;
        }
        WorldTasksManager.schedule(new WorldTask() {
            private final NPC dummy = new NPC(7556, new Location(npc.getLocation()), Direction.EAST, 0).spawn();
            private int ticks;

            @Override
            public void run() {
                if (getRoom().getRaid().isDestroyed()) {
                    stop();
                    return;
                }
                if (getRoom().getLeftClaw() == null && getRoom().getRightClaw() == null) {
                    stop();
                    return;
                }
                if (restoreStage == 2) {
                    val leftClaw = room.getLeftClaw();
                    val rightClaw = room.getRightClaw();
                    WorldTasksManager.schedule(() -> {
                        if (leftClaw != null) {
                            leftClaw.spawnClawlessObject();
                        }
                        if (rightClaw != null) {
                            rightClaw.spawnClawlessObject();
                        }
                    }, 5);
                    room.setLeftClaw(null);
                    room.setRightClaw(null);
                    crystalScript = new TransitionalFallingCrystals(Integer.MAX_VALUE);
                    crystalScript.handle(GreatOlm.this);
                    stop();
                    return;
                }
                if (ticks == 0) {
                    if (npc == getRoom().getLeftClaw()) {
                        getRoom().sendAnimation(getRoom().getLeftClawObject(), OlmAnimation.LEFT_CLAW_INVISIBLE);
                    } else {
                        getRoom().sendAnimation(getRoom().getRightClawObject(), OlmAnimation.RIGHT_CLAW_INVISIBLE);
                    }
                    npc.finish();
                }

                if (ticks == 34) {
                    npc.spawn();
                    npc.setCantInteract(false);
                    restoreStage = 0;
                    if (npc == getRoom().getLeftClaw()) {
                        room.sendAnimation(room.getLeftClawObject(), OlmAnimation.LEFT_CLAW_RISE);
                    } else {
                        room.sendAnimation(room.getRightClawObject(), OlmAnimation.RIGHT_CLAW_RISE);
                    }
                    if (npc instanceof LeftClaw) {
                        ((LeftClaw) npc).setRegenerated(true);
                    } else if (npc instanceof RightClaw) {
                        ((RightClaw) npc).setRegenerated(true);
                    }
                    npc.setHitpoints(npc.getMaxHitpoints());
                    npc.applyHit(new Hit(npc.getMaxHitpoints(), HitType.HEALED));
                    dummy.finish();
                    stop();
                    return;
                }
                final BeigeProgressiveHitBar hitbar = new BeigeProgressiveHitBar((int) (ticks * 3.7f));
                dummy.getHitBars().add(hitbar);
                dummy.getUpdateFlags().flag(UpdateFlag.HIT);
                ticks++;
            }
        }, 0, 0);
    }

    /**
     * Whether the current phase is the penultimate phase or not.
     */
    final boolean isPenultimatePhase() {
        return room.getCurrentPhase() == room.getTotalPhases();
    }

    /**
     * Whether the stage of the fight is the final stand - olm's head only.
     */
    public final boolean isFinalStand() {
        return isPenultimatePhase() && room.getLeftClaw() == null && room.getRightClaw() == null;
    }

    /**
     * Checks whether the left claw is alive and available or not.
     *
     * @return whether or not the left claw is attackable.
     */
    private final boolean isLeftClawUnavailable() {
        return room.getLeftClaw() == null || room.getLeftClaw().isClenched() || room.getLeftClaw().isCantInteract();
    }

    /**
     * Clears the list of players and repopulates if with those who're within
     * the chamber; dead and finished players aren't included.
     * Re-uses the same list over and over to save CPU & memory.
     *
     * @return list of players.
     */
    public final List<Player> everyone(final int coverage) {
        val players = new ObjectArrayList<Player>();
        for (val p : room.getRaid().getPlayers()) {
            if (p == null || p.isNulled() || p.isDead() || p.isFinished() || !p.isRunning()
                    || (coverage == SOUTH && !south(p.getLocation())
                    || coverage == MIDDLE && !middle(p.getLocation())
                    || coverage == NORTH && !north(p.getLocation()))) {
                continue;
            }
            if (room.inChamber(p.getLocation())) {
                players.add(p);
            }
        }
        return players;
    }

    /**
     * @return coordinates to where the head of the olm lies.
     */
    public Location getFaceCoordinates() {
        final Location tile = room.getSide() == OlmRoom.LEFT ? room.getLeftSideHead() : room.getRightSideHead();
        switch (direction) {
            case SOUTH:
                return new Location(tile.getX(), tile.getY() - 1, tile.getPlane());
            case NORTH:
                return new Location(tile.getX(), tile.getY() + 1, tile.getPlane());
            default:
                return tile;
        }
    }

    /**
     * Repopulates the list of players and picks a random player from it.
     *
     * @return random player from available players.
     */
    public final Player random(final int coverage) {
        final List<Player> players = everyone(coverage);
        if (players.isEmpty()) {
            return null;
        }
        return players.get(Utils.random(players.size() - 1));
    }

    @Override
    public void sendDeath() {
        if (!isFinalStand()) {
            setHitpoints(1);
            return;
        }
        if (deathDelay > System.currentTimeMillis()) {
            return;
        }
        deathDelay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        room.queueOrExecute(this::executeDeath);
    }

    /**
     * Executes the death sequence of the Olm, when all claws have been defeated. Rewards all the players in the raid after falling. Clears the path to the reward chest upon falling.
     * Sets the raid to a 'completed' status and lists the raid on the challenge mode scoreboard, if applicable.
     */
    private final void executeDeath() {
        try {
            //Give everyone immunity from olm's damage once it dies.
            for (val player : everyone(ENTIRE_CHAMBER)) {
                player.setProtectionDelay(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));
            }
            resetWalkSteps();
            combat.removeTarget();
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        WorldTasksManager.schedule(new WorldTask() {
            private int loop;

            @Override
            public void run() {
                if (getRoom().getRaid().isDestroyed()) {
                    stop();
                    return;
                }
                if (loop == 0) {
                    try {
                        final WorldObject blockingCrystal = World.getObjectWithType(GreatOlm.this.getRoom().getLocation(blockingCrystalPosition), 10);
                        if (blockingCrystal != null) {
                            for (final Player p : room.getRaid().getPlayers()) {
                                try {
                                    p.getVarManager().sendBit(5425, 5);
                                    p.sendMessage("As the Great Olm collapses, the crystal blocking your exit has been shattered.");
                                    p.getMusic().stop();
                                    p.getMusic().playJingle(152);
                                    p.getAchievementDiaries().update(KourendDiary.COMPLETE_A_RAID);
                                } catch (Exception e) {
                                    log.error(Strings.EMPTY, e);
                                }
                            }
                            World.sendObjectAnimation(blockingCrystal, crystalDisappearAnimation);
                            WorldTasksManager.schedule(() -> World.removeObject(blockingCrystal), 2);
                        }
                        room.setLeaveTime(WorldThread.WORLD_CYCLE);
                        if (crystalScript != null) {
                            crystalScript.setStopped(true);
                        }
                        room.sendAnimation(room.getHeadObject(), OlmAnimation.HEAD_FALL);
                        finish();
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                } else if (loop == 1) {
                    try {
                        final int side = room.getSide();
                        World.spawnObject(new WorldObject(29882, 10, side == OlmRoom.LEFT ? OlmRoom.leftSideHeadObject.getRotation() : OlmRoom.rightSideheaDObject.getRotation(), room.getLocation(side == OlmRoom.LEFT ? OlmRoom.leftSideHeadObject : OlmRoom.rightSideheaDObject)));
                        reset();
                        stop();
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                    try {
                        val raid = room.getRaid();
                        raid.setRewards(new RaidRewards(room.getRaid()));
                        val milliseconds = Utils.currentTimeMillis() - raid.getStartTime();
                        raid.setDuration((int) TimeUnit.MILLISECONDS.toTicks(milliseconds));
                        val rewards = room.getRaid().getRewards();
                        rewards.generate();
                        raid.setComplete();

                        val object = World.getObjectWithType(room.getLocation(crystalChestPosition), 10);
                        if (object != null) {
                            val obj = new WorldObject(object);
                            obj.setId(30028);
                            World.spawnObject(obj);
                        }
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                    try {
                        Scoreboard.addScore(raid.getParty().getChannel(), raid.getDuration());
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
                loop++;
            }
        }, 0, 1);
    }

    /**
     * Whether the given direction coverage contains a player or not.
     *
     * @param direction constant direction
     * @return whether a player was found in the coverage.
     */
    private boolean containsPlayer(final int direction) {
        for (final Player player : room.getRaid().getPlayers()) {
            if (player == null || player.isDead() || player.isFinished() || !player.isRunning()) {
                continue;
            }
            if (direction == SOUTH && south(player.getLocation())
                    || direction == MIDDLE && middle(player.getLocation())
                    || direction == NORTH && north(player.getLocation())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Forces the olm to look in a specific direction.
     *
     * @param direction the direction constant where the olm needs to look.
     */
    private void faceDirection(final int direction) {
        if (direction == this.direction) {
            return;
        }
        switch (direction) {
            case SOUTH:
                if (this.direction == NORTH) {
                    room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_LEFT_TO_RIGHT : OlmAnimation.HEAD_RIGHT_TO_LEFT);
                } else {
                    room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_MIDDLE_TO_RIGHT : OlmAnimation.HEAD_MIDDLE_TO_LEFT);
                }
                break;
            case MIDDLE:
                if (this.direction == SOUTH) {
                    room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_RIGHT_TO_MIDDLE : OlmAnimation.HEAD_LEFT_TO_MIDDLE);
                } else {
                    room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_LEFT_TO_MIDDLE : OlmAnimation.HEAD_RIGHT_TO_MIDDLE);
                }
                break;
            default:
                if (this.direction == SOUTH) {
                    room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_RIGHT_TO_LEFT : OlmAnimation.HEAD_LEFT_TO_RIGHT);
                } else {
                    room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_MIDDLE_TO_LEFT : OlmAnimation.HEAD_MIDDLE_TO_RIGHT);
                }
                break;
        }
        this.direction = direction;
        resetHeadStance();
    }

    /**
     * Resets the head stance to the default stance based on the direction the head will be facing.
     */
    private void resetHeadStance() {
        val latestAnimation = room.getLatestHeadAnimation();
        WorldTasksManager.schedule(() -> {
            if (room.getLatestHeadAnimation() != latestAnimation) {
                return;
            }
            room.sendAnimation(room.getHeadObject(), direction == SOUTH ? (room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_RIGHT_DEFAULT : OlmAnimation.HEAD_LEFT_DEFAULT) : direction == MIDDLE ? OlmAnimation.HEAD_MIDDLE_DEFAULT : (room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_LEFT_DEFAULT : OlmAnimation.HEAD_RIGHT_DEFAULT));
        }, 1);
    }

    /**
     * Goes over all the players in the room and gets the side with the most players.
     *
     * @return constant value for the side with most players.
     */
    private int getSideWithMostPlayers(final boolean extraLeft, final boolean extraRight) {
        float southern = 0F, middle = 0F, northern = 0F;
        for (final Player p : room.getRaid().getPlayers()) {
            if (p == null || p.isDead() || p.isFinished() || !p.isRunning()) {
                continue;
            }
            if (middle(p.getLocation())) {
                middle++;
            }
            if (northernHeadBit(p.getX(), p.getY())) {
                northern++;
            }
            if (southernHeadBit(p.getX(), p.getY())) {
                southern++;
            }
        }
        val leftSide = room.getSide() == OlmRoom.LEFT;
        if (((leftSide && extraLeft) || (!leftSide && extraRight))) {
            northern += 1F;
        }
        if (((leftSide && extraRight) || (!leftSide && extraLeft))) {
            southern += 1F;
        }
        if (leftSide) {
            return middle >= southern && middle >= northern ? MIDDLE : northern >= southern ? NORTH : SOUTH;
        }
        return middle >= southern && middle >= northern ? MIDDLE : southern >= northern ? SOUTH : NORTH;
    }

    /**
     * Performs the attack animation on the head object, and resets the stance to the default stance afterwards.
     */
    public void performAttack() {
        switch (direction) {
            case SOUTH:
                room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_RIGHT_ATTACK : OlmAnimation.HEAD_LEFT_ATTACK);
                break;
            case MIDDLE:
                room.sendAnimation(room.getHeadObject(), OlmAnimation.HEAD_MIDDLE_ATTACK);
                break;
            default:
                room.sendAnimation(room.getHeadObject(), room.getSide() == OlmRoom.LEFT ? OlmAnimation.HEAD_LEFT_ATTACK : OlmAnimation.HEAD_RIGHT_ATTACK);
                break;
        }
        resetHeadStance();
    }

    /**
     * Whether the location is within the southern coverage of the chamber.
     *
     * @param tile the location to check
     * @return whether or not the tile is within southern part of the chamber.
     */
    public boolean south(final Location tile) {
        return south(tile.getX(), tile.getY());
    }

    /**
     * Whether the location is within the northern coverage of the chamber.
     *
     * @param tile the location to check
     * @return whether or not the tile is within northern part of the chamber.
     */
    public boolean north(final Location tile) {
        return north(tile.getX(), tile.getY());
    }

    /**
     * Whether the location is within the middle coverage of the chamber.
     *
     * @param tile the location to check
     * @return whether or not the tile is within the middle part of the chamber.
     */
    public boolean middle(final Location tile) {
        return middle(tile.getX(), tile.getY());
    }

    /**
     * Whether the location is within the southern coverage of the chamber.
     *
     * @param x the x coordinate to check.
     * @param y the y coordinate to check.
     * @return whether or not the tile is within southern part of the chamber.
     */
    public boolean south(final int x, final int y) {
        return x >= room.getLeftCorner().getX() && x <= room.getSeparatingLine().getX()
                && y >= room.getLeftCorner().getY() && y <= room.getSeparatingLine().getY();
    }

    /**
     * Whether the location is within the northern coverage of the chamber.
     *
     * @param x the x coordinate to check.
     * @param y the y coordinate to check.
     * @return whether or not the tile is within northern part of the chamber.
     */
    public boolean north(final int x, final int y) {
        return x >= room.getLeftCorner().getX() && x <= room.getSeparatingLine().getX()
                && y >= room.getSeparatingLine().getY() && y <= room.getRightCorner().getY();
    }

    /**
     * Whether the location is within the southern bit of the room, excluding the separation line row.
     *
     * @param x the x coordinate to check.
     * @param y the y coordinate to check.
     * @return whether or not the tile is within the southern part of the chamber, excluding separation line row.
     */
    private boolean southernHeadBit(final int x, final int y) {
        return x >= room.getLeftCorner().getX() && x <= room.getSeparatingLine().getX()
                && y >= room.getLeftCorner().getY() && y < room.getSeparatingLine().getY();
    }

    /**
     * Whether the location is within the northern bit of the room, excluding the separation line row.
     *
     * @param x the x coordinate to check.
     * @param y the y coordinate to check.
     * @return whether or not the tile is within the northern part of the chamber, excluding separation line row.
     */
    private boolean northernHeadBit(final int x, final int y) {
        return x >= room.getLeftCorner().getX() && x <= room.getSeparatingLine().getX()
                && y > room.getSeparatingLine().getY() && y <= room.getRightCorner().getY();
    }

    /**
     * Whether the location is within the middle coverage of the chamber.
     *
     * @param x the x coordinate to check.
     * @param y the y coordinate to check.
     * @return whether or not the tile is within middle part of the chamber.
     */
    public boolean middle(final int x, final int y) {
        return x >= room.getLeftCorner().getX() && x <= room.getSeparatingLine().getX()
                && y > room.getSouthernMiddleLine().getY() && y < room.getNorthernMiddleLine().getY();
    }

    /**
     * Gets a random location within the chamber.
     *
     * @return a random location within the boundaries of the specified coverage.
     */
    public final Location randomLocation(final int coverage) {
        int trycount = 50;
        while (trycount > 0) {
            trycount--;
            final int x = Utils.random(room.getLeftCorner().getX(), room.getRightCorner().getX());
            final int y = Utils.random(room.getLeftCorner().getY(), room.getRightCorner().getY());
            if (World.isTileFree(x, y, 0, 1)) {
                if (coverage == ENTIRE_CHAMBER || coverage == NORTH && north(x, y) || coverage == MIDDLE && middle(x, y) || coverage == SOUTH && south(x, y)) {
                    return new Location(x, y, 0);
                }
            }
        }
        return null;
    }

    @Override
    public int attack(final Entity target) {
        if (isDead()) {
            return 0;
        }
        val leftClaw = room.getLeftClaw();
        val rightClaw = room.getRightClaw();
        val clench = leftClaw != null && leftClaw.shouldClench();
        val extraLeft = leftClaw != null && leftClaw.clearCalculatableDamage();
        val extraRight = rightClaw != null && rightClaw.clearCalculatableDamage();
        if (clench) {
            leftClaw.checkIfClenching();
        }
        val attackCounter = this.attackCounter++;
        val array = this.isPenultimatePhase() || this.isFinalStand() ? penultimateAttackSequence : attackSequence;
        if (attackCounter >= (array.length - 1)) {
            this.attackCounter = 0;
        }
        val attackType = array[attackCounter];
        if (!containsPlayer(getDirection()) || ((attackType == OlmAttackType.SKIPPED_ATTACK
                || attackType == OlmAttackType.CRYSTAL_BURST || attackType == OlmAttackType.LIGHTNING || attackType == OlmAttackType.PORTALS || attackType == OlmAttackType.LEFT_CLAW_PROTECTION)
                && !skippedBasic)) {
            if (attackType == OlmAttackType.AUTO_ATTACK) {
                this.skippedBasic = true;
            }

            val dir = getSideWithMostPlayers(extraLeft, extraRight);
            val currentDirection = getDirection();
            if (dir != currentDirection) {
                if (attackType == OlmAttackType.CRYSTAL_BURST || attackType == OlmAttackType.LIGHTNING || attackType == OlmAttackType.PORTALS || attackType == OlmAttackType.LEFT_CLAW_PROTECTION) {
                    if (containsPlayer(currentDirection)) {
                        val skipped = this.skippedBasic;
                        this.skippedBasic = false;
                        performClawSpecial(skipped, attackType);
                    }
                }
                faceDirection(dir);
                return 4;
            }

            if (attackType == OlmAttackType.SKIPPED_ATTACK) {
                return 4;
            }
        }
        val skipped = this.skippedBasic;
        this.skippedBasic = false;
        if (attackType == OlmAttackType.AUTO_ATTACK) {
            performAutoAttack();
        } else {
            return performClawSpecial(skipped, attackType);
        }
        return 4;
    }

    private int performClawSpecial(final boolean skipped, final OlmAttackType attackType) {
        usedCycleScript = false;
        if (skipped) {
            performAutoAttack();
        }
        if (isLeftClawUnavailable()) {
            return 4;
        }
        if (isPenultimatePhase() && !isFinalStand()) {
            this.getTemporaryAttributes().put("special attacks during second to last phase",
                    Integer.parseInt(this.getTemporaryAttributes().get("special attacks during second to last phase").toString()) + 1);
            for (Player p:raid.getPlayers()) {
                if (!p.getBooleanAttribute("master-combat-achievement17") && raid.getOriginalPlayers().size() == 1) {
                    p.sendMessage("You failed the A Not So Special Lizard task because Olm did one of 4 banned special attacks during the penultimate phase.");
                }
            }
        }
        switch (attackType) {
            case CRYSTAL_BURST:
                execute(new CrystalBurst());
                break;
            case LIGHTNING:
                execute(new Lightning());
                break;
            case PORTALS:
                execute(new Swap());
                break;
            case LEFT_CLAW_PROTECTION:
                execute(new LeftClawProtection());
                break;
        }
        return 4;
    }

    /**
     * Picks one of the available pseudo-random attacks and uses it agains the players in its visibility.
     * Has a 1:20 chance to perform the sphere attack, as long as the last attack it used wasn't the sphere attack.
     * Has a 1:16 chance to use the life siphoning attack during the final stand phase.
     * Has a 1:5 chance of using a random phase script - out of the six available scripts - as long as at least ten seconds have passed since the last time it used a script attack.
     * If all of the above fails, will used either a ranged or a magic attack, while being more likely to use the style it used last.
     */
    private void performAutoAttack() {
        if (lastAttack != Sphere.class && Utils.random(19) == 0) {
            execute(new Sphere());
        } else if (isFinalStand() && Utils.random(15) == 0) {
            execute(new LifeSiphon());
        } else if (cycleScriptDelay < System.currentTimeMillis() && !this.usedCycleScript && Utils.random(4) == 0) {
            this.usedCycleScript = true;
            cycleScriptDelay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
            execute(randomPhaseScript());
        } else {
            execute(new StandardAttack());
        }
    }

    /**
     * Executes the script specified and sets the last attack script variable.
     *
     * @param script the attack script.
     */
    private void execute(final OlmCombatScript script) {
        script.handle(this);
        this.lastAttack = script.getClass();
    }

    /**
     * Picks a random phase script that matches the necessary predicates. Can only use scripts from the element it rose with, except for the last phase when all attacks are on the
     * table.
     *
     * @return a random phase script to execute.
     */
    private final OlmCombatScript randomPhaseScript() {
        val random = Utils.random(1);
        val phase = this.isPenultimatePhase() ? Utils.random(2) : room.getPhase();
        switch (phase) {
            case ACID:
                return random == 0 && !this.scripts.contains(AcidDrip.class) ? new AcidDrip() : new AcidSpray();
            case FLAME:
                return random == 0 && !this.scripts.contains(DeepBurn.class) ? new DeepBurn() : new FireWall();
            case CRYSTAL:
                if (random == 0 && !this.scripts.contains(FallingCrystal.class)) {
                    final List<Player> everyone = everyone(getDirection());
                    if (!everyone.isEmpty()) {
                        return new FallingCrystal(Utils.getRandomCollectionElement(everyone));
                    }
                }
                return new CrystalBomb();
        }
        throw new IllegalStateException();
    }

    /**
     * An enum containing all the possible types of Olm's attacks.
     */
    private enum OlmAttackType {
        AUTO_ATTACK,
        SKIPPED_ATTACK,
        CRYSTAL_BURST,
        LIGHTNING,
        PORTALS,
        LEFT_CLAW_PROTECTION
    }
}
