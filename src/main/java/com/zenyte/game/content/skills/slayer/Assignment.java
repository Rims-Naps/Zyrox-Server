package com.zenyte.game.content.skills.slayer;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.boss.cerberus.Cerberus;
import com.zenyte.game.content.boss.dagannothkings.DagannothKing;
import com.zenyte.game.content.boss.grotesqueguardians.boss.Dusk;
import com.zenyte.game.content.boss.kraken.Kraken;
import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.AlchemicalHydra;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.impl.kalphite.KalphiteQueen;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.CatacombsOfKourend;
import com.zenyte.game.world.region.area.KalphiteLair;
import com.zenyte.game.world.region.area.TaverleyDungeon;
import com.zenyte.game.world.region.area.WaterbirthDungeon;
import com.zenyte.game.world.region.area.godwars.GodwarsDungeonArea;
import lombok.*;

/**
 * @author Kris | 7. aug 2018 : 16:44:57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@NoArgsConstructor
public class Assignment {

	public Assignment(final Player player, final Slayer slayer, final SlayerTask task, final String taskName, final int initialAmount, final int amount, final SlayerMaster master) {
		this(player, slayer, task, taskName, initialAmount, amount, master, null);
	}

	public Assignment(final Player player, final Slayer slayer, final SlayerTask task, final String taskName, final int initialAmount, final int amount, final SlayerMaster master, final Class<? extends Area> area) {
		this.player = player;
		this.slayer = slayer;
		this.task = task;
		this.taskName = taskName;
		this.initialAmount = initialAmount;
		this.amount = amount;
		this.master = master;
		this.area = area;
		if (area != null) {
			this.areaName = area.getSimpleName();
		}
	}

	public void initialize(final Player player, final Assignment old) {
		this.player = player;
		slayer = player.getSlayer();
		task = old.task;
		initialAmount = old.initialAmount;
		amount = old.amount;
		master = old.master;
		taskName = old.taskName;
		if (taskName.equals("DAGANNOTH_SUPREME") || taskName.equals("DAGANNOTH_REX") || taskName.equals("DAGANNOTH_PRIME")) {
			taskName = "DAGANNOTH_KINGS";
		}
		task = getTask(taskName);
		areaName = old.areaName;
		if (areaName != null) {
			if (areaName.equalsIgnoreCase("KalphiteQueenLair")) {
				areaName = "KalphiteLair";
			}
			if (task instanceof RegularTask && master != null) {
				val regularTask = (RegularTask) task;
				val taskSet = regularTask.getCertainTaskSet(master);
				for (val area : taskSet.getAreas()) {
					if (area.getSimpleName().equals(areaName)) {
						this.area = area;
						break;
					}
				}
			}
		}
	}

	public static final SlayerTask getTask(final String name) {
	    for (val regularTask : RegularTask.VALUES) {
	        if (regularTask.name().equals(name)) {
	            return regularTask;
            }
        }
        for (val bossTask : BossTask.VALUES) {
            if (bossTask.name().equals(name)) {
                return bossTask;
            }
        }
        throw new IllegalStateException();
    }

	protected transient Player player;
	protected transient Slayer slayer;
	@Getter
	protected transient SlayerTask task;
	@Getter
	private transient Class<? extends Area> area;
	@Getter
	protected String taskName;
	@Getter @Setter
	protected int initialAmount;
	@Getter
	@Setter
	protected int amount;
	@Getter
	private SlayerMaster master;
	@Getter
	protected String areaName;

	private static final DiaryReward[] DIARY_REWARDS = {
		DiaryReward.MORYTANIA_LEGS4, DiaryReward.MORYTANIA_LEGS3,
		DiaryReward.MORYTANIA_LEGS2, DiaryReward.MORYTANIA_LEGS1
	};

	public boolean checkExceptions(final NPC npc, final Class<? extends Area> area) {
		if (npc instanceof AlchemicalHydra && task.equals(RegularTask.HYDRAS)) {
			return true;
		}
		//Only the Taverley dungeon area task will work.
		if (npc instanceof Cerberus && task.equals(RegularTask.HELLHOUNDS)) {
			return area == TaverleyDungeon.class;
		}
		//Only the Kalphite lair works, not the slayer-only cave.
		if (npc instanceof KalphiteQueen && task.equals(RegularTask.KALPHITE)) {
			return area == KalphiteLair.class;
		}
		//Only the Waterbirth dungeon will work.
		if (npc instanceof DagannothKing && task.equals(RegularTask.DAGANNOTH)) {
			return area == WaterbirthDungeon.class;
		}
		if (npc instanceof Dusk && task.equals(RegularTask.GARGOYLES)) {
			return true;
		}
		if (npc instanceof Kraken && task.equals(RegularTask.CAVE_KRAKEN)) {
			return true;
		}
		if (npc.getId() == NpcId.THERMONUCLEAR_SMOKE_DEVIL && task.equals(RegularTask.SMOKE_DEVILS)) {
			return true;
		}
		//Only the Catacombs of Kourend work.
		if (npc.getId() == NpcId.SKOTIZO && (task.equals(RegularTask.BLACK_DEMONS) || task.equals(RegularTask.GREATER_DEMONS))) {
			return area == CatacombsOfKourend.class;
		}
		if (area == GodwarsDungeonArea.class) {
			return player.getArea() instanceof GodwarsInstance;
		}
		return false;
	}

	public void checkAssignment(@NonNull final String name, final NPC npc) {
		if (!task.validate(name, npc)) {
			return;
		}
		if (master.equals(SlayerMaster.KRYSTILIA)) {
			if (!player.inArea("Wilderness")) {
				player.sendFilteredMessage("Only kills done within Wilderness will count towards Krystilia's slayer assignment.");
				return;
			}
		} else if (master.equals(SlayerMaster.KONAR_QUO_MATEN) && area != null) {
			if (!player.inArea(area) && !checkExceptions(npc, area)) {
				player.sendMessage("Konar quo Maten requires you to complete your slayer task in the " + GlobalAreaManager.getArea(area) + ".");
				return;
			}
		}
		var experience = task.getExperience(npc);
		if (player.inArea("Slayer Tower")) {
			for (int index = 0; index < DIARY_REWARDS.length; index++) {
				val reward = DIARY_REWARDS[index];
				if (reward.eligibleFor(player)) {
					experience *= 1 + (0.025 * (index + 1));
					break;
				}
			}
		}
		player.getSkills().addXp(Skills.SLAYER, experience);
		val braceletId = player.getEquipment().getId(EquipmentSlot.HANDS);
		if (braceletId == 21183 || braceletId == 21177) {
			if (Utils.random(3) == 0) {
				if (task != RegularTask.TZTOK_JAD && task != RegularTask.TZKAL_ZUK) {
					if (braceletId == 21183) {
						val slaughterUses = player.getNumericAttribute("bracelet of slaughter uses").intValue() + 1;
						val chargesLeft = (30 - slaughterUses);
						if (chargesLeft == 0) {
							player.sendMessage("Your bracelet of slaughter prevents your slayer count decreasing. <col=FF0000>It then crumbles to dust.");
						} else if (chargesLeft == 1) {
							player.sendMessage("Your bracelet of slaughter prevents your slayer count decreasing. <col=FF0000>It has one charge left.");
						} else {
							player.sendFilteredMessage("Your bracelet of slaughter prevents your slayer count decreasing. It has " + chargesLeft + " charges left.");
						}
						player.addAttribute("bracelet of slaughter uses", slaughterUses % 30);
						if (slaughterUses >= 30) {
							player.getEquipment().set(EquipmentSlot.HANDS, null);
						}
					} else {
						val expeditiousBraceletUses = player.getNumericAttribute("expeditious bracelet uses").intValue() + 1;
						val chargesLeft = (30 - expeditiousBraceletUses);
						if (chargesLeft == 0) {
							player.sendMessage("Your expeditious bracelet helps you progress your slayer task faster. <col=FF0000>It then crumbles to dust.");
						} else if (chargesLeft == 1) {
							player.sendMessage("Your expeditious bracelet helps you progress your slayer task faster. <col=FF0000>It has one charge left.");
						} else {
							player.sendFilteredMessage("Your expeditious bracelet helps you progress your slayer task faster. It has " + chargesLeft + " charges left.");
						}
						player.addAttribute("expeditious bracelet uses", expeditiousBraceletUses % 30);
						if (expeditiousBraceletUses >= 30) {
							player.getEquipment().set(EquipmentSlot.HANDS, null);
						}
						amount = Math.max(0, amount - 2);
						finish();
					}
					return;
				}
			}
		}
		if ((task == RegularTask.TZTOK_JAD && !name.equalsIgnoreCase("TzTok-Jad")) || (task == RegularTask.TZKAL_ZUK && !name.equalsIgnoreCase("TzKal-Zuk"))) {
			return;
		}
		amount--;
		finish();
	}

	private void finish() {
		val braceletId = player.getEquipment().getId(EquipmentSlot.HANDS);
		if (amount <= 0) {
			slayer.finishAssignment();
		} else if (amount % 10 == 0) {
			if (braceletId == 11972 || braceletId == 11974 || braceletId == 11118 || braceletId == 11120 || braceletId == 11122 || braceletId == 11124) {
				player.sendMessage("You still need to kill " + amount + " x " + task.getTaskName() + " to complete your current Slayer assignment");
			}
		}
	}

	public boolean isValid(final Player player, final NPC npc) {
		if (task.validate(npc.getName(player), npc)) {
			if (master.equals(SlayerMaster.KONAR_QUO_MATEN) && area != null) {
				return player.inArea(area) || checkExceptions(npc, area);
			}
			return true;
		}
		return false;
    }

}
