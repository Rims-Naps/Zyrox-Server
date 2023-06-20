package com.zenyte.game.content.theatreofblood.plugin.entity;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;

import java.util.concurrent.TimeUnit;

/**
 * @author Corey
 * @since 23/05/2020
 */
public class VyreOrator extends NPC implements Spawnable {
    
    private static final ForceTalk[] MESSAGES = new ForceTalk[]{
            new ForceTalk("Welcome, one and all, to the Theatre of Blood."),
            new ForceTalk("Verzik Vitur cordially invites you to the Theatre."),
            new ForceTalk("Glory awaits those who enter the Theatre!"),
            new ForceTalk("Prove your worth in the Theatre!"),
            new ForceTalk("Only the Theatre can free you!")
    };
    private long chatDelay;
    
    public VyreOrator(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
    }
    
    @Override
    public void processNPC() {
        super.processNPC();
        if (chatDelay < Utils.currentTimeMillis()) {
            chatDelay = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(4);
            setForceTalk(MESSAGES[Utils.random(MESSAGES.length - 1)]);
        }
    }
    
    @Override
    public boolean validate(final int id, final String name) {
        return name.equalsIgnoreCase("vyre orator");
    }
    
}
