package com.zenyte.game.content.skills.farming;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.SkillcapePerk;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import com.zenyte.processor.Listener;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.zenyte.game.content.skills.farming.FarmingPatch.*;
import static com.zenyte.game.content.skills.farming.PatchFlag.*;
import static com.zenyte.game.content.skills.farming.PatchState.*;

/**
 * @author Kris | 03/02/2019 02:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@SuppressWarnings("unchecked cast")
public final class FarmingSpot {

    public FarmingSpot(final EnumMap<FarmingAttribute, Object> map) {
        this.map = map;
    }

    FarmingSpot(final Player player, final FarmingPatch patch) {
        this(new EnumMap<>(FarmingAttribute.class));
        this.player = player;
        this.map.put(FarmingAttribute.PRODUCT, FarmingProduct.WEEDS);
        this.map.put(FarmingAttribute.FLAGS, EnumSet.noneOf(PatchFlag.class));
        setPatch(patch);
        setTime(0);
        setValue(0);
        setLives((player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER) && this.getPatch().getType() == PatchType.HERB_PATCH) || getPatch().getType() == PatchType.BUSH_PATCH ? 4 : 3);
        resetTimer();
        if (patch.getType() == PatchType.COMPOST_BIN) {
            setTime(-1);
        }
    }

    @Setter private transient Player player;
    @Getter private final Map<FarmingAttribute, Object> map;

    public EnumSet<PatchFlag> getFlags() {
        return (EnumSet<PatchFlag>) map.get(FarmingAttribute.FLAGS);
    }

    public int getValue() {
        return (int) map.get(FarmingAttribute.VALUE);
    }

    public void setValue(final int value) {
        assert !(value < 0 || value > 255) : "Invalid varbit value: " + value;
        this.map.put(FarmingAttribute.VALUE, value);
    }

    public int getLives() {
        return (int) map.get(FarmingAttribute.LIVES);
    }

    private void setLives(final int lives) {
        this.map.put(FarmingAttribute.LIVES, lives);
    }

    public long getTime() {
        return (long) map.get(FarmingAttribute.TIME);
    }

    public void setTime(final long time) {
        this.map.put(FarmingAttribute.TIME, time);
    }

    @NotNull
    public FarmingPatch getPatch() {
        return (FarmingPatch) map.get(FarmingAttribute.PATCH);
    }

    public void setPatch(final FarmingPatch patch) {
        this.map.put(FarmingAttribute.PATCH, patch);
    }

    @NotNull
    public FarmingProduct getProduct() {
        return (FarmingProduct) map.get(FarmingAttribute.PRODUCT);
    }

    @NotNull
    public CompostBin getCompostBin() {
        assert this.getPatch().getType() == PatchType.COMPOST_BIN;
        return (CompostBin) map.computeIfAbsent(FarmingAttribute.COMPOST, k -> new CompostBin(CompostBin.isBigBin(getPatch().getIds())));
    }

    public int addCompostableItem(@NotNull final Item item) {
        assert this.getPatch().getType() == PatchType.COMPOST_BIN;
        val bin = getCompostBin();
        val amount = bin.add(item);
        val type = bin.getType().orElseThrow(RuntimeException::new);
        this.map.put(FarmingAttribute.PRODUCT, type.getProduct());
        val array = type.getCompostableItems();
        val count = bin.getAmount();
        setValue(array[count - 1]);
        refresh();
        return amount;
    }

    public Item removeCompostableItem() {
        assert this.getPatch().getType() == PatchType.COMPOST_BIN;
        val bin = getCompostBin();
        assert !bin.isEmpty();
        val type = bin.getType().orElseThrow(RuntimeException::new);
        bin.removeOne();
        val product = type.getProduct().getProduct();
        val count = bin.getAmount();
        if (count == 0) {
            clear();
        } else {
            val array = type.getCompost();
            setValue(array[count - 1]);
            refresh();
        }
        return product;
    }

    public void openCompostBin() {
        assert this.getPatch().getType() == PatchType.COMPOST_BIN;
        assert this.getState() == GROWN;
        val bin = getCompostBin();
        val type = bin.getType().orElseThrow(RuntimeException::new);
        val array = type.getCompost();
        val count = bin.getAmount();
        setValue(array[count - 1]);
        refresh();
    }

    public void closeCompostBin() {
        assert this.getPatch().getType() == PatchType.COMPOST_BIN;
        val bin = this.getCompostBin();
        assert bin.isFull();
        bin.setType(bin.getType().orElseThrow(RuntimeException::new));
        setValue(getProduct().getRegularStage().first());
        this.map.put(FarmingAttribute.TIME, System.currentTimeMillis() + getProduct().getCycleTime());
        refresh();
    }

    public void setProduct(final FarmingProduct product) {
        assert product != null;
        setValue(product.transform(WEEDS, GROWING, getValue()));
        this.map.put(FarmingAttribute.PRODUCT, product);
        this.setTime(System.currentTimeMillis() + product.getCycleTime());
        this.refresh();
    }

    public void process() {
        if (getValue() == 0 || getTime() == -1) {
            return;
        }
        val currentTime = System.currentTimeMillis();
        if (getTime() >= currentTime) {
            return;
        }
        val bitValue = getValue();
        val oldState = getState();
        boolean regeneratingFruit = false;
        while(getTime() < currentTime) {
            val product = getProduct();
            val value = getValue();
            val state = getState();
            if (product == FarmingProduct.REDWOOD) {
                if (value >= 41) {
                    val redwood = Objects.requireNonNull((RedwoodTree) map.get(FarmingAttribute.REDWOOD));
                    setValue(redwood.value());
                    val delay = redwood.nextDelay();
                    setTime(delay == 0 ? -1 : (System.currentTimeMillis() + delay));
                    break;
                }
            }
            if ((state == STUMP || state == GROWN || state == REGAINING_PRODUCE) && getPatch().getType() == PatchType.BUSH_PATCH && getLives() < calculateBushLives()) {
                setLives(getLives() + 1);
                regeneratingFruit = true;
                if (getLives() == calculateBushLives()) {
                    blockTime();
                    break;
                }
                setValue(product.getMiscStage().harvestStage);
                val fruitRegenTimer = TimeUnit.MINUTES.toMillis(FarmingConstants.FRUIT_REGENERATION_TIMER);
                if (this.getTime() < currentTime || this.getTime() > (currentTime + fruitRegenTimer)) {
                    setTime(currentTime + fruitRegenTimer);
                }
                break;
            }
            if (((product == FarmingProduct.WEEDS || product == FarmingProduct.SCARECROW) && value == product.getRegularStage().last())
                    || (state == DEAD || state == GROWN || state == HEALTH_CHECK
                        //Grapevine patches should never progress the normal way once they reach fully grown stage.
                        || state == REGAINING_PRODUCE && !product.getMiscStage().regenerates())) {
                blockTime();
                break;
            }
            setTime(getTime() + product.getCycleTime());
            if (state == DISEASED || disease(state)) {
                setValue(product.transform(state, state == DISEASED ? DEAD : DISEASED, value));
                break;
            } else if (state == STUMP) {
                setValue(product.transform(state, GROWN, value));
                this.restartFruitTimer();
                break;
            }
            setValue(product.transform(state, state == WATERED ? GROWING : state, value));
        }
        if (!regeneratingFruit) {
            val newState = getState();
            if (newState == GROWN && newState != oldState) {
                getCompostFlag().ifPresent(flag -> {
                    val extra = (player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER) && this.getPatch().getType() == PatchType.HERB_PATCH) || getPatch().getType() == PatchType.BUSH_PATCH ? 1 : 0;
                    if (flag == COMPOST) {
                        setLives(extra + 4);
                    } else if (flag == SUPERCOMPOST) {
                        setLives(extra + 5);
                    } else if (flag == ULTRACOMPOST) {
                        setLives(extra + 6);
                    }
                });
            }
        }
        if (bitValue != getValue()) {
            refresh();
        }
    }

    private final int calculateBushLives() {
        val optionalPatchFlag = getCompostFlag();
        if (optionalPatchFlag.isPresent()) {
            val flag = optionalPatchFlag.get();
            if (flag == COMPOST) {
                return 5;
            } else if (flag == SUPERCOMPOST) {
                return 6;
            } else if (flag == ULTRACOMPOST) {
                return 7;
            }
        }
        return 4;
    }

    public boolean isFullOfWeeds() {
        val product = getProduct();
        val value = getValue();
        return product == FarmingProduct.WEEDS && value < 3 || product == FarmingProduct.SCARECROW && value >= 33 && value <= 35;
    }

    public void setWatered() {
        setValue(getProduct().transform(getState(), WATERED, getValue()));
        refresh();
    }

    public void cure() {
        setValue(getProduct().transform(getState(), GROWING, getValue()));
        refresh();
    }

    public void setHealthChecked() {
        setValue(getProduct().transform(getState(), GROWN, getValue()));
        getCompostFlag().ifPresent(flag -> {
            if (flag == COMPOST) {
                setLives(getLives() + 1);
            } else if (flag == SUPERCOMPOST) {
                setLives(getLives() + 2);
            } else if (flag == ULTRACOMPOST) {
                setLives(getLives() + 3);
            }
        });
        refresh();
    }

    public void setChoppedDown(final int id) {
        val product = getProduct();
        val definitions = product.getTreeDefinitions();
        val time = definitions.getRespawnDelay();
        if (product == FarmingProduct.REDWOOD) {
            val redwood = (RedwoodTree) map.computeIfAbsent(FarmingAttribute.REDWOOD, k -> new RedwoodTree());
            redwood.setChopped(id);
            setValue(redwood.value());
            setTime(System.currentTimeMillis() + redwood.nextDelay());
            refresh();
            return;
        }
        setValue(product.transform(getState(), STUMP, getValue()));
        setTime(System.currentTimeMillis() + (TimeUnit.TICKS.toMillis(time)));
        refresh();
    }

    public boolean isClear() {
        if (getPatch().getType() == PatchType.GRAPEVINE_PATCH) {
            return getValue() == 1;
        }
        return getProduct() == FarmingProduct.WEEDS && getValue() == 3;
    }

    public PatchState getState() {
        return getProduct().getState(getValue());
    }

    private void blockTime() {
        setTime(-1);
    }

    public void setFlag(final PatchFlag flag) {
        getFlags().add(flag);
    }

    public boolean containsFlag(final PatchFlag flag) {
        return getFlags().contains(flag);
    }

    public void setScarecrow(final FarmingProduct product) {
        val index = ArrayUtils.indexOf(this.getProduct().getRegularStage().getValues(), getValue());
        assert index != -1;
        this.setProduct(product);
        this.setTime(System.currentTimeMillis() + product.getCycleTime());
        setValue(product.getRegularStage().get(index));
        refresh();
    }

    /**
     * Refreshes the timer for the patch if it was kept in the stopped state for too long, e.g. if the patch grows
     * full of weeds again and remains in that state for longer periods of time, stopping it from growing straight
     * back full over and over once the player starts raking it.
     */
    public void refreshTimer() {
        if (getTime() < System.currentTimeMillis() - 600) {
            resetTimer();
        }
    }

    private void resetTimer() {
        setTime(System.currentTimeMillis() + getProduct().getCycleTime());
    }

    public void forceGrowForDebug() {
        setTime(System.currentTimeMillis());
    }

    public void refresh() {
        val patch = getPatch();
        //If the player isn't near the patch, we don't update it.
        if (!patch.getRectangle().contains(player.getX(), player.getY()) && this.getPatch().getType() != PatchType.HESPORI_PATCH) {
            return;
        }
        player.getVarManager().sendBit(patch.getVarbit(), getValue());
    }

    @Listener(type = Listener.ListenerType.LOGIN)
    private static final void onLogin(final Player player) {
        player.getFarming().refreshPatch(FarmingPatch.FARMING_GUILD_HESPORI);
    }

    public void setTreated() {
        assert this.getPatch().getType() == PatchType.GRAPEVINE_PATCH;
        assert getValue() == 0;
        this.setValue(1);
        this.setTime(-1);
        refresh();
    }

    public void clear() {
        val type = this.getPatch().getType();
        this.setProduct(FarmingProduct.WEEDS);
        this.getFlags().clear();
        resetTimer();
        setLives((player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER) && this.getPatch().getType() == PatchType.HERB_PATCH) || getPatch().getType() == PatchType.BUSH_PATCH ? 4 : 3);
        setValue(type == PatchType.GRAPEVINE_PATCH || type == PatchType.COMPOST_BIN ? 0 : 3);
        if (type == PatchType.COMPOST_BIN) {
            this.map.remove(FarmingAttribute.COMPOST);
        }
        refresh();
        if (type == PatchType.COMPOST_BIN) {
            setTime(-1);
        }
        if (player.getMemberRank().eligibleTo(MemberRank.ZENYTE_MEMBER)) {
            setFlag(PatchFlag.ULTRACOMPOST);
        } else if (player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            setFlag(PatchFlag.SUPERCOMPOST);
        } else if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER)) {
            setFlag(PatchFlag.COMPOST);
        }
    }

    public boolean isTreePatch() {
        return this.getPatch().getType().isTree();
    }

    private boolean disease(final PatchState state) {
        val product = this.getProduct();
        if (product.getDiseasedStage() == null) {
            return false;
        }
        //Spots cannot become diseased when in their very first form because the smallest diseased forms do not exist
        //in the cache; since spots do not progress when they disease, it would result in -1 varbit value.
        if (getValue() == product.getRegularStage().first() || state != GROWING || getFlags().contains(WATCHED_OVER)) {
            return false;
        }

        val patch = this.getPatch();

        if (player.getMemberRank().equals(MemberRank.ZENYTE_MEMBER)) {
            return false;
        }

        if (patch == TROLL_STRONGHOLD_HERB || patch.getType() == PatchType.HESPORI_PATCH || patch == WEISS_HERB_PATCH) {
            return false;
        }

        if (patch.equals(FALADOR_TREE) && DiaryReward.FALADOR_SHIELD4.eligibleFor(player)) {
            return false;
        }

        if (this.getPatch().getType() == PatchType.ALLOTMENT) {
            val flowerPatch = player.getFarming().getNearbyFlowerPatch(this);
            if (flowerPatch.isPresent()) {
                val flower = flowerPatch.get().getProduct();
                if (flower == FarmingProduct.WHITE_LILY) {
                    return false;
                }
                if (flower == FarmingProduct.MARIGOLD) {
                    if (product == FarmingProduct.ONION || product == FarmingProduct.TOMATO || product == FarmingProduct.POTATO) {
                        return false;
                    }
                } else if (flower == FarmingProduct.ROSEMARY) {
                    if (product == FarmingProduct.CABBAGE) {
                        return false;
                    }
                } else if (flower == FarmingProduct.SCARECROW) {
                    if (product == FarmingProduct.SWEETCORN) {
                        return false;
                    }
                } else if (flower == FarmingProduct.NASTURTIUM) {
                    if (product == FarmingProduct.WATERMELON) {
                        return false;
                    }
                }
            }
        }

        val roll = Utils.random(127);
        int chance = product.getDiseaseChance();

        val perk = player.getPerkManager().isValid(PerkWrapper.FERTILIZER);
        if (perk) {
            chance *= 0.5F;
        }
        val compost = this.getCompostFlag();
        if (compost.isPresent()) {
            switch(compost.get()) {
                case COMPOST:
                    chance *= 0.5F;
                    break;
                case SUPERCOMPOST:
                    chance *= 0.2F;
                    break;
                case ULTRACOMPOST:
                    chance *= 0.1F;
                    break;
                default:
                    break;
            }
        }

        return roll < chance;
    }

    public boolean bearsFruit() {
        if (getPatch().getType() == PatchType.BUSH_PATCH) {
            return true;
        }
        val misc = this.getProduct().getMiscStage();
        return misc != null && misc.getProductStages().length > 0;
    }

    public boolean isFruitless() {
        if (this.getPatch().getType() == PatchType.BELLADONNA_PATCH || this.getPatch().getType() == PatchType.BUSH_PATCH) {
            return getLives() <= 0;
        }
        val state = getState();
        val miscStage = this.getProduct().getMiscStage();
        if (miscStage == null) {
            return true;
        }
        val stages = miscStage.getProductStages();
        if (stages.length == 0) {
            return true;
        }
        assert state == GROWN || state == REGAINING_PRODUCE;
        return this.getValue() == stages[0];
    }

    public void removeFruit() {
        if (isFruitless()) {
            checkHarvest();
            return;
        }
        if (this.getPatch().getType() == PatchType.GRAPEVINE_PATCH) {
            if (success()) {
                return;
            }
            if (getLives() > 3) {
                setLives(getLives() - 1);
                return;
            }
        }

        if (this.getPatch().getType() == PatchType.BUSH_PATCH) {
            if (success()) {
                return;
            }
            restartFruitTimer();
            setLives(Math.max(0, getLives() - 1));
            if (getLives() <= 0) {
                setValue(getProduct().getMiscStage().getClearStage());
                refresh();
            }
            return;
        }

        if (this.getPatch().getType() == PatchType.CACTUS_PATCH) {
            if (success()) {
                return;
            }
            restartFruitTimer();
            setLives(Math.max(0, getLives() - 1));
            /*
            setValue(getProduct().getRegularStage().get(getLives();));
            refresh();
             */
            if (getLives() <= 0) {
                setValue(getProduct().getMiscStage().getClearStage());
                refresh();
            }
            return;
        }

        val state = getState();
        val stages = getProduct().getMiscStage().getProductStages();
        assert stages.length > 0;
        restartFruitTimer();
        if (state == GROWN) {
            setValue(stages[stages.length - 1]);
        } else {
            val index = ArrayUtils.indexOf(stages, getValue());
            if (this.getPatch().getType() == PatchType.GRAPEVINE_PATCH && index == 0) {
                this.setValue(getProduct().transform(state, DEAD, getValue()));
                refresh();
                return;
            }
            assert index > 0;
            setValue(stages[index - 1]);
        }
        refresh();
    }

    private void restartFruitTimer() {
        //Grapevine patches cannot re-grow fruit, they die after havesting.
        if (this.getPatch().getType() == PatchType.GRAPEVINE_PATCH || this.getPatch().getType() == PatchType.CELASTRUS_PATCH) {
            return;
        }
        val stages = getProduct().getMiscStage().getProductStages();
        //Block the timer if the tree doesn't support fruit.
        if ((this.getPatch().getType() != PatchType.BUSH_PATCH || this.getPatch().getType() != PatchType.CACTUS_PATCH) && stages.length <= 0) {
            return;
        }
        val currentTime = System.currentTimeMillis();
        val fruitRegenTimer = TimeUnit.MINUTES.toMillis(FarmingConstants.FRUIT_REGENERATION_TIMER);
        if (this.getTime() < currentTime || this.getTime() > (currentTime + fruitRegenTimer)) {
            setTime(currentTime + fruitRegenTimer);
        }
    }

    public int checkHarvest() {
        if (success()) {
            setLives(getLives() - 1);
            if (getLives() <= 0) {
                clear();
            }
            refresh();
        }
        return getLives();
    }

    public double successProbability() {
        val product = getProduct();
        val patch = getPatch();
        val level = player.getSkills().getLevel(Skills.FARMING);

        val chanceA = product.getLowEndHarvestChance();
        val chanceB = product.getHighEndHarvestChance();

        float base = 1F;

        if (patch.getType() == PatchType.HERB_PATCH) {
            if (SkillcapePerk.FARMING.isEffective(player)) {
                base += 0.05F;
            }
        }

        if (player.getEquipment().getId(EquipmentSlot.WEAPON) == 7409) {
            base += 0.1F;
        }

        float diaryBase = 1;

        if (patch.equals(FarmingPatch.CATHERBY_HERB)) {
            if (DiaryReward.KANDARIN_HEADGEAR4.eligibleFor(player)) {
                diaryBase += 0.15F;
            } else if (DiaryReward.KANDARIN_HEADGEAR3.eligibleFor(player)) {
                diaryBase += 0.1F;
            } else if (DiaryReward.KANDARIN_HEADGEAR2.eligibleFor(player)) {
                diaryBase += 0.05F;
            }
        }

        //TODO: Kourend diary, attas seed.

        double chance = ((((((99F - level) * chanceA) / 98F) + (((level - 1F) * chanceB) / 98F)) * ((base) * diaryBase)) + 1) / 256F;

        //Cap it to 2% chance of using up a life in case some data corrupts or goes wrong; prevents deadlocks.
        if (chance > 0.98F) {
            chance = 0.98D;
        }
        return chance;
    }

    public boolean success() {
        return successProbability() < Utils.getRandomDouble(1);
    }

    private static final PatchFlag[] compostFlags = new PatchFlag[] {
            COMPOST, SUPERCOMPOST, ULTRACOMPOST
    };

    public Optional<PatchFlag> getCompostFlag() {
        if (!getFlags().isEmpty()) {
            for (val flag : compostFlags) {
                if (containsFlag(flag)) {
                    return Optional.of(flag);
                }
            }
        }
        return Optional.empty();
    }

}
