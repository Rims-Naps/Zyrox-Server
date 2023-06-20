package com.zenyte.plugins.object;

import com.zenyte.game.content.skills.prayer.actions.Ashes;
import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Kris | 25/04/2019 20:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ChaosAltar implements ItemOnObjectAction {

    private static final Location tile = new Location(2947, 3820, 0);

    @Override
    public Object[] getObjects() {
        return new Object[] { 411 };
    }

    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, int slot, final WorldObject object) {
        if (!object.matches(tile)) {
            player.sendMessage("Nothing interesting happens.");
            return;
        }
        if(Bones.getBone(item.getId()) != null) {
            player.getActionManager().setAction(new OfferingAction(Objects.requireNonNull(Bones.getBone(item.getId())), item, object));
        } else if(Ashes.getAsh(item.getId()) != null) {
            player.getActionManager().setAction(new OfferingAction(Objects.requireNonNull(Ashes.getAsh(item.getId())), item, object));
        }


    }

    @Override
    public Object[] getItems() {
        val list = new ArrayList<Object>(Bones.VALUES.length + Ashes.VALUES.length);
        for (val bone : Bones.VALUES) {
            for (val b : bone.getBones()) {
                list.add(b.getId());
            }
        }
        for (val ashes : Ashes.VALUES) {
            for (val a : ashes.getAshes()) {
                list.add(a.getId());
            }
        }
        return list.toArray(new Object[0]);
    }

    private static final class OfferingAction extends Action {

        private static final String CHAOS_ALTAR_MESSAGE = "The Dark Lord spares your sacrifice but still rewards you for your efforts.";
        private static final Animation OFFERING_ANIM = new Animation(3705);

        OfferingAction(final Bones bone, final Item item, final WorldObject altar) {
            this.bone = bone;
            this.ashes = null;
            this.item = item;
            this.altar = altar;
        }

        OfferingAction(final Ashes ashes, final Item item, final WorldObject altar) {
            this.ashes = ashes;
            this.item = item;
            this.altar = altar;
            this.bone = null;
        }

        private final Ashes ashes;
        private final Item item;
        private final Bones bone;
        private final WorldObject altar;

        @Override
        public boolean initiateOnPacketReceive() {
            return true;
        }

        @Override
        public boolean start() {
            if (!player.getInventory().containsItem(item)) {
                player.sendMessage("You don't have any " + item.getName().toLowerCase() + " to sacrifice.");
                return false;
            }
            if (bone == Bones.SUPERIOR_DRAGON_BONES) {
                if (player.getSkills().getLevelForXp(Skills.PRAYER) < 70) {
                    player.sendMessage("You need a Prayer level of at least 70 to sacrifice superior dragon bones.");
                    return false;
                }
            }
            return true;
        }

        @Override
        public void stop() {
            player.getActionManager().setActionDelay(1);
        }

        @Override
        public boolean process() {
            return true;
        }

        @Override
        public int processWithDelay() {
            if (!player.getInventory().containsItem(item)) {
                return -1;
            }
            player.setAnimation(OFFERING_ANIM);
            player.faceObject(altar);
            if (bone != null && bone.equals(Bones.SUPERIOR_DRAGON_BONES)) {
                player.getDailyChallengeManager().update(SkillingChallenge.OFFER_SUPERIOR_DRAGON_BONES_CHAOS_ALTAR);
            }
            if (Utils.random(1) == 0) {
                player.sendFilteredMessage(CHAOS_ALTAR_MESSAGE);
            } else {
                player.getInventory().deleteItem(item);
            }
            if(bone != null) {
                player.getSkills().addXp(Skills.PRAYER, bone.getXp() * 3.5F);
            } else if(ashes != null) {
                player.getSkills().addXp(Skills.PRAYER, ashes.getXp() * 3.5F);
            }

            return 3;
        }

    }

}