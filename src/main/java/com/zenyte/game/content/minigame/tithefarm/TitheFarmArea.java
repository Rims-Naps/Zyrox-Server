package com.zenyte.game.content.minigame.tithefarm;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.area.plugins.LayableTrapRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.LogoutPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.plugins.events.LoginEvent;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import static com.zenyte.plugins.object.TitheFarmSeedTable.*;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmArea extends DynamicArea implements CannonRestrictionPlugin, LayableTrapRestrictionPlugin, LogoutPlugin, CycleProcessPlugin {

    private static final String lastInstanceKey = "Last tithe farm index";

    private final Location welcomeTile;
    private final Location goodbyeTile;

    protected TitheFarmArea(final int index, AllocatedArea allocatedArea, int copiedChunkX, int copiedChunkY) {
        super(allocatedArea, copiedChunkX, copiedChunkY);
        welcomeTile = getLocation(new Location(1776, 3591, 0));
        goodbyeTile = new Location(1779, 3591, 0);
        this.index = index;
    }

    private final void wipeArea() {
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1784, 3603, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1784, 3600, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 0, getLocation(new Location(1779, 3603, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 3, getLocation(new Location(1779, 3600, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 0, getLocation(new Location(1771, 3592, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1766, 3592, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1771, 3589, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1766, 3589, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1771, 3586, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1766, 3586, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1771, 3583, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 3, getLocation(new Location(1766, 3583, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1771, 3575, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1766, 3575, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1761, 3575, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1771, 3572, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1766, 3572, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1761, 3572, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1771, 3569, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1766, 3569, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1761, 3569, 0))));

        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 1, getLocation(new Location(1771, 3566, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 2, getLocation(new Location(1766, 3566, 0))));
        World.spawnObject(new WorldObject(ObjectId.TITHE_PATCH, 10, 3, getLocation(new Location(1761, 3566, 0))));
    }

    private final int index;

    @Override
    public void constructed() {
        this.area.getAllocatedRegions().forEach((IntConsumer) id -> World.getRegion(id).load());
        wipeArea();
    }

    @Override
    public Location onLoginLocation() {
        return goodbyeTile;
    }

    public void enterInstance(@NotNull final Player player) {
        new FadeScreen(player, () -> player.setLocation(welcomeTile)).fade(2);
    }

    public void leaveInstance(@NotNull final Player player) {
        new FadeScreen(player, () -> player.setLocation(goodbyeTile)).fade(2);
    }

    @Override
    public final void enter(final Player player) {
        player.getAttributes().put(lastInstanceKey, index);
        GameInterface.TITHE_FARM_SACK.open(player);

        if(player.getInventory().hasFreeSlots() && !player.getInventory().containsItem(ItemId.GRICOLLERS_FERTILISER)) {
            player.getInventory().addItem(ItemId.GRICOLLERS_FERTILISER, 1);
        }
        player.getVarManager().sendBit(4900, 0); //Set fruit back to 0
    }

    private void resetVars() {


    }

    /**
     * We don't destroy tithe farm areas, let's just leave them be.
     */
    @Override
    public void destroyRegion() {

    }

    @Override
    public final void leave(final Player player, final boolean logout) {
        if (!logout) {
            player.getInventory().deleteItem(GOLOVANOVA_SEED);
            player.getInventory().deleteItem(BOLOGANO_SEED);
            player.getInventory().deleteItem(LOGAVANO_SEED);
            player.getInventory().deleteItem(ItemId.GOLOVANOVA_FRUIT, player.getInventory().getAmountOf(ItemId.GOLOVANOVA_FRUIT));
            player.getInventory().deleteItem(ItemId.BOLOGANO_FRUIT, player.getInventory().getAmountOf(ItemId.BOLOGANO_FRUIT));
            player.getInventory().deleteItem(ItemId.LOGAVANO_FRUIT, player.getInventory().getAmountOf(ItemId.LOGAVANO_FRUIT));
            player.getInventory().deleteItem(ItemId.GRICOLLERS_FERTILISER, player.getInventory().getAmountOf(ItemId.GRICOLLERS_FERTILISER));
            player.getAttributes().remove(lastInstanceKey);

            // remove current fruit
            player.addAttribute("tithe_farm_fruit", 0);

            player.getInterfaceHandler().closeInterface(GameInterface.TITHE_FARM_SACK);
        }

        // plants will die / be removed from the global list when a player leaves.
        final List<TithePlant> plantsToRemove = new ArrayList<>();

        for(final TithePlant plant : TithePlant.GLOBAL_PLANTS.values()) {
            if(plant.getOwner() == player) {
                plantsToRemove.add(plant);
            }
        }

        // remove all plants the player owned now that we're done with it
        for(final TithePlant plant : plantsToRemove) {
            plant.die();
        }
    }

    @Override
    public final String name() {
        return "Tithe Farm #" + index;
    }

    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        val player = event.getPlayer();
        val isPresent = player.getAttributes().containsKey(lastInstanceKey);
        if (isPresent) {
            player.lock(2);
            val index = player.getNumericAttribute(lastInstanceKey).intValue();
            player.getAttributes().remove(lastInstanceKey);
            TitheFarmManager.getSingleton().selectArea(index).ifPresent(area -> {
                WorldTasksManager.schedule(() -> area.enterInstance(player));
            });
        }
    }

    @Override
    public void process() {
        for(final TithePlant plant : TithePlant.GLOBAL_PLANTS.values()) {
            plant.process();
        }
    }
}
