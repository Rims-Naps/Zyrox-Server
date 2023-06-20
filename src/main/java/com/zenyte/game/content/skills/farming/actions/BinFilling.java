package com.zenyte.game.content.skills.farming.actions;

import com.zenyte.game.content.skills.farming.CompostBinType;
import com.zenyte.game.content.skills.farming.FarmingConstants;
import com.zenyte.game.content.skills.farming.FarmingSpot;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 23/02/2019 11:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class BinFilling extends Action {

    private static final Animation animation = new Animation(832);
    private static final SoundEffect sound = new SoundEffect(2441);

    private final FarmingSpot spot;
    private final Item item;

    @Override
    public boolean start() {
        val bin = spot.getCompostBin();
        val id = item.getId();
        if (id == FarmingConstants.VOLCANIC_ASH) {
            val type = bin.getType();
            if (!type.isPresent() || type.get() != CompostBinType.SUPERCOMPOST) {
                player.getDialogueManager().start(new PlainChat(player, "You can only apply volcanic ash to a bin " +
                        "containing super compost."));
                return false;
            }
            if (!bin.isFull()) {
                player.getDialogueManager().start(new PlainChat(player, "You can only apply volcanic ash to a bin " +
                        "filled with super compost."));
                return false;
            }
        } else if (bin.isFull()) {
            player.getDialogueManager().start(new PlainChat(player, "The compost bin must be emptied before you can " +
                    "put new items in it."));
            return false;
        }
        if (id == FarmingConstants.VOLCANIC_ASH) {
            val type = bin.getType().orElseThrow(RuntimeException::new);
            val array = type.getCompost();
            val value = spot.getValue();
            val binCount = bin.getAmount();
            if (value != array[binCount - 1]) {
                player.sendMessage("You can only add the volcanic ash when the vegetation has finished rotting.");
                return false;
            }
            val count = spot.getCompostBin().isBig() ? 50 : 25;
            if (!player.getInventory().containsItem(ItemId.VOLCANIC_ASH, count)) {
                player.sendMessage("You need at least " + count + " volcanic ash to fill the compost bin.");
                return false;
            }
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
        if (id == FarmingConstants.VOLCANIC_ASH) {
            val inventory = player.getInventory();
            inventory.deleteItem(new Item(id, spot.getCompostBin().isBig() ? 50 : 25));
            spot.getCompostBin().setType(CompostBinType.ULTRACOMPOST);
            val bin = spot.getCompostBin();
            val type = bin.getType().orElseThrow(RuntimeException::new);
            val array = type.getCompost();
            val count = bin.getAmount();
            spot.setValue(array[count - 1]);
            spot.refresh();
            return -1;
        }
        val inventory = player.getInventory();
        inventory.deleteItem(new Item(id, spot.addCompostableItem(new Item(id, id == FarmingConstants.WEEDS ? inventory.getAmountOf(id) : 1))));
        if (!inventory.containsItem(id, 1) || spot.getCompostBin().isFull()) {
            return -1;
        }
        play();
        return 1;
    }

    private void play() {
        player.setAnimation(animation);
        if (item.getId() != FarmingConstants.VOLCANIC_ASH) {
            player.sendSound(sound);
        }
    }
}
