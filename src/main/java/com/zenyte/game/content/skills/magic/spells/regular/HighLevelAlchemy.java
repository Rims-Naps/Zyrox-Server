package com.zenyte.game.content.skills.magic.spells.regular;

import com.zenyte.game.content.achievementdiary.plugins.item.ExplorersRing;
import com.zenyte.game.content.minigame.castlewars.CastleWarsArea;
import com.zenyte.game.content.skills.magic.SpellState;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.ItemSpell;
import com.zenyte.game.content.skills.magic.spells.lunar.SpellbookSwap;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.GameTab;
import com.zenyte.game.ui.testinterfaces.ExplorerRingInterface;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 8. jaan 2018 : 2:08.51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public final class HighLevelAlchemy implements ItemSpell {

	private static final Animation ANIM = new Animation(713);
	private static final Graphics GFX = new Graphics(113, 0, 96);

	@Override
	public int getDelay() {
		return 0;
	}

    @Override
    public void execute(final Player player, final Item item, final int slot) {
        if (!canCast(player) && !ExplorersRing.usingRing(player)) {
            player.sendMessage("You cannot cast that spell on this spellbook.");
            return;
        }
        if (!canUse(player)) {
            return;
        }
        val spellDelay = player.getNumericTemporaryAttribute("spellDelay").longValue();
        if (spellDelay > Utils.currentTimeMillis()) {
            return;
        }
        if (player.isLocked()) {
            return;
        }
        if (!ExplorersRing.check(player, new SpellState(player, getLevel(), getRunes()))) {
            return;
        }
        player.getInterfaceHandler().closeInterfaces();
        if (item != null){
            spellEffect(player, item, slot);
        }
    }

    @Override
    public boolean spellEffect(final Player player, final Item item, final int slot) {
        if (!item.isAlchemisable()) {
            player.sendMessage("You cannot high-alchemy that!");
            return false;
        }
        if (player.inArea(CastleWarsArea.class)) {
            player.sendMessage("You cannot cast alchemy spells in Castle-Wars.");
            return false;
        }
        alchemy(player, item, slot);
        return false;
    }

    private void alchemy(final Player player, final Item item, final int slot) {
        val price = ItemDefinitions.getSellPrice(item.getId());
        if (player.getTemporaryAttributes().remove("IgnoreAlchWarning") == null) {
            val alchemyWarning = player.getNumericAttribute("ALCHEMY_WARNING_VALUE").intValue();
            if (alchemyWarning <= price) {
                player.getDialogueManager().start(new Dialogue(player) {
                    @Override
                    public void buildDialogue() {
                        plain(Colour.RED + "Warning:<br>" + Colour.END
                                + " You are about to alchemy an expensive item.<br>Are you sure you wish to continue?");
                        options("Alchemy the item?", "Yes, alchemy it.", "No, keep the item.").onOptionOne(() -> {
                            if (player.getInventory().getItem(slot) == item) {
                                player.getTemporaryAttributes().put("IgnoreAlchWarning", true);
                                alchemy(player, item, slot);
                            }
                        });
                    }
                });
                return;
            }
        }
        if (player.getTemporaryAttributes().get("performing high alchemy") == null) {
            val state = new SpellState(player, getLevel(), getRunes());
            val casts = player.getVariables().getFreeAlchemyCasts();
            if (!ExplorersRing.check(player, state)) {
                return;
            }
            if (player.getInventory().getItem(slot) != item) {
                return;
            }
            player.getTemporaryAttributes().put("performing high alchemy", true);
            player.setLunarDelay(getDelay());
            if (!ExplorersRing.usingRing(player)) {
                state.remove();
            }
            SpellbookSwap.checkSpellbook(player);
            int value = (int) (item.getDefinitions().getPrice() * 0.6F);
            final int[] CRYSTALARRAY = new int[] {23927, 23929, 23931, 23933, 23935, 23937, 23939, 23941, 32176, 32178};
            if (ArrayUtils.contains(CRYSTALARRAY, item.getId())) {
                if (item.getName().contains("Hefin")
                        || item.getName().contains("Amlodd")
                        || item.getName().contains("Iorwerth")
                        || item.getName().contains("Cadarn")
                        || item.getName().contains("Trahaearn")
                        || item.getName().contains("Crwys")) {
                    value = 2_000_000;
                } else if (item.getName().contains("Calen")) {
                    value = 5_000_000;
                } else if (item.getName().contains("Ithell")
                        || item.getName().contains("Zenyte")
                        || item.getName().contains("Meilyr")) {
                    value = 500_000;
                }
            }
            player.log(LogLevel.INFO, "High alchemy used on item " + item + " for a value of " + value + ".");
            player.getInventory().deleteItem(item.getId(), 1);
            player.getInventory().addItem(new Item(995, value)).onFailure(it -> World.spawnFloorItem(it, player));
            player.sendSound(new SoundEffect(97));
            player.setAnimation(ANIM);
            player.setGraphics(GFX);
            if (ExplorersRing.usingRing(player)) {
                player.getVariables().setFreeAlchemyCasts(casts + 1);
                player.getVarManager().sendBit(ExplorerRingInterface.CHARGES_VARBIT, casts + 1);
            } else {
                addXp(player, 65);
            }
            player.getInterfaceHandler().openGameTab(GameTab.SPELLBOOK_TAB);
            WorldTasksManager.schedule(() -> {
                player.getTemporaryAttributes().remove("performing high alchemy");
                if (player.getTemporaryAttributes().get("next high alchemy item") instanceof Item) {
                    val slot1 = Integer.parseInt(player.getTemporaryAttributes().remove("next high alchemy slot").toString());
                    val item1 = (Item) player.getTemporaryAttributes().remove("next high alchemy item");
                    alchemy(player, item1, slot1);
                }
            }, 4);
        } else {
            player.getTemporaryAttributes().put("next high alchemy item", item);
            player.getTemporaryAttributes().put("next high alchemy slot", slot);
        }
    }

	@Override
	public Spellbook getSpellbook() {
		return Spellbook.NORMAL;
	}
}
