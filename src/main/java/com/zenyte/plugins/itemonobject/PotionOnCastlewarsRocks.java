package com.zenyte.plugins.itemonobject;

import com.zenyte.game.content.minigame.castlewars.CastleWars;
import com.zenyte.game.content.minigame.castlewars.CastleWarsOverlay.CWarsOverlayVarbit;
import com.zenyte.game.content.minigame.castlewars.CastleWarsTeam;
import com.zenyte.game.content.minigame.castlewars.CastlewarsRockPatch;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.plugins.itemonnpc.ItemOnBarricadeAction;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class PotionOnCastlewarsRocks implements ItemOnObjectAction {

    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        if(item.getId() != 4045)
            return;

        /** Explosive potion on rocks */
        if(object.getId() == 4437 || object.getId() == 4438) {
            player.getInventory().deleteItem(item);
            World.sendGraphics(ItemOnBarricadeAction.EXPLOSION, object);

            if(object.getId() == 4437) {
                World.spawnObject(new WorldObject(4438, object.getType(), object.getRotation(), object));
            } else {
                processVarbits(object, false);
                World.removeObject(object);
            }

            player.sendMessage(object.getId() == 4437 ? "You manage to remove some of the rocks." : "You manage to clear the rest of the rocks.");
        }

        /** Explosive potion to collapse walls */
        if(object.getId() == 4448) {
            val wallData = CastlewarsRockPatch.getData(object);
            if(wallData == null) {
                return;
            }

            player.getInventory().deleteItem(item);
            World.sendGraphics(ItemOnBarricadeAction.EXPLOSION, object);
            World.spawnObject(wallData.getPatch());
            player.sendMessage("You've collapsed the tunnel!");

            processVarbits(object, true);

            CharacterLoop.forEach(wallData.getPatch(), 1, Entity.class, entity -> {
                if (Utils.collides(wallData.getPatch().getX(), wallData.getPatch().getY(), 2, entity.getX(), entity.getY(), entity.getSize())) {
                    if (entity instanceof Player) {
                        entity.applyHit(new Hit(entity.getHitpoints(), HitType.REGULAR));
                    }
                }
            });
        }
    }

    public static void processVarbits(final WorldObject object, final boolean collapsed) {
        val wallData = CastlewarsRockPatch.getData(object);

        if(wallData == null) {
            return;
        }

        val saradomin = CastleWarsTeam.SARADOMIN.getRockPatches().contains(wallData);
        val team = saradomin ? CastleWarsTeam.SARADOMIN : CastleWarsTeam.ZAMORAK;
        val varbit = wallData.equals(CastlewarsRockPatch.SOUTH) || wallData.equals(CastlewarsRockPatch.NORTH) ? CWarsOverlayVarbit.ROCKS_NS : CWarsOverlayVarbit.ROCKS_EW;
        CastleWars.setVarbit(team, varbit, collapsed ? 0 : 1);
    }

    @Override
    public Object[] getItems() {
        return new Object[] { 4045 };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4437, 4438, 4448 };
    }
}
