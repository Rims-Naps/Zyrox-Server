package com.zenyte.game.world.entity.npc.drop.matrix;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author Kris | 12. sept 2018 : 19:35:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public abstract class DropProcessor {

    @Getter private final Long2ObjectOpenHashMap<PredicatedDrop> infoMap = new Long2ObjectOpenHashMap<>();
    @Getter private final List<DisplayedDrop> basicDrops = new ArrayList<>();

    protected void put(final int npcId, final int id, final PredicatedDrop drop) {
        if (infoMap.containsKey(id | ((long) npcId << 32L))) {
            throw new RuntimeException("Overriding predicated drop.");
        }
        infoMap.put(id | ((long) npcId << 32L), drop);
    }

    protected void put(final int id, final PredicatedDrop drop) {
        for (final int npcId : allIds) {
            if (infoMap.containsKey(id | ((long) npcId << 32L))) {
                throw new RuntimeException("Overriding predicated drop.");
            }
            infoMap.put(id | ((long) npcId << 32L), drop);
        }
    }

    protected int random(final int num) {
        //-1 because if wikia states 256 and you do random(256), you actually get a 1:257 rate.
        return Utils.random(num - 1);
    }

    /**
     * Appends a drop to the drop viewer interface. Note: The drops added here do not actually
     * get added to the drop table - this must be done manually in the processor.
     * @param drop the drop to display.
     */
    protected void appendDrop(final DisplayedDrop drop) {
        basicDrops.add(drop);
    }

    public abstract void attach();

    public void onDeath(final NPC npc, final Player killer) {
		
	}

    public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
	    return item;
    }
	
	public abstract int[] ids();

    protected final int[] allIds = ids();

    @Getter
	@AllArgsConstructor
	public static class PredicatedDrop {

	    private final BiPredicate<Player, Integer> predicate;
	    private final String information;

	    public PredicatedDrop(final String information) {
	        this.predicate = (player, npc) -> true;
	        this.information = information;
        }
    }

    @ToString
    @Getter
    @RequiredArgsConstructor
    public static class DisplayedDrop {

	    private final int id;
        private final int minAmount;
        private final int maxAmount;
	    private double rate;//1 : rate
        private BiPredicate<Player, Integer> predicate;
        private int[] npcIds;

        public DisplayedDrop(final int id, final int minAmount, final int maxAmount, final double rate) {
            this.id = id;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.rate = rate;
        }

        public DisplayedDrop(final int id, final int minAmount, final int maxAmount, final double rate, final BiPredicate<Player, Integer> predicate, final int... npcIds) {
            this.id = id;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.rate = rate;
            this.predicate = predicate;
            this.npcIds = npcIds;
        }

        public double getRate(final Player player, final int id) {
            return rate;
        }
    }
	
}
