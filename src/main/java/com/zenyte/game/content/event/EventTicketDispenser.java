package com.zenyte.game.content.event;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

import java.util.Optional;

public class EventTicketDispenser implements ObjectAction
{
    public static final String ATTR_UNCLAIMED_TICKET_AMOUNT = "unclaimed-event-tickets";
    public static final String ATTR_DAILY_GIVEN_AMOUNT = "daily-event-tickets-given";

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option)
    {
        if(option.equals("Claim"))
        {
            player.getDialogueManager().start(getClaimDialogue(player));
        }
        if(option.equals("Configure"))
        {
            player.getDialogueManager().start(getConfigDialogue(player));
        }
    }

    private void claimTicketAmount(Player player, int requestedAmount)
    {
        player.getDialogueManager().finish();
        final int eventTickets = player.getNumericAttribute(ATTR_UNCLAIMED_TICKET_AMOUNT).intValue();
        if(eventTickets < requestedAmount)
        {
            requestedAmount = eventTickets;
        }
        final int newAmount = requestedAmount;
        player.addAttribute(ATTR_UNCLAIMED_TICKET_AMOUNT, (int) (eventTickets - newAmount));
        player.getInventory().addItem(ItemId.EVENT_REWARD_TICKET, newAmount);
        WorldTasksManager.schedule(() -> {
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    plain("You claimed " + newAmount + (newAmount > 1 ? " event tickets" : " event ticket") + ". Your new unclaimed balance is: " + player.getNumericAttribute(EventTicketDispenser.ATTR_UNCLAIMED_TICKET_AMOUNT).intValue()).executeAction(() -> {
                        player.getDialogueManager().finish();
                        player.getDialogueManager().start(getClaimDialogue(player));
                    });
                }
            });
        });
    }

    public Dialogue getConfigDialogue(Player player)
    {
        return new Dialogue(player)
        {
            @Override
            public void buildDialogue()
            {
                if(!player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR))
                {
                   plain("You cannot access this menu.");
                   return;
                }
                options("What do you want to do?", new DialogueOption("Add to balance", () ->
                    {
                        player.getDialogueManager().finish();
                        player.sendInputString("Whose balance to add to?", string ->
                        {
                            player.sendInputInt("How many would you like to add?", value -> changeBalance(player, string, value));
                        });
                    }),
                    new DialogueOption("Remove from balance", () ->
                    {
                        player.getDialogueManager().finish();
                        player.sendInputString("Whose balance to remove from?", string ->
                        {
                            player.sendInputInt("How many would you like to remove?", value -> changeBalance(player, string, -value));
                        });
                    }),
                    new DialogueOption("Check balance", () ->
                    {
                        player.getDialogueManager().finish();
                        player.sendInputString("Whose balance to check?", string ->
                        {
                            checkBalance(player, string);
                        });
                    }),
                    new DialogueOption("Check daily limit", () ->
                    {
                       int dailyUsed = player.getNumericAttribute(ATTR_DAILY_GIVEN_AMOUNT).intValue();
                       player.getDialogueManager().finish();
                       player.getDialogueManager().start(new Dialogue(player)
                       {
                           @Override
                           public void buildDialogue()
                           {
                               plain("You have used " + dailyUsed + " out of your daily limit of 2,000");
                           }
                       });

                    })
                );
            }
        };
    }

    public Dialogue getClaimDialogue(Player player)
    {
        return new Dialogue(player)
        {
            @Override
            public void buildDialogue()
            {
                final int eventTickets = player.getNumericAttribute(ATTR_UNCLAIMED_TICKET_AMOUNT).intValue();
                if(eventTickets == 0)
                {
                    plain("You don't have any event reward tickets to claim.");
                }
                else
                {
                    if(player.getInventory().containsItem(ItemId.EVENT_REWARD_TICKET) || player.getInventory().getFreeSlots() > 0)
                    {
                        String title = "You have " + String.format("%,d", eventTickets) + (eventTickets > 1 ? " unclaimed event tickets." : " unclaimed event ticket.");
                        options(title, new DialogueOption("Claim 1", () -> claimTicketAmount(player, 1)), new DialogueOption("Claim 5", () -> claimTicketAmount(player, 5)), new DialogueOption("Claim 10", () -> claimTicketAmount(player, 10)), new DialogueOption("Claim 50", () -> claimTicketAmount(player, 50)), new DialogueOption("Claim All", () -> claimTicketAmount(player, Integer.MAX_VALUE)));
                    } else
                    {
                        plain("You need more free space to claim your event reward tickets.");
                    }
                }

            }
        };
    }


    private void changeBalance(Player player, String target, int amount)
    {
        Optional<Player> targetPlayer = World.getPlayer(target);
        boolean removing = amount < 0;
        if(targetPlayer.isPresent())
        {
            int finalAmount = amount;
            if(!player.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR) && Math.abs(finalAmount) > 100)
            {
                finalAmount = removing ? -100 : 100;
            }
            if(!player.getPrivilege().eligibleTo(Privilege.MODERATOR) && Math.abs(finalAmount) > 50)
            {
                finalAmount = removing ? -50 : 50;
            }
            int balance = targetPlayer.get().getNumericAttribute(ATTR_UNCLAIMED_TICKET_AMOUNT).intValue();
            int newBalance = balance + finalAmount;
            if(newBalance < 0)
            {
                finalAmount = finalAmount - newBalance;
                newBalance = 0;
            }
            final int finalBalance = newBalance;
            final int absolutelyFinalAmount = finalAmount;
            int dailyGivenAmount = player.getNumericAttribute(ATTR_DAILY_GIVEN_AMOUNT).intValue();
            if(!player.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR))
            {
                if(dailyGivenAmount >= 2000)
                {
                    player.getDialogueManager().finish();
                    player.getDialogueManager().start(new Dialogue(player)
                    {
                        @Override
                        public void buildDialogue()
                        {
                            plain("You have reached your daily limit on giving out tickets.");
                        }
                    });
                    return;
                } else {
                    int newDailyTotal = dailyGivenAmount + absolutelyFinalAmount;
                    player.addAttribute(ATTR_DAILY_GIVEN_AMOUNT, newDailyTotal);
                }

            }
            targetPlayer.get().addAttribute(ATTR_UNCLAIMED_TICKET_AMOUNT, finalBalance);
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    if(removing)
                    {
                        plain("You took " + absolutelyFinalAmount + " event tickets from " + targetPlayer.get().getUsername() + ". Their new unclaimed balance is: " + finalBalance).executeAction(() -> player.getDialogueManager().start(getConfigDialogue(player)));
                    } else
                    {
                        plain("You gave " + absolutelyFinalAmount + " event tickets to " + targetPlayer.get().getUsername() + ". Their new unclaimed balance is: " + finalBalance).executeAction(() -> {

                            player.getDialogueManager().start(getConfigDialogue(player));
                        });
                    }
                }
            });
        } else
        {
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    plain("You can't change the balance of an offline player.").executeAction(() -> player.getDialogueManager().start(getConfigDialogue(player)));
                }
            });
        }
    }

    private void checkBalance(Player player, String target)
    {
        Optional<Player> targetPlayer = World.getPlayer(target);
        if(targetPlayer.isPresent())
        {
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    int balance = targetPlayer.get().getNumericAttribute(ATTR_UNCLAIMED_TICKET_AMOUNT).intValue();
                    plain(targetPlayer.get().getUsername() + " has "  + balance + " unclaimed event tickets.");
                }
            });
        } else
        {
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    plain("You can't check the balance of an offline player.");
                }
            });
        }
    }


    @Override
    public Object[] getObjects()
    {
        return new Object[] { ObjectId.TICKET_DISPENSER_40070 };
    }
}
