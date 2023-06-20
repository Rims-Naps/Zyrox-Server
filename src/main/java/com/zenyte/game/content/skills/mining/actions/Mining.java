package com.zenyte.game.content.skills.mining.actions;

import com.zenyte.game.content.achievementdiary.diaries.*;
import com.zenyte.game.content.minigame.castlewars.CastlewarsRockPatch;
import com.zenyte.game.content.minigame.motherlode.OreVein;
import com.zenyte.game.content.minigame.motherlode.Paydirt;
import com.zenyte.game.content.minigame.motherlode.UpperMotherlodeArea;
import com.zenyte.game.content.skills.mining.MiningDefinitions.OreDefinitions;
import com.zenyte.game.content.skills.mining.MiningDefinitions.PickaxeDefinitions;
import com.zenyte.game.content.skills.mining.MiningDefinitions.ShapeDefinitions;
import com.zenyte.game.content.treasuretrails.ClueItem;
import com.zenyte.game.content.treasuretrails.clues.CharlieTask;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.npc.NPCObjectEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.plugins.dialogue.PlainChat;
import com.zenyte.plugins.itemonnpc.ItemOnBarricadeAction;
import com.zenyte.plugins.itemonobject.PotionOnCastlewarsRocks;
import lombok.val;
import lombok.var;

import java.util.Locale;

import static com.zenyte.game.content.skills.woodcutting.actions.Woodcutting.BURN_GFX;

/**
 * @author Noele | Nov 9, 2017 : 12:22:34 AM
 * see https://noeles.life || noele@zenyte.com
 */

public class Mining extends Action {
    @Override
    public boolean interruptedByCombat() {
        return false;
    }

    private final OreDefinitions ore;
    private WorldObject rock;
    private NPC npc;
    private PickaxeDefinitions tool;
    private Item pickaxe;
    private boolean pickaxeEquipment;
    private Container container;
    private int slotId;

    public static final Graphics ROCKFALL_EXPLOSION = new Graphics(305);
    public static final Projectile ROCKFALL_PROJECTILE = new Projectile(645, 255, 0, 0, 0, 25, 64, 5);

    public Mining(final WorldObject rock) {
        this.rock = rock;
        ore = OreDefinitions.getDef(rock.getId());
    }

    public Mining(final OreDefinitions ore, final NPC npc) {
        this.ore = ore;
        this.npc = npc;
    }

    @Override
    public boolean start() {
        if (!checkTool()) return false;
        if (!check()) {
            return false;
        }
        player.sendFilteredMessage("You swing your pick at the rock.");
        final int time = (ore.equals(OreDefinitions.ESSENCE)) ? 1 : tool.getMineTime();
        delay(time);
        return true;
    }

    public boolean success() {
        if (ore.equals(OreDefinitions.ESSENCE)) {
            return true;
        }
        assert ore.getSpeed() > 0;
        val ring = player.getRing();
        boolean celestial;
        if (ring == null) {
            celestial = false;
        } else {
            celestial = ring.getName().toLowerCase().startsWith("celestial");
        }
        int level = player.getSkills().getLevel(Skills.MINING) + (player.inArea("Mining Guild") ? 7 : 0) + (celestial ? 4 : 0);
        if (ore == OreDefinitions.GEM) {
            val amulet = player.getAmulet();
            if (amulet != null && amulet.getName().toLowerCase().contains("glory")) {
                level += 30;
            }
        }
        val advancedLevels = level - ore.getSpeed();
        if (ore == OreDefinitions.PAYDIRT) {
            return 25 + (level / 2.25F) > Utils.random(100);
        }
        val chance = Math.max(Math.min(Math.round(advancedLevels * 0.8F) + 20, 70), 4) * 2;
        return chance > Utils.random(100);
    }

    @Override
    public boolean process() {
        if (!check()) {
            return false;
        }

        val altAnim = this.ore == OreDefinitions.AMETHYST || this.ore == OreDefinitions.PAYDIRT || this.ore == OreDefinitions.CWARS_WALL;

        player.setAnimation(altAnim ? tool.getAlternateAnimation() : tool.getAnim());
        return checkObject();
    }

    private static final int randomGem() {
        val random = Utils.random(127);
        if (random < 60) {
            return 1625;
        } else if (random < 90) {
            return 1627;
        } else if (random < 105) {
            return 1629;
        } else if (random < 114) {
            return 1623;
        } else if (random < 119) {
            return 1621;
        } else if (random < 124) {
            return 1619;
        }
        return 1617;
    }

