package com.zenyte.game.content.skills.magic.spells.regular;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
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
import mgi.types.config.items.ItemDefinitions;
import lombok.val;

/**
 * @author Kris | 8. jaan 2018 : 1:33.41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class LowLevelAlchemy implements ItemSpell {

	private static final Animation ANIM = new Animation(712);
	private static final Graphics GFX = new Graphics(112, 0, 96);

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
            player.sendMessage("You cannot low-alchemy that!");
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
	    if (player.getTemporaryAttributes().get("performing low alchemy") == null) {
            val state = new SpellState(player, getLevel(), getRunes());
            val lowAlchemyCasts = player.getVariables().getFreeAlchemyCasts();
            if (!ExplorersRing.check(player, state)) {
                return;
            }
            if (player.getInventory().getItem(slot) != item) {
                return;
            }
            player.getTemporaryAttributes().put("performing low alchemy", true);
            player.setLunarDelay(getDelay());
            if (!ExplorersRing.hasAnyRing(player)) {
                state.remove();
            }
            SpellbookSwap.checkSpellbook(player);
            val value = (int) (item.getDefinitions().getPrice() * 0.4F);
            player.log(LogLevel.INFO, "Low alchemy used on item " + item + " for a value of " + value + ".");
            player.getAchievementDiaries().update(WildernessDiary.CAST_LOW_ALCHEMY);
            player.getInventory().deleteItem(item.getId(), 1);
            player.getInventory().addItem(new Item(995, value)).onFailure(it -> World.spawnFloorItem(it, player));
            player.sendSound(new SoundEffect(98));
            player.setAnimation(ANIM);
            player.setGraphics(GFX);
            if (ExplorersRing.usingRing(player)) {
                player.getVariables().setFreeAlchemyCasts(lowAlchemyCasts + 1);
                player.getVarManager().sendBit(ExplorerRingInterface.CHARGES_VARBIT, lowAlchemyCasts + 1);
            } else {
                addXp(player, 31);
            }
            player.getInterfaceHandler().openGameTab(GameTab.SPELLBOOK_TAB);
            WorldTasksManager.schedule(() -> {
                player.getTemporaryAttributes().remove("performing low alchemy");
                if (player.getTemporaryAttributes().get("next low alchemy item") instanceof Item) {
                    val slot1 = Integer.parseInt(player.getTemporaryAttributes().remove("next low alchemy slot").toString());
                    val item1 = (Item) player.getTemporaryAttributes().remove("next low alchemy item");
                    alchemy(player, item1, slot1);
                }
            }, 1);
        } else {
	        player.getTemporaryAttributes().put("next low alchemy item", item);
	        player.getTemporaryAttributes().put("next low alchemy slot", slot);
        }
    }

	@Override
	public Spellbook getSpellbook() {
		return Spellbook.NORMAL;
	}
}
