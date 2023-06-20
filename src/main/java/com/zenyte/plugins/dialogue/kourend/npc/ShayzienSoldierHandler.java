package com.zenyte.plugins.dialogue.kourend.npc;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

public class ShayzienSoldierHandler extends NPC implements Spawnable {


    public ShayzienSoldierHandler(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public void finish() {
        setTransformation(id - 1);
        super.finish();
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if(!getCombat().underCombat() && !isDead() && getHitpointsAsPercentage() > 0) {
            if(getId() % 2 == 1) {
                setTransformation(id - 1);
            }
        }
    }

    @Override
    public int getRespawnDelay() {
        return 10;
    }

    @Override
    protected void drop(final Location tile) {
        val killer = getDropRecipient();
        if (killer == null) {
            return;
        }
        onDrop(killer);
        dropItem(killer, new Item(getFirstUnobtainedPiece(getDropRecipient(), getId())), location, true);
    }

    private int getFirstUnobtainedPiece(Player player , int id) {
        int min = 13357, max = 13361;
        switch(getId()) {
            case 6904:
            case 6905:
                min = 13357;
                max = 13361;
                break;
            case 6906:
            case 6907:
                min = 13362;
                max = 13366;
                break;
            case 6908:
            case 6909:
                min = 13367;
                max = 13371;
                break;
            case 6910:
            case 6911:
                min = 13372;
                max = 13376;
                break;
            case 6912:
            case 6913:
                min = 13377;
                max = 13381;
                break;
        }
        for(int i = min; i <= max; i++) {
            if(!player.containsItem(i)) {
                return i;
            }
        }
        return min + Utils.random(4);
    }

    @Override
    public boolean validate(int id, String name) {
        return id >= 6904 && id <= 6913;
    }
}
