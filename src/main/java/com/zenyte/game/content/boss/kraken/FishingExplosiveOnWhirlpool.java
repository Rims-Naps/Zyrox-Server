package com.zenyte.game.content.boss.kraken;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Cresinkel
 */

public class FishingExplosiveOnWhirlpool implements ItemOnNPCAction {

    private static final Animation animation = new Animation(2779);
    private static final Graphics throwingGraphics = new Graphics(51, 0, 96);
    private static final Projectile throwingProjectile = new Projectile(50, 34, 10, 20, 25, 0, 11, 3);
    private static final SoundEffect explosionSound = new SoundEffect(1487, 10, 0);


    @Override
    public void handleItemOnNPCAction(Player player, Item item, int slot, NPC npc) {
        player.getInventory().deleteItem(ItemId.FISHING_EXPLOSIVE_6664, 1);
        player.faceEntity(npc);
        player.stopAll();
        player.setAnimation(animation);
        player.setGraphics(throwingGraphics);
        player.sendSound(2708);
        val delay = World.sendProjectile(player.getLocation(), npc, throwingProjectile);
        WorldTasksManager.schedule(() -> {
            World.sendSoundEffect(npc, explosionSound);
            Kraken.startKraken(player, (Kraken) npc);
        }, delay + 1);
    }

    @Override
    public Object[] getItems() {
        return new Object[] {ItemId.FISHING_EXPLOSIVE_6664};
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {NpcId.WHIRLPOOL_496};
    }

    @Override
    public void handle(Player player, Item item, int slot, NPC npc) {
        player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc,player.getLocation().getTileDistance(npc.getLocation()) - 1),false));
        handleItemOnNPCAction(player,item,slot,npc);
    }
}
