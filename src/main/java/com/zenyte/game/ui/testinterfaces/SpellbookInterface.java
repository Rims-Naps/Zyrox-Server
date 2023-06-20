package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.content.skills.magic.SpellDefinitions;
import com.zenyte.game.content.skills.magic.spells.DefaultSpell;
import com.zenyte.game.content.skills.magic.spells.teleports.SpellbookTeleport;
import com.zenyte.game.content.skills.magic.spells.teleports.structures.HomeStructure;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author Kris | 07/01/2019 15:01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class SpellbookInterface extends Interface {

    @Override
    protected void attach() {
        put(184, "Spell filters");
        put(184, 0, "Show combat spells");
        put(184, 1, "Show teleport spells");
        put(184, 2, "Show utility spells");
        put(184, 3, "Show spells you lack the magic level to cast");
        put(184, 4, "Show spells you lack the runes to cast");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Spell filters"),
                0, 5, AccessMask.CLICK_OP1);
    }

    @Override
    protected void build() {
        bind("Show combat spells", player -> player.getSettings().toggleSetting(Setting.SHOW_COMBAT_SPELLS));
        bind("Show teleport spells", player -> player.getSettings().toggleSetting(Setting.SHOW_TELEPORT_SPELLS));
        bind("Show utility spells", player -> player.getSettings().toggleSetting(Setting.SHOW_UTILITY_SPELLS));
        bind("Show spells you lack the magic level to cast",
                player -> player.getSettings().toggleSetting(Setting.SHOW_SPELLS_YOU_LACK_THE_MAGIC_LEVEL_TO_CAST));
        bind("Show spells you lack the runes to cast",
                player -> player.getSettings().toggleSetting(Setting.SHOW_SPELLS_YOU_LACK_THE_RUNES_TO_CAST));
    }

    @Override
    public DefaultClickHandler getDefaultHandler() {
        return ((player, componentId, slotId, itemId, optionId) -> {
            if (player.isLocked()) {
                return;
            }
            val option = getOptionString(componentId, optionId);
            val spell = Magic.getSpell(player.getCombatDefinitions().getSpellbook(), SpellDefinitions.getSpellName(componentId), DefaultSpell.class);

            //The below block prevents flapping on the home teleport spell when multi-clicking it.
            if (spell != null && spell.getSpellName().toLowerCase().endsWith("zenyte home teleport")
                    && player.getActionManager().getAction() instanceof HomeStructure.HomeTeleportAction
            && player.getTemporaryAttributes().get("Current teleport spell") == ((SpellbookTeleport) spell).getDestination()
            && Objects.equals(player.getTemporaryAttributes().get("Last Spellbook Click Option"), option)) {
                return;
            }
            player.getTemporaryAttributes().put("Last Spellbook Click Option", option);
            if (spell instanceof SpellbookTeleport) {
                player.getTemporaryAttributes().put("Current teleport spell", ((SpellbookTeleport) spell).getDestination());
            }
            player.stop(Player.StopType.INTERFACES, Player.StopType.WALK, Player.StopType.ROUTE_EVENT);
            //Exception for vengeance spell as it is a direct combat spell and shouldn't interrupt combat.
            if (spell == null || !spell.getSpellName().equalsIgnoreCase("Vengeance")) {
                player.stop(Player.StopType.ACTIONS);
            }
            if (option.equals("Warnings")) {
                val name = SpellDefinitions.getSpellName(componentId);
                if ("low level alchemy".equals(name) || "high level alchemy".equals(name)) {
                    player.getDialogueManager().start(new Dialogue(player) {
                        @Override
                        public void buildDialogue() {
                            val value = player.getNumericAttribute("ALCHEMY_WARNING_VALUE").intValue();
                            options("Warning trigger: " + Utils.format(value), new DialogueOption("Change warning value", () -> {
                                finish();
                                player.sendInputInt("Set new warning value: ", val -> {
                                    player.addAttribute("ALCHEMY_WARNING_VALUE", val);
                                    player.sendMessage("Alchemy warning set to: " + val);
                                });
                            }), new DialogueOption("Cancel"));
                        }
                    });
                }
                return;
            }
            if (spell == null) {
                return;
            }
            try {
                spell.execute(player, optionId, option);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.SPELLBOOK;
    }
}
