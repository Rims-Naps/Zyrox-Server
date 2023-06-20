package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

/**
 * @author Kris | 07/04/2019 13:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public final class TalkRequest implements ClueChallenge {
    private final int[] validNPCs;
    private Predicate<Player> predicate;
}