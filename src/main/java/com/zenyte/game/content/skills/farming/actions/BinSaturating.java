package com.zenyte.game.content.skills.farming.actions;

import com.zenyte.game.content.skills.farming.CompostBinType;
import com.zenyte.game.content.skills.farming.FarmingSpot;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 22/05/2019 13:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class BinSaturating extends Action {

    private static final Animation animation = new Animation(2569);
    private static final SoundEffect sound = new SoundEffect(2447);

    private final FarmingSpot spot;
    private final Item item;

    @Override
    public boolean start() {
        val bin = spot.getCompostBin();
        val type = bin.getType();
        if (!type.isPresent() || type.get() != CompostBinType.COMPOST) {
            player.getDialogueManager().start(new PlainChat(player, "You can only apply compost potion to a bin " +
                    "containing normal compost."));
            return false;
        }
        play();
        delay(2);
        return true;
    }

    @Override
    public boolean process() {
        return true;
    }

    @Override
    public int processWithDelay() {
        val id = item.getId();
        val inventory = player.getInventory();
        inventory.deleteItem(new Item(id, 1));
        inventory.addOrDrop(new Item((id + 2) == 6478 ? 229 : (id + 2), 1));
        spot.getCompostBin().setType(CompostBinType.SUPERCOMPOST);
        val bin = spot.getCompostBin();
        val type = bin.getType().orElseThrow(RuntimeException::new);
        val array = type.getCompost();
        val count = bin.getAmount();
        spot.setValue(array[count - 1]);
        spot.refresh();
        return -1;
    }

    private void play() {
        player.setAnimation(animation);
        player.sendSound(sound);
    }
}
