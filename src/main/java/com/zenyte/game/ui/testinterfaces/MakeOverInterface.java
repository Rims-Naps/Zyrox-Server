package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Appearance;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.MakeOverMageD;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 28-10-2018 | 19:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MakeOverInterface extends Interface {

    @Override
    protected void attach() {
        put(2, "Select male");
        put(6, "Select female");
        put(9, "Select skin colour");
        put(10, "Finish make-over");
    }

    @Override
    public void open(Player player) {
        player.getTemporaryAttributes().remove("SelectedGender");
        player.getTemporaryAttributes().remove("SelectedSkinColour");
        player.getVarManager().sendVar(261, player.getAppearance().isMale() ? 0 : 1);
        player.getVarManager().sendVar(262, player.getAppearance().getColours()[4]);
        player.getVarManager().sendBit(4803, 1);
        player.getVarManager().sendBit(4804, 1);
        player.getVarManager().sendBit(6007, 1);
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getPacketDispatcher().sendComponentText(getInterface(), getComponent("Finish make-over"), "CONFIRM (" + MakeOverMageD.PRICE.getAmount() + " coins)");
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Select skin colour"), 0, 12, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Select skin colour", (player, slotId, itemId, option) ->  {
            val value = slotId == 0 ? 7 : slotId >= 8 && slotId <= 12 ? slotId : slotId - 1;
            player.getVarManager().sendVar(262, value);
            player.getTemporaryAttributes().put("SelectedSkinColour", value);
        });
        bind("Finish make-over", player ->  {
            val male = player.getBooleanTemporaryAttribute("SelectedGender");
            val skinColour = player.getNumericTemporaryAttributeOrDefault("SelectedSkinColour", (int) player.getAppearance().getColours()[4]).intValue();
            if (male == player.getAppearance().isMale() && skinColour == player.getAppearance().getColours()[4]) {
                player.sendMessage("You haven't changed anything yet.");
                return;
            }
            val colour = SkinColour.get(skinColour);
            if (colour != null && !player.getMemberRank().eligibleTo(colour.getRank())) {
                player.sendMessage("You need to be " + Utils.getAOrAn(colour.getRank().toString()) + " " + colour.getRank() + " to change into this skin colour!");
                return;
            }
            player.getAppearance().modifyColour((byte) 4, (byte) skinColour);
            player.getInventory().deleteItem(MakeOverMageD.PRICE);
            player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.MAKEOVER;
    }

    @Getter
    @RequiredArgsConstructor
    private enum SkinColour {
        BLACK(9, MemberRank.EMERALD_MEMBER),
        WHITE(10, MemberRank.EMERALD_MEMBER),
        GREEN(8, MemberRank.RUBY_MEMBER),
        TURQOISE(11, MemberRank.DIAMOND_MEMBER),
        PURPLE(12, MemberRank.DRAGONSTONE_MEMBER);

        private final int index;
        private final MemberRank rank;

        private static final SkinColour[] all = values();
        private static final Map<Integer, SkinColour> map = new HashMap<>(all.length);

        public static SkinColour get(final int index) {
            return map.get(index);
        }

        static {
            for (val colour : all) {
                map.put(colour.index, colour);
            }
        }
    }
}
