package com.zenyte.game.parser.impl;

import com.google.common.base.Preconditions;
import com.zenyte.Constants;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Skills;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kris | 7. juuni 2018 : 04:05:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ItemRequirements {

	/**
	 * The collection holding all the optimized item requirements.
	 */
	private static final Int2ObjectMap<ItemRequirement> requirements = new Int2ObjectOpenHashMap<>();

	public static final ItemRequirement getRequirement(final int id) {
		return requirements.get(id);
	}

	/**
	 * Parses the item requirements on server load. Skips the construction requirement if the game is loaded live.
	 * @throws FileNotFoundException if requirements don't exist.
	 */
	public static final void parse() throws FileNotFoundException {
		val br = new BufferedReader(new FileReader("data/items/ItemRequirements.json"));
		val loadedRequirements = World.getGson().fromJson(br, LabelledItemRequirement[].class);
		//Skip construction requirements on the live game for max capes and hoods.
		val skipConstruction = !Constants.WORLD_PROFILE.isDevelopment() && !Constants.CONSTRUCTION;
		for (val req : loadedRequirements) {
			val labelledRequirements = new ObjectArrayList<>(req.requirements);
			val primitiveRequirements = new ObjectArrayList<ItemRequirement.PrimitiveRequirement>(labelledRequirements.size());
			for (val unidentifiedRequirement : labelledRequirements) {
				val skill = unidentifiedRequirement.getSkill();
				if (skipConstruction && skill == Skills.CONSTRUCTION) {
					val name = ItemDefinitions.getOrThrow(req.id).getName().toLowerCase();
					if (name.contains("max cape") || name.contains("max hood")) {
						continue;
					}
				}
				primitiveRequirements.add(new ItemRequirement.PrimitiveRequirement(skill, unidentifiedRequirement.getLevel()));
			}
			requirements.put(req.id, new ItemRequirement(req.id, primitiveRequirements));
		}
	}

	/**
	 * Clears the item requirements of a specific item.
	 * @param id the id of the item.
	 */
	public static final void clear(final int id) {
		requirements.remove(id);
	}

	/**
	 * Adds a new item requirement for the said item.
	 * @param id the id of the item.
	 * @param skill the id of the skill.
	 * @param level the level to set to.
	 */
	public static final void add(final int id, final int skill, final int level) {
		if (!Constants.WORLD_PROFILE.isDevelopment()) {
			return;
		}
		Preconditions.checkArgument(level > 1);
		Preconditions.checkArgument(level <= 99);
		Preconditions.checkArgument(skill >= 0);
		Preconditions.checkArgument(skill < Skills.SKILLS.length);
		Preconditions.checkArgument(ItemDefinitions.get(id) != null);
		val requirement = requirements.computeIfAbsent(id, __ -> new ItemRequirement(id, new ObjectArrayList<>()));
		requirement.requirements.removeIf(req -> req.skill == skill);
		requirement.requirements.add(new ItemRequirement.PrimitiveRequirement(skill, level));
	}

	/**
	 * Saves the item requirements in a properly formatted file, by their name rather than unambiguous ids.
	 */
	public static final void save() {
		if (!Constants.WORLD_PROFILE.isDevelopment()) {
			return;
		}
		val requirementsList = new ObjectArrayList<LabelledItemRequirement>();
		for (val requirement : requirements.values()) {
			val labelledRequirements = new ObjectArrayList<LabelledItemRequirement.LabelledRequirement>(requirement.requirements.size());
			for (val req : requirement.requirements) {
				labelledRequirements.add(new LabelledItemRequirement.LabelledRequirement(req.getLabelledSkill(), req.getLevel()));
			}
			requirementsList.add(new LabelledItemRequirement(requirement.id, ItemDefinitions.getOrThrow(requirement.id).getName(), labelledRequirements));
		}
		requirementsList.sort(Comparator.comparingInt(c -> c.id));
		try {
			val pw = new PrintWriter("data/items/ItemRequirements.json", "UTF-8");
			pw.println(World.getGson().toJson(requirementsList));
			pw.close();
		} catch (final Exception e) {
			log.error(Strings.EMPTY, e);
		}
	}

	@Getter
	@AllArgsConstructor
	public static final class ItemRequirement {
		private int id;
		private List<PrimitiveRequirement> requirements;

		@Getter
		public static final class PrimitiveRequirement extends Requirement {
			private final int skill;
			public PrimitiveRequirement(final int skill, final int level) {
				super(level);
				this.skill = skill;
			}

			@Override
			public String getLabelledSkill() {
				return Skills.SKILLS[skill];
			}
		}

	}

	@AllArgsConstructor
	private static final class LabelledItemRequirement {
		private int id;
		@SuppressWarnings("unused")
		private String description;
		private List<LabelledRequirement> requirements;

		private static final class LabelledRequirement extends Requirement {
			private final String skill;
			public LabelledRequirement(@NotNull final String skill, final int level) {
				super(level);
				this.skill = skill;
			}

			@Override
			public int getSkill() {
				val skillId = ArrayUtils.indexOf(Skills.SKILLS, skill);
				Preconditions.checkArgument(skillId >= 0);
				Preconditions.checkArgument(skillId < Skills.SKILLS.length);
				return skillId;
			}

			@Override
			public String getLabelledSkill() {
				return skill;
			}
		}
	}

	@AllArgsConstructor
	@Getter
	private static abstract class Requirement {
		private final int level;
		public abstract int getSkill();
		@SuppressWarnings("unused")
		public abstract String getLabelledSkill();
	}
	
}
