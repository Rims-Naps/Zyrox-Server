package com.zenyte.game.content;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerPolicy;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.var.VarCollection;
import mgi.types.config.enums.Enums;
import com.zenyte.plugins.events.InitializationEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.util.Optional;

/**
 * @author Kris | 01/11/2018 14:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public class ItemRetrievalService {

    @RequiredArgsConstructor
    public enum RetrievalServiceType {
        ZUL_GWENWYNIG(0, 0),
        PETRIFIED_PETE(1, 2),
        MAGICAL_CHEST(3, 4),
        TORFINN(5, 6),
        LUNAR(7, 8),
        LOW_COST_THEATRE_OF_BLOOD(9, 10),
        THEATRE_OF_BLOOD(11, 12),
        ORRVOR_QUO_MATEN(13, 14),
        ARNO(15, 16),
        MIMIC(17, 18),
        GODWARS(100, 101),
        HAGAVIK(102, 103);

        @Getter private final int lockedEnumIndex, unlockedEnumIndex;

        public boolean isFree() {
            return lockedEnumIndex == unlockedEnumIndex;
        }

        public int getCost() {
            return Enums.ITEM_RETRIEVAL_SERVICE.getValue(lockedEnumIndex).orElseThrow(Enums.exception());
        }

    }

    private static final VarCollection[] varps = new VarCollection[] {
            VarCollection.ZULRAH_RECLAIM, VarCollection.VORKATH_RECLAIM
    };

    public static final void updateVarps(final Player player) {
        for (val varp : varps) {
            varp.update(player);
        }
    }


    public static final int TYPE_VAR = 261;

    public ItemRetrievalService(final Player player) {
        this.player = player;
        this.container = new Container(ContainerPolicy.NORMAL, ContainerType.ITEM_RETRIEVAL_SERVICE, Optional.of(player));
    }

    private transient final Player player;
    @Getter @Setter private RetrievalServiceType type;
    @Getter @Setter private boolean locked;
    @Getter private final Container container;

    @Subscribe
    public static final void onInit(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val service = player.getRetrievalService();
        if (savedPlayer == null) {
            return;
        }
        val savedService = savedPlayer.getRetrievalService();
        if (savedService == null)
            return;
        service.type = savedService.type;
        service.locked = savedService.locked;
        service.container.setContainer(savedService.container);
    }

    public boolean is(final RetrievalServiceType type) {
        return this.type == type && !container.isEmpty();
    }

}
