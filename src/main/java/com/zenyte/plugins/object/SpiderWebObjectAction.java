package com.zenyte.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.Equipment;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Kris | 9. juuni 2018 : 07:19:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class SpiderWebObjectAction implements ObjectAction {

    private static final Animation KNIFE_ANIMATION = new Animation(911);

    public static final Predicate<String> sharpBladePredicate = name -> name.contains("scythe") || name.contains("sword") || name.contains("dagger")
            || name.contains("scimitar") || name.contains("axe") || name.equals("knife") || name.contains("whip") || name.contains("bludgeon") || name.contains("tentacle")  || name.contains("saeldor") || name.contains("staff of the dead") || name.contains("staff of light") || name.contains("dragon claws");

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val item = player.getWeapon();
        if (item != null && item.getDefinitions().getSlot() != -1 && sharpBladePredicate.test(item.getName().toLowerCase())) {
            SpiderWebObjectAction.slash(player, object, item);
            return;
        }
        if (player.getInventory().containsItem(946, 1)) {
            slash(player, object, new Item(946));
            return;
        }
        player.sendMessage("Only a sharp blade can cut through this sticky web.");
    }

    public static final void slash(@NotNull final Player player, @NotNull final WorldObject web, @NotNull final Item weapon) {
        player.lock(1);
        player.setAnimation(weapon.getId() == 946 ? KNIFE_ANIMATION : new Animation(Equipment.getAttackAnimation(weapon.getId(), 1)));
        player.sendSound(2500);
        val random = weapon.getName().toLowerCase().contains("wilderness sword") ? 0 : Utils.random(2);
        if (weapon.getId() != 946) {
            val defs = weapon.getDefinitions();
            player.getAppearance().setRenderAnimation(new RenderAnimation(defs.getStandAnimation(), defs.getStandTurnAnimation(), defs.getWalkAnimation(),
                    defs.getRotate180Animation(), defs.getRotate90Animation(), defs.getRotate270Animation(), defs.getRunAnimation()));
            player.getAppearance().forceAppearance(EquipmentSlot.WEAPON.getSlot(), weapon.getId());
            if (weapon.getDefinitions().isTwoHanded()) {
                player.getAppearance().forceAppearance(EquipmentSlot.SHIELD.getSlot(), -1);
            }
            WorldTasksManager.schedule(() -> {
                player.getAppearance().resetRenderAnimation();
                player.getAppearance().clearForcedAppearance();
            });
        }
        if (random == 0) {
            web.setLocked(true);
            WorldTasksManager.schedule(() -> {
                web.setLocked(false);
                World.spawnTemporaryObject(new WorldObject(734, web.getType(), web.getRotation(), web), web, 100);
            });
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 733 };
    }

}
