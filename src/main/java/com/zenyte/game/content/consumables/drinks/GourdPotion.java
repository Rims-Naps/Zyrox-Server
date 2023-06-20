package com.zenyte.game.content.consumables.drinks;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.map.RaidArea;
import com.zenyte.game.content.consumables.Drinkable;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.region.area.plugins.DrinkablePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 02/12/2018 16:43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum GourdPotion implements Drinkable {

    ELDER_MINUS(new int[] { 20913, 20914, 20915, 20916 }, new Boost(Skills.ATTACK, 0.1F, 4),
            new Boost(Skills.STRENGTH, 0.1F, 4), new Boost(Skills.DEFENCE, 0.1F, 4)),
    ELDER(new int[] { 20917, 20918, 20919, 20920 }, new Boost(Skills.ATTACK, 0.13F, 5),
            new Boost(Skills.STRENGTH, 0.13F, 5), new Boost(Skills.DEFENCE, 0.13F, 5)),
    ELDER_PLUS(new int[] { 20921, 20922, 20923, 20924 }, new Boost(Skills.ATTACK, 0.16F, 6),
            new Boost(Skills.STRENGTH, 0.16F, 6), new Boost(Skills.DEFENCE, 0.16F, 6)),
    TWISTED_MINUS(new int[] { 20925, 20926, 20927, 20928 }, new Boost(Skills.RANGED, 0.1F, 4)),
    TWISTED(new int[] { 20929, 20930, 20931, 20932 }, new Boost(Skills.RANGED, 0.13F, 5)),
    TWISTED_PLUS(new int[] { 20933, 20934, 20935, 20936 }, new Boost(Skills.RANGED, 0.16F, 6)),
    KODAI_MINUS(new int[] { 20937, 20938, 20939, 20940 }, new Boost(Skills.MAGIC, 0.1F, 4)),
    KODAI(new int[] { 20941, 20942, 20943, 20944 }, new Boost(Skills.MAGIC, 0.13F, 5)),
    KODAI_PLUS(new int[] { 20945, 20946, 20947, 20948 }, new Boost(Skills.MAGIC, 0.16F, 6)),
    REVITALISATION_MINUS(new int[] { 20949, 20950, 20951, 20952 }, Drinkable.getRestorations(0.2F, 6,
            Drinkable.getAllSkillsExcluding(Skills.HITPOINTS))),
    REVITALISATION(new int[] { 20953, 20954, 20955, 20956 }, Drinkable.getRestorations(0.25F, 8,
            Drinkable.getAllSkillsExcluding(Skills.HITPOINTS))),
    REVITALISATION_PLUS(new int[] { 20957, 20958, 20959, 20960 }, Drinkable.getRestorations(0.3F, 11,
            Drinkable.getAllSkillsExcluding(Skills.HITPOINTS))),
    PRAYER_ENHANCE_MINUS(new int[] { 20961, 20962, 20963, 20964 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(268, TickVariable.PRAYER_ENHANCE);
        }
    },
    PRAYER_ENHANCE(new int[] { 20965, 20966, 20967, 20968 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(375, TickVariable.PRAYER_ENHANCE);
        }
    },
    PRAYER_ENHANCE_PLUS(new int[] { 20969, 20970, 20971, 20972 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(483, TickVariable.PRAYER_ENHANCE);
        }
    },
    XERICS_AID_MINUS(new int[] { 20973, 20974, 20975, 20976 }, new Boost(Skills.DEFENCE, 0.1F, 5),
            new Debuff(Skills.ATTACK, 0.05F, 2), new Debuff(Skills.STRENGTH, 0.05F, 2), new Debuff(Skills.MAGIC,
            0.05F, 2), new Debuff(Skills.RANGED, 0.05F, 2), new Boost(Skills.HITPOINTS, 0.1F, 0)) {
        @Override
        public String startMessage() {
            return "You drink some of your weak Xeric's aid potion.";
        }
    },
    XERICS_AID(new int[] { 20977, 20978, 20979, 20980 }, new Boost(Skills.DEFENCE, 0.15F, 5),
            new Debuff(Skills.ATTACK, 0.075F, 2), new Debuff(Skills.STRENGTH, 0.075F, 2), new Debuff(Skills.MAGIC,
            0.075F, 2), new Debuff(Skills.RANGED, 0.075F, 2), new Boost(Skills.HITPOINTS, 0.15F, 0)) {
        @Override
        public String startMessage() {
            return "You drink some of your Xeric's aid potion.";
        }
    },
    XERICS_AID_PLUS(new int[] { 20981, 20982, 20983, 20984 }, new Boost(Skills.DEFENCE, 0.2F, 5),
            new Debuff(Skills.ATTACK, 0.1F, 2), new Debuff(Skills.STRENGTH, 0.1F, 2), new Debuff(Skills.MAGIC,
            0.1F, 2), new Debuff(Skills.RANGED, 0.1F, 2), new Boost(Skills.HITPOINTS, 0.2F, 0)) {
        @Override
        public String startMessage() {
            return "You drink some of your strong Xeric's aid potion.";
        }
    },
    OVERLOAD_MINUS(new int[] { 20985, 20986, 20987, 20988 }) {
        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final boolean previousOverload = player.getTemporaryAttributes().containsKey(overloadKey);
            if (previousOverload) {
                player.sendMessage("You are still suffering the effects of a fresh dose of overload.");
                return false;
            }
            if (player.getHitpoints() < 50) {
                player.sendMessage("You need at least 50 hitpoints to survive the overload.");
                return false;
            }
            return super.canConsume(player);
        }

        @Override
        public void onConsumption(final Player player) {
            player.getTemporaryAttributes().put(overloadKey, true);
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 5) {
                        stop();
                        player.getTemporaryAttributes().remove(overloadKey);
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.OVERLOAD);
            player.getVariables().setOverloadType((short) 1);
            Drinkable.applyOverload(player);
        }
    },
    OVERLOAD(new int[] { 20989, 20990, 20991, 20992 }) {
        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final boolean previousOverload = player.getTemporaryAttributes().containsKey(overloadKey);
            if (previousOverload) {
                player.sendMessage("You are still suffering the effects of a fresh dose of overload.");
                return false;
            }
            if (player.getHitpoints() < 50) {
                player.sendMessage("You need at least 50 hitpoints to survive the overload.");
                return false;
            }
            return super.canConsume(player);
        }

        @Override
        public void onConsumption(final Player player) {
            player.getTemporaryAttributes().put(overloadKey, true);
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 5) {
                        stop();
                        player.getTemporaryAttributes().remove(overloadKey);
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.OVERLOAD);
            player.getVariables().setOverloadType((short) 2);
            Drinkable.applyOverload(player);
        }
    },
    OVERLOAD_PLUS(new int[] { 20993, 20994, 20995, 20996 }) {
        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final boolean previousOverload = player.getTemporaryAttributes().containsKey(overloadKey);
            if (previousOverload) {
                player.sendMessage("You are still suffering the effects of a fresh dose of overload.");
                return false;
            }
            if (player.getHitpoints() < 50) {
                player.sendMessage("You need at least 50 hitpoints to survive the overload.");
                return false;
            }
            return super.canConsume(player);
        }

        @Override
        public void onConsumption(final Player player) {
            player.getTemporaryAttributes().put(overloadKey, true);

            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 5) {
                        stop();
                        player.getTemporaryAttributes().remove(overloadKey);
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.OVERLOAD);
            player.getVariables().setOverloadType((short) 3);
            Drinkable.applyOverload(player);
        }
    };
    GourdPotion(final int[] ids, final Boost... boosts) {
        this.ids = ids;
        this.boosts = boosts;
        val name = name();
        this.type = name.endsWith("MINUS") ? GourdType.WEAK : name.endsWith("PLUS") ? GourdType.STRONG : GourdType.MEDIUM;
    }

    /**
     * The ids of the potion. These go in ascending order, so effectively {@code { 1dose, 2doses, 3doses, 4doses }}.
     */
    @Getter
    private final int[] ids;
    private final Boost[] boosts;
    @Getter private final GourdType type;
    public static final GourdPotion[] values = values();

    private static final String overloadKey = "overload end timer";

    @Override
    public Item leftoverItem(int id) {
        val index = ArrayUtils.indexOf(ids, id);
        if (index == -1) {
            throw new RuntimeException("Invalid id: " + id + " " + this);
        }
        if (index == 0) {
            return null;
        }
        return new Item(ids[index - 1]);
    }

    @Override
    public Item getItem(final int doses) {
        if (doses < 0 || doses > ids.length)
            throw new RuntimeException("The potion " + this + " doesn't support dose " + doses + ".");
        if (doses == 0) {
            return null;
        }
        return new Item(ids[doses - 1]);
    }

    @Override
    public Boost[] boosts() {
        return boosts;
    }

    @Override
    public String startMessage() {
        return "You drink some of your %s %s.";
    }

    @Override
    public String endMessage(Player player) {
        return "You have %d dose%s left.";
    }

    @Override
    public int getDoses(int id) {
        val index = ArrayUtils.indexOf(ids, id);
        if (index == -1) {
            throw new RuntimeException("Invalid id: " + id + " " + this);
        }
        return index + 1;
    }

    @Override
    public String emptyMessage(final Player player) {
        return "You have finished your potion. The gourd vial disintegrates.";
    }

    @Override
    public boolean canConsume(final Player player) {
        if (!(player.getArea() instanceof RaidArea)) {
            player.sendMessage("You can only sip this potion while inside the Chambers of Xeric.");
            return false;
        }
        return player.getVariables().getPotionDelay() <= 0;
    }

    @Override
    public void consume(final Player player, final Item item, final int slotId) {
        val area = player.getArea();
        if ((area instanceof DrinkablePlugin && !((DrinkablePlugin) area).drink(player, this)) || !canConsume(player)) {
            return;
        }
        val name = item.getDefinitions().getName().toLowerCase();
        val inventory = player.getInventory();
        player.setUnprioritizedAnimation(animation());
        player.getPacketDispatcher().sendSoundEffect(sound);
        val id = item.getId();
        val startMessage = startMessage();
        player.sendFilteredMessage(String.format(startMessage, name.contains("(+)") ? "strong" : name.contains("(-)") ? "weak" : "",
                name.replaceAll("[([1-4])]", "").replaceAll("potion", "") + "potion")
                .replaceAll("[([+][-])]", "").replaceAll(" {2}", " "));
        val endMessage = endMessage(player);
        val doses = getDoses(id) - 1;
        player.sendFilteredMessage(String.format(doses == 0 ? emptyMessage(player) : endMessage, doses, doses == 1 ? "" : "s"));
        val leftover = leftoverItem(item.getId());
        if (leftover != null) {
            inventory.set(slotId, leftover);
        } else {
            inventory.deleteItem(slotId, new Item(id, 1));
        }
        applyEffects(player);
        Raid.applyPotionEffect(player, this, item);
    }

    @Override
    public void applyEffects(final Player player) {
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

    @Getter
    @AllArgsConstructor
    public enum GourdType {
        WEAK(0.25F),
        MEDIUM(0.5F),
        STRONG(1F);

        private final float multiplier;
    }

}
