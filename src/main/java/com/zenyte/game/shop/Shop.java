package com.zenyte.game.shop;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.AccomplishmentCape;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.containers.GemBag;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.StringUtilities;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.plugins.item.CoalBag;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.ints.IntHash;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * @author Kris | 23/11/2018 14:33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
@Slf4j
public final class Shop {

    private static final boolean USE_INSTANCED_SHOPS = false;

    static final int SHOPS_DUPLICATOR_COUNT = 5;
    static final int DEFAULT_RESTOCK_TIMER = 15;
    private static final Map<String, Shop> shops = new ConcurrentHashMap<>();
    private static final Map<String, Shop> ironmanShops = new ConcurrentHashMap<>();
    private static final List<Map<String, Shop>> allShopMaps = new ArrayList<>(Arrays.asList(shops, ironmanShops));
    private static final Map<String, List<BiConsumer<Player, Item>>> consumerMap = new HashMap<>();
    private static final Map<String, Consumer<Player>> consumerOpenMap = new HashMap<>();
    private static final Map<String, List<BiPredicate<Player, Item>>> predicateMap = new HashMap<>();

    static {
        appendConsumer("Culinaromancer's Chest", (player, item) -> {
            if (item.getId() == 7462) {
                player.getAchievementDiaries().update(LumbridgeDiary.PURCHASE_BARROWS_GLOVES);
            }
        });
        appendConsumer("Candle Shop", (player, item) -> {
            if (item.getId() == 36) {
                player.getAchievementDiaries().update(KandarinDiary.BUY_CANDLE);
            }
        });
        appendConsumer("Grace's Graceful Clothing", (player, item) -> {
            if (item.getName().contains("Graceful")) {
                player.getCollectionLog().add(item);
            }
        });
        appendConsumer("Mining Guild Mineral Exchange", (player, item) -> {
            if (item.getName().contains("gloves")) {
                player.getCollectionLog().add(item);
            }
        });
        appendConsumer("Melee Weaponry Shop", (player, item) -> {
            if (item.getId() == 2402) {
                player.sendMessage("You can upgrade the Silverlight into a Darklight after killing 100 demons.");
            }
        });
        appendConsumer("Accomplishment Cape Shop", (player, item) -> {
            val cape = AccomplishmentCape.get(item.getId());
            if (cape == null) {
                return;
            }
            player.getInventory().addItem(cape.getHood(), item.getAmount());
        });

        appendPredicate("Prospector Percy's Nugget Shop", (player, item) -> {
            if (item.getId() == CoalBag.ITEM.getId() || item.getId() == GemBag.GEM_BAG.getId()) {
                if (item.getAmount() > 1) {
                    player.sendMessage("You can only buy one of that item.");
                    return false;
                }
                if (player.containsItem(item.getId())) {
                    player.sendMessage("You already have this item and cannot get another one.");
                    return false;
                }
                return true;
            }
            return true;
        });

        appendPredicate("Event Token Exchange Shop", (player, item) -> {
            if (item.getId() == 32238 && player.getNumericAttribute("infernoVar").intValue() < 1) {
                player.sendMessage("You need to unlock Mol Ul Rek before you can buy this teleport.");
                return false;
            }
            return true;
        });

        //Making sure players can only have 1 seed box/rune pouch at a time
        appendPredicate("Vote Shop", (player, item) -> { //TODO improve
            if (item.getId() == ItemId.SMALL_POUCH) {
                if (item.getAmount() > 1 || player.containsItem(item.getId()) || player.containsItem(ItemId.COLOSSAL_POUCH)) {
                    player.sendMessage("You can only buy one of that item.");
                    return false;
                }
            }
            if (item.getId() == ItemId.MEDIUM_POUCH) {
                if (item.getAmount() > 1 || player.containsItem(item.getId()) || player.containsItem(ItemId.COLOSSAL_POUCH)) {
                    player.sendMessage("You can only buy one of that item.");
                    return false;
                }
                if (!player.containsItem(ItemId.SMALL_POUCH)) {
                    player.sendMessage("You need to own a small pouch before you can buy a medium pouch.");
                    return false;
                }
            }
            if (item.getId() == ItemId.LARGE_POUCH) {
                if (item.getAmount() > 1 || player.containsItem(item.getId()) || player.containsItem(ItemId.COLOSSAL_POUCH)) {
                    player.sendMessage("You can only buy one of that item.");
                    return false;
                }
                if (!player.containsItem(ItemId.SMALL_POUCH) || !player.containsItem(ItemId.MEDIUM_POUCH)) {
                    player.sendMessage("You need to own a small and a medium pouch before you can buy a large pouch.");
                    return false;
                }
            }
            if (item.getId() == ItemId.GIANT_POUCH) {
                if (item.getAmount() > 1 || player.containsItem(item.getId()) || player.containsItem(ItemId.COLOSSAL_POUCH)) {
                    player.sendMessage("You can only buy one of that item.");
                    return false;
                }
                if (!player.containsItem(ItemId.SMALL_POUCH) || !player.containsItem(ItemId.MEDIUM_POUCH) || !player.containsItem(ItemId.LARGE_POUCH)) {
                    player.sendMessage("You need to own a small, a medium and a large pouch before you can buy a giant pouch.");
                    return false;
                }
            }

            if (item.getId() == CoalBag.ITEM.getId() || item.getId() == GemBag.GEM_BAG.getId() || item.getId() == 12791 || item.getId() == 13639) {
                if (item.getAmount() > 1) {
                    player.sendMessage("You can only buy one of that item.");
                    return false;
                }
                if (player.containsItem(item.getId())) {
                    player.sendMessage("You already have this item and cannot get another one.");
                    return false;
                }
                return true;
            }
            return true;
        });
        //Making sure the player has 99 in a specific skill before buying a skillcape
        appendPredicate("Accomplishment Cape Shop", (player, item) -> {
            val cape = AccomplishmentCape.get(item.getId());
            if (cape == null) {
                return false;
            }
            if (cape.equals(AccomplishmentCape.DIARY)) {
                if (!player.getAchievementDiaries().isAllCompleted()) {
                    player.sendMessage("You need to complete all the Achievement diaries to buy this cape.");
                    return false;
                }
            } else if (cape.equals(AccomplishmentCape.MUSIC)) {
                if (player.getMusic().unlockedMusicCount() < 556) {
                    player.sendMessage("You need to unlock all 570 available music tracks to buy this cape.");
                    return false;
                }
            }else if (cape.name().contains("200M")) {
                val skill = cape.getSkill();
                if (player.getSkills().getExperience(skill) < 200000000) {
                    player.sendMessage("You need 200m experience in " + cape.name().replace("200M","").toLowerCase() + " to buy this skillcape.");
                    return false;
                }
            } else {
                val skill = cape.getSkill();
                if (player.getSkills().getLevelForXp(skill) < 99) {
                    player.sendMessage("You need level 99 in " + cape + " to buy this skillcape.");
                    return false;
                }
            }
            if (!player.getInventory().checkSpace(2)) {
                player.sendMessage("You need to have at least 2 inventory spaces for the cape and hood.");
                return false;
            }
            return true;
        });
        //On open consumers
        bind("Aleck's Hunter Emporium", p -> p.getAchievementDiaries().update(ArdougneDiary.VIEW_ALECKS_HUNTER_EMPORIUM));
        bind("Sarah's Farming Shop", p -> p.getAchievementDiaries().update(FaladorDiary.BROWSE_SARAHS_FARM_SHOP));
        bind("Keldagrim Stonemason", p -> p.getAchievementDiaries().update(FremennikDiary.BROWSE_THE_STONEMASONS_SHOP));
        bind("Thessalia's Fine Clothes", p -> p.getAchievementDiaries().update(VarrockDiary.BROWSE_THESSELIA_STORE));
    }

