package com.zenyte.game.content.skills.magic.spells.regular;

import com.zenyte.game.content.skills.magic.SpellState;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.ItemSpell;
import com.zenyte.game.content.skills.smithing.SmeltableBar;
import com.zenyte.game.content.skills.smithing.Smelting;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.GameTab;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.plugins.item.CoalBag;
import lombok.val;
import lombok.var;

import static com.zenyte.game.content.skills.smithing.Smelting.GOLDSMITH_GAUNTLETS;

/**
 * @author Kris | 8. jaan 2018 : 1:45.42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class SuperheatItem implements ItemSpell {

    private static final Graphics SPLASH = new Graphics(339, 0, 96);
    private static final Animation ANIM = new Animation(723);
    private static final Graphics GFX = new Graphics(148, 0, 100);

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public boolean spellEffect(final Player player, final Item item, final int slot) {
        superheat(player, item, slot);
        return false;
    }
    
    private void superheat(final Player player, final Item item, final int slot) {
        if (player.getTemporaryAttributes().get("performing superheat") == null) {
            val state = new SpellState(player, getLevel(), getRunes());
            if (!state.check()) {
                return;
            }
            if (player.getInventory().getItem(slot) != item) {
                return;
            }
            player.stop(Player.StopType.ACTIONS);
    
            val isIron = item.getId() == 440 && ((player.getInventory().getAmountOf(453) + Smelting.getAmountOfCoalInCoalBag(player) < 2) || player.getSkills().getLevel(Skills.SMITHING) < 30);
            val data = SmeltableBar.getData(item.getId(), isIron);

            if (data == null) {
                if (item.getId() == 453) {
                    player.sendMessage("You need to cast the spell on the primary ore of the bar instead of coal.");
                    return;
                }
                player.sendMessage("You need to cast superheat item on ore.");
                player.setGraphics(SPLASH);
                return;
            }
            if (!Smelting.hasMaterials(data, player, false, false)) {
                player.sendMessage("You need " + data.getMaterials()[0].getAmount() + " x " + data.getMaterials()[0].getName() + " & " + data.getMaterials()[1].getAmount() + " x " + data.getMaterials()[1].getName() + " to superheat this bar.");
                return;
            }
            if (player.getSkills().getLevel(Skills.SMITHING) < data.getLevel()) {
                player.sendMessage("You need at least level " + data.getLevel() + " smithing to smelt this bar.");
                return;
            }
            player.getTemporaryAttributes().put("performing superheat", true);
            player.setLunarDelay(getDelay());
            state.remove();
    
            var items = new Item[data.getMaterials().length];
            var coalToRemove = 0;
            for (int index = 0; index < items.length; index++) {
                val material = data.getMaterials()[index];
                var materialAmount = material.getAmount();
        
                if (material.getId() == Smelting.COAL.getId()) {
                    val originalAmount = materialAmount;
                    materialAmount = Math.min(player.getInventory().getAmountOf(Smelting.COAL.getId()), materialAmount);
                    coalToRemove = originalAmount == materialAmount ? 0 : originalAmount - materialAmount;
                }
        
                items[index] = new Item(material.getId(), materialAmount);
            }
    
            for (final Item material : items) {
                player.getInventory().deleteItem(material);
            }
            val coalBagAmount = Smelting.getAmountOfCoalInCoalBag(player);
            if (coalBagAmount > 0 && coalToRemove > 0) {
                player.getInventory().getContainer().getFirst(CoalBag.ITEM.getId()).getValue().setAttribute("coal", coalBagAmount - coalToRemove);
            }
            player.getInventory().addItem(data.getProduct());
            player.getSkills().addXp(Skills.MAGIC, 53);
            this.addXp(player, 53);
            if (data.equals(SmeltableBar.GOLD_BAR) && (player.getEquipment().getId(EquipmentSlot.HANDS) == GOLDSMITH_GAUNTLETS.getId() || SkillcapePerk.SMITHING.isEffective(player))) {
                player.getSkills().addXp(Skills.SMITHING, data.getXp() + 33.7);
            } else {
                player.getSkills().addXp(Skills.SMITHING, data.getXp());
            }
            if (data.equals(SmeltableBar.IRON_BAR)) {
                player.getDailyChallengeManager().update(SkillingChallenge.SMELT_IRON_BARS);
            } else if (data.equals(SmeltableBar.GOLD_BAR)) {
                player.getDailyChallengeManager().update(SkillingChallenge.SMELT_GOLD_BARS);
            } else if (data.equals(SmeltableBar.MITHRIL_BAR)) {
                player.getDailyChallengeManager().update(SkillingChallenge.SMELT_MITHRIL_BARS);
            } else if (data.equals(SmeltableBar.ADAMANTITE_BAR)) {
                player.getDailyChallengeManager().update(SkillingChallenge.SMELT_ADAMANT_BARS);
            } else if (data.equals(SmeltableBar.RUNITE_BAR)) {
                player.getDailyChallengeManager().update(SkillingChallenge.SMELT_RUNITE_BARS);
            }
            player.getInterfaceHandler().openGameTab(GameTab.SPELLBOOK_TAB);
            player.setAnimation(ANIM);
            player.setGraphics(GFX);
            player.getActionManager().setActionDelay(Math.max(player.getActionManager().getActionDelay(), 2));
            WorldTasksManager.schedule(() -> {
                player.getTemporaryAttributes().remove("performing superheat");
                if (player.getTemporaryAttributes().get("next superheat item") instanceof Item) {
                    val slot1 = Integer.parseInt(player.getTemporaryAttributes().remove("next superheat slot").toString());
                    val item1 = (Item) player.getTemporaryAttributes().remove("next superheat item");
                    superheat(player, item1, slot1);
                }
            }, 1);
        } else {
            player.getTemporaryAttributes().put("next superheat item", item);
            player.getTemporaryAttributes().put("next superheat slot", slot);
        }
    }

    @Override
    public Spellbook getSpellbook() {
        return Spellbook.NORMAL;
    }

}
