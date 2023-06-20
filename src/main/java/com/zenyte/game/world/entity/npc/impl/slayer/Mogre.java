package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import lombok.*;

import java.util.Optional;

/**
 * @author Kris | 10/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Mogre extends NPC implements Spawnable {

    private static final int TOTAL_WEIGHT;
    @Data
    private static class MogreQuote {
        private final int weight;
        private final ForceTalk message;
    }

    private static final MogreQuote[] quotes = new MogreQuote[]{
            new MogreQuote(11, new ForceTalk("Human hit me on the head!")),
            new MogreQuote(20, new ForceTalk("Human scare all da fishies!")),
            new MogreQuote(23, new ForceTalk("I get you!")),
            new MogreQuote(18, new ForceTalk("I smack you good!")),
            new MogreQuote(25, new ForceTalk("Smash stupid human!")),
            new MogreQuote(18, new ForceTalk("Tasty human!")),
            new MogreQuote(3, new ForceTalk("Da boom-boom kill all da fishies!")),
    };

    static {
        int weight = 0;
        for (val quote : quotes) {
            weight += quote.weight;
        }
        TOTAL_WEIGHT = weight;
    }

    private Optional<ForceTalk> getPseudoRandomForceTalk() {
        val roll = Utils.random(TOTAL_WEIGHT);
        var current = 0;
        for (val quote : quotes) {
            if ((current += quote.weight) >= roll) {
                return Optional.of(quote.message);
            }
        }
        return Optional.empty();
    }

    public Mogre(int id, Location tile, Direction facing, int radius) {
        super(id, tile, facing, radius);
        this.spawned = true;
    }

    private int ticks = 0;
    @Getter @Setter private String username;

    @Override
    public boolean isAttackable(final Entity e) {
        if (username == null || e instanceof NPC) {
            return true;
        }
        if (!((Player) e).getUsername().equalsIgnoreCase(username)) {
            ((Player) e).sendMessage("You can't kill someone else's mogre.");
            return false;
        }
        return true;
    }

    @Override
    public NPC spawn() {
        getPseudoRandomForceTalk().ifPresent(this::setForceTalk);
        return super.spawn();
    }

    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        if (username == null) {
            return;
        }
        World.getPlayer(username).ifPresent(player -> player.getTemporaryAttributes().remove("Is mogre spawned"));
    }

    @Override
    public void processNPC() {
        if (getAttackingDelay() < System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(5)) {
            if (++ticks >= 100) {
                finish();
                return;
            }
        } else {
            ticks = 0;
        }
        super.processNPC();
    }

    @Override
    public boolean validate(int id, String name) {
        return id == 2592;
    }
}
