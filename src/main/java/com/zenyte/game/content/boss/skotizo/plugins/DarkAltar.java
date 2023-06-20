package com.zenyte.game.content.boss.skotizo.plugins;

import com.zenyte.game.content.boss.skotizo.instance.SkotizoInstance;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 05/03/2020 | 18:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 *
 * and Cresinkel
 */
@Slf4j
public class DarkAltar implements ObjectAction, ItemOnObjectAction {

    private static final Item darkTotem = new Item(ItemId.DARK_TOTEM);
    private static final Animation altarResetAnimation = new Animation(1471);
    private static final Animation altarAnimation = new Animation(1472);
    public static final Animation teleportAnimation = new Animation(3865);
    public static final Graphics teleportGraphics = new Graphics(1296);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (option.equals("Teleport")) {
            teleport(player, object);
        } else if (option.equals("Totem")) {
            totem(player);
        }
    }

    @Override
    public void handleItemOnObjectAction(final Player player, final Item item, final int slot, final WorldObject object) {
        teleport(player, object);
    }

    private void teleport(final Player player, final WorldObject object) {
        if (!player.getInventory().containsItem(darkTotem)) {
            player.sendMessage("You need a dark totem to teleport through the altar.");
            return;
        }
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("<col=ff0000>You will lose all of your items dropped if you die!</col>", "I know I'm risking everything I have.", "I need to prepare some more.")
                        .onOptionOne(() -> setKey(5));
                options(5, "Are you sure?", "Yes, I know items dropped in the instance will be lost.", "On second thoughts, better not.")
                        .onOptionOne(() -> {
                            player.lock();
                            WorldTasksManager.schedule(new WorldTask() {
                                int ticks;

                                @Override
                                public void run() {
                                    switch (ticks++) {
                                        case 0:
                                            player.getInventory().deleteItem(darkTotem);
                                            World.sendObjectAnimation(object, altarAnimation);
                                            player.putBooleanAttribute("has_taken_damage_from_skotizo", false);
                                            return;
                                        case 1:
                                            player.setAnimation(teleportAnimation);
                                            player.setGraphics(teleportGraphics);
                                            return;
                                        case 2:
                                            new FadeScreen(player).fade(3);
                                            return;
                                        case 4:
                                            World.sendObjectAnimation(object, altarResetAnimation);
                                            return;
                                        case 5:
                                            try {
                                                val area = MapBuilder.findEmptyChunk(8, 8);
                                                val instance = new SkotizoInstance(player, area);
                                                instance.constructRegion();
                                            } catch (OutOfSpaceException e) {
                                                log.error(Strings.EMPTY, e);
                                            }
                                            stop();
                                            return;
                                    }
                                }
                            }, 0, 0);
                        });
            }
        });
    }

    private void totem(Player player) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Do you want to turn in 5 ancient shard per dark totem?", "Yes.", "No.")
                        .onOptionOne(() -> givetotem(player));
            }
        });
    }

    private void givetotem(Player player) {
        player.getDialogueManager().finish();
        player.sendInputInt("How much ancient shards do you want to turn in?", amount -> {
            if (amount % 5 == 0 && amount != 0) {
                if (player.getInventory().containsItem(19677, amount)) {
                    player.getInventory().deleteItem(19677, amount);
                    player.getInventory().addOrDrop(19685, amount/5);
                } else {
                    player.sendMessage("You do not have " + amount + " ancient shards.");
                }
            } else if (amount == 0) {
                player.sendMessage("You can not turn in 0 ancient shards");
            } else {
                player.sendMessage(amount + " is not a multiple of 5.");
            }
        });
    }

    @Override
    public Object[] getItems() {
        return new Object[]{ItemId.DARK_TOTEM};
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{28900};
    }
}
