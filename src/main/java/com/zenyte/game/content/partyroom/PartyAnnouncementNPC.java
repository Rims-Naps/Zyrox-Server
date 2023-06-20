package com.zenyte.game.content.partyroom;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import lombok.val;

/**
 * @author Kris | 02/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PartyAnnouncementNPC extends NPC implements Spawnable {
    public PartyAnnouncementNPC(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    private int ticks = Utils.random(150);

    @Override
    public void processNPC() {
        super.processNPC();
        val partyRoom = FaladorPartyRoom.getPartyRoom();
        val frequency = partyRoom.getAnnouncementFrequency();
        if (frequency <= 0 || !partyRoom.getVariables().isAnnouncements()) {
            return;
        }
        if (ticks++ % frequency == 0) {
            val announcement = partyRoom.pollAnnouncement();
            if (announcement != null) {
                setForceTalk(new ForceTalk(partyRoom.pollAnnouncementColour() + announcement));
            }
        }
    }

    @Override
    public boolean validate(int id, String name) {
        return id == NpcId.EMILY || id == NpcId.KAYLEE || (id >= NpcId.GUARD_3269 && id <= NpcId.GUARD_3283 && name.equals("guard")) || name.equals("banker");
    }
}
