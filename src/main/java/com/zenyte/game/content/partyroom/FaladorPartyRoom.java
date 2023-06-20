package com.zenyte.game.content.partyroom;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.FaladorArea;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.area.plugins.LayableObjectPlugin;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Kris | 26/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FaladorPartyRoom extends FaladorArea implements CycleProcessPlugin, CannonRestrictionPlugin, LayableObjectPlugin, RandomEventRestrictionPlugin {
    
    private static final String PRIVATE_ATTRIBUTE_KEY = "falador party chest private container";
    
    /**
     * A list of announcements which is populated when the lever is pulled. Rebuilt every one minute that passes. Cleared out when the balloons begin dropping.
     */
    private final List<String> announcements = new ObjectArrayList<>();
    @Getter
    private int announcementFrequency;

    @Getter private final Container container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.FALADOR_PARTY_CHEST_PUBLIC_DEPOSIT, Optional.empty());
    private final Set<String> viewingPlayers = new ObjectOpenHashSet<>();

    @Getter
    private final PartyRoomVariables variables = new PartyRoomVariables();

    @Getter @Setter private boolean partyPeteCountdown;

    @NotNull
    public static final FaladorPartyRoom getPartyRoom() {
        return Objects.requireNonNull(GlobalAreaManager.getArea(FaladorPartyRoom.class));
    }

    public void startViewing(@NotNull final Player player) {
        viewingPlayers.add(player.getUsername());
    }

    public void stopViewing(@NotNull final Player player) {
        viewingPlayers.remove(player.getUsername());
    }

    public void forEachViewing(@NotNull final Consumer<Player> consumer) {
        viewingPlayers.forEach(username -> World.getPlayer(username).ifPresent(consumer));
    }

    @NotNull final Container getPrivateContainer(@NotNull final Player player) {
        val optionalContainer = findPrivateContainer(player);
        if (!optionalContainer.isPresent()) {
            val container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.FALADOR_PARTY_CHEST_PRIVATE_DEPOSIT, Optional.of(player));
            player.getTemporaryAttributes().put(PRIVATE_ATTRIBUTE_KEY, container);
            return container;
        }
        return optionalContainer.get();
    }

    @NotNull private final Optional<Container> findPrivateContainer(@NotNull final Player player) {
        val attribute = player.getTemporaryAttributes().get(PRIVATE_ATTRIBUTE_KEY);
        if (attribute instanceof Container) {
            return Optional.of((Container) attribute);
        }
        return Optional.empty();
    }

    @Getter private boolean dancing;

    private static final ForceTalk[] song = new ForceTalk[] {
            new ForceTalk("We're Knights of the Party Room"),
            new ForceTalk("We dance round and round like a loon"),
            new ForceTalk("Quite often we like to sing"),
            new ForceTalk("Unfortunately we make a din"),
            new ForceTalk("We're Knights of the Party Room"),
            new ForceTalk("Do you like our helmet plumes?"),
            new ForceTalk("Everyone's happy now we can move"),
            new ForceTalk("Like a party animal in the groove")
    };

    void startKnightsDance() {
        if (dancing) {
            return;
        }
        dancing = true;
        val knights = new PartyRoomDancingKnight[6];
        for (int i = 0; i < 6; i++) {
            val knight = new PartyRoomDancingKnight(i);
            knight.spawn();
            knights[i] = knight;
        }
        val singingKnight = knights[2];
        for (int i = 0; i < song.length; i++) {
            val lyrics = song[i];
            WorldTasksManager.schedule(() -> singingKnight.setForceTalk(lyrics), 4 + (i * 4));
            if (i == song.length - 1) {
                WorldTasksManager.schedule(() -> {
                    for (val knight : knights) {
                        knight.finish();
                    }
                    dancing = false;
                }, 8 + (i * 4));
            }
        }
    }

    /**
     * The tiles in this party room mapped down.
     */
    private final List<ImmutableLocation> roomTiles;

    /**
     * A list of tiles which can be re-used to basically know where to place the balloons.
     */
    private final List<ImmutableLocation> reusableTilesList = new ObjectArrayList<>();

    /**
     * The tile hash to balloons map which maps them down per tile basis.
     */
    private final Int2ObjectMap<PartyBalloon> balloons = new Int2ObjectOpenHashMap<>();

    public FaladorPartyRoom() {
        super();
        val roomTiles = new LinkedList<ImmutableLocation>();
        val polygons = polygons();
        assert polygons.length == 1;
        val polygon = polygons[0];
        val rectangle = polygon.getPolygon().getBounds2D();
        val minX = (int) rectangle.getMinX();
        val maxX = (int) rectangle.getMaxX();
        val minY = (int) rectangle.getMinY();
        val maxY = (int) rectangle.getMaxY();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (polygon.contains(x, y)) {
                    roomTiles.add(new ImmutableLocation(x, y, 0));
                }
            }
        }
        this.roomTiles = Collections.unmodifiableList(roomTiles);
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 3037, 3386 },
                        { 3036, 3385 },
                        { 3036, 3382 },
                        { 3037, 3381 },
                        { 3037, 3376 },
                        { 3036, 3375 },
                        { 3036, 3372 },
                        { 3037, 3371 },
                        { 3040, 3371 },
                        { 3041, 3372 },
                        { 3051, 3372 },
                        { 3052, 3371 },
                        { 3055, 3371 },
                        { 3056, 3372 },
                        { 3056, 3375 },
                        { 3055, 3376 },
                        { 3055, 3381 },
                        { 3056, 3382 },
                        { 3056, 3385 },
                        { 3055, 3386 },
                        { 3052, 3386 },
                        { 3051, 3385 },
                        { 3041, 3385 },
                        { 3040, 3386 }
                }, 0)
        };
    }

    @Override
    public void enter(Player player) {
        super.enter(player);
    }

    @Override
    public void leave(Player player, boolean logout) {
        super.leave(player, logout);
    }

    @Override
    public String name() {
        return "Falador Party Room: Party area";
    }
    private static final Animation balloonPopAnimation = new Animation(794);
    private static final SoundEffect balloonPopSound = new SoundEffect(3252, 12, 30);

    public final void pop(@NotNull final Player player, @NotNull final PartyBalloon balloon) {
        if (variables.isDisabled()) {
            player.sendMessage("The party room is disabled right now.");
            return;
        }
        player.lock(2);
        player.setAnimation(balloonPopAnimation);
        balloon.getUsernames().add(player.getUsername());
        World.sendSoundEffect(new Location(player.getLocation()), balloonPopSound);
    }

    public void roll(@NotNull final PartyBalloon balloon) {
        if (container.isEmpty()) {
            return;
        }
        if (Utils.random(3) != 0) {
            return;
        }
        val usernames = balloon.getUsernames();
        if (usernames.isEmpty()) {
            return;
        }
        val randomUsername = Utils.getRandomCollectionElement(usernames);
        World.getPlayer(randomUsername).ifPresent(player -> {
            if (player.isIronman()) {
                player.sendFilteredMessage("As an Iron " + (player.getAppearance().isMale() ? "man" : "woman") + ", you cannot receive items from the balloons.");
                return;
            }
            selectAndRemoveRandomItem().ifPresent(item -> World.spawnFloorItem(item, player, 100, 200));
        });
    }

    private final Optional<Item> selectAndRemoveRandomItem() {
        if (container.isEmpty()) {
            return Optional.empty();
        }
        val item = new Item(Utils.getRandomCollectionElement(container.getItems().values()));
        //Items will automatically turn to stacks when added to the chest, however we should pop them out of the chest in the unstackable form.
        if (!item.isStackable()) {
            item.setAmount(1);
        }
        val price = (long) item.getSellPrice() * item.getAmount();
        if (price > 1_000_000) {
            val percentage = Utils.random(20, 70) / 100F;
            val valuePerItem = item.getSellPrice();
            val maxAmount = (long) Math.max(1, Math.round(item.getAmount() * percentage)) * valuePerItem;
            val amount = maxAmount >= 100_000_000 ? (int) Math.ceil(100_000_000F / valuePerItem) : Math.max(1, Math.round(item.getAmount() * percentage));
            item.setAmount(amount);
        }
        val result = container.remove(item);
        container.shift();
        val removedItem = new Item(item.getId(), result.getSucceededAmount());
        container.setFullUpdate(true);
        forEachViewing(container::refresh);
        return Optional.of(removedItem);
    }

    public void processBalloons() {
        if (!balloons.isEmpty()) {
            balloons.int2ObjectEntrySet().removeIf(entry -> !entry.getValue().alive());
        }
    }

    public String pollAnnouncementColour() {
        return announcementFrequency <= 30 && Utils.random(1) == 0 ? "<col=ff0000>" : "";
    }

    public String pollAnnouncement() {
        if (announcements.isEmpty()) {
            return null;
        }
        if (Utils.random(1) == 0) {
            return announcements.get(announcements.size() - 1);
        }
        return announcements.get(Utils.random(announcements.size() - 1));
    }

    long calculateChestValue() {
        long totalValueOfAllItemsCombined = 0;
        for (val item : container.getItems().values()) {
            if (item == null) {
                continue;
            }
            val pricePerItem = item.getSellPrice();
            val totalPrice = (long) pricePerItem * item.getAmount();
            totalValueOfAllItemsCombined += totalPrice;
        }
        return totalValueOfAllItemsCombined;
    }

    void rebuildAnnouncementsList() {
        announcements.clear();
        val countdown = variables.getCountdown();
        val minutes = ((int) Math.ceil(countdown / 100F));
        val seconds = (int) TimeUnit.TICKS.toSeconds(countdown);
        //If more than 45 seconds remaining, just announce minutes. If under, mention seconds!
        val timeRemaining = countdown > 75 ? (minutes + " minute" + (minutes == 1 ? "" : "s")) : (seconds + " second" + (seconds == 1 ? "" : "s"));
        long totalValueOfAllItemsCombined = 0;
        for (val item : container.getItems().values()) {
            if (item == null) {
                continue;
            }
            val pricePerItem = item.getSellPrice();
            val totalPrice = (long) pricePerItem * item.getAmount();
            totalValueOfAllItemsCombined += totalPrice;
            if (pricePerItem >= 200_000_000) {
                announcements.add(Utils.formatString(item.getName()) + " drop in the party room in " + timeRemaining + "!");
            }
        }
        val optionalFrequency = variables.getAnnouncementSpeed(totalValueOfAllItemsCombined);
        if (!optionalFrequency.isPresent()) {
            return;
        }
        val millions = totalValueOfAllItemsCombined / 1_000_000;
        if (millions >= 1) {
            announcements.add(millions + " million pot in the Falador party room in " + timeRemaining + "!");
        }
        this.announcementFrequency = optionalFrequency.getAsInt();
    }

    @Override
    public void process() {
        if (variables.isDisabled() || (variables.getCountdown() <= 0 && variables.getQueuedBalloonsQuantity() <= 0)) {
            return;
        }
        if (variables.getCountdown() > 0) {
            variables.setCountdown(variables.getCountdown() - 1);
            val countdown = variables.getCountdown();

            if (countdown == 0) {
                announcements.clear();
            } else if (countdown % 50 == 0) {
                rebuildAnnouncementsList();
            }
            return;
        }
        reusableTilesList.clear();
        val capacity = (float) balloons.size() / (roomTiles.size() * 0.85F);
        if (capacity >= 1) {
            return;
        }
        val minimumAmount = 3 - (int) (capacity * 4);
        val maximumAmount = 5 - (int) (capacity * 4);
        val amount = Math.min(Utils.random(minimumAmount, maximumAmount), variables.getQueuedBalloonsQuantity());
        for (val tile : roomTiles) {
            if (balloons.containsKey(tile.getPositionHash()) || !World.isFloorFree(tile, 1) || World.getRegion(tile.getRegionId()).containsObjectWithEqualSlot(0, tile.getX() & 63, tile.getY() & 63, 10)) {
                continue;
            }
            reusableTilesList.add(tile);
        }
        for (int i = 0; i < amount; i++) {
           if (reusableTilesList.isEmpty()) {
               break;
           }
           val tile = reusableTilesList.get(Utils.random(reusableTilesList.size() - 1));
           val balloon = new PartyBalloon(Utils.random(115, 122), tile.getX(), tile.getY(), tile.getPlane());
           balloon.alive();
           balloons.put(balloon.getPositionHash(), balloon);
           variables.setQueuedBalloonsQuantity(variables.getQueuedBalloonsQuantity() - 1);
        }
    }

    @Override
    public boolean canLay(@NotNull Player player, @NotNull LayableObjectType type) {
        player.sendMessage("You cannot do that here.");
        return false;
    }
}
