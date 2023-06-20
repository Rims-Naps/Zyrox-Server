package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Kris | 26/11/2018 18:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Ghommal extends NPCPlugin {


    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            if (TreasureTrail.talk(player, npc)) {
                return;
            }
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    boolean easy = EasyTasks.allEasyCombatAchievementsDone(player);
                    boolean medium = MediumTasks.allMediumCombatAchievementsDone(player) && easy;
                    boolean hard = HardTasks.allHardCombatAchievementsDone(player) && medium;
                    boolean elite = EliteTasks.allEliteCombatAchievementsDone(player) && hard;
                    boolean master = MasterTasks.allMasterCombatAchievementsDone(player) && elite;
                    boolean grandmaster = GrandmasterTasks.allGrandmasterCombatAchievementsDone(player) && master;
                    npc("Can I help you?");
                    options(TITLE, "I'm here to talk about Combat Achievements.", "Can you color my slayer helmet?", "Nevermind.")
                            .onOptionOne(() -> setKey(10))
                            .onOptionTwo(() -> setKey(80));
                    player(10, "I'm here to talk about Combat Achievements.");
                    options(TITLE, "Could you explain what they are?", "I think you owe me some rewards.", "Can you give me a book please?", "Can you give me your hilts back?", "I have to go.")
                            .onOptionOne(() -> setKey(20))
                            .onOptionTwo(() -> setKey(30))
                            .onOptionThree(() -> setKey(40))
                            .onOptionFour(() -> setKey(70));
                    player(20, "Could you explain what they are?");
                    npc("You can view your combat achievements via reading the combat achievements book.");
                    npc("Combat Achievements are broken down into six tiers: Easy, Medium, Hard, Elite, Master and Grandmaster.");
                    npc("Each of these tiers is progressively harder than the last, and to earn any rewards from the latter tiers, the prior tiers must be completed.");
                    npc("To complete a tier you will need to conquer every task within it.");
                    player(30, "I think you owe me some rewards.").executeAction(() -> {
                        if (easy) {
                            if (!player.getBooleanAttribute("Obtained easy Ca Rewards")) {
                                if (player.getInventory().hasSpaceFor(32262, 32259)) {
                                    player.putBooleanAttribute("Obtained easy Ca Rewards", true);
                                    player.getInventory().addItem(32262, 1);
                                    player.getInventory().addItem(32259, 1);
                                    WorldBroadcasts.broadcast(player, BroadcastType.COMBAT_ACHIEVEMENTS, "Easy");
                                } else {
                                    setKey(60);
                                }
                            } else {
                                if (medium) {
                                    if (!player.getBooleanAttribute("Obtained medium Ca Rewards")) {
                                        if (player.getInventory().hasSpaceFor(32263, 32257)) {
                                            player.putBooleanAttribute("Obtained medium Ca Rewards", true);
                                            player.getInventory().addItem(32263, 1);
                                            player.getInventory().addItem(32257, 1);
                                            WorldBroadcasts.broadcast(player, BroadcastType.COMBAT_ACHIEVEMENTS, "Medium");
                                        } else {
                                            setKey(60);
                                        }
                                    } else {
                                        if (hard) {
                                            if (!player.getBooleanAttribute("Obtained hard Ca Rewards")) {
                                                if (player.getInventory().hasSpaceFor(32264, 32255)) {
                                                    player.putBooleanAttribute("Obtained hard Ca Rewards", true);
                                                    player.getInventory().addItem(32264, 1);
                                                    player.getInventory().addItem(32255, 1);
                                                    WorldBroadcasts.broadcast(player, BroadcastType.COMBAT_ACHIEVEMENTS, "Hard");
                                                } else {
                                                    setKey(60);
                                                }
                                            } else {
                                                if (elite) {
                                                    if (!player.getBooleanAttribute("Obtained elite Ca Rewards")) {
                                                        if (player.getInventory().hasSpaceFor(32261, 32253)) {
                                                            player.putBooleanAttribute("Obtained elite Ca Rewards", true);
                                                            player.getInventory().addItem(32261, 1);
                                                            player.getInventory().addItem(32253, 1);
                                                            WorldBroadcasts.broadcast(player, BroadcastType.COMBAT_ACHIEVEMENTS, "Elite");
                                                        } else {
                                                            setKey(60);
                                                        }
                                                    } else {
                                                        if (master) {
                                                            if (!player.getBooleanAttribute("Obtained master Ca Rewards")) {
                                                                if (player.getInventory().hasSpaceFor(32265, 32251)) {
                                                                    player.putBooleanAttribute("Obtained master Ca Rewards", true);
                                                                    player.getInventory().addItem(32265, 1);
                                                                    player.getInventory().addItem(32251, 1);
                                                                    WorldBroadcasts.broadcast(player, BroadcastType.COMBAT_ACHIEVEMENTS, "Master");
                                                                } else {
                                                                    setKey(60);
                                                                }
                                                            } else {
                                                                if (grandmaster) {
                                                                    if (!player.getBooleanAttribute("Obtained grandmaster Ca Rewards")) {
                                                                        if (player.getInventory().hasSpaceFor(32266, 32249)) {
                                                                            player.putBooleanAttribute("Obtained grandmaster Ca Rewards", true);
                                                                            player.getInventory().addItem(32266, 1);
                                                                            player.getInventory().addItem(32249, 1);
                                                                            WorldBroadcasts.broadcast(player, BroadcastType.COMBAT_ACHIEVEMENTS, "Grandmaster");
                                                                        } else {
                                                                            setKey(60);
                                                                        }
                                                                    }
                                                                } else {
                                                                    setKey(50);
                                                                }
                                                            }
                                                        } else {
                                                            setKey(50);
                                                        }
                                                    }
                                                } else {
                                                    setKey(50);
                                                }
                                            }
                                        } else {
                                            setKey(50);
                                        }
                                    }
                                } else {
                                    setKey(50);
                                }
                            }
                        } else {
                            setKey(50);
                        }
                    });
                    player(40, "Can you give me a book please?");
                    npc("Here you go.").executeAction(() -> {
                        if (player.getInventory().hasFreeSlots()) {
                            player.getInventory().addItem(32239,1);
                        }
                    });
                    npc(50,"You have do not have any pending rewards.");
                    npc(60, "You need more inventory space to receive the rewards.");
                    player(70, "Can you give me your hilts back?");
                    npc("Sure, I will give you any hilts you deserve and you do not already own.").executeAction(() -> {
                        if (!player.containsItem(32259) && easy && player.getBooleanAttribute("Obtained easy Ca Rewards")) {
                            player.getInventory().addOrDrop(32259);
                        }
                        if (!player.containsItem(32257) && medium && player.getBooleanAttribute("Obtained medium Ca Rewards")) {
                            player.getInventory().addOrDrop(32257);
                        }
                        if (!player.containsItem(32255) && hard && player.getBooleanAttribute("Obtained hard Ca Rewards")) {
                            player.getInventory().addOrDrop(32255);
                        }
                        if (!player.containsItem(32253) && elite && player.getBooleanAttribute("Obtained elite Ca Rewards")) {
                            player.getInventory().addOrDrop(32253);
                        }
                        if (!player.containsItem(32251) && master && player.getBooleanAttribute("Obtained master Ca Rewards")) {
                            player.getInventory().addOrDrop(32251);
                        }
                        if (!player.containsItem(32249) && grandmaster && player.getBooleanAttribute("Obtained grandmaster Ca Rewards")) {
                            player.getInventory().addOrDrop(32249);
                        }
                    });
                    player(80, "Can you color my slayer helmet?").executeAction(() -> {
                        if (grandmaster) {
                            setKey(110);
                        } else if (master) {
                            setKey(100);
                        } else if (elite) {
                            setKey(90);
                        } else {
                            setKey(85);
                        }
                    });
                    npc(85, "You do not have any of the required combat achievements completed.");
                    npc(90, "Since you have the elite combat achievements completed I can recolor your helmet into a Tztok Slayer Helmet").executeAction(() -> {
                        if (player.getInventory().containsItem(11864)) {
                            player.getInventory().deleteItem(11864, 1);
                            player.getInventory().addItem(30901, 1);
                        } else if (player.getInventory().containsItem(11865)) {
                            player.getInventory().deleteItem(11865, 1);
                            player.getInventory().addItem(30903, 1);
                        } else {
                            setKey(115);
                        }
                    });
                    npc(100, "Since you have the master combat achievements completed I can recolor your helmet into a Tztok or Vampyric Slayer Helmet").executeAction(() -> {
                        if (player.getInventory().containsItem(11864)) {
                            setKey(120);
                        } else if (player.getInventory().containsItem(11865)) {
                           setKey(125);
                        } else {
                            setKey(115);
                        }
                    });
                    npc(110, "Since you have the grandmaster combat achievements completed I can recolor your helmet into a Tztok, Vampyric or Tzkal Slayer Helmet").executeAction(() -> {
                        if (player.getInventory().containsItem(11864)) {
                            setKey(130);
                        } else if (player.getInventory().containsItem(11865)) {
                            setKey(135);
                        } else {
                            setKey(115);
                        }
                    });
                    npc(115, "You do not have a Slayer Helmet or Slayer Helmet (i) in your inventory.");
                    options(120, TITLE, "Tztok slayer helmet", "Vampyric slayer helmet")
                            .onOptionOne(() -> {
                                player.getInventory().deleteItem(11864, 1);
                                player.getInventory().addItem(30901, 1);
                            })
                            .onOptionTwo(() -> {
                                player.getInventory().deleteItem(11864, 1);
                                player.getInventory().addItem(32243, 1);
                            });
                    options(125, TITLE, "Tztok slayer helmet (i)", "Vampyric slayer helmet (i)")
                            .onOptionOne(() -> {
                                player.getInventory().deleteItem(11865, 1);
                                player.getInventory().addItem(30903, 1);
                            })
                            .onOptionTwo(() -> {
                                player.getInventory().deleteItem(11865, 1);
                                player.getInventory().addItem(32241, 1);
                            });
                    options(130, TITLE, "Tztok slayer helmet", "Vampyric slayer helmet", "Tzkal slayer helmet")
                            .onOptionOne(() -> {
                                player.getInventory().deleteItem(11864, 1);
                                player.getInventory().addItem(30901, 1);
                            })
                            .onOptionTwo(() -> {
                                player.getInventory().deleteItem(11864, 1);
                                player.getInventory().addItem(32243, 1);
                            })
                            .onOptionThree(() -> {
                                player.getInventory().deleteItem(11864, 1);
                                player.getInventory().addItem(32245, 1);
                            });
                    options(135, TITLE, "Tztok slayer helmet (i)", "Vampyric slayer helmet (i)", "Tzkal slayer helmet (i)")
                            .onOptionOne(() -> {
                                player.getInventory().deleteItem(11865, 1);
                                player.getInventory().addItem(30903, 1);
                            })
                            .onOptionTwo(() -> {
                                player.getInventory().deleteItem(11865, 1);
                                player.getInventory().addItem(32241, 1);
                            })
                            .onOptionThree(() -> {
                                player.getInventory().deleteItem(11865, 1);
                                player.getInventory().addItem(32247, 1);
                            });
                }
            });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {2457};
    }
}
