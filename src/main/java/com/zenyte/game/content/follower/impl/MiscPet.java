package com.zenyte.game.content.follower.impl;

import com.zenyte.game.content.event.christmas2019.SnowImpFollowerD;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.Pet;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.followers.*;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 23-11-2018 | 18:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public enum MiscPet implements Pet {

    //TODO kittens and other variations of these.
    HERBI(21509, 7760, HerbiD.class),
    CHOMPY_CHICK(13071, 4002, ChompyChickD.class),
    BLOODHOUND(19730, 7232, BloodHoundD.class),

    TOY_CAT(14924, 2782),
    OVERGROWN_HELLCAT(7581, 5604),
    WILY_HELLCAT(7585, 5590),
    BLUEFISH(6670, -1),
    GREENFISH(6671, -1),
    SPINEFISH(6672, -1),
    CAT_1(1561, 1619),
    CAT_2(1562, 1620),
    CAT_3(1563, 1621),
    CAT_4(1564, 1622),
    CAT_5(1565, 1623),
    CAT_6(1566, 1624),
    CUTE_CREATURE(30000, 10013),
    STRAY_DOG(30001, 10014),
    EVIL_CREATURE(30002, 10015),
    JAL_IMREK(30003, 10016),
    BUCKET_PETE(30004, 10017),
    WYRMY(30005, 10018),
    SNOW_IMP(30114, 15060),
    AREA_LOCKED_SNOW_IMP(-1, 15062, SnowImpFollowerD.class),

    SARADOMIN_OWL(30150, 15104),
    ZAMORAK_HAWK(30151, 15105),
    GUTHIX_RAPTOR(30152, 15106),

    /* Do not rearrange the following pets */
    SPIRIT_KALPHITE(30207, 15107, SpiritKalphiteD.class),
    RED_BABYDRAGON(30156, 15128, DragonD.class),
    BLUE_BABYDRAGON(30157, 15129, DragonD.class),
    GREEN_BABYDRAGON(30158, 15130, DragonD.class),
    BLACK_BABYDRAGON(30159, 15131, DragonD.class),
    PRAYING_MANTIS(30206, 15101, PrayingMantisD.class),
    WOLPERTINGER(30209, 15103, WolpertingerD.class),
    GRANITE_CRAB(30205, 15100, GraniteCrabD.class),
    COCKROACH(30203, 15108, CockroachD.class),
    EVIL_TURNIP(30204, 15102, EvilTurnipD.class),
    SPIRIT_MOSQUITO(30208, 15109, SpiritMosquitoD.class),
    YELLOW_GECKO(30194, 15133, GeckoD.class),
    GREEN_GECKO(30195, 15134, GeckoD.class),
    RED_GECKO(30196, 15135, GeckoD.class),
    BLUE_GECKO(30197, 15136, GeckoD.class),
    BEIGE_PENGUIN(30153, 15148, PenguinD.class),
    DARK_PENGUIN(30202, 15149, PenguinD.class),
    WHITE_BULLDOG(30167, 15125, DogD.class),
    GRAY_BULLDOG(30190, 15126, DogD.class),
    BEIGE_BULLDOG(30191, 15127, DogD.class),
    GRAY_SQUIRREL(30161, 15153, SquirrelD.class),
    BEIGE_SQUIRREL(30198, 15154, SquirrelD.class),
    WHITE_SQUIRREL(30199, 15155, SquirrelD.class),
    BLACK_SQUIRREL(30200, 15156, SquirrelD.class),
    BROWN_SQUIRREL(30201, 15157, SquirrelD.class),
    GRAY_RACCOON(30160, 15150, RaccoonD.class),
    BEIGE_RACCOON(30192, 15151, RaccoonD.class),
    RED_RACCOON(30193, 15152, RaccoonD.class),
    BROWN_PLATYPUS(30168, 15168, PlatypusD.class),
    BEIGE_PLATYPUS(30169, 15169, PlatypusD.class),
    DARK_PLATYPUS(30170, 15170, PlatypusD.class),
    BEIGE_TERRIER(30162, 15110, DogD.class),
    WHITE_TERRIER(30180, 15111, DogD.class),
    BLACK_TERRIER(30181, 15112, DogD.class),
    BEIGE_GREYHOUND(30163, 15113, DogD.class),
    WHITE_GREYHOUND(30182, 15114, DogD.class),
    RED_GREYHOUND(30183, 15115, DogD.class),
    WHITE_LABRADOR(30164, 15116, DogD.class),
    BLACK_LABRADOR(30184, 15117, DogD.class),
    BEIGE_LABRADOR(30185, 15118, DogD.class),
    WHITE_DALMATIAN(30165, 15119, DogD.class),
    PINKISH_DALMATIAN(30186, 15120, DogD.class),
    BLACK_SHEEPDOG(30166, 15122, DogD.class),
    WHITE_SHEEPDOG(30188, 15123, DogD.class),
    BEIGE_SHEEPDOG(30189, 15124, DogD.class),
    MONKEY_1(30154, 15137, MonkeyD.class),
    MONKEY_2(30171, 15138, MonkeyD.class),
    MONKEY_3(30172, 15139, MonkeyD.class),
    MONKEY_4(30173, 15140, MonkeyD.class),
    MONKEY_5(30174, 15141, MonkeyD.class),
    MONKEY_6(30175, 15142, MonkeyD.class),
    MONKEY_7(30176, 15143, MonkeyD.class),
    MONKEY_8(30177, 15144, MonkeyD.class),
    MONKEY_9(30178, 15145, MonkeyD.class),
    MONKEY_10(30179, 15146, MonkeyD.class),
    CHAMELEON_1(30155, 15159, ChameleonD.class),
    CHAMELEON_2(30155, 15160, ChameleonD.class),
    CHAMELEON_3(30155, 15161, ChameleonD.class),
    CHAMELEON_4(30155, 15162, ChameleonD.class),
    CHAMELEON_5(30155, 15163, ChameleonD.class),
    CHAMELEON_6(30155, 15164, ChameleonD.class),
    CHAMELEON_7(30155, 15165, ChameleonD.class),
    CHAMELEON_8(30155, 15166, ChameleonD.class),
    CHAMELEON_9(30155, 15167, ChameleonD.class),
    CHAMELEON_10(30155, 15158, ChameleonD.class),
    DEATH_JR(24560, 13017, DeathJrD.class),
    SPOOKY_CHICKEN(24567, 13025, DeadChickenD.class),
    SPOOKY_COW(24569, 13026, SpookyCowD.class),
    SPOOKY_BAT(24571, 13027, SpookyBatD.class),
    SPOOKY_DRACULA(24573, 13028, SpookyDraculaD.class),
    SPOOKY_MONKEY(24577, 13029, MonkeyD.class),
    SPOOKY_WITCH(24579, 13030, SpookyWitchD.class),
    SPOOKY_GHOST(24581, 13032, SpookyGhostD.class),
    SPOOKY_ZOMBIE(24583, 13031, SpookyZombieD.class),
    SPOOKY_KILLER(24585, 13033, SpookyKillerD.class),
    JACK_O_KRAKEN(30087, 13034, JackOLanternD.class),
    CAT_7(1570, 7380, CatD.class),
    LAVA_DRAGON(27500, 16039, PetLavaDragonD.class),
    PET_GOBLIN(10998, 14032, PetGoblinD.class),
    BARA_PET(8312, 14034);


    /* Do not re-arrange the above pets */


    MiscPet(final int itemId, final int petId) {
        this(itemId, petId, null);
    }

    MiscPet(final int itemId, final int petId, final Class<? extends Dialogue> dialogue) {
        this.itemId = itemId;
        this.petId = petId;
        this.dialogue = dialogue;
    }

    private final int itemId, petId;
    private final Class<? extends Dialogue> dialogue;

    public static final MiscPet[] VALUES = values();

    @Override
    public int itemId() {
        return itemId;
    }

    @Override
    public int petId() {
        return petId;
    }

    @Override
    public String petName() {
        return name();
    }

    @Override
    public boolean hasPet(final Player player) {
        val petItemId = getItemId();
        if (player.containsItem(petItemId)) {
            return true;
        }
        return PetWrapper.checkFollower(player) && player.getFollower().getPet().petId() == getPetId();
    }

    @Override
    public Class<? extends Dialogue> dialogue() {
        return dialogue;
    }

    public boolean roll(final Player player, final int rarity) {
        if (this != BLOODHOUND || rarity == -1 || Utils.random(rarity) != 0) {
            return false;
        }
        val item = new Item(itemId);
        player.getCollectionLog().add(item);
        if (hasPet(player)) {
            player.sendMessage("<col=ff0000>You have a funny feeling like you would have been followed...</col>");
            return false;
        }
        if (player.getFollower() != null)
        {
            if (player.getInventory().addItem(item).isFailure())
            {
                if (player.getBank().add(item).isFailure())
                {
                    player.sendMessage("There was not enough space in your bank, and therefore the pet was lost.");
                    return false;
                } else
                {
                    player.sendMessage("<col=ff0000>You have a funny feeling like you're being followed - The pet has been added to your bank.</col>");
                }
            } else
            {
                player.sendMessage("<col=ff0000>You feel something weird sneaking into your backpack.</col>");
            }
        } else {
            player.sendMessage("<col=ff0000>You have a funny feeling like you're being followed.</col>");
            player.setFollower(new Follower(petId, player));
        }
        WorldBroadcasts.broadcast(player, BroadcastType.PET, this);
        return true;
    }
}
