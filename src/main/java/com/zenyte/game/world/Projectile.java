package com.zenyte.game.world;

import com.zenyte.Constants;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

/**
 * @author Kris | 14. okt 2017 : 10:35.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class Projectile {

	/** Right angle in degrees. */
	private static final int RIGHT_ANGLE = 90;
	
	/** The right angle in client. */
	private static final int CLIENT_RIGHT_ANGLE = 64;
	
	/** The ratio used to modify projectile angle for the client. */
	private static final float ANGLE_RATIO = (float) RIGHT_ANGLE / CLIENT_RIGHT_ANGLE;
	
	/** The default values for projectiles. */
	private static final int DEFAULT_SLOPE = 0, DEFAULT_OFFSET = 0, DEFAULT_SPEED = 10, DEFAULT_DELAY = 0;

	/** The id of the graphics that's going to be shot as a projectile. */
	@Getter private final int graphicsId;
	
	/** The starting height of the projectile. */
	@Getter private final int startHeight;
	
	/** The ending height of the projectile. */
	@Getter private final int endHeight;
	
	/** The delay before projectile starts flying, uses {@link Constants#CLIENT_CYCLE} as units. */
	@Getter @Setter private int delay;
	
	/** The angle of the projectile, will be converted from actual degrees to units client reads. {value can be from 0-90, including both extremes.} */
	@Getter private final int angle;
	
	/**
	 * The duration of the projectile that will be added or subtracted from the original flight duration of the projectile which is calculated from distance and delay.
	 */
	@Getter
	@Setter
	private int duration;
	
	/** The offset in distance. The projectile will be moved closer towards the end by input value. {value 64 is
     * equal to one tile distance.} */
	@Getter private final int distanceOffset;

	/** The multiplier used for adjusting projectile speed per tile distance basis. */
	@Getter private final int multiplier;

	public Projectile(final int graphicsId, final int startHeight, final int endHeight, final int delay,
                      final int angle, final int duration, final int distanceOffset, final int multiplier) {
		this.graphicsId = graphicsId;
		this.startHeight = startHeight;
		this.endHeight = endHeight;
		this.delay = delay;
		this.angle = angle > 0 ? (Math.round(Math.min(RIGHT_ANGLE, angle) / ANGLE_RATIO)) : (Byte.MAX_VALUE - Math.round(-Math.min(RIGHT_ANGLE, angle) / ANGLE_RATIO));
		this.duration = duration;
		this.distanceOffset = distanceOffset;
		this.multiplier = multiplier;
	}

    public Projectile(final Projectile projectile) {
        this.graphicsId = projectile.graphicsId;
        this.startHeight = projectile.startHeight;
        this.endHeight = projectile.endHeight;
        this.delay = projectile.delay;
        this.angle = projectile.angle;
		this.duration = projectile.duration;
		this.distanceOffset = projectile.distanceOffset;
		this.multiplier = projectile.multiplier;
	}
	
	@Deprecated
	public Projectile(final int graphicsId, final int startHeight, final int endHeight, final int duration, final int delay) {
		this(graphicsId, startHeight, endHeight, delay, DEFAULT_SLOPE, duration, DEFAULT_OFFSET, 5);
	}
	
	@Deprecated
	public Projectile(final int graphicsId, final int startHeight, final int endHeight, final int delay) {
		this(graphicsId, startHeight, endHeight, delay, DEFAULT_SLOPE, DEFAULT_SPEED, DEFAULT_OFFSET, 5);
	}
	
	@Deprecated
	public Projectile(final int graphicsId, final int startHeight, final int endHeight) {
		this(graphicsId, startHeight, endHeight, DEFAULT_DELAY, DEFAULT_SLOPE, DEFAULT_SPEED, DEFAULT_OFFSET, 5);
	}
	
	/**
	 * Gets the duration of the projectile in ticks, rounded.
	 *
	 * @param from
	 *            tile from where the projectile is sent.
	 * @param to
	 *            tile to which the projectile flies.
	 * @return 
	 * 			duration in ticks, rounded (>= .5 -> 1; < .5 -> 0)
	 */
	public int getTime(final Location from, final Location to) {
		float duration = getProjectileDuration(from, to) / Constants.CYCLES_PER_TICK;
		if (duration - (int) duration > 0.5F) {
			duration++;
		}
		return Math.max(0, (int) duration - 1);
	}

	public int getTime(final Location from, final Entity to) {
		return getTime(from, to.getLocation());
	}

	public int getTime(final Entity from, final Location to) {
		return getTime(from.getMiddleLocation(), to);
	}

	public int getTime(final Entity from, final Entity to) {
		return getTime(from.getMiddleLocation(), to.getLocation());
	}

	/**
	 * Gets the flight duration of the projectile in client cycles.
	 * 
	 * @param from
	 *            where the projectile is sent.
	 * @param to
	 *            where the projectile flies.
	 * @return 
	 * 			the duration of the projectile in client cycles.
	 */
	public int getProjectileDuration(final Position from, final Position to) {
		val fromLocation = from.getPosition();
	    val target = to instanceof Entity ? ((Entity) to).getMiddleLocation() : to.getPosition();
		val flightDuration = Math.max(Math.abs(fromLocation.getX() - target.getX()), Math.abs(fromLocation.getY() - target.getY())) * multiplier;
		return delay + duration + flightDuration;
	}

	/**
	 * Calculates the duration of the projectile from the input amount of ticks. 
	 * The value is unrestricted, so if the delay of the projectile ends up exceeding the new duration of the projectile, 
	 * projectile itself will never actually be visible.
	 * 
	 * @param ticks
	 * 				the duration in ticks the projectile should last for.
	 * @return
	 * 				the duration in client cycles the projectile will last for.
	 */
	public int getProjectileDurationByDistance(final int ticks) {
		return (int) (ticks * Constants.CYCLES_PER_TICK);
	}
}
