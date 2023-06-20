package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */

public class GrandBookshelf implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Search")) {
            player.sendMessage("Why do you want to read after such a battle? Nerd.");
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.GRAND_BOOKSHELF, ObjectId.GRAND_BOOKSHELF_33001, ObjectId.GRAND_BOOKSHELF_33002, ObjectId.GRAND_BOOKSHELF_33003};
    }
}
