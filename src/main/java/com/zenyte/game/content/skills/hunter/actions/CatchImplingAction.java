package com.zenyte.game.content.skills.hunter.actions;

import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.minigame.puropuro.PuroPuroArea;
import com.zenyte.game.content.skills.hunter.node.Impling;
import com.zenyte.game.content.skills.hunter.npc.ImplingNPC;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Position;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.pathfinding.events.player.CombatEntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 2-11-2018 | 18:44
 * @author Corey 26/12/19
 */
@RequiredArgsConstructor
public class CatchImplingAction extends Action {
    
    private static final Animation ANIMATION = new Animation(6606);
    private static final Animation BAREHANDED_ANIMATION = new Animation(7171);
    
    private final Impling impling;
    private final ImplingNPC npc;
    
    private boolean barehanded;
    private int requiredLevel;
    private Animation anim;
    
    public static boolean success(final Player player, final int requiredLevel) {
        val hasMagicNet = hasMagicButterflyNetEquipped(player);
        val level = player.getSkills().getLevel(Skills.HUNTER);
        val n = Math.floor((306F * (level - 1F + (hasMagicNet ? 8 : 0))) / 98F) - requiredLevel;
        val chance = n / 255F;
    
        val rand = Utils.randomDouble();
        return rand < chance;
    }

    private boolean initiateCombat(final Player player) {
        if (player.isDead() || player.isFinished() || player.isLocked() || player.isStunned() || player.isFullMovementLocked()) {
            return false;
        }
        val target = npc;
        if (target.isFinished() || target.isCantInteract() || target.isDead()) {
            return false;
        }
        val distanceX = player.getX() - target.getX();
        val distanceY = player.getY() - target.getY();
        val size = target.getSize();
        val viewDistance = player.getViewDistance();
        if (player.getPlane() != target.getPlane() || distanceX > size + viewDistance || distanceX < -1 - viewDistance || distanceY > size + viewDistance || distanceY < -1 - viewDistance) {
            return false;
        }
        if (player.isFrozen() || player.isStunned() || player.isMovementLocked(false)) {
            return true;
        }

        if (!target.hasWalkSteps() && Utils.collides(player.getX(), player.getY(), player.getSize(), target.getX(), target.getY(), target.getSize())) {
            player.getCombatEvent().process();
            return true;
        }
        player.resetWalkSteps();
        if (player.isProjectileClipped(target, true) || !(withinRange(target, target.getSize()))) {
            appendWalksteps();
        }
        return true;
    }

    protected boolean isWithinAttackDistance() {
        val target = npc;
        val nextTile = target.getLocation();
        val nextPosition = player.getNextPosition(player.isRun() ? 2 : 1);
        val tile = nextTile != null ? nextTile : target.getLocation();
        val distanceX = nextPosition.getX() - tile.getX();
        val distanceY = nextPosition.getY() - tile.getY();
        val size = target.getSize();
        val nextLocation = target.getNextPosition(target.isRun() ? 2 : 1);
        if ((player.isFrozen() || player.isStunned())
                && (Utils.collides(nextPosition.getX(), nextPosition.getY(), player.getSize(), nextLocation.getX(), nextLocation.getY(), target.getSize())
                || !withinRange(target, target.getSize()))) {
            return false;
        }
        return distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;
    }

    protected final void appendWalksteps() {
        player.getCombatEvent().process();
    }

    final boolean withinRange(final Position targetPosition, final int targetSize) {
        val target = targetPosition.getPosition();
        val nextPosition = player.getNextPosition(player.isRun() ? 2 : 1);
        val distanceX = nextPosition.getX() - target.getX();
        val distanceY = nextPosition.getY() - target.getY();
        val npcSize = player.getSize();
        if (distanceX == -npcSize && distanceY == -npcSize || distanceX == targetSize && distanceY == targetSize
                || distanceX == -npcSize && distanceY == targetSize || distanceX == targetSize && distanceY == -npcSize) {
            return false;
        }

        return !(distanceX > targetSize || distanceY > targetSize || distanceX < -npcSize || distanceY < -npcSize);
    }
    
    @Override
    public boolean process() {
        return initiateCombat(player);
    }
    
