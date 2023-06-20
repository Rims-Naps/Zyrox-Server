package com.zenyte.game.content.follower.impl;

import com.zenyte.Constants;
import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.Pet;
import com.zenyte.game.content.follower.PetWrapper;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.followers.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import static com.zenyte.game.world.entity.player.Skills.*;

/**
 * @author Tommeh | 23-11-2018 | 18:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum SkillingPet implements Pet {

    ROCK_GOLEM_DEFAULT(13321, 7451,MINING, RockGolemD.class),
    ROCK_GOLEM_TIN(21187, 7452, MINING, RockGolemD.class),
    ROCK_GOLEM_COPPER(21188, 7453, MINING, RockGolemD.class),
    ROCK_GOLEM_IRON(21189, 7454, MINING, RockGolemD.class),
    ROCK_GOLEM_BLURITE(21190, 7455, MINING, RockGolemD.class),
    ROCK_GOLEM_SILVER(21191, 7642, MINING, RockGolemD.class),
    ROCK_GOLEM_COAL(21192, 7643, MINING, RockGolemD.class),
    ROCK_GOLEM_GOLD(21193, 7644, MINING, RockGolemD.class),
    ROCK_GOLEM_MITHRIL(21194, 7645, MINING, RockGolemD.class),
    ROCK_GOLEM_GRANITE(21195, 7646, MINING, RockGolemD.class),
    ROCK_GOLEM_ADAMANTITE(21196, 7647, MINING, RockGolemD.class),
    ROCK_GOLEM_RUNITE(21197, 7648, MINING, RockGolemD.class),
    ROCK_GOLEM_AMETHYST(21340, 7711, MINING, RockGolemD.class),
    ROCK_GOLEM_LOVAKITE(21358, 7739, MINING, RockGolemD.class),
    ROCK_GOLEM_ELEMENTAL(21359, 7740, MINING, RockGolemD.class),
    ROCK_GOLEMD_DAEYALT(21360, 7741, MINING, RockGolemD.class),


    RIFT_GUARDIAN_DEFAULT(20665, 7354, RUNECRAFTING, RiftGuardianD.class), //dont remove cuz serialized
    RIFT_GUARDIAN_AIR(20667, 7355, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_MIND(20669, 7356, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_WATER(20671, 7357, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_EARTH(20673, 7358, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_FIRE(20665, 7354, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_BODY(20675, 7359, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_COSMIC(20677, 7360, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_CHAOS(20679, 7361, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_NATURE(20681, 7362, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_LAW(20683, 7363, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_DEATH(20685, 7364, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_BLOOD(20691, 7367, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_SOUL(20687, 7365, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_ASTRAL(20689, 7366, RUNECRAFTING, RiftGuardianD.class),
    RIFT_GUARDIAN_WRATH(21990, 8028, RUNECRAFTING, RiftGuardianD.class),
    
    RED_BABY_CHINCHOMPA(13323, 6756, HUNTER, BabyChinchompaD.class),
    GRAY_BABY_CHINCHOMPA(13324, 6757, HUNTER, BabyChinchompaD.class),
    BLACK_BABY_CHINCHOMPA(13325, 6758, HUNTER, BabyChinchompaD.class),
    GOLDEN_BABY_CHINCHOMPA(13326, 6759, HUNTER, BabyChinchompaD.class),
    BEAVER(13322, 6724, WOODCUTTING, BeaverD.class),
    ROCKY(20663, 7353, THIEVING, RockyD.class),
    ZIGGY(32285, 14045, THIEVING, RockyD.class),
    RED(32287, 14046, THIEVING, RockyD.class),
    GIANT_SQUIRREL(20659, 7351, AGILITY, GiantSquirrelD.class),
    DARK_SQUIRREL(32281, 14044, AGILITY, GiantSquirrelD.class),
    HERON(13320, 6722, FISHING, HeronD.class),
    GREAT_BLUE_HERON(32297, 14051, FISHING, HeronD.class),
    TANGLEROOT(20661, 7352, FARMING, TanglerootD.class),
    DRAGONFRUIT_TANGLEROOT(32271, 14039, FARMING, TanglerootD.class),
    HERB_TANGLEROOT(32273, 14040, FARMING, TanglerootD.class),
    CRYSTAL_TANGLEROOT(32275, 14041, FARMING, TanglerootD.class),
    WHITELILY_TANGLEROOT(32277, 14042, FARMING, TanglerootD.class),
    REDWOOD_TANGLEROOT(32279, 14043, FARMING, TanglerootD.class);

    public static final SkillingPet[] VALUES = values();
    public static final Int2ObjectOpenHashMap<ObjectList<SkillingPet>> SKILLING_PETS_BY_SKILL;
    public static final Int2ObjectOpenHashMap<SkillingPet> SKILLING_PETS_BY_ITEM;

    static {
        Utils.populateObjectListMap(VALUES, SKILLING_PETS_BY_SKILL = new Int2ObjectOpenHashMap<>(VALUES.length),
                SkillingPet::getSkill);
        Utils.populateMap(VALUES, SKILLING_PETS_BY_ITEM = new Int2ObjectOpenHashMap<>(VALUES.length), SkillingPet::getItemId);
    }

    private final int itemId, petId, skill;
    private final Class<? extends Dialogue> dialogue;

    public static SkillingPet getBySkill(final int skill) {
        if (skill == Skills.MINING) {
            return ROCK_GOLEM_DEFAULT;
        }

        val pets = SKILLING_PETS_BY_SKILL.get(skill);

        if (skill == HUNTER) {
            if (Utils.random(9999) == 0) {
                return GOLDEN_BABY_CHINCHOMPA;
            }
            pets.remove(GOLDEN_BABY_CHINCHOMPA);
            return Utils.getRandomCollectionElement(pets);
        }

        return pets != null ? pets.get(0) : null;
    }

    public static SkillingPet getByItem(final int itemId) {
        return SKILLING_PETS_BY_ITEM.get(itemId);
    }

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
        for (SkillingPet skillingPet : SKILLING_PETS_BY_SKILL.get(skill)) {
            if (player.containsItem(skillingPet.getItemId())) {
                return true;
            }
            if (PetWrapper.checkFollower(player) && player.getFollower().getPet().petId() == skillingPet.getPetId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends Dialogue> dialogue() {
        return dialogue;
    }

    @Override
    public boolean roll(final Player player, final int rarity) {
        int roll = rarity;

        if(Constants.BOOSTED_SKILLING_PETS)
            roll = (int) (rarity - (rarity * Constants.BOOSTED_SKILLING_PET_RATE));

        if (Utils.random(roll) != 0) {
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

    public static boolean isRiftGuardian(final Pet pet) {
        return SKILLING_PETS_BY_SKILL.get(RUNECRAFTING).contains(pet);
    }

    public static boolean isRockGolem(final Pet pet) {
        return SKILLING_PETS_BY_SKILL.get(MINING).contains(pet);
    }

    public static boolean isChinchompa(final Pet pet) {
        return SKILLING_PETS_BY_SKILL.get(HUNTER).contains(pet);
    }
}
