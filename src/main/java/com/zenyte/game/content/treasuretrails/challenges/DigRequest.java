package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.content.treasuretrails.TreasureGuardianNPC;
import com.zenyte.game.world.entity.Location;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kris | 07/04/2019 13:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class DigRequest implements ClueChallenge {

    public DigRequest(@NotNull final Location location) {
        this(location, null);
    }

    public DigRequest(@NotNull final Location location, @Nullable final TreasureGuardianNPC guardianNPC) {
        this.location = location;
        this.guardianNPC = guardianNPC;
    }

    @NotNull private final Location location;
    @Nullable private final TreasureGuardianNPC guardianNPC;

}
