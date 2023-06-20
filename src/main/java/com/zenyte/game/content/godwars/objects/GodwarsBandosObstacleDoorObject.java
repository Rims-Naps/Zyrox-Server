package com.zenyte.game.content.godwars.objects;

import com.zenyte.game.content.skills.smithing.Smithing;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 24-3-2019 | 13:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GodwarsBandosObstacleDoorObject implements ObjectAction {

    private static final Item DRAGON_WARHAMMER = new Item(13576);
    private static final Item STATIUS_WARHAMMER = new Item(22622);
    private static final Animation HAMMER_ANIM = new Animation(7214);
    private static final Animation DWH_ANIM = new Animation(7215);
    private static final Animation SWH_ANIM = new Animation(10002);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        val requireHammer = player.getX() >= 2851;
        if (requireHammer) {
            if (player.getSkills().getLevel(Skills.STRENGTH) < 70) {
                player.sendMessage("You need a Strength level of at least 70 to ring the gong.");
                return;
            }
            if (!player.getInventory().containsItem(Smithing.HAMMER) && !player.getInventory().containsItem(DRAGON_WARHAMMER) && player.getEquipment().getId(EquipmentSlot.WEAPON) != DRAGON_WARHAMMER.getId() && !player.getInventory().containsItem(STATIUS_WARHAMMER) && player.getEquipment().getId(EquipmentSlot.WEAPON) != STATIUS_WARHAMMER.getId()) {
                player.sendMessage("You need a suitable hammer to ring the gong.");
                return;
            }
        }
        if (requireHammer) {
            val dwh = player.getInventory().containsItem(DRAGON_WARHAMMER) || player.getEquipment().getId(EquipmentSlot.WEAPON) == DRAGON_WARHAMMER.getId();
            val swh = player.getInventory().containsItem(STATIUS_WARHAMMER) || player.getEquipment().getId(EquipmentSlot.WEAPON) == STATIUS_WARHAMMER.getId();
            player.lock();
            player.setAnimation(dwh ? DWH_ANIM : swh ? SWH_ANIM : HAMMER_ANIM);
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    switch (ticks++) {
                        case 0:
                            object.setLocked(true);
                            break;
                        case 1:
                            World.removeObject(object);
                            World.sendSoundEffect(object, new SoundEffect(71, 5, 0));
                            player.addWalkSteps(2850, 5333);
                            break;
                        case 2:
                            player.unlock();
                            World.spawnObject(object);
                            object.setLocked(false);
                            stop();
                            break;
                    }
                }
            }, 0, 1);
        } else {
            player.lock();
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    switch (ticks++) {
                        case 0:
                            World.removeObject(object);
                            World.sendSoundEffect(object, new SoundEffect(71, 5, 0));
                            player.addWalkSteps(2851, 5333);
                            break;
                        case 1:
                            player.unlock();
                            World.spawnObject(object);
                            stop();
                            break;
                    }
                }
            }, 0, 1);
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 26461 };
    }
}
