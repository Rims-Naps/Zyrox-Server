package com.zenyte.game.content.ogrechest;

import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class OgreCoffin implements ObjectAction {

    private static final Animation animation = new Animation(832);
    private static final Location ogre_chest = new Location(2914, 3452, 0);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!player.getInventory().containsItem(4850, 1)) {
            player.sendMessage("This chest is securely locked shut.");
            return;
        }
        player.setAnimation(animation);
        player.lock(2);
        player.getInventory().deleteItem(4850, 1);
        if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER) && Utils.random(getChance(player)) == 0) {
            player.sendMessage(Colour.RS_GREEN.wrap("You find double the loot from the ogre coffin."));
            OgreLoot.get(player).forEach(item -> player.getInventory().addOrDrop(item));
        }
        OgreLoot.get(player).forEach(item -> player.getInventory().addOrDrop(item));
    }
    private int getChance(final Player player) {
        val memberRank = player.getMemberRank();
        if (memberRank.eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            return 3;
        } else if (memberRank.eligibleTo(MemberRank.ONYX_MEMBER)) {
            return 3;
        } else if (memberRank.eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 4;
        } else if (memberRank.eligibleTo(MemberRank.DIAMOND_MEMBER)) {
            return 6;
        } else if (memberRank.eligibleTo(MemberRank.RUBY_MEMBER)) {
            return 6;
        } else if (memberRank.eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 9;
        }
        return 9;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 6848, 6850 };
    }
}
