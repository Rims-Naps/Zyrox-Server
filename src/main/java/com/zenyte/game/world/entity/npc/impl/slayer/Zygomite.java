package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.item.enums.FungicideSpray;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCTileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 11 dec. 2017 : 15:10:53
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class Zygomite extends NPC implements Spawnable {

    private boolean messageSent;
    private final int original;
    private boolean dying;

    public Zygomite(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
        original = id;
        if (id == 471 || id == 472) {
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
        if (getId() == 473 || getId() == 474) {
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
    protected String notificationName(@NotNull final Player player) {
        return "mutated zygomite";
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
        return id >= 471 && id <= 474;
    }

}
