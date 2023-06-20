package com.zenyte.game.content.crystalchest;

import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 04/04/2019 13:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CrystalKeyChest implements ObjectAction {

    private static final Animation animation = new Animation(832);
    private static final Location taverly_chest = new Location(2914, 3452, 0);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!player.getInventory().containsItem(989, 1)) {
            player.sendMessage("This chest is securely locked shut.");
            return;
        }
        if (object.getPositionHash() == taverly_chest.getPositionHash()) {
            player.getAchievementDiaries().update(FaladorDiary.UNLOCK_CRYSTAL_CHEST);
        }
        player.setAnimation(animation);
        player.lock(2);
        player.getInventory().deleteItem(989, 1);
        if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER) && Utils.random(getChance(player)) == 0) {
            player.sendMessage(Colour.RS_GREEN.wrap("You find double the loot from the crystal chest."));
            CrystalLoot.get(player).forEach(item -> player.getInventory().addOrDrop(item));
        }
        CrystalLoot.get(player).forEach(item -> player.getInventory().addOrDrop(item));
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
        return new Object[] { 172 };
    }
}
