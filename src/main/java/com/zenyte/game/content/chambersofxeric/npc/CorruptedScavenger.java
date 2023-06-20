package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.room.CreatureKeeperRoom;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.BlueProgressiveHitBar;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.pathfinding.Flags;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 18. nov 2017 : 6:00.51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class CorruptedScavenger extends RaidNPC<CreatureKeeperRoom> {

	public CorruptedScavenger(final Raid raid, final CreatureKeeperRoom room, final int id, final Location tile, final Direction direction) {
		super(raid, room, id, tile);
		setDirection(direction.getDirection());
		lifepoints = maxLifepoints = Math.min(300, Math.max(30, 15 + ((room.getRaid().getOriginalPlayers().size() - 1) * 16))) * 10;
	}

	private static final Animation sleepAnimation = new Animation(7497);
	private static final Animation eatAnimation = new Animation(7496);
	private static final ForceTalk yawnAnimation = new ForceTalk("Yawwwwn!");
	private static final SoundEffect eatSound = new SoundEffect(2393, 5, 0);
	private static final SoundEffect yawnSound = new SoundEffect(1734, 5, 0);
	private static final SoundEffect sleepingSound = new SoundEffect(869, 5, 0);
	private static final Graphics sleepGraphics = new Graphics(277);
	
	@Getter private boolean fed;
	private int percentage;
	private final int maxLifepoints;
	@Getter private double lifepoints;
	private int hungerDelay;
	private final BlueProgressiveHitBar hitbar = new BlueProgressiveHitBar(percentage);
	private int ticks;
	private boolean asleep;

    @Override
    public void processNPC() {
        val process = !fed && ticks++ % 4 == 0;
        if (fed) {
            if (asleep) {
                if (ticks++ % 5 == 0) {
                    setGraphics(sleepGraphics);
                    World.sendSoundEffect(getLocation(), sleepingSound);
                }
            }
            return;
        }
        if (process) {
            final int amount = room.processTrough();
            if (amount > 0) {
                World.sendSoundEffect(getLocation(), eatSound);
                setAnimation(eatAnimation);
                lifepoints = Math.max(0, lifepoints - (amount * 10));
                hungerDelay = 0;
            } else {
                val trough = room.getTrough();
                if (trough != null) {
                    if (trough.getId() == 29874) {
                        trough.setId(29746);
                        World.spawnObject(trough);
                    }
                }
                if (++hungerDelay >= 20) {
                    if (lifepoints < maxLifepoints) {
                        lifepoints += 10;
                    }
                }
            }
        }
        percentage = (int) (((float) lifepoints / maxLifepoints) * 100F);
        hitbar.setPercentage(percentage);
        getUpdateFlags().flag(UpdateFlag.HIT);
        getHitBars().add(hitbar);
        if (lifepoints <= 0) {
            WorldTasksManager.schedule(() -> {
                for (int x = getX(); x < getX() + getSize(); x++) {
                    for (int y = getY(); y < getY() + getSize(); y++) {
                        World.getRegion(Location.getRegionId(x, y), true).removeFlag(getPlane(), x & 0x3F, y & 0x3F, Flags.FLOOR);
                    }
                }
                setAnimation(Animation.STOP);
                addWalkSteps(room.getHay().getX(), room.getHay().getY(), 8, false);
                fed = true;
                WorldTasksManager.schedule(new WorldTask() {
                    private boolean second;

                    @Override
                    public void run() {
                        if (room.getRaid().isDestroyed()) {
                            stop();
                            return;
                        }
                        if (!second) {
                            ticks = 1;
                            second = true;
                            setForceTalk(yawnAnimation);
                            World.sendSoundEffect(getLocation(), yawnSound);
                            setAnimation(sleepAnimation);
                        } else {
                            asleep = true;
                            setTransformation(7603);
                            setAnimation(Animation.STOP);
                            stop();
                        }
                    }
                }, 4, 5);
            }, 1);
        }
    }

}
