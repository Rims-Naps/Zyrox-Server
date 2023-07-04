package com.zenyte.game.content.tog;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.tog.juna.Juna;
import com.zenyte.game.content.tog.juna.JunaEnterDialogue;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.GameTab;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.area.plugins.EquipmentPlugin;
import com.zenyte.game.world.region.area.plugins.TeleportPlugin;
import lombok.val;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Chris
 * @since September 07 2020
 */
public class TearsOfGuthixCaveArea extends TearsOfGuthixArea implements CycleProcessPlugin, DeathPlugin, TeleportPlugin, EquipmentPlugin {
    /**
     * The varbit which controls the amount of time a player has left inside the cave.
     */
    public static final int TIMER_VARBIT = 5099;
    /**
     * The varp which is used to extend maximum timer value for timer bar component.
     */
    private static final int TIMER_VARP = 262;
    /**
     * The id for the side panel interface which shows time left as well as tears collected.
     */
    private static final int TOG_SIDE_PANEL_INTERFACE_ID = 276;
    /**
     * The temporary attribute which determines whether player is entering the cave.
     * Timer is not decremented while this attribute is true.
     */
    private static final String ENTERING_PLAYER_ATTR = "entering tog";

    /**
     * The temporary attribute which determines whether player is already being removed. If so, then no processing needs to take place.
     */
    private static final String REMOVING_PLAYER_ATTR = "removing from tog";
    private static final Animation drinkAnim = new Animation(2045);
    /**
     * The list of tab interfaces that need to be closed when entering cave and opened when leaving.
     */
    private static final List<GameInterface> TABS_INTERFACES = Arrays.asList(
            GameInterface.COMBAT_TAB, GameInterface.SKILLS_TAB, GameInterface.JOURNAL_HEADER_TAB,
            GameInterface.SETTINGS, GameInterface.EMOTE_TAB, GameInterface.MUSIC_TAB,
            GameInterface.INVENTORY_TAB, GameInterface.EQUIPMENT_TAB, GameInterface.PRAYER_TAB_INTERFACE,
            GameInterface.SPELLBOOK, GameInterface.FRIEND_LIST_TAB, GameInterface.GAME_NOTICEBOARD,
            GameInterface.LOGOUT, GameInterface.CLAN_CHAT_TAB);
    /**
     * The first tile player will walk to when sequence for removing player from cave is executed.
     */
    private static final ImmutableLocation EXITING_INSIDE_CAVE_TILE = new ImmutableLocation(3253, 9517, 2);

    public static void remove(@NotNull final Player player) {
        if (player.isDying() || player.isDead() || player.isLocked() || !player.inArea(TearsOfGuthixCaveArea.class)) {
            return;
        }
        player.getDialogueManager().finish();
        val firstDistance = player.getLocation().getTileDistance(EXITING_INSIDE_CAVE_TILE);
        val lastDistance = EXITING_INSIDE_CAVE_TILE.getTileDistance(JunaEnterDialogue.OUTSIDE_CAVE_TILE);
        val tearAmount = player.getVarManager().getBitValue(CollectTearAction.TEARS_COLLECTED_VARBIT);

        player.putBooleanTemporaryAttribute(REMOVING_PLAYER_ATTR, true);
        player.lock(firstDistance + lastDistance + 5);
        player.sendMessage("Your time in the cave is up.");


        // Walk to first tile and wait for Juna to open path.
        player.addWalkSteps(EXITING_INSIDE_CAVE_TILE.getX(), EXITING_INSIDE_CAVE_TILE.getY(), firstDistance, false);

        // Send animation to Juna to open the path.
        WorldTasksManager.schedule(() -> {
            player.sendSound(1797);
            World.sendObjectAnimation(Juna.JUNA_OBJECT, new Animation(2055));
        }, firstDistance);

        // Walk past Juna to outside of cave.
        WorldTasksManager.schedule(() -> {
            player.addWalkSteps(JunaEnterDialogue.OUTSIDE_CAVE_TILE.getX(), JunaEnterDialogue.OUTSIDE_CAVE_TILE.getY(), lastDistance, false);
        }, firstDistance + 1);


        WorldTasksManager.schedule(() -> {
            player.setAnimation(drinkAnim);
            player.sendMessage("You drink the liquid.");
        }, firstDistance + lastDistance + 1);

        WorldTasksManager.schedule(() -> {
            val lowestSkill = getLowestSkill(player);

            val expPerTear = Math.min(60, Utils.linearInterpolate(1, 10, 20, 26.6, player.getSkills().getLevelForXp(lowestSkill)));
            val eligibleForBonusExp = DiaryReward.EXPLORERS_RING3.eligibleFor(player);
            val rewardedExp = expPerTear * tearAmount * (eligibleForBonusExp ? 1.10 : 1);
            player.getEquipment().deleteItem(new Item(ItemId.STONE_BOWL));
            player.getSkills().addXp(lowestSkill, rewardedExp);
            player.log(LogLevel.INFO, "Received " + rewardedExp + " experience from TOG for " + Skills.getSkillName(lowestSkill));
            player.sendMessage(TearsOfGuthixSkillMessage.of(lowestSkill).getMessage());
            player.sendSound(1796);
            player.getAppearance().resetRenderAnimation();
            if (eligibleForBonusExp) {
                player.sendMessage(Colour.RED.wrap("You are awarded an additional 10% experience for completing the hard Lumbridge achievement diary."));
            }
        }, firstDistance + lastDistance + 3);
    }

