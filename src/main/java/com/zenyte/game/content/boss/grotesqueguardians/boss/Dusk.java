package com.zenyte.game.content.boss.grotesqueguardians.boss;

import com.zenyte.game.content.boss.grotesqueguardians.FightPhase;
import com.zenyte.game.content.boss.grotesqueguardians.instance.GrotesqueGuardiansInstance;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.skills.prayer.actions.Bones;
import com.zenyte.game.content.skills.prayer.ectofuntus.Bonecrusher;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.*;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.npc.impl.slayer.Gargoyle;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.plugins.item.RingOfWealthItem;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.types.config.AnimationDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Tommeh | 21/07/2019 | 21:51
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Dusk extends NPC implements CombatScript {

    private static final Animation MELEE_ATTACK_ANIM = new Animation(7785);
    private static final Animation SECOND_MELEE_ATTACK_ANIM = new Animation(7787);
    private static final Animation MELEE_ATTACK_P4_ANIM = new Animation(7800);
    private static final Animation RANGED_ATTACK_ANIM = new Animation(7801);
    private static final Animation EXPLOSION_ATTACK_ANIM = new Animation(7802);
    private static final Animation PLAYER_KNOCKBACK_ANIM = new Animation(1157);
    private static final Animation PRISON_ATTACK_FIRST_ANIM = new Animation(7796);
    private static final Animation PRISON_ATTACK_ANIM = new Animation(7799);
    private static final Animation DEATH_FIRST_ANIM = new Animation(7803);
    private static final Animation DEATH_SECOND_ANIM = new Animation(7809);

    private static final Projectile RANGED_ATTACK_PROJ = new Projectile(1444, 90, 40, 0, 32);

    private static final Graphics PRISON_ATTACK_GFX = new Graphics(1434);
    private static final ForceTalk ARGHHH = new ForceTalk("Arghhh!");

    public static final int ATTACKABLE_NPC_ID = NpcId.DUSK_7851;
    public static final int NON_ATTACKABLE_NPC_ID = NpcId.DUSK_7854;
    public static final int LIGHTNING_ATTACK_NPC_ID = NpcId.DUSK_7855;
    private static final int P4_NON_ATTACKABLE_NPC_ID = NpcId.DUSK_7887;
    private static final int P4_ATTACKABLE_NPC_ID = NpcId.DUSK_7888;
    private static final int DEATH_SEQUENCE_NPC_ID = NpcId.DUSK_7889;

    @Setter
    private Dawn dawn;
    private GrotesqueGuardiansInstance instance;
    @Setter
    private boolean explosiveAttack;
    private boolean dying;
    @Getter
    private long prisonAttackDelay;
    @Getter
    private boolean performingPrisonAttack;
    private int attacks;
    private boolean onePrisonAttack;

    public Dusk(final Location tile, final GrotesqueGuardiansInstance instance) {
        super(ATTACKABLE_NPC_ID, tile, Direction.EAST, 10);
        this.instance = instance;
        this.maxDistance = 20;
        onePrisonAttack = true;
    }

    @Override
    public float getXpModifier(final Hit hit) {
        return hit.getHitType().equals(HitType.RANGED) || hit.getHitType().equals(HitType.MAGIC) ? 0 : 1;
    }

    @Override
    public void resetWalkSteps() {
        if (instance.isPerformingLightningAttack()) {
            return;
        }
        super.resetWalkSteps();
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        if (instance.isPerformingLightningAttack()) {
            return false;
        }
        if (id != NON_ATTACKABLE_NPC_ID && !instance.getWalkingBoundary().contains(nextX, nextY)) {
            return false;
        }
        return super.addWalkStep(nextX, nextY, lastX, lastY, check);
    }

    @Override
    public boolean canAttack(final Player source) {
        if (!definitions.containsOptionCaseSensitive("Attack")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (!isCantInteract()) {
            super.autoRetaliate(source);
        }
    }

    @Override
    public boolean setHitpoints(final int amount) {
        val dead = isDead();
        this.hitpoints = amount;
        if (!dead && hitpoints <= 9) {
            sendDeath();
            return true;
        }
        return false;
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
    public void handleIngoingHit(final Hit hit) {
        if (instance.getPhase().equals(FightPhase.PHASE_ONE) || instance.getPhase().equals(FightPhase.PHASE_THREE)) {
            hit.setDamage(0);
        } else if (hit.getHitType().equals(HitType.MAGIC) || hit.getHitType().equals(HitType.RANGED)) {
            hit.setDamage(0);
        }
        if (instance.getPhase().equals(FightPhase.PHASE_TWO)) {
            attacks++;
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public int attack(Entity target) {
        if (!(target instanceof Player) || dying || isDead()) {
            return getCombatDefinitions().getAttackSpeed();
        }
        val player = (Player) target;
        if (explosiveAttack && attacks >= 2) {
            lock();
            setAnimation(EXPLOSION_ATTACK_ANIM);
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;
                Location destination;
                boolean hit;

                @Override
                public void run() {
                    switch (ticks++) {
                        case 1:
                            if (!isWithinMeleeDistance(Dusk.this, player)) {
                                return;
                            }
                            hit = true;
                            player.getPacketDispatcher().sendClientScript(64);
                            val middle = getMiddleLocation();
                            double degrees = Math.toDegrees(Math.atan2(player.getY() - middle.getY(), player.getX() - middle.getX()));
                            if (degrees < 0) {
                                degrees += 360;
                            }
                            val angle = Math.toRadians(degrees);
                            val px = (int) Math.round(middle.getX() + (getSize() + 10) * Math.cos(angle));
                            val py = (int) Math.round(middle.getY() + (getSize() + 10) * Math.sin(angle));
                            val tiles = Utils.calculateLine(player.getX(), player.getY(), px, py, player.getPlane());
                            if (!tiles.isEmpty()) {
                                tiles.remove(0);
                            }
                            destination = new Location(player.getLocation());
                            for (val tile : tiles) {
                                val dir = Utils.getMoveDirection(tile.getX() - destination.getX(), tile.getY() - destination.getY());
                                if (dir == -1) {
                                    continue;
                                }
                                if (!World.checkWalkStep(destination.getPlane(), destination.getX(), destination.getY(), dir, player.getSize(), false, false))
                                    break;
                                destination.setLocation(tile);
                            }
                            val direction = Utils.getFaceDirection(player.getX() - destination.getX(), player.getY() - destination.getY());
                            if (!destination.matches(player)) {
                                player.setForceMovement(new ForceMovement(destination, 30, direction));
                                player.lock();
                            }
                            player.faceEntity(Dusk.this);
                            player.setAnimation(PLAYER_KNOCKBACK_ANIM);
                            break;
                        case 2:
                            if (!hit) {
                                break;
                            }
                            player.unlock();
                            player.getPacketDispatcher().sendClientScript(1970);
                            player.setLocation(destination);
                            player.applyHit(new Hit(Dusk.this, 20 + Utils.random(15), HitType.REGULAR));
                            player.putBooleanAttribute("was_hit_by_blinding_grot_guar", true);
                            break;
                        case 7:
                            unlock();
                            getCombat().setTarget(player);
                            instance.setDebrisAllowed(true);
                            stop();
                            break;
                    }
                }
            }, 0, 0);
            attacks = 0;
            explosiveAttack = false;
            return getCombatDefinitions().getAttackSpeed();
        }

        if (instance.getPhase().equals(FightPhase.PHASE_FOUR)) {
            if (Utils.random(2) <= 1) {
                setAnimation(RANGED_ATTACK_ANIM);
                World.sendProjectile(this, target, RANGED_ATTACK_PROJ);
                delayHit(this, RANGED_ATTACK_PROJ.getTime(this, target), target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
                WorldTasksManager.schedule(() -> {
                    World.sendProjectile(this, target, RANGED_ATTACK_PROJ);
                    delayHit(this, RANGED_ATTACK_PROJ.getTime(this, target), target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
                });
            } else {
                setAnimation(MELEE_ATTACK_P4_ANIM);
                delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), CRUSH, target), HitType.MELEE));
            }
        } else {
            val attack = Utils.random(1);
            setAnimation(attack == 0 ? MELEE_ATTACK_ANIM : SECOND_MELEE_ATTACK_ANIM);
            delayHit(this, attack, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), CRUSH, target), HitType.MELEE));
        }
        return getCombatDefinitions().getAttackSpeed();
    }

    public void prisonAttack(final boolean first) {
        //The size of the chamber is naturally 5x5 tiles-
        val chamberSize = 5;
        //The distance between the chamber's box and dusk; 0 = can be next to eachother, 1 = needs at least 1 tile between them. Cannot be too far or it won't always find a situation.
        int tilesDistance = 3;
        Optional<Location> optionalCenter;
        val randomPoint = instance.getRandomPoint();
        val boundary = instance.getPrisonBoundary();
        while(true) {
            val finalTilesDistance = tilesDistance;
            optionalCenter = Utils.findEmptySquare(randomPoint, 30, 5,
                    Optional.of(l -> !Utils.collides(l, chamberSize, getLocation(), 5, finalTilesDistance) && boundary.contains(l.getX(), l.getY())));
            if (optionalCenter.isPresent()) {
                break;
            }
            if (--tilesDistance < 0) {
                throw new RuntimeException("Unable to find a location for the square.");
            }
        }
        val center = optionalCenter.get().transform(2, 2, 0);
        val player = instance.getPlayer();
        performingPrisonAttack = true;
        lock();
        getCombat().setTarget(null);
        if (first) {
            //setHitpoints(225); //225
        } else {
            onePrisonAttack = false;
        }
        setTransformation(P4_NON_ATTACKABLE_NPC_ID);
        setAnimation(first ? PRISON_ATTACK_FIRST_ANIM : PRISON_ATTACK_ANIM);

        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (dying || isDead()) {
                    player.unlock();
                    stop();
                    return;
                }
                switch (ticks++) {
                    case 1:
                    case 2:
                        setFaceEntity(player);
                        break;
                    case 5:
                        player.setAnimation(PLAYER_KNOCKBACK_ANIM);
                        player.autoForceMovement(center, 60);
                        player.setForceTalk(ARGHHH);
                        player.lock(2);
                        setFaceEntity(player);
                        break;
                    case 7:
                        createPrison(center);
                        break;
                    case 11:
                        if (player.getLocation().withinDistance(center, 1)) {
                            player.applyHit(new Hit(Dusk.this, Utils.random(60, 67), HitType.REGULAR));
                            player.putBooleanAttribute("was_hit_by_prison_grot_guar", true);
                        }
                        break;
                    case 14:
                        performingPrisonAttack = false;
                        prisonAttackDelay = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(25);
                        unlock();
                        setTransformation(P4_ATTACKABLE_NPC_ID);
                        getCombat().setTarget(instance.getPlayer());
                        stop();
                        break;
                }
            }
        }, 0, 0);
    }

    private void createPrison(final Location center) {
        val edge = new Location[16];
        val corners = new Location[4];
        val middle = new Location[9];
        Location exit;

        var index = 0;
        for (int i = -2; i < 2; i++) { //north
            edge[index++] = center.transform(i, 2, 0);
        }
        for (int i = -2; i < 2; i++) { //west
            edge[index++] = center.transform(-2, i, 0);
        }
        for (int i = -1; i < 2; i++) { //south
            edge[index++] = center.transform(i, -2, 0);
        }
        for (int i = -2; i < 3; i++) { //south
            edge[index++] = center.transform(2, i, 0);
        }
        corners[0] = center.transform(-2, -2, 0);
        corners[1] = center.transform(-2, 2, 0);
        corners[2] = center.transform(2, -2, 0);
        corners[3] = center.transform(2, 2, 0);

        index = 0;
        for (int i = -1; i < 1; i++) { //north
            middle[index++] = center.transform(i, 1, 0);
        }
        for (int i = -1; i < 1; i++) { //west
            middle[index++] = center.transform(-1, i, 0);
        }
        for (int i = 0; i < 1; i++) { //south
            middle[index++] = center.transform(i, -1, 0);
        }
        for (int i = -1; i < 2; i++) { //south
            middle[index++] = center.transform(1, i, 0);
        }
        middle[index] = center;

        exit = getPrisonExitLocation(getMiddleLocation(), edge, corners);

        for (val location : edge) {
            if (exit.equals(location)) {
                continue;
            }
            World.sendGraphics(PRISON_ATTACK_GFX, location);
            World.spawnObject(new WorldObject(0, 10, 3, location)); //invisible, used to clip
        }
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                switch (ticks++) {
                    case 3:
                        for (val location : middle) {
                            World.sendGraphics(PRISON_ATTACK_GFX, location);
                        }
                        break;
                    case 6:
                        for (val location : edge) {
                            World.removeObject(new WorldObject(0, 10, 3, location));
                        }
                        stop();
                        break;
                }
            }
        }, 0, 0);
    }

    @NotNull
    private Location getPrisonExitLocation(final Location from, final Location[] edge, final Location[] corners) {
        Location tile = null;
        double distance = Double.MAX_VALUE;
        for (val location : edge) {
            if (ArrayUtils.contains(corners, location)) {
                continue;
            }
            val df = location.getDistance(from);
            if (df < distance) {
                tile = location;
                distance = df;
            }
        }
        if (tile == null) {
            throw new IllegalStateException();
        }
        return tile;
    }

    @Override
    protected void onFinish(final Entity source) {
        drop(getMiddleLocation());
        reset();
        finish();
        if (source instanceof Player) {
            val player = (Player) source;
            sendNotifications(player);
            if (!player.getBooleanAttribute("was_hit_by_lightning_grot_guar") && !player.getBooleanAttribute("hard-combat-achievement29")) {
                player.putBooleanAttribute("hard-combat-achievement29", true);
                HardTasks.sendHardCompletion(player, 29);
            }
            if (!player.getBooleanAttribute("dawn_healed") && !player.getBooleanAttribute("hard-combat-achievement30")) {
                player.putBooleanAttribute("hard-combat-achievement30", true);
                HardTasks.sendHardCompletion(player, 30);
            }
            if (!player.getBooleanAttribute("was_hit_by_rockfall_grot_guar") && !player.getBooleanAttribute("hard-combat-achievement31")) {
                player.putBooleanAttribute("hard-combat-achievement31", true);
                HardTasks.sendHardCompletion(player, 31);
            }
            if (!player.getBooleanAttribute("was_hit_by_prison_grot_guar") && !player.getBooleanAttribute("hard-combat-achievement32")) {
                player.putBooleanAttribute("hard-combat-achievement32", true);
                HardTasks.sendHardCompletion(player, 32);
            }
            if (!player.getBooleanAttribute("was_hit_by_blinding_grot_guar") && !player.getBooleanAttribute("hard-combat-achievement33")) {
                player.putBooleanAttribute("hard-combat-achievement33", true);
                HardTasks.sendHardCompletion(player, 33);
            }
            if (onePrisonAttack && !player.getBooleanAttribute("elite-combat-achievement37")) {
                player.putBooleanAttribute("elite-combat-achievement37", true);
                EliteTasks.sendEliteCompletion(player, 37);
            }
            if (!player.getBooleanAttribute("master-combat-achievement32")) {
                if (onePrisonAttack
                        && !player.getBooleanAttribute("was_hit_by_blinding_grot_guar")
                        && !player.getBooleanAttribute("was_hit_by_prison_grot_guar")
                        && !player.getBooleanAttribute("was_hit_by_rockfall_grot_guar")
                        && !player.getBooleanAttribute("dawn_healed")
                        && !player.getBooleanAttribute("was_hit_by_lightning_grot_guar")) {
                    player.getAttributes().put("perfectguardians", player.getNumericAttribute("perfectguardians").intValue() + 1);
                    player.sendMessage("You killed the Grotesque Guardians perfectly " +
                            player.getNumericAttribute("perfectguardians").intValue() + " times in a row.");
                } else {
                    player.getAttributes().put("perfectguardians", 0);
                    if (player.getBooleanAttribute("was_hit_by_blinding_grot_guar")) {
                        player.sendMessage("You were hit by Dusk's blinding attack, your perfect GG streak has ended.");
                    }
                    if (player.getBooleanAttribute("was_hit_by_prison_grot_guar")) {
                        player.sendMessage("You were hit by Dusk's prison attack, your perfect GG streak has ended.");
                    }
                    if (player.getBooleanAttribute("was_hit_by_rockfall_grot_guar")) {
                        player.sendMessage("You were hit by the rockfall, your perfect GG streak has ended.");
                    }
                    if (player.getBooleanAttribute("dawn_healed")) {
                        player.sendMessage("Dawn healed by absorbing her orbs, your perfect GG streak has ended.");
                    }
                    if (player.getBooleanAttribute("was_hit_by_lightning_grot_guar")) {
                        player.sendMessage("You were hit by the lightning, your perfect GG streak has ended.");
                    }
                    if (!onePrisonAttack) {
                        player.sendMessage("Dusk used his prison attack more than once, your perfect GG streak has ended.");
                    }
                }
            }
            if (player.getNumericAttribute("perfectguardians").intValue() != 0
                    && !player.getBooleanAttribute("elite-combat-achievement48")) {
                player.putBooleanAttribute("elite-combat-achievement48", true);
                EliteTasks.sendEliteCompletion(player, 48);
            }
            if (player.getNumericAttribute("perfectguardians").intValue() >= 5
                    && !player.getBooleanAttribute("master-combat-achievement32")) {
                player.putBooleanAttribute("master-combat-achievement32", true);
                MasterTasks.sendMasterCompletion(player, 32);
            }
        }
    }

    @Override
    protected void drop(final Location tile) {
        val killer = instance.getPlayer();
        if (killer == null) {
            return;
        }
        killer.getBossTimer().finishTracking("Grotesque Guardians");
        if (killer.getBossTimer().getBossTimer("Grotesque Guardians") < 140 && !killer.getBooleanAttribute("elite-combat-achievement66")) {
            killer.putBooleanAttribute("elite-combat-achievement66", true);
            EliteTasks.sendEliteCompletion(killer, 66);
        }
        if (killer.getNumericAttribute("grotesque_guardians_kc_on_instance_creation").intValue() + 9 <= killer.getNotificationSettings().getKillcount("grotesque guardians")
                && !killer.getBooleanAttribute("elite-combat-achievement69")
                && killer.getArea() instanceof GrotesqueGuardiansInstance) {
            killer.putBooleanAttribute("elite-combat-achievement69", true);
            EliteTasks.sendEliteCompletion(killer, 69);
        }
        if (killer.getNumericAttribute("grotesque_guardians_kc_on_instance_creation").intValue() + 19 <= killer.getNotificationSettings().getKillcount("grotesque guardians")
                && !killer.getBooleanAttribute("master-combat-achievement50")
                && killer.getArea() instanceof GrotesqueGuardiansInstance) {
            killer.putBooleanAttribute("master-combat-achievement50", true);
            MasterTasks.sendMasterCompletion(killer, 50);
        }
        if (killer.getBossTimer().getBossTimer("Grotesque Guardians") < 120 && !killer.getBooleanAttribute("master-combat-achievement45")) {
            killer.putBooleanAttribute("master-combat-achievement45", true);
            MasterTasks.sendMasterCompletion(killer, 45);
        }
        if (killer.getBossTimer().getBossTimer("Grotesque Guardians") < 110 && !killer.getBooleanAttribute("grandmaster-combat-achievement28")) {
            killer.putBooleanAttribute("grandmaster-combat-achievement28", true);
            GrandmasterTasks.sendGrandmasterCompletion(killer, 28);
        }
        onDrop(killer);
        this.id = 7888;
        val processors = DropProcessorLoader.get(id);
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }
        val drops = NPCDrops.getTable(id);
        if (drops == null) {
            return;
        }
        NPCDrops.forEach(drops, drop -> dropItem(killer, drop, tile));
        NPCDrops.forEach(drops, drop -> {
            if (!drop.isAlways())
                dropItem(killer, drop, tile);
        });
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
    protected void sendNotifications(final Player player) {
        player.getNotificationSettings().increaseKill("grotesque guardians");
        player.getNotificationSettings().sendBossKillCountNotification("grotesque guardians");
        VarCollection.GROTESQUE_GUARDIAN_KC_FOR_BELL.update(player);
    }

    @Override
    public void sendDeath() {
        if (dying || !instance.getPhase().equals(FightPhase.PHASE_FOUR)) {
            return;
        }
        val source = instance.getPlayer();
        val isUnlocked = source.getSlayer().isUnlocked("Gargoyle smasher");
        val attr = getTemporaryAttributes().get("used_rock_hammer");
        if ((attr != null && (boolean) attr) || (getHitpoints() <= 9 && isUnlocked && (source.getInventory().containsItem(Gargoyle.ROCK_HAMMER) || source.getInventory().containsItem(Gargoyle.GRANITE_HAMMER) || source.getInventory().containsItem(21754, 1)))) {
            dying = true;
            setId(P4_ATTACKABLE_NPC_ID);
            onDeath(source);
            lock();
            setAnimation(DEATH_FIRST_ANIM);

            getTemporaryAttributes().remove("used_rock_hammer");
            if (!source.getInventory().containsItem(Gargoyle.ROCK_HAMMER) && !source.getInventory().containsItem(Gargoyle.GRANITE_HAMMER)) {
                source.getInventory().deleteItem(21754, 1);
            }
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    switch (ticks++) {
                        case 1:
                            setTransformation(DEATH_SEQUENCE_NPC_ID);
                            setAnimation(DEATH_SECOND_ANIM);
                            break;
                        case 4:
                            instance.reset();
                            onFinish(source);
                            stop();
                            break;
                    }
                }
            }, 0, 0);
            source.sendMessage("Dusk cracks apart.");
        } else if (getHitpoints() == 0) {
            heal(1);
        }
    }

}
