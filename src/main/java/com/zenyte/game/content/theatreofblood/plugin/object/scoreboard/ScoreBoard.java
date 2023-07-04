package com.zenyte.game.content.theatreofblood.plugin.object.scoreboard;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;


public class ScoreBoard implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.setFaceLocation(player.getLocation().transform(Direction.NORTH));
        var teamSize = 5;
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options(TITLE, "Solo leaderboards", "Duo leaderboards", "Trio leaderboards", "4-Man leaderboards", "5-Man leaderboards")
                        .onOptionOne(() -> {
                            open(player, 1);
                            finish();
                        })
                        .onOptionTwo(() -> {
                            open(player, 2);
                            finish();
                        })
                        .onOptionThree(() -> {
                            open(player, 3);
                            finish();
                        })
                        .onOptionFour(() -> {
                            open(player, 4);
                            finish();
                        })
                        .onOptionFive(() -> {
                            open(player, 5);
                            finish();
                        });
            }
        });
    }

    private void open(Player player, Integer teamSize) {
        ScoreBoardInterface.handleBoard(player,teamSize);
    }

    @Override
    public void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object, 0), getRunnable(player, object, name, optionId,
                option), getDelay()));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                32987
        };
    }
}
