package com.zenyte.game.world.entity.player.container.impl.bank;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 01/03/2019 18:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BankPinSettingsInterface extends Interface {

    private static final SoundEffect sound = new SoundEffect(1040);

    @Override
    protected void attach() {
        put(6, "Pin status");
        put(8, "Recovery delay");
        put(10, "Pin lock on login");
        put(14, "Message");
        put(0, "Interface base");
        put(28, "Set PIN options");
        put(18, "PIN pending options");
        put(21, "Pin enabled options");
        put(26, "Cancel PIN option");
    }

    @Override
    public void open(Player player) {
        val disp = player.getPacketDispatcher();
        disp.sendComponentText(getInterface(), getComponent("Pin status"), "You have a PIN");
        disp.sendComponentText(getInterface(), getComponent("Recovery delay"), "3 days");
        disp.sendComponentText(getInterface(), getComponent("Pin lock on login"), "Always lock");
        disp.sendComponentText(getInterface(), getComponent("Message"), "Customers are reminded that they should " +
                "NEVER tell anyone their Bank PINs or passwords, nor should they ever enter their PINs on any website form.");
        player.getInterfaceHandler().sendInterface(getInterface());
        disp.sendComponentVisibility(getInterface(), getComponent("Interface base"), false);

        disp.sendComponentVisibility(getInterface(), getComponent("Set PIN options"), true);
        disp.sendComponentVisibility(getInterface(), getComponent("PIN pending options"), true);
        disp.sendComponentVisibility(getInterface(), getComponent("Pin enabled options"), false);
        disp.sendComponentVisibility(getInterface(), getComponent("Cancel PIN option"), true);
        player.sendSound(sound);
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.BANK_PIN_SETTINGS;
    }
}
