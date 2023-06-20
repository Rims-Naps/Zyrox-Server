package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.achievementdiary.diaries.ArdougneDiary;
import com.zenyte.game.content.follower.InsurableVariablePet;
import com.zenyte.game.content.follower.PetInsurance;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Examine;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import lombok.val;
import lombok.var;

/**
 * @author Tommeh | 24-11-2018 | 13:50
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class PetInsuranceInterface extends Interface {

    @Override
    protected void attach() {
        put(7, "Examine Insured Pet");
        put(14, "Reclaim Insured Pet");
    }

    @Override
    public void open(Player player) {
        long insureValue = 0, reclaimValue = 0;
        val e = EnumDefinitions.getIntEnum(985);
        loop : for (val pet : player.getPetInsurance().getInsuredPets()) {
            if (pet == null) {
                continue;
            }
            var p = e.getKey(pet.itemId());
            if (p.isPresent()) {
                insureValue |= 1L << p.getAsInt();
            }
            if (pet.hasPet(player)) {
                continue;
            }
            val variablePet = InsurableVariablePet.getPet(pet.itemId());
            if (variablePet != null) {
                for (val variableId : variablePet.getIds()) {
                    val alternativePet = PetWrapper.getByItem(variableId);
                    if (alternativePet == null) {
                        continue;
                    }
                    if (alternativePet.hasPet(player)) {
                        continue loop;
                    }
                }
                val parent = variablePet.getParentId();
                if (parent.isPresent()) {
                    p = e.getKey(parent.getAsInt());
                    if (p.isPresent()) {
                        reclaimValue |= 1L << p.getAsInt();
                    }
                }
                continue;
            }

            p = e.getKey(pet.itemId());
            if (p.isPresent()) {
                reclaimValue |= 1L << p.getAsInt();
            }
        }
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Examine Insured Pet"), 0, 100, AccessMask.CLICK_OP10);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Reclaim Insured Pet"), 0, 100, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
        player.getPacketDispatcher().sendClientScript(202, (int) (reclaimValue & 0x7FFFFFFF), (int) (reclaimValue >> 31));
        player.getVarManager().sendVar(1417, (int) (insureValue >> 32));
        player.getVarManager().sendVar(866, (int) insureValue);
        player.getAchievementDiaries().update(ArdougneDiary.CHECK_INSURED_PETS);
    }

    @Override
    protected void build() {
        bind("Examine Insured Pet", (player, slotId, itemId, option) -> Examine.sendItemExamine(player, itemId));
        bind("Reclaim Insured Pet", (player, slotId, itemId, option) -> {
            val insurance = player.getPetInsurance();
            val pet = PetWrapper.getByItem(itemId);
            if (pet == null || !insurance.isInsured(pet)) {
                return;
            }
            if (!player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER)) {
                if (!player.getInventory().containsItem(PetInsurance.RECLAIM_PRICE)) {
                    player.getDialogueManager().start(new NPCChat(player, 5906, "I'm afraid you don't have enough gold to reclaim the <col=00080>" + NPCDefinitions.get(pet.petId()).getName() + "</col> pet."));
                    return;
                }
            }
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    options("Would you like to reclaim the <col=00080>" + NPCDefinitions.getOrThrow(pet.petId()).getName() + "</col> pet for " +
                            (player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER) ? "free?" : (Utils.format(PetInsurance.RECLAIM_PRICE.getAmount()) + " coins?")), "Yes.", "Nevermind.")
                            .onOptionOne(() -> {
                                if (!player.getInventory().checkSpace()) {
                                    setKey(5);
                                } else {
                                    setKey(10);
                                    if (!player.getMemberRank().eligibleTo(MemberRank.DIAMOND_MEMBER)) {
                                        player.getInventory().deleteItem(PetInsurance.RECLAIM_PRICE);
                                    }
                                    player.getInventory().addItem(pet.itemId(), 1);
                                    GameInterface.PET_INSURANCE.open(player);
                                }
                            })
                            .onOptionTwo(() -> setKey(15));
                    npc(5906, "I'm afraid you don't have enough inventory space.", 5);
                    npc(5906, "Here's your pet!", 10);
                    player(15, "Nevermind.");
                }
            });
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.PET_INSURANCE;
    }
}
