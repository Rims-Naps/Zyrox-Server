package com.zenyte.game.content.skills.farming;

import com.google.gson.JsonDeserializer;
import com.zenyte.GameEngine;
import com.zenyte.game.content.skills.farming.contract.FarmingContract;
import com.zenyte.game.content.skills.farming.plugins.FarmingGuildArea;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Kris | 03/02/2019 02:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Farming {

    /**
     * Information:
     * https://www.reddit.com/r/runescape/comments/9dq861/mushroom_patch_timers/
     * _Even if a gardener is protecting a patch, they can still get diseased, and if they do, the miss a growth tick._
     */
    private transient final Player player;
    private final List<FarmingSpot> spots = new ObjectArrayList<>();
    @Getter private final FarmingStorage storage;
    private transient final Set<FarmingPatch> refreshedPatches = new ObjectOpenHashSet<>();

    public Farming(final Player player) {
        this.player = player;
        this.storage = new FarmingStorage(player);
    }

    public Farming(final Player player, final Farming farming) {
        this.player = player;
        this.spots.addAll(farming.spots);
        this.spots.forEach(spot -> spot.setPlayer(player));
        this.storage = new FarmingStorage(player, farming.storage.getStorage());
    }

    public void refresh() {
        val x = player.getX();
        val y = player.getY();
        if (!this.refreshedPatches.isEmpty()) {
            this.refreshedPatches.removeIf(patch -> !patch.getRectangle().contains(x, y));
        }
        for (val patch : FarmingPatch.values) {
            if (!patch.getRectangle().contains(x, y) || refreshedPatches.contains(patch)) {
                continue;
            }
            refreshedPatches.add(patch);
            refreshPatch(patch);
        }
    }

    public void refreshPatch(final FarmingPatch patch) {
        val spot = getSpot(patch);
        if (spot.isPresent()) {
            spot.get().refresh();
            return;
        }
        player.getVarManager().sendBit(patch.getVarbit(), 0);
    }


    public void processAll() {
        if (!spots.isEmpty()) {
            for (val spot : spots) {
                spot.process();
            }
        }
    }

    public void reset() {
        this.spots.clear();
        this.refreshedPatches.clear();
        refresh();
    }

    public FarmingSpot create(final WorldObject patchObject) {
        return create(getPatch(patchObject).orElseThrow(RuntimeException::new));
    }

    public FarmingSpot create(@NotNull final FarmingPatch patch) {
        val existingSpot = getSpot(patch);
        if (existingSpot.isPresent()) {
            return existingSpot.get();
        }
        val spot = new FarmingSpot(player, patch);
        if (player.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            spot.setFlag(PatchFlag.ULTRACOMPOST);
        } else if (player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            spot.setFlag(PatchFlag.SUPERCOMPOST);
        } else if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER)) {
            spot.setFlag(PatchFlag.COMPOST);
        }
        spots.add(spot);
        return spot;
    }

    public void handleContractCompletion(@NotNull final Player player, @NotNull final FarmingProduct product) {
        if (player.inArea(FarmingGuildArea.class)) {
            try {
                val attr = player.getAttributes().get(FarmingContract.CONTRACT_ATTR);
                if (attr == null) {
                    return;
                }
                val contractName = attr.toString();
                if (contractName == null || contractName.isEmpty()) {
                    return;
                }
                val currentContract = FarmingContract.getByName(contractName);
                if (Objects.equals(product, currentContract.getProduct())) {
                    currentContract.complete(player);
                }
            } catch (Exception e) {
                GameEngine.logger.error(Strings.EMPTY, e);
            }
        }
    }

    private final Optional<FarmingSpot> getSpot(@NotNull final FarmingPatch patch) {
        val spot = Utils.findMatching(spots, s -> patch.equals(s.getPatch()));
        return spot == null ? Optional.empty() : Optional.of(spot);
    }

    public final Optional<FarmingPatch> getPatch(@NotNull final WorldObject object) {
        val patch = Utils.findMatching(FarmingPatch.values, p -> ArrayUtils.contains(p.getIds(), object.getId()));
        return patch == null ? Optional.empty() : Optional.of(patch);
    }

    public static final JsonDeserializer<FarmingSpot> deserializer() {
        return (json, typeOfT, context) -> {
            val map = new EnumMap<>(FarmingAttribute.class);
            assert json.isJsonObject();
            val elementsMap = Objects.requireNonNull(json.getAsJsonObject().get("map"));
            assert elementsMap.isJsonObject();
            elementsMap.getAsJsonObject().entrySet().forEach(element -> {
                val attr = FarmingAttribute.valueOf(element.getKey());
                if (attr == FarmingAttribute.FLAGS) {
                    val set = EnumSet.noneOf(PatchFlag.class);
                    val value = element.getValue();
                    assert value.isJsonArray();
                    for (val e : value.getAsJsonArray()) {
                        set.add(PatchFlag.valueOf(e.getAsString()));
                    }
                    map.put(attr, set);
                    return;
                }
                map.put(attr, context.deserialize(element.getValue(), attr.getKey().clazz()));
            });
            return new FarmingSpot(map);
        };
    }

    Optional<FarmingSpot> getNearbyFlowerPatch(@NotNull final FarmingSpot allotment) {
        assert allotment.getPatch().getType() == PatchType.ALLOTMENT;
        val rectangle = allotment.getPatch().getRectangle();
        for (val spot : spots) {
            val patch = spot.getPatch();
            if (patch.getType() != PatchType.FLOWER_PATCH
                    || spot.getState() != PatchState.GROWN && spot.getProduct() != FarmingProduct.SCARECROW) {
                continue;
            }
            val tile = patch.getCenter();
            if (rectangle.contains(tile.getX(), tile.getY())) {
                return Optional.of(spot);
            }
        }
        return Optional.empty();
    }

    public int getGrownCount(@NotNull final PatchType patch, @NotNull final Predicate<FarmingSpot> predicate) {
        int count = 0;
        for (val spot : spots) {
            if (patch.equals(spot.getPatch().getType())) {
                if (predicate.test(spot)) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean containsGrownSpiritTree(final FarmingPatch patch) {
        for (val spot : spots) {
            if (patch.equals(spot.getPatch())) {
                return PatchState.GROWN.equals(spot.getState());
            }
        }
        return false;
    }
}
