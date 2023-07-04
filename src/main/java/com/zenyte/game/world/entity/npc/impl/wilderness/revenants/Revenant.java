package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.action.combat.magic.spelleffect.BindEffect;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.CharacterLoop;
import kotlin.ranges.IntRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;


/**
 * @author Tommeh | 7 aug. 2018 | 13:25:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class Revenant extends NPC implements CombatScript, Spawnable {

	public Revenant(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
		if (isAbstractNPC())
		    return;
		this.aggressionDistance = attackDistance = 8;
	}

	private int healedAmount;

	@Override
    protected void updateCombatDefinitions() {
        super.updateCombatDefinitions();
        this.getCombatDefinitions().setAttackStyle(AttackType.MAGIC);
    }

    @Override
    public void onFinish(final Entity source) {
	    super.onFinish(source);
	    healedAmount = 0;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

	private static final Graphics HEAL_GFX = new Graphics(1221);
	private static final Graphics MAGIC_HIT_GFX = new Graphics(1454, 0, 92);
	private static final Item BRACELET_OF_ETHEREUM = new Item(21816);

	@Override
	public int attack(final Entity target) {
		if (!(target instanceof Player)) {
			return 0;
		}
		val constants = RevenantConstants.REVENANTS.get(getId());
		boolean heal = Utils.random(3) == 0 && getHitpoints() <= getMaxHitpoints() / 2;
		if (heal && healedAmount < 300) {
			setGraphics(HEAL_GFX);
			int amount = getMaxHitpoints() / 4;
			if ((amount + healedAmount) > 300) {
			    amount = 300 - healedAmount;
            }
			healedAmount += amount;
			setHitpoints(getHitpoints() + (amount));
		} else {
			val player = (Player) target;
			val style = player.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC) ? "Ranged" : "Magic";
			getCombatDefinitions().setAttackStyle(style);
			if (style.equals("Magic")) {
				val projectile = new Projectile(1415, constants.getStartHeight(), 25, constants.getDelay(), 15, 15, 0, 5);
				val freezeDelay = player.getNumericTemporaryAttribute("revenant_freeze").longValue();
				setAnimation(getCombatDefinitions().getAttackAnim());
				WorldTasksManager.schedule(() -> {
					if (Utils.random(8) == 0 && freezeDelay < Utils.currentTimeMillis()) {
						player.setGraphics(CombatSpell.ICE_BARRAGE.getHitGfx());
						player.getTemporaryAttributes().put("revenant_freeze", Utils.currentTimeMillis() + 20000);
						val hit = getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MAGIC, target);
						new BindEffect(7).spellEffect(null, target, hit);
						delayHit(-1, target, new Hit(this, hit, HitType.MAGIC));
					} else {
                        CharacterLoop.forEach(target.getLocation(), 0, Player.class, p -> {
                            if (p.isDead()) {
                                return;
                            }
                            p.setGraphics(MAGIC_HIT_GFX);
                            delayHit(this, -1, p, new Hit(this, getRandomMaxHit(this,
                                    getCombatDefinitions().getMaxHit(), MAGIC, p), HitType.MAGIC));
                        });
					}
				}, World.sendProjectile(getFaceLocation(player), player, projectile));
			} else {
				val projectile = new Projectile(206, constants.getStartHeight(), 25, constants.getDelay(), 15, 15, 0, 5);
				setAnimation(getCombatDefinitions().getAttackAnim());
				delayHit(this, World.sendProjectile(this, player, projectile), player, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
			}
		}
		return getCombatDefinitions().getAttackSpeed();
	}
	
	@Override
	public void handleOutgoingHit(final Entity target, final Hit hit) {
		if (target instanceof Player) {
			val player = (Player) target;
			val bracelet = player.getEquipment().getItem(EquipmentSlot.HANDS);
			if (bracelet != null && bracelet.getId() == BRACELET_OF_ETHEREUM.getId() && bracelet.getCharges() > 0) {
				hit.setDamage(0);
				player.getChargesManager().removeCharges(bracelet, 1, player.getEquipment().getContainer(),
                        EquipmentSlot.HANDS.getSlot());
			} else {
				super.handleOutgoingHit(target, hit);
				return;
			}
		}
		super.handleOutgoingHit(target, hit);
	}

	@Override
	protected boolean isAcceptableTarget(final Entity entity) {
		if (entity instanceof Player) {
			val player = (Player) entity;
			val id = player.getEquipment().getItem(EquipmentSlot.HANDS) == null ? -1 : player.getEquipment().getItem(EquipmentSlot.HANDS).getId();
			return id != 21816;
		}
		return true;
	}

	@Override
    protected void drop(final Location tile) {
        val killer = getDropRecipient();
        if (killer == null) {
            return;
        }
        onDrop(killer);
        val processors = DropProcessorLoader.get(id);
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }

        val level = getCombatLevel();
        val clampedLevel = Math.max(1, Math.min(144, level));
        val chanceA = 2200 / ((int) Math.sqrt(clampedLevel));
        val chanceB = 15 + ((int) Math.pow(level + 60F, 2) / 200);
        val a = Utils.random(chanceA - 1);

        int amount = Utils.random(1, Math.max(1, (int) Math.sqrt(level * 3)));
        if (killer.getNumericAttribute("ethereum absorption").intValue() == 1) {
            val bracelet = killer.getEquipment().getId(EquipmentSlot.HANDS);
            if (bracelet == 21816 || bracelet == 21817) {
                val braceletItem = killer.getGloves();
                if (braceletItem.getCharges() + amount > 16000) {
                    val amt = 16000 - braceletItem.getCharges();
                    amount -= amt;
                    braceletItem.setCharges(braceletItem.getCharges() + amt);
                } else {
                    braceletItem.setCharges(braceletItem.getCharges() + amount);
                    amount = 0;
                }
                if (bracelet == 21817) {
                    braceletItem.setId(21816);
                    killer.getEquipment().refresh(EquipmentSlot.HANDS.getSlot());
                }
            }
        }

        if (amount > 0) {
            dropItem(killer, new Item(ItemId.REVENANT_ETHER, amount));
        }

        if (a == 0) {
            dropItem(killer, GoodRevenantDrop.get(killer), tile, false);
        } else if (a < (chanceB + 1)) {
            dropItem(killer, MediocreReventantDrop.get(), tile, false);
        } else {
            dropItem(killer, new Item(ItemId.COINS_995, Utils.random(50, 500)), tile, true);
        }
    }

    @Override
    protected void sendNotifications(final Player player) {
        player.getNotificationSettings().increaseKill("Revenant");
    }

	@Override
	public boolean validate(final int id, final String name) {
		return id == 7881 || id >= 7931 && id <= 7940;
	}

	@AllArgsConstructor
	public enum GoodRevenantDrop {
	    REVENANT_WEAPON(new IntRange(0, 0), new ImmutableItem(22557, 1, 1)) {
	        @Override
            public final ImmutableItem getItem() {
	            final int chance = Utils.random(4);
	            switch(chance) {
                    case 0:
                        return new ImmutableItem(22542, 1, 1);
                    case 1:
                        return new ImmutableItem(22547, 1, 1);
                    case 2:
                        return new ImmutableItem(22552, 1, 1);
                    default:
                        return super.item;
	            }
            }
        },
        ANCIENT_RELIC(new IntRange(1, 1), new ImmutableItem(22305, 1, 1)),
        ANCIENT_EFFIGY(new IntRange(2, 2), new ImmutableItem(22302, 1, 1)),
        ANCIENT_MEDALLION(new IntRange(3, 4), new ImmutableItem(22299, 1, 1)),
        ANCIENT_STATUETTE(new IntRange(5, 8), new ImmutableItem(21813, 1, 1)),
        MAGIC_SEEDS(new IntRange(9, 12), new ImmutableItem(5316, 5, 9)),
        ANCIENT_CRYSTAL(new IntRange(13, 15), new ImmutableItem(21804, 1, 1)),
        ANCIENT_TOTEM(new IntRange(16, 20), new ImmutableItem(21810, 1, 1)),
        ANCIENT_EMBLEM(new IntRange(21, 26), new ImmutableItem(21807, 1, 1)),
        DRAGON_MED_HELM(new IntRange(27, 39), new ImmutableItem(1149, 1, 1));

	    @Getter private final IntRange range;
	    @Getter private final ImmutableItem item;

	    private static final GoodRevenantDrop[] values = values();

	    public static final Item get(final Player player) {
	        val random = Utils.random(player.getVariables().isSkulled() ? 13 : 39);
            for (val value : values) {
                if (random >= value.range.getFirst() && random <= value.range.getLast()) {
                    val item = value.getItem();
                    return new Item(item.getId(), Utils.random(item.getMinAmount(), item.getMaxAmount()));
                }
            }
	        throw new IllegalStateException();
        }
    }

    @AllArgsConstructor
    @Getter
	public enum MediocreReventantDrop {

	    DRAGON_PLATELEGS(1, new ImmutableItem(4087)),
        DRAGON_PLATESKIRT(1, new ImmutableItem(4585)),
        RUNE_FULL_HELM(2, new ImmutableItem(1163)),
        RUNE_PLATEBODY(2, new ImmutableItem(1127)),
        RUNE_PLATELEGS(2, new ImmutableItem(1079)),
        RUNE_KITESHIELD(2, new ImmutableItem(1201)),
        RUNE_WARHAMMER(2, new ImmutableItem(1347)),
        DRAGON_LONGSWORD(1, new ImmutableItem(1305)),
        DRAGON_DAGGER(1, new ImmutableItem(1215)),
        SUPER_RESTORES(4, new ImmutableItem(3025, 5, 7)),
        ONYX_TIPS(4, new ImmutableItem(9194, 5, 10)),
        DRAGONSTONE_TIPS(3, new ImmutableItem(9193, 40, 70)),
        DRAGONSTONE(1, new ImmutableItem(1632, 5, 7)),
        DEATH_RUNES(3, new ImmutableItem(560, 80, 200)),
        BLOOD_RUNES(4, new ImmutableItem(565, 80, 200)),
        COINS(2, new ImmutableItem(995, 100_000, 250_000)),
        LAW_RUNES(4, new ImmutableItem(563, 80, 120)),
        RUNITE_ORES(5, new ImmutableItem(452, 3, 7)),
        ADAMANT_BARS(5, new ImmutableItem(2362, 8, 20)),
        COAL(3, new ImmutableItem(454, 50, 100)),
        BATTLESTAVES(5, new ImmutableItem(1392, 10, 30)),
        BLACK_DRAGONHIDE(4, new ImmutableItem(1748, 10, 15)),
        MAHOGANY_PLANKS(3, new ImmutableItem(8783, 15, 25)),
        MAGIC_LOGS(2, new ImmutableItem(1514, 50, 100)),
        YEW_LOGS(3, new ImmutableItem(1516, 75, 140)),
        MANTA_RAYS(3, new ImmutableItem(392, 45, 75)),
        RUNE_BARS(5, new ImmutableItem(2364, 3, 10)),
        TELEPORT_SCROLL(5, new ImmutableItem(21802, 1, 3));

        private final int weight;
        private final ImmutableItem item;

        private static final MediocreReventantDrop[] values = values();

        public static final Item get() {
            val random = Utils.random(95);
            var roll = 0;

            for (val drop : values) {
                if ((roll += drop.weight) >= random) {
                    return new Item(drop.item.getId(), Utils.random(drop.item.getMinAmount(),
                            drop.item.getMaxAmount()));
                }
            }
            return new Item(ItemId.BRACELET_OF_ETHEREUM_UNCHARGED);
        }
    }

}