    private final String name;
    private final ShopCurrency currency;
    private final ShopPolicy policy;
    private final List<StockItem> stock;
    private final ShopContainer container;
    private final List<Player> players;
    private final boolean ironman;
    private final float sellModifier;
    /**
     * Constructs a new game shop based on the scrap json shop.
     *
     * @param scrap the json serialized shop.
     */
    public Shop(final JsonShop scrap, final boolean ironman) {
        val shopName = scrap.getShopName();
        this.name = StringUtilities.escape(shopName);
        this.currency = scrap.getCurrency();
        this.policy = scrap.getSellPolicy();
        this.sellModifier = scrap.getSellMultiplier() == 0 ? 0.4F : scrap.getSellMultiplier();
        this.container = new ShopContainer(ContainerPolicy.ALWAYS_STACK, ContainerType.SHOP);
        this.stock = new ArrayList<>();
        for (val item : scrap.getItems()) {
            if (ItemDefinitions.isInvalid(item.id)) continue;
            if (ironman && item.ironmanRestricted) {
                continue;
            }
            stock.add(new StockItem(item));
        }
        this.players = new LinkedList<>();
        if (ironman ? ironmanShops.containsKey(shopName) : shops.containsKey(shopName)) {
            throw new ExceptionInInitializerError("Shop by the name of " + shopName + " is already mapped.");
        }
        this.ironman = ironman;
        fillContainer();
    }

