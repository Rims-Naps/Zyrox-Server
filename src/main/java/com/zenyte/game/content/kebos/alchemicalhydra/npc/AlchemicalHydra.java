package com.zenyte.game.content.kebos.alchemicalhydra.npc;

import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.kebos.alchemicalhydra.HydraPhase;
import com.zenyte.game.content.kebos.alchemicalhydra.instance.AlchemicalHydraInstance;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.HydraPhaseSequence;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.EnragedPhase;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.FlamePhase;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.combat.phases.LightningPhase;
import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.content.skills.prayer.ectofuntus.Bonecrusher;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.drop.matrix.Drop;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.plugins.item.RingOfWealthItem;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.var;
import mgi.types.config.AnimationDefinitions;

/**
 * @author Tommeh | 02/11/2019 | 16:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class AlchemicalHydra extends NPC implements CombatScript {

    private static final int DEATH_SEQ_NPC_ID = 8622;
    private static final Animation preDeathAnim = new Animation(8257);
    private static final Animation finalDeathAnim = new Animation(8258);

    public static final ForceTalk roar = new ForceTalk("Roaaaaaaaaaaar!");

    @Getter
    private AlchemicalHydraInstance instance;
    @Getter
    @Setter
    private HydraPhase phase;
    @Setter
    private HydraPhaseSequence sequence;
    @Getter
    @Setter
    private boolean shielded, transforming, dying;
    @Getter
    private double attackModifier;
    private int autoAttacks;
    @Getter
    @Setter
    private static boolean hasBeenHitByPoison;

    public AlchemicalHydra(final AlchemicalHydraInstance instance, final Location tile) {
        super(NpcId.ALCHEMICAL_HYDRA, tile, Direction.SOUTH, 16);
        this.instance = instance;
        resetNPC(HydraPhase.POISON);
        setAttackDistance(3);
    }

    public void switchStyle() {
        val style = getCombatDefinitions().getAttackStyle();
        if (style.equals(AttackType.MAGIC)) {
            combatDefinitions.setAttackStyle(AttackType.RANGED);
        } else {
            combatDefinitions.setAttackStyle(AttackType.MAGIC);
        }
    }

    @Override
    protected boolean preserveStatsOnTransformation() {
        return true;
    }

    @Override
    public void setTransformation(final int id) {
        val style = combatDefinitions.getAttackStyle();
        nextTransformation = id;
        setId(id);
        size = definitions.getSize();
        updateFlags.flag(UpdateFlag.TRANSFORMATION);
        if (preserveStatsOnTransformation()) {
            updateTransformationalDefinitions();
        } else {
            updateCombatDefinitions();
        }
        combatDefinitions.setAttackStyle(style);
    }

    public void transform() {
        if (transforming) {
            return;
        }
        val player = instance.getPlayer();
        val nextPhase = phase.next();
        transforming = true;
        player.stopAll(false);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (ticks == 0) {
                    setCantInteract(true);
                    combat.setTarget(null);
                    setInteractingWith(null);
                    resetWalkSteps();
                    lock();
                    setTransformation(nextPhase.getPreTransformation());
                    setAnimation(nextPhase.getPreTransformationAnim());
                } else if (ticks == nextPhase.getSequenceDelay()) {
                    setTransformation(nextPhase.getPostTransformation());
                    setAnimation(nextPhase.getPostTransformationAnim());
                } else if (ticks == nextPhase.getSequenceDelay() + 1) {
                    if (nextPhase.equals(HydraPhase.ENRAGED)) {
                        player.sendMessage("The Alchemical Hydra becomes enraged!");
                    }
                    resetNPC(nextPhase);
                    setCantInteract(false);
                    combat.setTarget(player);
                    setInteractingWith(player);
                    unlock();
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    public void incrementAutoAttacks() {
        autoAttacks++;
    }

    public void increaseStrength() {
        attackModifier = Math.min(1.5, attackModifier + 0.05);
    }

    public Location getRangedHeadLocation(final Entity target) {
        val tile = getMiddleLocation();
        val dir = Utils.getFaceDirection(target.getX() - tile.getX(), target.getY() - tile.getY());
        if (dir < 256) {
            tile.moveLocation(-2, -1, 0);
        } else if (dir < 512) {
            tile.moveLocation(-1, 1, 0);
        } else if (dir < 768) {
            tile.moveLocation(0, 1, 0);
        } else if (dir < 1024) {
            tile.moveLocation(1, 1, 0);
        } else if (dir < 1280) {
            tile.moveLocation(2, 0, 0);
        } else if (dir < 1536) {
            tile.moveLocation(2, -1, 0);
        } else if (dir < 1792) {
            tile.moveLocation(1, -1, 0);
        } else {
            tile.moveLocation(0, -2, 0);
        }
        return tile;
    }

    public Location getMagicHeadLocation(final Entity target) {
        val tile = getMiddleLocation();
        val dir = Utils.getFaceDirection(target.getX() - tile.getX(), target.getY() - tile.getY());
        if (dir < 256) {
            tile.moveLocation(1, -1, 0);
        } else if (dir < 512) {
            tile.moveLocation(0, -2, 0);
        } else if (dir < 768) {
            tile.moveLocation(-1, -1, 0);
        } else if (dir < 1024) {
            tile.moveLocation(-1, 1, 0);
        } else if (dir < 1280) {
            tile.moveLocation(-2, 1, 0);
        } else if (dir < 1536) {
            tile.moveLocation(0, 2, 0);
        } else if (dir < 1792) {
            tile.moveLocation(1, 2, 0);
        } else {
            tile.moveLocation(2, 0, 0);
        }
        return tile;
    }

    public int getMaxHit() {
        return phase.equals(HydraPhase.ENRAGED) ? 26 : 17;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public NPC spawn() {
        final NPC hydra = super.spawn();
        if (instance.isEntered()) {
            instance.getPlayer().getBossTimer().startTracking("Alchemical Hydra");
            instance.getPlayer().putBooleanAttribute("has_taken_damage_from_hydra", false);
            instance.getPlayer().putBooleanAttribute("NoPressureTask", true);
        }
        if (Utils.random(1) == 0) {
            switchStyle();
        }
        FlamePhase.setHasDoneFireWall(false);
        AlchemicalHydraInstance.setWasEmpowered(false);
        FlamePhase.setHasBeenHitByFireTrail(false);
        LightningPhase.setHasBeenHitByLightning(false);
        setHasBeenHitByPoison(false);
        return hydra;
    }

    @Override
    public int getRespawnDelay() {
        return BossRespawnTimer.ALCHEMICAL_HYDRA.getTimer().intValue();
    }

    @Override
    public boolean canAttack(final Player source) {
        if (transforming) {
            source.sendMessage("You cannot attack the Alchemical Hydra while it's transforming.");
            return false;
        }
        val assignment = source.getSlayer().getAssignment();
        if (assignment == null || !(assignment.getTask().equals(RegularTask.HYDRAS) || assignment.getTask().equals(RegularTask.ALCHEMICAL_HYDRAS))) {
            source.sendMessage("You can only attack the Alchemical Hydra while on a Hydras slayer task.");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkAggressivity() {
        if (sequence instanceof FlamePhase && ((FlamePhase) sequence).isPerformingSpecialAttack() && hasWalkSteps()) {
            return false;
        }
        return super.checkAggressivity();
    }

    @Override
    public void resetWalkSteps() {
        if (sequence instanceof FlamePhase && ((FlamePhase) sequence).isPerformingSpecialAttack()) {
            return;
        }
        super.resetWalkSteps();
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        if (sequence instanceof FlamePhase && ((FlamePhase) sequence).isPerformingSpecialAttack()) {
            return false;
        }
        return super.addWalkStep(nextX, nextY, lastX, lastY, check);
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (sequence instanceof FlamePhase && ((FlamePhase) sequence).isWalkingToMiddle()) {
            return;
        }
        super.autoRetaliate(source);

    }

    @Override
    public void setAnimation(final Animation animation) {
        this.animation = animation;
        if (animation == null) {
            updateFlags.set(UpdateFlag.ANIMATION, false);
            lastAnimation = 0;
        } else {
            updateFlags.flag(UpdateFlag.ANIMATION);
            val defs = AnimationDefinitions.get(animation.getId());
            if (defs != null) {
                lastAnimation = Utils.currentTimeMillis() + defs.getDuration();
            } else {
                lastAnimation = Utils.currentTimeMillis();
            }
        }
    }

    @Override
    public int attack(final Entity target) {
        randomWalkDelay = 10;
        if (target instanceof Player) {
            val specialAttack = sequence.attack(this, (Player) target);
            if (!specialAttack && autoAttacks > 0 && autoAttacks % 3 == 0) {
                switchStyle();
            }
            return combatDefinitions.getAttackSpeed();
        }
        return -1;
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        super.handleIngoingHit(hit);
        val source = hit.getSource();
        if (source instanceof Player) {
            val player = (Player) source;
            if (shielded && !phase.equals(HydraPhase.ENRAGED)) {
                hit.setDamage((int) (hit.getDamage() * 0.75F));
                player.sendFilteredMessage("The Alchemical Hydra's defences partially absorb your attack!");
            }
            if (!(player.getHitpoints() <= 10 && 4886 <= player.getWeapon().getId() && player.getWeapon().getId() <= 4890)) {
                player.putBooleanAttribute("NoPressureTask", false);
            }
        }
        sequence.handleIngoingHit(this, hit);
    }

    @Override
    public void processNPC() {
        super.processNPC();
        sequence.process(this, instance.getPlayer());
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            player.getAchievementDiaries().update(KourendDiary.KILL_A_HYDRA);
            if (!FlamePhase.isHasDoneFireWall() && !player.getBooleanAttribute("master-combat-achievement9")) {
                player.putBooleanAttribute("master-combat-achievement9", true);
                MasterTasks.sendMasterCompletion(player, 9);
            }
            if (!AlchemicalHydraInstance.isWasEmpowered() && !player.getBooleanAttribute("master-combat-achievement10")) {
                player.putBooleanAttribute("master-combat-achievement10", true);
                MasterTasks.sendMasterCompletion(player, 10);
            }
            if (!FlamePhase.isHasBeenHitByFireTrail() && !player.getBooleanAttribute("master-combat-achievement11")) {
                player.putBooleanAttribute("master-combat-achievement11", true);
                MasterTasks.sendMasterCompletion(player, 11);
            }
            if (!LightningPhase.isHasBeenHitByLightning() && !player.getBooleanAttribute("master-combat-achievement12")) {
                player.putBooleanAttribute("master-combat-achievement12", true);
                MasterTasks.sendMasterCompletion(player, 12);
            }
            if (!isHasBeenHitByPoison() && !player.getBooleanAttribute("master-combat-achievement13")) {
                player.putBooleanAttribute("master-combat-achievement13", true);
                MasterTasks.sendMasterCompletion(player, 13);
            }
            if (!isHasBeenHitByPoison()
                    && !LightningPhase.isHasBeenHitByLightning()
                    && !FlamePhase.isHasBeenHitByFireTrail()
                    && !player.getBooleanAttribute("has_taken_damage_from_hydra")
                    && !player.getBooleanAttribute("master-combat-achievement24")) {
                player.putBooleanAttribute("master-combat-achievement24", true);
                MasterTasks.sendMasterCompletion(player, 24);
            }
            if (player.getNumericAttribute("hydra_kc_on_instance_creation").intValue() + 14 <= player.getNotificationSettings().getKillcount("alchemical hydra")
                    && !player.getBooleanAttribute("master-combat-achievement49")
                    && player.getArea() instanceof AlchemicalHydraInstance) {
                player.putBooleanAttribute("master-combat-achievement49", true);
                MasterTasks.sendMasterCompletion(player, 49);
            }
            if (!player.getBooleanAttribute("grandmaster-combat-achievement12")
                    && player.getBooleanAttribute("NoPressureTask")) {
                player.putBooleanAttribute("grandmaster-combat-achievement12", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 12);
            }
        }
    }

    @Override
    public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        onDeath(source);
        lock();
        setAnimation(preDeathAnim);
        dying = true;
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks == 2) {
                    setTransformation(DEATH_SEQ_NPC_ID);
                    setAnimation(finalDeathAnim);
                } else if (ticks == 9) {
                    unlock();
                    setId(phase.getPostTransformation());
                    onFinish(source);
                    setId(HydraPhase.POISON.getPostTransformation());
                    resetNPC(HydraPhase.POISON);
                    dying = false;
                    stop();
                    return;
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public Location getRespawnTile() {
        return instance.getLocation(AlchemicalHydraInstance.hydraCenterLocation);
    }

    @Override
    public void dropItem(final Player killer, final Item item, final Location tile, boolean guaranteedDrop) {
        invalidateItemCharges(item);
        killer.getCollectionLog().add(item);
        WorldBroadcasts.broadcast(killer, BroadcastType.RARE_DROP, item, getName(killer));
        LootBroadcastPlugin.fireEvent(killer.getName(), item, tile, guaranteedDrop);
        if (item.getId() == 11941 && killer.containsItem(item)) {
            return;
        }
        //Amulet of avarice's effect inside revenant caves.
        if (killer.getEquipment().getId(EquipmentSlot.AMULET) == 22557 && GlobalAreaManager.get("Forinthry Dungeon").inside(getLocation())) {
            item.setId(item.getDefinitions().getNotedOrDefault());
        }
        val id = item.getId();
        if ((id == 995 || id == 21555 || id == 6529) && RingOfWealthItem.isRingOfWealth(killer.getRing())
                && !killer.getBooleanSetting(Setting.ROW_CURRENCY_COLLECTOR)) {
            killer.getInventory().addOrDrop(item);
            killer.getNotificationSettings().sendDropNotification(item);
            return;
        }

        killer.getNotificationSettings().sendDropNotification(item);

        val bone = Bones.getBone(item.getId());
        val crusherType = Bonecrusher.CrusherType.get(killer);
        if (crusherType != null && bone != null) {
            if (crusherType.getEffect().crush(killer, bone, false)) {
                return;
            }
        }
        spawnDrop(item, tile, killer);
    }

    @Override
    protected void drop(final Location tile) {
        val killer = instance.getPlayer();
        if (killer == null) {
            return;
        }
        killer.getBossTimer().finishTracking("Alchemical Hydra");
        if (killer.getBossTimer().getBossTimer("Alchemical Hydra") < 110 && !killer.getBooleanAttribute("master-combat-achievement39")) {
            killer.putBooleanAttribute("master-combat-achievement39", true);
            MasterTasks.sendMasterCompletion(killer, 39);
        }
        if (killer.getBossTimer().getBossTimer("Alchemical Hydra") < 90 && !killer.getBooleanAttribute("grandmaster-combat-achievement22")) {
            killer.putBooleanAttribute("grandmaster-combat-achievement22", true);
            GrandmasterTasks.sendGrandmasterCompletion(killer, 22);
        }
        onDrop(killer);
        val processors = DropProcessorLoader.get(HydraPhase.ENRAGED.getPostTransformation());
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }
        val drops = NPCDrops.getTable(HydraPhase.ENRAGED.getPostTransformation());
        if (drops == null) {
            return;
        }
        NPCDrops.forEach(drops, drop -> dropItem(killer, drop, tile, true));
        NPCDrops.forEach(drops, drop -> {
            if (!drop.isAlways()) {
                dropItem(killer, drop, tile, false);
            }
        });
    }

    public void dropItem(final Player killer, final Drop drop, final Location location, final boolean uniques) {
        var item = new Item(drop.getItemId(), Utils.random(drop.getMinAmount(), drop.getMaxAmount()));
        if (uniques) {
            val processors = DropProcessorLoader.get(HydraPhase.ENRAGED.getPostTransformation());
            if (processors != null) {
                val baseItem = item;
                for (val processor : processors) {
                    if ((item = processor.drop(this, killer, drop, item)) == null) {
                        return;
                    }
                    if (item != baseItem) break;
                }
            }
        }
        //do NOT reference 'drop' after this line, rely on 'item' only!
        dropItem(killer, item, location, drop.isAlways());
    }

    @Override
    protected void spawnDrop(final Item item, final Location tile, final Player killer) {
        if (item.getId() == ItemId.MYSTIC_FIRE_STAFF) {
            dropItem(killer, new Item(ItemId.MYSTIC_WATER_STAFF, item.getAmount()));
        } else if (item.getId() == ItemId.RUNE_PLATEBODY) {
            dropItem(killer, killer.getAppearance().isMale() ? new Item(ItemId.RUNE_PLATELEGS, item.getAmount()) : new Item(ItemId.RUNE_PLATESKIRT, item.getAmount()));
        } else if (item.getId() == ItemId.MYSTIC_ROBE_TOP_LIGHT) {
            dropItem(killer, new Item(ItemId.MYSTIC_ROBE_BOTTOM_LIGHT, item.getAmount()));
        } else if (item.getId() == ItemId.RANGING_POTION3) {
            dropItem(killer, new Item(ItemId.SUPER_RESTORE3, 2 * item.getAmount()));
        }
        super.spawnDrop(item, tile, killer);
    }

    private void resetNPC(final HydraPhase nextPhase) {
        shielded = true;
        transforming = false;
        attackModifier = 1.0;
        if (nextPhase == HydraPhase.ENRAGED) {
            //If the stage didn't already switch after the last hit, we reset the counter and swap the style.
            if (autoAttacks % 3 == 0) {
                switchStyle();
            }
            autoAttacks = 0;
        } else if (nextPhase == HydraPhase.POISON) {
            //When the next stage is poison(AKA back to the first stage of the kill), we reset the counter.
            autoAttacks = 0;
            //To start off the fight, we randomize the starting attack style.
            if (Utils.random(1) == 0) {
                switchStyle();
            }
        }
        val style = combatDefinitions.getAttackStyle();
        phase = nextPhase;
        try {
            sequence = phase.getPhaseSequence().newInstance();
            if (sequence instanceof EnragedPhase) {
                ((EnragedPhase) sequence).setPreviousStyle(style);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
