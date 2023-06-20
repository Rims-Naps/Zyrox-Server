package com.zenyte.game.content.event;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

public class EventTicketOnDispenser implements ItemOnObjectAction
{
    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object)
    {
        if(item.getId() == ItemId.EVENT_REWARD_TICKET)
        {
            final int amountToStore = item.getAmount();
            final int beforeAmount = player.getNumericAttribute(EventTicketDispenser.ATTR_UNCLAIMED_TICKET_AMOUNT).intValue();
            player.addAttribute(EventTicketDispenser.ATTR_UNCLAIMED_TICKET_AMOUNT, beforeAmount + amountToStore);
            player.getInventory().deleteItem(ItemId.EVENT_REWARD_TICKET, amountToStore);
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    int newBalance = player.getNumericAttribute(EventTicketDispenser.ATTR_UNCLAIMED_TICKET_AMOUNT).intValue();
                        plain("You stored " + amountToStore + " event tickets. Your new unclaimed balance is: " + newBalance);
                }
            });
        }
    }

    @Override
    public Object[] getItems()
    {
        return new Object[] {ItemId.EVENT_REWARD_TICKET};
    }

    @Override
    public Object[] getObjects()
    {
        return new Object[] {ObjectId.TICKET_DISPENSER_40070};
    }
}
