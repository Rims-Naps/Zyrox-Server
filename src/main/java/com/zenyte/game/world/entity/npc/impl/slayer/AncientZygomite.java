package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.item.enums.FungicideSpray;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCTileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import lombok.val;

/**
 * @author Kris | 19/06/2019 11:14
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AncientZygomite extends NPC implements Spawnable, CombatScript {

    private boolean messageSent;
    private final int original;
    private boolean dying;

    public AncientZygomite(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        original = id;
        if (id == NpcId.ANCIENT_ZYGOMITE) {
            lock();
        }
    }

    @Override
    public NPC spawn() {
        dying = false;
        return super.spawn();
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (getId() == NpcId.ANCIENT_ZYGOMITE) {
            if ((Utils.currentTimeMillis() - getAttackingDelay()) >= 5000) {
                val spawn = getNpcSpawn();
                val location = spawn == null ? getLocation() : new Location(spawn.getX(), spawn.getY(), spawn.getZ());
                cancelCombat();
                heal(getMaxHitpoints());
                setAttackingDelay(Utils.currentTimeMillis());
                setRouteEvent(new NPCTileEvent(this, new TileStrategy(location), () -> {
                    setTransformation(original);
                    lock();
                }).setOnFailure(() -> {
                    setTransformation(original);
                    lock();
                }));
            }
        }
    }

    @Override
    public void sendDeath() {
        if (dying) {
            return;
        }
        val source = getMostDamagePlayerCheckIronman();
        if (source == null) {
            super.sendDeath();
            return;
        }
        val isUnlocked = source.getSlayer().isUnlocked("'Shroom sprayer");
        val usedOn = getTemporaryAttributes().remove("used_fungicide_spray");
        val obj = isUnlocked && usedOn == null ? FungicideSpray.get(source) : usedOn;
        if (getHitpoints() == 0 && obj == null) {
            if (!messageSent) {
                source.sendMessage("The Zygomite is on its last legs! Finish it quickly!");
                messageSent = true;
            }
            heal(1);
        } else {
            val info = (Object[]) obj;
            assert info != null && info[0] instanceof FungicideSpray;
            val spray = (FungicideSpray) info[0];
            val slot = (int) info[1];
            val nextCharge = spray.getNextCharge().getId();
            source.getInventory().set(slot, new Item(nextCharge));
            source.sendMessage("The Zygomite is covered in fungicide. It bubbles away to nothing!");
            dying = true;
            super.sendDeath();
        }
    }

    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        setId(original);
        messageSent = false;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id >= NpcId.ANCIENT_ZYGOMITE && id <= NpcId.ANCIENT_FUNGI;
    }

    private static final Projectile projectile = new Projectile(681, 20, 34, 0, 10, 25, 0, 5);

    @Override
    public int attack(final Entity target) {
        animate();
        attackSound();
        val style = Utils.random(isWithinMeleeDistance(this, target) ? 1 : 0);
        if (style == 0) {
            delayHit(World.sendProjectile(new Location(getLocation()), target, projectile), target, new Hit(this, getRandomMaxHit(this, 10, AttackType.MAGIC, AttackType.RANGED, target), HitType.RANGED));
        } else {
            delayHit(0, target, new Hit(this, getRandomMaxHit(this, 10, AttackType.MAGIC, AttackType.MELEE, target), HitType.MELEE));
        }
        return 4;
    }
}
