package com.zenyte.game.world.entity.player.action.combat;

import lombok.Getter;

import static com.zenyte.game.world.entity.npc.combatdefs.AttackType.*;
import static com.zenyte.game.world.entity.player.action.combat.AttackStyle.AttackExperienceType.*;

public enum AttackStyleDefinition {

	UNARMED(new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
	AXE(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	MAUL(new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
	BOW(new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
	CLAWS(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	CROSSBOW(new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
	LIZARD(new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(MAGIC, MAGIC_XP)),
	CHINCHOMPA(new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
	BAZOOKA(new AttackStyle(RANGED, RANGED_XP), new AttackStyle(CRUSH, STRENGTH_XP)),
	SCIMITAR(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	TWO_HANDED(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	PICKAXE(new AttackStyle(STAB, ATTACK_XP), new AttackStyle(STAB, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
	HALBERD(new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
	NON_AUTOCAST_STAFF(new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
	SCYTHE(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(STAB, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	SPEAR(new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, SHARED_XP), new AttackStyle(CRUSH, SHARED_XP), new AttackStyle(STAB, DEFENCE_XP)),
	MACE(new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(STAB, SHARED_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
	DAGGER(new AttackStyle(STAB, ATTACK_XP), new AttackStyle(STAB, STRENGTH_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
	WAND(new AttackStyle(CRUSH, ATTACK_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, DEFENCE_XP)),
	THROWN(new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_XP), new AttackStyle(RANGED, RANGED_DEFENCE_XP)),
	WHIP(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, SHARED_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	STAFF(new AttackStyle(STAB, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP)),
	UNUSED_SCIMITAR(new AttackStyle(SLASH, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(SLASH, DEFENCE_XP)),
	THROWN_MAGIC(new AttackStyle(MAGIC, MAGIC_XP), new AttackStyle(MAGIC, MAGIC_XP), new AttackStyle(MAGIC, MAGIC_DEFENCE_XP)),
	UNUSED_SPEAR(new AttackStyle(STAB, ATTACK_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(CRUSH, SHARED_XP), new AttackStyle(STAB, DEFENCE_XP)),
	UNUSED_HALBERD(new AttackStyle(STAB, SHARED_XP), new AttackStyle(SLASH, STRENGTH_XP), new AttackStyle(STAB, DEFENCE_XP)),
	UNUSED_MAUL(new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP), new AttackStyle(CRUSH, STRENGTH_XP)),
	DINHS_BULWARK(new AttackStyle(CRUSH, ATTACK_XP), null);

	@Getter private final AttackStyle[] styles;
	
	public static final AttackStyleDefinition[] values = values();

	private AttackStyleDefinition(final AttackStyle... styles) {
		this.styles = styles;
	}

}
