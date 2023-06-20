package com.zenyte.game.world.entity.masks;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Kris | 6. nov 2017 : 14:30.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public class Hit {

	@Getter @Setter private Entity source;
	@Getter @Setter private HitType hitType;
	@Getter private int damage;
	@Getter private int delay;
	@Getter @Setter private long scheduleTime;
	@Getter @Setter private boolean forcedHitsplat;
	@Getter private Map<String, Object> attributes;

	public void setDamage(final int damage) {
		this.damage = Math.min(32767, Math.max(0, damage));
	}

	public void setDelay(final int delay) {
		this.delay = Math.min(32767, Math.max(0, delay));
	}

	public Hit(final int damage, final HitType look) {
		this(null, damage, look, 0);
	}	
	
	public Hit(final Entity source, final int damage, final HitType look) {
		this(source, damage, look, 0);
	}

	public Hit(final Entity source, final int damage, final HitType hitType, final int delay) {
		this.source = source;
		this.damage = damage;
		this.hitType = hitType;
		this.delay = delay;
		scheduleTime = Utils.currentTimeMillis();
	}
	
	public void putAttribute(final String key, final Object value) {
		if (attributes == null) {
			attributes = new Object2ObjectOpenHashMap<>();
		}
		attributes.put(key, value);
	}
	
	public void setWeapon(final Object weapon) {
		putAttribute("weapon", weapon);
	}

	public Hit onLand(final Consumer<Hit> consumer) {
	    putAttribute("on_hit_land", consumer);
	    return this;
    }

    @SuppressWarnings("all")
    public Consumer<Hit> getOnLandConsumer() {
        if (attributes == null) {
            return null;
        }
        val attachment = attributes.get("on_hit_land");
        if (attachment instanceof Consumer) {
            return (Consumer<Hit>) attachment;
        }
        return null;
    }

	public Hit setPredicate(final Predicate<Hit> attachment) {
	    putAttribute("predicate", attachment);
	    return this;
    }

    @SuppressWarnings("unchecked cast")
    public Predicate<Hit> getPredicate() {
	    if (attributes == null) {
	        return null;
        }
	    val attachment = attributes.get("predicate");

	    if (attachment instanceof Predicate) {
	        return (Predicate<Hit>) attachment;
        }
	    return null;
    }

    public boolean executeIfLocked() {
	    return containsAttribute("execute_if_locked");
    }

    public Hit setExecuteIfLocked() {
	    putAttribute("execute_if_locked", Boolean.TRUE);
	    return this;
    }

	public Object getWeapon() {
		if (attributes == null) {
			return null;
		}
		return attributes.get("weapon");
	}
	
	public boolean containsAttribute(final String key) {
		if (attributes == null) {
			return false;
		}
		return attributes.containsKey(key);
	}

	public final int getMark() {
		if (damage == 0 && !forcedHitsplat) {
			return HitType.MISSED.getMark();
		}
		return hitType.getMark();
	}
	
}
