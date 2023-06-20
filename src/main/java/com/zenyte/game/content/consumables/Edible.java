package com.zenyte.game.content.consumables;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.area.plugins.EdiblePlugin;
import lombok.val;

/**
 * @author Kris | 02/12/2018 17:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public interface Edible extends Consumable {

    Animation EAT_ANIM = new Animation(829);
    SoundEffect SOUND_EFFECT = new SoundEffect(2393);

    @Override
    default void consume(final Player player, final Item item, final int slotId) {
        val area = player.getArea();
        if ((area instanceof EdiblePlugin && !((EdiblePlugin) area).eat(player, this)) || !canConsume(player)) {
            return;
        }
        val name = item.getDefinitions().getName().toLowerCase();
        val inventory = player.getInventory();
        player.getPacketDispatcher().sendSoundEffect(SOUND_EFFECT);
        player.setAnimation(animation());
        val startMessage = startMessage();
        if (startMessage != null) {
            player.sendMessage(String.format(startMessage, name));
        }
        val endMessage = endMessage(player);
        if (endMessage != null) {
            player.sendMessage(endMessage);
        }
        val leftover = leftoverItem(item.getId());
        if (leftover != null) {
            inventory.set(slotId, leftover);
        } else {
            inventory.deleteItem(slotId, new Item(item.getId(), 1));
        }
        applyEffects(player);
    }

    @Override
    default void applyEffects(final Player player) {
        val delay = delay();
        if (delay > 0) {
            val actionManager = player.getActionManager();
            if (actionManager.getActionDelay() > 0 || actionManager.wasInCombatThisTick()) {
                actionManager.setActionDelay(actionManager.getActionDelay() + delay);
            }
            player.getVariables().setFoodDelay(delay);
        }
        onConsumption(player);
        heal(player);
        val boosts = boosts();
        if (boosts != null) {
            for (Boost boost : boosts) {
                boost.apply(player);
            }
        }
    }

    @Override
    default void heal(final Player player) {
        val heal = healedAmount(player);
        if (heal > 0) {
            player.heal(heal);
        }
    }

    @Override
    default Boost[] boosts() {
        return null;
    }

    @Override
    default int delay() {
        return 3;
    }

    @Override
    default String startMessage() {
        return "You eat the %s.";
    }

    @Override
    default String endMessage(Player player) {
        if (player.getHitpoints() >= player.getMaxHitpoints())
            return null;
        return "It heals some health.";
    }

    @Override
    default void onConsumption(Player player) {

    }

    @Override
    default boolean canConsume(Player player) {
        val variables = player.getVariables();
        return variables.getFoodDelay() <= 0 && variables.getPotionDelay() <= 0;
    }

}
