package com.zenyte.game.content.theatreofblood.party;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.interfaces.PartiesOverviewInterface;
import com.zenyte.game.content.theatreofblood.interfaces.PartyOverlayInterface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.util.*;

/**
 * @author Tommeh | 5/21/2020 | 7:32 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public class RaidingParty {

    private final int id;
    private final List<String> originalMembers;
    private final List<String> members;
    private final List<String> applicants;
    private final Set<String> blocked;
    private final Map<Player, Integer> statuses;
    private final Map<String, String> lifeStates;
    @Getter
    @Setter
    private boolean wiped = false;
    @Getter
    @Setter
    private boolean practice = false;

    private final Date creationTimeStamp = new Date();
    @Setter
    private TheatreOfBloodRaid raid;
    @Setter
    private int preferredSize;
    @Setter
    private int preferredCombatLevel = 3;

    public RaidingParty(final int id, final Player leader) {
        this.id = id;
        originalMembers = new ArrayList<>(5);
        members = new ArrayList<>(5);
        originalMembers.add(leader.getUsername());
        members.add(leader.getUsername());
        applicants = new ArrayList<>(10);
        blocked = new HashSet<>(2047);
        statuses = new HashMap<>(5);
        lifeStates = new HashMap<>(5);
    }

    public Player getMember(final int index) {
        return getPlayer(members.get(index));
    }

    public List<Player> getPlayers() {
        val players = new ArrayList<Player>();
        for (String memberName : members) {
            val member = getPlayer(memberName);
            if (member == null) {
                continue;
            }
            players.add(member);
        }
        return players;
    }

    public Player getLeader() {
        if (members.isEmpty()) {
            return null;
        }

        return getPlayer(members.get(0));
    }

    public static Player getPlayer(final String username) {
        val optionalPlayer = World.getPlayer(username);

        return optionalPlayer.orElse(null);
    }

    public void removeMember(final Player player) {
        members.remove(player.getUsername());

        if (members.isEmpty()) {
            VerSinhazaArea.removeParty(this);
            for (val a : applicants) {
                val applicant = getPlayer(a);
                if (applicant == null) {
                    continue;
                }
                if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    PartiesOverviewInterface.refresh(applicant);
                }
            }
        } else {
            if (raid != null) {
                members.forEach(m -> {
                    val member = getPlayer(m);
                    if (member == null) {
                        return;
                    }
                    initializeStatusHUD(member);
                    updateStatusHUD(member);
                });
                raid.getSpectators().forEach(s -> {
                    val spectator = getPlayer(s);
                    if (spectator == null) {
                        return;
                    }
                    initializeStatusHUD(spectator);
                    updateStatusHUD(spectator);
                });
            } else {
                for (val m : members) {
                    val member = getPlayer(m);
                    if (member == null) {
                        continue;
                    }
                    if (member.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                        updateInformation(member);
                    }
                    PartyOverlayInterface.refresh(member, this);
                }
                for (val a : applicants) {
                    val applicant = getPlayer(a);
                    if (applicant == null) {
                        continue;
                    }
                    if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                        updateInformation(applicant);
                    }
                }
            }
        }

        if (player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
            PartiesOverviewInterface.refresh(player);
        }
        PartyOverlayInterface.refresh(player, null);
    }

    public void addApplicant(final Player player) {
        applicants.add(player.getUsername());
        for (val entry : VerSinhazaArea.getParties().entrySet()) {
            val p = entry.getValue();
            if (p == this) {
                continue;
            }
            p.getApplicants().remove(player.getUsername());
            for (val m : p.getMembers()) {
                val member = getPlayer(m);
                if (member == null) {
                    continue;
                }
                if (member.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                    p.updateInformation(member);
                }
            }
        }
        if (getLeader().getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
            updateInformation(getLeader());
        }
        updateInformation(player);
    }

    public List<String> getAliveMembers() {
        val alive = new ArrayList<String>();
        for (String memberName : members) {
            val member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive")) {
                continue;
            }
            alive.add(member.getUsername());
        }
        //System.out.println("Alive:" + members.size());
        return alive;
    }

    public List<String> getTargetableMembers() {
        val targetable = new ArrayList<String>();
        for (String memberName : members) {
            val member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive") || !getRaid().getActiveRoom().inCombatZone(member.getX(), member.getY())) {
                continue;
            }
            targetable.add(member.getUsername());
        }
        //System.out.println("Alive:" + members.size());
        return targetable;
    }

    public List<Player> getAlivePlayers() {
        val targetable = new ArrayList<Player>();
        for (String memberName : members) {
            val member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive")) {
                continue;
            }
            targetable.add(member);
        }
        return targetable;
    }

    public List<Player> getTargetablePlayers() {
        val alive = new ArrayList<Player>();
        for (String memberName : members) {
            val member = getPlayer(memberName);
            if (member == null || !lifeStates.get(memberName).equals("alive") || !getRaid().getActiveRoom().inCombatZone(member.getX(), member.getY())) {
                continue;
            }
            alive.add(member);
        }
        return alive;
    }

    public void disband() {
        for (val m : members) {
            val member = getPlayer(m);
            if (member == null) {
                continue;
            }
            member.sendMessage("Your party has disbanded.");
            if (member.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                PartiesOverviewInterface.refresh(member);
            }
            PartyOverlayInterface.refresh(member, null);
        }

        for (val a : applicants) {
            val applicant = getPlayer(a);
            if (applicant == null) {
                continue;
            }
            if (applicant.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY_INFORMATION)) {
                PartiesOverviewInterface.refresh(applicant);
            }
        }
    }

    public boolean isLeader(final Player player) {
        return getLeader() == player;
    }

    public int getAge() {
        return (int) ((Utils.currentTimeMillis() - creationTimeStamp.getTime()) / 600);
    }

    public int getSize() {
        return members.size();
    }

    public PartyRights getRights(final Player player) {
        if (isLeader(player)) {
            return PartyRights.LEADER;
        } else if (members.contains(player.getUsername())) {
            return PartyRights.PARTY_MEMBER;
        } else if (applicants.contains(player.getUsername())) {
            return PartyRights.APPLICANT;
        } else if (blocked.contains(player.getUsername())) {
            return PartyRights.BLOCKED_APPLICANT;
        }
        return PartyRights.CAN_APPLY;
    }

    public void updateInformation(final Player player) {
        val dispatcher = player.getPacketDispatcher();
        player.addTemporaryAttribute("tob_viewing_party_id", id);

        GameInterface.TOB_PARTY_INFORMATION.open(player);
        player.getPacketDispatcher().sendClientScript(2524, -1, -1);
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                dispatcher.sendClientScript(2317, 2, "");
                continue;
            }
            val member = getMember(index);

            val builder = new StringBuilder();
            builder.append(member.getName() + "|");
            builder.append(member.getCombatLevel() + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.ATTACK) + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.STRENGTH) + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.RANGED) + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.MAGIC) + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.DEFENCE) + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.HITPOINTS) + "|");
            builder.append(member.getSkills().getLevelForXp(Skills.PRAYER) + "|");
            builder.append("0|");

            dispatcher.sendClientScript(2317, 2, builder.toString());
        }

        if (members.contains(player.getUsername()) || applicants.contains(player.getUsername())) {
            for (val a : applicants) {
                val applicant = getPlayer(a);
                if (applicant == null) {
                    continue;
                }

                val builder = new StringBuilder();
                builder.append(applicant.getName() + "|");
                builder.append(applicant.getCombatLevel() + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.ATTACK) + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.STRENGTH) + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.RANGED) + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.MAGIC) + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.DEFENCE) + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.HITPOINTS) + "|");
                builder.append(applicant.getSkills().getLevelForXp(Skills.PRAYER) + "|");
                builder.append("0|");

                dispatcher.sendClientScript(2321, builder.toString());
            }
        }
        dispatcher.sendClientScript(2323, getRights(player).getId(), preferredSize, preferredCombatLevel);
    }

    public void initializeStatusHUD(final Player player) {
        val arguments = new Object[5];
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                arguments[index] = "";
                continue;
            }
            val member = getMember(index);
            arguments[index] = member.getName();
        }
        if (!player.getInterfaceHandler().isPresent(GameInterface.TOB_PARTY)) {
            GameInterface.TOB_PARTY.open(player);
        }
        player.getVarManager().sendBit(6440, 2);
        player.getVarManager().sendBit(6441, raid.getSpectators().contains(player.getUsername()) ? 0 : members.indexOf(player.getUsername()) + 1);
        player.getPacketDispatcher().sendClientScript(2301, arguments);
        updateStatusHUD(player);
    }

    public void updateStatusHUD(final boolean force) {
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                for (val m : members) {
                    val member = getPlayer(m);
                    if (member == null) {
                        continue;
                    }
                    if (member.getVarManager().getBitValue(6442 + index) != 0) {
                        member.getVarManager().sendBit(6442 + index, 0);
                    }
                }
                for (val s : raid.getSpectators()) {
                    val spectator = getPlayer(s);
                    if (spectator == null) {
                        continue;
                    }
                    if (spectator.getVarManager().getBitValue(6442 + index) != 0) {
                        spectator.getVarManager().sendBit(6442 + index, 0);
                    }
                }
                continue;
            }
            val m = members.get(index);
            if (m == null) {
                continue;
            }
            val member = getPlayer(m);
            if (member == null) {
                continue;
            }

            val previousStatus = statuses.getOrDefault(member, -1);
            val currentStatus = getStatus(member);
            if (previousStatus != currentStatus || force) {
                for (val o : members) {
                    val otherMember = getPlayer(o);
                    if (otherMember == null) {
                        continue;
                    }
                    otherMember.getVarManager().sendBit(6442 + index, currentStatus == 31 ? 31 : otherMember == member ? 1 : currentStatus);
                }
                for (val s : raid.getSpectators()) {
                    val spectator = getPlayer(s);
                    if (spectator == null) {
                        continue;
                    }
                    spectator.getVarManager().sendBit(6442 + index, currentStatus);
                }
                statuses.put(member, currentStatus);
            }
        }
    }

    private void updateStatusHUD(final Player player) {
        for (int index = 0; index < 5; index++) {
            if (index >= members.size()) {
                if (player.getVarManager().getBitValue(6442 + index) != 0) {
                    player.getVarManager().sendBit(6442 + index, 0);
                }
                continue;
            }
            val m = members.get(index);
            if (m == null) {
                continue;
            }
            val member = getPlayer(m);
            if (member == null) {
                continue;
            }

            val status = statuses.getOrDefault(member, getStatus(member));
            player.getVarManager().sendBit(6442 + index, player == member ? 1 : status);
        }
    }

    public int getStatus(final Player member) {
        if (raid.getActiveRoom() != member.getArea() && !member.getBooleanTemporaryAttribute("tob_advancing_room")) {
            return 31;
        } else if (member.isDead() || getLifeStates().get(member.getUsername()).equals("dead")) {
            return 30;
        }
        val maxHp = Math.min(99, member.getMaxHitpoints());
        val currentHp = Math.min(99, member.getHitpoints());
        val fraction = currentHp / (double) maxHp;
        return (int) (1 + Math.round(26 * fraction));
    }

    public Player getRandomPlayer() {
        if (members.isEmpty()) {
            return null;
        }
        return getPlayer(members.get(Utils.random(members.size() - 1)));
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof RaidingParty)) {
            return false;
        }
        return ((RaidingParty) obj).getId() == id;
    }

    @Override
    public String toString() {
        return "RaidingParty(id=" + id + ", members=" + members + ")";
    }
}