    @Override
    public boolean start() {
        player.setCombatEvent(new CombatEntityEvent(player, new EntityStrategy(npc)));
        barehanded = !hasButterflyNetEquipped();
        anim = barehanded ? BAREHANDED_ANIMATION : ANIMATION;
        requiredLevel = Math.min(impling.getLevel() + (barehanded ? 10 : 0), 99);
        player.setFaceEntity(npc);
        if (initiateCombat(player)) {
            return true;
        }
        player.setFaceEntity(null);
        return false;
    }

    @Override
    public void stop() {
        val faceEntity = player.getFaceEntity();
        val lastDelay = player.getLastFaceEntityDelay();
        WorldTasksManager.schedule(() -> {
            if (player.getFaceEntity() == faceEntity && player.getLastFaceEntityDelay() == lastDelay) {
                player.setFaceEntity(null);
            }
        });
    }
    
    @Override
    public int processWithDelay() {
        if (!isWithinAttackDistance()) {
            return 0;
        }
        if (!player.getInventory().containsItem(ItemId.IMPLING_JAR)) {
            if (!barehanded
                    || player.getArea() instanceof PuroPuroArea) { // puro-puro implings must be caught in a jar
                player.sendMessage("You don't have an empty impling jar in which to keep an impling.");
                return -1;
            }
        }

        if (!barehanded) {
            if (player.getSkills().getLevel(Skills.HUNTER) < requiredLevel) {
                player.sendMessage("You need a Hunter level of at least " + requiredLevel + " to catch this impling.");
                return -1;
            }
        } else {
            if (player.getSkills().getLevel(Skills.HUNTER) < requiredLevel) {
                player.sendMessage("You need a Hunter level of at least " + requiredLevel + " to catch this impling barehanded.");
                return -1;
            }
            if (player.getInventory().getFreeSlots() < 2) {
                player.sendMessage("You need at least 2 spaces in your pack before attempting to catch this impling barehanded.");
                return -1;
            }
        }
        npc.lock(1);
        npc.resetWalkSteps();
        if (attemptCatch()) {
            player.getActionManager().setActionDelay(2);
            return -1;
        }
        return npc.isFrozen() ? 2 : 4;
    }

    private boolean attemptCatch() {
        val success = !npc.isCantInteract() && success(player, requiredLevel);
        if (success) {
            npc.setCantInteract(true);
        }
        WorldTasksManager.schedule(() -> {
            player.faceEntity(npc);
            player.setAnimation(anim);
            player.sendSound(new SoundEffect(2623));

            npc.setAnimation(new Animation(6615));
            npc.resetWalkSteps();
            npc.faceEntity(player);

            if (success) {
                npc.lock(5);
                WorldTasksManager.schedule(() -> {
                    npc.finish();
                    npc.setCantInteract(false);
                }, 1);

                player.getSkills().addXp(Skills.HUNTER, impling.getExperienceGielinor() * (barehanded ? 1.2 : 1.0));

                if (player.getArea() instanceof PuroPuroArea || !barehanded) {
                    player.getInventory().deleteItem(ItemId.IMPLING_JAR, 1);
                    player.getInventory().addItems(impling.getJar().getJarItem());
                    player.sendFilteredMessage("You manage to catch the impling and squeeze it into a jar.");
                } else {
                    player.sendFilteredMessage("You manage to catch the impling and acquire some loot.");
                    player.getInventory().addItems(impling.getJar().generateLoot(player));
                }

                val isPuro = (player.getArea() instanceof PuroPuroArea);
                val key = (isPuro ? Impling.PURO_IMPLING_TRACKER_ATTRIBUTE_KEY : Impling.SURFACE_IMPLING_TRACKER_ATTRIBUTE_KEY) + impling.getNpcId();
                player.addAttribute(key, player.getNumericAttribute(key).intValue() + 1);
    
                if (isPuro && (impling == Impling.ECLECTIC || impling == Impling.ESSENCE)) {
                    player.getAchievementDiaries().update(LumbridgeDiary.CATCH_IMPLING);
                }

                return;
            } else {
                npc.resetWalkSteps();
                npc.lock(1);
            }
            npc.setRetreatingFrom(player);
        });
        return success;
    }
    
    private static boolean hasMagicButterflyNetEquipped(final Player player) {
        return player.getEquipment().containsItem(new Item(ItemId.MAGIC_BUTTERFLY_NET));
    }
    
    private boolean hasButterflyNetEquipped() {
        if (player.getEquipment().containsItem(new Item(ItemId.BUTTERFLY_NET))) {
            return true;
        }
        return hasMagicButterflyNetEquipped(player);
    }
    
}
