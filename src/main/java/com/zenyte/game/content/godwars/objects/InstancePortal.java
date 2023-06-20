package com.zenyte.game.content.godwars.objects;

import com.zenyte.game.HintArrow;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.content.godwars.GodwarsInstanceManager;
import com.zenyte.game.content.godwars.GodwarsInstancePortal;
import com.zenyte.game.content.godwars.PortalTeleport;
import com.zenyte.game.content.godwars.instance.GodwarsInstance;
import com.zenyte.game.content.godwars.instance.InstanceConstants;
import com.zenyte.game.content.godwars.npcs.DyingKnight;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author Kris | 13/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class InstancePortal implements ObjectAction {

    private final Class<?>[] parameters = new Class[] {
            String.class, AllocatedArea.class
    };

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (!option.equals("Use")) {
            return;
        }
        if (!DyingKnight.canUsePortal(player)) {
            World.findNPC(10023, player.getLocation(), 10).ifPresent(npc -> player.getPacketDispatcher().sendHintArrow(new HintArrow(npc)));
            player.getDialogueManager().start(new Dialogue(player, 10023) {
                @Override
                public void buildDialogue() {
                    npc("Dying Knight", "Psst! Hey! Come talk to me first.");
                }
            });
            return;
        }
        val portal = Objects.requireNonNull(Utils.findMatching(GodwarsInstancePortal.getValues(), v -> v.getPortalObjectId() == object.getId()));
        if (player.getArea() instanceof GodwarsInstance) {
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain("Are you sure you wish to leave?<br>" +
                            "If there are no players left in the instance, " + Colour.RED.wrap("it will collapse") + ".");
                    options(new DialogueOption("Yes.", () -> {
                        player.lock();
                        player.addWalkSteps(object.getX(), object.getY(), 1, false);
                        WorldTasksManager.schedule(() -> new PortalTeleport(portal.getGodwarsPortalLocation()).teleport(player), 1);
                    }), new DialogueOption("No."));
                }
            });
            return;
        }
        val channel = player.getSettings().getChannel();
        if (channel == null) {
            player.sendMessage("You need to be in a clan chat channel to start or join an instance.");
            return;
        }

        val instance = GodwarsInstanceManager.getManager().findInstance(player, portal.getGod());
        if (instance.isPresent()) {
            player.lock();
            player.addWalkSteps(object.getX(), object.getY(), 1, false);
            WorldTasksManager.schedule(() -> new PortalTeleport(instance.get().getLocation(portal.getPortalLocation())).teleport(player), 1);
            val players = instance.get().getPlayers();
            for (val p : players) {
                p.getAttributes().remove("armadyl_kc_on_instance_creation");
                p.getAttributes().remove("kril_kc_on_instance_creation");
                p.getAttributes().remove("graardor_kc_on_instance_creation");
                p.getAttributes().remove("zilyana_kc_on_instance_creation");
            }
            return;
        }

        val rank = ClanManager.getRank(player, channel);
        if (rank.getId() < channel.getKickRank().getId()) {
            player.sendMessage("Clan members ranked as " + Utils.formatString(channel.getKickRank().toString()) + " or above can only start a clan instance.");
            return;
        }

        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                boolean easy = EasyTasks.allEasyCombatAchievementsDone(player);
                boolean medium = MediumTasks.allMediumCombatAchievementsDone(player) && easy;
                boolean hard = HardTasks.allHardCombatAchievementsDone(player) && medium;
                boolean elite = EliteTasks.allEliteCombatAchievementsDone(player) && hard;
                boolean master = MasterTasks.allMasterCombatAchievementsDone(player) && elite;
                boolean grandmaster = GrandmasterTasks.allGrandmasterCombatAchievementsDone(player) && master;
                val cost = grandmaster ? 75000 : master ? 100000 : elite ? 125000 : InstanceConstants.INSTANCE_COST;
                plain("Pay " + Utils.format(cost) + " to start an instance for your clan?");
                options(new DialogueOption("Yes.", () -> {
                            val amountInInventory = player.getInventory().getAmountOf(ItemId.COINS_995);
                            val amountInBank = player.getBank().getAmountOf(ItemId.COINS_995);
                            if ((long) amountInBank + amountInInventory >= cost) {
                                if (GodwarsInstanceManager.getManager().findInstance(player, portal.getGod()).isPresent()) {
                                    setKey(75);
                                    return;
                                }
                                player.lock();
                                player.getInventory().deleteItem(new Item(995, cost)).onFailure(remainder -> player.getBank().remove(remainder));
                                try {
                                    val allocatedArea = MapBuilder.findEmptyChunk(8, 8);
                                    val area = portal.getInstanceClass().getDeclaredConstructor(parameters).newInstance(player.getSettings().getChannel().getOwner(), allocatedArea);
                                    area.constructRegion();
                                    player.addWalkSteps(object.getX(), object.getY(), 1, false);
                                    WorldTasksManager.schedule(() -> new PortalTeleport(area.getLocation(portal.getPortalLocation())).teleport(player), 1);
                                    player.getAttributes().put("armadyl_kc_on_instance_creation", player.getNotificationSettings().getKillcount("kree'arra"));
                                    player.getAttributes().put("kril_kc_on_instance_creation", player.getNotificationSettings().getKillcount("kree'arra"));
                                    player.getAttributes().put("graardor_kc_on_instance_creation", player.getNotificationSettings().getKillcount("kree'arra"));
                                    player.getAttributes().put("zilyana_kc_on_instance_creation", player.getNotificationSettings().getKillcount("kree'arra"));
                                } catch (OutOfSpaceException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                                    log.error(Strings.EMPTY, e);
                                }
                                return;
                            }
                            setKey(50);
                        }),
                        new DialogueOption("No."));

                plain(50, "You don't have enough coins with you or in your bank.");
                plain(75, "Someone in your clan has already initiated an instance.");
            }
        });
    }

    @Override
    public Object[] getObjects() {
        val list = new ObjectArrayList<Object>();
        for (val portal : GodwarsInstancePortal.getValues()) {
            list.add(portal.getPortalObjectId());
        }
        return list.toArray();
    }
}
