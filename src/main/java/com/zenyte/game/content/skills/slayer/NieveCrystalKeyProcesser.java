package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.content.skills.slayer.SlayerMaster;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import lombok.val;
import mgi.types.config.npcs.NPCDefinitions;

import java.util.Arrays;

/**
 * @author Matt - 7/26/2021
 */
public class NieveCrystalKeyProcesser extends DropProcessor {

	@Override
	public void attach() {
		appendDrop(new DisplayedDrop(ItemId.CRYSTAL_KEY, 4, 4) {
			public double getRate(final Player player, int id) {
				if (id == 8058) { //vorkath
					id = 8060;
				}
				val definitions = NPCDefinitions.get(id);
				val combatLevel = definitions.getCombatLevel();
				double rate = 0;
				if (combatLevel >= 100) {
					rate = -0.2 * Math.min(combatLevel, 350) + 120;
				} else {
					rate = Math.pow(0.2 * (combatLevel - 100), 2) + 100;
				}
				return rate;
			}
		});
		put(ItemId.CRYSTAL_KEY, new PredicatedDrop("This drop can only be obtained when killing the monster during a slayer assignment, assigned by Nieve."));
	}

	@Override
	public Item drop(final NPC npc, final Player killer, final Drop drop, final Item item) {
		if (killer.getSlayer().getAssignment() != null) {
			val area = killer.getSlayer().getAssignment().getArea();
			if (!drop.isAlways() && killer.getSlayer().isCurrentAssignment(npc) && killer.getSlayer().getAssignment().getMaster().equals(SlayerMaster.NIEVE)) {
				if (random((int) getBasicDrops().get(0).getRate(killer, npc.getId())) == 0) {
					return new Item(990, 5);
				}
			}
		}
		return item;
	}

	@Override
	public int[] ids() {
		val list = IntSets.synchronize(new IntOpenHashSet(100));
		val definitionsList = Arrays.asList(NPCDefinitions.definitions);
		definitionsList.parallelStream().forEach(definition -> {
			if (definition == null) {
				return;
			}
			val name = definition.getName().toLowerCase();
			for (val task : RegularTask.VALUES) {
				for (val t : task.getTaskSet()) {
					if (t.getSlayerMaster().equals(SlayerMaster.NIEVE)) {
						for (val monster : task.getMonsters()) {
							if (monster.equals(name)) {
								list.add(definition.getId());
							}
						}
					}
				}
			}
		});
		return list.toIntArray();
	}
}