    private static void bind(final String name, final Consumer<Player> consumer) {
        consumerOpenMap.put(name, consumer);
    }

    public static void load() {
        try {
            val files = new File("data/shops/").listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isFile()) {
                    val reader = new BufferedReader(new FileReader(file));
                    val shop = World.getGson().fromJson(reader, JsonShop.class);
                    if (shop == null) continue;
                    final String name = shop.getShopName();
                    //General stores should not get duplicated.
                    if (shop.getSellPolicy() == ShopPolicy.CAN_SELL) {
                        final Shop normalShop = new Shop(shop, false);
                        final Shop ironmanShop = new Shop(shop, true);
                        if (USE_INSTANCED_SHOPS) {
                            for (int i = 0; i < SHOPS_DUPLICATOR_COUNT; i++) {
                                shops.put(name + "|" + i, normalShop);
                                ironmanShops.put(name + "|" + i, ironmanShop);
                            }
                        } else {
                            shops.put(name, normalShop);
                            ironmanShops.put(name, ironmanShop);
                        }
                        continue;
                    }
                    if (USE_INSTANCED_SHOPS) {
                        for (int i = 0; i < SHOPS_DUPLICATOR_COUNT; i++) {
                            shops.put(name + "|" + i, new Shop(shop, false));
                            ironmanShops.put(name + "|" + i, new Shop(shop, true));
                        }
                    } else {
                        shops.put(name, new Shop(shop, false));
                        ironmanShops.put(name, new Shop(shop, true));
                    }
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    /**
     * Gets the shop by the {@param name}.
     *
     * @param name the name of the shop.
     * @return the shop if it exist.
     * @throws RuntimeException if the shop by the requested name doesn't exist.
     */
    public static Shop get(@NonNull final String name, final boolean ironman, @NotNull final Player player) {
        val identifier = player.getNumericTemporaryAttributeOrDefault("shop_unique_identifier", player.getPlayerInformation().getUserIdentifier()).intValue();
        val shopIndex = identifier % SHOPS_DUPLICATOR_COUNT;
        final String key = USE_INSTANCED_SHOPS ? name + "|" + shopIndex : name;
        final Shop shop = ironman ? ironmanShops.get(key) : shops.get(key);
        if (shop == null) throw new RuntimeException("Shop by the name of " + name + " does not exist.");
        return shop;
    }

    /**
     * Appends a biconsumer into the consumer map. Used for adding consumers for purchasing from the shop.
     *
     * @param shopName the name of the shop.
     * @param consumer the biconsumer of player & item.
     */
    private static void appendConsumer(final String shopName, final BiConsumer<Player, Item> consumer) {
        consumerMap.computeIfAbsent(shopName, (name) -> new ArrayList<>()).add(consumer);
    }

    private static void appendPredicate(final String shopName, final BiPredicate<Player, Item> predicate) {
        predicateMap.computeIfAbsent(shopName, (name) -> new ArrayList<>()).add(predicate);
    }

    public static void process() {
        allShopMaps.forEach(shopMap -> {
            try {
            shopMap.forEach((name, shop) -> {
                val container = shop.getContainer();
                val stock = container.stockItems;
                stock.int2ObjectEntrySet().fastForEach(entry -> {
                    val item = entry.getValue();
                    val key = entry.getIntKey();
                    if (--item.restockTimer <= 0) {
                        item.restockTimer = item.defaultRestockTimer;
                        val it = container.get(key);
                        if (it == null) {
                            container.stockItems.remove(key);
                            return;
                        }
                        val isStockItem = shop.stock.contains(item);
                        val amount = it.getAmount();
                        if (amount == item.defaultAmount && isStockItem) return;
                        if (!isStockItem) {
                            it.setAmount(amount - 1);
                            if (amount == 1) {
                                container.set(key, null);
                                container.stockItems.remove(key);
                            }
                            container.refresh(key);
                            return;
                        }
                        it.setAmount(amount + (amount < item.defaultAmount ? 1 : -1));
                        container.refresh(key);
                    }
                });
                if (!container.getModifiedSlots().isEmpty()) {
                    shop.refresh();
                }
            });
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        });
    }

    /**
     * Refreshes the stock for all players currently browsing this shop.
     */
    private void refresh() {
        for (val player : players) {
            container.refresh(player);
        }
    }

    /**
     * Fills the shop's container with the stock.
     */
    private void fillContainer() {
        for (int i = 0; i < stock.size(); i++) {
            val item = stock.get(i);
            container.set(i, item);
            item.defaultStockItem = true;
            if (item.defaultRestockTimer == 0) {
                item.defaultRestockTimer = item.restockTimer = DEFAULT_RESTOCK_TIMER;
            }
            container.stockItems.put(i, item);
        }
    }

    /**
     * Opens this shop for the player; appends the shop as a temporary attribute for the interfaces.
     *
     * @param player the player opening the shop.
     */
    public void open(@NonNull final Player player) {
        player.getTemporaryAttributes().put("Shop", this);
        GameInterface.SHOP.open(player);
        val consumer = consumerOpenMap.get(name);
        if (consumer != null) {
            consumer.accept(player);
        }
    }

    /**
     * Gets the current buy price of the item. If value returned is 0, the item is considered to be free,
     * if it is <= -1, the item cannot be purchased.
     *
     * @param id the id of the item.
     * @return the current buy price of the item.
     */
    int getBuyPrice(final Player player, final int id) {
        float modifier = 1F;
        if (player != null && this.name.startsWith("Culinaromancer's Chest")) {
            if (DiaryReward.EXPLORERS_RING4.eligibleFor(player)) {
                modifier = 0.8F;
            }
        }

        if (player != null && this.currency == ShopCurrency.TOKKUL) {
            if (DiaryReward.KARAMJA_GLOVES1.eligibleFor(player)) {
                modifier = 13F/15F;
            }
        }

        val item = container.stockItems.get(container.getSlotOf(id));
        if (item == null) {
            return (int) (ItemDefinitions.getOrThrow(id).getPrice() * modifier);
        }
        return (int) Math.ceil(item.buyPrice * modifier);
    }

    /**
     * Gets the current sell price of the item. If the value returned is -1, the item cannot be sold to this shop.
     *
     *
     * @param player
     * @param id the id of the item.
     * @return the sell price of the item.
     */
    int getSellPrice(final Player player, final int id) {
        if (this.policy == ShopPolicy.NO_SELLING) return -1;
        val unnotedId = ItemDefinitions.getOrThrow(id).getUnnotedOrDefault();
        val notedId = ItemDefinitions.getOrThrow(id).getNotedOrDefault();
        var slot = container.getSlotOf(id);
        if (slot == -1) {
            val definition = ItemDefinitions.getOrThrow(id);
            val oppositeId = definition.isNoted() ? definition.getUnnotedOrDefault() : definition.getNotedOrDefault();
            if (oppositeId != -1) {
                slot = container.getSlotOf(oppositeId);
            }
        }
        val item = container.stockItems.get(slot);
        if (policy == ShopPolicy.STOCK_ONLY) {
            if (item == null || !item.defaultStockItem) return -1;
        }
        double modifier = 1;
        /*if (player != null && this.currency == ShopCurrency.TOKKUL) {
            if (DiaryReward.eligibleFor(player, DiaryReward.KARAMJA_GLOVES1)) {
                modifier = 7F/3F;
            }
        }*/
        if (item == null) {
            return (int) Math.ceil((ItemDefinitions.getOrThrow(id).getPrice() * sellModifier) * modifier);
        }
        return (int) Math.ceil(item.sellPrice * modifier);
    }

    /**
     * Attempts to purchase the item from this shop. Verifies all conditions before-hand, refreshes the shop for all
     * viewing players afterwards.
     *
     * @param player the player purchasing the item.
     * @param option the option clicked.
     * @param slotId the slot clicked.
     */
    void purchase(@NonNull final Player player, @NonNull final ShopInterface.ItemOption option, final int slotId) {
        if (option.is(ShopInterface.ItemOption.VALUE) || option.is(ShopInterface.ItemOption.EXAMINE)) {
            throw new IllegalArgumentException("Invalid purchase option: " + option);
        }
        val item = container.get(slotId);
        if (item == null) {
            throw new RuntimeException("Purchased item is null, slot: " + slotId + ", stock: " + container.getItems());
        }
        var amount = Math.min(item.getAmount(), option.amount);

        val baseAmount = amount;
        if (amount <= 0) {
            player.sendMessage("There's currently no stock of this item.");
            return;
        }
        val singleCost = getBuyPrice(player, item.getId());
        if (singleCost < 0) {
            player.sendMessage("This item is currently unavailable.");
            return;
        }
        val stockItem = container.stockItems.get(slotId);
        if (ironman) {
            if (!stockItem.defaultStockItem) {
                player.sendMessage("You cannot purchase items sold by other players.");
                return;
            }
            if (item.getAmount() > stockItem.defaultAmount) {
                player.sendMessage("Iron Men may not take advantage of the pricing when a shop is over-stocked.");
                return;
            }
        }

        val totalCost = (long) amount * singleCost;

        val inventory = player.getInventory().getContainer();
        val held = currency.getAmount(player);
        if (totalCost > held) {
            amount = held / singleCost;
        }
        val predicates = predicateMap.get(name);
        if (predicates != null) {
            for (val predicate : predicates) {
                if (!predicate.test(player, new Item(item.getId(), amount))) {
                    return;
                }
            }
        }


        var freeSlots = inventory.getFreeSlotsSize();

        val affordableAmount = amount;

        val inInventory = inventory.getAmountOf(item.getId());

        if (amount + inInventory < 0) {
            amount = Integer.MAX_VALUE - inInventory;
        }


        if (item.isStackable()) {
            if (currency.isPhysical()) {
                if ((freeSlots == 0 && inInventory == 0 && currency.getAmount(player) != (amount * singleCost))) {
                    amount = 0;
                }
            } else {
                if (freeSlots == 0 && inInventory == 0) {
                    amount = 0;
                }
            }
        } else {
            /*if (freeSlots < amount && currency.isPhysical() && currency.getAmount(player) == (amount * singleCost)) {
                freeSlots++;
            }*/
            //This is problematic because it doesnt take into account whether the amount you're buying is the same as the amount you'll
            //receive. It only checks to see if the amount was enough to clear up the inventory slot. however most shops that i've seen
            //won't let you do it like that. you have to have enough slots not counting the slot the currency is in
            amount = Math.min(freeSlots, amount);
        }

        final Optional<String> message = affordableAmount != baseAmount ? Optional.of("You don't have enough " + currency + ".") : amount != affordableAmount ? Optional.of("Not enough space in your inventory.") : Optional.empty();
        message.ifPresent(mes -> player.sendMessage(mes));
        if (amount <= 0) {
            return;
        }
        val cost = amount * singleCost;
        if (cost > 0) {
            currency.remove(player, cost);
        }

        val remainderItem = new Item(item.getId(), item.getAmount() - amount);
        container.set(slotId, remainderItem);

        if (remainderItem.getAmount() <= 0) {
            if (!stockItem.defaultStockItem) {
                container.set(slotId, null);
            }
        }
        val succeeded = new Item(item.getId(), amount, DegradableItem.getFullCharges(item.getId()));
        inventory.add(succeeded);
        inventory.refresh(player);
        refresh();
        player.getCollectionLog().add(new Item(item.getId(), 1));
        if (currency.equals(ShopCurrency.TOB_RESUPPLY_POINTS)) {
            player.sendMessage("You have " + Colour.RED.wrap(player.getNumericAttribute("tobrefillpoints").intValue()) + " points.");
        }
        val consumers = consumerMap.get(name);
        if (consumers == null) {
            return;
        }
        for (val consumer : consumers) {
            consumer.accept(player, succeeded);
        }
    }

    /**
     * Attempts to sell the selected item into this shop. Verifies the option, calculates the amount & refreshes
     * the interface for all players afterwards.
     *
     * @param player the player selling the item.
     * @param option the option clicked.
     * @param slotId the slot clicked.
     */
    void sell(@NonNull final Player player, @NonNull final ShopInventoryInterface.ItemOption option, final int slotId) {
        if (option.is(ShopInventoryInterface.ItemOption.VALUE) || option.is(ShopInventoryInterface.ItemOption.EXAMINE)) {
            throw new IllegalArgumentException("Invalid sell option: " + option);
        }
        val inventory = player.getInventory().getContainer();
        val item = inventory.get(slotId);
        if (item == null) {
            throw new RuntimeException("Sold item is null, slot: " + slotId + ", stock: " + inventory.getItems());
        }
        val id = item.getId();
        if ((id == 995 || !item.isTradable()) && !name.toLowerCase().contains("grace's graceful clothing") && !name.toLowerCase().contains("mysterious stranger") && !name.toLowerCase().contains("percy")) {
            player.sendMessage("You can't sell this item.");
            return;
        }
        val singleCost = getSellPrice(player, item.getId());
        if (singleCost < 0) {
            player.sendMessage("You can't sell this item to this shop.");
            return;
        }
        val itemAmount = inventory.getAmountOf(id);
        val unnotedId = name.equals("Food Shop") ? item.getId() : item.getDefinitions().getUnnotedOrDefault(); //TODO improve this
        if (ironman) {
            for (val entry : container.stockItems.int2ObjectEntrySet()) {
                val it = entry.getValue();
                if (it == null) continue;
                if (it.getId() == id) {
                    if (it.defaultStockItem && container.get(entry.getIntKey()).getAmount() < it.defaultAmount) {
                        player.sendMessage("Iron Men may not take advantage of the pricing when a shop is under-stocked.");
                        return;
                    }
                    break;
                }
            }
        }

        var amount = Math.min(Math.min(itemAmount, option.amount), container.getMaximumTransferrableAmount(new Item(unnotedId, itemAmount)));
        if (amount <= 0) {
            player.sendMessage("The shop is full!");
            return;
        }

        val freeSlots = inventory.getFreeSlotsSize();
        amount = currency.isPhysical() && !currency.isStackable() ? Math.min(freeSlots, amount) : Math.min(Integer.MAX_VALUE - currency.getAmount(player), amount);
        if (amount <= 0 || (currency.isPhysical() && currency.getAmount(player) <= 0 && freeSlots == 0 && amount < item.getAmount())) {
            player.sendMessage("Not enough free space in your inventory.");
            return;
        }
        inventory.remove(new Item(item.getId(), amount));
        container.add(new Item(unnotedId, amount));
        val currencyReturn = amount * singleCost;
        if (currencyReturn > 0) {
            currency.add(player, currencyReturn);
        }
        inventory.refresh(player);
        refresh();
    }

    @SuppressWarnings("unused")
    private class StockItem extends Item {
    
        //Default variables of the stock item.
        private final transient int defaultAmount;
        private transient int defaultRestockTimer;
        private transient int defaultBuyPrice;
        private transient int defaultSellPrice;
        private transient boolean defaultStockItem;
        //Current variables of the stock item.
        private transient int buyPrice;
        private transient int sellPrice;
        private transient int restockTimer;
        StockItem(final int id, final int amount, final int buyPrice, final int sellPrice, final int restockTimer) {
            super(id, amount);
            this.defaultAmount = amount;
            this.defaultRestockTimer = this.restockTimer = restockTimer;
            this.defaultBuyPrice = this.buyPrice = buyPrice;
            this.defaultSellPrice = this.sellPrice = sellPrice;
        }

        private StockItem(final JsonShop.Item item) {
            super(item.id, item.amount);
            this.defaultAmount = item.amount;
            this.defaultRestockTimer = this.restockTimer = item.restockTimer;
            this.defaultBuyPrice = this.buyPrice = item.buyPrice;
            this.defaultSellPrice = this.sellPrice = item.sellPrice;
        }
        private StockItem(final StockItem item) {
            super(item.getId(), item.getAmount());
            this.defaultAmount = item.getAmount();
            this.defaultRestockTimer = this.restockTimer = item.restockTimer;
            this.defaultBuyPrice = this.buyPrice = item.buyPrice;
            this.defaultSellPrice = this.sellPrice = item.sellPrice;
        }

        private void setDefaults(final StockItem item) {
            this.defaultRestockTimer = this.restockTimer = item.restockTimer;
            this.defaultBuyPrice = this.buyPrice = item.buyPrice;
            this.defaultSellPrice = this.sellPrice = item.sellPrice;
        }

    }

    class ShopContainer extends Container {
        private transient final StockMap<StockItem> stockItems = new StockMap<>();

        private ShopContainer(final ContainerPolicy policy, final ContainerType type) {
            super(policy, type, Optional.empty());
        }

        @Override
        public void set(final int slot, final Item item) {
            if (item == null) {
                stockItems.remove(slot);
            } else if (stockItems.get(slot) == null) {
                stockItems.put(slot, new StockItem(item.getId(), item.getAmount(), getBuyPrice(null, item.getId()), getSellPrice(null, item.getId()), DEFAULT_RESTOCK_TIMER));
            }
            super.set(slot, item);
        }

        class StockMap<V> extends Int2ObjectOpenCustomHashMap<V> {

            StockMap() {
                super(40, .75f, new IntHash.Strategy() {
                    @Override
                    public int hashCode(int e) {
                        return e;
                    }

                    @Override
                    public boolean equals(int a, int b) {
                        return a == b;
                    }
                });
            }

            public V[] valuesArray() {
                return value;
            }

            public int[] keysArray() {
                return key;
            }

            public int getN() {
                return n;
            }

        }

    }

}
