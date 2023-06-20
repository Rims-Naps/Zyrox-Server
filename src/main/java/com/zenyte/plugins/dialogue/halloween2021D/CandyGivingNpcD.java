package com.zenyte.plugins.dialogue.halloween2021D;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

public class CandyGivingNpcD extends Dialogue {

    public CandyGivingNpcD(Player player, NPC npc) {
        super(player, npc.getId(), npc);
    }

    @Override
    public void buildDialogue() {
        {
            player( "Trick or Treat!", Expression.LAUGH);
            npc("Oh, do you want some candy?"); //so apparently each time
            player("Yes please!");
            npc("And what are you supposed to be dressed as?");
            player("A ummm.... uuh.... a...");
            npc("Well?");
            options(new DialogueOption("A chicken!", key(200)), new DialogueOption("A ghost... boo!", key(300)), new DialogueOption("A dragon!", key(400)), new DialogueOption("A horse!", key(500)));
        }

        {
            player(200, "A chicken!");
            if(inChickenOutfit(player)) {
                npc("Yeah, you kinda do look like a chicken!");
                npc("Here, have some candy!").executeAction(() -> giveCandy(player));
            } else {
                npc("I don't see the resemblance...!");
                npc("I guess you can have some candy anyways!").executeAction(() -> giveCandy(player));
            }
        }

        {
            player(300, "A ghost... boo!");
            npc("OOhhhh... spooky!");
            npc("Here's some candy, stay spooky!!").executeAction(() -> giveCandy(player));
        }

        {
            player(400, "A dragon!");
            if(wearingDragonMask(player)) {
                npc("Wow, scary! Take my candy and don't hurt me!").executeAction(() -> giveCandy(player));
            } else {
                npc("Alright.... sure I guess... anyways, here's that candy you wanted.").executeAction(() -> giveCandy(player));
            }
        }

        {
            player(500, "A horse!");
            npc("A what now?");
            player("Erm... I'm not sure.", Expression.ANNOYED);
            npc("Well alright... Do you still want the candy?");
            player("Sure! I like candy!", Expression.HAVE_FUN).executeAction(() -> giveCandy(player));
        }

    }

    public boolean wearingDragonMask(Player p) {
        return player.getEquipment().isWearing(new Item(ItemId.BLACK_DRAGON_MASK)) || player.getEquipment().isWearing(new Item(ItemId.BLUE_DRAGON_MASK)) || player.getEquipment().isWearing(new Item(ItemId.RED_DRAGON_MASK)) ||
                player.getEquipment().isWearing(new Item(ItemId.BRONZE_DRAGON_MASK)) || player.getEquipment().isWearing(new Item(ItemId.IRON_DRAGON_MASK)) ||  player.getEquipment().isWearing(new Item(ItemId.GREEN_DRAGON_MASK));
    }


    public boolean inChickenOutfit(Player p) {
        return p.getEquipment().isWearing(new Item(ItemId.CHICKEN_FEET_11019)) && p.getEquipment().isWearing(new Item(ItemId.CHICKEN_WINGS_11020))
                && p.getEquipment().isWearing(new Item(ItemId.CHICKEN_HEAD_11021)) && p.getEquipment().isWearing(new Item(ItemId.CHICKEN_LEGS_11022));
    }

    private void giveCandy(Player player) {
        if(player.getInventory().getFreeSlots() > 0 || player.getInventory().getAmountOf(24565) > 0) {
            player.getInventory().addItem(new Item(24565, 1));
        } else {
            npc("Make some room before I can give this to you");
        }
    }
}
