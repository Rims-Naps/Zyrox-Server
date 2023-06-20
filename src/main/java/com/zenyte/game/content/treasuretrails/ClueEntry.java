package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.content.treasuretrails.challenges.ClueChallenge;
import com.zenyte.game.content.treasuretrails.clues.Clue;
import com.zenyte.game.item.Item;
import lombok.Data;

/**
 * @author Kris | 07/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class ClueEntry {
    private final int slot;
    private final Item item;
    private final Clue clueScroll;
    private final ClueChallenge challenge;
}
