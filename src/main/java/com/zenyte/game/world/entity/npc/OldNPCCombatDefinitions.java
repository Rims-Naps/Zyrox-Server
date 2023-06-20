package com.zenyte.game.world.entity.npc;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OldNPCCombatDefinitions {

	@Getter @Setter private int id;
	@Getter private boolean aggressive;
	@Getter private boolean poisonous;
	@Getter private boolean undead;
	@Getter private boolean poisonImmune;
	@Getter private boolean venomImmune;
	@Getter private int slayerRequirement;
	@Getter @Setter private int hitpoints = 1;
	private int attackSpeed = 5;
	@Getter @Setter private int maxHit;
	@Getter private String stats;
	@Getter private String bonuses;
	private int[] combatStats = new int[5];
	private int[] combatBonuses = new int[13];
	@Getter private int attackAnimation = -1;
	@Getter private int blockAnimation = -1;
	@Getter private int deathAnimation = -1;
	@Getter @Setter private Animation attackAnim = Animation.STOP;
	@Getter @Setter private Animation blockAnim = Animation.STOP;
	@Getter @Setter private Animation deathAnim = Animation.STOP;
	@Getter private List<String> weaknesses;
	@Getter @Setter private String attackStyle = "Melee";
	
	public OldNPCCombatDefinitions(final int id) {
		this.id = id;
	}
	
	public OldNPCCombatDefinitions(final OldNPCCombatDefinitions clone) {
		id = clone.id;
		aggressive = clone.aggressive;
		poisonous = clone.poisonous;
		undead = clone.undead;
		poisonImmune = clone.poisonImmune;
		venomImmune = clone.venomImmune;
		slayerRequirement = clone.slayerRequirement;
		hitpoints = clone.hitpoints;
		attackSpeed = clone.attackSpeed;
		maxHit = clone.maxHit;
		stats = clone.stats;
		bonuses = clone.bonuses;
		combatStats = clone.combatStats;
		combatBonuses = clone.combatBonuses;
		attackAnimation = clone.attackAnimation;
		blockAnimation = clone.blockAnimation;
		deathAnimation = clone.deathAnimation;
		weaknesses = clone.weaknesses;
		attackStyle = clone.attackStyle;
		attackAnim = new Animation(attackAnimation);
		blockAnim = new Animation(blockAnimation);
		deathAnim = new Animation(deathAnimation);
		if (combatStats == null)
			combatStats = new int[5];
		if (combatBonuses == null)
			combatBonuses = new int[13];
	}
	
	/**
     * Used for parsing wikipedia.
     */
    @SuppressWarnings("unchecked")
	public void parseTableRow(final String key, final Object value) {
        switch (key.toLowerCase().trim()) {
            case "hitpoints":
                if (value.toString().contains(",")) {
                    hitpoints = Integer.parseInt(Utils.sanitiseValue(value.toString().split(",")[0]));
                } else if (value.toString().toLowerCase().contains("unknown")
                        || value.toString().toLowerCase().contains("varies")
                        || value.toString().toLowerCase().contains("see")) {
                    hitpoints = 0;
                } else if (value.toString().contains("/")) {
                    hitpoints = Integer.parseInt(Utils.sanitiseValue(value.toString().split("/")[0]));
                } else if (value.toString().contains(" ")) {
                    hitpoints = Integer.parseInt(Utils.sanitiseValue(value.toString()).split(" ")[0]);
                } else {
                    hitpoints = Integer.parseInt(value.toString());
                }
                break;
            case "aggressive":
                aggressive = Boolean.parseBoolean(Utils.sanitiseValue(value.toString()));
                break;
            case "poisonous":
                poisonous = Boolean.parseBoolean(Utils.sanitiseValue(value.toString()));
                break;
            case "max hit":
                try {
                    if (value.toString().contains("nose peg")) {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString().split(" ")[0]));
                    } else if (value.toString().contains("earmuffs")) {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString().split(" ")[2]));
                    } else if (value.toString().contains(",")) {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString().split(",")[0]));
                    } else if (value.toString().toLowerCase().contains("unknown")
                            || value.toString().toLowerCase().contains("varies")
                            || value.toString().toLowerCase().contains("see table")) {
                        maxHit = 0;
                    } else if (value.toString().contains(" ")) {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString()).split(" ")[0]);
                    } else if (value.toString().contains("/")) {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString().split("/")[0]));
                    } else if (value.toString().contains("-")) {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString().split("-")[0]));
                    } else {
                        maxHit = Integer.parseInt(Utils.sanitiseValue(value.toString()));
                    }
                } catch (final NumberFormatException e) {
                    maxHit = 0;
                }
                break;
            case "weakness":
            	final String weakness = value.toString();
            	final List<String> weaknesses = new ArrayList<String>();
    			try {
    				final String[] weak = weakness.split(", ");
    				for (final String st : weak) {
    					if (st.equals("Arclight"))
    						weaknesses.add("Arclight");
    					if (st.equals("Silverlight"))
    						weaknesses.add("Silverlight");
    					if (st.equals("Darklight"))
    						weaknesses.add("Darklight");
    					if (st.toLowerCase().equals("water spells"))
    						weaknesses.add("Water");
    					if (st.equals("Water & Fire spells")) {
    						weaknesses.add("Water");
    						weaknesses.add("Fire");
    					}
    					if (st.equals("Keris"))
    						weaknesses.add("Keris");
    					if (st.toLowerCase().equals("holy water"))
    						weaknesses.add("Holy water");
    					if (st.equals("Earth"))
    						weaknesses.add(st);
    					if (st.equalsIgnoreCase("Dragon hunter crossbow"))
    						weaknesses.add("Dragon hunter crossbow");
    					if (st.equals("Earth spells"))
    						weaknesses.add("Earth");
    					if (st.equals("Fire spells"))
    						weaknesses.add("Fire");
    					if (st.equals("Fire"))
    						weaknesses.add(st);
    					if (st.equals("Fire Magic"))
    						weaknesses.add("Fire");
    					if (st.equals("Brutal arrows"))
    						weaknesses.add("Brutal arrows");
    					if (st.equals("Arrows"))
    						weaknesses.add("Arrows");
    					if (st.equals("Wolfbane Dagger"))
    						weaknesses.add("Wolfbane dagger");
    					if (st.equals("Water"))
    						weaknesses.add(st);
    					if (st.contains("Silverlight/Darklight")) {
    						weaknesses.add("Silverlight");
    						weaknesses.add("Darklight");
    					}
    					if (st.equals("Air"))
    						weaknesses.add("Air");
    					if (st.equals("Thrown"))
    						weaknesses.add("Thrown");
    					if (st.equals("Brine sabre"))
    						weaknesses.add("Brine sabre");
    					if (st.equals("Magic (Fire spells)"))
    						weaknesses.add("Fire");
    					if (st.equals("Air Spells"))
    						weaknesses.add("Air");
    					
    				}
    				} catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
    				}
    			if (!weaknesses.isEmpty()) {
    				this.weaknesses = new ArrayList<String>(weaknesses);
    			}
                break;
            case "attack styles":
            	final String style = value.toString();
            	final String[] styles = style.split(", ");
    			for (final String st : styles) {
    				if (st.contains("Melee") || st.contains("melee")
    						|| st.toLowerCase().contains("stab")
    						|| st.toLowerCase().contains("crush")
    						|| st.toLowerCase().contains("slash"))
    					attackStyle = "Melee"; 
    				else if (st.toLowerCase().contains("magic"))
    					attackStyle = "Magic";
    				else if (st.toLowerCase().contains("ranged")
    						|| st.toLowerCase().contains("range"))
    					attackStyle = "Ranged";
    			}
                break;
            case "slayer level":
                if (value.toString().contains(",")) {
                    slayerRequirement = Integer.parseInt(Utils.sanitiseValue(value.toString().split(",")[0]));
                } else if (value.toString().contains("/")) {
                    slayerRequirement = Integer.parseInt(Utils.sanitiseValue(value.toString().split("/")[0]));
                } else if (value.toString().toLowerCase().contains("unknown") || value.toString().toLowerCase().contains("none")) {
                    slayerRequirement = 0;
                } else {
                    try {
                        slayerRequirement = Integer.parseInt(Utils.sanitiseValue(value.toString()));
                    } catch (final NumberFormatException e) {
                        slayerRequirement = 0;
                    }
                }
                break;
            case "combat stats":
                // TODO parsing
            	String[] combatStats = new String[5];
                combatStats = ((List<String>) value).toArray(combatStats);
                this.combatStats = new int[5];
                for (int i = 0; i < combatStats.length; i++) {
                	this.combatStats[i] = Integer.valueOf(combatStats[i]);
                }
                
                break;
            case "aggressive stats":
                String[] aggressiveStats = new String[5];
                aggressiveStats = ((List<String>) value).toArray(aggressiveStats);
                
                if (combatBonuses == null)
                	combatBonuses = new int[13];
                for (int i = 0; i < 5; i++) {
                	combatBonuses[i] = Integer.valueOf(aggressiveStats[i]);
                }
                
                break;
            case "defensive stats":
            	String[] defensiveStats = new String[5];
            	defensiveStats = ((List<String>) value).toArray(defensiveStats);
                
                if (combatBonuses == null)
                	combatBonuses = new int[13];
                for (int i = 5; i < 10; i++) {
                	combatBonuses[i] = Integer.valueOf(defensiveStats[i - 5]);
                }
            	
                break;
            case "other bonuses immunities":
            	String[] otherBonuses = new String[5];
            	otherBonuses = ((List<String>) value).toArray(otherBonuses);
                
                if (combatBonuses == null)
                	combatBonuses = new int[13];
                for (int i = 10; i < 13; i++) {
                	combatBonuses[i] = Integer.valueOf(otherBonuses[i - 10]);
                }
                
                poisonImmune = otherBonuses[3].toLowerCase().equals("immune");
                venomImmune = otherBonuses[4].toLowerCase().equals("immune");
                
            	
                break;
            case "attack speed":
                try {
                    attackSpeed = Integer.parseInt(value.toString());
                } catch (final NumberFormatException e) {
                    attackSpeed = 0;
                }
                break;
            default:
                //System.out.println("Invalid key found: " + key);
                break;
        }
    }

    public int[] getCombatStats() {
    	if (combatStats == null)
    		combatStats = new int[5];
    	return combatStats;
    }
    
    public int[] getCombatBonuses() {
    	if (combatBonuses == null)
    		combatBonuses = new int[13];
    	return combatBonuses;
    }
    
	
	public void setCombatLevel(final int index, final int level) {
		if (combatStats == null || index >= combatStats.length)
			return;
		combatStats[index] = level;
	}
	
	public void setCombatBonus(final int index, final int level) {
		if (combatBonuses == null || index >= combatBonuses.length)
			return;
		combatBonuses[index] = level;
	}
	
	public int getAttackSpeed() {
		return 10 - attackSpeed;
	}
	
	public void setBonuses() {
		combatStats = new int[5];
		combatBonuses = new int[13];
		hitpoints = hitpoints == 0 ? 1 : hitpoints;
		if (stats != null) {
			final String[] iStats = stats.split(", ");
			for (int i = 0; i < iStats.length; i++)
				combatStats[i] = Integer.valueOf(iStats[i]);
		}
		if (bonuses != null) {
			final String[] iBonuses = bonuses.split(", ");
			for (int i = 0; i < iBonuses.length; i++)
				combatBonuses[i] = Integer.valueOf(iBonuses[i]);
		}
	}
	
	/**
	 * Drains the specified skill for a given percentage and returns the amount it drained for.
	 * @param skill
	 * @param percentage
	 * @return
	 */
	public final int drainSkill(final int skill, final double percentage) {
		final int index = getIndex(skill);
		if (index == -1)
			return 0;
		final int currentLevel = getCombatStats()[index];
		final int amt = (int) (currentLevel * (percentage / 100));
		final int newLevel = currentLevel - amt;
		combatStats[index] = newLevel;
		return amt;
	}
	
	/**
	 * Drains the specified skill for a given amount and returns the amount drained; cannot go below zero.
	 * @param skill
	 * @param amount
	 * @return
	 */
	public final int drainSkill(final int skill, final int amount) {
		final int index = getIndex(skill);
		if (index == -1)
			return 0;
		final int currentLevel = getCombatStats()[index];
		final int amt = (currentLevel - amount) < 0 ? currentLevel : amount;
		final int newLevel = currentLevel - amt;
		combatStats[index] = newLevel;
		return amt;
	}
	
	private int getIndex(final int skill) {
		switch(skill) {
		case Skills.ATTACK:
			return 0;
		case Skills.STRENGTH:
			return 1;
		case Skills.DEFENCE:
			return 2;
		case Skills.RANGED:
			return 3;
		case Skills.MAGIC:
			return 4;
		default:
			return -1;
		}
	}
	
	public final int getMeleeAttack() {
		return getCombatBonuses()[12];
	}
	
	public final int getMagicAttack() {
		return getCombatBonuses()[3];
	}
	
	public final int getRangedAttack() {
		return getCombatBonuses()[4];
	}
	
}
