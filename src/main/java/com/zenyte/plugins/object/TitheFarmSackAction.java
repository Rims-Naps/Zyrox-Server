package com.zenyte.plugins.object;

import com.zenyte.game.content.minigame.tithefarm.TitheFarmSackOverlay;
import com.zenyte.game.content.minigame.tithefarm.TithePlantType;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.DoubleItemChat;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TitheFarmSackAction implements ObjectAction {

    private final static Animation PUT_ANIMATION = new Animation(832);

    @Override
    public final void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if(option.equals("Deposit")) {
            if(!player.getInventory().containsAnyOf(ItemId.GOLOVANOVA_FRUIT, ItemId.BOLOGANO_FRUIT, ItemId.LOGAVANO_FRUIT)) {
                player.getDialogueManager().start(new DoubleItemChat(player,
                        TithePlantType.GOLOVANOVA.getFruit(), TithePlantType.LOGAVANO.getFruit(), "You don't have any suitable fruit to deposit."));
                return;
            }

            TithePlantType type = null;
            if(player.getInventory().containsItem(ItemId.GOLOVANOVA_FRUIT)) {
                type = TithePlantType.GOLOVANOVA;
            } else if(player.getInventory().containsItem(ItemId.BOLOGANO_FRUIT)) {
                type = TithePlantType.BOLOGANO;
            } else if(player.getInventory().containsItem(ItemId.LOGAVANO_FRUIT)) {
                type = TithePlantType.LOGAVANO;
            }

            val endType = type;

            WorldTasksManager.schedule(new WorldTask() {

                private int ticks;

                @Override
                public void run() {
                    if(ticks == 0) {
                        player.lock();
                        final int amount = player.getInventory().getAmountOf(endType.getFruit().getId());
                        final int sackAmount = player.getNumericAttribute("tithe_farm_fruit").intValue();
                        final int points = player.getNumericAttribute("tithe_farm_points").intValue();
                        final int total = Math.min(sackAmount + amount, 100);

                        player.setAnimation(PUT_ANIMATION);
                        player.getInventory().deleteItem(endType.getFruit().getId(), amount);

                        double finalXp = 0;
                        val fruitXp = endType.getBaseXp() * 10;

                        // if the total amount of fruit is over 74, display points + total - 74 as points.
                        if(total > 74) {
                            // send a prompt if this is the first time during the session that the user got pts / extra xp
                            if(sackAmount <= 74) {
                                player.getDialogueManager().start(new ItemChat(player, endType.getFruit(), "You receive a bonus for harvesting fruit from at least 75% of the seeds."));

                                // add initial xp for "75 fruit bonus", which is 250 * baseXp, or 25 * fruitXp
                                finalXp += fruitXp * 25;
                            }

                            player.addAttribute("tithe_farm_points", points + (total-74));
                        }

                        // iterate over the amount of items we're adding, compared to the sack vs 74
                        for(int i=0; i <= amount; i++) {
                            val addAmt = fruitXp * ( (i + sackAmount <= 74) ? 1 : 2 );
                            finalXp += addAmt;
                        }

                        // add the final tally of xp
                        player.getSkills().addXp(Skills.FARMING, finalXp);

                        player.addAttribute("tithe_farm_fruit", total);
                        TitheFarmSackOverlay.process(player, endType);
                    } else if(ticks == 1) {
                        player.unlock();
                        stop();
                    }
                    ticks++;
                }

            }, 0, 1);
        }
    }

    @Override
    public final Object[] getObjects() {
        return new Object[] { 27431 };
    }
}
