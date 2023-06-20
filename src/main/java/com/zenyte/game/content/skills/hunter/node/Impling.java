package com.zenyte.game.content.skills.hunter.node;

import com.zenyte.game.content.skills.hunter.plugins.ImplingJarPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.utils.Ordinal;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Tommeh | 2-11-2018 | 18:13
 * @author Corey 09/01/20
 */
@Getter
@RequiredArgsConstructor
@Ordinal
public enum Impling {
    
    BABY(1635, ImplingJarPlugin.ImplingJar.BABY, 17, 18, 20),
    YOUNG(1636, ImplingJarPlugin.ImplingJar.YOUNG, 22, 20, 22),
    GOURMET(1637, ImplingJarPlugin.ImplingJar.GOURMET, 28, 22, 24),
    EARTH(1638, ImplingJarPlugin.ImplingJar.EARTH, 36, 25, 27),
    ESSENCE(1639, ImplingJarPlugin.ImplingJar.ESSENCE, 42, 27, 29),
    ECLECTIC(1640, ImplingJarPlugin.ImplingJar.ECLECTIC, 50, 32, 34),
    NATURE(1641, ImplingJarPlugin.ImplingJar.NATURE, 58, 34, 36, true),
    MAGPIE(1642, ImplingJarPlugin.ImplingJar.MAGPIE, 65, 44, 216, true),
    NINJA(1643, ImplingJarPlugin.ImplingJar.NINJA, 74, 52, 240, true),
    DRAGON(1644, ImplingJarPlugin.ImplingJar.DRAGON, 83, 65, 300, true),
    LUCKY(7233, ImplingJarPlugin.ImplingJar.LUCKY, 89, 80, 380, true),
    CRYSTAL(13000, ImplingJarPlugin.ImplingJar.CRYSTAL, 90, 80, 380, true),
    ;
    
    public static final Int2ObjectOpenHashMap<Impling> implings;
    public static final String SURFACE_IMPLING_TRACKER_ATTRIBUTE_KEY = "implings_caught_";
    public static final String PURO_IMPLING_TRACKER_ATTRIBUTE_KEY = "implings_caught_puro_";
    private static final Impling[] values = values();
    
    static {
        Utils.populateMap(values, implings = new Int2ObjectOpenHashMap<>(values.length), Impling::getNpcId);
    }
    
    private final int npcId;
    private final ImplingJarPlugin.ImplingJar jar;
    private final int level;
    private final double experiencePuroPuro, experienceGielinor;
    private final boolean isRare;
    
    Impling(int npcId, ImplingJarPlugin.ImplingJar jar, int level, int experiencePuroPuro, int experienceGielinor, boolean isRare) {
        this.npcId = npcId;
        this.jar = jar;
        this.level = level;
        this.experiencePuroPuro = experiencePuroPuro;
        this.experienceGielinor = experienceGielinor;
        this.isRare = isRare;
    }
    
    Impling(int npcId, ImplingJarPlugin.ImplingJar jar, int level, int experiencePuroPuro, int experienceGielinor) {
        this(npcId, jar, level, experiencePuroPuro, experienceGielinor, false);
    }
    
    public static Impling get(final int npcId) {
        return implings.get(npcId);
    }
    
    public static boolean contains(final int npcId) {
        return implings.containsKey(npcId);
    }
    
    public String formattedName() {
        return StringUtils.capitalize(name().toLowerCase());
    }
    
}
