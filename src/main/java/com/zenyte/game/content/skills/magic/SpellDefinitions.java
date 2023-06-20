package com.zenyte.game.content.skills.magic;

import com.zenyte.game.item.Item;
import mgi.types.config.enums.Enums;
import mgi.types.config.enums.IntEnum;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kris | 23. mai 2018 : 17:31:51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class SpellDefinitions {

	public static final Map<String, SpellDefinitions> SPELLS = new HashMap<String, SpellDefinitions>();

	private static final Int2ObjectOpenHashMap<String> spellComponentMap = new Int2ObjectOpenHashMap<>();

	public static final Object2IntOpenHashMap<String> autocastSlotMap = new Object2IntOpenHashMap<>();

	public SpellDefinitions(final int level, final Item[] runes) {
		this.level = level;
		this.runes = runes;
	}

	@Getter
	private final int level;
	@Getter
	private final Item[] runes;

	private static final int[] RUNE_INDEXES = new int[] { 365, 367, 369, 606 };
	private static final int[] RUNE_AMOUNTS = new int[] { 366, 368, 370, 607 };

	static {
        val enums = new IntEnum[] { Enums.REGULAR_SPELLS_ENUM, Enums.ANCIENT_SPELLS_ENUM, Enums.LUNAR_SPELLS_ENUM,
                Enums.ARCEUUS_SPELLS_ENUM
        };
        for (val spellbookEnum : enums) {
            for (val enumEntry : spellbookEnum.getValues().int2IntEntrySet()) {
                val spellItem = enumEntry.getIntValue();
                val definitions = ItemDefinitions.getOrThrow(spellItem);
                val params = definitions.getParameters();
                if (params == null)
                    throw new RuntimeException("Spell item parameters are null!");

                val name = (String) params.get(601);
                val level = (int) params.get(604);
                val runes = new ArrayList<Item>(4);

                val componentId = ((int) params.get(596)) & 0xFFFF;

                for (int a = 0; a < 4; a++) {
                    val runeIndex = RUNE_INDEXES[a];
                    val entry = params.get(runeIndex);
                    if (entry == null)
                        continue;
                    val amount = RUNE_AMOUNTS[a];
                    runes.add(new Item((int) entry, (int) params.get(amount)));
                }
                val definition = new SpellDefinitions(level, runes.toArray(new Item[0]));
                val refactoredName = name.replaceAll("-", "").toLowerCase();
                SPELLS.put(refactoredName, definition);
                spellComponentMap.put(componentId, refactoredName);
            }
        }

        for (val entry : Enums.AUTOCASTABLE_SPELLS_ENUM.getValues().int2IntEntrySet()) {
            val key = entry.getIntKey();
            val value = entry.getIntValue();
            val itemDefinitions = ItemDefinitions.getOrThrow(value);
            val params = Objects.requireNonNull(itemDefinitions.getParameters());
            val name = (String) params.get(601);
            val refactoredName = name.replaceAll("-", "").toLowerCase();
            autocastSlotMap.put(refactoredName, key);
        }
    }

	public static final String getSpellName(final int componentId) {
        try {
            return spellComponentMap.get(componentId);
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return null;
    }

    public static final int getSpellComponent(final String spellName) {
        try {
            for (val entry : spellComponentMap.int2ObjectEntrySet()) {
                if (entry.getValue().equalsIgnoreCase(spellName)) {
                    return entry.getIntKey();
                }
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return -1;
    }

}
