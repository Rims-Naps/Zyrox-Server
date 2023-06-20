package com.zenyte.game.content.event.christmas2019.cutscenes;

import com.google.common.base.Preconditions;
import com.zenyte.game.content.event.christmas2019.ChristmasConstants;
import com.zenyte.game.content.event.christmas2019.ChristmasUtils;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.cutscene.Cutscene;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraLookAction;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraPositionAction;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraResetAction;
import mgi.types.config.ObjectDefinitions;
import lombok.val;

import static com.zenyte.game.content.event.christmas2019.ChristmasConstants.UNFROZEN_GUESTS_HASH_KEY;

/**
 * @author Kris | 21/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PastScourgeCutsceneP2Repeat extends Cutscene {
    @Override
    public void build() {
        addActions(0, () -> player.lock(), () -> player.getVarManager().sendBit(4606, 1), new CameraPositionAction(player, new Location(2079, 5398, 0), 1600, 100, 100),
                new CameraLookAction(player, new Location(2079, 5401, 0), 200, 100, 100),
                () -> player.sendMessage("You've broken the curse in the wrong order!"));

        addActions(1, () -> player.sendMessage("The curse re-establishes itself!"),
                () -> {
            val hash = player.getNumericAttribute(UNFROZEN_GUESTS_HASH_KEY).intValue();
            val guests = ChristmasUtils.getFrozenGuestOrder(player);
            val chars = guests.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                val character = chars[i];
                val respectiveGuest = Utils.findMatching(FrozenGuest.getValues(), g -> g.getConstant() == character);
                Preconditions.checkArgument(respectiveGuest != null);
                val isUnfrozen = ((hash >> respectiveGuest.ordinal()) & 0x1) == 1;
                if (isUnfrozen) {
                    val npc = World.findNPC(respectiveGuest.getBaseNPC(), respectiveGuest.getTile(), 1).orElseThrow(RuntimeException::new);
                    npc.setInvalidAnimation(respectiveGuest.getPreFreezeAnimation());
                    player.getPacketDispatcher().sendGraphics(new Graphics(2507), npc.getLocation());
                }
            }
            player.addAttribute(UNFROZEN_GUESTS_HASH_KEY, 0);
        });



        addActions(2, () -> {
            for (val guest : FrozenGuest.getValues()) {
                player.getVarManager().sendBit(ObjectDefinitions.getOrThrow(guest.getBaseObject()).getVarbitId(), 1);
            }
        });

        val chars = ChristmasUtils.getFrozenGuestOrder(player);
        val length = chars.length();
        for (int i = 0; i < length; i++) {
            val character = chars.charAt(i);
            addActions(3 + (i * 2), () -> {
                val guest = Utils.findMatching(FrozenGuest.getValues(), g -> g.getConstant() == character);
                Preconditions.checkArgument(guest != null);
                val tile = guest.getTile();
                val object = World.getObjectWithId(tile, guest.getBaseObject());
                World.sendObjectAnimation(object, new Animation(15108));
                player.getPacketDispatcher().sendGraphics(new Graphics(1010, 0,150), tile);
            });
        }

        addActions(15, player::unlock, new CameraResetAction(player), () -> player.getVarManager().sendBit(4606, 0), () -> ChristmasConstants.refreshAllVarbits(player));
    }
}
