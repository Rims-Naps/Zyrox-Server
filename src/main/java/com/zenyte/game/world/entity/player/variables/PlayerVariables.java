package com.zenyte.game.world.entity.player.variables;

import com.google.common.eventbus.Subscribe;
import com.zenyte.Constants;
import com.zenyte.GameEngine;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.BonusCoxManager;
import com.zenyte.game.BonusTobManager;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.loyalty.LoyaltyManager;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import com.zenyte.game.world.region.area.freakyforester.FreakyForesterArea;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import com.zenyte.plugins.events.LogoutEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Slf4j
public class PlayerVariables {

	private transient Player player;

	public PlayerVariables(final Player player) {
		this.player = player;
		resetCharacterSaveTimer();
	}

	public void set(final PlayerVariables copy) {
        copy.scheduled.forEach((k, v) -> {
            try {
                val tickTask = TickVariable.valueOf(TickVariable.class, k);
                scheduled.put(k, new Variable(v.ticks, tickTask.task, tickTask.messages));
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        });
        bonusXP = copy.bonusXP;
        raidsBoost = copy.raidsBoost;
		tobBoost = copy.tobBoost;
        diedInUnsafeRaids = copy.diedInUnsafeRaids;
        runEnergy = copy.runEnergy;
		playTime = copy.playTime;
		absorption = copy.absorption;
		skulled = copy.skulled;
		lastLogin = copy.lastLogin;
		raidAdvertsQuota = copy.raidAdvertsQuota;
		ardougneFarmTeleports = copy.ardougneFarmTeleports;
		fountainOfRuneTeleports = copy.fountainOfRuneTeleports;
		fishingColonyTeleports = copy.fishingColonyTeleports;
		rellekkaTeleports = copy.rellekkaTeleports;
		sherlockTeleports = copy.sherlockTeleports;
		faladorPrayerRecharges = copy.faladorPrayerRecharges;
		runReplenishments = copy.runReplenishments;
		freeAlchemyCasts = copy.freeAlchemyCasts;
		cabbageFieldTeleports = copy.cabbageFieldTeleports;
		nardahTeleports = copy.nardahTeleports;
		spellbookSwaps = copy.spellbookSwaps;
		claimedBattlestaves = copy.claimedBattlestaves;
		grappleAndCrossbowSearches = copy.grappleAndCrossbowSearches;
		this.teletabPurchases = copy.teletabPurchases;
		this.zulrahResurrections = copy.zulrahResurrections;
		zulrahResurrections = copy.zulrahResurrections;
		kourendWoodlandTeleports = copy.kourendWoodlandTeleports;
		mountKaruulmTeleports = copy.mountKaruulmTeleports;
		godwarsGhommalHiltTeleports = copy.godwarsGhommalHiltTeleports;
		morulrekGhommalHiltTeleports = copy.morulrekGhommalHiltTeleports;
	}

	private transient long lastClanKick;
	private double runEnergy = 100;

	private transient int potionDelay;
	private transient int foodDelay;
	private transient int karambwanDelay;
	private transient int nextScheduledCharacterSave;

	private transient int ticksInterval;

	private int bonusXP;
	private int raidsBoost;
	private int tobBoost;
	private boolean diedInUnsafeRaids;
	private int playTime;
	private int lastLogin;
	private int raidAdvertsQuota = 15;
	private int slimePitTeleports;
	private int ardougneFarmTeleports;
	private int fountainOfRuneTeleports;
	private int fishingColonyTeleports;
	private int sherlockTeleports;
	private int faladorPrayerRecharges;
	private int rellekkaTeleports;
	private int runReplenishments;
	private int freeAlchemyCasts;
	private int cabbageFieldTeleports;
	private int nardahTeleports;
	private int kourendWoodlandTeleports;
	private int mountKaruulmTeleports;
	private int godwarsGhommalHiltTeleports;
	private int morulrekGhommalHiltTeleports;
	private int spellbookSwaps;
	private int zulrahResurrections;
	private int absorption;
	private int overloadType;
	private int divineType;
	private int divinebastionType;
	private int divinebattlemageType;
	private int divinecombatType;
	private int divinerangedType;
	private int divinemagicType;
	private int divineattackType;
	private int divinedefenceType;
	private int divinestrengthType;
	private int toleranceTimer;
	private int specRegeneration, statRegeneration, healthRegeneration;
	private int grappleAndCrossbowSearches;
	private int teletabPurchases;
	private boolean skulled;
	private boolean claimedBattlestaves;
	private final Map<String, Variable> scheduled = new LinkedHashMap<>();


	private void resetCharacterSaveTimer() {
		nextScheduledCharacterSave = Constants.WORLD_PROFILE.isDevelopment() ? (int) TimeUnit.SECONDS.toTicks(15) : ((int) TimeUnit.MINUTES.toTicks(5));
    }
    public enum HealthRegenBoost {
	    RAPID_HEAL,
        REGEN_BRACELET,
        HITPOINTS_CAPE
    }

	@Getter private transient final Set<HealthRegenBoost> healthRegenBoosts = EnumSet.noneOf(HealthRegenBoost.class);
	private transient int cycle = calculateCycle();

	public void addBoost(final HealthRegenBoost boost) {
	    healthRegenBoosts.add(boost);
	    val current = cycle;
	    cycle = calculateCycle();
	    if (current > cycle || boost == HealthRegenBoost.RAPID_HEAL) {
            healthRegeneration = 0;
        }
    }

    public void removeBoost(final HealthRegenBoost boost) {
        healthRegenBoosts.remove(boost);
        cycle = calculateCycle();
        if (boost == HealthRegenBoost.RAPID_HEAL) {
            healthRegeneration = 0;
        }
    }

    public void resetTeleblock() {
	    scheduled.remove(TickVariable.TELEBLOCK.toString());
	    scheduled.remove(TickVariable.TELEBLOCK_IMMUNITY.toString());
    }

    @Subscribe
    public static final void onLogout(final LogoutEvent event) {
	    event.getPlayer().getVariables().resetTeleblock();
    }

    private int calculateCycle() {
	    if (healthRegenBoosts.isEmpty()) {
	        return 100;
        }
	    int delay = 100;
	    if (healthRegenBoosts.contains(HealthRegenBoost.REGEN_BRACELET)) {
	        delay /= 2;
        }
	    if (healthRegenBoosts.contains(HealthRegenBoost.RAPID_HEAL) ||healthRegenBoosts.contains(HealthRegenBoost.HITPOINTS_CAPE)) {
	        delay /= 2;
        }
	    return delay;
    }

	/**
	 * Schedules a tickvariable to be executed on the player if there isn't one
	 * of the same type with longer duration already executing.
	 * 
	 * @param duration
	 *            the duration of the variable.
	 * @param variable
	 *            the variable itself.
	 */
	public void schedule(final int duration, final TickVariable variable) {
		val existing = scheduled.get(variable.toString());
		if (existing != null) {
			if (existing.ticks >= duration) {
				return;
			}
		}
		scheduled.put(variable.toString(), new Variable(duration, variable.task, variable.messages));
	}

	public void cancel(final TickVariable variable) {
	    scheduled.remove(variable.toString());
    }

	public void resetScheduled() {
		scheduled.forEach((k, v) -> {
			if (v.task != null) {
				v.task.run(player, 0);
			}
		});
		scheduled.clear();
        if (player.getEquipment().getId(EquipmentSlot.AMULET) == 22557) {
            player.getVariables().setPermanentSkull();
        }
	}

	public int getTime(final TickVariable variable) {
		val scheduled = this.scheduled.get(variable.toString());
		if (scheduled == null) {
			return 0;
		}
		return scheduled.ticks;
	}

	private static final int[] processedSkills;

	static {
	    processedSkills = new int[Skills.SKILLS.length - 1];
	    int index = 0;
	    for (int i = 0; i < Skills.SKILLS.length; i++) {
	        if (i == Skills.PRAYER)
	            continue;
	        processedSkills[index++] = i;
        }
    }

    public void onLogin() {
	    if (getTime(TickVariable.STAMINA_ENHANCEMENT) > 0) {
            player.getVarManager().sendBit(25, 1);
        }
    }

	public void process() {
		playTime++;
		specRegeneration++;
		toleranceTimer++;

		if (raidsBoost > 0) {
			if (!Constants.BOOSTED_COX) {
				val raidsBitValue = player.getVarManager().getBitValue(5425);
				if (raidsBitValue > 0 && raidsBitValue < 5) {
					raidsBoost--;
					if (raidsBoost == 0) {
						player.sendMessage(Colour.RED.wrap("Your private COX Boost has ran out!"));
					}
				}
			}
		}
		if (tobBoost > 0) {
			if (!Constants.BOOSTED_TOB) {
				if (VerSinhazaArea.getParty(player) != null) {
					if (VerSinhazaArea.getParty(player).getRaid() != null) {
						val bool = (player.getArea() instanceof TheatreArea) && (!VerSinhazaArea.getParty(player).getRaid().isCompleted());
						if (bool) {
							tobBoost--;
							if (tobBoost == 0) {
								player.sendMessage(Colour.RED.wrap("Your private TOB Boost has ran out!"));
							}
						}
					}
				}
			}
		}
		if (bonusXP > 0) {
			if (!Constants.BOOSTED_XP) {
				bonusXP--;
				if (bonusXP == 0) {
					player.sendMessage(Colour.RED.wrap("Your private bonus experience has ran out!"));
				}
			}
		}

		if (Utils.random(Constants.randomEvent) == 0) {
		    if (!player.isLocked() && !player.isFinished() && !player.isDead() && !player.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL) && !player.isStaff()) {
                val lastEvent = player.getNumericAttribute("last random event").longValue();
                if (lastEvent + TimeUnit.HOURS.toMillis(1) < System.currentTimeMillis() && player.getActionManager().getLastAction() >= (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))) {
                    if (!player.isUnderCombat() && !(player.getActionManager().getAction() instanceof PlayerCombat)) {
                        val area = player.getArea();
                        if (!(area instanceof RandomEventRestrictionPlugin)) {
							if (Utils.random(1) == 0)
							{
								EvilBobIsland.teleport(player);
							} else
							{
								FreakyForesterArea.teleport(player);
							}
						}
                    }
                }
            }
        }

