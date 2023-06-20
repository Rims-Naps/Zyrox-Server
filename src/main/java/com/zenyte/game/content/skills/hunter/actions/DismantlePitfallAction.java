package com.zenyte.game.content.skills.hunter.actions;

import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.KaramjaDiary;
import com.zenyte.game.content.achievementdiary.diaries.WesternProvincesDiary;
import com.zenyte.game.content.skills.hunter.TrapState;
import com.zenyte.game.content.skills.hunter.node.TrapPrey;
import com.zenyte.game.content.skills.hunter.object.HunterTrap;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 30/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class DismantlePitfallAction extends BuiltHunterTrapAction {

    /**
     * The animation played by the {@link com.zenyte.game.world.entity.player.Player} when dismantling this pitfall trap. If the trap has prey in it, the player is rewarded correspondingly.
     */
    private static final Animation animation = new Animation(5212);

    private final WorldObject object;
    private final HunterTrap trap;

    @Override
    public boolean start() {
        if (!preconditions(player, true)) {
            return false;
        }
        player.setAnimation(animation);
        player.lock(3);
        delay(2);
        return true;
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public int processWithDelay() {
        val state = trap.getState();
        if (state == TrapState.PROCESSING || state == TrapState.REMOVED) {
            return -1;
        }
        val prey = trap.getPrey();
        player.sendFilteredMessage("You dismantle the trap.");
        player.sendSound(trap.getType().getTakeSound());
        if (prey != null) {
            val items = new ObjectArrayList<>(prey.getItems());
            //The items list contains both regular and tatted fur. Let's remove one of them.
            items.remove(Utils.random(1, 2));
            val inventory = player.getInventory();
            items.forEach(it -> inventory.addOrDrop(new Item(it.getId(), Utils.random(it.getMinAmount(), it.getMaxAmount()))));
            player.getSkills().addXp(Skills.HUNTER, prey.getExperience());
            player.sendFilteredMessage("You've caught " + Utils.getAOrAn(prey.toString()) + " " + prey + "!");
            checkDiaries(prey);
        }
        player.getVarManager().sendBit(object.getDefinitions().getVarbit(), 0);
        trap.remove();
        return -1;
    }

    /**
     * Checks the diary stages to see if the player has progressed any of them when successfully catching a prey.
     *
     * @param prey the prey whom the player caught.
     */
    private void checkDiaries(@NotNull final TrapPrey prey) {
        if (prey.equals(TrapPrey.HORNED_GRAAHK)) {
            player.getAchievementDiaries().update(KaramjaDiary.TRAP_A_HORNED_GRAAHK);
        } else if (prey.equals(TrapPrey.SABRE_TOOTHED_KYATT)) {
            player.getAchievementDiaries().update(FremennikDiary.CATCH_SABRE_TOOTHED_KYATT);
        } else if (prey.equals(TrapPrey.SPINED_LARUPIA)) {
            player.getAchievementDiaries().update(WesternProvincesDiary.TRAP_A_SPINED_LARUPIA);
        }
    }

    @Override
    protected HunterTrap getTrap() {
        return trap;
    }
}
