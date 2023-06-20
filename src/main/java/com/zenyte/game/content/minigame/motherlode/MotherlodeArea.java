package com.zenyte.game.content.minigame.motherlode;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.LayableTrapRestrictionPlugin;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.events.LoginEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class MotherlodeArea extends Area implements CannonRestrictionPlugin, LayableTrapRestrictionPlugin {

    @AllArgsConstructor
    private enum NuggetRefund {
        PROSPECTOR_HAT(12013, 40),
        PROSPECTOR_JACKET(12014, 60),
        PROSPECTOR_LEGS(12015, 50),
        PROSPECTOR_BOOTS(12016, 30),
        SOFT_CLAY_PACK(12009, 10),
        BAG_FULL_OF_GEMS(19473, 40);

        private final int id, originalCost;
        private static final NuggetRefund[] values = values();
    }

    @Subscribe
    public static final void onLogin(final LoginEvent event) {
        val player = event.getPlayer();
        if (player.getAttributes().containsKey("Refunded motherlode mine nuggets")) {
            return;
        }
        player.getAttributes().put("Refunded motherlode mine nuggets", true);
        int count = 0;
        for (val nuggetReward : NuggetRefund.values) {
            val amount = player.getAmountOf(nuggetReward.id);
            val refund = nuggetReward.originalCost - (int) (nuggetReward.originalCost / 2.5F);
            count += refund * amount;
        }
        if(player.getBooleanAttribute("motherlode_upstairs")) {
            count += 60;
        }
        if(player.getBooleanAttribute("motherlode_sack_upgrade")) {
            count += 120;
        }
        if (count > 0) {
            player.getInventory().addItem(new Item(12012, count)).onFailure(item -> {
                if (player.getGameMode() == GameMode.ULTIMATE_IRON_MAN) {
                    World.spawnFloorItem(item, player);
                } else {
                    player.getBank().add(item).onFailure(remaining -> World.spawnFloorItem(remaining, player));
                }
            });
            player.sendMessage(Colour.RS_RED.wrap(count + " golden nuggets have been refunded to you."));
            player.getDialogueManager().start(new ItemChat(player, new Item(GOLDEN_NUGGET, count), count + " golden nuggets have been refunded to you."));
        }
    }

    private static final int GOLDEN_NUGGET = 12012;
    public static final Item NUGGET = new Item(GOLDEN_NUGGET, 1);

    @Getter private final Int2ObjectMap<OreVein> lowerOreMap = new Int2ObjectOpenHashMap<>();
    @Getter private final Int2ObjectMap<OreVein> higherOreMap = new Int2ObjectOpenHashMap<>();

    @Getter private final Int2ObjectMap<WorldObject> rockfallMap = new Int2ObjectOpenHashMap<>();


    public static final Map<Boolean, Boolean> WATER_WHEELS = new HashMap() {{
        put(true, false);
        put(false, false);
    }};

    public MotherlodeArea() {
        spawn();
    }

    void spawn() {
        val region = World.getRegion(14936, true);
        val objects = region.getObjects().values();
        for (val object : objects) {
            if (object.getId() > 26664 && object.getId() < 26669) {
                val vein = new OreVein(object);
                World.spawnObject(vein);
                if (UpperMotherlodeArea.polygon.contains(vein)) {
                    higherOreMap.put(vein.getPositionHash(), vein);
                } else {
                    lowerOreMap.put(vein.getPositionHash(), vein);
                }
            } else if (object.getId() >= 26679 && object.getId() <= 26680) {
                rockfallMap.put(object.getPositionHash(), object);
            }
        }
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{{3648, 5728}, {3648, 5567}, {3840, 5568}, {3840, 5727}}, 0)
        };
    }

    @Override
    public void enter(Player player) {
        GameInterface.MOTHERLODE_MINE.open(player);
    }

    @Override
    public void leave(Player player, boolean logout) {
        player.getInterfaceHandler().closeInterface(GameInterface.MOTHERLODE_MINE);
    }

    @Override
    public String name() {
        return "Motherlode Mine";
    }
}
