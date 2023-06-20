package com.zenyte.game.content.chambersofxeric.greatolm.scripts;

import com.zenyte.game.content.chambersofxeric.greatolm.GreatOlm;
import com.zenyte.game.content.chambersofxeric.greatolm.LeftClaw;
import com.zenyte.game.content.chambersofxeric.greatolm.OlmCombatScript;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.skills.prayer.PrayerManager;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.MovementLock;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.CharacterLoop;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kris | 16. jaan 2018 : 5:08.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class Lightning implements OlmCombatScript {

	private static final IntArrayList tiles = new IntArrayList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
	private static final Graphics lightningGraphics = new Graphics(1356);
	private final List<LightningSpiral> spirals = new ArrayList<>();
	private static final Animation electrocutionAnimation = new Animation(3170);
	private static final SoundEffect sound = new SoundEffect(3887, 15, 0);
	private static final SoundEffect walkFailSound = new SoundEffect(154);
	
	@Override
	public void handle(final GreatOlm olm) {
		olm.getScripts().add(this.getClass());
		if (olm.getRoom().getLeftClaw() != null) {
			olm.getRoom().getLeftClaw().displaySign(LeftClaw.LIGHTNING_SIGN);
		}
		final List<Player> everyone = olm.everyone(GreatOlm.ENTIRE_CHAMBER);
		int amount = everyone.size() >= 6 ? 5 : 4;
		val xAxis = new IntArrayList(tiles);
		int northCount = 0;
		int southCount = 0;
		while (amount-- > 0) {
			val x = xAxis.removeInt(Utils.random(xAxis.size() - 1));
			//If north has (amount - 1) lightnings, it means this is the last roll and it must be put to the south & vice versa. There needs to be at least one lightning on each side.
			val north = (northCount != amount - 1) && ((southCount == amount - 1) || Utils.random(1) == 0);
			val corner = north ? olm.getLightningLowerCorner() : olm.getLightningUpperCorner();
			val tile = new Location(corner.getX() + x, corner.getY(), corner.getPlane());
			spirals.add(new LightningSpiral(tile, north));
			if (north) {
			    northCount++;
            } else {
			    southCount++;
            }
		}
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks = 18;
			@Override
			public void run() {
				if (olm.getRoom().getRaid().isDestroyed()) {
					stop();
					return;
				}
				if (--ticks == 0) {
					stop();
					olm.getScripts().remove(Lightning.this.getClass());
					return;
				}
                CharacterLoop.forEach(olm.getMiddleLocation(), 30, Player.class, p -> {
                    LightningSpiral northern = null;
                    LightningSpiral southern = null;
                    for (val lightning : spirals) {
                        if (lightning.isNorth()) {
                            if (northern == null || northern.getTile().getDistance(p.getLocation()) > lightning.getTile().getDistance(p.getLocation())) {
                                northern = lightning;
                            }
                        } else {
                            if (southern == null || southern.getTile().getDistance(p.getLocation()) > lightning.getTile().getDistance(p.getLocation())) {
                                southern = lightning;
                            }
                        }
                    }
                    if (northern != null) {
                        p.getPacketDispatcher().sendAreaSoundEffect(new Location(northern.getTile()), sound);
                    }
                    if (southern != null) {
                        p.getPacketDispatcher().sendAreaSoundEffect(new Location(southern.getTile()), sound);
                    }
                });
				spirals.forEach(spiral -> {
				    val tile = new Location(spiral.getTile());
					World.sendGraphics(lightningGraphics, tile);
					for (final Player player : olm.everyone(GreatOlm.ENTIRE_CHAMBER)) {
						if (player.getLocation().getPositionHash() == spiral.getTile().getPositionHash()) {
							player.applyHit(new Hit(olm, Utils.random(15, 25), HitType.REGULAR));
							player.putBooleanAttribute("PerfectOlm", false);
							player.sendMessage("<col=ff0000>You've been electrocuted to the spot!");
							final PrayerManager prayer = player.getPrayerManager();
                            deactivateOverheadProtectionPrayers(player, prayer, true);
                            player.setAnimation(electrocutionAnimation);
                            player.addMovementLock(new MovementLock(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5), null, () -> player.sendSound(walkFailSound)));
						}
					}
					spiral.getTile().moveLocation(0, spiral.isNorth() ? 1 : -1, 0);
				});
			}
		}, 0, 0);
	}

    static void deactivateOverheadProtectionPrayers(final Player player, final PrayerManager prayer, final boolean injure) {
	    if (injure) {
            player.getTemporaryAttributes().put("prayer delay", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3));
        }
	    val bool = new MutableBoolean();
        if (prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
            prayer.deactivatePrayer(Prayer.PROTECT_FROM_MAGIC);
            bool.setTrue();
        }
        if (prayer.isActive(Prayer.PROTECT_FROM_MELEE)) {
            prayer.deactivatePrayer(Prayer.PROTECT_FROM_MELEE);
            bool.setTrue();
        }
        if (prayer.isActive(Prayer.PROTECT_FROM_MISSILES)) {
            prayer.deactivatePrayer(Prayer.PROTECT_FROM_MISSILES);
            bool.setTrue();
        }
        if (bool.isTrue()) {
            player.sendMessage("You've been injured and can't use protection prayers!");
        }
    }

    private static final class LightningSpiral {
		
		LightningSpiral(final Location tile, final boolean north) {
			this.tile = tile;
			this.north = north;
		}
		
		@Getter private final Location tile;
		@Getter private final boolean north;
		
	}

}
