package com.zenyte.game.content.chambersofxeric.greatolm;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.utils.ProjectileUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 16. jaan 2018 : 1:13.19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * The crystal cluster object that spawns underneath players' feet and quickly shrinks into large crystals. Spawned during
 * {@link com.zenyte.game.content.chambersofxeric.greatolm.scripts.CrystalBurst} attack.
 */
@EqualsAndHashCode(callSuper = true)
public final class CrystalCluster extends WorldObject {

	private static final Graphics SHATTER_GFX = new Graphics(1338);
	private static final Animation shoveAnimation = new Animation(1114);
	private static final SoundEffect riseSound = new SoundEffect(1551, 5, 0);
	private static final SoundEffect explodeSound = new SoundEffect(3821, 5, 0);
	
	public CrystalCluster(final OlmRoom room, final Location tile) {
		super(30033, 10, 0, tile);
		this.room = room;
		room.getCrystalClusters().add(this);
	}
	
	private final OlmRoom room;

    /**
     * The number of ticks that the cluster remains on the ground for, before completely vanishing.
     */
	@Getter private int ticks = 7;
	
	public boolean process() {
		switch(--ticks) {
		case 6: {
            val existingObject = World.getObjectWithType(this, getType());
            World.sendSoundEffect(this, riseSound);
            if (existingObject == null) {
                World.spawnObject(this);
            }
            return true;
        }
		case 3: {
            val existingObject = World.getObjectWithType(this, getType());
            if (existingObject != null && existingObject.getId() == 30033) {
                World.spawnObject(new WorldObject(30034, 10, 0, this));
            }
            World.sendSoundEffect(this, explodeSound);
            this.setId(30034);
            val tiles = new ArrayList<Location>(8);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    val tile = transform(x, y, 0);
                    if (tile.matches(this) || ProjectileUtils.isProjectileClipped(null, null, this, tile, true)) {
                        continue;
                    }
                    tiles.add(tile);
                }
            }
            for (Player player : room.getOlm().everyone(GreatOlm.ENTIRE_CHAMBER)) {
                if (!player.getLocation().matches(this)) {
                    continue;
                }
                player.stop(Player.StopType.ROUTE_EVENT, Player.StopType.ACTIONS, Player.StopType.WALK, Player.StopType.INTERFACES);
                val hit = new Hit(room.getOlm(), Utils.random(10, 40), HitType.REGULAR);
                hit.setExecuteIfLocked();
                player.applyHit(hit);
                player.putBooleanAttribute("PerfectOlm", false);
                player.lock(1);
                player.sendMessage("The crystal beneath your feet grows rapidly and shunts you to the side.");
                val randomTile = tiles.get(Utils.random(tiles.size() - 1));
                player.setForceMovement(new ForceMovement(player.getLocation(), 1, randomTile, 30, OlmRoom.getMovementDirection(player, randomTile)));
                WorldTasksManager.schedule(() -> player.setLocation(randomTile));
                player.setAnimation(shoveAnimation);
            }
            return true;
        }
		case 0:
            val existingObject = World.getObjectWithType(this, getType());
			World.sendGraphics(SHATTER_GFX, this);
			if (existingObject == this || (existingObject != null && (existingObject.getId() == 30033 || existingObject.getId() == 30034))) {
                World.removeObject(World.getObjectWithType(this, 10));
            }
			return false;
			default:
				return true;
		}
	}


}
