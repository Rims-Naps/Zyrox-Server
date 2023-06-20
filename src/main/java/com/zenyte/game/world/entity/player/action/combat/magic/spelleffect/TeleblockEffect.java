package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import lombok.val;

public class TeleblockEffect implements SpellEffect {

	@Override
	public void spellEffect(final Entity player, final Entity target, final int damage) {
		if (target.getEntityType() != EntityType.PLAYER) {
			return;
		}
		val p = (Player) target;
		if (p.getVariables().getTime(TickVariable.TELEBLOCK) > 0 || p.getVariables().getTime(TickVariable.TELEBLOCK_IMMUNITY) > 0) {
			return;
		}
		val halved = p.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC);
		p.getVariables().schedule(halved ? 250 : 500, TickVariable.TELEBLOCK);
		p.getVariables().schedule(halved ? 350 : 600, TickVariable.TELEBLOCK_IMMUNITY);
		p.sendMessage("<col=4f006f>A teleblock spell has been cast on you. It will expire in "
				+ (halved ? "2 minutes, 30 seconds." : "5 minutes, 0 seconds.") + "</col>");
		if (player instanceof Player) {
			((Player) player).getSkills().addXp(Skills.MAGIC, 11);
		}
	}

}
