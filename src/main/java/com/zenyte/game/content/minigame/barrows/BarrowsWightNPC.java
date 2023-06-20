package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.content.combatachievements.combattasktiers.EasyTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.minigame.barrows.wights.KarilTheTainted;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.player.Player;
import lombok.NonNull;
import lombok.val;

/**
 * @author Kris | 6. dets 2017 : 1:43.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class BarrowsWightNPC extends BarrowsNPC {

	public BarrowsWightNPC(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
		setForceAttackable(true);
	}

	@NonNull
	BarrowsWight getWight() {
        val wight = Utils.findMatching(BarrowsWight.values, npc -> npc.getNpcId() == getId());
        if (wight == null) {
            throw new RuntimeException("No matching wight found for npc: " + getId());
        }
        return wight;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
	    if (owner == null || !(entity instanceof Player)) {
	        return false;
        }
	    val owner = this.owner.get();
	    if (owner == null) {
	        return false;
        }
	    return owner.getUsername().equals(((Player) entity).getUsername());
    }

    @Override
    public void onDeath(final Entity source) {
	    super.onDeath(source);
        val owner = this.owner.get();
        if (owner == null) {
            return;
        }
	    owner.getBarrows().removeTarget();
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (player.getCombatDefinitions().getAttackType() == CombatScript.MAGIC && !player.getBooleanAttribute("easy-combat-achievement21")) {
                    player.putBooleanAttribute("easy-combat-achievement21", true);
                    EasyTasks.sendEasyCompletion(player, 21);
                }
                if (player.getPrayerManager().getPrayerPoints() > 0) {
                    player.putBooleanAttribute("faithless_barrows", false);
                }
                if (super.getId() == BarrowsWight.KARIL.getNpcId() && !player.getBooleanAttribute("hard-combat-achievement42")) {
                    if (KarilTheTainted.allHitsOnKarilWereSpecial) {
                        player.putBooleanAttribute("hard-combat-achievement42", true);
                        HardTasks.sendHardCompletion(player, 42);
                    }
                }
            }
        }
    }

    @Override
    public void onFinish(final Entity source) {
	    super.onFinish(source);
        if (source instanceof Player) {
            val owner = this.owner.get();
            if (owner == null) {
                return;
            }
            owner.getBarrows().onDeath(this);
        }
    }

}
