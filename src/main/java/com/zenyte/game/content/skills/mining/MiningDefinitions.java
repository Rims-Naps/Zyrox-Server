package com.zenyte.game.content.skills.mining;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.utils.Ordinal;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MiningDefinitions {

    public static final Int2ObjectOpenHashMap<OreDefinitions> ores = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectOpenHashMap<PickaxeDefinitions> tools = new Int2ObjectOpenHashMap<>();
    public static final Int2IntOpenHashMap rocks = new Int2IntOpenHashMap();
    public static final Int2ObjectOpenHashMap<PaydirtDefinitions> paydirts = new Int2ObjectOpenHashMap<>();

	public static void load() {
		for(OreDefinitions entry : OreDefinitions.values())
			for(int id : entry.getRocks())
				ores.put(id, entry);
		
		for(ShapeDefinitions entry : ShapeDefinitions.values())
			for(int id : entry.getOres())
				rocks.put(id, entry.getEmpty());
		
		for(PickaxeDefinitions entry : PickaxeDefinitions.values())
			tools.put(entry.getId(), entry);

		for(final PaydirtDefinitions value : PaydirtDefinitions.values())
			paydirts.put(value.getOre(), value);
	}

	public enum PaydirtDefinitions {

		COAL(453, 30, 0),
		GOLD(444, 40, 15),
		MITHRIL(447, 55, 30),
		ADAMANTITE(449, 70, 45),
		RUNITE(451, 85, 75)
		;

		@Getter private final int ore;
		@Getter private final int level;
		@Getter private final int xp;

		public static final Map<Integer, PaydirtDefinitions> entries = new HashMap<>();

		PaydirtDefinitions(final int ore, final int level, final int xp) {
			this.ore = ore;
			this.level = level;
			this.xp = xp;
		}
	}

	@Ordinal
	@Getter
	public enum OreDefinitions {

		/** Ores */
		TIN(741600, 438, 1, 1, 2, 17.5, 2, 1, true, 7485, 7486, 20407, 20443, 20444, 20445),
		COPPER(741600, 436, 1, 1, 2, 17.5, 2, 1, true, 7453, 7484, 20408, 20446, 20447, 20448),
		CLAY(741600, 434, 1, 1, 2, 5, 2, 1, true, 7454, 7487, 20449, 20450),
		BLURITE(741600, 668, 10, 10, 47, 17.5, 5, 1, true, 7462, 7495),
		IRON(741600, 440, 15, 15, 3, 35, 6, 1, true, 7455, 7488, 20422, 20423, 20425),
		SILVER(741600, 442, 20, 25, 17, 40, 8, 1, true, 7490, 7457),
		COAL(296640, 453, 30, 35, 12, 50, 6, 1, true, 7456, 7489, 20410, 20411, 20412, 20413),
		GOLD(296640, 444, 40, 45, 17, 65, 9, 1, true, 7458, 7491),
		MITHRIL(148320, 447, 55, 65, 33, 80, 12, 1, true, 7459, 7492, 20418, 20419, 20420, 20421),
		LOVAKITE(245562, 13356, 65, 100, 50, 10, 0, 1, true, 28596, 28597),
		ADAMANTITE(59328, 449, 70, 80, 67, 95, 15, 1, true, 7460, 7493, 20414, 20415, 20417),
		RUNITE(42377, 451, 85, 99, 150, 125, 18, 1, false, 7461, 7494),

		/** Wilderness Resource Area */
		RUNE_NOTED(42377, 452, 85, 99, 150, 90, 15, 2, true, 40065),
		ADAMANTITE_NOTED(59328, 450, 70, 80, 67, 42, 15, 2, true, 40062),
		MITH_NOTED(148320, 448, 55, 65, 33, 40, 15, 3, true, 40064),
		GOLD_NOTED(296640, 445, 40, 45, 17, 32, 15, 3, true, 40063),
		COAL_NOTED(296640, 454, 30, 30, 12, 25, 6, 5, true, 40066),
		IRON_NOTED(741600, 441, 15, 15, 3, 17, 6, 5, true, 40067),


		/** Motherlode mine */
		PAYDIRT(-1, 12011, 30, 50, 150, 60, 0, 0, true, 26661, 26662, 26663, 26664),

		/** Unique */
        SANDSTONE(741600, -1, 35, 25, 8, -1, 0, 1, true, 8727),
		GRANITE(741600, -1, 45, 35, 8, -1, 0, 1, true, 7467),
		GEM(211886, -1, 40, 60, 40, 65, 0, 1, false, 7463, 7464),

		RUNITE_GOLEM_ROCKS(42377, 451, 85, 99, -1, 125, 0, 1, false),
		ROCKSLIDE(-1, -1, 99, 15, 20, 0, 0, 1, false, 27062),
		ROCKFALL(-1, -1, 30, 20, 30, 10, 0, 1, false, 26679, 26680),

		URT_SALT(-1, 22597, 72, 70, 3, 5, 0, 11, false, 33254),
		EFH_SALT(-1, 22595, 72, 70, 15, 5, 0, 11, false, 33255),
		TE_SALT(-1, 22593, 72, 70, 15, 5, 0, 11, false, 33256),
		BASALT(-1, 22603, 72, 85, 15, 5, 0, 11, false, 33257),

		ESSENCE(-1, 7936, 1, 1, -1, 5, 0, 0, false, 7471),
		VOLCANIC_ASH(741600, 21622, 22, 5, 50, 10, 0, 11, false, 30985),
		AMETHYST(46350, 21347, 92, 100, 42, 240, 0, 3, false, 30371, 30372),

		/** Castle wars */
		CWARS_ROCKS(-1, -1, 1, 35, 100, 0, 0, 1, false, 4437, 4438),
		CWARS_WALL(-1, -1, 1, 35, 100, 0, 0, 1, false, 4448),

		/** Crytal Rocks */
		CRYSTAL_ROCKS(741600, 30560, 94, 45, 125, 350, 0, 6, false, 7465, 7466, 8725),

		/** Star Mining */
		FALLING_STAR(741600, 30546, 70, 10, 12000, 80, 0, 600, false, 40040),
		FALLING_STAR1(741600, 30546, 80, 8, 12000, 100, 0, 600, false, 40041),
		FALLING_STAR2(741600, 30546, 90, 5, 12000, 150, 0, 600, false, 40042),
		;
		
		private final int baseClueGeodeChance;
		private int ore;
		private int level;
		private int speed;
		private int time;
		private final int incinerationExperience;
		private double xp;
		private final int depletionRate;
		private int[] rocks;
		private boolean extraOre;
		
		OreDefinitions(final int baseClueGeodeChance, int ore, int level, final int speed, int time, double xp, int incinerationExperience, int depletionRate, boolean extraOre, int... rocks) {
			this.baseClueGeodeChance = baseClueGeodeChance;
			this.incinerationExperience = incinerationExperience;
			this.ore = ore;
			this.level = level;
			this.speed = speed;
			this.time = time;
			this.xp = xp;
			this.depletionRate = depletionRate;
			this.extraOre = extraOre;
			this.rocks = rocks;
		}
		
		public static OreDefinitions getDef(int id) {
			return ores.get(id);
		}
		
		public String getName() {
			return ItemDefinitions.getOrThrow(this.ore).getName().toLowerCase().replace(" ore", "");
		}
		
		public boolean isSmeltable() {
			return this.equals(BLURITE) || this.equals(IRON) || this.equals(SILVER) || this.equals(GOLD) || this.equals(MITHRIL) || this.equals(ADAMANTITE) || this.equals(RUNITE);
		}
	}

	@AllArgsConstructor
	public enum PickaxeDefinitions {

		BRONZE(1265, 1, new Animation(625), new Animation(6753), 6),
		IRON(1267, 1, new Animation(626), new Animation(6754), 6),
		STEEL(1269, 6, new Animation(627), new Animation(6755), 5),
		BLACK(12297, 11, new Animation(3873), new Animation(6109), 5),
		MITHRIL(1273, 21, new Animation(629), new Animation(6757), 4),
		ADAMANT(1271, 31, new Animation(628), new Animation(6756), 3),
		RUNE(1275, 41, new Animation(624), new Animation(6752), 2),
		GILDED(23276, 41, new Animation(8313), new Animation(8312), 2),
		DRAGON(11920, 61, new Animation(7139), new Animation(6758), -1) {
			@Override
			public int getMineTime() {
				return Utils.random(1, 2);
			}
		},
		DRAGON_OR(12797, 61, new Animation(643), new Animation(335), -1) {
			@Override
			public int getMineTime() {
				return Utils.random(1, 2);
			}
		},
		THIRD_AGE(20014, 61, new Animation(7283), new Animation(7282), -1) {
			@Override
			public int getMineTime() {
				return Utils.random(1, 2);
			}
		},
		UNCHARGED_INFERNAL(13244, 61, new Animation(4482), new Animation(4481), -1) {
			@Override
			public int getMineTime() {
				return Utils.random(1, 2);
			}
		},
		INFERNAL(13243, 61, new Animation(4482), new Animation(4481), -1) {
            @Override
            public int getMineTime() {
                return Utils.random(1, 2);
            }
        },
		CRYSTAL(30742, 71, new Animation(10003), new Animation(10004), 1),
        ;
		
		@Getter
		private int id;
		@Getter
		private int level;
		@Getter
		private Animation anim;
		@Getter private Animation alternateAnimation;
		@Getter private final int mineTime;

		private static final PickaxeDefinitions[] values = values();
		
		public static PickaxeDefinitions getDef(int id) {
			return tools.get(id);
		}

		@AllArgsConstructor
        @Getter
		public static final class PickaxeResult {
		    private final PickaxeDefinitions definitions;
		    private final Container container;
		    private final int slot;
		    private final Item item;
        }

		public static final Optional<PickaxeResult> get(final Player player, final boolean checkInventory) {
		    val level = player.getSkills().getLevel(Skills.MINING);
		    val inventory = player.getInventory().getContainer();
		    val weapon = player.getEquipment().getId(EquipmentSlot.WEAPON);
		    PickaxeResult bestOptionalMatch = null;
		    for (int i = values.length - 1; i >= 0; i--) {
		        val def = values[i];
		        if (level < def.level)
		            continue;
		        if (weapon == def.id) {
					if (def == INFERNAL && player.getWeapon().getCharges() <= 0) {
						bestOptionalMatch = new PickaxeResult(def, player.getEquipment().getContainer(), 3, player.getWeapon());
					} else {
						return Optional.of(new PickaxeResult(def, player.getEquipment().getContainer(), 3, player.getWeapon()));
					}
                }
		        if (checkInventory) {
                    for (int slot = 0; slot < 28; slot++) {
                        val item = inventory.get(slot);
                        if (item == null || item.getId() != def.id) {
                            continue;
                        }
						if (def == INFERNAL && item.getCharges() <= 0 && bestOptionalMatch == null) {
							bestOptionalMatch = new PickaxeResult(def, player.getInventory().getContainer(), slot, item);
						} else {
							return Optional.of(new PickaxeResult(def, player.getInventory().getContainer(), slot, item));
						}
                    }
                }
            }
		    return Optional.ofNullable(bestOptionalMatch);
        }
	}
	
	public enum ShapeDefinitions {

		ROUND(7468, 7453, 7485, 7455, 7457, 7491, 7459, 7493, 7461, 7463, 7487, 7489),
		SHARP(7469, 7484, 7486, 7458, 7488, 7490, 7492, 7460, 7494, 7464, 7454, 7456),
		ROCKSLIDE(27063, 27062),
        VOLCANIC_ASH(30986, 30985),
        WALL(30373, 30371, 30372),
		WALL_LEFT(20441, 20410, 20414, 20418, 20422, 20443, 20446, 20449),
		WALL_MIDDLE(20440, 20413, 20407, 20408, 20417, 20421, 20425),
		WALL_RIGHT(20439, 20411, 20415, 20419, 20423, 20444, 20447, 20450),
		WALL_CROSS(20442, 20412, 20420, 20445, 20448),
		STARS(40043, 40040, 40041, 40042),
		SALT_ROCK(33253, 33254, 33255, 33256, 33257);
		
		@Getter
		private int empty;
		@Getter
		private int[] ores;
		
		ShapeDefinitions(int empty, int... ores) {
			this.empty = empty;
			this.ores = ores;
		}
		
		public static int getEmpty(int id) {
			return rocks.getOrDefault(id, 7468);
		}
		
	}
	
}
