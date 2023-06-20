package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.room.ResourcesRoom;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 28/07/2019 08:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Bat extends RaidNPC<ResourcesRoom> {
    public Bat(final Raid raid, final ResourcesRoom room, final int id, final Location tile) {
        super(raid, room, id, tile);
    }

    @Override
    public int getRespawnDelay() {
        return 2;
    }

    @Override
    public void setRespawnTask() {
        if (!isFinished()) {
            reset();
            finish();
        }
        WorldTasksManager.schedule(this::spawn, getRespawnDelay());
    }

    public static final class BatPlugin extends NPCPlugin {

        private static final SoundEffect attempting = new SoundEffect(2623);
        private static final SoundEffect caught = new SoundEffect(293);

        @Override
        public void handle() {
            bind("Catch", new OptionHandler() {
                @Override
                public void handle(final Player player, final NPC npc) {
                    player.getActionManager().setAction(new Action() {


                        private BatType bat;
                        private boolean success;

                        private boolean isAbleToBarehand() {
                            if (player.getSkills().getLevel(Skills.HUNTER) >= 99) {
                                return true;
                            }else {
                                return false;
                            }
                        }

                        @Override
                        public boolean start() {
                            if (player.getEquipment().getId(EquipmentSlot.WEAPON) != 10010) {
                                if (!isAbleToBarehand()) {
                                    player.getDialogueManager().start(new ItemChat(player, new Item(10010), "You need a butterfly net to catch these bats."));
                                    return false;
                                }
                            }
                            if ((bat = Utils.findMatching(BatType.values, bat -> bat.id == npc.getId())) == null) {
                                throw new IllegalStateException();
                            }
                            if (player.getSkills().getLevel(Skills.HUNTER) < bat.level) {
                                player.sendMessage("You need a Hunter level of at least " + bat.level + " to catch this bat.");
                                return false;
                            }
                            if (!player.getInventory().hasFreeSlots()) {
                                player.sendMessage("You need some free inventory space to catch the bat.");
                                return false;
                            }
                            this.success = success(player);
                            player.sendSound(attempting);
                            player.setAnimation(new Animation(success ? 6606 : 6605));
                            if (success) {
                                npc.setCantInteract(true);
                            }
                            delay(1);
                            return true;
                        }

                        private boolean success(@NotNull final Player player) {
                            val level = player.getSkills().getLevel(Skills.HUNTER);
                            val n = Math.floor((306F * (level - 1F)) / 98F) - 78;
                            val chance = n / 255F;
                            return Utils.randomDouble() < chance;
                        }

                        @Override
                        public boolean process() {
                            return true;
                        }

                        @Override
                        public int processWithDelay() {
                            if (!success) {
                                player.sendMessage("You fail to catch the " + npc.getName(player).toLowerCase() + ".");
                                return -1;
                            }
                            player.sendSound(caught);
                            player.sendMessage("You successfully catch the " + npc.getName(player).toLowerCase() + ".");
                            player.getInventory().addItem(new Item(bat.itemId));
                            player.getSkills().addXp(Skills.HUNTER, bat.experience);
                            npc.setRespawnTask();
                            if (success) {
                                npc.setCantInteract(false);
                            }
                            return -1;
                        }
                    });
                }

                @Override
                public void execute(final Player player, final NPC npc) {
                    player.stopAll();
                    player.setFaceEntity(npc);
                    handle(player, npc);
                }
            });
        }

        @Override
        public int[] getNPCs() {
            return new int[] {
                    NpcId.GUANIC_BAT, NpcId.PRAEL_BAT, NpcId.GIRAL_BAT, NpcId.PHLUXIA_BAT, NpcId.KRYKET_BAT, NpcId.MURNG_BAT, NpcId.PSYKK_BAT
            };
        }
    }

    @Getter
    @AllArgsConstructor
    public enum BatType {
        GUANIC(NpcId.GUANIC_BAT, ItemId.RAW_GUANIC_BAT_0, 1, 5),
        PRAEL(NpcId.PRAEL_BAT, ItemId.RAW_PRAEL_BAT_1, 15, 9),
        GIRAL(NpcId.GIRAL_BAT, ItemId.RAW_GIRAL_BAT_2, 30, 13),
        PHLUXIA(NpcId.PHLUXIA_BAT, ItemId.RAW_PHLUXIA_BAT_3, 45, 17),
        KRYKET(NpcId.KRYKET_BAT, ItemId.RAW_KRYKET_BAT_4, 60, 21),
        MURNG(NpcId.MURNG_BAT, ItemId.RAW_MURNG_BAT_5, 75, 25),
        PSYKK(NpcId.PSYKK_BAT, ItemId.RAW_PSYKK_BAT_6, 90, 29);

        private final int id, itemId, level, experience;
        public static final BatType[] values = values();
    }
}
