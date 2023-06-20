package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 29. juuli 2018 : 03:52:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class CrawsBowCombat extends RangedCombat {

    public CrawsBowCombat(final Entity target, final AmmunitionDefinitions defs) {
        super(target, defs);
	}

	@Override
	protected boolean checkPreconditions()
	{
		final int[] BOFAARRAY = new int[] {30596, 32158, 32160, 32162, 32164, 32166, 32168, 32170, 32172, 32174};

		if(player.getWeapon().getId() == ItemId.CRAWS_BOW && player.getWeapon().getCharges() > 0)
		{
			return true;
		}
		if(ArrayUtils.contains(BOFAARRAY, player.getWeapon().getId()))
		{
			return true;
		}
		player.cancelCombat();
		return false;
	}

	@Override
	protected void dropAmmunition(final int delay, final boolean destroy) {
	    if (player.getWeapon().getId() == ItemId.CRAWS_BOW && player.getWeapon().getCharges() > 0) {
            player.getChargesManager().removeCharges(DegradeType.OUTGOING_HIT);
        }
	}

	@Override
    protected boolean outOfAmmo() {
        return false;
    }

	@Override
	public int getMaxHit(final Player player, final double specialModifier, double activeModifier, final boolean ignorePrayers) {
	    return super.getMaxHit(player,
                specialModifier * (isBoosted() ? 1.5F : 1F), activeModifier, ignorePrayers);
	}
	
	@Override
	public int getRandomHit(final Player player, final Entity target, final int maxhit, final double modifier, final AttackType oppositeIndex) {
        return super.getRandomHit(player, target, maxhit,
                modifier * (isBoosted() ? 1.5F : 1F), oppositeIndex);
	}

	private boolean isBoosted() {
	    return player.getWeapon().getCharges() > 1000 && target instanceof NPC && WildernessArea.isWithinWilderness(target.getX(),
                target.getY());
    }


	@Override
	protected boolean initiateCombat(Player player)
	{
		if(player.getWeapon().getId() == ItemId.CRAWS_BOW && player.getWeapon().getCharges() <= 0)
		{
			player.sendMessage("You need to add more charges before you can use this!");
			return false;
		}
		if(player.getWeapon().getId() == ItemId.CRAWS_BOW_U)
		{
			player.sendMessage("This must be activated before you can use it!");
			return false;
		}
		return super.initiateCombat(player);
	}
}
