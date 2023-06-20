package com.zenyte.game.content.consumables.drinks;

import com.zenyte.game.content.consumables.Drinkable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.plugins.item.RingOfEndurance;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 02/12/2018 13:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum Potion implements Drinkable {

    STRENGTH_POTION(new int[] { 119, 117, 115, 113 }, new Boost(Skills.STRENGTH, 0.1F, 3)),
    ATTACK_POTION(new int[] { 125, 123, 121, 2428 }, new Boost(Skills.ATTACK, 0.1F, 3)),
    DEFENCE_POTION(new int[] { 137, 135, 133, 2432 }, new Boost(Skills.DEFENCE, 0.1F, 3)),
    SUPER_ATTACK_POTION(new int[] { 149, 147, 145, 2436 }, new Boost(Skills.ATTACK, 0.15F, 5)),
    FISHING_POTION(new int[] { 155, 153, 151, 2438 }, new Boost(Skills.FISHING, 0, 3)),
    SUPER_STRENGTH_POTION(new int[] { 161, 159, 157, 2440 }, new Boost(Skills.STRENGTH, 0.15F, 5)),
    SUPER_DEFENCE_POTION(new int[] { 167, 165, 163, 2442 }, new Boost(Skills.DEFENCE, 0.15F, 5)),
    RANGING_POTION(new int[] { 173, 171, 169, 2444 }, new Boost(Skills.RANGED, 0.1F, 4)),
    ZAMORAK_BREW(new int[] { 193, 191, 189, 2450 }, new Boost(Skills.ATTACK, 0.2F, 2), new Boost(Skills.STRENGTH,
            0.12F, 2), new Debuff(Skills.DEFENCE, 0.1F, 2), new Restoration(Skills.PRAYER, 0.1F, 0)) {
        @Override
        public String startMessage() {
            return "You drink some of the foul liquid.";
        }
        @Override
        public void onConsumption(final Player player) {
            player.applyHit(new Hit((int) Math.floor(player.getHitpoints() * 0.12F), HitType.REGULAR));
            if (!player.getBooleanAttribute("hard-combat-achievement43")) {
                player.getAttributes().put("restored_prayer_via_pot_at_chaosfanatic_kc", player.getNotificationSettings().getKillcount("chaos fanatic"));
            }
            if (player.inArea("Fight caves")) {
                player.putBooleanAttribute("drainedPrayerInFightCaves", true);
            }
        }
    },
    AGILITY_POTION(new int[] { 3038, 3036, 3034, 3032 }, new Boost(Skills.AGILITY, 0, 3)),
    MAGIC_POTION(new int[] { 3046, 3044, 3042, 3040 }, new Boost(Skills.MAGIC, 0, 4)),
    SARADOMIN_BREW(new int[] { 6691, 6689, 6687, 6685 }, new Boost(Skills.HITPOINTS, 0.15F, 2),
            new Boost(Skills.DEFENCE, 0.2F, 2), new Debuff(Skills.STRENGTH, 0.1F, 2), new Debuff(Skills.ATTACK, 0.1F,
            2), new Debuff(Skills.MAGIC, 0.1F, 2), new Debuff(Skills.RANGED, 0.1F, 2)) {
        @Override
        public String startMessage() {
            return "You drink some of the foul liquid.";
        }
    },
    MAGIC_ESSENCE(new int[] { 9024, 9023, 9022, 9021 }, new Boost(Skills.MAGIC, 0, 3)),
    COMBAT_POTION(new int[] { 9745, 9743, 9741, 9739 }, new Boost(Skills.ATTACK, 0.1F, 3), new Boost(Skills.STRENGTH,
            0.1F, 3)),
    HUNTER_POTION(new int[] { 10004, 10002, 10000, 9998 }, new Boost(Skills.HUNTER, 0, 3)),
    SUPER_RANGING(new int[] { 11725, 11724, 11723, 11722 }, new Boost(Skills.RANGED, 0.15F, 5)) {
        @Override
        public boolean canConsume(final Player player) {
            if (!player.inArea("Nightmare zone")) {
                player.sendMessage("You can only sip an overload while in the Nightmare zone.");
                return false;
            }
            return true;
        }
    },
    SUPER_MAGIC_POTION(new int[] { 11729, 11728, 11727, 11726 }, new Boost(Skills.MAGIC, 0.15F, 5)) {
        @Override
        public boolean canConsume(final Player player) {
            if (!player.inArea("Nightmare zone")) {
                player.sendMessage("You can only sip an overload while in the Nightmare zone.");
                return false;
            }
            return true;
        }
    },
    SUPER_COMBAT_POTION(new int[] { 12701, 12699, 12697, 12695 }, new Boost(Skills.ATTACK, 0.15F, 5),
            new Boost(Skills.STRENGTH, 0.15F, 5), new Boost(Skills.DEFENCE, 0.15F, 5)),
    DIVINE_COMBAT_POTION(new int[] { 30629, 30626, 30620, 30623 }, new Boost(Skills.ATTACK, 0.15F, 5),
            new Boost(Skills.STRENGTH, 0.15F, 5), new Boost(Skills.DEFENCE, 0.15F, 5)),
    ALT_PRAYER_POTION(new int[] { 20396, 20395, 20394, 20393 }, new Restoration(Skills.PRAYER, 0.25F, 7))
            {
                @Override
                public void onConsumption(final Player player) {
                    if (!player.getBooleanAttribute("hard-combat-achievement43")) {
                        player.getAttributes().put("restored_prayer_via_pot_at_chaosfanatic_kc", player.getNotificationSettings().getKillcount("chaos fanatic"));
                    }
                    if (player.inArea("Fight caves")) {
                        player.putBooleanAttribute("drainedPrayerInFightCaves", true);
                    }
                }
            }
    ,
    BATTLEMAGE_POTION(new int[] { 22458, 22455, 22452, 22449 }, new Boost(Skills.MAGIC, 0, 4),
            new Boost(Skills.DEFENCE, 0.15F, 5)) {
        @Override
        public String startMessage() {
            return "You drink some of your %s. It's a little tangy.";
        }
    },
    BASTION_POTION(new int[] { 22470, 22467, 22464, 22461 }, new Boost(Skills.RANGED, 0.1F, 4),
            new Boost(Skills.DEFENCE, 0.15F, 5)) {
        @Override
        public String startMessage() {
            return "You drink some of your %s. It's a little tangy.";
        }
    },
    PRAYER_POTION(new int[] { 143, 141, 139, 2434 }, new Restoration(Skills.PRAYER, 0.25F, 7))
            {
                @Override
                public void onConsumption(final Player player) {
                    if (!player.getBooleanAttribute("hard-combat-achievement43")) {
                        player.getAttributes().put("restored_prayer_via_pot_at_chaosfanatic_kc", player.getNotificationSettings().getKillcount("chaos fanatic"));
                    }
                    if (player.inArea("Fight caves")) {
                        player.putBooleanAttribute("drainedPrayerInFightCaves", true);
                    }
                }
            }
    ,
    ANTIPOISON(new int[] { 179, 177, 175, 2446 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().cureToxin(Toxins.ToxinType.POISON);
            player.getVariables().schedule(150, TickVariable.POISON_IMMUNITY);
        }
    },
    SUPERANTIPOISON(new int[] { 185, 183, 181, 2448 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().cureToxin(Toxins.ToxinType.POISON);
            player.getVariables().schedule(600, TickVariable.POISON_IMMUNITY);
        }
    },
    ANTIFIRE_POTION(new int[] { 2458, 2456, 2454, 2452 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(600, TickVariable.ANTIFIRE);
        }
    },
    ENERGY_POTION(new int[] { 3014, 3012, 3010, 3008 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 10);
        }
    },
    SUPER_ENERGY_POTION(new int[] { 3022, 3020, 3018, 3016 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 20);
        }
    },
    RELICYMS_BALM(new int[] { 4848, 4846, 4844, 4842 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().weakenDisease();
        }
    },
    ANTIDOTE_PLUS(new int[] { 5949, 5947, 5945, 5943 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().cureToxin(Toxins.ToxinType.POISON);
            player.getVariables().schedule(900, TickVariable.POISON_IMMUNITY);
        }
    },
    ANTIDOTE_PLUS_PLUS(new int[] { 5958, 5956, 5954, 5952 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().cureToxin(Toxins.ToxinType.POISON);
            player.getToxins().cureToxin(Toxins.ToxinType.VENOM);
            player.getVariables().schedule(1200, TickVariable.POISON_IMMUNITY);
        }
    },
    EXTENDED_ANTIFIRE(new int[] { 11957, 11955, 11953, 11951 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(1200, TickVariable.ANTIFIRE);
        }
    },
    STAMINA_POTION(new int[] { 12631, 12629, 12627, 12625 }) {
        @Override
        public void onConsumption(final Player player) {
            if (player.getEquipment().getId(EquipmentSlot.RING) == ItemId.RING_OF_ENDURANCE) {
                int charges = player.getEquipment().getItem(EquipmentSlot.RING).getCharges();
                if (charges > 0) {
                    player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 40);
                    player.getVariables().schedule(400, TickVariable.STAMINA_ENHANCEMENT);
                    player.getVarManager().sendBit(25, 1);
                    player.getChargesManager().removeCharges(player.getRing(), 1, player.getEquipment().getContainer(), EquipmentSlot.RING.getSlot());
                }
                else {
                    player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 20);
                    player.getVariables().schedule(200, TickVariable.STAMINA_ENHANCEMENT);
                    player.getVarManager().sendBit(25, 1);
                }
            } else {
                player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 20);
                player.getVariables().schedule(200, TickVariable.STAMINA_ENHANCEMENT);
                player.getVarManager().sendBit(25, 1);
            }
        }
    },
    ANTI_VENOM(new int[] { 12911, 12909, 12907, 12905 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().resetVenom();
            player.getVariables().schedule(1200, TickVariable.POISON_IMMUNITY);
            player.getVariables().schedule(100, TickVariable.VENOM_IMMUNITY);
        }
    },
    ANTI_VENOM_PLUS(new int[] { 12919, 12917, 12915, 12913 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().resetVenom();
            player.getVariables().schedule(1500, TickVariable.POISON_IMMUNITY);
            player.getVariables().schedule(300, TickVariable.VENOM_IMMUNITY);
        }
    },
    ALT_SUPER_ENERGY_POTION(new int[] { 20551, 20550, 20549, 20548 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 20);
        }
    },
    SUPER_ANTIFIRE_POTION(new int[] { 21987, 21984, 21981, 21978 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(300, TickVariable.SUPER_ANTIFIRE);
        }
    },
    EXTENDED_SUPER_ANTIFIRE(new int[] { 22218, 22215, 22212, 22209 }) {
        @Override
        public void onConsumption(final Player player) {
            player.getVariables().schedule(600, TickVariable.SUPER_ANTIFIRE);
        }
    },
    RESTORE_POTION(new int[] { 131, 129, 127, 2430 }, Drinkable.getRestorations(0.3F, 10, Skills.DEFENCE,
            Skills.ATTACK, Skills.STRENGTH, Skills.MAGIC, Skills.RANGED)),
    SUPER_RESTORE_POTION(new int[] { 3030, 3028, 3026, 3024 }, Drinkable.getRestorations(0.25F, 8,
            Drinkable.getAllSkillsExcluding(Skills.HITPOINTS)))
            {
                @Override
                public void onConsumption(final Player player) {
                    if (!player.getBooleanAttribute("hard-combat-achievement43")) {
                        player.getAttributes().put("restored_prayer_via_pot_at_chaosfanatic_kc", player.getNotificationSettings().getKillcount("chaos fanatic"));
                    }
                    if (player.inArea("Fight caves")) {
                        player.putBooleanAttribute("drainedPrayerInFightCaves", true);
                    }
                }
            }
    ,
    SANFEW_SERUM(new int[] { 10931, 10929, 10927, 10925 }, Drinkable.getRestorations(0.3F, 4,
            Drinkable.getAllSkillsExcluding(Skills.HITPOINTS))) {
        @Override
        public void onConsumption(final Player player) {
            player.getToxins().cureToxin(Toxins.ToxinType.POISON);
            player.getToxins().cureToxin(Toxins.ToxinType.DISEASE);
            player.getVariables().schedule(600, TickVariable.POISON_IMMUNITY);
            if (!player.getBooleanAttribute("hard-combat-achievement43")) {
                player.getAttributes().put("restored_prayer_via_pot_at_chaosfanatic_kc", player.getNotificationSettings().getKillcount("chaos fanatic"));
            }
            if (player.inArea("Fight caves")) {
                player.putBooleanAttribute("drainedPrayerInFightCaves", true);
            }
        }

        @Override
        public String startMessage() {
            return "You drink some of your Sanfew serum.";
        }

    },
    OVERLOAD(new int[] { 11733, 11732, 11731, 11730 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int overload = player.getVariables().getTime(TickVariable.OVERLOAD);
            if (overload > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of overload.");
                return false;
            }
            if (player.getHitpoints() < 50) {
                player.sendMessage("You need at least 50 hitpoints to survive the overload.");
                return false;
            }
            if (!player.inArea("Nightmare zone")) {
                player.sendMessage("You can only sip an overload while in the Nightmare zone.");
                return false;
            }
            return true;
        }
    },
    DIVINECOMBAT(new int[] { 30629, 30626, 30623, 30620 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINECOMBAT);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

    },
    DIVINEBASTION(new int[] { 30701, 30698, 30695, 30692 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINEBASTION);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINEBASTION);
            player.getVariables().setDivinebastionType((short) 3);
            Drinkable.applydivinebastion(player);
        }

    },
    DIVINERANGED(new int[] { 30677, 30674, 30671, 30668 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINERANGE);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINERANGE);
            player.getVariables().setDivinerangedType((short) 3);
            Drinkable.applydivineranging(player);
        }

    },
    DIVINEATTACK(new int[] { 30642, 30639, 30636, 30633 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINEATTACK);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINEATTACK);
            player.getVariables().setDivineattackType((short) 3);
            Drinkable.applydivineattack(player);
        }

    },
    DIVINESTRENGTH(new int[] { 32009,32006, 32003, 32000 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINESTRENGTH);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINESTRENGTH);
            player.getVariables().setDivineattackType((short) 3);
            Drinkable.applydivinestrength(player);
        }

    },
    DIVINEDEFENCE(new int[] { 30665, 30662, 30660, 30657 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINEDEFENCE);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINEDEFENCE);
            player.getVariables().setDivineattackType((short) 3);
            Drinkable.applydivinedefence(player);
        }

    },
    DIVINEMAGIC(new int[] { 30689, 30686, 30683, 30680 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINEMAGIC);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINEMAGIC);
            player.getVariables().setDivinemagicType((short) 3);
            Drinkable.applydivinemagic(player);
        }

    },
    DIVINEBATTLEMAGE(new int[] { 30713, 30710, 30707, 30704 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINEBATTLEMAGE);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINEBATTLEMAGE);
            player.getVariables().setDivinebattlemageType((short) 3);
            Drinkable.applydivinebattlemage(player);
        }

    },
    DIVINECOMBATPOTION(new int[] { 30629, 30626, 30623, 30620 }) {

        private final Animation animation = new Animation(3170);
        private final Graphics graphics = new Graphics(560);

        @Override
        public boolean canConsume(final Player player) {
            final int divine = player.getVariables().getTime(TickVariable.DIVINECOMBAT);
            if (divine > 0) {
                player.sendMessage("You are still suffering the effects of a fresh dose of divine potion.");
                return false;
            }
            if (player.getHitpoints() < 10) {
                player.sendMessage("You need at least 10 hitpoints to survive the divine potion.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(final Player player) {
            WorldTasksManager.schedule(new WorldTask() {
                private int count;
                @Override
                public void run() {
                    if (player.isDead() || player.isFinished() || count++ == 1) {
                        stop();
                        return;
                    }
                    player.setAnimation(animation);
                    player.setGraphics(graphics);
                    player.applyHit(new Hit(10, HitType.REGULAR));
                }
            }, 0, 1);
            player.getVariables().schedule(500, TickVariable.DIVINECOMBAT);
            player.getVariables().setDivinecombatType((short) 3);
            Drinkable.applydivinecombat(player);
        }

    },
    GUTHIX_REST(new int[] {ItemId.GUTHIX_REST1, ItemId.GUTHIX_REST2, ItemId.GUTHIX_REST3, ItemId.GUTHIX_REST4}, new Boost(Skills.HITPOINTS, 0, 5)) {

        @Override
        public String startMessage() {
            return "You drink the herbal tea.";
        }

        @Override
        public String endMessage(Player player) {
            return "The potion restores some energy.";
        }

        @Override
        public String emptyMessage(final Player player) {
            return "You have finished your potion.";
        }

        public Item vial() {
            return new Item(ItemId.EMPTY_CUP);
        }

        @Override
        public void onConsumption(final Player player) {
            if (player.getHitpoints() < (player.getMaxHitpoints() + 5)) {
                player.sendMessage("The tea boosts your hitpoints.");
            }
            player.getVariables().setRunEnergy(player.getVariables().getRunEnergy() + 5);
            if (player.getToxins().isVenomed()) {
                player.getToxins().cureToxin(Toxins.ToxinType.VENOM);
            } else if (player.getToxins().isPoisoned()) {
                player.getToxins().setDamage(player.getToxins().getDamage() - 1);
                if (player.getToxins().getDamage() <= 0) {
                    player.getToxins().cureToxin(Toxins.ToxinType.POISON);
                }
            }
        }
    },
    ABSORPTION(new int[] { 11737, 11736, 11735, 11734 }) {

        @Override
        public boolean canConsume(final Player player) {
            if (!player.inArea("Nightmare zone")) {
                player.sendMessage("You can only sip the potion while in the Nightmare zone.");
                return false;
            }
            return true;
        }

        @Override
        public void onConsumption(Player player) {
            player.getVariables().setAbsorption(Math.min(1000, player.getVariables().getAbsorption() + 50));
        }
    };

    Potion(final int[] ids, final Boost... boosts) {
        this.ids = ids;
        this.boosts = boosts;
    }

    private static final Item vial = new Item(229);

    /**
     * The ids of the potion. These go in ascending order, so effectively {@code { 1dose, 2doses, 3doses, 4doses }}.
     */
    @Getter private final int[] ids;
    private final Boost[] boosts;

    public static final Potion[] values = values();
    public static final Int2ObjectMap<Potion> POTIONS = new Int2ObjectOpenHashMap<>();

    static {
        for (val potion : values) {
            for (val id : potion.getIds()) {
                val noted = ItemDefinitions.getOrThrow(id).getNotedId();
                if (noted != -1) {
                    POTIONS.put(noted, potion);
                }
                POTIONS.put(id, potion);
            }
        }
    }

    public static Potion get(final int id) {
        return POTIONS.get(id);
    }

    @Override
    public Item leftoverItem(int id) {
        val index = ArrayUtils.indexOf(ids, id);
        if (index == -1) {
            throw new RuntimeException("Invalid id: " + id + " " + this);
        }
        if (index == 0) {
            return new Item(vial());
        }
        return new Item(ids[index - 1]);
    }

    @Override
    public Item getItem(final int doses) {
        if (doses < 0 || doses > ids.length)
            throw new RuntimeException("The potion " + this + " doesn't support dose " + doses + ".");
        if (doses == 0) {
            return new Item(vial());
        }
        return new Item(ids[doses - 1]);
    }

    public Item vial() {
        return vial;
    }

    @Override
    public Boost[] boosts() {
        return boosts;
    }

    @Override
    public String startMessage() {
        return "You drink some of your %s.";
    }

    @Override
    public String endMessage(Player player) {
        return "You have %d dose%s of potion left.";
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
        return "You have finished your potion.";
    }

}
