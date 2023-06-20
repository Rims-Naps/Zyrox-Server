package com.zenyte.game.content.godwars.npcs;

import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combatdefs.AggressionType;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCombatDefinitions;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.godwars.GodwarsDungeonArea;
import com.zenyte.game.world.region.area.wilderness.WildernessGodwarsDungeon;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import static com.zenyte.game.world.entity.npc.NpcId.*;

/**
 * @author Kris | 20/11/2018 22:54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class KillcountNPC extends NPC implements Spawnable {

    static final IntSet specialNPCs =
            new IntOpenHashSet(new int[]
                    {
                        SERGEANT_GRIMSPIKE, AVIANSIE_3174, AVIANSIE_3183, AVIANSIE_3182, BLOODVELD_487, FLIGHT_KILISA,
                        AVIANSIE_3175, SPIRITUAL_RANGER, SPIRITUAL_RANGER_2242, AVIANSIE_3173, BALFRUG_KREEYATH,
                        SARADOMIN_PRIEST, BLOODVELD_486, BLOODVELD_485, SPIRITUAL_MAGE_3168, SPIRITUAL_MAGE_2244,
                        ICEFIEND_4813, ZAKLN_GRITCH, SPIRITUAL_RANGER_3160, SERGEANT_STRONGSTACK, BREE,
                        FLOCKLEADER_GEERIN, ICEFIEND, SPIRITUAL_MAGE, GENERAL_GRAARDOR, SERGEANT_STEELWILL, KREEARRA,
                        KRIL_TSUTSAROTH, AVIANSIE_3179, AVIANSIE_3178, BLOODVELD_3138, WINGMAN_SKREE,
                        SPIRITUAL_MAGE_3161, TSTANON_KARLAK, AVIANSIE_3171, STARLIGHT, COMMANDER_ZILYANA, BLOODVELD,
                        GROWLER, AVIANSIE_3172, AVIANSIE_3170, AVIANSIE, GOBLIN_2249, AVIANSIE_3181, AVIANSIE_3176,
                        AVIANSIE_3177, AVIANSIE_3180, SPIRITUAL_WARRIOR, SPIRITUAL_WARRIOR_2243, SPIRITUAL_WARRIOR_3159,
                        SPIRITUAL_WARRIOR_3166
            });

    protected KillcountNPC(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        if (tile == null)
            return;
        this.isInWildernessGodwars = WildernessGodwarsDungeon.polygon.contains(tile);
        this.isInGodwars = GodwarsDungeonArea.polygon.contains(tile) || isInWildernessGodwars || GlobalAreaManager.getArea(tile) instanceof GodwarsInstance;
        if (isInGodwars) {
            setTargetType(EntityType.BOTH);
        }
        this.type = (id >= OGRE_2233 && id <= GOBLIN_2249 && id != SPIRITUAL_MAGE_2244) ? GodType.BANDOS
                : (id >= SARADOMIN_PRIEST && id <= KNIGHT_OF_SARADOMIN_2214 && id != SPIRITUAL_MAGE) ? GodType.SARADOMIN
                : (id >= HELLHOUND_3133 && id <= GORAK_3141 || id == SPIRITUAL_WARRIOR_3159 || id == SPIRITUAL_RANGER_3160) ? GodType.ZAMORAK : GodType.ARMADYL;
    }

    private transient GodType type;
    private boolean isInGodwars;
    private boolean isInWildernessGodwars;

    @Override
    public void setCombatDefinitions(final NPCCombatDefinitions definitions) {
        super.setCombatDefinitions(definitions);
        if (!isInGodwars) {
            return;
        }
        if (!(this instanceof Bloodveld)) {
            combatDefinitions.setAggressionType(AggressionType.AGGRESSIVE);
        }
    }

    @Override
    public boolean isTolerable() {
        if (!isInGodwars) {
            return super.isTolerable();
        }
        return false;
    }

    public GodType type() {
        return type;
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (isInWildernessGodwars || !isInGodwars || !(source instanceof Player)) {
            return;
        }
        val player = (Player) source;
        val type = type();
        val prefix = type.formattedString;
        val killcount = player.getNumericAttribute(prefix + "Kills").intValue() + 1;
        player.getAttributes().put(prefix + "Kills", killcount);
        player.getVarManager().sendBit(type.varbit, killcount);
    }

    private boolean isWieldingProtectiveItem(final Player player) {
        val container = player.getEquipment().getContainer();
        val array = type().protectiveItems;
        Item item;
        for (int slot = container.getContainerSize() - 1; slot >= 0; slot--) {
            item = container.get(slot);
            if (item == null) {
                continue;
            }
            if (ArrayUtils.contains(array, item.getId()))
                return true;
        }
        return false;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity target) {
        if (!isInGodwars) {
            return super.isAcceptableTarget(target);
        }
        if (target instanceof Player) {
            return !isWieldingProtectiveItem((Player) target);
        }
        if (!(target instanceof KillcountNPC))
            return false;
        return type() != ((KillcountNPC) target).type();
    }

    @RequiredArgsConstructor
    public enum GodType {
        BANDOS(3975, new int[]{
                ItemId.ANCIENT_MACE, ItemId.BANDOS_GODSWORD, ItemId.BANDOS_CHESTPLATE, ItemId.BANDOS_TASSETS, ItemId.BANDOS_BOOTS, ItemId.BANDOS_ROBE_TOP, ItemId.BANDOS_ROBE_LEGS,
                ItemId.BANDOS_STOLE, ItemId.BANDOS_MITRE, ItemId.BANDOS_CLOAK, ItemId.BANDOS_CROZIER, ItemId.BANDOS_PLATEBODY, ItemId.BANDOS_PLATELEGS, ItemId.BANDOS_PLATESKIRT,
                ItemId.BANDOS_FULL_HELM, ItemId.BANDOS_KITESHIELD, ItemId.BANDOS_BRACERS, ItemId.BANDOS_DHIDE, ItemId.BANDOS_CHAPS, ItemId.BANDOS_COIF, ItemId.BANDOS_DHIDE_BOOTS,
                ItemId.BANDOS_GODSWORD_OR, ItemId.BANDOS_GODSWORD_20782, ItemId.BANDOS_GODSWORD_21060, ItemId.WAR_BLESSING, ItemId.DAMAGED_BOOK_12607, ItemId.BOOK_OF_WAR, ItemId.GUARDIAN_BOOTS,
                ItemId.BANDOS_DHIDE_SHIELD
        }),
        ZAMORAK(3976, new int[]{
                ItemId.ZAMORAK_MONK_BOTTOM, ItemId.ZAMORAK_MONK_TOP, ItemId.ZAMORAK_CAPE, ItemId.ZAMORAK_STAFF, ItemId.ZAMORAK_PLATEBODY, ItemId.ZAMORAK_PLATELEGS, ItemId.ZAMORAK_FULL_HELM,
                ItemId.ZAMORAK_KITESHIELD, ItemId.ZAMORAK_PLATESKIRT, ItemId.ZAMORAK_MJOLNIR, ItemId.ZAMORAK_BRACERS, ItemId.ZAMORAK_DHIDE, ItemId.ZAMORAK_CHAPS, ItemId.ZAMORAK_COIF,
                ItemId.ZAMORAK_CROZIER, ItemId.ZAMORAK_CLOAK, ItemId.ZAMORAK_MITRE, ItemId.ZAMORAK_ROBE_TOP, ItemId.ZAMORAK_ROBE_LEGS, ItemId.ZAMORAK_STOLE, ItemId.ZAMORAK_PLATEBODY_10776,
                ItemId.ZAMORAK_ROBE_TOP_10786, ItemId.ZAMORAK_DHIDE_10790, ItemId.ZAMORAK_GODSWORD, ItemId.ZAMORAKIAN_SPEAR, ItemId.ZAMORAKIAN_HASTA, ItemId.ZAMORAK_HALO, ItemId.ZAMORAK_MAX_CAPE,
                ItemId.ZAMORAK_MAX_HOOD, ItemId.ZAMORAK_DHIDE_BOOTS, ItemId.ZAMORAK_GODSWORD_OR, ItemId.IMBUED_ZAMORAK_MAX_CAPE, ItemId.IMBUED_ZAMORAK_MAX_HOOD, ItemId.IMBUED_ZAMORAK_CAPE,
                ItemId.UNHOLY_BLESSING, ItemId.UNHOLY_SYMBOL, ItemId.DAMAGED_BOOK_3841, ItemId.UNHOLY_BOOK, ItemId.UNHOLY_SYMBOL_4683, ItemId.STAFF_OF_THE_DEAD, ItemId.TOXIC_STAFF_OF_THE_DEAD,
                ItemId.VIGGORAS_CHAINMACE_U, ItemId.VIGGORAS_CHAINMACE, ItemId.DRAGON_HUNTER_LANCE, ItemId.THAMMARONS_SCEPTRE, ItemId.THAMMARONS_SCEPTRE_U, ItemId.ZAMORAK_DHIDE_SHIELD
        }),
        SARADOMIN(3972, new int[]{
                ItemId.SARADOMIN_CAPE, ItemId.SARADOMIN_STAFF, ItemId.SARADOMIN_PLATEBODY, ItemId.SARADOMIN_PLATELEGS, ItemId.SARADOMIN_FULL_HELM, ItemId.SARADOMIN_KITESHIELD,
                ItemId.SARADOMIN_PLATESKIRT, ItemId.SARADOMIN_MJOLNIR, ItemId.SARADOMIN_SYMBOL, ItemId.SARADOMIN_BRACERS, ItemId.SARADOMIN_DHIDE, ItemId.SARADOMIN_CHAPS, ItemId.SARADOMIN_COIF,
                ItemId.SARADOMIN_CROZIER, ItemId.SARADOMIN_CLOAK, ItemId.SARADOMIN_MITRE, ItemId.SARADOMIN_ROBE_TOP, ItemId.SARADOMIN_ROBE_LEGS, ItemId.SARADOMIN_STOLE, ItemId.SARADOMIN_PLATE,
                ItemId.SARADOMIN_ROBE_TOP_10784, ItemId.SARADOMIN_DHIDE_10792, ItemId.SARADOMIN_GODSWORD, ItemId.SARADOMIN_SWORD, ItemId.SARADOMIN_HALO, ItemId.SARAS_BLESSED_SWORD_FULL,
                ItemId.SARADOMINS_BLESSED_SWORD, ItemId.SARADOMIN_MAX_CAPE, ItemId.SARADOMIN_MAX_HOOD, ItemId.SARADOMIN_DHIDE_BOOTS, ItemId.SARADOMIN_GODSWORD_OR, ItemId.IMBUED_SARADOMIN_MAX_CAPE,
                ItemId.IMBUED_SARADOMIN_MAX_HOOD, ItemId.IMBUED_SARADOMIN_CAPE, ItemId.HOLY_SYMBOL, ItemId.HOLY_SYMBOL_4682, ItemId.MONKS_ROBE, ItemId.MONKS_ROBE_TOP, ItemId.HOLY_BOOK,
                ItemId.HOLY_BLESSING, ItemId.DAMAGED_BOOK, ItemId.HOLY_SANDALS, ItemId.DEVOUT_BOOTS, ItemId.JUSTICIAR_FACEGUARD, ItemId.JUSTICIAR_CHESTGUARD, ItemId.JUSTICIAR_LEGGUARDS,
                ItemId.SARADOMIN_DHIDE_SHIELD, ItemId.HOLY_WRAPS, ItemId.RING_OF_ENDURANCE_UNCHARGED, ItemId.RING_OF_ENDURANCE,
        }),
        ARMADYL(3973, new int[]{ItemId.ARMADYL_PENDANT, ItemId.ARMADYL_CROSSBOW, ItemId.ARMADYL_GODSWORD, ItemId.ARMADYL_HELMET, ItemId.ARMADYL_CHESTPLATE, ItemId.ARMADYL_CHAINSKIRT,
                ItemId.ARMADYL_ROBE_TOP, ItemId.ARMADYL_ROBE_LEGS, ItemId.ARMADYL_STOLE, ItemId.ARMADYL_MITRE, ItemId.ARMADYL_CLOAK, ItemId.ARMADYL_CROZIER, ItemId.ARMADYL_PLATEBODY,
                ItemId.ARMADYL_PLATELEGS, ItemId.ARMADYL_PLATESKIRT, ItemId.ARMADYL_FULL_HELM, ItemId.ARMADYL_KITESHIELD, ItemId.ARMADYL_BRACERS, ItemId.ARMADYL_DHIDE, ItemId.ARMADYL_CHAPS,
                ItemId.ARMADYL_COIF, ItemId.ARMADYL_DHIDE_BOOTS, ItemId.ARMADYL_GODSWORD_OR, ItemId.ARMADYL_GODSWORD_20593, ItemId.HONOURABLE_BLESSING, ItemId.DAMAGED_BOOK_12609, ItemId.BOOK_OF_LAW
                , ItemId.CRAWS_BOW, ItemId.CRAWS_BOW_U, ItemId.ARMADYL_DHIDE_SHIELD
        });

        private final String formattedString = Utils.formatString(name());
        private final int varbit;
        @Getter
        private final int[] protectiveItems;
    }

}
