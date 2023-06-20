package com.zenyte.game.world.entity.player;

import static com.zenyte.game.util.AccessMask.CLICK_OP10;
import static com.zenyte.game.world.entity.player.Emote.GIVE_THANKS_VARP;
import static com.zenyte.game.world.entity.player.MessageType.GLOBAL_BROADCAST;

import com.google.common.collect.ObjectArrays;
import com.zenyte.Constants;
import com.zenyte.api.client.query.DiscordVerificationPost;
import com.zenyte.api.client.webhook.GlobalBroadcastWebhook;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.BonusCoxManager;
import com.zenyte.game.BonusTobManager;
import com.zenyte.game.BonusXpManager;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.Book;
import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.boss.corporealbeast.CorporealBeastDynamicArea;
import com.zenyte.game.content.boss.grotesqueguardians.instance.GrotesqueGuardiansInstance;
import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.content.chambersofxeric.dialogue.RaidFloorOverviewD;
import com.zenyte.game.content.chambersofxeric.map.RaidArea;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.content.combatachievements.combattasktiers.EasyTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.event.DoubleDropsManager;
import com.zenyte.game.content.event.christmas2019.AChristmasWarble;
import com.zenyte.game.content.event.christmas2019.ChristmasConstants;
import com.zenyte.game.content.event.christmas2019.cutscenes.PresentScourgeCutscene;
import com.zenyte.game.content.event.christmas2019.cutscenes.ScourgeHouseInstance;
import com.zenyte.game.content.event.halloween2019.HalloweenUtils;
import com.zenyte.game.content.grandexchange.GrandExchangePriceManager;
import com.zenyte.game.content.minigame.barrows.Barrows;
import com.zenyte.game.content.minigame.fightcaves.FightCaves;
import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.content.minigame.inferno.model.InfernoWave;
import com.zenyte.game.content.minigame.inferno.npc.impl.zuk.TzKalZuk;
import com.zenyte.game.content.minigame.pestcontrol.PestControlGameType;
import com.zenyte.game.content.minigame.pestcontrol.PestControlUtilities;
import com.zenyte.game.content.minigame.wintertodt.Wintertodt;
import com.zenyte.game.content.partyroom.BirthdayEventRewardList;
import com.zenyte.game.content.partyroom.PartyRoomVariables;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.actions.Teleother;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.content.skills.slayer.Assignment;
import com.zenyte.game.content.skills.slayer.BossTask;
import com.zenyte.game.content.skills.slayer.RegularTask;
import com.zenyte.game.content.skills.slayer.SlayerMaster;
import com.zenyte.game.content.skills.slayer.SlayerTask;
import com.zenyte.game.content.theatreofblood.TheatreOfBloodRaid;
import com.zenyte.game.content.theatreofblood.TheatreRoom;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.NylocasPhase;
import com.zenyte.game.content.theatreofblood.boss.nylocas.model.WaveDefinition;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.SotetsegRoom;
import com.zenyte.game.content.theatreofblood.party.RaidingParty;
import com.zenyte.game.content.tog.juna.JunaEnterDialogue;
import com.zenyte.game.content.tournament.Tournament;
import com.zenyte.game.content.tournament.plugins.TournamentLobby;
import com.zenyte.game.content.tournament.preset.TournamentPreset;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.enums.RareDrop;
import com.zenyte.game.packet.out.Heatmap;
import com.zenyte.game.packet.out.PingStatisticsRequest;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.testinterfaces.GameNoticeboardInterface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.StringUtilities;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.broadcasts.TriviaBroadcasts;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraLookAction;
import com.zenyte.game.world.entity.player.cutscene.actions.CameraPositionAction;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.login.BackupManager;
import com.zenyte.game.world.entity.player.login.InvitedPlayersList;
import com.zenyte.game.world.entity.player.login.LoginManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import com.zenyte.game.world.entity.player.teleportsystem.PortalTeleport;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.CorporealBeastArea;
import com.zenyte.game.world.region.area.CorporealBeastCavern;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import com.zenyte.game.world.region.area.freakyforester.FreakyForesterArea;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import com.zenyte.game.world.region.area.wilderness.WildernessArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfBoundaryException;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import com.zenyte.plugins.dialogue.CountDialogue;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.dialogue.WiseOldManD;
import com.zenyte.plugins.item.DiceItem;
import com.zenyte.tools.AnimationExtractor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import mgi.GhettoPacker;
import mgi.Indice;
import mgi.types.component.ComponentDefinitions;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.ObjectDefinitions;
import mgi.types.config.VarbitDefinitions;
import mgi.types.config.enums.EnumDefinitions;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tom
 */
@Slf4j
public final class GameCommands {

	private static final Map<String, Command> COMMANDS = new HashMap<>();