		//Resynchronize the client timers for time spent online and server online time in general.
		if (++ticksInterval % 100 == 0) {
			val varManager = player.getVarManager();
            varManager.sendVar(3500, (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - GameEngine.SERVER_START_TIME));
            varManager.sendVar(3501, (int) (playTime * 0.6F));
            varManager.sendVar(3506, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusXpManager.expirationDate - System.currentTimeMillis())));
            varManager.sendVar(3507, (int) (raidsBoost * 0.6F));
            varManager.sendVar(3801, (int) (bonusXP * 0.6F));
			varManager.sendVar(3804, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusCoxManager.expirationDateCox - System.currentTimeMillis())));
			varManager.sendVar(3510, (int) (tobBoost * 0.6F));
			varManager.sendVar(3805, Math.max(0, (int) TimeUnit.MILLISECONDS.toSeconds(BonusTobManager.expirationDateTob - System.currentTimeMillis())));
			player.refreshGameClock();
        }
		if (ticksInterval % LoyaltyManager.LOYALTY_POINTS_INTERVAL_TICKS == 0) {
			player.getLoyaltyManager().informSession((int) (ticksInterval / LoyaltyManager.LOYALTY_POINTS_INTERVAL_TICKS));
		}

		if (--nextScheduledCharacterSave <= 0) {
            resetCharacterSaveTimer();
            CoresManager.getLoginManager().submitSaveRequest(player);
        }
		if (foodDelay > 0) {
		    foodDelay--;
        }
		if (potionDelay > 0) {
		    potionDelay--;
        }
		if (karambwanDelay > 0) {
		    karambwanDelay--;
        }
		if (player.getCombatDefinitions().getSpecialEnergy() == 100) {
			specRegeneration = 0;
		}
		if (specRegeneration % 50 == 0) {
			if (player.getCombatDefinitions().getSpecialEnergy() < 100) {
				val energy = player.getCombatDefinitions().getSpecialEnergy();
				player.getCombatDefinitions().setSpecialEnergy(Math.min(100, energy + 10));
			}
		}
		statRegeneration++;
		if (statRegeneration % /*(manager.isPrayerActive(Prayer.RAPID_RESTORE) ? 25 : 50)*/100 == 0) {//TODO: Rewrite
		    // this entire shit.
			for (int skill : processedSkills) {
				final int currentLevel = player.getSkills().getLevel(skill);
				final int normalLevel = player.getSkills().getLevelForXp(skill);
				if (currentLevel > normalLevel) {
					player.getSkills().setLevel(skill, currentLevel - 1);
				} else if (currentLevel < normalLevel) {
					if (skill != Skills.HITPOINTS) {
						player.getSkills().setLevel(skill, currentLevel + 1);
					}
				}
			}
		}
		healthRegeneration++;
		if (healthRegeneration % cycle == 0) {
			if (!player.isDead() && player.getHitpoints() < player.getMaxHitpoints()) {
				player.setHitpoints(player.getHitpoints() + 1);
			}
		}
		if (!scheduled.isEmpty()) {
			scheduled.values().removeIf(variable -> {
				variable.ticks--;
				if (variable.messages != null) {
					val message = variable.messages.get(variable.ticks);
					if (message != null) {
						player.sendMessage(message);
					}
				}
				if (variable.task != null) {
					variable.task.run(player, variable.ticks);
				}
				return variable.ticks <= 0;
			});
		}
	}

	public void setRunEnergy(double runEnergy) {
		if (runEnergy > 100) {
			runEnergy = 100;
		} else if (runEnergy < 0) {
			runEnergy = 0;
		}
		if (this.runEnergy == runEnergy) {
		    return;
        }
		this.runEnergy = runEnergy;
		player.getPacketDispatcher().sendRunEnergy();
	}

    public void forceRunEnergy(double runEnergy) {
	    if (runEnergy < 0) {
            runEnergy = 0;
        }
        this.runEnergy = runEnergy;
        player.getPacketDispatcher().sendRunEnergy();
    }

    public void setPermanentSkull() {
        this.skulled = true;
        schedule(Integer.MAX_VALUE, TickVariable.SKULL);
        player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
    }

	public void setSkull(final boolean skulled) {
		this.skulled = skulled;
		if (skulled) {
			schedule((int) TimeUnit.MINUTES.toTicks(20), TickVariable.SKULL);
		}
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void removeSkull() {
		this.skulled = false;
		scheduled.remove(TickVariable.SKULL.toString());
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

}
