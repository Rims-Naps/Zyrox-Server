package com.zenyte.game.content.chambersofxeric.greatolm.scripts;

import com.zenyte.game.CameraShakeType;
import com.zenyte.game.content.chambersofxeric.greatolm.GreatOlm;
import com.zenyte.game.content.chambersofxeric.greatolm.OlmCombatScript;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Setter;
import lombok.val;

/**
 * @author Kris | 14. jaan 2018 : 3:17.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class TransitionalFallingCrystals implements OlmCombatScript {

	private static final Projectile projectile = new Projectile(1357, 255, 0, 0, 0, 120, 127, 0);
	private static final Graphics explode = new Graphics(1358);
	private static final Graphics shadow = new Graphics(1446, 30, 0);
	private static final SoundEffect fallSound = new SoundEffect(3834, 10, 0);
	private static final SoundEffect explodeSound = new SoundEffect(1021, 10, 0);
	
	public TransitionalFallingCrystals(final int duration) {
		this.duration = duration;
	}
	
	private final int duration;
	@Setter private boolean stopped;
	
	@Override
	public void handle(final GreatOlm olm) {
		for (val player : olm.everyone(GreatOlm.ENTIRE_CHAMBER)) {
			player.getPacketDispatcher().sendCameraShake(CameraShakeType.LEFT_AND_RIGHT, (byte) 5, (byte) 0, (byte) 0);
		}
		olm.getScripts().add(this.getClass());
		WorldTasksManager.schedule(new WorldTask() {
		    private final Object2IntMap<Location> crystals = new Object2IntOpenHashMap<>();
			private int ticks;
			private int selectedDelay = 5;
			private final int cap = olm.everyone(GreatOlm.ENTIRE_CHAMBER).size() >= 6 ? 5 : 4;
			@Override
			public void run() {
				if (olm.getRoom().getRaid().isDestroyed() || stopped) {
					for (val player : olm.everyone(GreatOlm.ENTIRE_CHAMBER)) {
						player.getPacketDispatcher().resetCamera();
					}
					olm.getScripts().remove(TransitionalFallingCrystals.this.getClass());
					stop();
					return;
				}
				if (!crystals.isEmpty()) {
					val everyone = olm.everyone(GreatOlm.ENTIRE_CHAMBER);
					val remainingCrystals = new Object2IntOpenHashMap<Location>(crystals);
                    remainingCrystals.object2IntEntrySet().removeIf(entry -> {
					    val k = entry.getKey();
					    val v = entry.getIntValue();
                        if (v <= 0) {
                            World.sendSoundEffect(k, explodeSound);
                            for (val player : everyone) {
                                if (player.getLocation().withinDistance(k, 1)) {
                                    player.applyHit(new Hit(player.getLocation().matches(k) ? Utils.random(17, 25) : Utils.random(10, 16), HitType.REGULAR));
                                }
                            }
                            return true;
                        } else if (v == 1) {
                            World.sendGraphics(explode, k);
                        }
                        return false;
                    });
                    crystals.clear();
                    remainingCrystals.object2IntEntrySet().fastForEach(entry -> crystals.put(entry.getKey(), entry.getIntValue() - 1));
				}
				boolean dropOnPlayer = --selectedDelay <= 0;
				if (ticks++ < duration) {
					int amount = Math.min(cap - crystals.size(), Utils.random(0, cap - 1));
					if (amount <= 0) {
						return;
					}
					while(amount-- > 0) {
						int trycount = 10;
						Location tile = null;
						if (dropOnPlayer) {
						    dropOnPlayer = false;
                            selectedDelay = 5;
						    val player = olm.random(GreatOlm.ENTIRE_CHAMBER);
						    if (player != null) {
                                tile = new Location(player.getLocation());
                            }
                        } else {
                            loop:
                            while (trycount-- > 0) {
                                if ((tile = olm.randomLocation(GreatOlm.ENTIRE_CHAMBER)) != null) {
                                    for (final Location usedTile : crystals.keySet()) {
                                        if (usedTile.getPositionHash() == tile.getPositionHash()) {
                                            continue loop;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
						if (tile == null) {
							continue;
						}
						val location = new Location(tile.getX(), tile.getY(), tile.getPlane());
						do {
						    location.moveLocation(Utils.random(-1, 1), Utils.random(-1, 1), 0);
                        } while (location.getPositionHash() == tile.getPositionHash());
						World.sendProjectile(location, tile, projectile);
						World.sendGraphics(shadow, tile);
						if (!olm.isFinalStand()) {
						    World.sendSoundEffect(tile, fallSound);
                        }
						crystals.put(tile, 3);
					}
				} else {
					if (crystals.isEmpty()) {
						for (val player : olm.everyone(GreatOlm.ENTIRE_CHAMBER)) {
							player.getPacketDispatcher().resetCamera();
						}
						olm.getScripts().remove(TransitionalFallingCrystals.this.getClass());
						stop();
                    }
				}
			}
		}, 0, 0);
	}

}
