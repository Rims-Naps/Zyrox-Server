package com.zenyte.game.world.entity.npc.impl;

import com.zenyte.game.content.godwars.npcs.KillcountNPC;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.region.CharacterLoop;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 28. juuli 2018 : 15:18:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class DesertBandit extends NPC implements Spawnable {

	private static final IntOpenHashSet VALUABLE_GOD_ITEMS = new IntOpenHashSet();
    private static final IntOpenHashSet SARADOMIN_ITEMS = new IntOpenHashSet();
    private static final IntOpenHashSet ZAMORAK_ITEMS = new IntOpenHashSet();

    private static final ForceTalk SARADOMIN_FORCE_CHAT = new ForceTalk("Time to die, Saradominist filth!");
    private static final ForceTalk ZAMORAK_FORCE_CHAT = new ForceTalk("Prepare to suffer, Zamorakian scum!");
    private static final ForceTalk AGGRESSION_FORCE_CHAT = new ForceTalk("You chose the wrong place to start trouble!");

    static {
	    for (val i : KillcountNPC.GodType.ZAMORAK.getProtectiveItems())
            ZAMORAK_ITEMS.add(i);
        for (val i : KillcountNPC.GodType.SARADOMIN.getProtectiveItems())
            SARADOMIN_ITEMS.add(i);
        VALUABLE_GOD_ITEMS.addAll(SARADOMIN_ITEMS);
        VALUABLE_GOD_ITEMS.addAll(ZAMORAK_ITEMS);
	}
	
	public DesertBandit(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
		setForceAggressive(true);
	}

	@Override
	public boolean validate(final int id, final String name) {
		return id == 690 || id == 695;
	}

	private boolean isWieldingValuableItem(final Player player) {
		final Container container = player.getEquipment().getContainer();
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			final Item item = container.get(slot);
			if (item == null) {
				continue;
			}
			if (VALUABLE_GOD_ITEMS.contains(item.getId())) {
				return true;
			}
		}
		return false;
	}

    @Override
    public void autoRetaliate(final Entity source) {
        val currentTarget = combat.getTarget();
        super.autoRetaliate(source);
        val newTarget = combat.getTarget();
        if (newTarget != null && newTarget != currentTarget) {
            setForceTalk(AGGRESSION_FORCE_CHAT);
            CharacterLoop.forEach(this.getLocation(), 10, NPC.class, n -> {
                if (n.isDead() || n.getCombat().getTarget() != null || !n.isAttackable() || n.isProjectileClipped(source, false)) {
                    return;
                }
                n.getCombat().setTarget(source);
                n.setForceTalk(AGGRESSION_FORCE_CHAT);
            });
        }
    }

	@Override
    public boolean checkAggressivity() {
        final boolean check = super.checkAggressivity();
        if (this.combat.getTarget() != null) {
            final Entity target = this.combat.getTarget();
            if (target instanceof Player) {
                final Player player = (Player) target;
                final Container container = player.getEquipment().getContainer();
                for (int slot = 0; slot < container.getContainerSize(); slot++) {
                    final Item item = container.get(slot);
                    if (item == null) {
                        continue;
                    }
                    if (SARADOMIN_ITEMS.contains(item.getId())) {
                        this.setForceTalk(SARADOMIN_FORCE_CHAT);
                        return true;
                    }
                    if (ZAMORAK_ITEMS.contains(item.getId())) {
                        this.setForceTalk(ZAMORAK_FORCE_CHAT);
                        return true;
                    }
                }
            }
        }
        return check;
    }
	
	@Override
	public boolean isTolerable() {
		return false;
	}
	
	@Override
	protected boolean isAcceptableTarget(final Entity target) {
		if (target instanceof Player) {
			return isWieldingValuableItem((Player) target);
		}
		return super.isAcceptableTarget(target);
	}
	
}
