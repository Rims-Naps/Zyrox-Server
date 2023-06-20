package com.zenyte.game.world.entity.npc.impl.slayer;

import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;

import java.util.Objects;

/**
 * @author Tommeh | 30 mrt. 2018 : 23:13:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 *
 * @author Kris | 05/08/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * Revised by Kris.
 */
public class DemonicGorilla extends CrashSiteGorilla implements Spawnable {

    private static final int MAX_DAMAGE_THRESHOLD = 50;
    private static final int MELEE_GORILLA = NpcId.DEMONIC_GORILLA;
    private static final int RANGED_GORILLA = NpcId.DEMONIC_GORILLA_7145;
    private static final int MAGIC_GORILLA = NpcId.DEMONIC_GORILLA_7146;

    public DemonicGorilla(final int id, final Location tile, final Direction direction, final int radius) {
        super(id, tile, direction, radius);
    }

    @Override
    int failedHitsUntilSwitch() {
        return 3;
    }

    private int hitThreshold;

    @Override
    public NPC spawn() {
        hitThreshold = 0;
        return super.spawn();
    }

    @Override
    public void processNPC() {
        val target = combat.getTarget();
        if (target != null) {
            if (target instanceof Player) {
                if (Objects.equals(target.getTemporaryAttributes().get("demonic_gorillas_debug"), "true")) {
                    setForceTalk(new ForceTalk("Hits threshold: " + hitThreshold + ", Attacks counter: " + missedHits + ", Melee delay: " + meleeMovementDelay + ", Style: " + combatDefinitions.getAttackType()));
                }
            }
        }
        super.processNPC();
    }

    @Override
    public float getXpModifier(final Hit hit) {
        val weapon = hit.getWeapon();
        if (weapon != null) {
            if ("Dwarf Multicannon".equals(weapon.toString())) {
                hit.setDamage(0);
                return 0;
            }
        }
        val hitType = hit.getHitType();
        if (id == MELEE_GORILLA && hitType.equals(HitType.MELEE)
                || id == RANGED_GORILLA && hitType.equals(HitType.RANGED)
                || id == MAGIC_GORILLA && hitType.equals(HitType.MAGIC)) {
            hit.setDamage(0);
            return 0;
        }
        val source = hit.getSource();
        //Poison and things like this do not count so if the source is null, let's ignore it.
        if (source != null && (hitThreshold += hit.getDamage()) >= MAX_DAMAGE_THRESHOLD) {
            val next = hitType.equals(HitType.MELEE) ? MELEE_GORILLA : hitType.equals(HitType.RANGED) ? RANGED_GORILLA : MAGIC_GORILLA;
            val style = combatDefinitions.getAttackStyle();
            setTransformation(next);
            setAnimation(boulderAnimation);
            combatDefinitions.setAttackStyle(style);
            if (combat.getCombatDelay() < 5) {
                combat.setCombatDelay(5);
            }
            hitThreshold = 0;
            source.cancelCombat();
        }
        return 1;
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id >= NpcId.DEMONIC_GORILLA && id <= NpcId.DEMONIC_GORILLA_7146;
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (player.getEquipment().getItem(EquipmentSlot.WEAPON) != null) {
                    if ((player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("silverlight")
                            || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("darklight")
                            || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("arclight"))
                            && !player.getBooleanAttribute("elite-combat-achievement57")) {
                        player.putBooleanAttribute("elite-combat-achievement57", true);
                        EliteTasks.sendEliteCompletion(player, 57);
                    }
                }
            }
        }
    }

}
