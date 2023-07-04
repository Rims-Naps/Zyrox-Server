package com.zenyte.game.content.minigame.barrows;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.CameraShakeType;
import com.zenyte.game.HintArrow;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.utils.ProjectileUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.types.config.npcs.NPCDefinitions;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @author Kris | 28/11/2018 21:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public final class Barrows {

    private static final int MAXIMUM_POTENTIAL = 1000;
    private static final int MOUND_RADIUS = 3;
    private static final int SHUT_DOORWAYS = 6;
    private static final int SLAIN_WIGHT_VARBIT = 457;
    private static final int POTENTIAL_VARBIT = 463;
    private static final int LADDER_VARBIT = 4743;
    private static final int MINIMUM_TIMER = 15, MAXIMUM_TIMER = 85;
    private static final int LADDER_DISTANCE = 16;
    private static final int SPAWN_ATTEMPT_COUNT = 100;
    private static final int DEFAULT_SPAWN_DISTANCE = 3;
    private static final int CRYPT_NPC_WEIGHT = 6;
    static final int CHEST_VARBIT = 1394;

    @Getter
    @Setter
    private static int barrowsMultiplier = 1;

    private static final IntArrayList cryptMonsters = IntArrayList.wrap(new int[] {
            1678, 1679, 1685, 1686, 1687, 1688
    });

    public Barrows(final Player player) {
        this.player = player;
        this.container = new Container(ContainerPolicy.ALWAYS_STACK, ContainerType.BARROWS_CHEST, Optional.empty());
        this.slainWights = new HashSet<>(BarrowsWight.values.length);
        this.shutDoorways = new HashSet<>(SHUT_DOORWAYS);
        this.puzzle = new BarrowsPuzzle(player);
        resetTimer();
        reset();
    }

    private transient final Player player;
    private transient Container container;
    private transient int timer;
    private transient final BarrowsPuzzle puzzle;
    private boolean skipTunnels;
    private BarrowsWight hiddenWight;
    private Set<BarrowsWight> slainWights;
    private Set<CryptDoorway> shutDoorways;
    private CryptDoorway openDoorway;
    private BarrowsCorner corner;
    private int potential;
    @Setter private boolean looted, puzzleSolved;
    @Getter private transient BarrowsWightNPC currentWight;

    public void setMaximumReward(final int rp) {
        this.potential = rp;
        slainWights.addAll(Arrays.asList(BarrowsWight.values));
    }

    @Subscribe
    public static final void onInit(final InitializationEvent event) {
        val player = event.getPlayer();
        val parser = event.getSavedPlayer();
        val parserBarrows = parser.getBarrows();
        if (parserBarrows == null)
            return;
        val barrows = player.getBarrows();
        barrows.hiddenWight = parserBarrows.hiddenWight;
        barrows.slainWights = parserBarrows.slainWights;
        barrows.corner = parserBarrows.corner;
        barrows.potential = parserBarrows.potential;
        barrows.looted = parserBarrows.looted;
        barrows.skipTunnels = parserBarrows.skipTunnels;
    }

    public void resetTimer() {
        timer = 30;
    }

    /**
     * Gets the actual potential the player currently has accumulated.
     * @return full potential accumulated, including slain wights.
     */
    private int getFullPotential() {
        var potential = this.potential;
        for (val slain : slainWights) {
            potential += slain.getCombatLevel();
        }
        return Math.min(MAXIMUM_POTENTIAL, potential) + (slainWights.size() << 1);
    }

    /**
     * Gets a random barrows wight still alive. Returns an empty optional if all are slain.
     * @return an optional barrows wight, or none if all are slain.
     */
    private Optional<BarrowsWight> getRandomAliveWight() {
        val list = Utils.getArrayList(BarrowsWight.values);
        list.removeAll(slainWights);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(Utils.random(list.size() - 1)));
    }

    /**
     * Gets a random barrows wight that has been already slain. Returns an empty optional if none have been slain.
     * @return an optional barrows wight, or none if none has been slain.
     */
    Optional<BarrowsWight> getRandomSlainWight() {
        if (slainWights.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Utils.getRandomCollectionElement(slainWights));
    }

    /**
     * Resets the player's barrows settings to new randomly generated ones.
     */
    public void reset() {
        hiddenWight = Utils.getRandomElement(BarrowsWight.values);
        corner = Utils.getRandomElement(BarrowsCorner.values);
        shiftDoorways();
        potential = 0;
        container.clear();
        looted = false;
        slainWights.clear();
        puzzleSolved = player.getPerkManager().isValid(PerkWrapper.RIDDLE_IN_THE_TUNNELS);
        skipTunnels = Utils.random(3) == 0;
        player.putBooleanAttribute("has_taken_damage_from_wight", false);
    }

    void shiftCorner() {
        val oldCorner = corner;
        int tryCount = 100;
        while(--tryCount > 0 && corner == oldCorner) {
            corner = Utils.getRandomElement(BarrowsCorner.values);
        }
    }

    public void enter(final BarrowsWight wight) {
        player.sendMessage("You break into the crypt.");
        player.setLocation(wight.getInChamber());
        if (slainWights.size() == 0) {
            if (player.getPrayerManager().getPrayerPoints() < 1) {
                player.putBooleanAttribute("faithless_barrows", true);
            }
        }
        if (player.getPrayerManager().getPrayerPoints() > 0) {
            player.putBooleanAttribute("faithless_barrows", false);
        }
    }

    public Optional<BarrowsWight> getMound() {
        val location = player.getLocation();
        for (val wight : BarrowsWight.values) {
            if (location.withinDistance(wight.getMoundCenter(), MOUND_RADIUS)) {
                return Optional.of(wight);
            }
        }
        return Optional.empty();
    }

    void refreshInterface() {
        val varManager = player.getVarManager();
        for (val wight : BarrowsWight.values) {
            varManager.sendBit(SLAIN_WIGHT_VARBIT + wight.ordinal(), slainWights.contains(wight));
        }
        varManager.sendBit(POTENTIAL_VARBIT,
                (int) ((float) Math.min(MAXIMUM_POTENTIAL, getFullPotential()) / (float) MAXIMUM_POTENTIAL * 1000F));
    }

    void refreshDoors() {
        val varManager = player.getVarManager();
        for (val doorway : CryptDoorway.values) {
            varManager.sendBit(doorway.varbitId, shutDoorways.contains(doorway));
        }
    }

    void refreshShaking() {
        if (!isLooted() || !player.inArea("Barrows chambers"))
            return;
        player.getPacketDispatcher().sendCameraShake(CameraShakeType.LEFT_AND_RIGHT, 5, 0, 0);
    }

    void refreshLadder(final Location position) {
        val varManager = player.getVarManager();
        val showing = varManager.getBitValue(LADDER_VARBIT) == 1;
        if (position.withinDistance(corner.ladder, LADDER_DISTANCE) != showing) {
            varManager.sendBit(LADDER_VARBIT, !showing);
        }
    }

    void calculateLoot() {
        val slainAmount = slainWights.size();
        val totalRolls = Math.max(1, slainAmount);
        var barrowsRolls = 0;
        var lootMultiplier = 1;
        if(rollForTripleLoot()) {
            lootMultiplier = 3;
            player.sendMessage(Colour.RS_GREEN.wrap("You manage to find triple the loot this time!"));
        } else if(rollForDoubleLoot()) {
            lootMultiplier = 2;
            player.sendMessage(Colour.RS_GREEN.wrap("You manage to find double the loot this time!"));
        }
        val availableBarrowsLoot = new ArrayList<Item>(slainAmount * 4);
        for (val wight : slainWights) {
            availableBarrowsLoot.addAll(Arrays.asList(wight.getArmour()));
        }
        while(barrowsRolls++ < totalRolls) {
            val n = (int) ((450 - (62 * slainAmount)) * 0.75F)/barrowsMultiplier; //for 6 slain brothers: was 1/12.75, now 1/9.75 (1/3.19 and 1/2.44 on event respectively)
            if (Utils.random(n) == 0) {
                for(int i = 0; i < lootMultiplier; i++) {
                    val item = availableBarrowsLoot.remove(Utils.random(availableBarrowsLoot.size() - 1));
                    item.setCharges(DegradableItem.getDefaultCharges(item.getId(), 0));
                    container.add(item);
                }
            }
        }
        val potential = getFullPotential();
        var remainingRolls = totalRolls - container.getSize();
        val isMorytaniaCompleted = DiaryReward.MORYTANIA_LEGS3.eligibleFor(player);
        boolean clue = false;
        while(remainingRolls-- > 0) {
            if (!clue) {
                if (Utils.random(player.getBooleanAttribute("Obtained elite Ca Rewards") ? 190 : 199) == 0) {
                    clue = true;
                    container.add(new Item(ClueItem.ELITE.getScrollBox(), 1 * lootMultiplier));
                }
            }
            val roll = Utils.random(potential);
            if (roll >= 1006) {
                if (roll == 1012) {
                    container.add(new Item(BarrowsReward.DRAGON_MED_HELM.item.getId(), 1 * lootMultiplier));
                } else {
                    container.add(new Item((Utils.random(1) == 0 ? BarrowsReward.LOOP_KEY_HALF : BarrowsReward.TOOTH_KEY_HALF).item.getId(), 1 * lootMultiplier));
                }
                continue;
            }
            for (val reward : BarrowsReward.values) {
                if (reward.maximumPotential >= roll) {
                    float modifier = 1F;
                    if (potential < reward.maximumPotential) {
                        val base = reward.maximumPotential - reward.requiredPotential;
                        val usersBase = potential - reward.requiredPotential;
                        modifier = (float) usersBase / (float) base;
                    }
                    val amount = (int) Math.floor(reward.item.getAmount() / 3F * (isMorytaniaCompleted ? 1.5F : 1F) * modifier);
                    container.add(new Item(reward.item.getId(), Utils.random(1, amount * lootMultiplier)));
                    break;
                }
            }
        }
        if (slainAmount == 6 && Utils.random(99) == 0) {
            container.add(new Item(12851, 1 * lootMultiplier));
        }
        if (slainWights.contains(BarrowsWight.GUTHAN)
                && slainWights.contains(BarrowsWight.DHAROK)
                && slainWights.contains(BarrowsWight.TORAG)
                && slainWights.contains(BarrowsWight.VERAC)) {
            if (player.getTemporaryAttributes().getOrDefault("has_been_hit_by_guthan", true).equals(false)
                    && player.getTemporaryAttributes().getOrDefault("has_been_hit_by_dharok", true).equals(false)
                    && player.getTemporaryAttributes().getOrDefault("has_been_hit_by_verac", true).equals(false)
                    && player.getTemporaryAttributes().getOrDefault("has_been_hit_by_torag", true).equals(false)
                    && !player.getBooleanAttribute("medium-combat-achievement17")) {
                player.putBooleanAttribute("medium-combat-achievement17", true);
                MediumTasks.sendMediumCompletion(player, 17);
            }
        }
        if (slainAmount == 6 && !player.getBooleanAttribute("has_taken_damage_from_wight") && !player.getBooleanAttribute("medium-combat-achievement24")) {
            player.putBooleanAttribute("medium-combat-achievement24", true);
            MediumTasks.sendMediumCompletion(player, 24);
        }
        if (player.getPrayerManager().getPrayerPoints() < 1
                && player.getBooleanAttribute("faithless_barrows")
                && !player.getBooleanAttribute("hard-combat-achievement41")
                && slainAmount == 6) {
            player.putBooleanAttribute("hard-combat-achievement41", true);
            HardTasks.sendHardCompletion(player, 41);
        }
    }

    private boolean rollForDoubleLoot() {
        if(Utils.random(0, 100) < getDoubleChance(player)) {
            return true;
        }
        return false;
    }

    private boolean rollForTripleLoot() {
        if(Utils.random(0, 100) < getTripleChance(player)) {
            return true;
        }
        return false;
    }

    private int getDoubleChance(Player p) {
        switch(p.getMemberRank()) {
            case RUBY_MEMBER:
            case DIAMOND_MEMBER:
            case DRAGONSTONE_MEMBER:
            case ONYX_MEMBER:
            case ZENYTE_MEMBER:
                return 20;
            default:
            case NONE:
            case SAPPHIRE_MEMBER:
            case EMERALD_MEMBER:
                return 0;
        }
    }

    private int getTripleChance(Player p) {
        switch(p.getMemberRank()) {
            case ONYX_MEMBER:
            case ZENYTE_MEMBER:
                return 10;
            default:
            case NONE:
            case SAPPHIRE_MEMBER:
            case EMERALD_MEMBER:
            case RUBY_MEMBER:
            case DIAMOND_MEMBER:
            case DRAGONSTONE_MEMBER:
                return 0;
        }
    }

    /**
     * Adds the loot to the player's inventory, or drops it under them. Refreshes the containers.
     */
    void addLoot() {
        if (container.isEmpty())
            return;
        val inventory = player.getInventory().getContainer();
        container.getItems().int2ObjectEntrySet().fastForEach(entry -> {
            player.getCollectionLog().add(entry.getValue());
            val runePouch = player.getRunePouch();
            val amountInRunePouch = runePouch.getAmountOf(entry.getValue().getId());
            val addToRunePouch = player.getInventory().containsItem(12791, 1)
                    && amountInRunePouch > 0 && (amountInRunePouch + entry.getValue().getAmount()) < 16000;
            val addToQuiver = (player.getEquipment().getId(EquipmentSlot.AMMUNITION) == entry.getValue().getId() || (entry.getValue().isStackable() && player.getEquipment().getId(EquipmentSlot.WEAPON) == entry.getValue().getId()));
            val container = addToQuiver ? player.getEquipment().getContainer() : addToRunePouch ? runePouch.getContainer() : inventory;
            container.add(entry.getValue()).onFailure(remainder -> World.spawnFloorItem(remainder, player));
        });
        player.getRunePouch().getContainer().refresh(player);
        player.getEquipment().getContainer().refresh(player);
        inventory.refresh(player);
        container.refresh(player);
        container.clear();
        player.getInterfaceHandler().closeInterface(GameInterface.BARROWS_OVERLAY);
    }

    void shiftDoorways() {
        shutDoorways.clear();
        if (player.getPerkManager().isValid(PerkWrapper.RIDDLE_IN_THE_TUNNELS) || true) {
            return;
        }
        val cornerDoorways = new ArrayList<CryptDoorway>(SHUT_DOORWAYS >> 1);
        val centerDoorways = new ArrayList<CryptDoorway>(SHUT_DOORWAYS >> 1);
        cornerDoorways.addAll(Arrays.asList(corner.room.doorways));
        cornerDoorways.remove(Utils.random(cornerDoorways.size() - 1));
        centerDoorways.addAll(Arrays.asList(CryptDoorway.centerDoorways));
        this.openDoorway = centerDoorways.remove(Utils.random(centerDoorways.size() - 1));
        shutDoorways.addAll(cornerDoorways);
        shutDoorways.addAll(centerDoorways);
    }

    void onDeath(final BarrowsNPC npc) {
        if (npc instanceof BarrowsWightNPC) {
            if (isLooted())
                return;
            slainWights.add(((BarrowsWightNPC) npc).getWight());
        } else {
            if (isLooted())
                return;
            potential = Math.min(MAXIMUM_POTENTIAL, potential + npc.getCombatLevel());
        }
        refreshInterface();
    }

    void onFinish(final BarrowsNPC npc) {
        if (npc instanceof BarrowsWightNPC) {
            player.getPacketDispatcher().resetHintArrow();
        }
    }

    void removeTarget() {
        if (currentWight == null)
            return;
        player.getPacketDispatcher().resetHintArrow();
        if (!currentWight.isDead()) {
            currentWight.finish();
        }
        currentWight = null;
    }

    void sendRandomTarget(final Location position) {
        val random = Utils.random(CRYPT_NPC_WEIGHT + BarrowsWight.values.length - slainWights.size());
        if (currentWight == null && random > CRYPT_NPC_WEIGHT) {
            getRandomAliveWight().ifPresent(wight -> sendWight(wight, position, null));
        } else {
            val wightsList = BarrowsNPC.getWightsList(player);
            if (wightsList.size() >= 9) {
                return;
            }
            sendCryptNPC(position);
        }
    }

    void sendWight(final BarrowsWight wight, final Location location, final String message) {
        if (!player.inArea("Barrows chambers"))
            throw new RuntimeException("Unable to invocate target outside of barrows chambers.");
        val definitions = NPCDefinitions.getOrThrow(wight.getNpcId());
        val size = definitions.getSize();
        val npc = World.invoke(wight.getNpcId(), getSpawnTile(location, size), Direction.SOUTH, 5);
        this.currentWight = (BarrowsWightNPC) npc;
        currentWight.owner = new WeakReference<>(player);
        npc.spawn();
        npc.setSpawned(true);
        player.getPacketDispatcher().sendHintArrow(new HintArrow(npc));
        if (message != null) {
            npc.setForceTalk(new ForceTalk(message));
        }
        val name = definitions.getName().toLowerCase();
        if (name.contains("guthan")) {
            player.getTemporaryAttributes().put("has_been_hit_by_guthan", false);
        } else if (name.contains("dharok")){
            player.getTemporaryAttributes().put("has_been_hit_by_dharok", false);
        } else if (name.contains("verac")){
            player.getTemporaryAttributes().put("has_been_hit_by_verac", false);
        } else if (name.contains("torag")){
            player.getTemporaryAttributes().put("has_been_hit_by_torag", false);
        }
        WorldTasksManager.schedule(() -> npc.getCombat().forceTarget(player));
    }

    private void sendCryptNPC(final Location location) {
        if (!player.inArea("Barrows chambers"))
            throw new RuntimeException("Unable to invocate target outside of barrows chambers.");
        val id = cryptMonsters.getInt(Utils.random(cryptMonsters.size() - 1));
        val definitions = NPCDefinitions.getOrThrow(id);
        val size = definitions.getSize();
        val npc = World.invoke(id, getSpawnTile(location, size), Direction.SOUTH, 5);
        ((BarrowsNPC) npc).owner = new WeakReference<>(player);
        npc.setSpawned(true);
        npc.spawn();
        npc.freeze(1);
        npc.getCombat().forceTarget(player);
        npc.getCombat().setCombatDelay(2);
    }

    private Location getSpawnTile(final Location tile, final int size) {
        int count = SPAWN_ATTEMPT_COUNT;
        Location spawnTile;
        while(ProjectileUtils.isProjectileClipped(null, null, tile,
                (spawnTile = new Location(tile, DEFAULT_SPAWN_DISTANCE + size)), true)
                || !World.isFloorFree(spawnTile, size) || tile.matches(spawnTile)) {
            if (--count == 0) {
                return tile;
            }
        }
        return spawnTile;
    }

    int getAndDecrementTimer() {
        val timer = Math.max(0, --this.timer);
        if (timer == 0) {
            resetTimer();
        }
        return timer;
    }



}
