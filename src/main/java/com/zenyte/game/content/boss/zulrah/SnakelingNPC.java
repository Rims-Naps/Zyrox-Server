package com.zenyte.game.content.boss.zulrah;

import com.zenyte.game.RuneDate;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 17. march 2018 : 17:30.41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class SnakelingNPC extends NPC implements CombatScript {

	private static final Animation ANIM = new Animation(2413);
	private static final SoundEffect IMPACT = new SoundEffect(1930, 5);
	private int ticks;

	public SnakelingNPC(final ZulrahNPC zulrah, final Player player, final Location tile) {
		super(Utils.random(3) == 0 ? NpcId.SNAKELING_2046 : NpcId.SNAKELING, tile, Direction.SOUTH, 5);
		setForceMultiArea(true);
		setAggressionDistance(25);
		setAttackDistance(6);
		setMaxDistance(25);
		setSpawned(true);
		lock(3);
		setAnimation(ANIM);
        World.sendSoundEffect(tile, IMPACT);
		this.player = player;
		this.zulrah = zulrah;
		this.supplyCache = false;
	}

	@Getter
	private final ZulrahNPC zulrah;
	private final Player player;

	@Override
	public void processNPC() {
	    if (zulrah.isStopped()) {
	        return;
        }
	    if (++ticks >= 67) {
	        this.applyHit(new Hit(1, HitType.REGULAR));
	        return;
        }
		if (isLocked() || isDead()) {
			return;
		}
		if (combat.getTarget() != player) {
			combat.setTarget(player);
		}
		combat.process();
	}

	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		zulrah.getSnakelings().remove(this);
		if (source instanceof Player ) {
			if (!player.getBooleanAttribute("elite-combat-achievement39")) {
				val oldSnakelingCount = player.getAttributes().getOrDefault("amount_of_snakeling_killed", "0").toString();
				player.getAttributes().put("amount_of_snakeling_killed", oldSnakelingCount.contains("0") ? "1" : oldSnakelingCount.contains("1") ? "2" : "3");
				val newSnakelingCount = player.getAttributes().get("amount_of_snakeling_killed").toString();
				if (newSnakelingCount.equals("1")) {
					player.getAttributes().put("time_when_first_snakeling_was_killed", RuneDate.currentTimeMillis());
				} else if (newSnakelingCount.equals("3")) {
					long timeOfFirstKill = 0;
					if (player.getAttributes().get("time_when_first_snakeling_was_killed") instanceof Long) {
						timeOfFirstKill = (long) player.getAttributes().get("time_when_first_snakeling_was_killed");
					}
					val timeNow = RuneDate.currentTimeMillis();
					if (timeOfFirstKill + 500L >= timeNow) {
						player.putBooleanAttribute("elite-combat-achievement39", true);
						EliteTasks.sendEliteCompletion(player, 39);
					}
					player.sendMessage("You killed the first and last snakeling " + Colour.RED.wrap((int) (timeNow - timeOfFirstKill)) + " milliseconds apart");
					player.getAttributes().put("amount_of_snakeling_killed", "0");
				}
			}
		}
	}

    private static final SoundEffect PROJ_SOUND = new SoundEffect(224, 15);
    private static final SoundEffect IMPACT_SOUND = new SoundEffect(794, 2770, 15);
    private static final SoundEffect MELEE_SOUND = new SoundEffect(794, 15);

    private static final Projectile MAGIC_PROJ = new Projectile(1230, 15, 16, 30, 15, 18, 0, 5);

    @Override
	public int attack(final Entity target) {
        setAnimation(combatDefinitions.getAttackDefinitions().getAnimation());
	    if (id == 2045) {
            zulrah.delayHit(0, new Hit(this, getRandomMaxHit(this, 15, MELEE, target), HitType.MELEE));
            World.sendSoundEffect(this, MELEE_SOUND);
        } else {
	        World.sendSoundEffect(this, PROJ_SOUND);
	        zulrah.delayHit(World.sendProjectile(this, target, MAGIC_PROJ), new Hit(this, getRandomMaxHit(this, 15, MAGIC, target), HitType.MAGIC));
            World.sendSoundEffect(target, new SoundEffect(IMPACT_SOUND.getId(), IMPACT_SOUND.getRadius(), MAGIC_PROJ.getProjectileDuration(this.getLocation(), target.getLocation())));
        }
		return 4;
	}

}
