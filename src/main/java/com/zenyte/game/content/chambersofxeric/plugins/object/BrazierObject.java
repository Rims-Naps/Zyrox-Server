package com.zenyte.game.content.chambersofxeric.plugins.object;

import com.zenyte.game.content.chambersofxeric.room.IceDemonRoom;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import static com.zenyte.game.content.chambersofxeric.room.IceDemonRoom.depositAnimation;

/**
 * @author Kris | 06/07/2019 03:59
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BrazierObject implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player, IceDemonRoom.class, room -> {
            val chance = player.getSkills().getLevel(Skills.FIREMAKING) / 6;
            player.getActionManager().setAction(new Action() {
                @Override
                public boolean start() {
                    return true;
                }

                @Override
                public boolean process() {
                    return preconditions(player, object, room);
                }

                @Override
                public int processWithDelay() {
                    if (object.getId() == 29747 && Utils.random(80 + chance) < 5) {
                        player.setAnimation(depositAnimation);
                        player.sendSound(new SoundEffect(2066));
                        return 2;
                    }
                    light(player, object);
                    return -1;
                }
            });
        }));
    }

    private boolean preconditions(@NotNull final Player player, @NotNull final WorldObject object, @NotNull final IceDemonRoom room) {
        val inventory = player.getInventory();
        if (object.getId() == 29747) {
            val demon = room.getDemon();
            if (demon != null && demon.getStage() != 0) {
                player.sendMessage("That's not going to help now.");
                return false;
            }
            if (!inventory.containsItem(20799, 1)) {
                player.sendMessage("You need some kindling to light the brazier.");
                return false;
            }
            if (!inventory.containsItem(590, 1)) {
                player.sendMessage("You need a tinderbox to light the brazier.");
                return false;
            }
        }
        if (!inventory.containsItem(20799, 1)) {
            player.sendMessage("You need some kindling to light the brazier.");
            return false;
        }
        return true;
    }

    private void light(@NotNull final Player player, @NotNull final WorldObject object) {
        player.getRaid().ifPresent(raid -> raid.ifInRoom(player, IceDemonRoom.class, room -> {
            if (object.getId() == 29747) {
                if (room.getDemon().getStage() != 0) {
                    player.sendMessage("That's not going to help now.");
                    return;
                }
                if (!player.getInventory().containsItem(20799, 1)) {
                    player.sendMessage("You need some kindling to light the brazier.");
                    return;
                }
                if (!player.getInventory().containsItem(590, 1)) {
                    player.sendMessage("You need a tinderbox to light the brazier.");
                    return;
                }
                val icefiend = Utils.findMatching(room.getIcefiends(), i -> i.getLocation().withinDistance(object, 1));
                val amount = player.getInventory().getAmountOf(20799);
                val brazier = new IceDemonRoom.Brazier(room, amount * 5, icefiend, 29748, object.getType(), object.getRotation(), new Location(object));
                World.spawnObject(brazier);
                room.getBraziers().add(brazier);
                raid.addPoints(player, amount * 10);
                player.setAnimation(depositAnimation);
                player.sendMessage("You put the kindling in the brazier and light it on fire.");
                player.sendSound(new SoundEffect(2066));
                player.getSkills().addXp(Skills.FIREMAKING, amount * 5);
                player.getInventory().deleteItem(20799, amount);
                return;
            }
            if (!player.getInventory().containsItem(20799, 1)) {
                player.sendMessage("You need some kindling to light the brazier.");
                return;
            }
            val brazier = Utils.findMatching(room.getBraziers(), b -> b == object);
            if (brazier == null) {
                return;
            }
            val amount = player.getInventory().getAmountOf(20799);
            brazier.setKindling(brazier.getKindling() + (amount * 5));
            raid.addPoints(player, amount * 10);
            player.setAnimation(new Animation(832));
            player.sendMessage("You add the kindling to the fire in the brazier.");
            player.getSkills().addXp(Skills.FIREMAKING, amount * 5);
            player.getInventory().deleteItem(20799, amount);
        }));
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{
                29748, 29747
        };
    }
}
