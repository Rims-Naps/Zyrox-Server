package com.zenyte.game.world.entity.player.container.impl.death;

import com.zenyte.game.content.itemtransportation.masterscrolls.MasterScrollBookInterface;
import com.zenyte.game.content.skills.thieving.CoinPouch;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.containers.GemBag;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.degradableitems.RepairableItem;
import com.zenyte.game.item.enums.DismantleableItem;
import com.zenyte.game.item.enums.UpgradeKit;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.LootingBag;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.logging.log4j.util.Strings;

import static com.zenyte.game.item.ItemId.*;

/**
 * @author Kris | 20/01/2019 23:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
class DeathFilters {

    static final Function<Item, DeathMechanics> preFillFilters;
    static final Function<Item, DeathMechanics> postFillFilters;

    interface Function<K, V> {
        boolean test(K k, V v, boolean service);
    }

    static {

        //functions executed before populating the lost and kept items. Executed on the list collection.
        preFillFilters = (item, mechs, service) -> {
            val id = item.getId();
            val optionalLevel = WildernessArea.getWildernessLevel(mechs.player.getLocation());
            val level = optionalLevel.isPresent() ? optionalLevel.getAsInt() : -1;
            val def = ItemDefinitions.get(id);
            //Seed pod always kept
            if (id == ROYAL_SEED_POD || id == TORN_CLUE_SCROLL_PART_1 || id == TORN_CLUE_SCROLL_PART_2 || id == TORN_CLUE_SCROLL_PART_3) {
                mechs.kept.add(item);
                return true;
            }
            //Rune pouch filter
            if (id == RUNE_POUCH) {
                if (!mechs.player.inArea("Wilderness")) {
                    mechs.kept.add(item);
                    val runePouch = mechs.player.getRunePouch();
                    for (val rune : runePouch.getContainer().getItems().values()) {
                        if (rune == null)
                            continue;
                        mechs.kept.add(rune);
                    }
                    runePouch.clear();
                    return true;
                }
                val runePouch = mechs.player.getRunePouch();
                for (val rune : runePouch.getContainer().getItems().values()) {
                    if (rune == null)
                        continue;
                    mechs.lost.add(rune);
                }
                runePouch.clear();
                return true;
            }

            if (id == MASTER_SCROLL_BOOK || id == MASTER_SCROLL_BOOK_EMPTY) {
                val items = MasterScrollBookInterface.toItemList(item);
                for (val scroll : items) {
                    mechs.lost.add(scroll);
                }
                item.resetAttributes();
                item.setId(ItemId.MASTER_SCROLL_BOOK_EMPTY);
                mechs.lost.add(item);
                return true;
            }

            if (id == GRANITE_CANNONBALL && !(mechs.killer instanceof Player)) {
                mechs.kept.add(item);
                return true;
            }

            //Herb sack
            if (id == HERB_SACK) {
                val sack = mechs.player.getHerbSack();
                for (val herb : sack.getHerbs().int2ObjectEntrySet()) {
                    mechs.lost.add(herb.getValue());
                }
                sack.clear();
                return false;
            }

            //Seed box
            if (id == SEED_BOX) {
                val box = mechs.player.getSeedBox();
                for (val seed : box.getSeeds().int2ObjectEntrySet()) {
                    mechs.lost.add(seed.getValue());
                }
                box.clear();
                return false;
            }

            //Gem bag
            if (id == GemBag.GEM_BAG.getId()) {
                val bag = mechs.player.getGemBag();
                for (val gem : bag.getGems().int2ObjectEntrySet()) {
                    mechs.lost.add(gem.getValue());
                }
                bag.clear();
                return false;
            }

            //Looting bag
            if (LootingBag.isBag(item.getId())) {
                for (val entry : mechs.player.getLootingBag().getContainer().getItems().int2ObjectEntrySet()) {
                    val i = entry.getValue();
                    if (i == null) {
                        continue;
                    }
                    mechs.lost.add(i);
                }
                mechs.player.getLootingBag().setOpen(false);
                mechs.player.getLootingBag().clear();
                return true;
            }

            //Bonecrusher.
            if (id == BONECRUSHER) {
                if (level < 21) {
                    mechs.kept.add(item);
                    return true;
                }
            }

            //Bracelet of ethereum
            if (id == BRACELET_OF_ETHEREUM || id == BRACELET_OF_ETHEREUM_UNCHARGED) {
                mechs.lost.add(new Item(BRACELET_OF_ETHEREUM_UNCHARGED, 1));
                if (item.getCharges() > 0) {
                    mechs.lost.add(new Item(REVENANT_ETHER, item.getCharges()));
                }
                return true;
            }

            //Bonds
            if (id == 13190 || id == 13192) {
                if (!mechs.player.getGameMode().equals(GameMode.ULTIMATE_IRON_MAN)) {
                    mechs.kept.add(item);
                    return true;
                }
            }
            //Graceful
            if (def != null && def.getName().contains("Graceful")) {
                if (level < 21) {
                    mechs.kept.add(item);
                    return true;
                }
            }

            if (id == IMBUED_SARADOMIN_MAX_CAPE | id == IMBUED_SARADOMIN_MAX_HOOD || id == IMBUED_ZAMORAK_MAX_CAPE || id == IMBUED_ZAMORAK_MAX_HOOD || id == IMBUED_GUTHIX_MAX_CAPE || id == IMBUED_GUTHIX_MAX_HOOD || id == IMBUED_SARADOMIN_CAPE || id == IMBUED_GUTHIX_CAPE || id == IMBUED_ZAMORAK_CAPE
                    || id == SARADOMIN_MAX_CAPE || id == SARADOMIN_MAX_HOOD || id == GUTHIX_MAX_CAPE || id == MAX_CAPE_13342 || id == ARDOUGNE_MAX_CAPE){
                if (level < 21) {
                    mechs.kept.add(item);
                    return true;
                } else
                {
                    mechs.lost.add(new Item(COINS_995, item.getSellPrice()));
                    return false;
                }
            }

            if (id == CRAWS_BOW || id == VIGGORAS_CHAINMACE || id == THAMMARONS_SCEPTRE)
            {
                if(level > 0)
                {
                    if (id == CRAWS_BOW)
                    {
                        item.setId(CRAWS_BOW_U);
                    }
                    else if (id == VIGGORAS_CHAINMACE)
                    {
                        item.setId(VIGGORAS_CHAINMACE_U);
                    }
                    else
                    {
                        item.setId(THAMMARONS_SCEPTRE_U);
                    }
                    if (item.getCharges() > 0)
                    {
                        mechs.lost.add(new Item(REVENANT_ETHER, item.getCharges()));
                        item.setCharges(0);
                    }
                    mechs.kept.add(item);
                    return true;
                }
                mechs.kept.add(item);
                return true;
            }

            if(id == SMALL_POUCH || id == MEDIUM_POUCH || id == LARGE_POUCH || id == GIANT_POUCH)
            {
                mechs.kept.add(item);
                return true;
            }

            return false;
        };

        val imbuedRingMap = new Int2IntOpenHashMap();
        imbuedRingMap.put(RING_OF_SUFFERING_RI, RING_OF_SUFFERING_R);
        imbuedRingMap.put(RING_OF_SUFFERING_I, RING_OF_SUFFERING);
        imbuedRingMap.put(SEERS_RING_I, SEERS_RING);
        imbuedRingMap.put(ARCHERS_RING_I, ARCHERS_RING);
        imbuedRingMap.put(BERSERKER_RING_I, BERSERKER_RING);
        imbuedRingMap.put(WARRIOR_RING_I, WARRIOR_RING);
        imbuedRingMap.put(TREASONOUS_RING_I, TREASONOUS_RING);
        imbuedRingMap.put(TYRANNICAL_RING_I, TYRANNICAL_RING);
        imbuedRingMap.put(RING_OF_THE_GODS_I, RING_OF_THE_GODS);
        imbuedRingMap.put(GRANITE_RING_I, GRANITE_RING);

        //Functions executed after populating the lost and kept items. Executed on the lost collection.
        postFillFilters = (item, mechs, service) -> {
            try {
                var id = item.getId();
                val optionalLevel = WildernessArea.getWildernessLevel(mechs.player.getLocation());
                val level = optionalLevel.isPresent() ? optionalLevel.getAsInt() : -1;

                if (!(mechs.killer instanceof Player)) {
                    //Chinchompas
                    if (id == CHINCHOMPA || id == RED_CHINCHOMPA || id == CHINCHOMPA_10033 || id == RED_CHINCHOMPA_10034 || id == BLACK_CHINCHOMPA) {
                        return true;
                    }
                }
                if (mechs.killer instanceof Player && level > 0)
                {
                    item.setId(id = imbuedRingMap.getOrDefault(id, id));

                    //Slayer helmet.
                    if (item.getName().toLowerCase().contains("slayer helmet")) {
                        item.setId(id = 8921);
                    }
                    //Imbued black masks.
                    if (id >= 11774 && id <= 11784) {
                        item.setId(id = 8921);
                    }
                    //Recolored Saeldors.
                    if (item.getName().toLowerCase().contains("saeldor (c)")) {
                        item.setId(id = 30763);
                    }
                    //Recolored Bofas.
                    if (item.getName().toLowerCase().contains("faerdhinen (c)")) {
                        item.setId(id = 30596);
                    }
                    //Crystal body.
                    if (item.getName().toLowerCase().contains("crystal body")) {
                        item.setId(id = 30804);
                    }
                    //Crystal helm.
                    if (item.getName().toLowerCase().contains("crystal helm")) {
                        item.setId(id = 30804);
                    }
                    //Crystal legs.
                    if (item.getName().toLowerCase().contains("crystal legs")) {
                        item.setId(id = 30804);
                    }
                    //Recolored dhcbs
                    if (item.getName().toLowerCase().contains("dragon hunter crosswbow (b)") || item.getName().toLowerCase().contains("dragon hunter crosswbow (t)")) {
                        item.setId(id = 21012);
                    }
                }
                else
                {
                    if (imbuedRingMap.containsKey(id) || item.getName().toLowerCase().contains("slayer helmet") || (id >= 11774 && id <= 11784) || item.getName().toLowerCase().contains("saeldor (c)") || item.getName().toLowerCase().contains("faerdhinen (c)") || item.getName().toLowerCase().contains("crystal body") || item.getName().toLowerCase().contains("crystal helm") || item.getName().toLowerCase().contains("crystal legs")) {
                        mechs.kept.add(item);
                        return true;
                    }
                }

                if (id == RANGING_CAPE || id == RANGING_CAPET || id == MAX_CAPE_13342)
                {
                    val attrKey = "assembler_effect_on_" + ItemDefinitions.nameOf(id).toLowerCase().replace(" ", "_").replace("(t)", Strings.EMPTY);
                    if (mechs.player.getAttributes().remove(attrKey) != null)
                    {
                        mechs.player.sendMessage("Your " + ItemDefinitions.nameOf(id).replace("(t)", Strings.EMPTY).toLowerCase() + " has lost the Assembler effect.");
                    }
                }

                // Coin pouches
                if (CoinPouch.ITEMS.keySet().contains(id))
                {
                    val pouch = CoinPouch.ITEMS.get(id);
                    mechs.lost.add(new Item(COINS_995, com.zenyte.plugins.item.CoinPouch.getCoinAmount(pouch, item.getAmount())));
                    return true;
                }

                if (id == GRANITE_CANNONBALL && mechs.killer instanceof Player)
                {
                    item.setId(CANNONBALL);
                    return false;
                }

                if (id == FEROCIOUS_GLOVES && mechs.killer instanceof Player) {
                    item.setId(HYDRA_LEATHER);
                    return false;
                }

                //Repairables
                val repairable = RepairableItem.getItem(item);
                if (repairable != null) {
                    if (!repairable.isTradeable()) {
                        val ids = repairable.getIds();
                        if (service) {
                            return false;
                        }
                        if (ids.length < 2 || id == ids[ids.length - 1] || !optionalLevel.isPresent()) {
                            mechs.kept.add(item);
                            return true;
                        }
                        if (level < 21) {
                            item.setId(ids[1]);
                            mechs.kept.add(item);
                        }
                        mechs.lost.add(new Item(995, (int) (repairable.getRepairCost() * 0.75F)));
                        return true;
                    }
                }



                //Degradables
                val degradable = DegradableItem.ITEMS.get(id);

                /*if (degradable == DegradableItem.FULL_TRIDENT_OF_THE_SEAS || degradable == DegradableItem.TRIDENT_OF_THE_SEAS
                 || degradable == DegradableItem.TRIDENT_OF_THE_SWAMP || degradable == DegradableItem.BLOWPIPE || degradable == DegradableItem.SERPENTINE_HELM
                || degradable == DegradableItem.TANZANITE_HELM || degradable == DegradableItem.MAGMA_HELM) {
                    mechs.kept.add(item);
                    return true;
                }*/

                if (degradable != null) {
                    if (level == -1) {
                        mechs.kept.add(item);
                        return true;
                    }
                    val function = degradable.getFunction();
                    if (function != null) {
                        val items = function.apply(item);
                        if (items != null) {
                            for (val it : items) {
                                if (it != null && it.getAmount() > 0) {
                                    mechs.lost.add(it);
                                }
                            }
                        }
                    }
                    val degraded = DegradableItem.getCompletelyDegradedId(item.getId());
                    item.setId(degraded);
                    return false;
                }

                //Dismantleables
                val dismantleable = DismantleableItem.MAPPED_VALUES.get(id);
                if (dismantleable != null) {
                    if (level == -1) {
                        mechs.kept.add(item);
                        return true;
                    }
                    if (dismantleable.isSplitOnDeath()) {
                        mechs.lost.add(new Item(dismantleable.getBaseItem()));
                        mechs.lost.add(new Item(dismantleable.getKit()));
                        return true;
                    }
                }
                //Upgradeable
                val upgradeable = UpgradeKit.MAPPED_VALUES.get(id);
                if (upgradeable != null ) {
                    if (level == -1 && !(mechs.killer instanceof Player)) {
                        mechs.kept.add(item);
                        return true;
                    }
                    mechs.lost.add(new Item(upgradeable.getBaseItem()));
                    return true;
                }

                if (!item.isTradable()) {
                    if (optionalLevel.isPresent() && mechs.killer instanceof Player) {
                        val amount = item.getAmount() * Math.round(item.getName().toLowerCase().contains("max cape") ? 569250 : item.getDefinitions().getPrice() * 0.6F);
                        mechs.lost.add(new Item(995, amount));
                        return true;
                    }
                    mechs.kept.add(item);
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        };

    }

}
