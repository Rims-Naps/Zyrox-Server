package com.zenyte.game.content.theatreofblood.plugin.npc;

import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.MakeType;
import com.zenyte.plugins.dialogue.SkillDialogue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * @author Corey
 * @since 24/05/2020
 */
public class MysteriousStranger extends NPCPlugin {
    
    private static final String MYSTERIOUS_STRANGER_DIALOGUE_ATTRIBUTE = "tob_mysterious_stranger";
    
    public static boolean completedInitialDialogue(final Player player) {
        return player.getBooleanAttribute(MYSTERIOUS_STRANGER_DIALOGUE_ATTRIBUTE);
    }
    
    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.getDialogueManager().start(new Dialogue(player, npc.getId()) {
                    @Override
                    public void buildDialogue() {
                        if(TheatreOfBloodRaid.TOB_ENABLED) {
                            if (!completedInitialDialogue(player)) {
                                npc("You look like someone who can handle themselves. Are you intending to partake in the Theatre?");
                                player("Maybe?");
                                npc("You're wondering what's in it for you I suppose. The citizens of Meiyerditch come here looking for freedom, but I can tell you're no citizen.");
                                npc("Don't worry, you're not the first to sneak in. The vampyres here turn a blind eye to that sort of thing. Outsiders generally perform better in the Theatre so make for more interesting entertainment.");
                                npc("Even the outsiders don't survive though, no one does. You, however, might just have what it takes.");
                                player("Where are you going with this?");
                                npc("You're strong enough to survive the Theatre but you need a reason to try. I have a little proposition for you.");
                                player("Go on...");
                                npc("The Theatre is owned by Lady Verzik Vitur. I represent a party that has certain... interests in Verzik.");
                                npc("Enter the Theatre and beat the challenges within. Doing so would cause be quite the embarrassment for Verzik, something that my associates would very much appreciate.");
                                player("So you just need me to embarrass her? Doesn't sound too bad.");
                                npc("Be aware, this will be no easy challenge. I doubt you'll succeed alone. However, the vampyres will let you enter in a group up to five. I suggest you take advantage of this.");
                                npc("You can use the notice board to find suitable allies with whom to enter the Theatre.")
                                        .executeAction(() -> player.putBooleanAttribute(MYSTERIOUS_STRANGER_DIALOGUE_ATTRIBUTE, true));
                            } else {
                                npc("Any luck in the Theatre?");
                                options(TITLE,
                                        new DialogueOption("What am I meant to be doing again?", key(100)),
                                        new DialogueOption("I've managed to defeat her!", key(200)),
                                        new DialogueOption("")
                                );

                                {
                                    player(100, "What am I meant to be doing again?");
                                    npc("Enter the Theatre and beat the challenges within. Doing so would cause great embarrassment to Verzik and my associates would very much appreciate it.");
                                }

                                {
                                    player(200, "I've managed to defeat her! I survived every single one of Verzik's challenges!");
                                    if (player.getNumericAttribute("theatreofblood").intValue() > 0) {
                                        player("I've managed to defeat her! I survived every single one of Verzik's challenges!");
                                        player("At the end, she even chose to face me herself. I couldn't fully defeat her though, she just transformed into a bat and flew away.");
                                        npc("Impressive. Don't stop there though, return to the Theatre and embarrass her again. Keep it up and you will be rewarded.");
                                    } else {
                                        npc("Really?");
                                        player("No.");
                                        npc("I suggest you don't waste my time.");
                                    }
                                }
                            }
                        } else {
                            npc("What do you want?");
                            player("I was hoping you'd tell me about the Theatre of Blood or something.");
                            npc("Now's not the time. Soon you will have answers...");
                        }

                    }
                });
            }
        
            @Override
            public void execute(Player player, NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
            }
        });
        bind("Trade", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                if(TheatreOfBloodRaid.TOB_ENABLED) {
                    player.openShop("Mysterious Stranger");
                } else {
                    player.sendMessage("I guess they don't feel like trading right now...");
                }
            }
        
            @Override
            public void execute(Player player, NPC npc) {
                player.stopAll();
                player.setFaceEntity(npc);
                handle(player, npc);
            }
        });
        bind("Claim-cape", (player, npc) -> {
            final int killcount = player.getNumericAttribute("theatreofblood").intValue();
            if (killcount < 10) {
                player.getDialogueManager().start(new Dialogue(player, npc) {
                    @Override
                    public void buildDialogue() {
                        npc("I have no capes to give to you right now. Come talk to me whenever you've completed 10, 25, 75, 150 or 300 runs.");
                    }
                });
                return;
            }
            List<Item> list = new ObjectArrayList<>();
            list.add(new Item(ItemId.SINHAZA_SHROUD_TIER_1));
            if (killcount >= 25) {
                list.add(new Item(ItemId.SINHAZA_SHROUD_TIER_2));
            }
            if (killcount >= 75) {
                list.add(new Item(ItemId.SINHAZA_SHROUD_TIER_3));
            }
            if (killcount >= 150) {
                list.add(new Item(ItemId.SINHAZA_SHROUD_TIER_4));
            }
            if (killcount >= 300) {
                list.add(new Item(ItemId.SINHAZA_SHROUD_TIER_5));
            }

            player.getDialogueManager().start(new SkillDialogue(player, list.toArray(new Item[0])) {
                @Override
                public void run(final int slotId, final int amount) {
                    player.getInventory().addItem(list.get(slotId));
                    player.getCollectionLog().add(list.get(slotId));
                }

                @Override
                public final void buildDialogue() {
                    skill(1, MakeType.TAKE, "Select the cape to claim", items);
                }
            });
        });
    }
    
    @Override
    public int[] getNPCs() {
        return new int[]{NpcId.MYSTERIOUS_STRANGER_8325};
    }
    
}
