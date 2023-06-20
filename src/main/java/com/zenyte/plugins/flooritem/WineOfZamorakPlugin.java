package com.zenyte.plugins.flooritem;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.FaladorDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.Flags;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.region.CharacterLoop;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 4. juuni 2018 : 01:36:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class WineOfZamorakPlugin implements FloorItemPlugin {

    private static final Location WINE_OF_ZAMORAK_LOCATION = new Location(2950, 3824, 0);
    private static final Location FALADOR_WINE_OF_ZAMORAK_LOCATION = new Location(2930, 3515, 0);
    private static final Location FALADOR_UPSTAIRS_WINE_OF_ZAMORAK_LOCATION = new Location(2938, 3517, 1);

    {
        //Jagex is doing some dumb shit with the upstairs table there and as such, I need to override the mask cus the table is projectile clipped by default.
        World.getRegion(FALADOR_UPSTAIRS_WINE_OF_ZAMORAK_LOCATION.getRegionId(), true).setMask(1, (2938 & 0x3F), (3517 & 0x3F),
                Flags.OBJECT);
    }

    @Override
    public void telegrab(@NotNull final Player player, @NotNull final FloorItem item) {
        if (!canTelegrab(player, item)) {
            return;
        }
        World.destroyFloorItem(item);
        val addedItem = new Item(item);
        player.getAchievementDiaries().update(FaladorDiary.TELEGRAB_WINE_OF_ZAMORAK);
        if (WineOfZamorakPlugin.WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation()) && DiaryReward.WILDERNESS_SWORD3.eligibleFor(player)) {
            addedItem.setId(246);
        } else if (FALADOR_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation()) || FALADOR_UPSTAIRS_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation())) {
            val monks = CharacterLoop.find(player.getLocation(), 10, NPC.class, npc -> !npc.isDead() && (npc.getId() == 8400 || npc.getId() == 8401));
            if (!monks.isEmpty()) {
                if (!FALADOR_UPSTAIRS_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation()) || !wearingZamorakRobes(player)) {
                    monks.forEach(monk -> monk.getCombat().setTarget(player));
                    damage(player, true);
                }
            }
        }
        player.getInventory().addItem(addedItem).onFailure(it -> World.spawnFloorItem(it, player, 100, 200));
    }

    public static boolean wearingZamorakRobes(final Player player) {
        val top = player.getEquipment().getId(EquipmentSlot.PLATE);
        val bottoms = player.getEquipment().getId(EquipmentSlot.LEGS);
        return (top == 10460 || top == 1035) && (bottoms == 10468 || bottoms == 1033);
    }

    @Override
    public void handle(final Player player, final FloorItem item, final int optionId, final String option) {
        if (option.equals("Take")) {
            if (WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation()) || FALADOR_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation()) || FALADOR_UPSTAIRS_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation())) {
                if (FALADOR_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation()) || FALADOR_UPSTAIRS_WINE_OF_ZAMORAK_LOCATION.matches(item.getLocation())) {
                    val monks = CharacterLoop.find(player.getLocation(), 10, NPC.class, npc -> !npc.isDead() && (npc.getId() == 8400 || npc.getId() == 8401));
                    if (monks.isEmpty()) {
                        World.takeFloorItem(player, item);
                        damage(player, false);
                        return;
                    }
                    monks.forEach(monk -> monk.getCombat().setTarget(player));
                }
                damage(player, true);
                return;
            }
            World.takeFloorItem(player, item);
        }
    }

    private static final int[] skills = new int[] {
            Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC
    };

    private void damage(final Player player, final boolean message) {
        player.setGraphics(CombatSpell.FLAMES_OF_ZAMORAK.getHitGfx());
        if (message) {
            player.sendMessage("STOP STEALING MY WINE! GAH!");
        }
        WorldTasksManager.schedule(() -> {
            for (val skill : skills) {
                player.drainSkill(skill, 4);
            }
            val hitpoints = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            val damage = 1 + (hitpoints / 20);
            player.applyHit(new Hit(damage, HitType.DEFAULT));
        });
    }

    @Override
    public boolean overrideTake() {
        return true;
    }

    @Override
    public int[] getItems() {
        return new int[] { 245 };
    }

}
