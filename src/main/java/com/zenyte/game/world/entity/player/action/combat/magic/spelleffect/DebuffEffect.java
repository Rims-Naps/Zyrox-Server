package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.world.entity.Entity;

public class DebuffEffect implements SpellEffect {

	public DebuffEffect(final int skill, final int percentage, final int minimumDrain) {
		this.skill = skill;
		this.percentage = percentage;
		this.minimumDrain = minimumDrain;
	}

	private final int skill, percentage, minimumDrain;

	@Override
	public void spellEffect(final Entity player, final Entity target, final int damage) {
		target.drainSkill(skill, (double) percentage, minimumDrain);
	}

}
