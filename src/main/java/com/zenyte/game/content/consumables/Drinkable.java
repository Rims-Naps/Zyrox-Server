package com.zenyte.game.content.consumables;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.GameSetting;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.region.area.plugins.DrinkablePlugin;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 02/12/2018 13:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public interface Drinkable extends Consumable {

    int[] overloadSkills = new int[]{Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGED};

    static Restoration[] getRestorations(final float percentage, final int amount, final int... skills) {
        val restorations = new Restoration[skills.length];
        for (int i = 0; i < skills.length; i++) {
            restorations[i] = new Restoration(skills[i], percentage, amount);
        }
        return restorations;
    }

    static int[] getAllSkillsExcluding(final int... exclude) {
        val skills = new int[Skills.SKILLS.length - exclude.length];
        int index = 0;
        for (int i = 0; i < Skills.SKILLS.length; i++) {
            if (ArrayUtils.contains(exclude, i)) continue;
            skills[index++] = i;
        }
        return skills;
    }

    static void applyOverload(final Player player) {
        val type = player.getVariables().getOverloadType();
        val boost = type == 1 ? 4 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F : type == 2 ? 0.13F : 0.16F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill : overloadSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetOverload(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : overloadSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(50);
        player.sendMessage("<col=ff0000>The effects of overload have worn off, and you feel normal again.");
    }

    int[] divinecombatSkills = new int[]{Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE,};

    static void applydivinecombat(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F : type == 2 ? 0.014F : 0.14F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill : divinecombatSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivinecombat(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinecombatSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    int[] divinebastionSkills = new int[]{Skills.RANGED, Skills.DEFENCE,};

    static void applydivinebastion(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 4 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F : type == 2 ? 0.008F : 0.08F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill : divinebastionSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivinebastion(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinebastionSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    int[] divinerangingSkills = new int[]{Skills.RANGED};

    static void applydivineranging(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F : type == 2 ? 0.008F : 0.08F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill : divinerangingSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivineranging(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinerangingSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    int[] divineattackSkills = new int[]{Skills.ATTACK};

    static void applydivineattack(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F :  type == 2 ? 0.014F : 0.14F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill : divineattackSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivineattack(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divineattackSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }
    int[] divinedefenceSkills = new int[]{Skills.DEFENCE};

    static void applydivinedefence(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F :  type == 2 ? 0.014F : 0.14F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill :divinedefenceSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivinedefence(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinedefenceSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    int[] divinemagicSkills = new int[]{Skills.MAGIC};

    static void applydivinemagic(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F :  type == 2 ? 0.001F : 0.01F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill :divinemagicSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivinemagic(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinemagicSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    int[] divinestrengthSkills = new int[]{Skills.STRENGTH};

    static void applydivinestrength(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F :  type == 2 ? 0.014F : 0.14F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill :divinestrengthSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivinestrength(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinestrengthSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    int[] divinebattlemageSkills = new int[]{Skills.MAGIC, Skills.DEFENCE};

    static void applydivinebattlemage(final Player player) {
        val type = player.getVariables().getDivineType();
        val boost = type == 1 ? 2 : type == 2 ? 5 : 6;
        val modifier = type == 1 ? 0.1F : type == 2 ? 0.030F : 0.03F;
        val skills = player.getSkills();
        int currentLevel, realLevel;
        for (val skill : divinebattlemageSkills) {
            currentLevel = skills.getLevel(skill);
            realLevel = skills.getLevelForXp(skill);
            skills.setLevel(skill, (int) (Math.min(currentLevel, realLevel) + boost + (realLevel * modifier)));
        }
    }

    static void resetdivinebattlemage(final Player player) {
        if (player.isDead() || player.isFinished() || !player.isRunning())
            return;
        val skills = player.getSkills();
        for (val skill : divinebattlemageSkills) {
            skills.setLevel(skill, skills.getLevelForXp(skill));
        }
        player.heal(10);
        player.sendMessage("<col=ff0000>The effects of divine potion have worn off, and you feel normal again.");
    }

    String emptyMessage(Player player);
    int getDoses(int id);
    Item getItem(final int dose);
    int[] getIds();

    @Override
    default int healedAmount(Player player) {
        return 0;
    }

    @Override
    default void heal(final Player player) {
        val heal = healedAmount(player);
        if (heal > 0) {
            player.heal(heal);
        }
    }

    @Override
    default int delay() {
        return 3;
    }

    @Override
    default void onConsumption(Player player) {

    }

    @Override
    default Animation animation() {
        return anim;
    }

    @Override
    default boolean canConsume(Player player) {
        return player.getVariables().getPotionDelay() <= 0;
    }

    Animation anim = new Animation(829);

    Item vial = new Item(229);
    Item cocktailGlass = new Item(2026);
    Item jug = new Item(1935);
    Item beerGlass = new Item(1919);

    SoundEffect sound = new SoundEffect(2401);

    @Override
    default void consume(final Player player, final Item item, final int slotId) {
        val area = player.getArea();
        if ((area instanceof DrinkablePlugin && !((DrinkablePlugin) area).drink(player, this)) || !canConsume(player)) {
            return;
        }
        val name = item.getDefinitions().getName().toLowerCase().replaceAll("[([1-4])]", "").replaceAll(" potion", "");
        val inventory = player.getInventory();
        player.setUnprioritizedAnimation(animation());
        player.getPacketDispatcher().sendSoundEffect(sound);
        val id = item.getId();
        val startMessage = startMessage();
        if (startMessage != null) {
            player.sendFilteredMessage(String.format(startMessage, name + " potion"));
        }
        val endMessage = endMessage(player);
        if (endMessage != null) {
            val doses = getDoses(id) - 1;
            player.sendFilteredMessage(String.format(doses == 0 ? emptyMessage(player) : endMessage, doses, doses == 1 ? "" : "s"));
        }
        val leftover = leftoverItem(item.getId());
        if (leftover != null) {
            if (leftover.getId() == 229 && player.getNumericAttribute(GameSetting.SMASH_VIALS.toString()).intValue() == 1) {
                player.sendFilteredMessage("You quickly smash the empty vial using the trick a Barbarian taught you.");
                inventory.deleteItem(slotId, new Item(id, 1));
            } else {
                inventory.set(slotId, leftover);
            }
        } else {
            inventory.deleteItem(slotId, new Item(id, 1));
        }
        applyEffects(player);
    }

    @Override
    default void applyEffects(final Player player) {
        val delay = delay();
        if (delay > 0) {
            player.getVariables().setPotionDelay(delay);
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

}
