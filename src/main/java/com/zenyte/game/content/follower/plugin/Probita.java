package com.zenyte.game.content.follower.plugin;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.follower.InsurableVariablePet;
import com.zenyte.game.content.follower.PetInsurance;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnNPCAction;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import lombok.var;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.items.ItemDefinitions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * @author Kris | 27/11/2018 11:33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Probita extends NPCPlugin implements ItemOnNPCAction {
    public static final Map<Integer, Integer> insurablePets = new HashMap<>();
    private static final int INSURABLE_PETS_ENUM = 985;

    static {
        populateInsurablePets();
    }

    private static void populateInsurablePets() {
        EnumDefinitions.get(INSURABLE_PETS_ENUM).getValues().forEach((key, value) -> {
            val petId = (int) value;
            val variablePet = InsurableVariablePet.getPet(petId);
            if (variablePet != null) {
                for (int id : variablePet.getIds()) {
                    insurablePets.put(id, variablePet.getBaseId());
                }
            } else {
                insurablePets.put(petId, petId);
            }
        });
    }

    @Override
    public void handle() {
        bind("Check", (player, npc) -> GameInterface.PET_INSURANCE.open(player));
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    val basePetItemId = getBasePetId(player);
                    val basePetName = basePetItemId == -1 ? null : getPetName(basePetItemId);
                    npc("Welcome to the pet insurance bureau.<br><br>How can I help you?");
                    options(TITLE, "Tell me about pet insurance", "I've lost a pet. Have you got it?", "I have a pet that I'd like to insure.", "What pets have I insured?", "Maybe another time.")
                            .onOptionTwo(() -> setKey(10))
                            .onOptionThree(() -> setKey(15))
                            .onOptionFour(() -> setKey(50))
                            .onOptionFive(() -> setKey(60));
                    player(10, "I've lost a pet. Have you got it?");
                    npc("Sorry to hear! Let me check for you...");
                    if (player.getPetInsurance().getInsuredPets().isEmpty()) {
                        npc("I'm afraid you don't have any pets insured at the moment.");
                    } else {
                        npc("Here's an overview of your insured pet(s).").executeAction(() -> GameInterface.PET_INSURANCE.open(player));
                    }
                    player(15, "I have a pet that I'd like to insure.");
                    val canInsureForFree = player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER) && player.getPetInsurance().getInsuredPets().size() >= 2;
                    if (canInsureForFree) {
                        npc("Great! There is no fee for you to insure any more pets, as you have insured at least two of them with me and are a dragonstone member or above. Just hand me the pet so " +
                                "I " +
                                "can register it.");
                        npc("Once you've insured the pet, you can reclaim it here unlimited times for a reclamation fee of " +
                                (player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER) ? "nothing." : (Utils.format(PetInsurance.RECLAIM_PRICE.getAmount()) + " coins."))).executeAction(() -> {
                            if (basePetItemId == -1) {
                                setKey(70);
                                return;
                            }
                            setKey(20);
                        });
                    } else {
                        npc("Great! The insurance fee is " + Utils.format(PetInsurance.INSURANCE_PRICE.getAmount()) + " coins. Just hand<br><br>me the pet so I can register it.");
                        npc("My insurance fee is " + Utils.format(PetInsurance.INSURANCE_PRICE.getAmount()) + " coins. Once you've paid that, the pet's insured forever, and you can reclaim it here unlimited times for a reclamation fee of " +
                                (player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER) ? "nothing." : (Utils.format(PetInsurance.RECLAIM_PRICE.getAmount()) + " coins."))).executeAction(() -> {
                            if (basePetItemId == -1) {
                                setKey(70);
                                return;
                            }
                            if (!player.getInventory().containsItem(PetInsurance.INSURANCE_PRICE)) {
                                setKey(75);
                                return;
                            }
                            setKey(20);
                        });
                    }
                    npc(70, "It seems that you don't have any pets in your inventory that need insurance right now. Come back whenever you do!");
                    if (basePetItemId >= 0) {
                        npc(75, "I'm afraid you don't have enough gold to insure the <col=00080>" + basePetName + "</col> pet.");
                        options(20, "<col=00080>" + basePetName + "</col>: " +
                                (canInsureForFree ? "Insure for free?" : ("Insure for " + Utils.format(PetInsurance.INSURANCE_PRICE.getAmount()) + " coins?")), "Yes.", "No.").onOptionOne(() -> {
                            setKey(25);
                            if (!canInsureForFree) {
                                player.getInventory().deleteItem(PetInsurance.INSURANCE_PRICE);
                            }
                            player.getPetInsurance().insurePet(basePetItemId);
                        }).onOptionTwo(() -> setKey(30));
                        item(25, new Item(basePetItemId), "Your pet is now insured. You can reclaim it from Probita if you ever lose it.");
                    }
                    player(30, "No.");
                    npc("Come back when you change your mind!");
                    player(50, "What pets have I insured?").executeAction(() -> GameInterface.PET_INSURANCE.open(player));
                    player(60, "Maybe another time.");
                }
            });
        });
    }

    private int getBasePetId(final Player player) {
        var currentPetItemId = PetWrapper.fromInventory(player);
        return insurablePets.getOrDefault(currentPetItemId, -1);
    }

    private String getPetName(final int itemId) {
        val itemName = ItemDefinitions.getOrThrow(itemId).getName();
        return TextUtils.capitalize(itemName.replaceAll("Pet", "").trim());
    }

    @Override
    public int[] getNPCs() {
        return new int[]{NpcId.PROBITA};
    }

    @Override
    public void handleItemOnNPCAction(Player player, Item item, int slot, NPC npc) {
        val petItemId = insurablePets.getOrDefault(item.getId(), -1);
        if (petItemId == -1) {
            throw new IllegalStateException();
        }
        val basePetName = getPetName(petItemId);
        player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                if (player.getPetInsurance().isInsured(petItemId)) {
                    npc("You've already insured that pet.");
                    return;
                }
                val canInsureForFree = player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER) && player.getPetInsurance().getInsuredPets().size() >= 2;
                if (!canInsureForFree && !player.getInventory().containsItem(PetInsurance.INSURANCE_PRICE)) {
                    npc("I'm afraid you don't have enough gold to insure the <col=00080>" + basePetName + "</col> pet.");
                    return;
                }

                options("<col=00080>" + basePetName + "</col>: " +
                        (canInsureForFree ? "Insure for free?" : ("Insure for " + Utils.format(PetInsurance.INSURANCE_PRICE.getAmount()) + " coins?")), "Yes.", "No.").onOptionOne(() -> {
                    setKey(25);
                    if (!canInsureForFree) {
                        player.getInventory().deleteItem(PetInsurance.INSURANCE_PRICE);
                    }
                    player.getPetInsurance().insurePet(petItemId);
                }).onOptionTwo(() -> setKey(30));
                item(25, new Item(petItemId), "Your pet is now insured. You can reclaim it from Probita if you ever lose it.");
                player(30, "No.");
                npc("Come back when you change your mind!");
            }
        });
    }

    @Override
    public Object[] getItems() {
        val integerSet = new HashSet<Integer>();
        integerSet.addAll(insurablePets.keySet());
        integerSet.addAll(insurablePets.values());
        return integerSet.toArray();
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{
                NpcId.PROBITA
        };
    }
}