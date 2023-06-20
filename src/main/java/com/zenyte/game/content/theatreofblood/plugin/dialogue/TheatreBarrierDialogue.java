package com.zenyte.game.content.theatreofblood.plugin.dialogue;

import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Tommeh | 6/1/2020 | 12:37 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TheatreBarrierDialogue extends Dialogue {

    private final TheatreArea room;
    private final WorldObject barrier;

    public TheatreBarrierDialogue(final Player player, final TheatreArea room, final WorldObject barrier) {
        super(player);
        this.room = room;
        this.barrier = barrier;
    }

    @Override
    public void buildDialogue() {
        options("Is your party ready to fight?", "Yes, let's begin.", "No, don't start yet.")
                .onOptionOne(() -> room.enterBossRoom(barrier, player));
    }
}
