package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.SpellDefinitions;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.testinterfaces.AutocastInterface;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.action.combat.AttackStyle;
import com.zenyte.game.world.entity.player.action.combat.AttackStyle.AttackExperienceType;
import com.zenyte.game.world.entity.player.action.combat.AttackStyleDefinition;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.SpellbookChangeEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class CombatDefinitions {

	public CombatDefinitions(final Player player) {
		this.player = player;
	}

	public void initialize(final CombatDefinitions defs) {
		style = defs.style;
		specialEnergy = defs.specialEnergy;
		autoRetaliate = defs.autoRetaliate;
		defensiveAutocast = defs.defensiveAutocast;
		autocastSpell = defs.autocastSpell;
		if (defs.lastStyleSettings != null) {
		    Objects.requireNonNull(this.lastStyleSettings).putAll(defs.lastStyleSettings);
        }
	}

	@Expose
	@Getter
	private int style, specialEnergy = 100;
	@Getter
	private Spellbook spellbook = Spellbook.NORMAL;
	@Expose
	@Getter
	private boolean defensiveAutocast;
	@Expose
	@Getter
	private boolean autoRetaliate;
	@Expose
	@Getter
	private CombatSpell autocastSpell;
	@Getter
	private transient AttackStyleDefinition styleDefinition;
	@Getter
	@Setter
	private transient boolean usingSpecial;

	public void setDefensiveAutocast(final boolean value) {
	    this.defensiveAutocast = value;
        refreshCachedSettings();
    }

	@Nullable private final Map<AttackStyleDefinition, CachedStyleAttributes> lastStyleSettings = new EnumMap<>(AttackStyleDefinition.class);

	@ToString
	private static final class CachedStyleAttributes {
	    private boolean isDefensiveAutocasting;
	    private CombatSpell autocastSpell;
	    private int style;
    }

	private final transient Player player;

	public final AttackStyle getAttackStyle() {
		final AttackStyle[] styles = styleDefinition.getStyles();
		if (style >= styles.length) {
			return styles[styles.length - 1];
		}
		return styles[style];
	}

	public void resetAutocast() {
		if (player.getInterfaceHandler().isVisible(201)) {
            GameInterface.COMBAT_TAB.open(player);
		}
		if (autocastSpell == null) {
			return;
		}
		autocastSpell = null;
		player.getVarManager().sendBit(2668, -1);
		player.getVarManager().sendBit(276, -1);
	}

	public final AttackType getAttackType() {
		if (autocastSpell != null) {
			return AttackType.MAGIC;
		}
		return getAttackStyle().getType();
	}

	public final AttackExperienceType getAttackExperienceType() {
		if (autocastSpell != null) {
			return defensiveAutocast ? AttackExperienceType.MAGIC_DEFENCE_XP : AttackExperienceType.MAGIC_XP;
		}
		final AttackStyleDefinition definition = player.getCombatDefinitions().getStyleDefinition();
		if (definition == null) {
		    return AttackExperienceType.NOT_AVAILABLE;
        }
		final Item weapon = player.getWeapon();
		if (weapon != null) {
			if (weapon.getId() == 21015) {
				if (style != 0) {
					return AttackExperienceType.DEFENCE_XP;
				}
			}
		}
		final AttackStyle[] styles = definition.getStyles();
		if (style >= definition.getStyles().length || style < 0) {
			return styles[styles.length - 1].getExperienceType();
		}
		final AttackStyle attackStyle = styles[style];
		return attackStyle.getExperienceType();
	}

	public void refresh() {
	    val typeVarbit = getWeaponTypeVarbit();
        val attackStylesLength = AttackStyleDefinition.values[typeVarbit].getStyles().length;
        val varManager = player.getVarManager();
        val dispatcher = player.getPacketDispatcher();
        val weapon = player.getEquipment().getItem(EquipmentSlot.WEAPON);
        styleDefinition = AttackStyleDefinition.values[typeVarbit];
        val cached = Objects.requireNonNull(this.lastStyleSettings).get(styleDefinition);
        if (cached != null && player.getVarManager().getValue(VarCollection.REMEMBER_COMBAT_SETTINGS.getId()) == 1) {
            this.defensiveAutocast = cached.isDefensiveAutocasting;
            this.style = cached.style;
            if (cached.autocastSpell != null && AutocastInterface.canCast(player, cached.autocastSpell) && !player.isCanPvp()) {
                this.autocastSpell = cached.autocastSpell;
            }
        }
        varManager.sendVar(172, autoRetaliate ? 0 : 1);
        varManager.sendVar(300, Math.min(255, specialEnergy) * 10);
        varManager.sendVar(301, usingSpecial ? 1 : 0);
        varManager.sendBit(357, typeVarbit);
        varManager.sendBit(2668, defensiveAutocast ? 1 : 2);
        varManager.sendBit(276, autocastSpell == null ? -1 : SpellDefinitions.autocastSlotMap.getInt(autocastSpell.getSpellName()));
        varManager.sendVar(43, autocastSpell == null ? (attackStylesLength == 3 && style == 2 ? (typeVarbit == 6 ? 2 : 3) : style) : 5);
        dispatcher.sendComponentText(593, 1, weapon == null ? "Unarmed" : weapon.getName());
        dispatcher.sendComponentText(593, 2, "Combat Lvl: " + player.getSkills().getCombatLevel());
	}

	private int getWeaponTypeVarbit() {
	    val weapon = player.getEquipment().getItem(EquipmentSlot.WEAPON);
	    if (weapon == null)
	        return 0;
	    return weapon.getDefinitions().getInterfaceVarbit();
    }

    private void refreshCachedSettings() {
	    if (player.getVarManager().getValue(VarCollection.REMEMBER_COMBAT_SETTINGS.getId()) == 0) {
	        return;
        }
        val typeVarbit = getWeaponTypeVarbit();
        styleDefinition = AttackStyleDefinition.values[typeVarbit];
        val cached = Objects.requireNonNull(this.lastStyleSettings).computeIfAbsent(styleDefinition, p -> new CachedStyleAttributes());
        cached.style = style;
        cached.isDefensiveAutocasting = defensiveAutocast;
        cached.autocastSpell = autocastSpell;
    }

    public final void resetAutocastIfNotCached() {
        val typeVarbit = getWeaponTypeVarbit();
        styleDefinition = AttackStyleDefinition.values[typeVarbit];
	    if (Objects.requireNonNull(lastStyleSettings).get(styleDefinition) == null || player.getVarManager().getValue(VarCollection.REMEMBER_COMBAT_SETTINGS.getId()) == 0) {
	        setAutocastSpell(null);
        }
    }

	public void setStyle(final int style) {
		this.style = style;
		refreshCachedSettings();
		player.getVarManager().sendVar(43, style);
	}

	public void setAutocastSpell(final CombatSpell spell) {
		autocastSpell = spell;
        refreshCachedSettings();
		refresh();
	}

	public void setSpecialEnergy(final int energy) {
		if ((specialEnergy = energy) >= 100) {
			player.getVariables().setSpecRegeneration(0);
		}
		player.getVarManager().sendVar(300, Math.min(255, specialEnergy) * 10);
	}

	public void setAutoRetaliate(final boolean autoRetaliate) {
		this.autoRetaliate = autoRetaliate;
		player.getVarManager().sendVar(172, autoRetaliate ? 0 : 1);
	}

	public void setSpecial(final boolean state, final boolean ignoreWeapon) {
		usingSpecial = state;
		player.getVarManager().sendVar(301, usingSpecial ? 1 : 0);
		if (!ignoreWeapon) {
			val weaponId = player.getEquipment().getId(EquipmentSlot.WEAPON);
			if ((weaponId == 4153 || weaponId == 12848 || weaponId == 20557 || weaponId == 20849 || weaponId == 21207)
                    || (usingSpecial && player.getWeapon() != null
					&& PlayerCombat.INSTANT_SPEC_WEAPONS.contains(player.getWeapon().getId()))) {
				PlayerCombat.performInstantSpecial(player);
			}
		}
	}

	public void setSpellbook(final Spellbook spellbook, final boolean refresh) {
		if (refresh) {
			player.getVarManager().sendBit(4070, spellbook.ordinal());
		}
		if (refresh) {
		    setAutocastSpell(null);
        } else
            autocastSpell = null;
        player.getTemporaryAttributes().remove("SPELLBOOK_SWAP");
        val oldSpellbook = this.spellbook;
		this.spellbook = spellbook;
        PluginManager.post(new SpellbookChangeEvent(player, oldSpellbook));
	}

	public boolean hasFullAhrimsAndDamned() {
		final Item hood = player.getEquipment().getItem(EquipmentSlot.HELMET.getSlot());
		final Item amulet = player.getEquipment().getItem(EquipmentSlot.AMULET.getSlot());
		final Item top = player.getEquipment().getItem(EquipmentSlot.PLATE.getSlot());
		final Item skirt = player.getEquipment().getItem(EquipmentSlot.LEGS.getSlot());
		final Item staff = player.getEquipment().getItem(EquipmentSlot.WEAPON.getSlot());
		if (hood == null || amulet == null || top == null || skirt == null || staff == null) {
			return false;
		}
		if (hood.getName().contains("Ahrim's hood") && top.getName().contains("Ahrim's robetop")
				&& skirt.getName().contains("Ahrim's robeskirt") && staff.getName().contains("Ahrim's staff")
				&& amulet.getName().contains("Amulet of the damned")) {
			return true;
		}
		return false;
	}

}
