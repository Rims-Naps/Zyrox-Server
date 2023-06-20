package com.zenyte.game.content.skills.magic.spells.lunar;

import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.DefaultSpell;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import lombok.val;

/**
 * @author Kris | 19. veebr 2018 : 0:07.46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class HealGroup implements DefaultSpell {

	private static final Animation ANIM = new Animation(4409);
	private static final Graphics GFX = new Graphics(745, 0, 90);
	private static final SoundEffect sound = new SoundEffect(2894, 10, 51);
	private static final SoundEffect otherSound = new SoundEffect(2892, 10, 67);

	@Override
	public int getDelay() {
		return 4500;
	}

	@Override
	public boolean spellEffect(final Player player, final int optionId, final String option) {
        if (!hasDefenceRequirement(player)) {
            return false;
        }
		val minimumRequired = (int) Math.ceil(player.getMaxHitpoints() * 0.11f);
		if (player.getHitpoints() <= minimumRequired) {
			player.sendMessage("You need more hitpoints to cast this spell.");
			return false;
		}


        val affectedPlayers = player.findCharacters(1, Player.class, p2 -> {
            if (p2 == player || p2.isDead() || !p2.isRunning() || !p2.getBooleanSetting(Setting.ACCEPT_AID) || p2.getDuel() != null || p2.getHitpoints() >= p2.getMaxHitpoints()) {
                return false;
            }
            val inWilderness = WildernessArea.isWithinWilderness(p2.getX(), p2.getY());
            return !inWilderness || p2.isMultiArea();
        });

		if (affectedPlayers.size() > 0) {
            World.sendSoundEffect(player, sound);
			player.sendMessage("The spell affected " + affectedPlayers.size() + " nearby people.");
			this.addXp(player, 124);
			player.setAnimation(ANIM);
			int maximumPossibleAmount = 0;
			for (val p : affectedPlayers) {
			    maximumPossibleAmount += (p.getMaxHitpoints() - p.getHitpoints());
            }
			val amount = (int) (player.getHitpoints() * 0.75f);
			val actualAmount = maximumPossibleAmount > amount ? amount : maximumPossibleAmount;
			player.applyHit(new Hit(actualAmount, HitType.REGULAR));
			val heal = actualAmount / affectedPlayers.size();
			for (int i = affectedPlayers.size() - 1; i >= 0; i--) {
				val p = affectedPlayers.get(i);
				World.sendSoundEffect(p, otherSound);
				p.setGraphics(GFX);
				p.heal(heal);
				p.sendMessage(player.getName() + " has transferred some of their health to you.");
			}
			return true;
		}
		player.sendMessage("There are no players around you with whom you can share health.");
		return false;
	}
	
	@Override
	public Spellbook getSpellbook() {
		return Spellbook.LUNAR;
	}

}