	static {

		new Command(Privilege.SPAWN_ADMINISTRATOR, "test", TempCommand::run);

		new Command(Privilege.SPAWN_ADMINISTRATOR, "settobstart", (p, args) -> {
			p.sendInputString(
					"Where do you want to start tob? [maiden, bloat, nylo, sote, xarpus, verzik]", room -> {
						TheatreRoom room1 = null;
						if(room.startsWith("maiden")) {
							room1 = TheatreRoom.THE_MAIDEN_OF_SUGADINTI;
						} else if(room.startsWith("bloat")) {
							room1 = TheatreRoom.THE_PESTILENT_BLOAT;
						} else if(room.startsWith("nylo")) {
							room1 = TheatreRoom.THE_NYLOCAS;
						} else if(room.startsWith("sote")) {
							room1 = TheatreRoom.SOTETSEG;
						} else if(room.startsWith("xarpus")) {
							room1 = TheatreRoom.XARPUS;
						} else if(room.startsWith("verzik")) {
							room1 = TheatreRoom.VERZIK;
						}
						if(room1 == null) {
							p.sendMessage("Room by the name of " + room + " not found.");
							return;
						}
						TheatreOfBloodRaid.theatreRoom = room1;
						p.sendMessage("Tob starting room set to: " + room1.getName() + ".");

					});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "tobca", (p, args) -> {
			String playerName = args[0];
			val target = World.getPlayer(playerName);
			if(!target.isPresent()) {
				p.sendMessage(playerName + " is not logged in.");
				return;
			}
			val player = target.get();
			for(int taskId = 55; taskId < 70; taskId++) {
				String tier = "master";
				val key = (String) (tier + "-combat-achievement" + taskId);
				player.putBooleanAttribute(key, false);
			}
			for(int taskId = 37; taskId < 43; taskId++) {
				String tier = "grandmaster";
				val key = (String) (tier + "-combat-achievement" + taskId);
				player.putBooleanAttribute(key, false);
			}
			for(int taskId = 1; taskId < 6; taskId++) {
				val key = (String) ("tobpb" + taskId);
				if(player.getAttributes().containsKey(key)) {
					player.getAttributes().remove(key);
				}
			}
			player.sendMessage("Your tob combat achievements and pb's have been reset.");
			p.sendMessage(player.getName() + "'s tob ca and pb's have been reset.");
		});

		new Command(Privilege.GLOBAL_MODERATOR, "checkpoints", (p, args) -> {
			for(val player : World.getPlayers()) {
				p.sendMessage("Player: " + player.getName()
						+ ", Vote Points: " + player.getNumericAttribute("vote_points").intValue()
						+ ", Loyalty Points: " + player.getLoyaltyManager().getLoyaltyPoints()
						+ ", Event Points: " + (
						player.getBank().getContainer().getAmountOf(ItemId.EVENT_REWARD_TICKET)
								+ player.getInventory().getContainer().getAmountOf(ItemId.EVENT_REWARD_TICKET)));
			}
		});

		new Command(Privilege.MODERATOR, "eventhost", (p, args) -> {
			if(p.getArea() instanceof WildernessArea) {
				p.sendMessage("Can't set this to the Wildy");
				return;
			}
			Constants.EVENTSET = !Constants.EVENTSET;
			if(Constants.EVENTSET) {
				Constants.EVENTHOSTLOCATION = new Location(p.getX(), p.getY(), p.getPlane());
				p.sendMessage(
						"Event Location set on (x,y,z): (" + p.getX() + ", " + p.getY() + ", " + p.getPlane()
								+ ").");
			} else {
				p.sendMessage("Event Location turned off.");

			}
		});

		new Command(Privilege.PLAYER, "eventtele", (p, args) -> {
			if(p.getAttributes().containsKey("eventteletimer")) {
				if((System.currentTimeMillis() - p.getNumericAttribute("eventteletimer").longValue())
						< 5 * 60 * 1000) {
					p.sendMessage("You can only use this command once every 5 minutes.");
					return;
				}
			}
			if(Constants.EVENTSET) {
				p.sendMessage("Moved to event.");
				p.setLocation(Constants.EVENTHOSTLOCATION);
				p.getAttributes().put("eventteletimer", System.currentTimeMillis());
			} else {
				p.sendMessage("There is no event location set.");
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "findstaff", (p, args) -> {
			p.sendMessage("Checking players");
			for(val player : World.getPlayers()) {
				p.sendMessage("Checking " + player.getName());
				if(player.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)) {
					p.sendMessage("Player: " + player.getName() + ", Staff role: " + player.getPrivilege());
				}
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "setca", (p, args) -> {
			val tier = (String) args[0];
			val taskId = Integer.parseInt(args[1]);
			String playerName = "";
			if(args.length >= 3) {
				playerName = (String) args[2];
			}
			String bool = "";
			if(args.length == 4) {
				bool = (String) args[3];
			}
			val key = (String) (tier.toLowerCase() + "-combat-achievement" + taskId);
			if(playerName.equals("")) {
				p.sendMessage("Invalid use: ::setca tiername taskid targetname bool");
				return;
			}
			if(bool.equals("")) {
				p.sendMessage("Invalid use: ::setca tiername taskid targetname bool");
				return;
			}
			val target = World.getPlayer(playerName);
			if(!target.isPresent()) {
				p.sendMessage(playerName + " is not logged in.");
				return;
			}
			val t = target.get();
			if(bool.contains("true")) {
				if(tier.contains("easy")) {
					EasyTasks.sendEasyCompletion(t, taskId);
					t.putBooleanAttribute(key, true);
				} else if(tier.contains("medium")) {
					MediumTasks.sendMediumCompletion(t, taskId);
					t.putBooleanAttribute(key, true);
				} else if(tier.contains("hard")) {
					HardTasks.sendHardCompletion(t, taskId);
					t.putBooleanAttribute(key, true);
				} else if(tier.contains("elite")) {
					t.putBooleanAttribute(key, true);
					EliteTasks.sendEliteCompletion(t, taskId);
				} else if(tier.contains("master")) {
					MasterTasks.sendMasterCompletion(t, taskId);
					t.putBooleanAttribute(key, true);
				} else if(tier.contains("grandmaster")) {
					GrandmasterTasks.sendGrandmasterCompletion(t, taskId);
					t.putBooleanAttribute(key, true);
				} else {
					p.sendMessage("Invalid tier name");
				}
			} else {
				t.putBooleanAttribute(key, false);
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetca", (p, args) -> {
			val tier = (String) args[0];
			val taskId = Integer.parseInt(args[1]);
			val key = (String) (tier.toLowerCase() + "-combat-achievement" + taskId);
			p.putBooleanAttribute(key, false);
		});

		new Command(Privilege.GLOBAL_MODERATOR, "barrowsevent", (p, args) -> {
			if(args.length == 0) {
				p.sendMessage("Incorrect use of the command, '::barrowsevent [true/false]");
				return;
			}
			val bool = (String) args[0];
			val multiplier = 4;
			if(bool.equals("true")) {
				Barrows.setBarrowsMultiplier(multiplier);
				World.sendMessage(GLOBAL_BROADCAST,
						"Barrows uniques are now " + multiplier + " times as common due to an event.");
			} else if(bool.equals("false")) {
				Barrows.setBarrowsMultiplier(1);
				World.sendMessage(GLOBAL_BROADCAST,
						"Barrows uniques rarity has been set back to normal as the event ends.");
			} else {
				p.sendMessage("Incorrect use of the command, '::barrowsevent [true/false]");
			}
		});

		new Command(Privilege.GLOBAL_MODERATOR, "wtodtevent", (p, args) -> {
			if(args.length == 0) {
				p.sendMessage("Incorrect use of the command, '::wtodtevent [true/false]");
				return;
			}
			val bool = (String) args[0];
			if(bool.equals("true")) {
				Wintertodt.setPointsMultiplier(2);
				World.sendMessage(GLOBAL_BROADCAST, "Wintertodt points have been doubled for an event!");
			} else if(bool.equals("false")) {
				Wintertodt.setPointsMultiplier(1);
				World.sendMessage(GLOBAL_BROADCAST,
						"Wintertodt points have been set back to normal as the event ends.");
			} else {
				p.sendMessage("Incorrect use of the command, '::wtodtevent [true/false]");
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetsand", (p, args) -> {
			p.getAttributes().remove("DAILY_SAND");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "daily", "Gives new daily challenges", (p, args) -> {
			int count = 3;
			if(p.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
				count = 5;
			} else if(p.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER)) {
				count = 4;
			}
			for(int i = count; i > 0; --i) {
				val challenge = p.getDailyChallengeManager().getRandomChallenge();
				if(challenge != null) {
					p.getDailyChallengeManager().assignChallenge(challenge);
				}
			}
		});

		new Command(Privilege.ADMINISTRATOR, "resetkilltimer",
				"Reset the boss kill timer for the given boss for a given player", (p, args) -> {
			if(args.length < 2) {
				p.sendMessage("Incorrect use of the command, '::resetkilltimer playername bossname");
				return;
			}
			val name = args[0];
			val target = World.getPlayer(name);
			val bossname = args[1];
			if(!target.isPresent()) {
				p.sendMessage(name + " is not logged in.");
				return;
			}
			val t = target.get();
			if(bossname.length() == 0) {
				p.sendMessage("No boss name was given.");
				return;
			}
			if(!t.getBossTimer().resetBossTimer(bossname)) {
				p.sendMessage("This player does not have a kill timer for the given boss.");
			} else {
				p.sendMessage(t.getName() + "'s kill timer for " + bossname + " has been reset.");
			}

		});

		new Command(Privilege.PLAYER, new String[] {"ans", "answer", "trivia"},
				"Use this command to provide an answer to an active trivia question.", (p, args) -> {
			if(!TriviaBroadcasts.getCurrentTriviaQuestion().equals("")) {
				String answer = StringUtilities.compile(args, 0, args.length, ' ');
				if(args.length == 0) {
					p.sendMessage("You have to give an answer!");
					return;
				}
				if(TriviaBroadcasts.isCorrectAnswer(answer)) {
					switch(TriviaBroadcasts.getTriviaWinners().size()) {
						case 0:
							TriviaBroadcasts.getTriviaWinners().add(p.getUsername());
							p.sendMessage("You have answered correctly!");
							break;
						case 1:
						case 2:
						case 3:
							if(TriviaBroadcasts.getTriviaWinners().contains(p.getUsername())) {
								p.sendMessage("You can't win twice!");
								return;
							}
							for(int i = 0; i < TriviaBroadcasts.getTriviaWinners().size(); i++) {
								if(!World.getPlayer(TriviaBroadcasts.getTriviaWinners().get(i)).isPresent()) {
									continue;
								}
								if(p.getIP().equals(
										World.getPlayer(TriviaBroadcasts.getTriviaWinners().get(i)).get().getIP())) {
									p.sendMessage("Only one submission per IP address!");
									return;
								}
							}

							p.sendMessage("You have answered correctly!");
							TriviaBroadcasts.getTriviaWinners().add(p.getUsername());
							if(TriviaBroadcasts.getTriviaWinners().size() == 4) {
								TriviaBroadcasts.announceWinners();
								TriviaBroadcasts.payWinners();
								TriviaBroadcasts.reset();
							}
							break;
						default:
							break;
					}
				} else {
					p.sendMessage(answer + " is not a correct answer, try again.");
				}
			} else {
				p.sendMessage("There isn't a currently active trivia question.");
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "setupurl", (p, args) -> {
			if(Constants.isOwner(p)) {
				Constants.UPDATE_LOG_URL = String.valueOf(args[0]);
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "addspins", (p, args) -> {
			p.getWheelOfFortune().setSpins(p.getWheelOfFortune().getSpins() + Integer.parseInt(args[0]));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "banhammer", "Ban a player in style", (p, args) -> {
			PunishmentManager.requestPunishment(p, PunishmentType.BANHAMMER);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "dd", "Modifies the double drop of an item",
				(p, args) -> {
					DoubleDropsManager.handleCommand(p, args);
				});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "ddevent", "Activates a dd event", (p, args) -> {
			if(args.length < 1) {
				p.sendMessage(
						"Incorrect usage: '::ddevent [slayer/keys/gwd/revs/wildyslayer/zulrah/clues/cox]'");
				return;
			}
			val eventName = args[0];
			final int[] DD_ARRAY = new int[100];
			if(eventName.equals("slayer")) {
				final int[] DD_ARRAY2 = new int[] {11840, 20736, 11905, 11907, 11908, 8901, 22983, 19529,
						30538, 2513, 13265,
						11286, 21270, 11998, 21726, 19679, 19681, 12002, 21730, 11235, 19683, 12004, 21028,
						13227, 13229, 22957,
						21742, 13231, 22960, 13233, 20724, 22966, 4151, 20727, 20730, 4158, 11902, 4159, 19677};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("keys")) {
				final int[] DD_ARRAY2 = new int[] {989, 990, 30915, 30912, 30540, 23083, 22374, 11942, 993,
						987, 985, 991};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("gwd")) {
				final int[] DD_ARRAY2 = new int[] {11826, 11828, 11830, 11810, 11812, 11832, 11834, 11836,
						11816, 11824, 11787, 11791, 11785, 11814, 11838, 13256};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("revs")) {
				final int[] DD_ARRAY2 = new int[] {22552, 22547, 22542, 22305, 22302, 22299, 21813, 21810,
						21807, 21804, 22557};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("zulrah")) {
				final int[] DD_ARRAY2 = new int[] {12934, 12932, 12927, 12922, 6571, 12936};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("clues")) {
				final int[] DD_ARRAY2 = new int[] {2803, 2805, 2807, 2809, 2811, 2813};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("wildy")) {
				final int[] DD_ARRAY2 = new int[] {30540, 993, 12746, 12748, 12749, 12750, 12751, 12752,
						12753, 12754, 12755, 12756, 30568};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else if(eventName.equals("cox")) {
				final int[] DD_ARRAY2 = new int[] {21019, 21021, 21025, 21035, 21080, 21001, 21016, 21013,
						20848, 21004, 21044};
				for(int i = 0; i < DD_ARRAY2.length; i++) {
					DD_ARRAY[i] = DD_ARRAY2[i];
				}
			} else {
				p.sendMessage(
						"Incorrect usage: '::ddevent [slayer/keys/gwd/revs/wildyslayer/zulrah/clues/cox]'");
				return;
			}
			for(int id : DD_ARRAY) {
				if(id == 0) {
					continue;
				}
				String idString = String.valueOf(id);
				String[] commandArgs = new String[] {idString, "true"};
				DoubleDropsManager.handleCommand(p, commandArgs);
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "removealldd", "Removes all double drops",
				(p, args) -> {
					DoubleDropsManager.removeAll();
					p.sendMessage("Removed all double drops.");
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetdd", "Removes dd statements that are off",
				(p, args) -> {
					int count = 0;
					List<Integer> doubleList = new ArrayList<>(DoubleDropsManager.DOUBLE_DROPS.keySet());
					for(int keyIndex = 0; keyIndex < doubleList.size(); keyIndex++) {
						val key = doubleList.get(keyIndex);
						if(!DoubleDropsManager.isDoubled(key)) {
							DoubleDropsManager.DOUBLE_DROPS.remove(key, false);
							count++;
						}
					}
					p.sendMessage("Deleted " + count + " false doubles from the dd list.");
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "zportal", "Opens portal interface", (p, args) -> {
			GameInterface.TELEPORT_MENU.open(p);
		});

		new Command(Privilege.ADMINISTRATOR, "togglecw",
				"Toggles the ability to start castle wars games.", (p, args) -> {
			if(Constants.isOwner(p) || p.getUsername().equalsIgnoreCase("Admin_carl")) {
				Constants.CASTLE_WARS = !Constants.CASTLE_WARS;
				p.sendMessage(
						"Castle Wars is now " + (Constants.CASTLE_WARS ? Colour.RS_GREEN.wrap("enabled.")
								: Colour.RED.wrap("disabled.")));
			} else {
				p.sendMessage("You aren't able to use this command.");
			}
		});

		new Command(Privilege.PLAYER, "checkdd", "Shows the double drop status of items.",
				(p, args) -> {
					ArrayList<String> statuses = new ArrayList<String>();
					DoubleDropsManager.DOUBLE_DROPS.forEach((id, doubled) -> {
						statuses.add(id + " (" + ItemDefinitions.get(id).getName() + ") : " + (doubled
								? Colour.RS_GREEN.wrap("doubled") : Colour.RED.wrap("not doubled")));
					});
					Diary.sendJournal(p, "Double drops: " + DoubleDropsManager.DOUBLE_DROPS.size(), statuses);
				});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "toggletob", (p, args) -> {
			if(Constants.isOwner(p)) {
				TheatreOfBloodRaid.TOB_ENABLED = !TheatreOfBloodRaid.TOB_ENABLED;
				p.sendMessage(
						"ToB is now " + (TheatreOfBloodRaid.TOB_ENABLED ? Colour.RS_GREEN.wrap("enabled.")
								: Colour.RED.wrap("disabled.")));
			} else {
				p.sendMessage("You are not able to use this command.");
			}

		});
		new Command(Privilege.GLOBAL_MODERATOR, "addbroadcast", (p, args) -> {
			val id = Integer.parseInt(args[0]);
			p.getDialogueManager().start(new Dialogue(p) {
				@Override
				public void buildDialogue() {
					options("Add " + ItemDefinitions.getOrThrow(id).getName() + " to broadcasts?",
							new DialogueOption("Yes", () -> {
								RareDrop.add(id);
								p.sendMessage(
										"Added " + ItemDefinitions.getOrThrow(id).getName() + " to custom broadcasts.");
							}),
							new DialogueOption("No"));
				}
			});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "tobpractice", (p, args) -> {
			RaidingParty rp = VerSinhazaArea.getParty(p);
			if(rp != null) {
				if(!rp.isWiped()) {
					rp.setPractice(!rp.isPractice());
					for(Player p2 : rp.getPlayers()) {
						p2.sendMessage(Colour.RED.wrap(
								"Practice mode has been turned " + (rp.isPractice() ? "ON" : "OFF")));
					}
				} else {
					p.sendMessage("Stop that.");
				}
			} else {
				p.sendMessage("You need to be in a Theatre of Blood party first.");
			}
		});

		new Command(Privilege.GLOBAL_MODERATOR, "removebroadcast", (p, args) -> {
			val broadcasts = RareDrop.getDynamicIds();
			if(broadcasts.isEmpty()) {
				p.sendMessage("No dynamic broadcasts present.");
				return;
			}
			val options = new ObjectArrayList<String>();
			for(val bc : broadcasts) {
				options.add(ItemDefinitions.getOrThrow(bc).getName());
			}
			options.add("All broadcasts");
			p.getDialogueManager()
					.start(new OptionsMenuD(p, "Select broadcast to remove", options.toArray(new String[0])) {
						@Override
						public void handleClick(int slotId) {
							if(slotId >= broadcasts.size()) {
								for(val bc : broadcasts) {
									RareDrop.remove(bc);
								}
								p.sendMessage("Wiped custom broadcasts.");
								return;
							}
							val id = broadcasts.getInt(slotId);
							RareDrop.remove(id);
							p.sendMessage(
									"Removed " + ItemDefinitions.getOrThrow(id).getName() + " from broadcasts.");
						}

						@Override
						public boolean cancelOption() {
							return true;
						}
					});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "skotaltar", "Teleports you to Skotizo's Altar",
				(p, args) -> {
					p.setLocation(new Location(1665, 10048, 0));
				});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "skotroom", "Teleports you to Skotizo's Room",
				(p, args) -> {
					p.setLocation(new Location(1693, 9886, 0));
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "dmgzuk",
				"Deals damage to zuk. E.g. ::dmgzuk 100 dealts 100 damage", (p, args) -> {
			val area = p.getArea();
			if(area instanceof Inferno) {
				val inferno = (Inferno) area;
				val zuk = inferno.getNPCs(TzKalZuk.class).get(0);
				zuk.applyHit(new Hit(p, Integer.parseInt(args[0]), HitType.REGULAR));
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "skipwave", "Skips the current inferno wave",
				(p, args) -> {
					if(!p.inArea(p.getName() + "'s Inferno Instance")) {
						p.sendMessage("You must be in the Inferno to do this.");
						return;
					}
					val inferno = (Inferno) p.getArea();
					val wave = InfernoWave.get(Integer.parseInt(args[0]));
					p.sendMessage("Skipped to wave " + wave.getWave() + ".");
					inferno.skip(wave);
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "inferno", "Teleports you to the inferno",
				(p, args) -> {
					p.setLocation(new Location(2496, 5115, 0));
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "cutscene",
				"Sends you to the christmas event cutscene", (p, args) -> {
			try {
				val instance = new ScourgeHouseInstance(MapBuilder.findEmptyChunk(4, 4));
				instance.constructRegion();
				val fadeScreen = new FadeScreen(p);
				fadeScreen.fade();
				p.getCutsceneManager()
						.play(new PresentScourgeCutscene(p, instance, () -> fadeScreen.unfade(false)));
			} catch(OutOfSpaceException e) {
				e.printStackTrace();
			}
		});

		new Command(Privilege.ADMINISTRATOR, "eventstage",
				(p, args) -> p.sendInputName("Whose event stage to update?",
						name -> World.getPlayer(name).ifPresent(target -> {
							val stages = AChristmasWarble.ChristmasWarbleProgress.values();
							val stageNames = new ObjectArrayList<String>();
							for(val stage : stages) {
								stageNames.add(stage.toString());
							}
							p.getDialogueManager()
									.start(new OptionsMenuD(p, "Select stage", stageNames.toArray(new String[0])) {
										@Override
										public void handleClick(int slotId) {
											target.getAttributes()
													.put(AChristmasWarble.ChristmasWarbleProgress.EVENT_ATTRIBUTE_KEY,
															stages[slotId].getStage());
											if(stages[slotId].ordinal()
													<= AChristmasWarble.ChristmasWarbleProgress.FROZEN_GUESTS.ordinal()) {
												target.getAttributes().remove("A Christmas Warble unfrozen guests hash");
											}
											ChristmasConstants.refreshAllVarbits(target);
										}

										@Override
										public boolean cancelOption() {
											return false;
										}
									});
						})));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetevent", (p, args) -> {
			p.getAttributes().remove(AChristmasWarble.ChristmasWarbleProgress.EVENT_ATTRIBUTE_KEY);
			p.getAttributes().remove("A Christmas Warble unfrozen guests hash");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "completeevent", (p, args) -> {
			AChristmasWarble.progress(p, AChristmasWarble.ChristmasWarbleProgress.EVENT_COMPLETE);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "progress", (p, args) -> {
			p.sendMessage(AChristmasWarble.getProgress(p).name());
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "bonusxp",
				(p, args) -> p.sendInputString("Enter bonus xp expiration date: (format: YYYY/MM/DD/HH) ",
						value -> {
							val split = value.split("/");
							if(split.length != 4) {
								p.sendMessage("Invalid format.");
								return;
							}
							val instance = Calendar.getInstance();
							instance.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1,
									Integer.parseInt(split[2]), Integer.parseInt(split[3]), 0, 0);
							p.getDialogueManager().start(new Dialogue(p) {
								@Override
								public void buildDialogue() {
									options(
											"Set bonus experience expiration date to <br>" + instance.getTime().toString()
													+ "?",
											new DialogueOption("Yes.",
													() -> BonusXpManager.set(instance.getTimeInMillis())),
											new DialogueOption("No."));
								}
							});
						}));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "bonuscox",
				(p, args) -> p.sendInputString("Enter bonus cox expiration date: (format: YYYY/MM/DD/HH) ",
						value -> {
							val split = value.split("/");
							if(split.length != 4) {
								p.sendMessage("Invalid format.");
								return;
							}
							val instance = Calendar.getInstance();
							instance.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1,
									Integer.parseInt(split[2]), Integer.parseInt(split[3]), 0, 0);
							p.getDialogueManager().start(new Dialogue(p) {
								@Override
								public void buildDialogue() {
									options(
											"Set bonus cox expiration date to <br>" + instance.getTime().toString() + "?",
											new DialogueOption("Yes.",
													() -> BonusCoxManager.set(instance.getTimeInMillis())),
											new DialogueOption("No."));
								}
							});
						}));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "bonustob",
				(p, args) -> p.sendInputString("Enter bonus xp expiration date: (format: YYYY/MM/DD/HH) ",
						value -> {
							val split = value.split("/");
							if(split.length != 4) {
								p.sendMessage("Invalid format.");
								return;
							}
							val instance = Calendar.getInstance();
							instance.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1,
									Integer.parseInt(split[2]), Integer.parseInt(split[3]), 0, 0);
							p.getDialogueManager().start(new Dialogue(p) {
								@Override
								public void buildDialogue() {
									options(
											"Set bonus tob expiration date to <br>" + instance.getTime().toString() + "?",
											new DialogueOption("Yes.",
													() -> BonusTobManager.set(instance.getTimeInMillis())),
											new DialogueOption("No."));
								}
							});
						}));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "multispawn", (p, args) -> {
			val id = Integer.parseInt(args[0]);
			val radius = args.length == 1 ? 10 : Integer.parseInt(args[1]);
			val defs = Objects.requireNonNull(NPCDefinitions.get(id));
			val size = defs.getSize();
			val px = p.getX();
			val py = p.getY();
			int count = 0;
			for(int x = px - radius; x <= px + radius; x += size) {
				for(int y = py - radius; y <= py + radius; y += size) {
					val tile = new Location(x, y, p.getPlane());
					if(p.isProjectileClipped(tile, true)) {
						continue;
					}
					if(++count > 250) {
						break;
					}
					val npc = World.spawnNPC(id, tile);
					npc.setSpawned(true);
				}
			}
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "kill", (p, args) -> {
			String name = StringUtilities.compile(args, 0, args.length, ' ');
			Optional<Player> targetPlayer = World.getPlayer(name);
			if(targetPlayer.isPresent()) {
				targetPlayer.get().applyHit(new Hit(targetPlayer.get().getHitpoints(), HitType.REGULAR));
			}
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "setattr",
				(p, args) -> p.getTemporaryAttributes().put(args[0], args[1]));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "setpermaattr", (p, args) -> p.getAttributes()
				.put(args[0], (p.getNumericAttribute(args[0]).intValue() + Integer.parseInt(args[1]))));
		new Command(Privilege.GLOBAL_MODERATOR, "resetother", "Resets the chosen skill for a player",
				(p, args) -> {
					p.sendInputName("Whose levels to reset?", n -> {
						Optional<Player> target = World.getPlayer(n);
						if(!target.isPresent()) {
							p.sendMessage("User not online.");
							return;
						}
						p.sendInputName("What skill to reset?", n2 -> {
							EnumDefinitions e = EnumDefinitions.get(680);
							for(int i = e.getSize() - 1; i >= 0; i--) {
								if(e.getStringValue(i).equalsIgnoreCase(n2)) {
									target.get().getSkills().setSkill(i, n2.equalsIgnoreCase("hitpoints") ? 10 : 1,
											n2.equalsIgnoreCase("hitpoints") ? Skills.getXPForLevel(10) : 0D);
									p.sendMessage(String.format("%s's %s level has been reset to 1.",
											target.get().getUsername(), e.getStringValue(i)));
									return;
								}
							}
						});
						p.sendMessage("Invalid skill name input.");
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "maxstats", "Maxes out all of your stats.",
				(p, args) -> {
					for(int i = 0; i < 23; i++) {
						p.getSkills().setSkill(i, 255, 200_000_000);
					}
					p.getSkills().refresh();
				});

		new Command(Privilege.ADMINISTRATOR, "setlevelother",
				"Set one of your own levels. Usage: ::setlevelother [Optional 'temp'] [Skillname or id] [Level]",
				(p, args) -> {
					p.sendInputName("Whose levels to change?", n -> {
						val target = World.getPlayer(n);
						if(!target.isPresent()) {
							p.sendMessage("User not online.");
							return;
						}
						try {
							val t = target.get();
							val temporary = args[0].equals("temp");
							val isNumber = NumberUtils.isCreatable(args[temporary ? 1 : 0]);
							val e = EnumDefinitions.get(680);
							if(isNumber) {
								val number = Integer.valueOf(args[temporary ? 1 : 0]);
								if(number < 0 || number >= e.getSize()) {
									p.sendMessage(
											"Invalid skill id of " + number + ", valid values are 0-" + e.getSize()
													+ ".");
									return;
								}
								if(temporary) {
									val level = Math.min(255, Math.max(0, Integer.parseInt(args[2])));
									t.getSkills().setLevel(number, level);
									t.log(LogLevel.INFO,
											Skills.getSkillName(number) + " has been temporarily boosted to level "
													+ level + " by " + p.getName() + ".");
									t.sendMessage(
											Skills.getSkillName(number) + " has been temporarily boosted to level "
													+ level + ".");
									p.sendMessage(
											Skills.getSkillName(number) + " has been temporarily boosted to level "
													+ level + " for " + t.getName() + ".");
									t.getAppearance().resetRenderAnimation();
								} else {
									val level = Math.min(99, Math.max(1, Integer.parseInt(args[1])));
									t.getSkills().setSkill(number, level, Skills.getXPForLevel(level));
									t.log(LogLevel.INFO,
											Skills.getSkillName(number) + " has been set to level " + level + " by "
													+ p.getName() + ".");
									t.sendMessage(
											Skills.getSkillName(number) + " has been set to level " + level + ".");
									p.sendMessage(
											Skills.getSkillName(number) + " has been set to level " + level + " for "
													+ t.getName() + ".");
									t.getAppearance().resetRenderAnimation();
								}
							} else {
								val name = args[temporary ? 1 : 0].toLowerCase();
								for(int i = e.getSize() - 1; i >= 0; i--) {
									val skillName = e.getStringValue(i);
									if(skillName.toLowerCase().startsWith(name)) {
										if(temporary) {
											val level = Math.min(255, Math.max(0, Integer.parseInt(args[2])));
											t.getSkills().setLevel(i, level);
											t.log(LogLevel.INFO,
													skillName + " has been temporarily boosted to level " + level + " by "
															+ p.getName() + ".");
											t.sendMessage(
													skillName + " has been temporarily boosted to level " + level + ".");
											p.sendMessage(
													skillName + " has been temporarily boosted to level " + level + " for "
															+ t.getName() + ".");
											t.getAppearance().resetRenderAnimation();
										} else {
											val level = Math.min(99, Math.max(1, Integer.parseInt(args[1])));
											t.getSkills().setSkill(i, level, Skills.getXPForLevel(level));
											t.log(LogLevel.INFO,
													skillName + " has been set to level " + level + " by " + p.getName()
															+ ".");
											t.sendMessage(skillName + " has been set to level " + level + ".");
											p.sendMessage(
													skillName + " has been set to level " + level + " for " + t.getName()
															+ ".");
											t.getAppearance().resetRenderAnimation();
										}
										return;
									}
								}
							}
						} catch(final Exception e) {
							p.sendMessage(
									"Invalid syntax. Use command as: ;;setlevelother [Optional 'temp'] [Skillname or id] [Level]");
						}
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "setlevel",
				"Set one of your own levels. Usage: ::setlevel [Optional 'temp'] [Skillname or id] [Level]",
				(p, args) -> {
					try {
						val temporary = args[0].equals("temp");
						val isNumber = NumberUtils.isCreatable(args[temporary ? 1 : 0]);
						val e = EnumDefinitions.get(680);
						if(isNumber) {
							val number = Integer.valueOf(args[temporary ? 1 : 0]);
							if(number < 0 || number >= e.getSize()) {
								p.sendMessage(
										"Invalid skill id of " + number + ", valid values are 0-" + e.getSize() + ".");
								return;
							}
							if(temporary) {
								val level = Math.min(255, Math.max(0, Integer.parseInt(args[2])));
								p.getSkills().setLevel(number, level);
								p.sendMessage(
										Skills.getSkillName(number) + " has been temporarily boosted to level " + level
												+ ".");
								p.getAppearance().resetRenderAnimation();
							} else {
								val level = Math.min(99, Math.max(1, Integer.parseInt(args[1])));
								p.getSkills().setSkill(number, level, Skills.getXPForLevel(level));
								p.sendMessage(
										Skills.getSkillName(number) + " has been set to level " + level + ".");
								p.getAppearance().resetRenderAnimation();
							}
						} else {
							val name = args[temporary ? 1 : 0].toLowerCase();
							for(int i = e.getSize() - 1; i >= 0; i--) {
								val skillName = e.getStringValue(i);
								if(skillName.toLowerCase().startsWith(name)) {
									if(temporary) {
										val level = Math.min(255, Math.max(0, Integer.parseInt(args[2])));
										p.getSkills().setLevel(i, level);
										p.sendMessage(
												skillName + " has been temporarily boosted to level " + level + ".");
										p.getAppearance().resetRenderAnimation();
									} else {
										val level = Math.min(99, Math.max(1, Integer.parseInt(args[1])));
										p.getSkills().setSkill(i, level, Skills.getXPForLevel(level));
										p.sendMessage(skillName + " has been set to level " + level + ".");
										p.getAppearance().resetRenderAnimation();
									}
									return;
								}
							}
						}
					} catch(final Exception e) {
						p.sendMessage(
								"Invalid syntax. Use command as: ;;setlevel [Optional 'temp'] [Skillname or id] [Level]");
					}
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "objs",
				"Prints out information on the objects in your tile location", (p, args) -> {
			val objects = World.getRegion(p.getLocation().getRegionId())
					.getObjects(p.getPlane(), p.getX() & 0x3F, p.getY() & 0x3F);
			if(objects == null) {
				p.sendMessage("No objects detected on this location.");
				return;
			}
			for(val object : objects) {
				if(object == null) {
					continue;
				}
				p.sendMessage("Object: " + object.getId() + ", type: " + object.getType() + ", rotation: "
						+ object.getRotation() + ", location: " + object.getX() + ", " + object.getY() + ", "
						+ object.getPlane() + ".");
			}
		});

		new Command(Privilege.ADMINISTRATOR, "gc", (p, args) -> {

			if(!Constants.WORLD_PROFILE.isDevelopment()) {
				return;
			}
			System.gc();

		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "nyloboss",
				"Use this at Nylocas to skip to boss phase", (p, args) -> {
			val area = p.getArea();
			if(!(area instanceof NylocasRoom)) {
				return;
			}

			val room = (NylocasRoom) area;
			if(!room.isStarted()) {
				p.sendMessage("You must go through the barrier first to make use of this command.");
				return;
			}

			if(room.getPhase() != NylocasPhase.MINIONS) {
				p.sendMessage("You cannot use this command during the boss phase.");
				return;
			}

			for(val entry : room.getNylos().entrySet()) {
				val nylo = entry.getValue();
				nylo.finish();
			}

			room.getNylos().clear();

			room.setPhase(NylocasPhase.BOSS);
		});

		new Command(Privilege.ADMINISTRATOR, "nylowave", "Sets your current nylo wave (1-31)",
				(p, args) -> {
					if(args.length != 1) {
						p.sendMessage("Usage: ::nylowave 3");
						return;
					}
					val area = p.getArea();
					if(!(area instanceof NylocasRoom)) {
						return;
					}

					val room = (NylocasRoom) area;
					if(!room.isStarted()) {
						p.sendMessage("You must go through the barrier first to make use of this command.");
						return;
					}

					if(room.getPhase() != NylocasPhase.MINIONS) {
						p.sendMessage("You cannot use this command during the boss phase.");
						return;
					}

					for(val entry : room.getNylos().entrySet()) {
						val nylo = entry.getValue();
						nylo.finish();
					}

					room.getNylos().clear();

					val wave = WaveDefinition.get(Integer.parseInt(args[0]));

					room.setWave(wave);
				});

		new Command(Privilege.ADMINISTRATOR, "tob", "Teleports you to ToB", (p, args) -> {
			p.setLocation(new Location(3664, 3220, 0));
		});

		new Command(Privilege.ADMINISTRATOR, "memory", (p, args) -> {

			val runtime = Runtime.getRuntime();
			val totalMem = runtime.totalMemory();
			val freeMem = runtime.freeMemory();
			val maxMem = runtime.maxMemory();
			p.sendMessage("Memory specifications: ");
			p.sendMessage("Free memory: " + Utils.format(freeMem));
			p.sendMessage("Used memory: " + Utils.format(totalMem - freeMem));
			p.sendMessage("Total memory: " + Utils.format(totalMem));
			p.sendMessage("Max memory: " + Utils.format(maxMem));
		});

		new Command(Privilege.ADMINISTRATOR, "disablehydra", "Disables Hydra boss", (p, args) -> {
			Constants.ALCHEMICAL_HYDRA = !Constants.ALCHEMICAL_HYDRA;
			p.sendMessage("Alchemical Hydra: " + Constants.ALCHEMICAL_HYDRA);
		});

		new Command(Privilege.ADMINISTRATOR, "disableraids", "Disables Raids", (p, args) -> {
			Constants.CHAMBERS_OF_XERIC = !Constants.CHAMBERS_OF_XERIC;
			p.sendMessage("Chambers of Xeric: " + Constants.CHAMBERS_OF_XERIC);
		});

		new Command(Privilege.JUNIOR_MODERATOR, "timeout",
				"Toggles staff logout-timer for current session.", (p, args) -> {
			p.getTemporaryAttributes().put("staff timeout disabled",
					p.getNumericTemporaryAttribute("staff timeout disabled").intValue() == 1 ? 0 : 1);
			p.sendMessage("Timeout setting: " + (
					p.getNumericTemporaryAttribute("staff timeout disabled").intValue() == 1 ? "enabled"
							: "disabled") + ".");

		});

		new Command(Privilege.MODERATOR, "bosstimers", "Opens boss spawn timer menu.",
				(p, args) -> BossRespawnTimer.open(p));

		new Command(Privilege.ADMINISTRATOR, "disablediscordbroadcast", "Toggles discord broadcasts.",
				(p, args) -> {
					GlobalBroadcastWebhook.setDisabled(!GlobalBroadcastWebhook.isDisabled());
					p.sendMessage("Discord broadcasts disabled: " + GlobalBroadcastWebhook.isDisabled());
				});

		new Command(Privilege.ADMINISTRATOR, "disablewintertodt",
				"Toggles access to the Wintertodt's prison.", (p, args) -> {
			Wintertodt.setDisabled(!Wintertodt.isDisabled());
			p.sendMessage("Wintertodt disabled: " + Wintertodt.isDisabled());
		});

		new Command(Privilege.PLAYER,
				new String[] {"gambling", "gamble", "dice", "dicing", "fp", "flowerpoker", "flower"},
				"Teleports you to Gambling area", (p, args) -> {
			if(p.isLocked()) {
				return;
			}
			p.getDialogueManager().start(new PlainChat(p, DiceItem.GAMBLE_WARNING));
		});

		new Command(Privilege.ADMINISTRATOR, "tempattr",
				(p, args) -> p.sendInputString("Enter name of the temporary attribute", key -> {
					p.getDialogueManager().finish();
					p.sendInputInt("Enter value of the temporary attribute", value -> {
						p.getTemporaryAttributes().put(key, value);
						p.sendMessage("Temporary attribute " + Colour.RS_RED.wrap(key) + " value set to "
								+ Colour.RS_RED.wrap(Integer.toString(value)));
					});
				}));

		new Command(Privilege.ADMINISTRATOR, "forceprice",
				"Initiates forcing a price change for an item.", (p, args) -> {
			p.sendInputItem("What item's price would you like to change?",
					item -> p.sendInputInt("What price would you like to set to " + item.getName() + "?",
							value -> {
								val existingPrice = ItemDefinitions.getSellPrice(item.getId());
								GrandExchangePriceManager.forcePrice(item.getId(), value);
								p.sendMessage(
										"Price of " + item.getName() + " changed from " + Utils.format(existingPrice)
												+ " to " + Utils.format(value) + ".");
							}));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "invite",
				"Grant beta access to a user. Usage: ::invite player_name", (p, args) -> {
			val name = Utils.formatUsername(StringUtilities.compile(args, 0, args.length, '_'));
			if(name.length() >= 1 && name.length() <= 12) {
				InvitedPlayersList.invitedPlayers.add(name);
				p.sendMessage("Granted beta access to " + name + ".");
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "gg", (p, args) -> {
			p.setLocation(GrotesqueGuardiansInstance.OUTSIDE_LOCATION);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "gargtask", (p, args) -> {
			p.getSlayer()
					.setAssignment(p.getSlayer().getAssignment(RegularTask.GARGOYLES, SlayerMaster.NIEVE));
			p.getSlayer().sendTaskInformation();
		});

		new Command(Privilege.ADMINISTRATOR, "uninvite",
				"Revoke beta access of a user. Usage: ::uninvite player_name", (p, args) -> {
			val name = Utils.formatUsername(StringUtilities.compile(args, 0, args.length, '_'));
			if(name.length() >= 1 && name.length() <= 12) {
				if(ArrayUtils.contains(Constants.owners, name)) {
					p.sendMessage("You cannot uninvite owners.");
					return;
				}
				InvitedPlayersList.invitedPlayers.remove(name);
				p.sendMessage("Revoked beta access from " + name + ".");
			}
		});

		new Command(Privilege.MODERATOR, "related",
				"See others users with the same ip or mac address. Usage: ::related player name",
				(p, args) -> World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '))
						.ifPresent(target -> {
							val ip = target.getIP();
							val mac = target.getMACAddress();

							val foundPlayers = new HashMap<String, String>(); // username, reason

							for(val player : World.getPlayers()) {
								val name = player.getName();
								if(!ip.isEmpty() && player.getIP().equals(ip)) {
									foundPlayers.put(name, "Matched IP Address");
								}
								if(!mac.isEmpty() && player.getMACAddress().equals(mac)) {
									if(foundPlayers.containsKey(name)) {
										foundPlayers.put(name, "Matched IP and Mac");
									} else {
										foundPlayers.put(name, "Matched Mac Address");
									}
								}
							}

							if(foundPlayers.size() == 1) { // if the only result was the player itself
								p.sendMessage("No related players found.");
							} else {
								val entries = new ArrayList<String>(foundPlayers.size());
								for(val entry : foundPlayers.entrySet()) {
									entries.add(
											Colour.BLUE.wrap(entry.getKey()) + " - reason: " + Colour.RS_GREEN.wrap(
													entry.getValue()));
								}

								Diary.sendJournal(p, "Related players: " + (entries.size() - 1), entries);
							}
						}));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "clearnullednpcs", (p, args) -> {
			//Clears the npcs which are visible in the player's viewport and have died - if they're still in dead status 10 ticks after the check.
			CharacterLoop.forEach(p.getLocation(), p.getViewDistance(), NPC.class, npc -> {
				if(npc.isDead()) {
					p.sendMessage("Dead NPC: " + npc.getName(p) + ": " + npc.getLocation());
					WorldTasksManager.schedule(() -> {
						if(npc.isDead() && !npc.isFinished()) {
							npc.setRespawnTask();
						}
					}, 10);
				}
			});
		});

		new Command(Privilege.ADMINISTRATOR, "randomfrequency", (p, args) -> {
			val value = Integer.parseInt(args[0]);
			Constants.randomEvent = (int) TimeUnit.HOURS.toTicks(value);
			p.sendMessage("Random events are on average now occuring every " + value + " hours.");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetecto",
				(p, args) -> World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '))
						.ifPresent(user -> {
							user.addAttribute("ectofuntus bone status", 0);
							user.addAttribute("ectofuntus grinded bone", 0);
							p.sendMessage(user.getName() + "'s ectofuntus settings reset.");
						}));

		new Command(Privilege.MODERATOR, "checkbob", (p, args) -> {
			if(Constants.WORLD_PROFILE.isBeta() && !Constants.isOwner(p)) {
				return;
			}
			if(!p.isLocked() && !p.isFinished() && !p.isDead()) {
				val area = p.getArea();
				if(!(area instanceof RandomEventRestrictionPlugin)) {
					p.getAttributes().put("evil bob complete", true);
					p.getAttributes().put("observing random event", true);
					EvilBobIsland.teleport(p);
				} else {
					p.sendMessage("You cannot teleport to the random event island from here.");
				}
			} else {
				p.sendMessage("You can't do that right now.");
			}
		});

		new Command(Privilege.MODERATOR, "checkfreaky", (p, args) -> {
			if(Constants.WORLD_PROFILE.isBeta() && !Constants.isOwner(p)) {
				return;
			}
			if(!p.isLocked() && !p.isFinished() && !p.isDead()) {
				val area = p.getArea();
				if(!(area instanceof RandomEventRestrictionPlugin)) {
					p.getAttributes().put("freaky forester complete", true);
					p.getAttributes().put("observing random event", true);
					FreakyForesterArea.teleport(p);
				} else {
					p.sendMessage("You cannot teleport to the random event from here.");
				}
			} else {
				p.sendMessage("You can't do that right now.");
			}
		});

		new Command(Privilege.MODERATOR, "checkrandom",
				"Tells the random event location of the user. Usage: ::checkrandom player name",
				(p, args) -> {
					if(Constants.WORLD_PROFILE.isBeta() && !Constants.isOwner(p)) {
						return;
					}
					World.getPlayer(StringUtilities.compile(args, 0, args.length, ' ')).ifPresent(target ->
					{
						if(target.getArea() instanceof FreakyForesterArea) {
							p.sendMessage(target.getUsername() + " is in freaky forester random.");
						} else if(target.getArea() instanceof EvilBobIsland) {
							p.sendMessage(target.getUsername() + " is in bob random.");
						} else {
							p.sendMessage(target.getUsername() + " is NOT in a random event.");
						}
						return;
					});
					//p.sendMessage("Couldn't find player: " + StringUtilities.compile(args, 0, args.length, ' '));
				});

		new Command(Privilege.MODERATOR, "random",
				"Initiate a random event for a user. Usage: ::random player name", (p, args) ->
				World.getPlayer(StringUtilities.compile(args, 0, args.length, ' ')).ifPresent(target ->
				{
					if(Constants.WORLD_PROFILE.isBeta() && !Constants.isOwner(p)) {
						return;
					}
					val lastEvent = target.getNumericAttribute("last random event").longValue();
					if(!p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)
							&& lastEvent + TimeUnit.MINUTES.toMillis(45) > System.currentTimeMillis()) {
						p.sendMessage(
								"That user has already played through a random event within the past 45 minutes.");
						return;
					}
					target.log(LogLevel.INFO, "Forced random event by " + p.getName() + ".");
					if(Utils.random(1) == 0) {
						EvilBobIsland.teleport(target);
					} else {
						FreakyForesterArea.teleport(target);
					}
				}));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "freaky",
				"Initiate a random event for a user. Usage: ::random player name",
				(p, args) -> World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '))
						.ifPresent(target -> {
							if(Constants.WORLD_PROFILE.isBeta() && !Constants.isOwner(p)) {
								return;
							}
							val lastEvent = target.getNumericAttribute("last random event").longValue();
							if(!p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)
									&& lastEvent + TimeUnit.MINUTES.toMillis(45) > System.currentTimeMillis()) {
								p.sendMessage(
										"That user has already played through a random event within the past 45 minutes.");
								return;
							}
							target.log(LogLevel.INFO, "Forced random event by " + p.getName() + ".");
							FreakyForesterArea.teleport(target);
						}));

		new Command(Privilege.MODERATOR, "movehome",
				"Moves another user home if they accept the request. Usage: ::movehome player name",
				(p, args) -> World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '))
						.ifPresent(target -> {
							if(p == target) {
								p.sendMessage("You can't teleport yourself.");
								return;
							}
							val teleport = new Teleport() {
								@Override
								public TeleportType getType() {
									return TeleportType.ZENYTE_PORTAL_TELEPORT;
								}

								@Override
								public Location getDestination() {
									return new Location(3087, 3489, 0);
								}

								@Override
								public int getLevel() {
									return 0;
								}

								@Override
								public double getExperience() {
									return 0;
								}

								@Override
								public int getRandomizationDistance() {
									return 3;
								}

								@Override
								public Item[] getRunes() {
									return null;
								}

								@Override
								public int getWildernessLevel() {
									return 0;
								}

								@Override
								public boolean isCombatRestricted() {
									return false;
								}

								@Override
								public String toString() {
									return "Home";
								}
							};
							Teleother.request(p, target, teleport);
						}));

		new Command(Privilege.JUNIOR_MODERATOR, "clearfriendlist",
				"Remove all entries from friend list.", (p, args) -> {
			p.getSocialManager().getFriends().clear();
			p.sendMessage("Relog to refresh your friends list.");
		});

		new Command(Privilege.PLAYER, "ccban",
				"Ban given user from your clan chat. Usage: ::ccban player name",
				(p, args) -> ClanManager.permban(p, StringUtilities.compile(args, 0, args.length, ' ')));
		new Command(Privilege.PLAYER, "ccunban",
				"Unban given user from your clan chat. Usage: ::ccunban player name",
				(p, args) -> ClanManager.permunban(p, StringUtilities.compile(args, 0, args.length, ' ')));

		new Command(Privilege.MODERATOR, "observe", (p, args) -> {
			if(Constants.WORLD_PROFILE.isBeta() && !Constants.isOwner(p)) {
				return;
			}
			if(args.length == 0 || p.getTemporaryAttributes().get("observee") != null) {
				val observee = p.getTemporaryAttributes().remove("observee");
				if(observee instanceof Player) {
					((Player) observee).getBotObservers().remove(p);
					p.sendMessage("No longer observing " + ((Player) observee).getName());
				}
				return;
			}
			World.getPlayer(StringUtilities.compile(args, 0, args.length, ' ')).ifPresent(user -> {
				p.getTemporaryAttributes().put("observee", user);
				user.getBotObservers().add(p);
			});
		});

		new Command(Privilege.ADMINISTRATOR, "fixtournament", (p, args) -> {

			World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '))
					.ifPresent(user -> user.getAttributes().remove("was inside tournament lobby"));

		});

		new Command(Privilege.ADMINISTRATOR, "npcinfo", (p, args) -> {

			p.sendMessage("Currently " + World.getNPCs().size() + " in the game.");

		});

		new Command(Privilege.JUNIOR_MODERATOR, "checkinv",
				"Check inv of given player. Usage: ::checkinv player name", (p, args) -> {
			val target = StringUtilities.compile(args, 0, args.length, ' ');
			val targetPlayer = World.getPlayer(target);
			if(!targetPlayer.isPresent()) {
				p.sendMessage(target + " is not online.");
				return;
			}
			val tp = targetPlayer.get();
			if(tp == p) {
				p.sendMessage("You can't do this on yourself.");
				return;
			}
			p.getTemporaryAttributes().put("viewing another bank", true);
			p.setCloseInterfacesEvent(() -> p.getTemporaryAttributes().remove("viewing another bank"));
			p.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 12);
			p.getPacketDispatcher()
					.sendUpdateItemContainer(tp.getInventory().getContainer(), ContainerType.BANK);
			p.getPacketDispatcher().sendComponentSettings(12, 13, 0, 1000, CLICK_OP10);
		});

		new Command(Privilege.MODERATOR, "checkbank",
				"Check bank of given player. Usage: ::checkbank player name", (p, args) -> {
			val target = StringUtilities.compile(args, 0, args.length, ' ');
			val targetPlayer = World.getPlayer(target);
			if(!targetPlayer.isPresent()) {
				if(p.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
					Player targPlayer = LoginManager.getPlayerSave(target);
					if(targPlayer == null) {
						p.sendMessage("Target player does not exist.");
						return;
					}
					targPlayer.setBank(new Bank(p, targPlayer.getBank()));
					val dispatcher = p.getPacketDispatcher();
					p.getTemporaryAttributes().put("viewing another bank", true);
					p.setCloseInterfacesEvent(
							() -> p.getTemporaryAttributes().remove("viewing another bank"));
					p.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 12);
					p.getPacketDispatcher()
							.sendUpdateItemContainer(targPlayer.getBank().getContainer(), ContainerType.BANK);
					targPlayer.getBank().refreshBankSizes(p);
					p.getPacketDispatcher().sendComponentSettings(12, 13, 0, 1000, CLICK_OP10);
					return;
				} else {
					p.sendMessage(target + " is not online.");
					return;
				}

			}
			val tp = targetPlayer.get();
			if(tp == p) {
				p.sendMessage("You can't do this on yourself.");
				return;
			}
			p.getTemporaryAttributes().put("viewing another bank", true);
			p.setCloseInterfacesEvent(() -> p.getTemporaryAttributes().remove("viewing another bank"));
			p.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 12);
			p.getPacketDispatcher()
					.sendUpdateItemContainer(tp.getBank().getContainer(), ContainerType.BANK);
			tp.getBank().refreshBankSizes(p);
			p.getPacketDispatcher().sendComponentSettings(12, 13, 0, 1000, CLICK_OP10);
		});

		new Command(Privilege.MODERATOR, "checkgear",
				"Check current worn equipment of a player. Usage: ::checkgear player name", (p, args) -> {
			val target = StringUtilities.compile(args, 0, args.length, ' ');
			val targetPlayer = World.getPlayer(target);
			if(!targetPlayer.isPresent()) {
				p.sendMessage(target + " is not online.");
				return;
			}
			val tp = targetPlayer.get();
			if(tp == p) {
				p.sendMessage("You can't do this on yourself.");
				return;
			}
			p.getTemporaryAttributes().put("viewing another bank", true);
			p.setCloseInterfacesEvent(() -> p.getTemporaryAttributes().remove("viewing another bank"));
			p.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, 12);
			p.getPacketDispatcher()
					.sendUpdateItemContainer(tp.getEquipment().getContainer(), ContainerType.BANK);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "togglebeta", (p, args) -> {
			if(!Constants.isOwner(p)) {
				return;
			}
			Constants.OPEN_BETA = !Constants.OPEN_BETA;
			p.sendMessage("Open beta is now: " + (Constants.OPEN_BETA ? " enabled." : " disabled."));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "antiknox", (p, args) -> {
			Constants.ANTIKNOX = !Constants.ANTIKNOX;
			p.sendMessage("Antiknox: " + Constants.ANTIKNOX);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "purgechunks", (p, args) -> {
			Constants.PURGING_CHUNKS = !Constants.PURGING_CHUNKS;
			p.sendMessage("Purging chunks: " + Constants.PURGING_CHUNKS);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "huntercheck", (p, args) -> {
			Constants.CHECK_HUNTER_TRAPS_QUANTITY = !Constants.CHECK_HUNTER_TRAPS_QUANTITY;
			p.sendMessage("Checking hunter trap quantity: " + Constants.CHECK_HUNTER_TRAPS_QUANTITY);
		});

		new Command(Privilege.ADMINISTRATOR, "duelarena", "Toggle duel arena access.", (p, args) -> {
			Constants.DUEL_ARENA = !Constants.DUEL_ARENA;
			p.sendMessage("Duel Arena: " + Constants.DUEL_ARENA);
		});

		new Command(Privilege.ADMINISTRATOR, "grots", "Toggle grotesque guardians.", (p, args) -> {
			Constants.GROTESQUE_GUARDIANS = !Constants.GROTESQUE_GUARDIANS;
			p.sendMessage("Grotesque Guardians: " + Constants.GROTESQUE_GUARDIANS);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "whitelisting", (p, args) -> {
			Constants.WHITELISTING = !Constants.WHITELISTING;
			p.sendMessage("Whitelisting: " + Constants.WHITELISTING);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "whitelist", (p, args) -> {
			Constants.whitelistedUsernames.add(
					Utils.formatUsername(StringUtilities.compile(args, 0, args.length, ' ')));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "teleparty", (p, args) -> {
			if(!Constants.isOwner(p)) {
				return;
			}
			val area = p.getArea();
			if(!(area instanceof RaidArea)) {
				return;
			}
			val raidArea = (RaidArea) area;
			val raid = raidArea.getRaid();
			val members = raid.getPlayers();
			for(val member : members) {
				member.setLocation(p.getLocation());
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "tourneyall", (p, args) -> {
			if(Tournament.tournaments.isEmpty() || !Constants.isOwner(p)) {
				return;
			}
			val tournaments = new ArrayList<>(Tournament.tournaments);
			for(val tournament : tournaments) {
				if(!tournament.expired()) {
					for(val player : World.getPlayers()) {
						tournament.getLobby().teleportPlayer(player);
					}
					break;
				}
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "starttournament", (p, args) -> {
			val presets = new ArrayList<>(TournamentPreset.values);
			val presetNameList = new ArrayList<String>(presets.size());
			for(val preset : presets) {
				presetNameList.add(preset.toString());
			}
			p.getDialogueManager()
					.start(new OptionsMenuD(p, "Select a preset", presetNameList.toArray(new String[0])) {
						@Override
						public void handleClick(final int slotId) {
							val preset = presets.get(slotId);
							p.getDialogueManager().finish();
							p.sendInputString("When to start the tournament?(MM DD HH MM)", value -> {
								val format = new SimpleDateFormat("yyyy MM dd HH mm");
								val date = format.parse((Calendar.getInstance().get(Calendar.YEAR) + " ") + value,
										new ParsePosition(0));
								val milliseconds = date.toInstant().toEpochMilli();
								val currentTime = System.currentTimeMillis();
								if(currentTime > milliseconds) {
									p.sendMessage(
											"Cannot schedule a tournament for that date; It has already passed!");
									return;
								}
								val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds - currentTime);
								p.getDialogueManager().finish();
								try {
									val area = MapBuilder.findEmptyChunk(64, 64);
									val lobby = new TournamentLobby(area, preset);
									lobby.constructRegion();
									lobby.createTournament((int) seconds, date);
									Tournament.tournaments.add(lobby.getTournament());
									World.sendMessage(GLOBAL_BROADCAST, "A " + preset
											+ " tournament has been started! Go talk to the Tournament Guard in Edgeville to participate.");
								} catch(OutOfSpaceException e) {
									e.printStackTrace();
								}
								//jvm fails to compile it thru the below dialogue
                        /*p.getDialogueManager().start(new Dialogue(p) {
                            @Override
                            public void buildDialogue() {
                                options("Start a tournament with the preset " + preset.toString() + " on " + date.toString() + "?",
                                        new DialogueOption("Start it.", () -> {
                                            try {
                                                val area = MapBuilder.findEmptyChunk(64, 64);
                                                val lobby = new TournamentLobby(area, preset);
                                                lobby.constructRegion();
                                                lobby.createTournament((int) seconds);
                                                Tournament.tournaments.add(lobby.getTournament());
                                            } catch (OutOfSpaceException e) {
                                                e.printStackTrace();
                                            }
                                        }), new DialogueOption("Do not start it."));
                            }
                        });*/
							});
						}

						@Override
						public boolean cancelOption() {
							return true;
						}
					});
		});

		new Command(Privilege.MODERATOR, "ip", "Get the IP address of a user.",
				(p, args) -> p.sendInputString("Whose" +
						" IP address to obtain?", value -> {
					val player = World.getPlayer(value);
					if(!player.isPresent()) {
						p.sendMessage("Player is not online.");
						return;
					}
					val target = player.get();
					p.sendMessage("IP address for " + target.getName() + " is: " + target.getIP());
				}));

		new Command(Privilege.MODERATOR, "mac", "Get the MAC address of a user.",
				(p, args) -> p.sendInputString(
						"Whose MAC address to obtain?", value -> {
							val player = World.getPlayer(value);
							if(!player.isPresent()) {
								p.sendMessage("Player is not online.");
								return;
							}
							val target = player.get();
							p.sendMessage(
									"MAC address for " + target.getName() + " is: " + target.getSession().getRequest()
											.getMacAddress());
						}));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "superiorrate", (p, args) -> {
			p.getTemporaryAttributes().put("superior rate", Math.max(0, Integer.parseInt(args[0]) - 1));
			p.sendMessage("Superiors will now appear at a rate of 1/" + (
					p.getNumericTemporaryAttribute("superior rate").intValue() + 1) + ".");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "js5duplicates", (p, args) -> {
			Constants.FILTERING_DUPLICATE_JS5_REQUESTS = !Constants.FILTERING_DUPLICATE_JS5_REQUESTS;
			p.sendMessage("JS5 duplicates filtering: " + Constants.FILTERING_DUPLICATE_JS5_REQUESTS);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "backups", (p, args) -> {
			BackupManager.PAUSE_BACKUPS = !BackupManager.PAUSE_BACKUPS;
			p.sendMessage("Backups paused: " + BackupManager.PAUSE_BACKUPS);
		});

		new Command(Privilege.ADMINISTRATOR, "broadcast",
				"Initiates sending a server-wide broadcast. No arguments required.", (p, args) -> {
			p.sendInputString("Enter text to broadcast: ", string -> {
				p.getDialogueManager().start(new Dialogue(p) {
					@Override
					public void buildDialogue() {
						plain("Broadcast message: <br>" + string);
						options("Broadcast it?",
								new DialogueOption("Yes.", () -> World.sendMessage(GLOBAL_BROADCAST, string)),
								new DialogueOption("No."));
					}
				});
			});
		});

		new Command(Privilege.ADMINISTRATOR, "clanunban", "Unban a user from your clan.", (p, args) -> {
			val name = StringUtilities.compile(args, 0, args.length, '_');
			val clan = ClanManager.getChannel(p.getUsername());
			if(clan.isPresent()) {
				val bannedMembers = clan.get().getBannedMembers();
				if(bannedMembers.removeLong(Utils.formatUsername(name)) != 0) {
					p.sendMessage("User successfully unbanned from clan.");
				} else {
					p.sendMessage("Could not find user " + name + ".");
				}
			} else {
				p.sendMessage("You do not own a clan.");
			}
		});

		new Command(Privilege.ADMINISTRATOR, "status", "Get status of a user.", (player, strings) -> {
			val targetName = StringUtilities.compile(strings, 0, strings.length, ' ');
			val targetPlayer = World.getPlayer(targetName);
			if(!targetPlayer.isPresent()) {
				player.sendMessage("The user " + targetName + " is not online.");
				return;
			}
			val t = targetPlayer.get();
			player.sendMessage(Colour.RS_RED.wrap("Status on " + t.getName() + ":"));
			player.sendMessage("Logout timer: " + t.getLogoutCount());
			player.sendMessage("Channel active: " + t.getSession().getChannel().isActive());
			player.sendMessage("Channel open: " + t.getSession().getChannel().isOpen());
			player.sendMessage("Last packet received: " + TimeUnit.MILLISECONDS.toSeconds(
					System.currentTimeMillis() - t.getLastReceivedPacket()) + " seconds ago");
			player.sendMessage("-------------------");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetge",
				(p, args) -> p.getGrandExchange().resetExistingOffers());

		new Command(Privilege.SPAWN_ADMINISTRATOR, "multigfx", (p, args) -> {

			var id = Integer.parseInt(args[0]);
			val px = p.getX();
			val py = p.getY();
			for(int x = px - 10; x < px + 10; x++) {
				for(int y = py - 10; y < py + 10; y++) {
					val proj = new Projectile(id++, 50, 50, 0, 0, 5000, 0, 5);
					World.sendProjectile(new Location(x, y, p.getPlane()),
							new Location(x + 5, y, p.getPlane()), proj);
				}
			}
			p.sendMessage("Last: " + (id - 1));

		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "barrows", (p, args) -> {
			if(args.length < 2) {
				p.sendMessage("Arguments are <Number of kills> <Reward potential>");
				return;
			}
			val barrows = p.getBarrows();
			val number = Math.min(100, Integer.parseInt(args[0]));
			val rp = Math.max(0, Integer.parseInt(args[1]) - 668);
			for(int i = 0; i < number; i++) {
				barrows.setMaximumReward(rp);
				GameInterface.BARROWS_REWARDS.open(p);
			}

			p.sendMessage(
					"Rolled " + number + " Barrows rewards at a reward potential of " + (rp + 668) + ".");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "defencemultiplier", (p, args) -> {
			val dbl = Double.parseDouble(args[0]);
			Constants.defenceMultiplier = Math.max(0.5, Math.min(2, dbl));
			for(val player : World.getPlayers()) {
				player.sendMessage("PvP Defence multiplier has been set to " + Constants.defenceMultiplier,
						GLOBAL_BROADCAST);
			}

		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "birthdayeventreload", (p, args) -> {
			BirthdayEventRewardList.reload();
			p.sendMessage("Birthday event reward list reloaded.");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "addbirthdayreward", (p, args) -> {
			val username = StringUtilities.compile(args, 0, args.length, '_');
			BirthdayEventRewardList.addUsername(username);
			p.sendMessage(username + " added to birthday event reward list.");
		});

		new Command(Privilege.ADMINISTRATOR, "partyroom", "Opens the party room modification menu.",
				(p, args) -> {
					PartyRoomVariables.openEditMode(p);
				});

		new Command(Privilege.MODERATOR, "pc", "Opens the pest control modification menu.",
				(p, args) -> {
					p.getDialogueManager().start(new OptionsMenuD(p, "Select the setting to change",
							"Minimum players requirement: " + Colour.RS_GREEN.wrap(
									PestControlUtilities.MINIMUM_PLAYERS_LIMIT + ""),
							"Maximum players requirement: " + Colour.RS_GREEN.wrap(
									PestControlUtilities.MAXIMUM_PLAYERS_LIMIT + ""),
							"Time until deporting: " + Colour.RS_GREEN.wrap(
									PestControlUtilities.TIME_UNTIL_GAME_START + ""),
							"Veteran points per game: " + Colour.RS_GREEN.wrap(
									PestControlGameType.VETERAN.getPointsPerGame() + ""),
							"Intermediate points per game: " + Colour.RS_GREEN.wrap(
									PestControlGameType.INTERMEDIATE.getPointsPerGame() + ""),
							"Novice points per game: " + Colour.RS_GREEN.wrap(
									PestControlGameType.NOVICE.getPointsPerGame() + "")) {
						@Override
						public void handleClick(int slotId) {
							if(slotId == 0) {
								player.sendInputInt("Enter minimum players requirement",
										PestControlUtilities::setMinimum);
							} else if(slotId == 1) {
								player.sendInputInt("Enter maximum players requirement",
										PestControlUtilities::setMaximum);
							} else if(slotId == 2) {
								player.sendInputInt("Enter delay between deportation in ticks",
										PestControlUtilities::setTime);
							} else if(slotId == 3) {
								player.sendInputInt("Enter veteran points per game (default 10)",
										PestControlGameType.VETERAN::setPointsPerGame);
							} else if(slotId == 4) {
								player.sendInputInt("Enter intermediate points per game (default 8)",
										PestControlGameType.INTERMEDIATE::setPointsPerGame);
							} else if(slotId == 5) {
								player.sendInputInt("Enter novice points per game (default 6)",
										PestControlGameType.NOVICE::setPointsPerGame);
							}
						}

						@Override
						public boolean cancelOption() {
							return true;
						}
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "smoke", (p, args) -> {

			val radius = args.length == 0 ? p.getViewDistance() : Integer.parseInt(args[0]);
			val list = new ArrayList<NPC>();

			val unrestricted = args.length == 2;

			CharacterLoop.forEach(p.getLocation(), Math.min(p.getViewDistance(), radius), NPC.class,
					n -> {
						if(n.isAttackable() && n.isAttackable(p) && (unrestricted || !p.isProjectileClipped(n,
								false))) {
							list.add(n);
						}
					});

			val projectile = new Projectile(310, 34, 50, 0, 0, 20, 0, 5);

			for(val npc : list) {
				World.scheduleProjectile(p, npc, projectile).schedule(() -> npc.applyHit(new Hit(p,
						npc.getHitpoints(), HitType.REGULAR)));
			}

		});
		new Command(Privilege.MODERATOR, "yellmute", "Yell mute a player",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.YELL_MUTE));
		new Command(Privilege.MODERATOR, "ipyellmute", "Ip yell mute a player",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.IP_YELL_MUTE));
		new Command(Privilege.MODERATOR, "macyellmute", "Mac yell mute a player",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.MAC_YELL_MUTE));

		new Command(Privilege.MODERATOR, "mute", "Mute a player",
				(p, args) -> PunishmentManager.requestPunishment(p,
						PunishmentType.MUTE));

		new Command(Privilege.MODERATOR, "ban", "Ban a player", (p, args) -> {
			PunishmentManager.requestPunishment(p, PunishmentType.BAN);
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "banhammer", "Ban a player in style", (p, args) -> {
			PunishmentManager.requestPunishment(p, PunishmentType.BANHAMMER);
		});
		new Command(Privilege.MODERATOR, "ipmute", "Ip mute a player",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.IP_MUTE));

		new Command(Privilege.MODERATOR, "ipban", "Ip ban a player",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.IP_BAN));

		new Command(Privilege.MODERATOR, "macmute", "Mac mute a player",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.MAC_MUTE));

		new Command(Privilege.MODERATOR, "macban", "Mac ban a player.",
				(p, args) -> PunishmentManager.requestPunishment(p, PunishmentType.MAC_BAN));

		new Command(Privilege.MODERATOR, "revoke", "Open the punishment revocation menu.",
				(p, args) -> PunishmentManager.revokePunishments(p));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetfarming", (p, args) -> {
			p.getFarming().reset();
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "resettog",
				(p, args) -> p.sendInputName("Whose Tears of Guthix restriction to remove?",
						name -> World.getPlayer(name).ifPresent(targetPlayer -> targetPlayer.getAttributes()
								.remove(JunaEnterDialogue.LAST_ATTEMPT_DATE_ATTR))));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "cycle", (p, args) -> {
			Constants.CYCLE_DEBUG = !Constants.CYCLE_DEBUG;
			p.sendMessage("Cycle debug: " + Constants.CYCLE_DEBUG);
		});

//        new Command(Privilege.SPAWN_ADMINISTRATOR, "savepreset", (p, args) -> {
//            if (args.length == 0) {
//                p.sendMessage("Must enter name as the argument.");
//                return;
//            }
//            val name = Utils.formatString(StringUtilities.compile(args, 0, args.length, ' '));
//            if (name.length() == 0) {
//                return;
//            }
//            p.getPresetManager().savePreset(name, false);
//        });
//
//        new Command(Privilege.SPAWN_ADMINISTRATOR, "presets", (p, args) -> p.getPresetManager().open());

		new Command(Privilege.MODERATOR, "heatmaps",
				"Toggles heatmap mode. Usage: ::heatmaps [0 = off, 1 = on]", (p, args) -> {
			val value = Integer.valueOf(args[0]);
			val distance = 16383;//Integer.valueOf(args[1]);
			val bool = value == 1;
			p.setHeatmap(bool);
			p.setHeatmapRenderDistance(distance);
			p.send(new Heatmap(bool));
		});

		new Command(Privilege.ADMINISTRATOR, "campos", (p, args) -> {
			int x = p.getX();
			int y = p.getY();
			int plane = 1000;
			int speed = 127;
			int acceleration = 127;
			if(args.length > 0) {
				x = Integer.parseInt(args[0]);
			}
			if(args.length > 1) {
				y = Integer.parseInt(args[1]);
			}
			if(args.length > 2) {
				plane = Integer.parseInt(args[2]);
			}
			if(args.length > 3) {
				speed = Integer.parseInt(args[3]);
			}
			if(args.length > 4) {
				acceleration = Integer.parseInt(args[4]);
			}
			new CameraPositionAction(p, new Location(x, y), plane, speed, acceleration).run();
		});

		new Command(Privilege.ADMINISTRATOR, "camlook", (p, args) -> {
			int x = p.getX();
			int y = p.getY();
			int plane = 1000;
			int speed = 127;
			int acceleration = 127;
			if(args.length > 0) {
				x = Integer.parseInt(args[0]);
			}
			if(args.length > 1) {
				y = Integer.parseInt(args[1]);
			}
			if(args.length > 2) {
				plane = Integer.parseInt(args[2]);
			}
			if(args.length > 3) {
				speed = Integer.parseInt(args[3]);
			}
			if(args.length > 4) {
				acceleration = Integer.parseInt(args[4]);
			}
			new CameraLookAction(p, new Location(x, y), plane, speed, acceleration).run();
		});

		new Command(Privilege.ADMINISTRATOR, "camreset", (p, args) -> {
			p.getPacketDispatcher().resetCamera();
		});

		new Command(Privilege.ADMINISTRATOR, "testproj", (p, args) -> {
			val proj = new Projectile(Integer.parseInt(args[0]), 50, 50, 0, 0, 50, 0, 5);
			World.sendProjectile(p.getLocation(), new Location(p.getX() + 10, p.getY(), p.getPlane()),
					proj);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "task", "Choose a slayer task.", (p, args) -> {
			List<SlayerTask> tasks = new ArrayList<>();

			tasks.addAll(Arrays.asList(
					ObjectArrays.concat(RegularTask.VALUES, BossTask.VALUES, SlayerTask.class)));
			tasks.sort(Comparator.comparing(Object::toString));
			val names = new ArrayList<String>();
			for(val task : tasks) {
				names.add(task.getTaskName());
			}

			p.getDialogueManager().start(new OptionsMenuD(p, "Select the task to receive",
					names.toArray(new String[0])) {
				@Override
				public void handleClick(final int slotId) {
					if(slotId >= tasks.size()) {
						return;
					}
					val task = tasks.get(slotId);
					//noinspection Convert2Lambda
					player.sendInputInt("Enter kill count requirement:", new CountDialogue() {
						@Override
						public void run(int amount) {
							val assignment = new Assignment(player, player.getSlayer(), task, task.getEnumName(),
									amount, amount, player.getSlayer().getMaster());
							p.getSlayer().setAssignment(assignment);
							p.getDialogueManager().start(new Dialogue(p, p.getSlayer().getMaster().getNpcId()) {
								@Override
								public void buildDialogue() {
									npc("Your new task is to kill " + assignment.getAmount() + " "
											+ assignment.getTask().toString() + ".");
								}
							});
						}
					});

				}

				@Override
				public boolean cancelOption() {
					return true;
				}
			});

		});

		new Command(Privilege.ADMINISTRATOR, "combatdebug",
				(p, args) -> p.getTemporaryAttributes().put("combat debug", Boolean.valueOf(args[0])));

		new Command(Privilege.PLAYER, "home", "Teleport home. Works anywhere.", (p, args) -> {
			if(p.isLocked()) {
				return;
			}
			if(!p.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
				if(!p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
					return;
				}
			}
			val teleport = new Teleport() {
				@Override
				public TeleportType getType() {
					return TeleportType.ZENYTE_PORTAL_TELEPORT;
				}

				@Override
				public Location getDestination() {
					return new Location(3087, 3489, 0);
				}

				@Override
				public int getLevel() {
					return 0;
				}

				@Override
				public double getExperience() {
					return 0;
				}

				@Override
				public int getRandomizationDistance() {
					return 3;
				}

				@Override
				public Item[] getRunes() {
					return null;
				}

				@Override
				public int getWildernessLevel() {
					return p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR) ? 100 : 20;
				}

				@Override
				public boolean isCombatRestricted() {
					return false;
				}
			};
			teleport.teleport(p);
		});

		new Command(Privilege.ADMINISTRATOR, "perks", (p, args) -> {
			val builder = new StringBuilder();
			builder.append("Unlocked perks:<br>");
			for(val entry : p.getPerkManager().getPerks().entrySet()) {
				builder.append("- <col=00080>");
				builder.append(entry.getValue().getName());
				builder.append("</col><br>");
			}
			p.sendMessage(builder.toString());
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "spawning", (p, args) -> {
			try {
				val area = MapBuilder.findEmptyChunk(8, 8);
				val dynamicArea = new DynamicArea(area, 0, 0) {

					@Override
					public void enter(Player player) {

					}

					@Override
					public void leave(Player player, boolean logout) {

					}

					@Override
					public String name() {
						return "Spawning area";
					}

					@Override
					public Location onLoginLocation() {
						return new Location(3222, 3219, 0);
					}

					@Override
					public void constructed() {
						p.setLocation(
								new Location((area.getChunkX() + 4) << 3, (area.getChunkY() + 4) << 3, 0));
					}

					@Override
					public void constructRegion() {
						if(constructed) {
							return;
						}
						GlobalAreaManager.add(this);
						try {
							for(int x = 0; x < 8; x++) {
								for(int y = 0; y < 8; y++) {
									MapBuilder.copySquare(area, 1, 396, 441, 0, x + area.getChunkX(),
											y + area.getChunkY(), 0, 0);
								}
							}
						} catch(OutOfBoundaryException e) {
							log.error(Strings.EMPTY, e);
						}
						constructed = true;
						constructed();
					}
				};
				dynamicArea.constructRegion();
			} catch(OutOfSpaceException e) {
				log.error(Strings.EMPTY, e);
			}

		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "toggleoptions", (p, args) -> {
			p.setUpdatingNPCOptions(!p.isUpdatingNPCOptions());
			p.setUpdateNPCOptions(true);
		});

		new Command(Privilege.ADMINISTRATOR, "scene", (p, args) -> {
			p.setViewDistance(Integer.parseInt(args[0]));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, new String[] {"unlimitedrunes", "unlrunes", "runes"},
				"Grants you unlimited runes.", (p, args) -> {
			p.getVarManager().sendBit(4145, 1);
			p.sendMessage(Colour.RS_GREEN.wrap(
					"Fountain of Rune effect activated - no runes are required, "
							+ "and you get no base experience for casting spells."));
		});

		new Command(Privilege.ADMINISTRATOR, "open", (p, args) -> {
			val name = StringUtilities.compile(args, 0, args.length, ' ');
			for(val inter : GameInterface.VALUES) {
				if(inter.toString().replaceAll("_", " ").toLowerCase().startsWith(name)) {
					inter.open(p);
					return;
				}
			}
		});

		new Command(Privilege.PLAYER, new String[] {"drops", "drop", "dropviewer"},
				"Opens the drop viewer.", (p, args) -> {
			GameInterface.DROP_VIEWER.open(p);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "wave", (p, args) -> {
			if(!p.inArea("Fight caves")) {
				p.sendMessage("You must be in fight caves to do this.");
				return;
			}
			val caves = (FightCaves) p.getArea();
			caves.skip(Integer.parseInt(args[0]));
		});

		new Command(Privilege.ADMINISTRATOR, "scrapdrops", (p, args) -> {

			/**String[] names = new String[]{
			 "Wolf bone", "Bat wing", "Rat bone", "Baby dragon bone", "Ogre ribs", "Jogre bone",
			 "Zogre bone", "Mogre bone", "Dagannoth ribs", "Snake spine", "Zombie bone",
			 "Werewolf bone", "Moss giant bone", "Fire giant bone", "Ice giant ribs",
			 "Terrorbird wing", "Ghoul bone", "Troll bone", "Seagull wing", "Undead cow ribs",
			 "Experiment bone", "Rabbit bone", "Basilisk bone", "Desert lizard bone",
			 "Cave goblin skull", "Vulture wing", "Jackal bone"
			 };
			 val map = new Int2ObjectOpenHashMap<Drop[]>();
			 NPCDrops.drops.forEach((k, v) -> {
			 val list = new ArrayList<Drop>();
			 for (val drop : v) {
			 if (drop.getItemIds() == 617) {
			 drop.setItemId((short) 995);
			 }
			 val name = ItemDefinitions.get(drop.getItemIds()).getAreaName();

			 if (name.startsWith("Clue scroll") || name.endsWith("champion scroll") || name.startsWith("Ensouled")
			 || name.equals("Looting bag") || name.equals("Slayer's enchantment"))
			 if (name.equals("Goblin skull") || name.equals("Big frog leg") || name.equals("Bear ribs")
			 || name.equals("Ram skull") || name.equals("Unicorn bone") || name.equals("Monkey paw")
			 || name.equals("Giant rat bone") || name.equals("Giant bat wing") || name.equals("Mysterious emblem"))
			 continue;

			 if (ArrayUtils.contains(names, name)) {
			 continue;
			 }

			 list.enqueue(drop);
			 }
			 if (!list.isEmpty())
			 map.put(k, list.toArray(new Drop[list.size()]));
			 });
			 NPCDrops.drops = map;

			 NPCDrops.save();
			 */

		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "addclitem", (p, args) -> {
			p.sendInputInt("What item would you like to add?",
					itemId -> {
						val item = new Item(itemId);
						p.sendInputInt("What amount of " + item.getName() + " would you like to set to add?",
								value -> p.sendInputName(
										"Who would you like to give " + value + " x " + item.getName() + " to?",
										name -> {
											val target = World.getPlayer(name);
											if(!target.isPresent()) {
												p.sendMessage(name + " is not logged in.");
												return;
											}
											val t = target.get();
											t.getCollectionLog().add(new Item(item.getId(), value));
											p.sendMessage(value + " x " + item.getName() + " added to " + name
													+ "'s collection log.");
											t.sendMessage(
													"Your collection log has received " + value + " x " + item.getName()
															+ "!");
										}));
					});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "removeclitem", (p, args) -> {
			p.sendInputInt("What item would you like to remove?",
					itemId -> {
						val item = new Item(itemId);
						p.sendInputInt("What amount of " + item.getName() + " would you like to set to remove?",
								value -> p.sendInputName(
										"Who would you like to take " + value + " x " + item.getName() + " from?",
										name -> {
											val target = World.getPlayer(name);
											if(!target.isPresent()) {
												p.sendMessage(name + " is not logged in.");
												return;
											}
											val t = target.get();
											val result = t.getCollectionLog().getContainer()
													.remove(new Item(item.getId(), value));
											p.sendMessage(
													result.getSucceededAmount() + " x " + item.getName() + " taken from "
															+ name + "'s collection log.");
											if(result.getSucceededAmount() > 0) {
												t.sendMessage(
														"Your collection log has had " + result.getSucceededAmount() + " x "
																+ item.getName() + " removed!");
											}
										}));
					});
		});

		new Command(Privilege.ADMINISTRATOR, "chunkhash", (p, args) -> {
			val x = p.getX();
			val y = p.getY();
			val hash = x >> 3 << 16 | y >> 3;
			p.sendMessage("Chunk hash: " + hash);
			System.err.println(hash);
		});

		new Command(Privilege.ADMINISTRATOR, "value", (p, args) -> {
			val id = Integer.valueOf(args[0]);
			val definitions = ItemDefinitions.get(id);
			p.sendMessage("Value of " + definitions.getName() + " is " + definitions.getPrice());
		});

		new Command(Privilege.ADMINISTRATOR, "play", (p, args) -> {
			p.getPacketDispatcher().sendMusic(Integer.parseInt(args[0]));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, new String[] {"update", "shutdown"}, (p, args) -> {
			if(!Constants.isOwner(p)) {
				p.sendMessage("You are not authorized to use this command!");
				return;
			}
			p.sendInputInt("How many ticks until server shutdown?",
					value -> p.getDialogueManager().start(new Dialogue(p) {
						@Override
						public void buildDialogue() {
							options("Shut the server down in " + TimeUnit.TICKS.toSeconds(value) + " seconds?",
									new DialogueOption("Shut it down.", () -> World.setShutdown(value)),
									new DialogueOption("Keep it running."));
						}
					}));
		});

		new Command(Privilege.ADMINISTRATOR, "packassets", (p, args) -> {
			if(!Constants.isOwner(p)) {
				return;
			}
			try {
				GhettoPacker.packLocal();
				p.sendMessage("Packing finished.");
			} catch(final IOException e) {
				log.error(Strings.EMPTY, e);
			}

		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "xp",
				"Sets your experience modifier to suggested value(s)", (p, args) -> {
			try {
				val combat = Integer.parseInt(args[0]);
				val skilling = Integer.parseInt(args[1]);

				if(combat < 1 || skilling < 1) {
					p.sendMessage("Minimum experience rate value permitted is 1!");
					return;
				}
				if(combat > 1000 || skilling > 1000) {
					p.sendMessage("Maximum experience rate value permitted is 1000!");
					return;
				}

				p.setExperienceMultiplier(combat, skilling);
				p.sendMessage("Experience rate set to x" + combat + " & x" + skilling + ".");
				GameInterface.GAME_NOTICEBOARD.open(p);
			} catch(final Exception e) {
				e.printStackTrace();
				p.sendMessage("Format is ::xp combat_rate_value skilling_rate_value");
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetbooster", "Resets booster perks for a player",
				(p, args) -> {
					val target = StringUtilities.compile(args, 0, args.length, ' ');
					World.getPlayer(target).ifPresent((targetPlayer) -> {
						targetPlayer.addAttribute(WiseOldManD.BOOSTER_END, 0L);
						if(targetPlayer.getPrivilege().eligibleTo(Privilege.BOOSTER)
								&& !targetPlayer.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)) {
							targetPlayer.setPrivilege(Privilege.PLAYER);
						}
						p.sendMessage("You have reset the booster perks for " + targetPlayer.getUsername());
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "givebooster",
				"Gives 7 days of booster perks to a player", (p, args) -> {
			val target = StringUtilities.compile(args, 0, args.length, ' ');
			World.getPlayer(target).ifPresent((targetPlayer) -> {
				targetPlayer.addAttribute(WiseOldManD.BOOSTER_END, System.currentTimeMillis() + 604800000L);
				if(!targetPlayer.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)) {
					targetPlayer.setPrivilege(Privilege.BOOSTER);
				}
				targetPlayer.sendMessage("You have been given 7 days of booster perks.");
				targetPlayer.getInventory().addOrDrop(new Item(ItemId.BOOK_OF_BOOSTS));
				p.sendMessage("You have given " + targetPlayer.getUsername() + " 7 days of booster perks.");
			});
		});

		new Command(Privilege.ADMINISTRATOR, "attr", (p, args) -> {
			val attr = StringUtilities.compile(args, 0, args.length, ' ');
			p.sendMessage("Value for attr: " + attr + ", " + p.getAttributes().get(attr));
		});

		new Command(Privilege.ADMINISTRATOR, "setattr",
				(p, args) -> p.getAttributes().put(args[0], Integer.parseInt(args[1])));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetinfernoskip",
				(p, args) -> p.incrementNumericAttribute("inferno_wave_skip",
						-(int) p.getNumericAttribute("inferno_wave_skip")));

        /*new Command(Privilege.SPAWN_ADMINISTRATOR, "doublexp", (p, args) -> {
            Constants.BOOSTED_XP = !Constants.BOOSTED_XP;
            p.sendMessage("Boosted xp: " + Constants.BOOSTED_XP);
        });

        new Command(Privilege.SPAWN_ADMINISTRATOR, "setboostedxp", (p, args) -> {
            p.sendInputInt("Set the xp boost % (eg. 50 for 1.5x xp) to?", amount -> {
                Constants.BOOSTED_XP_MODIFIER = amount;
                val boost = (1F + Constants.BOOSTED_XP_MODIFIER / 100D);
                p.sendMessage("XP boost set to " + boost + "x (" + Constants.BOOSTED_XP_MODIFIER + "%).");
            });
        });*/

		new Command(Privilege.SPAWN_ADMINISTRATOR, "ironman", "Sets your ironman mode", (p, args) -> {
			final String rights = args[0];
			val name = StringUtilities.compile(args, 1, args.length, ' ');
			World.getPlayer(name).ifPresent(a -> {
				GameMode mode;
				if(rights.startsWith("reg") || rights.startsWith("ironman")) {
					mode = GameMode.STANDARD_IRON_MAN;
				} else if(rights.startsWith("ult")) {
					mode = GameMode.ULTIMATE_IRON_MAN;
				} else if(rights.startsWith("hard")) {
					mode = GameMode.HARDCORE_IRON_MAN;
				} else {
					mode = GameMode.REGULAR;
				}
				a.setGameMode(mode);
			});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "member", "Sets your member rank", (p, args) -> {
			val value = Integer.valueOf(args[0]);
			val rank = MemberRank.get(value);
			val name = StringUtilities.compile(args, 1, args.length, ' ');
			World.getPlayer(name).ifPresent(a -> {
				if(rank != null) {
					a.setMemberRank(rank);
				}
			});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "shufflepids", "Shuffles all of the players' PIDs",
				(p, args) -> {
					World.shufflePids();
					p.sendMessage("Your new PID is: " + p.getPid());
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "questpoints",
				"Sets your quest points to the defined value.", (p, args) -> {
			p.setQuestPoints(Math.max(0, Integer.parseInt(args[0])));
			p.refreshQuestPoints();
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "slayerpoints",
				"Sets your slayer points to the defined value.", (p, args) -> {
			p.getSlayer().setSlayerPoints(Integer.parseInt(args[0]), true);
		});

		new Command(Privilege.ADMINISTRATOR, "area", (p, args) -> {
			val area = p.getArea();
			if(area == null) {
				p.sendMessage("Currently not in any defined area.");
				return;
			}
			p.sendMessage("Current area: " + area.name());
		});

		new Command(Privilege.ADMINISTRATOR, "areas", (p, args) -> {
			World.getPlayer(StringUtilities.compile(args, 0, args.length, ' ')).ifPresent(target -> {
				val area = target.getArea();
				if(area == null) {
					p.sendMessage("Currently not in any defined areas.");
					return;
				}
				final List<Area> areas = new ArrayList<>();

				Area extension = area;

				while(extension.getSuperArea() != null && extension.getSuperArea()
						.inside(p.getLocation())) {
					extension = extension.getSuperArea();
				}
				areas.add(extension);
				Area a;
				while(!extension.getExtendAreas().isEmpty()) {
					a = extension;
					for(int i = extension.getExtendAreas().size() - 1; i >= 0; i--) {
						val nextPick = extension.getExtendAreas().get(i);
						if(nextPick.inside(p.getLocation())) {
							extension = nextPick;
							areas.add(extension);
							break;
						}
					}
					if(extension == a) {
						break;
					}
				}
				p.sendMessage("Current areas: " + areas);
			});
		});

		new Command(Privilege.ADMINISTRATOR, "nametag", (p, args) -> {
			final int index = Integer.parseInt(args[0]);
			final String tag = StringUtilities.compile(args, 1, args.length, ' ');
			p.setNametag(index, tag);
		});

		new Command(Privilege.JUNIOR_MODERATOR, "kick", "Disconnects a user.", (p, args) -> {
			val t = World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '));
			if(!t.isPresent()) {
				p.sendMessage("Player was not found.");
				return;
			}
			val target = t.get();
			target.log(LogLevel.INFO, "Forcefully kicked by " + p.getName() + ".");
			target.logout(true);
			p.sendMessage("Successfully kicked <col=C22731>" + target.getUsername() + "</col>!");
		});

		new Command(Privilege.ADMINISTRATOR, "unlock",
				"Unlocks a player who is currently locked. USE WITH CAUTION! NEVER unlock a player by request!",
				(p, args) -> {
					val target = World.getPlayerByUsername(String.valueOf(args[0]));
					if(target == null) {
						p.sendMessage("Player was not found.");
						return;
					}
					target.unlock();
					p.sendMessage("Target unlocked.");
				});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "dd", (p, args) -> {
			DoubleDropsManager.handleCommand(p, args);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "killme", (p, args) -> {
			p.applyHit(new Hit(p.getHitpoints(), HitType.REGULAR));
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "fakehammer", (p, args) -> {
			if(args.length == 0) {
				return;
			}
			val player = World.getPlayerByUsername(String.valueOf(args[0]));
			val punisher = p;
			if(player == null || player.isLoggedOut()) {
				return;
			}
			WorldTasksManager.schedule(new WorldTask() {
				int ticks = 0;
				Item originalWeapon = null;

				@Override
				public void run() {
					if(ticks == 0) {
						player.lock();
						punisher.lock();
						originalWeapon = punisher.getEquipment().getItem(EquipmentSlot.WEAPON);
					}
					if(ticks == 1) {
						player.setLocation(
								new Location(punisher.getLocation().getX(), punisher.getLocation().getY() + 1,
										punisher.getPlane()));
					}
					if(ticks == 2) {
						player.setFaceEntity(punisher);
						player.setAnimation(new Animation(2836));
						punisher.setFaceEntity(player);
					}
					if(ticks == 5) {
						punisher.getEquipment().set(EquipmentSlot.WEAPON, new Item(ItemId.DRAGON_WARHAMMER));
						punisher.setAnimation(new Animation(1378));
						punisher.setGraphics(new Graphics(1292));
					}
					if(ticks == 6) {
						player.applyHit(new Hit(punisher, 0, HitType.REGULAR));
						player.unlock();
					}
					if(ticks == 7) {
						player.setGraphics(new Graphics(287));
						punisher.getEquipment().set(EquipmentSlot.WEAPON, originalWeapon);
					}
					if(ticks == 8) {
						punisher.unlock();
						stop();
					}
					ticks++;
				}
			}, 0, 1);
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "poison", (p, args) -> {
			p.getToxins().applyToxin(ToxinType.POISON, Integer.parseInt(args[0]));
		});

		new Command(Privilege.ADMINISTRATOR, "printmasks", (p, args) -> {
			final int val = Integer.parseInt(args[0]);
			System.err.println("Masks for value " + val + ": " + AccessMask.getBuilder(val, false));
		});

		new Command(Privilege.ADMINISTRATOR, "duration", (p, args) -> {
			final int anim = Integer.parseInt(args[0]);
			final AnimationDefinitions defs = AnimationDefinitions.get(anim);
			p.sendMessage("Duration: " + defs.getDuration());
		});

		new Command(Privilege.ADMINISTRATOR, "objvar", (p, args) -> {
			int varbit = ObjectDefinitions.get(Integer.parseInt(args[0])).getVarbit();
			if(varbit == -1) {
				varbit = ObjectDefinitions.get(Integer.parseInt(args[0])).getVarp();
				if(varbit == -1) {
					p.sendMessage("No varps or varbits found for that object.");
					return;
				}
				p.sendMessage("Varp for " + args[0] + " is " + varbit);
				p.getVarManager().sendVar(varbit, 1);
			} else {
				p.sendMessage("Varbit for " + args[0] + " is " + varbit);
				p.getVarManager().sendBit(varbit, 1);
			}
		});

		new Command(Privilege.ADMINISTRATOR, "extract", (p, args) -> {
			new AnimationExtractor().extract();
		});

		new Command(Privilege.ADMINISTRATOR, "tolerance", (p, args) -> {
			final int value = Integer.parseInt(args[0]);
			p.setMaximumTolerance(value == 1);
			p.sendMessage("Maximum tolerance set to: " + value);
		});

		new Command(Privilege.PLAYER, "commands", (p, args) -> {
			val entries = new ArrayList<String>();

			COMMANDS.values().stream().filter(distinctByKey(c -> c.name)).sorted().forEach(c -> {
				if(!p.getPrivilege().eligibleTo(c.privilege)) {
					return;
				}
				if(c.description != null) {
					final String[] lines = Book.splitIntoLine(c.description, 55);
					entries.add(c.privilege.getCrown() + "<col=ffff00> ::" + c.name);
					entries.addAll(Arrays.asList(lines));
				}
			});

			Diary.sendJournal(p, "Commands list", entries);
		});

		new Command(Privilege.ADMINISTRATOR, "testvarp", (p, args) -> {
			for(int i = 0; i < Utils.getIndiceSize(Indice.VARBIT_DEFINITIONS); i++) {
				final VarbitDefinitions def = VarbitDefinitions.get(i);
				if(def.getBaseVar() == Integer.parseInt(args[0])) {
					System.out.println(
							"Varbit: " + i + ", from bitshift:" + def.getStartBit() + ", till bitshift: "
									+ def.getEndBit());
				}
			}
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "copy",
				"Copies the requested player's inventory and equipment.", (p, args) -> {
			final StringBuilder bldr = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				bldr.append(args[i] + ((i == args.length - 1) ? "" : " "));
			}
			final String name = Utils.formatString(bldr.toString());
			final Player player = World.getPlayerByDisplayname(name);
			if(player == null) {
				p.sendMessage("Could not find player.");
				return;
			}
			p.getInventory().setInventory(player.getInventory());
			p.getEquipment().setEquipment(player.getEquipment());
			p.getInventory().refreshAll();
			p.getEquipment().refreshAll();
			p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			p.sendMessage(
					"Inventory & Equipment copied from " + player.getPlayerInformation().getDisplayname()
							+ ".");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "copyinv",
				"Copies the requested player's inventory.", (p, args) -> {
			final StringBuilder bldr = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				bldr.append(args[i] + ((i == args.length - 1) ? "" : " "));
			}
			final String name = Utils.formatString(bldr.toString());
			final Player player = World.getPlayerByDisplayname(name);
			if(player == null) {
				p.sendMessage("Could not find player.");
				return;
			}
			p.getInventory().setInventory(player.getInventory());
			p.getInventory().refreshAll();
			p.sendMessage(
					"Inventory copied from " + player.getPlayerInformation().getDisplayname() + ".");
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "copyequipment",
				"Copies the requested player's equipment.", (p, args) -> {

			final StringBuilder bldr = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				bldr.append(args[i] + ((i == args.length - 1) ? "" : " "));
			}
			final String name = Utils.formatString(bldr.toString());
			final Player player = World.getPlayerByDisplayname(name);
			if(player == null) {
				p.sendMessage("Could not find player.");
				return;
			}
			p.getEquipment().setEquipment(player.getEquipment());
			p.getEquipment().refreshAll();
			p.sendMessage(
					"Equipment copied from " + player.getPlayerInformation().getDisplayname() + ".");
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "maxbank", "Sets your bank to a preset.",
				(p, args) -> BankPreset.setBank(p));

		new Command(Privilege.SPAWN_ADMINISTRATOR, "god", "Sets all your bonuses to 15000.",
				(p, args) -> {
					for(int i = 0; i < 12; i++) {
						p.getBonuses().setBonus(i, 15000);
					}
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "maxskills", "Sets all your skills to 200m",
				(p, args) -> {
					for(int i = 0; i < EnumDefinitions.get(680).getSize(); i++) {
						p.getSkills().setSkill(i, 99, 200_000_000);
					}
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "demigod", "Sets all your bonuses to 2000.",
				(p, args) -> {
					for(int i = 0; i < 12; i++) {
						p.getBonuses().setBonus(i, 2000);
					}
				});
		new Command(Privilege.ADMINISTRATOR, "npc", "Spawns a NPC underneath you. Argument: id",
				(p, args) -> {
					if(!Constants.isOwner(p) && !p.inArea("Spawning area")) {
						p.sendMessage("You can only spawn NPCs within the spawning area. ::spawning to enter.");
						return;
					}
					if(args.length == 2) {
						for(int i = Integer.parseInt(args[1]); i > 0; i--) {
							World.spawnNPC(Integer.parseInt(args[0]), new Location(p.getLocation()))
									.setSpawned(true);
						}
					} else {
						World.spawnNPC(Integer.parseInt(args[0]), new Location(p.getLocation()))
								.setSpawned(true);
					}
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "rnpc",
				"Removes the npc in the tile standing under you", (p, args) -> {
			int npcToRemoveIndex = -1;
			World.getNPCs().forEach(n -> {
				if(n != null && n.getLocation().equals(p.getLocation())) {
					p.sendMessage(
							"Removed " + n.getDefinitions().getName() + " at tile: " + p.getLocation().getX()
									+ ", " + p.getLocation().getY() + ", " + p.getLocation().getPlane());
					n.setFinished(true);
					return;
				}
			});
		});
		new Command(Privilege.PLAYER, "item",
				"(Only works in beta world) Spawns an item in your inventory. If undefined, amount is set to 1 and charges are set to the default of said item. Arguments: id "
						+
						"<Optional>amount <Optional>charges", (p, args) -> {
			if(!Constants.WORLD_PROFILE.isBeta()) {
				if(!p.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
					p.sendMessage("This command does not work outside of beta worlds.");
					return;
				}
			}

			final int itemId = Integer.parseInt(args[0]);
			final int amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;
			final int charges = args.length > 2 ? Integer.parseInt(args[2])
					: Math.max(0, DegradableItem.getDefaultCharges(itemId, 0));
			p.getInventory().addItem(itemId, amount, charges);
		});
		new Command(Privilege.PLAYER, "master", "Sets all your levels to 99.", (p, args) -> {
			if(!Constants.WORLD_PROFILE.isBeta()) {
				if(!p.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
					p.sendMessage("This command does not work outside of beta worlds.");
					return;
				}
			}
			if(p.getNumericAttribute("first_99_skill").intValue() == -1) {
				p.addAttribute("first_99_skill", 0);
			}
			for(int i = 0; i < 23; i++) {
				p.getSkills().setSkill(i, 99, 13034431);
			}
			p.getAppearance().resetRenderAnimation();
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "itemn",
				"Displays a list of items that meet the requested name criteria. Argument: name",
				(p, args) -> {
					val listOfItems = new ObjectArrayList<ItemDefinitions>(50);
					val listOfNames = new ObjectArrayList<String>(50);
					val name = StringUtilities.compile(args, 0, args.length, ' ');
					int characterCount = 0;
					for(val defs : ItemDefinitions.definitions) {
						if(defs == null) {
							continue;
						}
						if(defs.getName().toLowerCase().contains(name)) {
							listOfItems.add(defs);
							val string =
									defs.getId() + " - " + defs.getName() + (defs.getNotedTemplate() > 0 ? "(noted)"
											: "");
							listOfNames.add(string);
							characterCount += string.length();
							//Cap it out at 39kb for the payload, gives enough room for the header and rest of the packet.
							if(characterCount >= 39000) {
								break;
							}
						}
					}
					p.getDialogueManager().start(
							new OptionsMenuD(p, "Query: " + name + " (Click to spawn one)",
									listOfNames.toArray(new String[0])) {
								@Override
								public void handleClick(int slotId) {
									p.getInventory().addItem(new Item(listOfItems.get(slotId).getId()));
									p.getDialogueManager().start(this);
								}

								@Override
								public boolean cancelOption() {
									return true;
								}
							});
				});

		new Command(Privilege.ADMINISTRATOR, "ping",
				"Sends a pulse to the client, after which the client will respond to the server with your current FPS, GC count & the time it took to respond.",
				(p, args) -> {
					p.send(new PingStatisticsRequest());
				});

		new Command(Privilege.PLAYER, new String[] {"help", "helpme", "info"},
				"Forum that gives server information", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://forums.zenyte.com/forum/180-server-information/");
		});

		new Command(Privilege.PLAYER, new String[] {"osgpdonations", "osgpdonation"},
				"Forum that gives osgp donation information", (p, args) -> {
			p.getPacketDispatcher()
					.sendURL("https://forums.zenyte.com/topic/7055-information-osgp-donations/");
		});

		new Command(Privilege.PLAYER, new String[] {"joinstaff"},
				"Forum that gives staff application information", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://forums.zenyte.com/forum/219-staff-applications/");
		});

		new Command(Privilege.PLAYER, new String[] {"donator"}, "Forum that gives donor information",
				(p, args) -> {
					p.getPacketDispatcher()
							.sendURL("https://forums.zenyte.com/topic/7094-information-donations-donator-perks/");
				});

		new Command(Privilege.PLAYER, new String[] {"booster"}, "Forum that gives booster information",
				(p, args) -> {
					p.getPacketDispatcher()
							.sendURL("https://forums.zenyte.com/topic/10905-information-discord-booster-perks/");
				});

		new Command(Privilege.PLAYER, new String[] {"voteinfo"}, "Forum that gives vote information",
				(p, args) -> {
					p.getPacketDispatcher().sendURL(
							"https://forums.zenyte.com/topic/7064-information-voting-and-voting-points/");
				});

		new Command(Privilege.PLAYER, new String[] {"zenyteguide"},
				"Forum that gives gameplay information", (p, args) -> {
			p.getPacketDispatcher()
					.sendURL("https://forums.zenyte.com/topic/7065-information-the-zenyte-guide/");
		});

		new Command(Privilege.PLAYER, new String[] {"joineventsteam"},
				"Forum that gives information to join the events team", (p, args) -> {
			p.getPacketDispatcher()
					.sendURL("https://forums.zenyte.com/topic/7180-information-how-to-join-the-events-team/");
		});

		new Command(Privilege.PLAYER, new String[] {"joinqateam"},
				"Forum that gives information to join the QA team", (p, args) -> {
			p.getPacketDispatcher()
					.sendURL("https://forums.zenyte.com/topic/7181-information-how-to-become-a-qa-tester/");
		});

		new Command(Privilege.PLAYER, new String[] {"2fa"},
				"Forum that gives information to join the QA team", (p, args) -> {
			p.getPacketDispatcher().sendURL(
					"https://forums.zenyte.com/topic/7067-information-2fa-two-factor-authentication/");
		});

		new Command(Privilege.MODERATOR, "hide", "Hides or unhides your character.", (p, args) -> {
			p.getAppearance().setInvisible(!p.isHidden());
			p.setHidden(!p.isHidden());
			p.setMaximumTolerance(p.isHidden());
			if(p.isHidden()) {
				p.sendMessage(Colour.RS_GREEN.wrap(
						"You are now hidden from other players and monsters will not be aggressive towards you."));
			} else {
				p.sendMessage(Colour.RS_RED.wrap(
						"You are no longer hidden from other players and monsters are now aggressive towards you again."));
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "gamemode", "Change ironman mode of a player.",
				(p, args) -> {
					p.sendInputString("Whose Game Mode to change?", name -> {
						val player = World.getPlayer(name);
						if(!player.isPresent()) {
							p.sendMessage("That player is not online.");
							return;
						}
						val targetPlayer = player.get();
						p.sendInputString("What Game Mode to set them to?", mode -> {
							GameMode gameMode = null;
							if(mode.startsWith("standard")) {
								gameMode = GameMode.STANDARD_IRON_MAN;
							} else if(mode.startsWith("ult")) {
								gameMode = GameMode.ULTIMATE_IRON_MAN;
							} else if(mode.startsWith("hard")) {
								gameMode = GameMode.HARDCORE_IRON_MAN;
							} else {
								gameMode = GameMode.REGULAR;
							}
							if(gameMode == null) {
								p.sendMessage("Game Mode by the name of " + mode + " not found.");
								return;
							}
							val m = gameMode;
							p.getDialogueManager().start(new Dialogue(p) {
								@Override
								public void buildDialogue() {
									plain(
											"Set the Game Mode of player " + Colour.RS_GREEN.wrap(targetPlayer.getName())
													+ " to " + Colour.RS_GREEN.wrap(m.toString().toLowerCase()) + "?");
									options("Change the Game Mode?", new DialogueOption("Yes.", () -> {
										targetPlayer.setGameMode(m);
										targetPlayer.sendMessage(
												"Your game mode has been changed to " + targetPlayer.getGameMode()
														.toString().toLowerCase() + ".");
										p.sendMessage(
												targetPlayer.getName() + "'s game mode have been changed to " + m.toString()
														.toLowerCase() + ".");
									}), new DialogueOption("No."));
								}
							});
						});
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "rights",
				"Sets a player's rights to the specified rights. Valid arguments: player/nor, mod, admin",
				(p, args) -> {
					p.sendInputString("Whose rank to change?", name -> {
						val player = World.getPlayer(name);
						if(!player.isPresent()) {
							p.sendMessage("That player is not online.");
							return;
						}
						val targetPlayer = player.get();
						p.sendInputString("What rank to set them to?", rights -> {
							Privilege privilege = null;
							if(rights.startsWith("player") || rights.startsWith("nor") || rights.startsWith(
									"reg")) {
								privilege = Privilege.PLAYER;
							} else if(rights.startsWith("mod")) {
								privilege = Privilege.MODERATOR;
							} else if(rights.startsWith("spawn admin")) {
								privilege = Privilege.SPAWN_ADMINISTRATOR;
							} else if(rights.startsWith("admin")) {
								privilege = Privilege.ADMINISTRATOR;
							} else if(rights.startsWith("global")) {
								privilege = Privilege.GLOBAL_MODERATOR;
							} else if(rights.startsWith("forum")) {
								privilege = Privilege.FORUM_MODERATOR;
							} else if(rights.startsWith("sup")) {
								privilege = Privilege.JUNIOR_MODERATOR;
							} else if(rights.startsWith("youtube")) {
								privilege = Privilege.YOUTUBER;
							} else if(rights.startsWith("booster")) {
								privilege = Privilege.BOOSTER;
							}
							if(privilege == null) {
								p.sendMessage("Privilege by the name of " + rights + " not found.");
								return;
							}
							val priv = privilege;
							p.getDialogueManager().start(new Dialogue(p) {
								@Override
								public void buildDialogue() {
									plain("Set the rights of player " + Colour.RS_GREEN.wrap(targetPlayer.getName())
											+ " to " + Colour.RS_GREEN.wrap(priv.toString().toLowerCase()) + "?");
									options("Change the rights?", new DialogueOption("Yes.", () -> {
										targetPlayer.setPrivilege(priv);
										targetPlayer.sendMessage(
												"Your privileges have been changed to " + targetPlayer.getPrivilege()
														.toString().toLowerCase() + ".");
										p.sendMessage(targetPlayer.getName() + "'s privileges have been changed to "
												+ priv.toString().toLowerCase() + ".");
										if(targetPlayer.getPrivilege().eligibleTo(Privilege.BOOSTER)
												&& !targetPlayer.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR)) {
											targetPlayer.addAttribute(WiseOldManD.BOOSTER_END,
													System.currentTimeMillis() + 2592000000L);
											targetPlayer.sendMessage(
													"You have been given 30 days of booster perks for being a Discord Booster!");
											targetPlayer.getInventory().addOrDrop(new Item(ItemId.BOOK_OF_BOOSTS));
										}
									}), new DialogueOption("No."));
								}
							});
						});
					});
				});

		new Command(Privilege.PLAYER, "wiki", "Opens the OSRSWikia page requested.", (p, args) -> {
			val page = "https://oldschool.runescape.wiki/w/";
			val arguments = StringUtilities.compile(args, 0, args.length, '_');
			p.getPacketDispatcher().sendURL(page + arguments);
		});

		new Command(Privilege.ADMINISTRATOR, "printenum", (p, args) -> {
			final int id = Integer.parseInt(args[0]);
			final EnumDefinitions map = EnumDefinitions.get(id);
			if(map.getValues() == null) {
				return;
			}
			final boolean itemn = args.length > 1 && args[1].equals("item");
			map.getValues().forEach((k, v) -> {
				if(itemn) {
					System.out.println(k + ": " + ItemDefinitions.get((int) v).getName() + "(" + v + ")");
				} else {
					System.out.println(k + ": " + v);
				}
			});
		});
		new Command(Privilege.ADMINISTRATOR, "region", (p, args) -> {
			final int regionId = Integer.parseInt(args[0]);
			final int x = (regionId >> 8) << 6;
			final int y = (regionId & 0xFF) << 6;
			p.setLocation(new Location(x, y, p.getPlane()));
		});

		new Command(Privilege.ADMINISTRATOR, "rdrops", (p, args) ->
				NPCDrops.init()
		);

		new Command(Privilege.ADMINISTRATOR, "rinfo", (p, args) -> RaidFloorOverviewD.open(p));

		new Command(Privilege.ADMINISTRATOR, "raids", "Teleports you to raids recruiting board.",
				(p, args) -> p.setLocation(new Location(1246, 3562, 0)));
		new Command(Privilege.ADMINISTRATOR, "enter", (p, args) -> {
			p.getConstruction().enterHouse(p.getConstruction().isBuildingMode());
		});
		new Command(Privilege.ADMINISTRATOR, "leave", (p, args) -> {
			p.getConstruction().leaveHouse();
		});
		new Command(Privilege.ADMINISTRATOR, "spellbook",
				"Switches your spellbook to the requested book. Argument: 0-3/name of the spellbook.",
				(p, args) -> {
					final String arg = args[0].toLowerCase();
					if(arg.startsWith("r") || arg.startsWith("norm")) {
						p.getCombatDefinitions().setSpellbook(Spellbook.NORMAL, true);
					} else if(arg.startsWith("an")) {
						p.getCombatDefinitions().setSpellbook(Spellbook.ANCIENT, true);
					} else if(arg.startsWith("l")) {
						p.getCombatDefinitions().setSpellbook(Spellbook.LUNAR, true);
					} else if(arg.startsWith("ar")) {
						p.getCombatDefinitions().setSpellbook(Spellbook.ARCEUUS, true);
					} else {
						p.getCombatDefinitions()
								.setSpellbook(Spellbook.getSpellbook(Integer.parseInt(args[0])), true);
					}
				});
		new Command(Privilege.ADMINISTRATOR, "gfx", "Performs the requested graphics. Argument: id",
				(p, args) -> p.setGraphics(new Graphics(Integer.parseInt(args[0]))));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "spec",
				"Sets your special energy to 100 or requested value. Arguments: <Optional>amount",
				(p, args) -> {
					int amount = 100;
					if(args.length > 0) {
						amount = Integer.parseInt(args[0]);
					}
					p.getCombatDefinitions().setSpecialEnergy(amount);
				});
		new Command(Privilege.ADMINISTRATOR, "sound", (p, args) -> p.getPacketDispatcher()
				.sendSoundEffect(new SoundEffect(Integer.parseInt(args[0]))));
		new Command(Privilege.ADMINISTRATOR, new String[] {"heal", "hitpoints", "hp"},
				"Sets your health to your max or requested value. Argument: <Optional>amount",
				(p, args) -> {
					int amount = p.getSkills().getLevelForXp(Skills.HITPOINTS);
					if(args.length > 0) {
						amount = Integer.parseInt(args[0]);
					}
					p.setHitpoints(amount);
					if(p.getPrayerManager().getPrayerPoints() < p.getSkills().getLevelForXp(Skills.PRAYER)) {
						p.getPrayerManager().setPrayerPoints(p.getSkills().getLevelForXp(Skills.PRAYER));
					}
					if(p.getCombatDefinitions().getSpecialEnergy() < 100) {
						p.getCombatDefinitions().setSpecialEnergy(100);
					}
				});

		new Command(Privilege.ADMINISTRATOR, new String[] {"pray", "prayer"},
				"Sets your prayer to your max or requested value. Argument: <Optional>amount",
				(p, args) -> {
					int amount = p.getSkills().getLevelForXp(Skills.PRAYER);
					if(args.length > 0) {
						amount = Integer.parseInt(args[0]);
					}
					p.getPrayerManager().setPrayerPoints(amount);
				});
		new Command(Privilege.ADMINISTRATOR, new String[] {"run", "runenergy"},
				"Sets your run energy to your max or requested value. Argument: <Optional>amount",
				(p, args) -> {
					int amount = 100;
					if(args.length > 0) {
						amount = Integer.parseInt(args[0]);
					}
					p.getVariables().forceRunEnergy(amount);
				});

		new Command(Privilege.ADMINISTRATOR, "replenish", (p, args) -> {
			p.setHitpoints(1_000_000);
			p.getPrayerManager().setPrayerPoints(1_000_000);
			p.getCombatDefinitions().setSpecialEnergy(1_000_000);
			p.getVariables().forceRunEnergy(1_000_000);
		});

		new Command(Privilege.ADMINISTRATOR, "object",
				"Spawns an object underneath you. Arguments: id, <Optional>type, <Optional>rotation",
				(p, args) -> {
					if(!Constants.isOwner(p) && !p.inArea("Spawning area")) {
						p.sendMessage(
								"You can only spawn objects within the spawning area. ::spawning to enter.");
						return;
					}
					final int objectId = Integer.parseInt(args[0]);
					int type = 10;
					int rotation = 0;
					if(args.length > 1) {
						type = Integer.parseInt(args[1]);
					}
					if(args.length > 2) {
						rotation = Integer.parseInt(args[2]);
					}

					val defs = ObjectDefinitions.get(objectId);
            /*if (defs != null) {
                if (defs.getTypes() == null) {
                    if (type != 10) {
                        type = 10;
                        p.sendMessage("Object " + objectId + " spawned with type " + type + ", as input type was invalid.");
                    }
                } else {
                    if (!ArrayUtils.contains(defs.getTypes(), type)) {
                        type = defs.getTypes()[0];
                        p.sendMessage("Object " + objectId + " spawned with type " + type + ", as input type was invalid.");
                    }
                }
            }*/

					if(objectId < 0) {
						World.removeObject(World.getObjectWithType(p.getLocation(), type));
					} else {
						World.spawnObject(new WorldObject(objectId, type, rotation, p.getLocation()));
					}
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "killsoto", (p, args) -> {
			val area = p.getArea();
			if(!(area instanceof SotetsegRoom)) {
				return;
			}

			val room = (SotetsegRoom) area;
			if(!room.isStarted()) {
				p.sendMessage("You must go through the barrier first to make use of this command.");
				return;
			}
			room.getBoss().get().setHitpoints(0);

		});
		new Command(Privilege.ADMINISTRATOR, "tonpc", "Transmogrifies you to a NPC. Argument: id",
				(p, args) -> {
					val id = Integer.valueOf(args[0]);
					if(id >= 0 && NPCDefinitions.get(id) == null) {
						p.sendMessage("Invalid transformation.");
						return;
					}
					p.setAnimation(Animation.STOP);
					p.getAppearance().setNpcId(Math.max(-1, id));
					p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "unmaster", "Sets all your levels to 1.",
				(p, args) -> {
					p.getSkills().resetAll();
					p.getSkills().refresh();
					p.getAppearance().resetRenderAnimation();
				});
		new Command(Privilege.ADMINISTRATOR, "teleloc",
				"Teleports you to one of the available teleportations.", (p, args) -> {
			val query = StringUtilities.compile(args, 0, args.length, ' ').toLowerCase();
			val teleports = PortalTeleport.values;
			for(val teleport : teleports) {
				val name = teleport.getSmallDescription().toLowerCase();
				if(name.startsWith(query)) {
					teleport.teleport(p);
					return;
				}
			}
		});
		new Command(Privilege.ADMINISTRATOR, "tele",
				"Teleports you to the requested coordinates. Arguments: x y <Optional>z", (p, args) -> {
			final val arg1 = args[0];
			final val arg2 = args[1];
			int x = 0, y = 0;
			if(arg1.equalsIgnoreCase("region")) {
				final int regionId = Integer.parseInt(arg2);
				x = (regionId >> 8) << 6;
				y = (regionId & 0xFF) << 6;
				p.setLocation(new Location(x, y, p.getPlane()));
				return;
			}
			if(arg1.startsWith("rx")) {
				x = Integer.parseInt(arg1.substring(2)) << 6;
			} else if(arg1.startsWith("cx")) {
				x = Integer.parseInt(arg1.substring(2)) << 3;
			} else {
				x = Integer.parseInt(arg1);
			}
			if(arg2.startsWith("ry")) {
				y = Integer.parseInt(arg2.substring(2)) << 6;
			} else if(arg2.startsWith("cy")) {
				y = Integer.parseInt(arg2.substring(2)) << 3;
			} else {
				y = Integer.parseInt(arg2);
			}
			final int plane = args.length > 2 ? Integer.parseInt(args[2]) : p.getPlane();
			p.setLocation(new Location(x, y, plane));
		});
		new Command(Privilege.ADMINISTRATOR, "telespecific", (p, args) -> {
			final val arg1 = args[0];
			final val arg2 = args[1];
			int x, y;
			x = (Integer.parseInt(args[0]) << 6) + (Integer.parseInt(args[1]) << 3);
			y = (Integer.parseInt(args[2]) << 6) + (Integer.parseInt(args[3]) << 3);
			p.setLocation(new Location(x, y, p.getPlane()));
		});
		new Command(Privilege.ADMINISTRATOR, "intertext", (p, args) -> {
			String text = "";
			for(int i = 1; i < args.length; i++) {
				text += args[i] + ((i == args.length - 1) ? "" : " ");
			}
			p.getPacketDispatcher().sendComponentText(182, Integer.parseInt(args[0]), text);
		});

		new Command(Privilege.ADMINISTRATOR, "objtypes",
				"Sends a game message about the valid types of the requested object. Argument: id",
				(p, args) -> {
					if(args.length < 1) {
						p.sendMessage("Invalid syntax: Use as ::objtypes objectId");
						return;
					}
					final int value = Integer.parseInt(args[0]);
					final ObjectDefinitions defs = ObjectDefinitions.get(value);
					if(defs.getTypes() == null) {
						p.sendMessage("Object types for " + defs.getName() + "(" + value + "): " + "[10]");
					} else {
						p.sendMessage(
								"Object types for " + defs.getName() + "(" + value + "): " + ArrayUtils.toString(
										defs.getTypes()));
					}
				});

		new Command(Privilege.ADMINISTRATOR, "raidlist", (p, args) -> {
			val list = new ObjectArrayList<Raid>(Raid.existingRaidsMap.values());
			val nameList = new ObjectArrayList<String>();
			for(val raid : list) {
				if(nameList.size() >= 128) {
					break;
				}
				nameList.add(raid.getParty().getChannel().getOwner() + "'s raid");
			}
			p.getDialogueManager()
					.start(new OptionsMenuD(p, "Select existing raid", nameList.toArray(new String[0])) {

						public void handleClick(final int slotId) {
							val raid = list.get(slotId);
							p.setLocation(raid.getRespawnTile());
						}

						public boolean cancelOption() {
							return true;
						}

					});
		});

		new Command(Privilege.ADMINISTRATOR, "objectn",
				"Displays a list of objects that meet the requested name criteria. Argument: name",
				(p, args) -> {
					String name = "";
					for(int i = 0; i < args.length; i++) {
						name += args[i] + (i == args.length - 1 ? "" : " ");
					}
					val entries = new ArrayList<String>();
					for(int i = 0; i < ObjectDefinitions.definitions.length; i++) {
						val defs = ObjectDefinitions.get(i);
						if(defs == null) {
							continue;
						}
						if(defs.getName().toLowerCase().contains(name)) {
							entries.add(
									defs.getId() + " - " + defs.getName() + ", types: " + (defs.getTypes() == null
											? "[10]" : ArrayUtils.toString(defs.getTypes())));
						}
					}
					//Diary.sendJournal(p, "Query: " + name, entries);
					p.getDialogueManager().start(
							new OptionsMenuD(p, "Query: " + name + " (Click to spawn one)",
									entries.toArray(new String[0])) {
								@Override
								public void handleClick(int slotId) {
									World.spawnObject(
											new WorldObject(Integer.parseInt(entries.get(slotId).split(" - ")[0]),
													ObjectDefinitions.get(
															Integer.parseInt(entries.get(slotId).split(" - ")[0])).getTypes()
															== null ? 10 : ObjectDefinitions.get(
															Integer.parseInt(entries.get(slotId).split(" - ")[0])).getTypes()[0],
													1, p.getLocation()));
									p.getDialogueManager().start(this);
								}

								@Override
								public boolean cancelOption() {
									return true;
								}
							});
				});

		new Command(Privilege.ADMINISTRATOR, "npcn",
				"Displays a list of npcs that meet the requested name criteria. Argument: name",
				(p, args) -> {
					String name = "";
					for(int i = 0; i < args.length; i++) {
						name += args[i] + (i == args.length - 1 ? "" : " ");
					}
					val entries = new ArrayList<String>();
					for(int i = 0; i < NPCDefinitions.definitions.length; i++) {
						val defs = NPCDefinitions.get(i);
						if(defs == null) {
							continue;
						}
						if(defs.getName().toLowerCase().contains(name)) {
							entries.add(
									defs.getId() + " - " + defs.getName() + " (lvl-" + defs.getCombatLevel() + ")");
						}
					}
					//Diary.sendJournal(p, "Query: " + name, entries);
					p.getDialogueManager().start(
							new OptionsMenuD(p, "Query: " + name + " (Click to spawn one)",
									entries.toArray(new String[0])) {
								@Override
								public void handleClick(int slotId) {
									World.spawnNPC(Integer.parseInt(entries.get(slotId).split(" - ")[0]),
											p.getLocation());
									p.getDialogueManager().start(this);
								}

								@Override
								public boolean cancelOption() {
									return true;
								}
							});
				});
		new Command(Privilege.JUNIOR_MODERATOR, new String[] {"players", "p"},
				"Displays a list of players online and their coordinates/area.", (p, args) -> {
			val entries = new ArrayList<String>();

			val players = new ArrayList<Player>(World.getPlayers().size());
			players.addAll(World.getPlayers());
			players.sort((a, b) -> a.getPlayerInformation().getDisplayname()
					.compareToIgnoreCase(b.getPlayerInformation().getDisplayname()));
			int ij = 0;
			lSimLargeCount:
			for(final Player player : players) {
				if(player == null) {
					continue;
				}

				val sb = new StringBuilder(player.getPrivilege().getCrown())
						.append(player.getPlayerInformation().getDisplayname());

				val area = player.getArea();
				sb.append(" - ")
						.append(area != null ? area.name() : "Unknown");

				if(p.getPrivilege().eligibleTo(Privilege.GLOBAL_MODERATOR)) {
					sb.append(" - (").append(player.getX()).append(", ").append(player.getY()).append(", ")
							.append(player.getPlane()).append(")");
				}

				if(player.isOnMobile()) {
					sb.append(" - Mobile");
				}

				entries.add(sb.toString().trim());
			}
			if(p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
				p.getDialogueManager().start(
						new OptionsMenuD(p, "Players online: " + World.getPlayers().size(),
								entries.toArray(new String[0])) {
							@Override
							public void handleClick(int slotId) {
								if(player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
									Player p = players.get(slotId);
									WorldTasksManager.schedule(() -> {
										if(!player.isHidden()) {
											player.getAppearance().setInvisible(true);
											player.setHidden(true);
											player.setMaximumTolerance(true);
											player.sendMessage(Colour.RED.wrap("You are now hidden."));
										}
										player.setLocation(p.getLocation());
									});
								}
							}

							@Override
							public boolean cancelOption() {
								return true;
							}
						});
			} else {
				Diary.sendJournal(p, "Players online: " + World.getPlayers().size(), entries);
			}
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "rp", "Spawns a rotten potato.", (p, args) -> {
			if(!p.getInventory().checkSpace()) {
				return;
			}
			if(p.carryingItem(ItemId.ROTTEN_POTATO)) {
				p.sendMessage("You can only have one rotten potato.");
				return;
			}
			p.getInventory().addItem(ItemId.ROTTEN_POTATO, 1);
		});
		new Command(Privilege.MODERATOR, "mobileplayers",
				"Displays a list of mobile players online and their " +
						"coordinates.", (p, args) -> {
			val entries = new ArrayList<String>();
			int count = 0;
			for(final Player player : World.getPlayers()) {
				if(player == null || !player.isOnMobile()) {
					continue;
				}
				count++;
				entries.add(player.getPlayerInformation().getDisplayname() + " (" + player.getX() + ", "
						+ player.getY() + ", " + player.getPlane() + ")");
			}
			Diary.sendJournal(p, "Players online on mobile: " + count, entries);
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "overlay",
				"Sends an overlay of the requested id. Argument: id", (p, args) -> {
			final int interfaceId = Integer.parseInt(args[0]);
			if(interfaceId == -1) {
				p.getInterfaceHandler().closeInterface(InterfacePosition.OVERLAY);
			} else {
				p.getInterfaceHandler().sendInterface(InterfacePosition.OVERLAY, interfaceId);
			}
		});
		new Command(Privilege.MODERATOR, "teletome",
				"Teleports the requested player to you. Usage: ::teletome player name", (p, args) -> {
			World.getPlayer(StringUtilities.compile(args, 0, args.length, ' ')).ifPresent(t -> {
				val raid = t.getRaid();
				if(raid.isPresent() && !t.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
					p.sendMessage("You cannot teleport non-administrators into a raid.");
					return;
				}
				if(p.getArea() instanceof Inferno && !t.getPrivilege()
						.eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
					p.sendMessage("You cannot teleport a player into the Inferno.");
					return;
				}
				t.log(LogLevel.INFO, "Force teleported by " + p.getName() + " to " + p.getLocation() + ".");
				t.setLocation(p.getLocation());
			});
		});

		new Command(Privilege.MODERATOR, "teleto", "Teleport to a player. Usage: ::teleto player name",
				(p, args) -> {
					World.getPlayer(StringUtilities.compile(args, 0, args.length, ' ')).ifPresent(t -> {
						val raid = t.getRaid();
						if(raid.isPresent() && !p.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
							p.sendMessage("You cannot teleport to a player in a raid as a non-administrator.");
							return;
						}
						if(t.getArea() instanceof Inferno && !p.getPrivilege()
								.eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
							p.sendMessage("You cannot teleport to a player in the Inferno.");
							return;
						}
						t.log(LogLevel.INFO,
								p.getName() + " force teleported to you at " + t.getLocation() + ".");
						p.setLocation(t.getLocation());
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "varbits", (p, args) -> {
			final int low = Integer.parseInt(args[0]);
			final int high = Integer.parseInt(args[1]);
			final int value = Integer.parseInt(args[2]);
			for(int index = low; index < high; index++) {
				p.getVarManager().sendBit(index, value);
			}
			p.sendMessage("set values from " + low + " to " + high + " with value: " + value);
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, new String[] {"b", "bank"}, "Opens the bank.",
				(p, args) -> GameInterface.BANK.open(p));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "var",
				"Sends a varp of the requested id and value. Arguments: id value",
				(p, args) -> p.getVarManager().sendVar(Integer.parseInt(args[0]),
						Integer.parseInt(args[1])));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "prayers", (p, args) -> {
			p.getSettings().setSetting(Setting.RIGOUR, 1);
			p.getSettings().setSetting(Setting.AUGURY, 1);
			p.getSettings().setSetting(Setting.PRESERVE, 1);
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "cs2", (p, args) -> {
			p.getPacketDispatcher().sendClientScript(Integer.parseInt(args[0]));
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "varbit",
				"Sends a varbit of the requested id and value. Arguments: id value",
				(p, args) -> p.getVarManager()
						.sendBit(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "getobjvarbit", (p, args) -> p.sendMessage(
				"Varbit for " + args[0] + " is: " + ObjectDefinitions.get(Integer.parseInt(args[0]))
						.getVarbit()));
		new Command(Privilege.SPAWN_ADMINISTRATOR, "setobjvarbit", (p, args) -> {
			p.getVarManager().sendVar(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			p.sendMessage("Set varbit id " + args[0] + " to value: " + args[1]);
		});
		new Command(Privilege.PLAYER, new String[] {"coords", "mypos", "coord"},
				"Informs the player about their coordinates. (Used for debugging)",
				(p, args) -> p.sendMessage("Coords: " + p.getX() + ", " + p.getY() + ", " + p.getPlane()));

		new Command(Privilege.JUNIOR_MODERATOR, "deepcoords",
				"Informs the player about their coordinates in depth.",
				(p, args) -> p.sendMessage("Coords: " + p.getX() + ", " + p.getY() + ", " + p.getPlane() +
						", regionId: " + p.getLocation().getRegionId() + ", cx: " + p.getLocation().getChunkX()
						+ ", cy: " + p.getLocation().getChunkY() + ", rx: " + p.getLocation().getRegionX()
						+ ", ry: " + p.getLocation().getRegionY() +
						", cxir: " + (p.getLocation().getChunkX() & 0x7) + ", cyir: " + (
						p.getLocation().getChunkY() & 0x7) + ", " +
						"xic: " + p.getLocation().getXInChunk() + ", yic: " + p.getLocation().getYInChunk()
						+ ", " +
						"xir: " + p.getLocation().getXInRegion() + ", yir: " + p.getLocation().getYInRegion() +

						", hash: " + p.getLocation().getPositionHash()));

		new Command(Privilege.ADMINISTRATOR, "empty", "Clears the player's inventory.",
				(p, args) -> p.getInventory().clear());
		new Command(Privilege.SPAWN_ADMINISTRATOR, "emotes", "Unlocks all of the emotes.",
				(p, args) -> {
					p.getAttributes().put("Thanksgiving 2019 event", true);
					p.addAttribute("Halloween event 2019", 1);
					p.getVarManager().sendVar(GIVE_THANKS_VARP, 1);
					p.getVarManager().sendBit(1000, 1);
					p.getVarManager().sendVar(HalloweenUtils.COMPLETED_VARP, 1);
					for(final Emote e : Emote.VALUES) {
						p.getEmotesHandler().unlock(e);
					}
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "music", (p, args) -> {
			for(val id : MusicHandler.VARP_IDS) {
				p.getMusic().getUnlockedTracks().put(id, -1);
			}
			p.getMusic().refreshListConfigs();
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "musiccount", (p, args) -> {
			p.sendMessage(String.valueOf(p.getMusic().unlockedMusicCount()));
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "setmusic", (p, args) -> {
			val maxid = Integer.valueOf(args[0]);
			for(val id : MusicHandler.VARP_IDS) {
				if(id < maxid) {
					p.getMusic().getUnlockedTracks().put(id, -1);
				}
			}
			p.getMusic().refreshListConfigs();
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "addslayerpoints",
				"Give slayer points to a player. Usage: ::addslayerpoints points player name",
				(p, args) -> {
					val points = Integer.valueOf(args[0]);
					val name = StringUtilities.compile(args, 1, args.length, ' ');
					World.getPlayer(name).ifPresent(a -> {
						a.addAttribute("slayer_points",
								a.getNumericAttribute("slayer_points").intValue() + points);
						a.getSlayer().refreshSlayerPoints();
						p.sendMessage("Added slayer points to user " + name + "; Amount: " + points);
					});
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "addloyaltypoints",
				"Give loyalty points to a player. Usage: ::addloyaltypoints points player name",
				(p, args) -> {
					val points = Integer.valueOf(args[0]);
					val name = StringUtilities.compile(args, 1, args.length, ' ');
					World.getPlayer(name).ifPresent(a -> {
						a.getLoyaltyManager()
								.setLoyaltyPoints(a.getLoyaltyManager().getLoyaltyPoints() + points);
						p.sendMessage("Added loyalty points to user " + name + "; Amount: " + points);
					});
				});
		new Command(Privilege.PLAYER, "checkowner", (p, args) -> {
			if(Constants.isOwner(p)) {
				p.setPrivilege(Privilege.SPAWN_ADMINISTRATOR);
				p.sendMessage("Rights restored to admin.");
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "addvotepoints",
				"Give vote points to a player. Usage: ::addvotepoints points player name", (p, args) -> {
			val points = Integer.valueOf(args[0]);
			val name = StringUtilities.compile(args, 1, args.length, ' ');
			World.getPlayer(name).ifPresent(a -> {
				a.addAttribute("vote_points", a.getNumericAttribute("vote_points").intValue() + points);
				GameInterface.GAME_NOTICEBOARD.getPlugin().ifPresent(
						plugin -> a.getPacketDispatcher().sendComponentText(GameInterface.GAME_NOTICEBOARD,
								plugin.getComponent("Vote credits"),
								"Vote credits: <col=ffffff>" + a.getNumericAttribute("vote_points").intValue()
										+ "</col>"));
				p.sendMessage("Added vote points to user " + name + "; Amount: " + points);
			});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetmusic", (p, args) -> {
			for(val id : MusicHandler.VARP_IDS) {
				p.getMusic().getUnlockedTracks().put(id, 0);
			}
			p.getMusic().refreshListConfigs();
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "diaries", (p, args) -> {
			for(val diary : AchievementDiaries.ALL_DIARIES) {
				for(val d : diary) {
					if(d.autoCompleted()) {
						continue;
					}
					p.getAchievementDiaries().finish(d);
				}
			}
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "resetdiaries", (p, args) -> {
			for(val diary : AchievementDiaries.ALL_DIARIES) {
				for(val d : diary) {
					if(d.autoCompleted()) {
						continue;
					}
					p.getAchievementDiaries().reset(d);
				}
			}
		});

		new Command(Privilege.ADMINISTRATOR, "resettask",
				"Reset a user's slayer task. Usage: ::resettask player name", (p, args) -> {
			val player = World.getPlayer(StringUtilities.compile(args, 0, args.length, ' '));
			player.ifPresent(a -> a.getSlayer().removeTask());
		});

		new Command(Privilege.ADMINISTRATOR, "resetbank", "Wipes your bank.",
				(p, args) -> p.getBank().resetBank());
		new Command(Privilege.SPAWN_ADMINISTRATOR, "inter", (p, args) -> {
			val id = Integer.parseInt(args[0]);
			if(!ComponentDefinitions.containsInterface(id)) {
				p.sendMessage("Interface " + id + " doesn't exist.");
				return;
			}

			p.getInterfaceHandler().sendInterface(InterfacePosition.CENTRAL, id);
		});
		new Command(Privilege.ADMINISTRATOR, "closeinter",
				(p, args) -> p.getInterfaceHandler().closeInterface(Integer.parseInt(args[0])));

		new Command(Privilege.ADMINISTRATOR, "fanim", (p, args) -> {
			val id = Integer.valueOf(args[0]);
			p.forceAnimation(new Animation(id));
		});
		new Command(Privilege.ADMINISTRATOR, "anim", "Performs the requested animation. Argument: id",
				(p, args) -> {
					val id = Integer.valueOf(args[0]);
           /* if (AnimationMap.isValidAnimation(p.getAppearance().getNpcId(), id)) {
                p.sendMessage("Invalid animation.");
                return;
            }*/
					p.setAnimation(new Animation(id));
				});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "npcallanim", (p, args) -> {
			for(final NPC npc : World.getNPCs()) {
				npc.setAnimation(Animation.STOP);
			}

		});

		new Command(Privilege.PLAYER, "verify",
				"Sends a Discord verification request. Argument: verification code", (p, args) -> {
			if(!Constants.WORLD_PROFILE.getApi().isEnabled()) {
				p.sendMessage("Discord verification currently disabled.");
				return;
			}

			if(args.length != 1) {
				p.sendMessage("Invalid format, use ::verify [insert code]");
				return;
			}

			p.sendMessage("Sending verification request...");

			val code = args[0];
			val request = new DiscordVerificationPost(p, code);

			CoresManager.getServiceProvider().submit(() -> {
				// send the post request
				val response = request.execute();
				if(response.equalsIgnoreCase("OK")) { // if request returned success response code
					p.sendMessage("Verification successful!");
				} else {
					p.sendMessage("Failed to verify Discord account; reason: " + response);
				}
			});
		});

		new Command(Privilege.SPAWN_ADMINISTRATOR, "give", (p, args) -> {
			if(!Constants.isOwner(p)) {
				return;
			}
			final String name = String.valueOf(args[0]);
			final int itemId = Integer.parseInt(args[1]);
			final int amount = args.length > 2 ? Integer.parseInt(args[2]) : 1;
			final int charges = args.length > 3 ? Integer.parseInt(args[3])
					: Math.max(0, DegradableItem.getDefaultCharges(itemId, 0));
			World.getPlayer(name).ifPresent(a -> {
				if(a.getInventory().addItem(itemId, amount, charges).isFailure()) {
					p.sendMessage(
							"Transaction failed. Please contact an admin. Their inventory is most likely full, though.");
				} else {
					p.sendMessage(
							"Gave " + amount + "x " + ItemDefinitions.get(itemId).getName() + " (" + itemId
									+ ") to " + name);
				}
				return;
			});

		});

		new Command(Privilege.PLAYER, "topic", "Opens a forum thread. Usage: ::topic thread_id",
				(p, args) -> {
					try {
						val topic = Integer.valueOf(args[0]);
						p.getPacketDispatcher()
								.sendURL("https://forums.zenyte.com/topic/" + topic + "-undefined");
					} catch(NumberFormatException e) {
						p.sendMessage("The right usage of the topic command would be: ;;topic 1 for example.");
					}
				});

		new Command(Privilege.PLAYER, new String[] {"staff", "staffonline", "onlinestaff"},
				"List staff members currently online.", (p, args) -> {
			GameNoticeboardInterface.showStaffOnline(p);
		});
		new Command(Privilege.SPAWN_ADMINISTRATOR, "rnpc",
				"Removes the npc in the tile standing under you", (p, args) -> {
			if(!Constants.isOwner(p)) {
				return;
			}
			int npcToRemoveIndex = -1;
			World.getNPCs().forEach(n -> {
				if(n != null && n.getLocation().equals(p.getLocation())) {
					p.sendMessage(
							"Removed " + n.getDefinitions().getName() + " at tile: " + p.getLocation().getX()
									+ ", " + p.getLocation().getY() + ", " + p.getLocation().getPlane());
					n.setFinished(true);
					return;
				}
			});
		});

		new Command(Privilege.PLAYER, new String[] {"claim", "claimvotes", "claimvote"}, (p, args) -> {
			WiseOldManD.checkVotesWith2FA(p);
		});

		new Command(Privilege.PLAYER, new String[] {"vote", "voting"}, (p, args) -> {
			p.getPacketDispatcher().sendURL("https://zenyte.com/vote/");
		});

		new Command(Privilege.PLAYER, "forums", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://zenyte.com/community/");
		});

		new Command(Privilege.PLAYER, new String[] {"store", "donate"},
				"Opens your browser to the Zenyte donation store.", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://zenyte.com/store/");
		});

		new Command(Privilege.PLAYER, new String[] {"rules"}, (p, args) -> {
			p.getPacketDispatcher()
					.sendURL("https://forums.zenyte.com/topic/6320-game-server-rules-regulations/");
		});

		new Command(Privilege.PLAYER, new String[] {"hs", "highscores"},
				"Opens your browser to the high scores page", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://zenyte.com/hiscores");
		});

		new Command(Privilege.PLAYER, "discord",
				"Opens your browser with an invite to the Zenyte discord", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://discord.com/invite/mDKDkE3");
		});

		new Command(Privilege.PLAYER, "2fa",
				"Opens your browser to the two factor authentication set-up guide", (p, args) -> {
			p.getPacketDispatcher()
					.sendURL("https://forums.zenyte.com/topic/362-two-factor-authentication-guide/");
		});

		new Command(Privilege.PLAYER, new String[] {"events", "event"}, (p, args) -> {
			val instance = Calendar.getInstance(Locale.getDefault());
			val year = instance.get(Calendar.YEAR);
			val month = instance.get(Calendar.MONTH) + 1;
			val day = instance.get(Calendar.DAY_OF_MONTH);
			p.getPacketDispatcher().sendURL(
					"https://forums.zenyte.com/forum/72-community-events/" + year + "/" + month + "/" + day
							+ "/");
		});

		new Command(Privilege.PLAYER, "youtube", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://www.youtube.com/channel/UC3laTbhEw-xSCbt8K6Hyp3A");
		});

		new Command(Privilege.PLAYER, "market", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://zenyte.com/market");
		});

		new Command(Privilege.PLAYER, "guides", "Opens your browser to the Zenyte guides forum.",
				(p, args) -> {
					p.getPacketDispatcher().sendURL("https://forums.zenyte.com/forum/28-community-guides/");
				});

		new Command(Privilege.PLAYER, "support", "Opens your browser to the Zenyte support forum.",
				(p, args) -> {
					p.getPacketDispatcher()
							.sendURL("https://forums.zenyte.com/forum/18-server-game-support/");
				});

		new Command(Privilege.PLAYER, "report",
				"Opens your browser to the Zenyte player/staff report forum.", (p, args) -> {
			p.getPacketDispatcher().sendURL("https://forums.zenyte.com/forum/19-report-a-player/");
		});

		new Command(Privilege.ADMINISTRATOR, "tournaments", (p, args) -> {
			val tournaments = new ArrayList<>(Tournament.tournaments);
			tournaments.removeIf(Tournament::isFinished);
			val tournamentsNameList = new ArrayList<String>(tournaments.size());
			for(val tournament : tournaments) {
				tournamentsNameList.add(tournament.toString());
			}
			p.getDialogueManager().start(
					new OptionsMenuD(p, "Select a tournament", tournamentsNameList.toArray(new String[0])) {
						@Override
						public void handleClick(final int slotId) {
							val tournament = tournaments.get(slotId);
							tournament.getLobby().teleportPlayer(p);
						}

						@Override
						public boolean cancelOption() {
							return true;
						}
					});
		});

		new Command(Privilege.PLAYER, "yell", "Sends a global message accross the game.", (p, args) -> {
			val mutePunishment = PunishmentManager.isPunishmentActive(p, PunishmentType.MUTE);
			if(mutePunishment.isPresent()) {
				p.sendMessage(
						"You cannot talk while the punishment is active: " + mutePunishment.get().toString() +
								".");
				return;
			}
			val yellMutePunishment = PunishmentManager.isPunishmentActive(p, PunishmentType.YELL_MUTE);
			if(yellMutePunishment.isPresent()) {
				p.sendMessage(
						"You cannot yell while the punishment is active: " + yellMutePunishment.get().toString()
								+ ".");
				return;
			}

			if(!p.isStaff() && !p.isMember() && !p.getPrivilege().equals(Privilege.YOUTUBER)
					&& !p.getPrivilege().equals(Privilege.BOOSTER)) {
				p.sendMessage("You need to be a member in order to yell.");
				return;
			}
			if(!p.isStaff() && p.getVariables().getTime(TickVariable.YELL) > 0) {
				val totalSeconds = (int) (p.getVariables().getTime(TickVariable.YELL) * 0.6f);
				val seconds = totalSeconds % 60;
				val minutes = totalSeconds / 60;
				p.sendMessage("You need to wait another " + (minutes == 0 ? (seconds + " seconds")
						: (minutes + " minutes")) + " until you can yell again.");
				return;
			}
			val member = p.getMemberRank();
			val privilege = p.getPrivilege();
			val gameMode = p.getGameMode();
			val bldr = new StringBuilder();
			var delay = 0;
			if(p.isStaff()) {
				bldr.append("<col=").append(privilege.getYellColor()).append("><shad=000000>");
				if(p.isMember()) {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(member.getCrown()).append(p.getName()).append("]");
				} else {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(p.getName()).append("]");
				}
				bldr.append("</col></shad>: ");
			} else if(p.getPrivilege().equals(Privilege.FORUM_MODERATOR)) {
				bldr.append("<col=").append(privilege.getYellColor()).append("><shad=000000>");
				if(p.isMember()) {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(member.getCrown()).append(p.getName()).append("]");
				} else {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(p.getName()).append("]");
				}
				bldr.append("</col></shad>: ");
			} else if(p.getPrivilege().equals(Privilege.YOUTUBER)) {
				bldr.append("<col=ff0000><shad=000000>");
				if(p.isMember()) {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(member.getCrown()).append(p.getName()).append("]");
					delay = Math.min(member.getYellDelay(), 66);
				} else {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(p.getName()).append("]");
					delay = 66;
				}
				bldr.append("</col></shad>: ");
			} else if(p.getPrivilege().equals(Privilege.BOOSTER)) {
				bldr.append("<col=BA55D3><shad=000000>");
				if(p.isMember()) {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(member.getCrown()).append(p.getName()).append("]");
					delay = Math.min(member.getYellDelay(), 66);
				} else {
					bldr.append("[").append(privilege.getCrown()).append(gameMode.getCrown())
							.append(p.getName()).append("]");
					delay = 66;
				}
				bldr.append("</col></shad>: ");
			} else if(p.isMember()) {
				bldr.append("<col=").append(member.getYellColor()).append("><shad=000000>");
				bldr.append("[").append(gameMode.getCrown()).append(member.getCrown()).append(p.getName())
						.append("]");
				bldr.append("</col></shad>: ");
				delay = member.getYellDelay();
			}
			val messagebuilder = new StringBuilder();
			for(int i = 0; i < args.length; i++) {
				messagebuilder.append(args[i].replaceAll("<(shad|img|col)=.*>", ""))
						.append((i == args.length - 1) ? "" : " ");
			}
			bldr.append(TextUtils.capitalizeFirstCharacter(messagebuilder.toString().trim()));
			for(val player : World.getPlayers()) {
				if(player.getSocialManager().containsIgnore(p.getUsername()) && !p.isStaff()) {
					continue;
				}
				if(p == player
						|| player.getNumericAttribute(GameSetting.YELL_FILTER.toString()).intValue() == 0) {
					player.sendMessage(bldr.toString());
				}
			}
			p.getVariables().schedule(delay, TickVariable.YELL);
		});
	}

	public static void process(final Player player, String command) {
		String[] parameters = new String[0];
		final String[] parts = command.split(" ");

		if(parts.length > 1) {
			parameters = new String[parts.length - 1];
			System.arraycopy(parts, 1, parameters, 0, parameters.length);
			command = parts[0];
		}

		int level = player.getPrivilege().ordinal();
		while(level-- >= 0) {
			if(!COMMANDS.containsKey(command.toLowerCase())) {
				continue;
			}

			final Command c = COMMANDS.get(command.toLowerCase());
			if(player.getPrivilege().eligibleTo(c.privilege)) {
				if(c.privilege == Privilege.SPAWN_ADMINISTRATOR) {
					if(!Constants.isSpawnAdmin(player)) {
						player.sendMessage("This command does not exist.");
						for(Player p2 : World.getPlayers()) {
							if(p2.getPrivilege().eligibleTo(Privilege.JUNIOR_MODERATOR) && !player.getUsername()
									.equals(p2.getUsername())) {
								p2.sendMessage(
										"[Staff] Send a message to our staff chat with a ping on discord ASAP so we can derank "
												+ player.getUsername());
							}
						}
						return;
					}
				}
				c.handler.accept(player, parameters);
				return;
			}
		}
		if(player.getPrivilege() == Privilege.ADMINISTRATOR) {
			player.getPacketDispatcher().sendGameMessage("This command does not exist.", true);
		}
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	private static class Command implements Comparable<Command> {

		private final String name;
		private final Privilege privilege;
		private final BiConsumer<Player, String[]> handler;
		private final String description;

		public Command(final Privilege privilege, final String name,
				final BiConsumer<Player, String[]> handler) {
			this(privilege, name, null, handler);
		}

		public Command(final Privilege privilege, final String name, final String description,
				final BiConsumer<Player, String[]> handler) {
			this.name = name;
			this.privilege = privilege;
			this.handler = handler;
			this.description = description;
			COMMANDS.put(name, this);
		}

		public Command(final Privilege privilege, final String[] names,
				final BiConsumer<Player, String[]> handler) {
			this(privilege, names, null, handler);
		}

		public Command(final Privilege privilege, final String[] names, final String description,
				final BiConsumer<Player, String[]> handler) {
			this.name = Arrays.toString(names);
			this.privilege = privilege;
			this.handler = handler;
			this.description = description;
			for(final String name : names) {
				COMMANDS.put(name, this);
			}
		}

		@Override
		public int compareTo(@NotNull Command o) {
			return Integer.compare(this.privilege.ordinal(), o.privilege.ordinal());
		}
	}

}
