package com.zenyte.game.content.tournament.plugins;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;
import lombok.val;

/**
 * @author Tommeh | 31/05/2019 | 20:00
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TournamentGuardLobby extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                val obj = player.getArea();
                if (!(obj instanceof TournamentLobby)) {
                    return;
                }
                val area = (TournamentLobby) obj;
                player.getDialogueManager().start(new Dialogue(player, npc) {

                    @Override
                    public void buildDialogue() {
                        npc("Greetings warrior, I'm the Tournament Guard. What can I do for you?");
                        options(TITLE, "Can you tell me more about the tournament?", "Can I spectate a fight?", "Nevermind.")
                                .onOptionOne(() -> setKey(5))
                                .onOptionTwo(() -> setKey(15))
                                .onOptionThree(() -> setKey(25));
                        player(5, "Can you tell me more about this tournament?");
                        npc("Yes of course! Every once in awhile a tournament will be started. Everyone is able to join and you will be granted a reward if you want to win all rounds!");
                        npc("Every tournament comes with a selected preset load-out. All the participants will be using this preset during their fights.");
                        npc("While you're in the lobby, you will however be able to take supplies like runes, food, potions and special-attack weapons from the Tournament Merchant.");
                        npc("In order to participate, you must bank your inventory and equipment. You cannot enter the tournament if you're still holding any items with you!").executeAction(() -> setKey(1));

                        player(15, "Can I spectate a fight?");
                        if (area.getTournament().isFinished()) {
                            npc("The tournament has unfortunately already ended. Come back some other time.");
                        } else if (area.getTournament().getRound() == 0) {
                            npc("The tournament has not started yet. It will start soon enough."); //TODO give exact time
                        } else {
                            npc("Yes of course! Just select a fight you would like to view.").executeAction(() -> GameInterface.TOURNAMENT_VIEWER.open(player));
                        }

                        player(25, "Nevermind.");

                    }
                });
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new TileEvent(player, new TileStrategy(npc.getLocation().transform(player.getAttributes().containsKey("was inside tournament lobby") ?
                                Direction.NORTH : Direction.EAST,
                        1), 0), () -> {
                    player.stopAll();
                    player.faceEntity(npc);
                    this.handle(player, npc);
                }));
            }
        });

        bind("Spectate", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                val obj = player.getArea();
                if (!(obj instanceof TournamentLobby)) {
                    return;
                }
                val area = (TournamentLobby) obj;
                if (area.getTournament().isFinished()) {
                    player.getDialogueManager().start(new NPCChat(player, 10011, "The tournament has unfortunately already ended. Come back some other time."));
                    return;
                }
                if (area.getTournament().getRound() == 0) {
                    player.getDialogueManager().start(new NPCChat(player, 10011, "The tournament has not started yet. It will start soon enough.")); //TODO give exact time
                    return;
                }
                if (!player.getInterfaceHandler().isResizable()) {
                    player.getDialogueManager().start(new NPCChat(player, 10011, "You cannot spectate other fights while on fixed mode. Switch to resizable mode first."));
                    return;
                }
                GameInterface.TOURNAMENT_VIEWER.open(player);
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new TileEvent(player, new TileStrategy(npc.getLocation().transform(player.getAttributes().containsKey("was inside tournament lobby") ?
                        Direction.NORTH : Direction.EAST, 1), 0), () -> {
                    player.stopAll();
                    player.faceEntity(npc);
                    this.handle(player, npc);
                }));
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 10011 };
    }
}
