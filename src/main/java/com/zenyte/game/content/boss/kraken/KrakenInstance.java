package com.zenyte.game.content.boss.kraken;

import com.zenyte.game.content.skills.slayer.BossTask;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.area.plugins.EntityAttackPlugin;
import com.zenyte.game.world.region.area.plugins.LogoutPlugin;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.val;

/**
 * @author Kris | 28/11/2018 17:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class KrakenInstance extends DynamicArea implements EntityAttackPlugin, DeathPlugin, LogoutPlugin, LootBroadcastPlugin {

    public static final Location OUTSIDE_TILE = new Location(2280, 10016, 0);
    public static final Location INSIDE_TILE = new Location(2280, 10022, 0);

    public KrakenInstance(final Player player, final AllocatedArea allocatedArea, final int copiedChunkX, final int copiedChunkY) {
        super(allocatedArea, copiedChunkX, copiedChunkY);
        this.player = player;
    }

    private final Player player;

    @Override
    public void constructed() {
        val kraken = new Kraken(496, getLocation(2278, 10034, 0), Direction.SOUTH, 5);
        kraken.spawn();
        player.sendMessage(Colour.RED.wrap("Should you die in the instance, your items will be permanently lost!"));
        player.getAttributes().put("kraken_kc_on_instance_creation", player.getNotificationSettings().getKillcount("kraken"));
        if (player.getNumericAttribute("kraken instance warning").intValue() == 0) {
            player.addAttribute("kraken instance warning", 1);
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain(Colour.RED.wrap("WARNING: ") + "If you die in this instance, your items will be permanently lost!");
                }
            });
        }
        player.setLocation(getLocation(INSIDE_TILE));
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {
        if (logout) {
            player.forceLocation(OUTSIDE_TILE);
        }
        player.getAttributes().remove("kraken_kc_on_instance_creation");
    }

    @Override
    public void onLogout(final Player player) {
        player.setLocation(OUTSIDE_TILE);
    }

    @Override
    public Location onLoginLocation() {
        return OUTSIDE_TILE;
    }

    @Override
    public String name() {
        return player.getName() + "'s Kraken instance";
    }

    @Override
    public boolean attack(Player player, Entity entity) {
        if (entity instanceof NPC) {
            val id = ((NPC) entity).getId();
            if (id == 492 || id == 493) {
                val assignment = player.getSlayer().getAssignment();
                if (assignment == null || assignment.getTask() != RegularTask.CAVE_KRAKEN) {
                    player.getDialogueManager().start(new Dialogue(player, 7412) {
                        @Override
                        public void buildDialogue() {
                            npc("You can only kill the Cave krakens while on a slayer task!");
                        }
                    });
                    return false;
                }
            } else if (id == 494 || id == 496) {
                val assignment = player.getSlayer().getAssignment();
                if (assignment != null && (assignment.getTask() == RegularTask.CAVE_KRAKEN || assignment.getTask() == BossTask.KRAKEN)) {
                    return true;
                }
                player.getDialogueManager().start(new Dialogue(player, 7412) {
                    @Override
                    public void buildDialogue() {
                        npc("You can only kill the Krakens while on a slayer task!");
                    }
                });
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSafe() {
        return false;
    }

    @Override
    public String getDeathInformation() {
        return null;
    }

    @Override
    public Location getRespawnLocation() {
        return null;
    }

}
