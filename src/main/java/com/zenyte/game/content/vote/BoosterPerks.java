package com.zenyte.game.content.vote;

import com.zenyte.Constants;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.WiseOldManD;

public enum BoosterPerks
{

    THIEVING("Thieving: Decrease chance to get caught while pickpocketing by 5%.", 1),
    WOODCUTTING("Woodcutting: Decrease chance of a tree being felled by an action (excludes single action trees).", 2),
    FISHING("Fishing: Gain a 5% chance to catch an extra fish while fishing with no extra xp.", 3),
    SLAYER("Slayer: Increase the chance at encountering a superior creature by 5%.", 4),
    CRAFTING("Crafting: 5% chance to skip a tick while crafting certain items.", 5),
    HERBLORE("Herbolore: 5% chance to make a 4-dose potion instead of a 3-dose one.", 6),
    COOKING("Cooking: Slightly lower the chance to burn food while cooking.", 7),
    SMITHING("Smithing: 5% chance to save a bar while Smithing.", 8),
    RUNECRAFT("Runecraft: 5% chance to gain extra runes up to the amount crafted with no extra xp.", 9),
    FARMING("Farming: 5% chance per harvest to gain an extra product.", 10),
    AGILITY("Agility: 5% chance to find an extra Mark of grace.", 11);

    private String description;
    private int perkId;

    BoosterPerks(String description, int perkId) {
        this.description = description;
        this.perkId = perkId;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getPerkId()
    {
        return this.perkId;
    }

    public static String[] descriptionsToArray()
    {
        String[] descs = new String[BoosterPerks.values().length];
        for(int i = 0; i < BoosterPerks.values().length; i++)
        {
            descs[i] = BoosterPerks.values()[i].getDescription();
        }
        return descs;
    }

    public static String[] descriptionsToColoredArray(Player p)
    {
        String[] descs = new String[BoosterPerks.values().length];
        for(int i = 0; i < BoosterPerks.values().length; i++)
        {
            descs[i] = isActive(p, BoosterPerks.values()[i]) ? Colour.GREEN.wrap(BoosterPerks.values()[i].getDescription()) : Colour.RED.wrap(BoosterPerks.values()[i].getDescription());
        }
        return descs;
    }

    public static boolean isActive(Player player, BoosterPerks perk)
    {
        if(!Constants.isOwner(player) && player.getNumericAttribute(WiseOldManD.BOOSTER_END).longValue() < System.currentTimeMillis())
        {
            return false;
        }
        return player.getBooleanAttribute("booster-perk" + perk.getPerkId());
    }

    public static void toggle(Player player, BoosterPerks perk)
    {
        boolean toggled = player.getBooleanAttribute("booster-perk" + perk.getPerkId());
        if(!toggled)
        {
            player.putBooleanAttribute("booster-perk" + perk.getPerkId(), true);
        } else
        {
            player.putBooleanAttribute("booster-perk" + perk.getPerkId(), false);
        }
    }
}
