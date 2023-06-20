package com.zenyte.game.content.minigame.inferno.model;

import com.zenyte.game.content.minigame.inferno.npc.*;
import com.zenyte.game.content.minigame.inferno.npc.impl.*;
import com.zenyte.game.content.minigame.inferno.npc.impl.zuk.TzKalZuk;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 29/11/2019 | 20:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum WaveNPC {
    JAL_NIB(JalNib.class, NpcId.JALNIB),
    JAL_MEJRAH(JalMejRah.class, NpcId.JALMEJRAH),
    JAL_AK(JalAk.class, NpcId.JALAK),
    JAL_AKREK_MEJ(JalAkRekMej.class, NpcId.JALAKREKMEJ),
    JAL_AKREK_XIL(JalAkRekXil.class, NpcId.JALAKREKXIL),
    JAL_AKREK_KET(JalAkRekKet.class, NpcId.JALAKREKKET),
    JAL_IMKOT(JalImKot.class, NpcId.JALIMKOT),
    JAL_XIL(JalXil.class, NpcId.JALXIL),
    JAL_ZEK(JalZek.class, NpcId.JALZEK),
    JALTOK_JAD(JalTokJad.class, NpcId.JALTOKJAD),
    TZKAL_ZUK(TzKalZuk.class, NpcId.TZKALZUK);

    private final Class<? extends InfernoNPC> clazz;
    private final int baseNPC;
}