    /**
     * Returns the skill with lowest experience following the order of {@link Skills#SKILL_IDS}.
     */
    private static int getLowestSkill(@NotNull final Player player) {
        var lowestSkill = Skills.SKILL_IDS.getInt(0);
        for (val skillId : Skills.SKILL_IDS) {
            val experience = player.getSkills().getExperience(skillId);
            if (experience < player.getSkills().getExperience(lowestSkill) && (skillId != Skills.CONSTRUCTION || Constants.CONSTRUCTION)) {
                lowestSkill = skillId;
            }
        }
        return lowestSkill;
    }

    public static int ticks(@NotNull final Player player) {
        val totalLevel = player.getSkills().getTotalLevel();
        return 50 + (totalLevel / 10) + TearsOfGuthixMemberPerk.ticks(player);
    }

    public RSPolygon[] polygons() {
        return new RSPolygon[]{
                new RSPolygon(new int[][]{
                        {3263, 9513},
                        {3263, 9521},
                        {3253, 9521},
                        {3253, 9513},
                })
        };
    }

    @Override
    public void enter(Player player) {
        val varManager = player.getVarManager();
        val timerTicks = ticks(player);

        player.putBooleanTemporaryAttribute(ENTERING_PLAYER_ATTR, true);
        WorldTasksManager.schedule(() -> player.putBooleanTemporaryAttribute(ENTERING_PLAYER_ATTR, false), 5);

        varManager.sendVar(TIMER_VARP, timerTicks);
        varManager.sendBit(TIMER_VARBIT, timerTicks);
        varManager.sendBit(CollectTearAction.TEARS_COLLECTED_VARBIT, 0);

        for (val tabInter : TABS_INTERFACES) {
            player.getInterfaceHandler().closeInterface(tabInter);
        }
        player.getInterfaceHandler().sendInterface(InterfacePosition.EQUIPMENT_TAB, TOG_SIDE_PANEL_INTERFACE_ID);
        player.getInterfaceHandler().openGameTab(GameTab.EQUIPMENT_TAB);
    }

    @Override
    public void process() {
        for (val player : getPlayers()) {
            if (player.getBooleanTemporaryAttribute(ENTERING_PLAYER_ATTR) || player.getBooleanTemporaryAttribute(REMOVING_PLAYER_ATTR)) {
                continue;
            }

            player.getVarManager().incrementBit(TIMER_VARBIT, -1);
            if (player.getVarManager().getBitValue(TIMER_VARBIT) <= 0) {
                remove(player);
            }
        }

        for (val weepingWall : TearsOfGuthixWall.WALLS) {
            weepingWall.process();
        }
    }


    @Override
    public void leave(Player player, boolean logout) {
        if (logout) {
            player.forceLocation(JunaEnterDialogue.OUTSIDE_CAVE_TILE);
        }
        for (val tabInter : TABS_INTERFACES) {
            tabInter.open(player);
        }
        if (logout || player.getLocation().getDistance(JunaEnterDialogue.OUTSIDE_CAVE_TILE) > 5) {
            player.getEquipment().deleteItem(new Item(ItemId.STONE_BOWL));
            player.getAppearance().resetRenderAnimation();
        }
        player.getTemporaryAttributes().remove(REMOVING_PLAYER_ATTR);
        player.getVarManager().sendBit(TIMER_VARBIT, 0);
        player.getVarManager().sendBit(CollectTearAction.TEARS_COLLECTED_VARBIT, 0);
    }

    @Override
    public String name() {
        return "Tears Of Guthix Cavede";
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return null;
    }

    @Override
    public Location getRespawnLocation() {
        return null;
    }

    @Override
    public boolean sendDeath(Player player, Entity source) {
        player.getEquipment().deleteItem(new Item(ItemId.STONE_BOWL));
        player.getTemporaryAttributes().remove(REMOVING_PLAYER_ATTR);
        player.getVarManager().sendBit(TIMER_VARBIT, 0);
        player.getVarManager().sendBit(CollectTearAction.TEARS_COLLECTED_VARBIT, 0);
        player.getAppearance().resetRenderAnimation();
        return false;
    }

    @Override
    public boolean canTeleport(Player player, Teleport teleport) {
        player.sendMessage("Maybe I should speak to Juna if I want to leave.");
        return false;
    }

    @Override
    public boolean unequip(final Player player, final Item item, final int slot) {
        if (item.getId() == ItemId.STONE_BOWL) {
            player.sendMessage("You can't collect any tears if you put the bowl in your pack.");
        }
        return false;
    }
}
