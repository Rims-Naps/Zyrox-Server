package com.zenyte.game.world.broadcasts;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TriviaBroadcasts
{

    private static final String[] TRIVIA_QUESTIONS = { "What is the name of the town where Pixies's home is located?",
                                                       "Who is the Taskmaster for the Varrock Achievement Diaries?", //Submitted by MasterMewTwo
                                                       "How many sets of Barrows armour are obtainable?",
                                                       "What Slayer Master requires you to kill a monster in a specific area?",
                                                       "What is the normal kill count you must have in order to enter a God Wars room?",
                                                       "What Slayer level do you need to be able to injure Cerberus?",
                                                       "What is the lowest XP rate you can have on Pixies?",
                                                       "Who is the first NPC you meet upon creating a new account?",
                                                       "What boss besides corp can be found in the Wilderness but isn't considered a Wilderness boss?",
                                                       "What is the required mining level to be able to mine crystal shards?",
                                                       "What is the rarest unique drop from Lizardman Shamans?",
                                                       "Who do you talk to in order to buy a skill cape in Pixies?",
                                                       "Who is the only NPC to be found wearing an Elysian Spirit Shield?",
                                                       "Name one of the original creators of Runescape",
                                                       "What Wilderness level is the Corporal Beasts lair located at?",
                                                       "Name one of the items unlocked upon completion of The Lost City quest.",
                                                       "What year did the Falador Massacre originally occur in Runescape?",
                                                       "What is the maximum combat level?",
                                                       "Name one skill that was polled in OSRS but didn't make the cut.",
                                                       "Name an NPC that can crush your herblore secondaries.",
                                                       "How many inferno waves are there?",
                                                       "What is the famous 'b0aty' number?",
                                                       "In Pixies, how many bounty hunter points do you get for selling a T10 emblem?",
                                                       "Armadyl is the god of ...?",
                                                       "Guthix is the god of ...?",
                                                       "Name the ancient ranger whose armour you can get from Wilderness drops.",
                                                       "Name the ancient mage whose armour you can get from Wilderness drops.",
                                                       "Name one the ancient warriors whose armour you can get from Wilderness drops.",
                                                       "What weapon can deal 25% extra damage while draining 50% of the special energy bar?",
                                                       "What quest do you have to complete to be able to wield the Dragon scimitar?",
                                                       "What quest do you have to complete to unlock the region of Tirannwn?",
                                                       "How many quest points do you need to be able to defeat the Culiniromancer?",
                                                       "Bandos is the god of ...?",
                                                       "What cooking level do you need to be able to cook sharks?",
                                                       "What race of monster is General Graardor?",
                                                       "What is the name of the person that sells various skilling tools at home?",
                                                       "Where do you obtain an ale of the gods?",
                                                       "In what city is the White Knights castle located in?",
                                                       "What spellbook do you need to be on to teleport to the Salve Graveyard?",
                                                       "Name the NPC that will repair your Barrows armor for a fee.",
                                                       "Name the NPC that sells newspapers in Varrock.",
                                                       "To make a blessed spirit shield, you need a spirit shield and what other item?",
                                                       "What is the name of the Duke of Lumbridge?",
                                                       "What stat does the dragon warhammer special attack decrease?",
                                                       "What kind of staff do you need to travel by Fairy Ring?",
                                                       "What prayer level do you need to activate the Incredible Reflexes prayer?",
                                                       "What's the required Herblore level to make super defense mix?",
                                                       "What bow's damage is scaled based on the target's magic level?",
                                                       "The dragon warhammer is a 1 in ???? drop? (Fill in the blank)",
                                                       "What cooking level is required to be able to cook rainbow fish?",
                                                       "What is the max limit for a single item stack in Runescape?",
                                                       "What OSRS quest can you complete to pass through the gates to Al Kharid for free?",
                                                       "The Elysian sigil is a 1 in ???? drop from Corporeal Beast? (Fill in the blank)",
                                                       "What wave number do donators start the fight caves at on Pixies?",
                                                       "How many Marks of Grace does full Graceful cost on Pixies?",
                                                       "What is the maximum XP cap in each skill?",
                                                       "What Crafting level is required to be able to cut Onyx?",
                                                       "What is the name of the pet you receive from Herbiboar?",
                                                       "What is the heaviest item you can carry in the Weapon slot?",
                                                       "How many coal do you need when smithing an Adamant bar in a regular furnace?",
                                                       "What is the combat level of the Kalphite Queen?",
                                                       "What Achievement Diary gives you Unlimited Teleports to the Piscatoris Fishing Colony?",
                                                       "What is the name of the pirate, whose name is a reference to Luke Skywalker, and captains a ship?",
                                                       "In Pixies, the drop rate of a Brittle Key from a Gargoyle is 1 in ?",
                                                       "What Thieving level is required to pickpocket Master farmers?",
                                                       "How much special attack energy does the Dragon mace special attack use?",
                                                       "What is the name of the stackable fish that's used for bait?",
                                                       "How many skills can you currently max in Pixies?",
                                                       "What tree requires 75 Woodcutting to cut down?",
                                                       "What herb do you need to make a Saradomin brew?",
                                                       "What is the name of the highest tier of clue scroll?",
                                                       "What is the name of the completed god book of Guthix? The Book of ????",
                                                       "How many types of blessed d'hide sets are there in game?",
                                                       "What is the name of the ring that can charm certain NPC's to make things cheaper?",
                                                       "What's the name of the Thieving pet?",
                                                       "What kind of animal is the Woodcutting pet?",
                                                       "??? legs are used in Gnome cooking, as well as in Agility potions",
                                                       "A Sanfew Serum is made from a Super Restore, Unicorn horn dust, Snake weed, and ???",
                                                       "The mine located under Jatizso contains adamantite, tin, coal, and which other ore type?",
                                                       "What is the largest weight of sandstone you can mine?",
                                                       "A Wild Pie is made from raw rabbit, raw bear meat, and what birds raw meat? (Name the bird)",
                                                       "What type of compost do coconut shells produce when placed into a composting bin?",
                                                       "This tree smells of yak, and has an odd Woodcutting requirement of lvl 54?",
                                                       "This method of transport can take you all the way along the River Lum, and even into the Wilderness with enough skill. What is it?",
                                                       "Enchanting dragonstone crossbow bolts cost 1 cosmic, 10 soul, and 15 of what rune type?",
                                                       "How many flax can you spin per cast of the Lunar Spin Flax spell?",
                                                       "Steam runes are a combination of water and which other rune?",
                                                       "What is the name of the village you must spin a crossbow string in order to complete the Ardougne Elite diary?",
                                                       "Most often called the Rev caves, this wilderness dungeons actual name is the ???",
                                                       "Which type of enchanted bolt can inflict poison damage?",
                                                       "Insulated boots are used to reduce the damage from the special attack of Rune dragons, and the ranged damage of what other slayer monster?",
                                                       "What is the current max total level in Pixies?",
                                                       "What is the name of the green crystal dropped by Cerberus?",
                                                       "What is the name of the red crystal dropped by Cerberus?",
                                                       "What is the name of the purple crystal dropped by Cerberus?",
                                                       "What Herblore level is required to be able to make a Saradomin brew?",
                                                       "What is the fairy ring code for Miscellania?",
                                                       "You can earn more vote rewards by having what enabled?",
                                                       "What is the name of the Fairy Slayer Master?",
                                                       "Who is the final boss you defeat in Dragon Slayer II?",
                                                       "What is the name of the potion that boosts your stats but damages you for 50 HP?",
                                                       "Which city contains the highest level rooftop Agility course?",
                                                       "How many runes are inside of a rune pack?",
                                                       "What equipment is required to survive the Smoke Dungeon?",
                                                       "What is the name of the fish you can trade to an NPC named Kylie in exchange for Sharks?",
                                                       "What's the name of the Ogre that teaches you how to catch Chompy birds?",
                                                       "What type of ammunition is used in the ballista?",
                                                       "What kind of impling requires 99 Hunter to catch barehanded?",
                                                       "What level of clue scroll can reward you with Ranger boots?",
                                                       "The Blue Beret is a clue scroll reward from which tier of clue?",
                                                       "What level hunter is required to catch a black chinchompa?",
                                                       "What combat level is the Alchemical Hydra?",
                                                       "What thieving level is required to pickpocket an elf?",
                                                       "Which continent is Wintertodt located on?",
                                                       "What magic level is required to enchant a ruby amulet?",
                                                       "What cooking level is required to combine a smouldering stone to a dragon harpoon?",
                                                       "Where is Bob the axe merchant located?",
                                                       "What slayer level is required to use Duradel?",
                                                       "How many vote points does a hard clue bottle cost in the vote shop?",
                                                       "How many slayer points does a Barrelchest Anchor cost in the slayer reward shop?",
                                                       "What herblore level is required to make a Magic Essence mix?",
                                                       "The 3rd age cloak is obtained from which tier clues?",
                                                       "Flippers are acquired from which monster?",
                                                       "Where is the boss slayer master located?",
                                                       "Where do you obtain a torn prayer scroll from?",
                                                       "What tier clue is a big pirate hat obtained from?",
                                                       "What's the drop rate of any skilling outfit piece whilst skilling?",
                                                       "How many points is it to upgrade a SINGLE piece of void into elite void?",
                                                       "What monster drops the pet Dark core?",
                                                       "What monster drops the Dragon full helm?",
                                                       "How many different types of defenders are there in Pixies?",
                                                       "What defense level is required to wear Barrows gloves?",
                                                       "What fishing level is required to fish Infernal eels?",
                                                       "What agility level is required to use crystal equipment?",
                                                       "What floor is the Grain of Plenty located on in the Stronghold of Security?",
                                                       "What mining level is required to wear Superior mining gloves?",
                                                       "What farming level is required to plant a woad seed?",
                                                       "How many coal do you need when smithing a Lovakite bar?",
                                                       "What fletching level is required to fletch a yew shield?",
                                                       "How many types of wands are there?",
                                                       "What item do you need 10 of to enchant the trident of the swamp/seas?",
                                                       "What hunter level is required to lay 5 traps?",
                                                       "How many bars do you need to smith platebodies?",
                                                       "How many slayer masters are there in Pixies?",
                                                       "What year was Pixies released to the public?",
                                                       "How many types of firelighters are there?",
                                                       "Who discovered giant moles?",
                                                       "What shield reduces the effectiveness of all Prayer draining attacks?",
                                                       "What is the place called where all runes are unlimited?",
                                                       "What is the second to last boss in the Theatre of Blood?",
                                                       "What year did the Theatre of Blood release to the public on Pixies?",
                                                       "What is the best defender in the game called?",
                                                       "Which type of enchanted bolt has a healing effect?",
                                                       "In order to runecraft Steam runes, you need water and which other type of rune?",
                                                       "What's the combat level of Zulrah?",
                                                       "What Hunter level is required in order to catch a Greater Siren?",
                                                       "Which Combat Achievements' tier completion unlocks you the jad slayer helmet recolour?",
                                };



    private static final String[][] TRIVIA_ANSWERS = {{"Edgeville", "Edge"},
                                                      {"Toby"},
                                                      {"6", "six"},
                                                      {"Konar quo Maten", "Konar", "KQM"},
                                                      {"40", "forty"},
                                                      {"91", "ninety one"},
                                                      {"5x", "x5", "5", "5 x", "x 5", "five"},
                                                      {"Pixies guide", "zen guide"},
                                                      {"King Black Dragon", "KBD"},
                                                      {"94", "ninety four"},
                                                      {"Dragon warhammer", "dragon war hammer", "dwh"},
                                                      {"Mac"},
                                                      {"Nieve"},
                                                      {"Andrew Gower", "Ian Gower", "Paul Gower", "Andrew", "Ian", "Paul"},
                                                      {"21", "twenty one"},
                                                      {"Dragon dagger", "Dragon longsword", "Dragon long"},
                                                      {"2006"},
                                                      {"126", "one hundred twenty six"},
                                                      {"Sailing", "Warding", "Artisan"},
                                                      {"Zahur", "Wesley"},
                                                      {"69", "sixty nine"},
                                                      {"73", "seventy three"},
                                                      {"5,000,000", "5000000", "5m", "5 million", "5 mil"},
                                                      {"Justice"},
                                                      {"Balance"},
                                                      {"Morrigan", "Morrigans"},
                                                      {"Zuriel", "Zuriels"},
                                                      {"Vesta", "Statius"},
                                                      {"Armadyl godsword", "ags"},
                                                      {"Monkey Madness"},
                                                      {"Regicide"},
                                                      {"175", "one hundred seventy five"},
                                                      {"War"},
                                                      {"80", "eighty"},
                                                      {"Ourg"},
                                                      {"Jackie"},
                                                      {"clue scroll reward", "clue reward", "Master clue reward", "Master clues", "master clue", "clue", "clues"},
                                                      {"Falador", "Fally"},
                                                      {"Arceuus"},
                                                      {"Bob", "Perdu"},
                                                      {"Benny Gutenburg", "Benny"},
                                                      {"Holy elixir", "Holy elixer"},
                                                      {"Horacio", "Duke horacio"},
                                                      {"defense", "defence"},
                                                      {"Dramen", "Lunar"},
                                                      {"34", "thirty four"},
                                                      {"71", "seventy one"},
                                                      {"Twisted bow", "tbow"},
                                                      {"2000"},
                                                      {"35", "thirty five"},
                                                      {"2,147,483,647", "2147483647"},
                                                      {"Prince Ali Rescue"},
                                                      {"2304"},
                                                      {"31", "thirty one"},
                                                      {"104", "one hundred and four"},
                                                      {"200,000,000", "200m", "200 m", "200 mil", "200 million", "200 mill", "200000000"},
                                                      {"67", "sixty seven"},
                                                      {"Herbi"},
                                                      {"Barrelchest anchor"},
                                                      {"6", "six"},
                                                      {"333"},
                                                      {"Western province", "Western provinces"},
                                                      {"Lokar Searunner"},
                                                      {"100", "one hundred"},
                                                      {"38", "thirty eight"},
                                                      {"25%", "25"},
                                                      {"Karambwanji"},
                                                      {"22", "twenty two"},
                                                      {"Magic", "Magic tree", "Magic Trees"}, /* Cresbugfix */
                                                      {"Toadflax"},
                                                      {"Master"},
                                                      {"Balance"},
                                                      {"6", "six"},
                                                      {"Charos", "Ring of Charos"},
                                                      {"Rocky"},
                                                      {"Beaver"},
                                                      {"Toad", "Toad's", "Toads"},
                                                      {"Nail beast nails"},
                                                      {"Mithril", "Mith"},
                                                      {"10kg", "10 kg"},
                                                      {"Chompy", "Raw chompy", "Chompy bird"},
                                                      {"supercompost", "super compost", "super"},
                                                      {"Arctic pine", "Arctic pine tree"},
                                                      {"Canoe"},
                                                      {"Earth"},
                                                      {"5", "five"},
                                                      {"Fire"},
                                                      {"Witchaven"},
                                                      {"Forinthry dungeon", "Forinthry"},
                                                      {"Emerald"},
                                                      {"Killerwatts", "Killerwatt"},
                                                      {"2179"},
                                                      {"Pegasian", "Pegasians"},
                                                      {"Primordial", "Primordials"},
                                                      {"Eternal", "Eternals"},
                                                      {"81", "eighty one"},
                                                      {"CIP", "c i p"},
                                                      {"2 factor authentication (2fa)", "2fa", "2 factor authentication", "two-factor authentication"},
                                                      {"Chaeldar"},
                                                      {"Galvek"},
                                                      {"Overload", "ovl"},
                                                      {"Ardougne", "Ardy"},
                                                      {"100", "one hundred"},
                                                      {"Face mask", "Slayer helm", "Slayer helmet", "Facemask"},
                                                      {"Minnow", "Minnows"},
                                                      {"Rantz"},
                                                      {"Javelins", "Javelin"},
                                                      {"Lucky"},
                                                      {"Medium", "Medium clue", "Medium clue scroll", "Mediums", "Medium clue scrolls"},
                                                      {"Easy", "Easy Clue", "Easy clue scroll", "Easys", "Easy clue scrolls"},
                                                      {"73", "seventy three"},
                                                      {"426", "four hundred and twenty six"},
                                                      {"85", "eighty five"},
                                                      {"Zeah"},
                                                      {"49", "forty nine"},
                                                      {"85", "eighty five"},
                                                      {"Lumbridge", "Lumby", "Lumb"},
                                                      {"50", "fifty"},
                                                      {"9", "nine"},
                                                      {"200", "two hundred"},
                                                      {"61", "sixty one"},
                                                      {"Elite", "Elite clue", "Elite clues", "Elites", "Master", "Masters", "Master clue", "Master clues"},
                                                      {"Mogre", "Morges"},
                                                      {"Myth guild", "Myth's guild"},
                                                      {"Cox", "Chambers of xeric", "raids 1"},
                                                      {"Elite", "Elite clue", "Elite clues", "Elites"},
                                                      {"1/1000", "1/1k", "0.001%"},
                                                      {"40", "forty"},
                                                      {"Corp", "Corporeal beast", "Corp beast"},
                                                      {"Mithril dragon", "Mith Dragon"},
                                                      {"9", "nine"},
                                                      {"40", "forty"},
                                                      {"80", "eighty"},
                                                      {"50", "fifty"},
                                                      {"Second", "2nd"},
                                                      {"55", "fifty five"},
                                                      {"25", "twenty five"},
                                                      {"2", "two"},
                                                      {"72", "seventy two"},
                                                      {"6", "six"},
                                                      {"Kraken tentacle", "Kraken Tent"},
                                                      {"80", "eighty"},
                                                      {"5", "five"},
                                                      {"9", "nine"},
                                                      {"2019"},
                                                      {"5", "five"},
                                                      {"Wyson", "Wyson the gardener"},
                                                      {"Spectral spirit shield", "Spectral"},
                                                      {"Fountain of Rune"},
                                                      {"Xarpus"},
                                                      {"2022"},
                                                      {"Avernic defender"},
                                                      {"Onyx"},
                                                      {"Fire"},
                                                      {"725"},
                                                      {"87"},
                                                      {"Elite"},
                                                    };


    private static final int SCROLL_BOX_IDS[] = {ItemId.SCROLL_BOX_EASY, ItemId.SCROLL_BOX_MEDIUM,
                                                 ItemId.SCROLL_BOX_HARD, ItemId.SCROLL_BOX_ELITE, ItemId.SCROLL_BOX_MASTER };

    private static String CURRENT_TRIVIA_QUESTION = "";
    private static String CURRENT_TRIVIA_ANSWERS[] = { };

    private static List<String> TRIVIA_WINNERS = new ArrayList<>();

    public static String getCurrentTriviaQuestion()
    {
        return CURRENT_TRIVIA_QUESTION;
    }

    public static void setNextTriviaQuestion()
    {
        final int random = Utils.random(0, TRIVIA_QUESTIONS.length - 1);
        CURRENT_TRIVIA_QUESTION = TRIVIA_QUESTIONS[random];
        CURRENT_TRIVIA_ANSWERS = TRIVIA_ANSWERS[random];
    }


    public static boolean isCorrectAnswer(String answer)
    {
        if(!CURRENT_TRIVIA_QUESTION.equals(""))
        {
            if(CURRENT_TRIVIA_ANSWERS != null)
            {
                for(String acceptableAnswer : CURRENT_TRIVIA_ANSWERS)
                {
                    if(answer.equalsIgnoreCase(acceptableAnswer))
                    {
                        return true;
                    }
                }
            }
        } else
        {
            return false;
        }
        return false;
    }

    public static List<String> getTriviaWinners()
    {
        return TRIVIA_WINNERS;
    }

    public static void announceWinners()
    {
        if(getTriviaWinners() == null || getTriviaWinners().size() == 0)
        {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<img=13>");
        sb.append(Colour.ORANGE.wrap("[Trivia]:"));
        for(int i = 0; i < getTriviaWinners().size(); i++)
        {
            if(i == getTriviaWinners().size() - 1)
            {
                if(i == 0)
                {
                    sb.append(Utils.formatString(getTriviaWinners().get(i)));
                } else {
                    sb.append(" and " + Utils.formatString(getTriviaWinners().get(i)));
                }

            } else
            {
                if(getTriviaWinners().size() == 2)
                {
                    sb.append(" " + Utils.formatString(getTriviaWinners().get(i)));
                } else {
                    sb.append(" " + Utils.formatString(getTriviaWinners().get(i)) + ",");
                }

            }
        }
        sb.append(" won that round with the answer: ");
        sb.append(Colour.RS_GREEN.wrap(CURRENT_TRIVIA_ANSWERS[0]));

        World.sendMessage(MessageType.UNFILTERABLE, sb.toString());
    }

    public static void payWinners()
    {
        if(getTriviaWinners() == null)
        {
            return;
        }

        for(String s : getTriviaWinners())
        {
            Optional<Player> winner = World.getPlayer(s);
            if(winner.isPresent())
            {
                Item randomScrollbox = new Item(SCROLL_BOX_IDS[Utils.random(SCROLL_BOX_IDS.length - 1)]);

                if(winner.get().getGameMode() == GameMode.ULTIMATE_IRON_MAN)
                {
                    winner.get().sendMessage("You received a " + randomScrollbox.getName() + " for answering correctly.");
                    winner.get().getInventory().addOrDrop(randomScrollbox);
                    continue;
                }

                if(winner.get().getBank().hasFreeSlots())
                {
                    winner.get().sendMessage("You received a " + randomScrollbox.getName() + " for answering correctly. It has been sent to your bank.");
                    winner.get().getBank().add(randomScrollbox);
                }
                else
                {
                    winner.get().sendMessage("You received a " + randomScrollbox.getName() + " for answering correctly.");
                    winner.get().getInventory().addOrDrop(randomScrollbox);
                }
            }
        }
    }

    public static void reset()
    {
        CURRENT_TRIVIA_QUESTION = "";
        CURRENT_TRIVIA_ANSWERS = null;
        getTriviaWinners().clear();
    }

    public static void expireQuestion()
    {
        if(CURRENT_TRIVIA_QUESTION.equals(""))
        {
            return;
        }
        switch(getTriviaWinners().size())
        {
            case 0:
                World.sendMessage(MessageType.FILTERABLE, "<img=13>" + String.format(Colour.ORANGE.wrap("[Trivia]:") + " Time expired! There were no winners this time. The answer was: %s ", CURRENT_TRIVIA_ANSWERS[0]));
                break;
            case 1:
            case 2:
            case 3:
                announceWinners();
                payWinners();
                break;
            default:
                break;
        }
        reset();
    }

}
