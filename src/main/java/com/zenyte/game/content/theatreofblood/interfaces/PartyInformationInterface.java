package com.zenyte.game.content.theatreofblood.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;

import java.util.HashSet;

/**
 * @author Tommeh | 5/21/2020 | 7:36 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PartyInformationInterface extends Interface {

    private static final SoundEffect rejectSound = new SoundEffect(2277);

    @Override
    protected void attach() {
        put(0, 0, "Back");
        put(0, 5, "Apply/Disband/Leave/Withdraw");
        put(8, "Refresh");
        put(26, "Kick/Quit Party");
        put(41, "Accept/Reject");
        put(46, "Unblock");
        put(47, "Set preferred size");
        put(51, "Set preferred combat level");
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Refresh"), -1, 1, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Accept/Reject"), -1, 500, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Kick/Quit Party"), -1, 500, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Set preferred size"), -1, 1, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Set preferred combat level"), -1, 1, AccessMask.CLICK_OP1);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Unblock"), -1, 1, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Back", PartiesOverviewInterface::refresh);
        bind("Refresh", player -> {
            val party = VerSinhazaArea.getParty(player, true);
            if (party == null) {
                return;
            }
            party.updateInformation(player);
        });
        bind("Apply/Disband/Leave/Withdraw", player -> {
            val party = VerSinhazaArea.getParty(player, false, true, true);
            if (party == null) {
                return;
            }
            switch (party.getRights(player)) {
                case LEADER:
                    VerSinhazaArea.removeParty(party);
                    party.disband();
                    break;
                case PARTY_MEMBER:
                    party.removeMember(player);
                    break;
                case APPLICANT:
                    party.getApplicants().remove(player.getUsername());
                    party.updateInformation(player);
                    if (party.getLeader().getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                        party.updateInformation(party.getLeader());
                    }
                    break;
                case CAN_APPLY:
                    val currentParty = VerSinhazaArea.getParty(player);
                    if (currentParty != null) {
                        player.getInterfaceHandler().closeInterface(getInterface());
                        player.getDialogueManager().start(new Dialogue(player) {
                            @Override
                            public void buildDialogue() {
                                options("You are already in a party.", "Stay in my existing party.", "Quit that one and apply to this one.")
                                        .onOptionOne(() -> party.updateInformation(player))
                                        .onOptionTwo(() -> {
                                            currentParty.removeMember(player);
                                            party.addApplicant(player);
                                            party.updateInformation(player);
                                        });
                            }
                        });
                        player.sendMessage("You cannot apply for this party if you're already in another one.");
                        return;
                    }
                    if (party.getApplicants().size() == 10) {
                        player.sendMessage("There are too many applicants for this party at the moment.");
                        return;
                    }
                    if (party.getBlocked().contains(player.getUsername())) {
                        player.sendMessage("You have already applied for this party. Wait until the party leader either accepts or rejects it.");
                        return;
                    }
                    if (party.getApplicants().contains(player.getUsername())) {
                        return;
                    }
                    party.addApplicant(player);
                    break;
                case BLOCKED_APPLICANT:
                    player.getInterfaceHandler().closeInterfaces();
                    player.getDialogueManager().start(new PlainChat(player, "You have been declined by this party."));
                    break;

            }
        });
        bind("Accept/Reject", (player, slotId, itemId, option) -> {
            val index = slotId / 20;
            val party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (index >= party.getApplicants().size()) {
                return;
            }
            val username = party.getApplicants().get(index);
            val applicant = RaidingParty.getPlayer(username);
            if (applicant == null) {
                return;
            }
            if (option == 1) {
                if (party.getMembers().size() == 5) {
                    player.sendMessage("Your raiding party is already full.");
                    return;
                }
                if (VerSinhazaArea.getParty(applicant) != null) {
                    party.getApplicants().remove(applicant.getUsername());
                    player.getInterfaceHandler().closeInterface(getInterface());
                    player.getDialogueManager().start(new PlainChat(player, applicant.getName() + " appears to have joined another party."));
                    return;
                }
                if (party.getApplicants().remove(applicant.getUsername())) {
                    party.getMembers().add(applicant.getUsername());
                    party.getOriginalMembers().add(applicant.getUsername());
                }
                party.getMembers().forEach(m -> {
                    val member = RaidingParty.getPlayer(m);
                    if (member == null) {
                        return;
                    }
                    PartyOverlayInterface.refresh(member, party);
                });
            } else {
                applicant.sendMessage("Your application to the party of " + party.getLeader().getName() + " has been declined");
                applicant.sendSound(rejectSound);
                party.getBlocked().add(applicant.getUsername());
                party.getApplicants().remove(applicant.getUsername());
            }
            if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                party.updateInformation(applicant);
            }
            if (player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                party.updateInformation(player);
            }
        });
        bind("Set preferred size", player -> {
            val party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            player.getInterfaceHandler().closeInterfaces();
            player.sendInputInt("Set a preferred party size (or 0 to clear it)", s -> {
                val size = Math.min(5, s);
                party.setPreferredSize(size);
                party.updateInformation(player);
            });
        });
        bind("Set preferred combat level", player -> {
            val party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            player.getInterfaceHandler().closeInterfaces();
            player.sendInputInt("Set a preferred combat level (or 0 to clear it)", l -> {
                val level = Math.min(126, l);
                party.setPreferredCombatLevel(level);
                party.updateInformation(player);
            });
        });
        bind("Unblock", player -> {
            val party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            val usernames = new HashSet<>(party.getBlocked());
            party.getBlocked().clear();
            for (val username : usernames) {
                val p = RaidingParty.getPlayer(username);
                if (p == null) {
                    continue;
                }
                if (p.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    party.updateInformation(p);
                }
            }
        });
        bind("Kick/Quit Party", (player, slotId, itemId, option) -> {
            val party = VerSinhazaArea.getParty(player);
            if (party == null) {
                return;
            }
            if (!party.isLeader(player)) {
                return;
            }
            if (slotId == 0) {
                if (party.getMembers().size() == 1) {
                    VerSinhazaArea.removeParty(party);
                    party.disband();
                } else {
                    val member = party.getMember(0);
                    if (member == null) {
                        return;
                    }
                    party.removeMember(member);
                }
                return;
            }
            val index = slotId / 11;
            if (index > party.getMembers().size()) {
                return;
            }
            val member = party.getMember(index);
            if (member == null) {
                return;
            }
            party.removeMember(member);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TOB_PARTY_INFORMATION;
    }
}
