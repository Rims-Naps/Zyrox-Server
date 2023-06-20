package com.zenyte.game.content.skills.farming.hespori;

import com.zenyte.game.RuneDate;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 24/02/2019 15:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class Flower extends NPC implements CombatScript, Spawnable {

    /**
     * An enum containing the positions where the flowers spawn.
     */
    @AllArgsConstructor
    enum FlowerPosition {SOUTH_WESTERN(new Location(1243, 10083, 0)), NORTH_WESTERN(new Location(1243, 10091, 0)),
        NORTH_EASTERN(new Location(1251, 10091, 0)), SOUTH_EASTERN(new Location(1251, 10083, 0));

        static final FlowerPosition[] values = values();
        @NotNull
        private final Location tile;}

    /**
     * The animation the npc performs when it enters the bloom mode.
     */
    private static final Animation bloomAnimation = new Animation(8229);

    /**
     * The animation the npc performs when it dies in the bloom mode.
     */
    private static final Animation stillAnimation = new Animation(8230);

    /**
     * The npc's ids for bloom and still modes.
     */
    private static final int BLOOMING = NpcId.FLOWER, STILL = NpcId.FLOWER_8585;

    /**
     * The package-level constructor for the flowers. Sets the npc to face south & starts it off in the still mode.
     * Random walk radius is set to 0, as the flowers cannot walk.
     *
     * @param instance the Hespori's instance that spawns the flowers.
     * @param position the position of the flowers where they spawn.
     */
    Flower(@NotNull final HesporiInstance instance, @NotNull final FlowerPosition position) {
        this(STILL, instance.getLocation(position.tile), Direction.SOUTH, 0);
    }

    /**
     * The default constructor for the flowers, as they also exist in the static map.
     *
     * @param id     the id of the npc.
     * @param tile   the tile where the npc spawns.
     * @param facing the facing of the npc.
     * @param radius the radius of the npc.
     */
    private Flower(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
    }

    /**
     * Validates the npc when spawning it from external fiels.
     *
     * @param id   the id of the npc spawned.
     * @param name the name of the npc spawned.
     * @return whether or not the npc matches this npc.
     */
    @Override
    public boolean validate(int id, String name) {
        return id == STILL || id == BLOOMING;
    }

    /**
     * Custom attack method, implemented to not allow the flowers to attack targets back, thus resulting in
     * {@link Integer#MAX_VALUE } delay.
     *
     * @param target the target entity.
     * @return the delay.
     */
    @Override
    public int attack(Entity target) {
        return Integer.MAX_VALUE;
    }

    /**
     * THe Hespori stands on-top of a clipped farming patch, meaning we must override the projectile clip method to
     * not check for clipping whilst attacking it.
     *
     * @param player the player attacking Hespori.
     * @return whether or not to check for projectile clipping underneath the NPC.
     */
    @Override
    public boolean checkProjectileClip(final Player player) {
        return false;
    }

    /**
     * The flower npcs cannot walk around under any circumstances.
     *
     * @return whether or not the npc can walk.
     */
    @Override
    public boolean isMovableEntity() {
        return false;
    }

    /**
     * Sets the flower to bloom, and puts it back to life.
     */
    void bloom() {
        assert getId() != BLOOMING;
        reset();
        setAnimation(bloomAnimation);
        WorldTasksManager.schedule(() -> setTransformation(BLOOMING), 1);
    }

    /**
     * Checks to see whether the flower is blooming or not, based on their current id.
     *
     * @return whether or not the flower is blooming.
     */
    boolean isBlooming() {
        return getId() == BLOOMING;
    }

    /**
     * Sends the npc to "death", during which phase the flower stands still.
     *
     */
    @Override
    public void sendDeath() {
        assert getId() != STILL;
        setAnimation(stillAnimation);
        combat.removeTarget();
        WorldTasksManager.schedule(() -> setTransformation(STILL));
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        super.handleIngoingHit(hit);
        hit.setDamage(10);
        val source = hit.getSource();
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (!player.getBooleanAttribute("hard-combat-achievement34")) {
                    val oldFlowerCount = player.getAttributes().getOrDefault("amount_of_flowers_killed", "0").toString();
                    player.getAttributes().put("amount_of_flowers_killed", oldFlowerCount.contains("0") ? "1" : oldFlowerCount.contains("1") ? "2" : oldFlowerCount.contains("2") ? "3" : "4");
                    val newFlowerCount = player.getAttributes().get("amount_of_flowers_killed").toString();
                    if (newFlowerCount.equals("1")) {
                        player.getAttributes().put("time_when_first_flower_was_killed", RuneDate.currentTimeMillis());
                    } else if (newFlowerCount.equals("4")) {
                        long timeOfFirstKill = 0;
                        if (player.getAttributes().get("time_when_first_flower_was_killed") instanceof Long) {
                            timeOfFirstKill = (long) player.getAttributes().get("time_when_first_flower_was_killed");
                        }
                        val timeNow = RuneDate.currentTimeMillis();
                        if (timeOfFirstKill + 5000L >= timeNow) {
                            player.putBooleanAttribute("hard-combat-achievement34", true);
                            HardTasks.sendHardCompletion(player, 34);
                        }
                        player.sendMessage("You killed the first and last flower " + Colour.RED.wrap((int) (timeNow - timeOfFirstKill)) + " milliseconds apart");
                        player.getAttributes().put("amount_of_flowers_killed", "0");
                    }
                }
            }
        }
    }

}
