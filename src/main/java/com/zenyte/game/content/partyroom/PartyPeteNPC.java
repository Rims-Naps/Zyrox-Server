package com.zenyte.game.content.partyroom;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import lombok.val;

/**
 * @author Kris | 02/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PartyPeteNPC extends NPC implements Spawnable {
    public PartyPeteNPC(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    private static final Animation danceAnimation = new Animation(784);

    private int danceDelay;

    void startDancing() {
        resetWalkSteps();
        setAnimation(danceAnimation);
        danceDelay = danceAnimation.getCeiledDuration() / 600;
    }

    @Override
    public void processNPC() {
        val partyRoom = FaladorPartyRoom.getPartyRoom();
        if (partyRoom.isPartyPeteCountdown()) {
            val countdown = partyRoom.getVariables().getCountdown();
            if (countdown < 100) {
                setForceTalk(new ForceTalk(Integer.toString(countdown + 1)));
                if (countdown <= 0) {
                    partyRoom.setPartyPeteCountdown(false);
                    WorldTasksManager.schedule(() -> setForceTalk(new ForceTalk("Dropping now!")));
                }
            }
        }

        if (danceDelay > 0) {
            danceDelay--;
            return;
        }
        //If the NPC isn't doing the dancing stuff, let's process random walk and all the other stuff.
        super.processNPC();
    }

    @Override
    public boolean validate(int id, String name) {
        return id == NpcId.PARTY_PETE;
    }
}