    @Override
    public int processWithDelay() {
        if (!success()) {
            return (ore.equals(OreDefinitions.ESSENCE)) ? 1 : tool.getMineTime();
        }

        if (ore == OreDefinitions.CWARS_WALL) {
            val wallData = CastlewarsRockPatch.getData(rock);

            if (wallData == null)
                return -1;

            player.sendMessage("You've collapsed the tunnel!");
            World.sendGraphics(ItemOnBarricadeAction.EXPLOSION, wallData.getPatch());
            World.spawnObject(wallData.getPatch());
            PotionOnCastlewarsRocks.processVarbits(rock, true);

            CharacterLoop.forEach(wallData.getPatch(), 1, Entity.class, entity -> {
                if (Utils.collides(wallData.getPatch().getX(), wallData.getPatch().getY(), 2, entity.getX(), entity.getY(), entity.getSize())) {
                    if (entity instanceof Player) {
                        entity.applyHit(new Hit(entity.getHitpoints(), HitType.REGULAR));
                    }
                }
            });

            return -1;
        }

        if (ore == OreDefinitions.CWARS_ROCKS) {
            val initial = rock.getId() == 4437;
            if(initial) {
                rock = new WorldObject(4438, rock.getType(), rock.getRotation(), rock);
                World.spawnObject(rock);
            } else {
                World.removeObject(rock);
                PotionOnCastlewarsRocks.processVarbits(rock, false);
            }

            return initial ? tool.getMineTime() : -1;
        }

        val skills = player.getSkills();
        if (ore == OreDefinitions.ROCKFALL) {
            skills.addXp(Skills.MINING, ore.getXp());
            World.removeObject(rock);
            WorldTasksManager.schedule(() -> {
                val elements = new int[] {-1, 1};
                World.sendProjectile(rock.transform(elements[Utils.random(elements.length - 1)], elements[Utils.random(elements.length - 1)], 0), rock, ROCKFALL_PROJECTILE);
                World.sendProjectile(rock.transform(elements[Utils.random(elements.length - 1)], elements[Utils.random(elements.length - 1)], 0), rock, ROCKFALL_PROJECTILE);
                WorldTasksManager.schedule(() -> {
                    CharacterLoop.forEach(rock, 1, Entity.class, entity -> {
                        if (Utils.collides(rock.getX(), rock.getY(), 1, entity.getX(), entity.getY(), entity.getSize())) {
                            if (entity instanceof Player) {
                                entity.applyHit(new Hit(Utils.random(1, 4), HitType.DEFAULT));
                            }
                            if (entity instanceof Player) {
                                entity.setRouteEvent(new ObjectEvent(((Player) entity), new ObjectStrategy(rock), null));
                            } else {
                                entity.setRouteEvent(new NPCObjectEvent(((NPC) entity), new ObjectStrategy(rock)));
                            }
                        }
                    });
                    World.sendGraphics(ROCKFALL_EXPLOSION, rock);
                    World.spawnObject(rock);
                });
            }, this.ore.getTime());
            return -1;
        }

        if (ore == OreDefinitions.PAYDIRT) {
            if (rock instanceof OreVein) {
                ((OreVein) rock).start();
            }
            if(!UpperMotherlodeArea.polygon.contains(rock) && Utils.random(0, 2) == 0) {
                val emptyId = rock.getId() + 4;
                val empty = new WorldObject(emptyId, rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane());
                World.spawnObject(empty);
                final int size = World.getPlayers().size();
                final float percentage = Math.min(size, 500) / 1000f;
                final int time = (int) (ore.getTime() - (ore.getTime() * percentage));
                WorldTasksManager.schedule(() -> World.spawnObject(rock), time);
            }
        }

        val inventory = player.getInventory();
        if (ore == OreDefinitions.VOLCANIC_ASH) {
            skills.addXp(Skills.MINING, ore.getXp());
            val level = skills.getLevel(Skills.MINING);
            int amount = level >= 97 ? 6 : level >= 82 ? 5 : level >= 67 ? 4 : level >= 52 ? 3 : level >= 37 ? 2 : 1;
            val ore = new Item(this.ore.getOre(), amount);
            player.sendFilteredMessage("You manage to mine some " + ore.getName().toLowerCase() + ".");
            inventory.addItem(ore);
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            if (Utils.random(8) == 0) {
                val empty = new WorldObject(ShapeDefinitions.getEmpty(rock.getId()), rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane());
                World.spawnObject(empty);
                WorldTasksManager.schedule(() -> World.spawnObject(rock), this.ore.getTime());
                return -1;
            }
            return tool.getMineTime();
        }
        if (ore == OreDefinitions.GEM) {
            skills.addXp(Skills.MINING, ore.getXp());
            val gem = new Item(randomGem());
            if (gem.getId() == ItemId.UNCUT_RED_TOPAZ) {
                player.getAchievementDiaries().update(KaramjaDiary.MINE_A_RED_TOPAZ);
            }
            player.sendFilteredMessage("You manage to mine some " + gem.getName().toLowerCase().replace("uncut ", "") + ".");
            inventory.addItem(gem);
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            val empty = new WorldObject(ShapeDefinitions.getEmpty(rock.getId()), rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane());
            World.spawnObject(empty);
            WorldTasksManager.schedule(() -> World.spawnObject(rock), ore.getTime());
            return -1;
        }
        val valid = player.getPerkManager().isValid(PerkWrapper.MASTER_MINER);
        var amount = player.getPerkManager().isValid(PerkWrapper.MASTER_MINER) && Utils.random(0, 100) <= 15 ? 2 : 1;
        if (ore.isExtraOre() && Utils.random(99) < 5 && SkillcapePerk.MINING.isEffective(player)) {
            amount++;
        }
        if (ore == OreDefinitions.TE_SALT || ore == OreDefinitions.EFH_SALT || ore == OreDefinitions.URT_SALT) {
            amount = Utils.random(2, 5);
        }
        val body = player.getEquipment().getId(EquipmentSlot.PLATE);
        if (body >= 13104 && body <= 13107) {
            val limit = body == 13104 ? OreDefinitions.COAL : body == 13105 ? OreDefinitions.MITHRIL : body == 13106 ? OreDefinitions.ADAMANTITE : OreDefinitions.AMETHYST;
            if (Utils.random(10) == 0 && ore.ordinal() <= limit.ordinal()) {
                amount++;
            }
        }
        if (celestialRingSuccess()) {
            amount++;
            player.sendFilteredMessage("Your ring gives you an extra ore.");
        }
        val deplete = ore.getDepletionRate() > 0 && Utils.random(ore.getDepletionRate() - 1) == 0  && !miningGloveSuccess();
        if (deplete && npc == null) {
            World.spawnObject(new WorldObject(ShapeDefinitions.getEmpty(rock.getId()), rock.getType(), rock.getRotation(), rock.getX(), rock.getY(), rock.getPlane()));
            WorldTasksManager.schedule(() -> World.spawnObject(rock), ore.getTime());
        }

        if (ore.equals(OreDefinitions.SANDSTONE)) {
            val type = Utils.random(3);
            val ore = 6971 + (type * 2);
            val experience = 30 + (type * 10);
            skills.addXp(Skills.MINING, experience);
            inventory.addOrDrop(new Item(ore, amount));
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            player.sendFilteredMessage("You manage to mine some sandstone.");
            if (valid) {
                player.getPerkManager().consume(PerkWrapper.MASTER_MINER);
            }
            return -1;
        } else if (ore.equals(OreDefinitions.GRANITE)) {
            val type = Utils.random(2);
            val ore = 6979 + (type * 2);
            val experience = type == 0 ? 50 : type == 1 ? 60 : 75;
            skills.addXp(Skills.MINING, experience);
            inventory.addOrDrop(new Item(ore, amount));
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            player.sendFilteredMessage("You manage to mine some granite.");
            if (valid) {
                player.getPerkManager().consume(PerkWrapper.MASTER_MINER);
            }
            return -1;
        }

        if (ore.equals(OreDefinitions.RUNITE_GOLEM_ROCKS)) {
            if (npc != null) {
                npc.finish();
            }
        }

        if (ore.equals(OreDefinitions.IRON)) {
            CharlieTask.MINE_IRON.progress(player);
        }

        val diaries = player.getAchievementDiaries();
        val daily = player.getDailyChallengeManager();
        if (ore.equals(OreDefinitions.ESSENCE)) {
            daily.update(SkillingChallenge.MINE_ESSENCE);
        } else if (ore.equals(OreDefinitions.CLAY)) {
            if (player.getX() >= 3399 && player.getX() <= 3424 && player.getY() >= 3152 && player.getY() <= 3169) {
                diaries.update(DesertDiary.MINE_CLAY);
            }
            daily.update(SkillingChallenge.MINE_CLAY);
        } else if (ore.equals(OreDefinitions.IRON)) {
            if(Utils.random(84) == 0) {
                inventory.addOrDrop(ItemId.UNIDENTIFIED_MINERALS,1);
            }
            diaries.update(WildernessDiary.MINE_IRON_ORE);
            diaries.update(VarrockDiary.MINE_IRON);
            diaries.update(LumbridgeDiary.MINE_IRON);
            diaries.update(WesternProvincesDiary.MINE_IRON_NEAR_PISCATORIS);
            diaries.update(KourendDiary.MINE_IRON_IN_MT_KARUULM);
            daily.update(SkillingChallenge.MINE_IRON_ORES);
        } else if (ore.equals(OreDefinitions.COAL)) {
            if(Utils.random(49) == 0) {
                inventory.addOrDrop(ItemId.UNIDENTIFIED_MINERALS,1);
            }
            diaries.update(KandarinDiary.MINE_COAL);
            diaries.update(FremennikDiary.MINE_COAL_IN_RELLEKKA);
        } else if (ore.equals(OreDefinitions.SILVER)) {
            diaries.update(FremennikDiary.CRAFT_A_TIARA, 0x1);
            daily.update(SkillingChallenge.MINE_SILVER_ORES);
        } else if (ore.equals(OreDefinitions.GOLD)) {
            diaries.update(FaladorDiary.MINE_GOLD_ORE);
            diaries.update(KaramjaDiary.MINE_GOLD);
            daily.update(SkillingChallenge.MINE_GOLD_ORES);
            //TODO: Convert the boundary to diary restriction on its own.
        } else if (ore.equals(OreDefinitions.MITHRIL)) {
            if(Utils.random(29) == 0) {
                inventory.addOrDrop(ItemId.UNIDENTIFIED_MINERALS,1);
            }
            diaries.update(WildernessDiary.MINE_MITHRIL_ORE);
            diaries.update(MorytaniaDiary.MINE_MITHRIL_IN_ABANDONED_MINE);
            daily.update(SkillingChallenge.MINE_MITHRIL_ORES);
            SherlockTask.MINE_MITHRIL_ORE.progress(player);
        }  else if (ore.equals(OreDefinitions.ADAMANTITE)) {
            if(Utils.random(19) == 0) {
                inventory.addOrDrop(ItemId.UNIDENTIFIED_MINERALS,1);
            }
            diaries.update(FremennikDiary.MINE_ADAMANTITE_ORE);
            diaries.update(WesternProvincesDiary.MINE_ADAMANTITE_IN_TIRANNWN);
        } else if (ore.equals(OreDefinitions.RUNITE) || ore.equals(OreDefinitions.RUNITE_GOLEM_ROCKS)) {
            if(Utils.random(14) == 0) {
                inventory.addOrDrop(ItemId.UNIDENTIFIED_MINERALS,1);
            }
            daily.update(SkillingChallenge.MINE_RUNITE_ORES);
        } else if (ore.equals(OreDefinitions.AMETHYST)) {
            if(Utils.random(14) == 0) {
                inventory.addOrDrop(ItemId.UNIDENTIFIED_MINERALS,1);
            }
            daily.update(SkillingChallenge.MINE_AMETHYST);
        } else if (ore.equals(OreDefinitions.LOVAKITE)) {
            diaries.update(KourendDiary.MINE_SOME_LOVAKITE);
        }
        skills.addXp(Skills.MINING, amount * ore.getXp());
        if (ore.getIncinerationExperience() > 0 && pickaxe.getCharges() > 0 && tool.equals(PickaxeDefinitions.INFERNAL) && Utils.random(2) == 0) {
            skills.addXp(Skills.SMITHING, ore.getIncinerationExperience());
            player.getChargesManager().removeCharges(pickaxe, 1, container, slotId);
            player.setAnimation(Animation.STOP);
            player.sendFilteredMessage("You manage to mine some " + ore.getName() + ".");
            player.sendSound(2725);
            player.setGraphics(BURN_GFX);
            ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            return ore.equals(OreDefinitions.ESSENCE) ? 1 : -1;
        } else {
            val essence = skills.getLevel(Skills.MINING) < 30 ? 1436 : 7936;
            if (ore.equals(OreDefinitions.CLAY)) {
                val bracelet = player.getEquipment().getItem(EquipmentSlot.HANDS);
                if (bracelet != null && bracelet.getId() == ItemId.BRACELET_OF_CLAY) {
                    player.getChargesManager().removeCharges(bracelet, 1, container, slotId);
                    inventory.addOrDrop(new Item(1761, amount));
                } else {
                    inventory.addItem(ore.getOre(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                }
                ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
            } else {
                if (ore.getOre() != -1) {
                    if (ore == OreDefinitions.PAYDIRT) {
                        val generated = Paydirt.generate(player);
                        generated.ifPresent(paydirt -> {
                            val item = new Item(ore.getOre(), 1);
                            item.setAttribute("paydirt ore id", paydirt.getId());
                            inventory.addItem(item).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                            player.sendFilteredMessage("You manage to mine some pay-dirt.");
                        });
                    } else {
                        inventory.addItem(ore.equals(OreDefinitions.ESSENCE) ? essence : ore.getOre(), amount).onFailure(remainder -> World.spawnFloorItem(remainder, player));
                        ClueItem.roll(player, this.ore.getBaseClueGeodeChance(), skills.getLevel(Skills.MINING), ClueItem::getClueGeode);
                    }
                }
            }
        }
        if (valid) {
            player.getPerkManager().consume(PerkWrapper.MASTER_MINER);
        }
        return ore.equals(OreDefinitions.ESSENCE) ? 1 : deplete ? -1 : tool.getMineTime();
    }

    @Override
    public void stop() {
        player.setAnimation(Animation.STOP);
    }

    private boolean check() {
        if(ore.equals(OreDefinitions.ROCKFALL))
            return checkLevel();

        return (checkLevel() && player.getInventory().checkSpace());
    }

    private boolean checkTool() {
        val axe = PickaxeDefinitions.get(player, true);
        if (!axe.isPresent()) {
            player.getDialogueManager().start(new PlainChat(player, "You need a pickaxe to mine this rock. You do not have a pickaxe which you have the Mining level to use."));
            return false;
        }
        val definitions = axe.get();
        this.pickaxeEquipment = definitions.getContainer() == player.getEquipment().getContainer();
        this.slotId = definitions.getSlot();
        this.tool = definitions.getDefinitions();
        this.container = definitions.getContainer();
        this.pickaxe = definitions.getItem();
        return true;
    }

    private boolean checkLevel() {
        if (player.getSkills().getLevel(Skills.MINING) < ore.getLevel()) {
            player.getDialogueManager().start(new PlainChat(player, "You need a Mining level of " + ore.getLevel() + " to mine this rock."));
            return false;
        }
        return true;
    }

    private boolean checkObject() {
        if (ore.equals(OreDefinitions.RUNITE_GOLEM_ROCKS)) {
            return npc != null && !npc.isFinished();
        }

        if (ore.equals(OreDefinitions.CWARS_ROCKS)) {
            return (World.getRegion(rock.getRegionId()).containsObject(4437, rock.getType(), rock) ||
                    World.getRegion(rock.getRegionId()).containsObject(4438, rock.getType(), rock));
        }

        return World.getRegion(rock.getRegionId()).containsObject(rock.getId(), rock.getType(), rock);
    }

    private boolean miningGloveSuccess() {
        val gloves = player.getEquipment().getId(EquipmentSlot.HANDS);
        if ((ore.equals(OreDefinitions.SILVER) || ore.equals(OreDefinitions.COAL) || ore.equals(OreDefinitions.GOLD)) && (gloves == 21343 || gloves == 21392)) {
            if (ore.equals(OreDefinitions.SILVER)) {
                if (Utils.random(1) == 0) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
                }
            }
            if (ore.equals(OreDefinitions.COAL)) {
                if (Utils.random(4) <2) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
                }
            }
            if (ore.equals(OreDefinitions.GOLD)) {
                if (Utils.random(2) == 0) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
                }
            }
        }
        if ((ore.equals(OreDefinitions.MITHRIL) || ore.equals(OreDefinitions.ADAMANTITE) || ore.equals(OreDefinitions.RUNITE)) && (gloves == 21345 || gloves == 21392)) {
            if (ore.equals(OreDefinitions.MITHRIL)) {
                if (Utils.random(3) == 0) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
                }
            }
            if (ore.equals(OreDefinitions.ADAMANTITE)) {
                if (Utils.random(5) == 0) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
                }
            }
            if (ore.equals(OreDefinitions.RUNITE)) {
                if (Utils.random(7) == 0) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
                }
            }
        }
        if (ore.equals(OreDefinitions.AMETHYST) && gloves == 21392) {
            if (Utils.random(3) == 0) {
                    player.sendFilteredMessage("Your gloves save the ore from depleting.");
                    return true;
            }
        }
        return false;
    }

    private boolean celestialRingSuccess() {
        val ring = player.getRing();
        if (ring == null) {
            return false;
        }
        if (!ring.getName().toLowerCase().startsWith("celestial")) {
            return false;
        }
        if (player.getInventory().getFreeSlots() < 2) {
            return false;
        }
        if (Utils.random(9) == 0) {
            return true;
        } else {
            return false;
        }
    }

}
