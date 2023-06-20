package com.zenyte.plugins.dialogue;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.tithefarm.TitheFarmManager;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class FarmerGricollerD extends Dialogue {

    private final boolean reminder;
    private final boolean needsSeeds;

    public FarmerGricollerD(final Player player, final int npcId, final boolean reminder, final boolean needsSeeds) {
        super(player, npcId);
        this.reminder = reminder;
        this.needsSeeds = needsSeeds;
    }

    @Override
    public void buildDialogue() {
        final String title = player.getAppearance().getGender().toLowerCase().contains("woman") ? "lady" : "man";

        if(needsSeeds) {
            npc("Take some seeds from the table, otherwise you can't do any farming in there!").executeAction(() -> finish());
        } else {
            if(reminder) {
                npc("Hey, young " + title + ", do you know what you're doing in there?").executeAction(() -> setKey(46));
            } else {
                npc("Hello, young " + title + ".<br><br>Did you want to work on my farm?");
            }
        }

        options(2, TITLE, "Tell me about your farm.", "Tell me about yourself.", "I'd like to work on your farm.", "Have you any rewards?", "I was just leaving")
                .onOptionOne(() -> setKey(5))
                .onOptionTwo(() -> setKey(10))
                .onOptionThree(() -> setKey(15))
                .onOptionFour(() -> GameInterface.TITHE_FARM_REWARDS.open(player));

        player(5, "Tell me about your farm.");
        npc(6, "I grow fruit here. Special fruit. It grows very fast, but it needs a lot of tending, otherwise it gets the blight and dies.");
        npc(7, "These days I'm getting old and slow. You can see all the dead plants from where I've tried to farm them.");

        player(10, "Tell me about yourself.");
        npc(11, "I'm a fruit farmer. But now I'm a bit too old for that. Maybe I should try writing books again, like I did when I was younger.");

        player(15, "I'd like to work on your farm.");
        npc(16, "Alright. Take a handful of seeds from the table, and head into the farm. When you've harvested some fruit, deposit it straight into the sacks.");
        npc(17, "Remember, you have to water the plants at each stage, otherwise they die of the blight. Fertiliser makes them grow faster... or die faster if you don't water them.");
        npc(18, "You'll gain a little extra experience if you grow enough of the fruit successfully.");

        options(46, TITLE, "No, how does it work?", "Yes, I know what I'm doing.", "Yes, I'm an expert, don't ask me again.", "On second thought, I'll wait")
                .onOptionOne(() -> setKey(16))
                .onOptionTwo(() -> TitheFarmManager.getSingleton().selectRandomInstance().enterInstance(player))
                .onOptionThree(() -> setKey(50));

        player(50, "Yes, I'm an expert, don't ask me again.");
        npc(51, "Alright, young "+title+", I won't. <br><br>Talk to me if you ever need a reminder.").executeAction( () -> {
            player.putBooleanAttribute("gricoller_reminder_disabled", true);
            TitheFarmManager.getSingleton().selectRandomInstance().enterInstance(player);
        });
    }
}
