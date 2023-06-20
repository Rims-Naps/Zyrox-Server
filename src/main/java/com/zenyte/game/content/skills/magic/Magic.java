package com.zenyte.game.content.skills.magic;

import com.zenyte.game.content.skills.magic.spells.MagicSpell;
import com.zenyte.game.content.skills.magic.spells.teleports.SpellbookTeleport;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kris | 11. dets 2017 : 2:56.45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public final class Magic {

    public static final Logger logger = LoggerFactory.getLogger(Magic.class);

	public static final int FIRE_RUNE = 554, WATER_RUNE = 555, AIR_RUNE = 556, EARTH_RUNE = 557, MIND_RUNE = 558, BODY_RUNE = 559,
			DEATH_RUNE = 560, NATURE_RUNE = 561, CHAOS_RUNE = 562, LAW_RUNE = 563, COSMIC_RUNE = 564, BLOOD_RUNE = 565, SOUL_RUNE = 566,
			ASTRAL_RUNE = 9075, WRATH_RUNE = 21880, STEAM_RUNE = 4694, MIST_RUNE = 4695, DUST_RUNE = 4696, SMOKE_RUNE = 4697,
			MUD_RUNE = 4698, LAVA_RUNE = 4699;

	public static final Map<String, MagicSpell> regularSpells = new Object2ObjectOpenHashMap<>();
    public static final Map<String, MagicSpell> ancientSpells = new Object2ObjectOpenHashMap<>();
    public static final Map<String, MagicSpell> lunarSpells = new Object2ObjectOpenHashMap<>();
    public static final Map<String, MagicSpell> arceuusSpells = new Object2ObjectOpenHashMap<>();

    public static final Map<String, MagicSpell> SPELLS_BY_NAME = new HashMap<>();

	public static final Optional<CombatSpell> getCombatSpell(@NotNull final Spellbook spellbook, @NotNull final String name) {
		val spell = spellbook.getSpellCollection().get(name);
		if (!(spell instanceof CombatSpell))
			return Optional.empty();
		return Optional.of((CombatSpell) spell);
	}

	public static final void add(final Class<? extends MagicSpell> c) {
		try {
			if (c.isEnum()) {
				final MagicSpell[] possibleValues = c.getEnumConstants();
				for (val spell : possibleValues) {
				    if (spell == SpellbookTeleport.TELEPORT_TO_BOUNTY_TARGET) {
				        for (val spellbook : Spellbook.VALUES) {
				            spellbook.getSpellCollection().put(spell.getSpellName(), spell);
                        }
				        continue;
                    }
				    val spellbook = spell.getSpellbook();
				    if (spellbook == null) {
				        continue;
                    }
				    spellbook.getSpellCollection().put(spell.getSpellName(), spell);

				}
				return;
			}
			if (c.isAnonymousClass() || c.isInterface()) {
				return;
			}
			val spell = c.newInstance();
            val spellbook = spell.getSpellbook();
            spellbook.getSpellCollection().put(spell.getSpellName(), spell);
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends MagicSpell> T getSpell(@NotNull final Spellbook spellbook, final String name, @NotNull final Class<T> type) {
		val spell = spellbook.getSpellCollection().get(name);
		if (type.isInstance(spell)) {
			return (T) spell;
		}
		return null;
	}

    public enum TeleportType {
		REGULAR,
		ITEM,
		OBJECT
	}

}
