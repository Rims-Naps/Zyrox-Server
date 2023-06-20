package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.content.treasuretrails.ClueLevel;
import com.zenyte.game.content.treasuretrails.clues.emote.ItemRequirement;
import com.zenyte.game.world.entity.player.Emote;
import com.zenyte.game.world.region.RSPolygon;
import lombok.Data;

import java.util.List;

/**
 * @author Kris | 23/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public final class EmoteRequest implements ClueChallenge {
    private final List<Emote> emotes;
    private final boolean agents;
    private final ItemRequirement[] requirements;
    private final RSPolygon polygon;
    private final ClueLevel level;
}
