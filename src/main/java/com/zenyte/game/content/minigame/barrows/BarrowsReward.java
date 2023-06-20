package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.item.Item;
import lombok.AllArgsConstructor;

/**
 * @author Kris | 30/11/2018 20:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public enum BarrowsReward {

    COINS(new Item(995, 5306), 1, 380),
    MIND_RUNE(new Item(558, 2889), 381, 505),
    CHAOS_RUNE(new Item(562, 885), 506, 630),
    DEATH_RUNE(new Item(560, 578), 631, 755),
    BLOOD_RUNE(new Item(565, 236), 756, 880),
    BOLT_RACK(new Item(4740, 191), 881, 1005),
    LOOP_KEY_HALF(new Item(987), 1006, 1),
    TOOTH_KEY_HALF(new Item(985), 1006, 1),
    DRAGON_MED_HELM(new Item(1149), 1012, 1);

    static BarrowsReward[] values = values();
    final Item item;
    final int requiredPotential;
    final int maximumPotential;

}
