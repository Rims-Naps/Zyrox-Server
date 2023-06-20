package com.zenyte.plugins.object;

import com.zenyte.game.content.chambersofxeric.storageunit.StorageUnit;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 4. mai 2018 : 18:45:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class StorageUnitOPlugin implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
	    if (object.getId() == 30107) {
            player.getPrivateStorage().open(-1);
	        return;
        }
	    val optionalRaid = player.getRaid();
	    if (!optionalRaid.isPresent()) {
	        return;
        }
	    val id = object.getId();
	    val raid = optionalRaid.get();
	    if (raid.isConstructingStorage()) {
	        player.sendMessage(id == 29769 ? "This storage unit is being built!" : "This storage unit is being upgraded!");
	        return;
        }
		if (option.equals("Private")) {
			player.getPrivateStorage().open(id == 29770 ? 30 : id == 29779 ? 60 : 90);
		} else if (option.equals("Shared")) {
		    raid.constructOrGetSharedStorage().open(player);
		} else if (option.equalsIgnoreCase("Build") || option.equalsIgnoreCase("Upgrade")) {
            StorageUnit.openCreationMenu(player, object);
        }
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 30107, 29769, 29770, 29779, 29780 };
	}

}
