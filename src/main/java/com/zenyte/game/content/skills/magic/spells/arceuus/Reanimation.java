package com.zenyte.game.content.skills.magic.spells.arceuus;

import com.google.common.base.CaseFormat;
import com.zenyte.game.content.skills.magic.SpellState;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.content.skills.magic.spells.FloorItemSpell;
import com.zenyte.game.content.skills.magic.spells.ItemSpell;
import com.zenyte.game.content.skills.magic.spells.lunar.SpellbookSwap;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.game.world.region.RSPolygon;
import mgi.Indice;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.function.IntPredicate;

/**
 * @author Kris | 13. juuli 2018 : 22:40:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum Reanimation implements ItemSpell, FloorItemSpell {

	REANIMATE_GOBLIN(6, 130, 13448, 7018, new NPCEntry("Goblin", 30, id -> NPCDefinitions.getOrThrow(id).getCombatLevel() >= 11), new NPCEntry("Goblin", 35)),
	REANIMATE_MONKEY(14, 182, 13451, 7019, new NPCEntry("Monkey", 35)),
	REANIMATE_IMP(24, 286, 13454, 7020, new NPCEntry("Imp", 25, i -> i < 15000)),
	REANIMATE_MINOTAUR(32, 364, 13457, 7021, new NPCEntry("Minotaur", 50)),//TODO: lowercase + contains, random -1
	REANIMATE_SCORPION(38, 454, 13460, 7022, new NPCEntry("Scorpion", 25), new NPCEntry("Giant lobster", 25), new NPCEntry("Scorpia", 18)),
	REANIMATE_BEAR(42, 480, 13463, 7023, new NPCEntry("Black bear", 25), new NPCEntry("Grizzly bear", 25), new NPCEntry("Bear Cub", 25), new NPCEntry("Grizzly bear cub", 25)),
	REANIMATE_UNICORN(44, 494, 13466, 7024, new NPCEntry("Unicorn", 35)),
	REANIMATE_DOG(52, 520, 13469, 7025, new NPCEntry("Guard dog", 25), new NPCEntry("Wild dog", 25)),
	REANIMATE_CHAOS_DRUID(60, 584, 13472, 7026, new NPCEntry("Elder chaos druid", 20), new NPCEntry("Chaos druid warrior", 25), new NPCEntry("Chaos druid", 20)),
	REANIMATE_GIANT(74, 650, 13475, 7027, new NPCEntry("Obor", 1), new NPCEntry("Fire giant", 20), new NPCEntry("Ice giant", 21), new NPCEntry("Moss giant", 24), new NPCEntry("Hill " +
            "giant", 25), new NPCEntry("Black knight titan", 10)),
	REANIMATE_OGRE(80, 716, 13478, 7028, new NPCEntry("Ogre", 30)),
	REANIMATE_ELF(86, 754, 13481, 7029, new NPCEntry("Elf Warrior", 40, i -> NPCDefinitions.getOrThrow(i).getCombatLevel() > 90), new NPCEntry("Elf warrior", 50)),
	REANIMATE_TROLL(93, 780, 13484, 7030, new NPCEntry("Troll spectator", 45), new NPCEntry("Mountain troll", 45), new NPCEntry("Thrower troll", 45),
            new NPCEntry("Stick", 28), new NPCEntry("Kraka", 28), new NPCEntry("Pee Hat", 28), new NPCEntry("Troll general", 28),
            new NPCEntry("Ice troll", 20)),
	REANIMATE_HORROR(104, 832, 13487, 7031, new NPCEntry("Jungle horror", 40), new NPCEntry("Cave horror", 30)),
	REANIMATE_KALPHITE(114, 884, 13490, 7032, new NPCEntry("Kalphite queen", 20), new NPCEntry("Kalphite guardian", 35), new NPCEntry("Kalphite soldier", 90), new NPCEntry("Kalphite " +
            "worker", 250)),
	REANIMATE_DAGANNOTH(124, 936, 13493, 7033, new NPCEntry("Dagannoth Prime", 20), new NPCEntry("Dagannoth supreme", 20), new NPCEntry("Dagannoth prime", 20), new NPCEntry(
	        "Dagannoth", 40, i -> {
	            val level = NPCDefinitions.getOrThrow(i).getCombatLevel();
	            return level == 74 || level == 92 || level == 88;
    }), new NPCEntry("Dagannoth", 35)),
	REANIMATE_BLOODVELD(130, 1040, 13496, 7034, new NPCEntry("Insatiable bloodveld", 1), new NPCEntry("Insatiable mutated bloodveld", 1),
            new NPCEntry("Bloodveld", 35)),
	REANIMATE_TZHAAR(138, 1104, 13499, 7035, new NPCEntry("Tzhaar-ket", 35)),
	REANIMATE_DEMON(144, 1170, 13502, 7036, new NPCEntry("Lesser demon", 50), new NPCEntry("Greater demon", 40), new NPCEntry("Black demon", 35)),
	REANIMATE_AVIANSIE(156, 1234, 13505, 7037, new NPCEntry("Aviansie", 20, i ->
            i == 3181 || i == 3179 || i == 3177 || i == 3183 || i == 3182 || i == 3175 || i == 3171 || i == 3169), new NPCEntry("Aviansie", 35)),
	REANIMATE_ABYSSAL_CREATURE(170, 1300, 13508, 7038, new NPCEntry("Greater abyssal demon", 1), new NPCEntry("Abyssal demon", 25), new NPCEntry("Abyssal walker", 40)),
	REANIMATE_DRAGON(186, 1560, 13511, 7039,
            new NPCEntry("Brutal black dragon", 20), new NPCEntry("Brutal red dragon", 20), new NPCEntry("Brutal blue dragon", 20),
            new NPCEntry("Brutal green dragon", 28), new NPCEntry("Black dragon", 35, id -> id != 6502), new NPCEntry("Green dragon", 35),
            new NPCEntry("Red dragon", 40), new NPCEntry("Blue dragon", 50));

	private static final Graphics CAST_GFX = new Graphics(1288);
	private static final Graphics IMPACT_GFX = new Graphics(1290);
	private static final Animation ANIMATION = new Animation(7198);

	private static final Projectile PROJECTILE = new Projectile(1289, 30, 0, 50, 15, 30, 0, 5);

	private static final Reanimation[] VALUES = values();

	private static final IntOpenHashSet ENSOULED_HEADS = new IntOpenHashSet(VALUES.length);

	static {
		for (val value : VALUES) {
			ENSOULED_HEADS.add(value.itemId);
		}
	}

	public static final class ReanimatedNPCProcessor extends DropProcessor {

        @Override
        public void attach() {
            loop : for (val i : allIds) {
                val definitions = NPCDefinitions.get(i);
                if (definitions == null) {
                    continue;
                }
                val name = definitions.getName().toLowerCase();
                for (val value : VALUES) {
                    for (val entry : value.entries) {
                        if (name.contains(entry.name) && (entry.predicate == null || entry.predicate.test(i))) {
                            this.appendDrop(new DisplayedDrop(value.itemId, 1, 1, entry.rate, (player, npcId) -> npcId == i, i));
                            continue loop;
                        }
                    }
                }
            }
        }

        @Override
        public void onDeath(final NPC npc, final Player killer) {
            val name = npc.getDefinitions().getName().toLowerCase();
            for (val value : VALUES) {
                for (val entry : value.entries) {
                    if (name.contains(entry.name) && (entry.predicate == null || entry.predicate.test(npc.getId()))) {
                        if (random(entry.rate) == 0) {
                            val ensouledHead = new Item(value.itemId);
                            ensouledHead.setAttribute("ensouled head drop time", Utils.currentTimeMillis());
                            ensouledHead.setAttribute("Tradability", Boolean.TRUE);
                            npc.dropItem(killer, ensouledHead);
                        }
                        return;
                    }
                }
            }
        }

        @Override
        public int[] ids() {
            val set = new IntOpenHashSet();
            loop : for (int i = 0; i < Utils.getIndiceSize(Indice.NPC_DEFINITIONS); i++) {
                val definitions = NPCDefinitions.get(i);
                if (definitions == null) {
                    continue;
                }
                val name = definitions.getName().toLowerCase();
                if (name.contains("Reanimated")) {
                    continue;
                }
                for (val value : VALUES) {
                    for (val entry : value.entries) {
                        if (name.contains(entry.name) && (entry.predicate == null || entry.predicate.test(i))) {
                            set.add(i);
                            continue loop;
                        }
                    }
                }
            }
            return set.toIntArray();
        }
    }

	private static final RSPolygon DARK_ALTAR_PRESENCE = new RSPolygon(new int[][] { { 1636, 3887 }, { 1638, 3870 }, { 1645, 3877 },
			{ 1655, 3877 }, { 1657, 3875 }, { 1657, 3871 }, { 1655, 3869 }, { 1653, 3869 }, { 1650, 3866 }, { 1650, 3860 }, { 1654, 3856 },
			{ 1667, 3856 }, { 1670, 3853 }, { 1678, 3853 }, { 1680, 3851 }, { 1681, 3851 }, { 1690, 3842 }, { 1698, 3842 }, { 1699, 3843 },
			{ 1700, 3843 }, { 1701, 3844 }, { 1703, 3844 }, { 1704, 3845 }, { 1709, 3845 }, { 1711, 3847 }, { 1743, 3848 }, { 1743, 3857 },
			{ 1747, 3867 }, { 1743, 3871 }, { 1741, 3871 }, { 1742, 3874 }, { 1728, 3904 }, { 1708, 3904 }, { 1702, 3898 }, { 1702, 3896 },
			{ 1696, 3896 }, { 1696, 3891 }, { 1694, 3893 }, { 1691, 3893 }, { 1689, 3891 }, { 1689, 3887 }, { 1688, 3887 }, { 1688, 3894 },
			{ 1682, 3894 }, { 1682, 3887 }, { 1681, 3887 }, { 1681, 3890 }, { 1679, 3892 }, { 1675, 3892 }, { 1675, 3897 }, { 1669, 3897 },
			{ 1669, 3890 }, { 1667, 3890 }, { 1657, 3900 }, { 1649, 3900 } }, 0);

	@Getter
	private final double magicExperience, prayerExperience;
	@Getter
	private final int itemId, npcId;
	@Getter private final NPCEntry[] entries;

	Reanimation(final double magicExperience, final double prayerExperience, final int itemId, final int npcId, final NPCEntry... entries) {
		this.magicExperience = magicExperience;
		this.prayerExperience = prayerExperience;
		this.itemId = itemId;
		this.npcId = npcId;
		this.entries = entries;
	}

	private static final class NPCEntry {
	    private final String name;
	    private final int rate;
	    private IntPredicate predicate;

	    NPCEntry(final String name, final int rate) {
	        this(name, rate, null);
        }

        NPCEntry(final String name, final int rate, final IntPredicate predicate) {
	        this.name = name.toLowerCase();
	        this.rate = rate;
	        this.predicate = predicate;
        }
    }

	@Override
	public int getDelay() {
		return 1000;
	}

	@Override
	public boolean spellEffect(final Player player, final Item item, final int slot) {
		if (item.getId() != itemId) {
		    player.getDialogueManager().start(new ItemChat(player, new Item(itemId), "This spell cannot reanimated that item.<br>" +
                    "Its intended target is: " + Colour.RS_RED.wrap(ItemDefinitions.getOrThrow(itemId).getName()) + "."));
			return false;
		}
		val definitions = NPCDefinitions.get(npcId);
		if (definitions == null) {
			return false;
		}

		if (player.getTemporaryAttributes().containsKey("reanimation creature")) {
            player.sendMessage("You should finish off your last one first.");
		    return false;
        }

		val size = definitions.getSize();
		val pos = new Location(player.getLocation());

        if (!DARK_ALTAR_PRESENCE.contains(player.getLocation())) {
            player.getDialogueManager().start(new ItemChat(player, new Item(itemId), "That creature cannot be reanimated here. The power of the crystals by the Dark Altar " +
                    "will increase the potency of the spell."));
            return false;
        }

        val square = Utils.findEmptySquare(pos, size + 6, size,
                Optional.empty()).orElse(null);
		if (square == null) {
			player.sendMessage("The creature wouldn't have room to re-animate there.");
			return false;
		}

		player.setRouteEvent(new TileEvent(player, new TileStrategy(square, 1), () -> {

            val state = new SpellState(player, getLevel(), getRunes());
            if (!state.check()) {
                return;
            }
            player.getInterfaceHandler().closeInterfaces();
            player.setLunarDelay(getDelay());
            state.remove();
            SpellbookSwap.checkSpellbook(player);

			addXp(player, magicExperience);
			player.lock();
			player.setAnimation(ANIMATION);
			player.setGraphics(CAST_GFX);

			val tile = player.getLocation().transform(Direction.getMovementDirection(Utils.getMoveDirection(square.getX() - player.getX(), square.getY() - player.getY())), 1);
			player.setFaceLocation(tile);
			player.getInventory().set(slot, null);
			World.spawnFloorItem(item, player, tile);
			World.sendProjectile(player, tile, PROJECTILE);
			WorldTasksManager.schedule(() -> World.destroyFloorItem(World.getFloorItem(item.getId(), tile, player)),
					PROJECTILE.getTime(player, tile) + 1);
			World.scheduleProjectile(player, tile, PROJECTILE).schedule(() -> {
				World.sendGraphics(IMPACT_GFX, tile);
				WorldTasksManager.schedule(() -> {
					player.unlock();
					val npc = new ReanimatedNPC(Reanimation.this, player, square).spawn();
					npc.freeze(1);
					npc.resetWalkSteps();
				}, 5);

			});
		}));
		return false;
	}

	@Override
	public String getSpellName() {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, toString().toLowerCase()).replaceAll("_", " ");
	}

	private static final class ReanimatedNPC extends NPC {

		ReanimatedNPC(final Reanimation reanimation, final Player owner, final Location tile) {
			super(reanimation.getNpcId(), tile, true);
			this.reanimation = reanimation;
			this.owner = owner;
            owner.getTemporaryAttributes().put("reanimation creature", true);
            combat.forceTarget(owner);
		}

		private final Reanimation reanimation;
		private final Player owner;
		private int ticks = 50;

		@Override
		public void processNPC() {
		    super.processNPC();
			if (owner.isFinished() || !owner.getLocation().withinDistance(getLocation(), 25)) {
				finish();
				return;
			}
			if (!isUnderCombat()) {
				if (--ticks <= 0) {
					finish();
                }
			    return;
			}
			ticks = 50;
		}

		@Override
		public void sendDeath() {
			super.sendDeath();
			owner.getSkills().addXp(Skills.PRAYER, reanimation.getPrayerExperience());
			if (reanimation == REANIMATE_ABYSSAL_CREATURE) {
                SherlockTask.KILL_REANIMATED_ABYSSAL.progress(owner);
            }
		}

		@Override
        public void finish() {
		    super.finish();
            owner.getTemporaryAttributes().remove("reanimation creature");
        }

		@Override
		public boolean canAttack(final Player source) {
			if (source != owner) {
				source.sendMessage("You cannot attack that creature.");
				return false;
			}
			return super.canAttack(source);
		}

		@Override
		public List<Entity> getPossibleTargets(final EntityType type) {
			if (!possibleTargets.isEmpty()) {
				possibleTargets.clear();
			}
			possibleTargets.add(owner);
			return possibleTargets;
		}

	}

	@Override
	public Spellbook getSpellbook() {
		return Spellbook.ARCEUUS;
	}

	@Override
	public boolean spellEffect(final Player player, final FloorItem item) {
        if (item.getId() != itemId) {
            player.getDialogueManager().start(new ItemChat(player, new Item(itemId), "This spell cannot reanimated that item.<br>" +
                    "Its intended target is: " + Colour.RS_RED.wrap(ItemDefinitions.getOrThrow(itemId).getName()) + "."));
            return false;
        }
		val definitions = NPCDefinitions.get(npcId);
		if (definitions == null) {
			return false;
		}

        if (player.getTemporaryAttributes().containsKey("reanimation creature")) {
            player.sendMessage("You should finish off your last one first.");
            return false;
        }

		player.setRouteEvent(new TileEvent(player, new TileStrategy(item.getLocation(), 1), () -> {
			if (World.getFloorItem(item.getId(), item.getLocation(), player) == null) {
				player.sendMessage("Too late - It's gone!");
				return;
			}

			val size = definitions.getSize();
			val tile = new Location(item.getLocation());

			val square = World.findEmptyNPCSquare(tile, size);
			val receiveTime = item.getNumericAttribute("ensouled head drop time").longValue();

            if (receiveTime < (Utils.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1))) {
                if (!DARK_ALTAR_PRESENCE.contains(player.getLocation())) {
                    player.getDialogueManager().start(new ItemChat(player, new Item(itemId), "That creature cannot be reanimated here. The power of the crystals by the Dark Altar " +
                            "will increase the potency of the spell."));
                    return;
                }
            }
			if (square == null) {
				player.sendMessage("The creature wouldn't have room to re-animate there.");
				return;
			}
			val state = new SpellState(player, getLevel(), getRunes());
			if (!state.check()) {
				return;
			}
			player.setLunarDelay(getDelay());
			state.remove();
			SpellbookSwap.checkSpellbook(player);
			World.destroyFloorItem(item);
			World.spawnFloorItem(item, tile, player, 200, 100);
			player.getSkills().addXp(Skills.MAGIC, magicExperience);
			player.lock();
			player.setAnimation(ANIMATION);
			player.setGraphics(CAST_GFX);
			player.setFaceLocation(tile);
			World.sendProjectile(player, tile, PROJECTILE);
			WorldTasksManager.schedule(() -> World.destroyFloorItem(World.getFloorItem(item.getId(), tile, player)),
					PROJECTILE.getTime(player, tile) + 1);
			World.scheduleProjectile(player, tile, PROJECTILE).schedule(() -> {
				World.sendGraphics(IMPACT_GFX, tile);
				WorldTasksManager.schedule(() -> {
					player.unlock();
					if (!World.isFloorFree(square, size)) {
						return;
					}
					new ReanimatedNPC(Reanimation.this, player, square).spawn();
				}, 5);
			});
		}));
		return false;
	}

}
