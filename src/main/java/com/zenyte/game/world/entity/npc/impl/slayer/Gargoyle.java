package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.AnimationDefinitions;
import com.zenyte.utils.Ordinal;
import lombok.val;

/**
 * @author Tommeh | 11 dec. 2017 : 15:10:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class Gargoyle extends NPC implements Spawnable {

    public static final Item ROCK_HAMMER = new Item(4162);
    public static final Item GRANITE_HAMMER = new Item(21742);
    private static final Animation BOSS_FINISH_OFF = new Animation(401);
    private boolean dying;

    @Override
    public NPC spawn() {
        dying = false;
        return super.spawn();
    }

    public Gargoyle(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
    }

    @Ordinal
    public boolean setHitpoints(final int amount) {
        val dead = isDead();
        this.hitpoints = amount;
        if (!dead && hitpoints <= 9) {
            sendDeath();
            return true;
        }
        return false;
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
        val isUnlocked = source.getSlayer().isUnlocked("Gargoyle smasher");
        val obj = getTemporaryAttributes().get("used_rock_hammer");
        if ((obj != null && (boolean) obj) || (getHitpoints() <= 9 && isUnlocked && (source.getInventory().containsItem(ROCK_HAMMER) || source.getInventory().containsItem(GRANITE_HAMMER) || source.getInventory().containsItem(21754, 1)))) {
            if (isUnlocked) {
                deathSequence(source);
                //source.lock();
                //source.setRouteEvent(new EntityEvent(source, new DistancedEntityStrategy(this, 1), () -> deathSequence(source), true));
            } else {
                deathSequence(source);
            }
        } else if (getHitpoints() == 0) {
            heal(1);
        }
    }

    private void deathSequence(final Player source) {
        dying = true;
        source.setAnimation(BOSS_FINISH_OFF);
        source.faceEntity(this);
        //source.lock();
        onDeath(getMostDamagePlayerCheckIronman());
        lock(3);
        setAnimation(new Animation(1520));
        val id = getId();
        setTransformation(413);
        getTemporaryAttributes().remove("used_rock_hammer");
        if (!source.getInventory().containsItem(ROCK_HAMMER) && !source.getInventory().containsItem(GRANITE_HAMMER)) {
            source.getInventory().deleteItem(21754, 1);
        }
        WorldTasksManager.schedule(new WorldTask() {
            private int loop;

            @Override
            public void run() {
                if (loop == 1) {
                    //source.unlock();
                    setId(id);
                    onFinish(source);
                    setId(id);
                    stop();
                }
                loop++;
            }
        }, 0, 1);
        source.sendFilteredMessage("The gargoyle cracks apart.");
    }

    @Override
    public void setAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            final AnimationDefinitions defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == 412 || id == 413 || id == 1543;
    }

}
