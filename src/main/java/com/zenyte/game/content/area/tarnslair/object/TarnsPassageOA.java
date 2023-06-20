package com.zenyte.game.content.area.tarnslair.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Set;

public class TarnsPassageOA implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        final Passage passage = Passage.PASSAGE_MAP.get(object.getPositionHash());
        if(passage != null) {
            player.lock(2);
            WorldTasksManager.schedule(() -> player.setLocation(passage.getTo()));
        }
    }

    @Override
    public Object[] getObjects() {
        final Set<Object> list = new ObjectOpenHashSet<>(Passage.VALUES.length);
        for(final Passage passage : Passage.VALUES) {
            list.add(passage.getPassage().getId());
        }
        return list.toArray();
    }
}
