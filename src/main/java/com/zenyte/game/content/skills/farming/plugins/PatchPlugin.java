package com.zenyte.game.content.skills.farming.plugins;

import com.zenyte.game.content.skills.farming.FarmingPatch;
import com.zenyte.game.content.skills.farming.FarmingProduct;
import com.zenyte.game.content.skills.farming.PatchType;
import com.zenyte.game.content.skills.farming.RedwoodTree;
import com.zenyte.game.content.skills.farming.actions.*;
import com.zenyte.game.content.skills.woodcutting.actions.Woodcutting;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import mgi.types.config.enums.Enums;
import com.zenyte.plugins.dialogue.SpiritTreeD;
import com.zenyte.plugins.dialogue.SpiritTreeMenuD;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.val;

/**
 * @author Kris | 10. nov 2017 : 22:27.04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class PatchPlugin implements ObjectAction {

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val spot = player.getFarming().create(object);
        val manager = player.getActionManager();
        switch (option) {
            case "Guide":
                player.getSkills().sendSkillMenu(Enums.SKILL_GUIDES_ENUM.getKey(Skills.SKILLS[Skills.FARMING]).orElseThrow(RuntimeException::new), spot.getPatch().getType().getVarbit());
                return;
            case "Rake":
                manager.setAction(new Raking(spot));
                break;
            case "Clear":
                manager.setAction(new Clearing(spot));
                break;
            case "Inspect":
                manager.setAction(new Inspecting(spot));
                break;
            case "Check-health":
                manager.setAction(new HealthChecking(spot));
                break;
            case "Cut":
            case "Chop":
            case "Chop down":
            case "Chop-down":
                assert spot.getProduct().isTree();
                manager.setAction(new Woodcutting(object, spot.getProduct().getTreeDefinitions(), () -> spot.setChoppedDown(object.getId())));
                break;
            case "Cure":
            case "Prune":
                manager.setAction(new Curing(object, spot));
                break;
            case "Remove":
                assert spot.getProduct() == FarmingProduct.SCARECROW;
                manager.setAction(new ScarecrowRemoval(spot));
                break;
            case "Talk-to":
                assert spot.getProduct() == FarmingProduct.SPIRIT_TREE;
                player.getDialogueManager().start(new SpiritTreeD(player));
                break;
            case "Travel":
                assert spot.getProduct() == FarmingProduct.SPIRIT_TREE;
                player.getDialogueManager().start(new SpiritTreeMenuD(player));
                break;
            case "Take":
                assert spot.getPatch().getType() == PatchType.COMPOST_BIN;
                manager.setAction(new BinClearing(spot));
                break;
            case "Dump":
            case "Open":
            case "Close":
                assert spot.getPatch().getType() == PatchType.COMPOST_BIN;
                if (option.equalsIgnoreCase("Dump")) {
                    player.getDialogueManager().start(new Dialogue(player) {
                        @Override
                        public void buildDialogue() {
                            options("Dump the entire contents of the bin?", new DialogueOption("Yes, throw it all " +
                                    "away.", () -> manager.setAction(new BinInteraction(spot, option))), new DialogueOption("No, keep it."));
                        }
                    });
                    return;
                }
                manager.setAction(new BinInteraction(spot, option));
                break;
            case "Harvest":
            case "Pick":
            case "Pick-spine":
            case "Pick-coconut":
            case "Pick-dragonfruit":
            case "Pick-fruit":
            case "Pick-pineapple":
            case "Pick-leaf":
            case "Pick-orange":
            case "Pick-banana":
            case "Pick-apple":
            case "Pick-from":
                manager.setAction(new Harvesting(spot));
                break;
            case "Dig":
                assert spot.getPatch().getType() == PatchType.GRAPEVINE_PATCH;
                manager.setAction(new GrapevineTreating(spot));
                break;
        }
    }

    @Override
    public Object[] getObjects() {
        val list = new IntOpenHashSet();
        for (val patch : FarmingPatch.values) {
            for (int i : patch.getIds()) {
                list.add(i);
            }
        }
        for (val redwood : RedwoodTree.RedwoodTreeBranch.values()) {
            list.add(redwood.getId());
        }
        return list.toArray();
    }

    private static final int REDWOOD_PATCH_CENTER_OBJECT = 34055;

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId,
                  final String option) {
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object,
                object.getId() == REDWOOD_PATCH_CENTER_OBJECT ? 3 : 0), getRunnable(player, object, name, optionId,
                option), getDelay()));
    }

    @Override
    public int getDelay() {
        return 1;
    }

}
