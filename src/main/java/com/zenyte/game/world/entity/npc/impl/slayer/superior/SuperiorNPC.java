package com.zenyte.game.world.entity.npc.impl.slayer.superior;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 27/05/2019 23:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SuperiorNPC extends NPC {
    private final NPC root;
    private final Player owner;

    private int ticks;

    public SuperiorNPC(@NotNull final Player owner, @NotNull final NPC root, final int id, final Location tile) {
        super(id, tile, Direction.SOUTH, 5);
        this.root = root;
        this.owner = owner;
        this.spawned = true;
        this.setForceAttackable(true);
        this.combat = new NPCCombat(this) {
            @Override
            protected boolean attackable() {
                return true;
            }
        };
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return entity == owner;
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (isUnderCombat()) {
            ticks = 0;
        } else {
            if (owner.getLocation().withinDistance(getLocation(), 15)) {
                if (ticks++ == 200) {
                    finish();
                }
            } else if (ticks++ == 50) {
                finish();
            }
        }
    }

    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        owner.getTemporaryAttributes().remove("superior monster");
    }

    @Override
    public boolean canAttack(final Player source) {
        if (source != owner) {
            source.sendMessage("This is not your superior foe.");
            return false;
        }
        return super.canAttack(source);
    }

    private void executeOnDeathPlugins(final Player killer) {
        val processors = DropProcessorLoader.get(getId());
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }
    }

    @Override
    public void drop(final Location tile) {
        val killer = getDropRecipient();
        if (killer == null) {
            return;
        }
        executeOnDeathPlugins(killer);

        val processors = DropProcessorLoader.get(root.getId());
        val drops = NPCDrops.getTable(root.getId());
        for (int i = 0; i < 3; i++) {
            root.onDrop(killer);
            if (processors != null) {
                for (val processor : processors) {
                    processor.onDeath(root, killer);
                }
            }
            if (drops == null) {
                continue;
            }
            val index = i;
            NPCDrops.forEach(drops, drop -> {
                if (index != 0 && drop.isAlways()) {
                    return;
                }
                dropItem(killer, drop, tile);
            });
        }
        val req = getCombatDefinitions().getSlayerLevel();
        val probability = 0.5F / (200F - (Math.pow(req + 55F, 2) / 125F));
        if (Utils.randomDouble() < probability) {
            val roll = Utils.random(7);
            if (roll < 3) {
                dropItem(killer, new Item(20736));
            } else if (roll < 6) {
                dropItem(killer, new Item(20730));
            } else if (roll == 6) {
                dropItem(killer, new Item(20724));
            } else {
                dropItem(killer, new Item(21270));
            }
        }
    }

}
