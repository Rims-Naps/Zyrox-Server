package com.zenyte.game.content.chambersofxeric.plugins.object;

import com.zenyte.game.content.chambersofxeric.room.LargeScavengerRoom;
import com.zenyte.game.content.skills.woodcutting.actions.Woodcutting;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 06/09/2019 09:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ScavengerRottenTree implements ObjectAction {

    private static final SoundEffect successfulChop = new SoundEffect(2070);
    private static final SoundEffect treeFall = new SoundEffect(2734, 4, 0);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.getRaid().ifPresent(raid -> {
            if (option.equalsIgnoreCase("Chop")) {
                if (!(object instanceof LargeScavengerRoom.BlockingObject)) {
                    throw new IllegalStateException("Object not instanceof blocking object.");
                }
                val blockingObject = (LargeScavengerRoom.BlockingObject) object;
                player.getActionManager().setAction(new Action() {

                    private Woodcutting.AxeResult axe;
                    private int ticks;

                    @Override
                    public boolean start() {
                        val optionalAxe = Woodcutting.getAxe(player);
                        if (!optionalAxe.isPresent()) {
                            player.sendMessage("You do not have an axe which you have the Woodcutting level to use.");
                            return false;
                        }
                        this.axe = optionalAxe.get();
                        if (blockingObject.getLevelRequired() > player.getSkills().getLevel(Skills.WOODCUTTING)) {
                            player.sendMessage("You need a Woodcutting level of at least " + blockingObject.getLevelRequired() + " to chop down this tree.");
                            return false;
                        }
                        blockingObject.getInteractingPlayers().add(player);
                        player.sendFilteredMessage("You swing your axe at the sapling...");
                        delay(axe.getDefinitions().getCutTime());
                        return true;
                    }

                    @Override
                    public boolean process() {
                        if (ticks++ % 4 == 0) player.setAnimation(axe.getDefinitions().getEmote());
                        return blockingObject.exists();
                    }

                    @Override
                    public int processWithDelay() {
                        player.sendSound(successfulChop);
                        player.getSkills().addXp(Skills.WOODCUTTING, 4.5);
                        raid.addPoints(player, 47);
                        if (Utils.random(Math.max(5, Math.min(20, raid.getOriginalPlayers().size() / 3))) == 0) {
                            val remnants = new WorldObject(blockingObject);
                            remnants.setId(LargeScavengerRoom.STUB);
                            World.spawnObject(remnants);
                            World.sendSoundEffect(object, treeFall);
                            ScavengerBlockingObstacleRemnants.cross(player, object);
                            blockingObject.getInteractingPlayers().clear();
                            return -1;
                        }
                        return axe.getDefinitions().getCutTime();
                    }

                    @Override
                    public void stop() {
                        blockingObject.getInteractingPlayers().remove(player);
                    }

                    @Override
                    public boolean interruptedByCombat() {
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getDelay() {
        return 1;
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                LargeScavengerRoom.TREE
        };
    }
}
