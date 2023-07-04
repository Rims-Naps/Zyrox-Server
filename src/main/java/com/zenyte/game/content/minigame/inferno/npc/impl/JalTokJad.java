package com.zenyte.game.content.minigame.inferno.npc.impl;

import com.zenyte.game.RuneDate;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.minigame.inferno.model.InfernoWave;
import com.zenyte.game.content.minigame.inferno.npc.InfernoNPC;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Tommeh | 29/11/2019 | 19:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class JalTokJad extends InfernoNPC {

    private static final Animation meleeAnimation = new Animation(7590);
    private static final Animation rangedAnimation = new Animation(7593);
    private static final Animation magicAnimation = new Animation(7592);
    private static final Graphics rangedGfx = new Graphics(451);
    private static final Graphics magicGfx = new Graphics(157, 0, 96);
    private static final SoundEffect meleeAttackSound = new SoundEffect(408);
    private static final SoundEffect rangedAttackSound = new SoundEffect(163);
    private static final SoundEffect magicAttackSound = new SoundEffect(162);
    private static final SoundEffect magicLandSound = new SoundEffect(163);
    private static final Projectile magicHeadProj = new Projectile(448, 140, 20, 70, 5, 100, 0, 0);
    private static final Projectile magicBodyProj = new Projectile(449, 140, 20, 75, 5, 100, 0, 0);
    private static final Projectile magicTrailProj = new Projectile(450, 140, 20, 80, 5, 100, 0, 0);

    private static final Projectile[] magicProjectiles = new Projectile[]{magicHeadProj, magicBodyProj, magicTrailProj};

    private final int maximumHealth = getMaxHitpoints() >> 1;

    private final List<YtHurKot> healers = new ArrayList<>(5);

    private static final Location[] wave69HealerLocations = {
            new Location(2270, 5352, 0), new Location(2270, 5353, 0),
            new Location(2272, 5352, 0)
    };

    public JalTokJad(final Location location, final Inferno inferno) {
        super(NpcId.JALTOKJAD, location, inferno);
        setAttackDistance(15);
    }

    @Override
    protected void postHitProcess() {
        if (isDead()) {
            return;
        }
        if (!spawned && getHitpoints() < maximumHealth) {
            spawned = true;
            val count = inferno.getNPCs(YtHurKot.class).size();
            val maxCount = inferno.getWave().equals(InfernoWave.WAVE_67) ? 5 : 3;
            for (int index = 0; index < (maxCount - count); index++) {
                val location = inferno.getWave().equals(InfernoWave.WAVE_69) ? inferno.getLocation(wave69HealerLocations[index]) : getHealerLocation();
                val healer = new YtHurKot(location, inferno, this);
                healer.spawn();
                healer.faceEntity(this);
                healers.add(healer);
            }
        }
    }

    private Location getHealerLocation() {
        val optionalLocation = Utils.findEmptySquare(getLocation(), inferno.getWave().equals(InfernoWave.WAVE_67) ? 9 : 6, 1,
                Optional.of(l -> {
                    val xOffset = Utils.random(1) == 0 ? Utils.random(2) : Utils.random(7, 9);
                    val yOffset = Utils.random(1) == 0 ? Utils.random(2) : Utils.random(7, 9);
                    l.setLocation(l.transform(xOffset, yOffset, 0));
                    var occupied = false;
                    for (val healer : healers) {
                        if (healer.getLocation().matches(l)) {
                            occupied = true;
                            break;
                        }
                    }
                    val distance = l.getTileDistance(getLocation());
                    return !occupied && distance >= 4;
        }));
        return optionalLocation.orElseGet(() -> getLocation().transform(Utils.random(3), Utils.random(3), 0));
    }

    @Override
    public boolean isFlinchable() {
        return false;
    }

    @Override
    public void heal(final int amount) {
        super.heal(amount);
        if (getHitpoints() >= getMaxHitpoints()) {
            spawned = false;
        }
    }

    @Override
    public int attack(final Entity target) {
        val style = Utils.random(isWithinMeleeDistance(this, target) ? 2 : 1);
        if (style == 2) {
            inferno.playSound(meleeAttackSound);
            setAnimation(meleeAnimation);
            delayHit(0, target, new Hit(this, getRandomMaxHit(this, 97, STAB, target), HitType.MELEE));
        } else if (style == 1) {
            setAnimation(rangedAnimation);
            WorldTasksManager.schedule(() -> {
                inferno.playSound(rangedAttackSound);
                delayHit(2, target, new Hit(this, getRandomMaxHit(this, 97, RANGED, target), HitType.RANGED));
                target.setGraphics(rangedGfx);
                WorldTasksManager.schedule(() -> target.setGraphics(magicGfx), 1);
            }, 2);
        } else {
            inferno.playSound(magicAttackSound);
            setAnimation(magicAnimation);
            for (val projectile : magicProjectiles) {
                World.sendProjectile(this, target, projectile);
            }
            WorldTasksManager.schedule(() -> target.setGraphics(magicGfx), 5);
            WorldTasksManager.schedule(() -> delayHit(3, target,
                    new Hit(this, getRandomMaxHit(this, 97, MAGIC, target), HitType.MAGIC).onLand(h -> inferno.playSound(magicLandSound))), 2);
        }
        return combatDefinitions.getAttackSpeed();
    }

    @Override
    protected void onDeath(Entity source) {
        super.onDeath(source);
        for (val healer : healers) {
            healer.sendDeath();
        }
        healers.clear();
        val player = inferno.getPlayer();
        if (inferno.getWave() == InfernoWave.WAVE_68 && !player.getBooleanAttribute("grandmaster-combat-achievement5")) {
            val oldFlowerCount = player.getAttributes().getOrDefault("amount_of_jads_killed", "0").toString();
            player.getAttributes().put("amount_of_jads_killed", oldFlowerCount.contains("0") ? "1" : oldFlowerCount.contains("1") ? "2" : "3" );
            val newFlowerCount = player.getAttributes().get("amount_of_jads_killed").toString();
            if (newFlowerCount.equals("1")) {
                player.getAttributes().put("time_when_first_jad_was_killed", RuneDate.currentTimeMillis());
            } else if (newFlowerCount.equals("3")) {
                long timeOfFirstKill = 0;
                if (player.getAttributes().get("time_when_first_jad_was_killed") instanceof Long) {
                    timeOfFirstKill = (long) player.getAttributes().get("time_when_first_jad_was_killed");
                }
                val timeNow = RuneDate.currentTimeMillis();
                if (timeOfFirstKill + 30000L >= timeNow) {
                    player.putBooleanAttribute("grandmaster-combat-achievement5", true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, 5);
                }
                player.sendMessage("You killed the first and last jad " + Colour.RED.wrap((int) (timeNow - timeOfFirstKill)) + " milliseconds apart");
                player.getAttributes().put("amount_of_jads_killed", "0");
            }
        }
        if (inferno != null) {
            if (inferno.getWave() == InfernoWave.WAVE_69 && !player.getBooleanAttribute("grandmaster-combat-achievement14")) {
                player.putBooleanAttribute("jadOnWave69Died", true);
            }
        }
    }
}
