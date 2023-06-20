package com.zenyte.game.world.entity.masks;

import com.google.gson.annotations.Expose;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentType;
import com.zenyte.network.io.RSBuffer;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import it.unimi.dsi.fastutil.bytes.Byte2ShortOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Arrays;

/**
 * @author Kris | 1. veebr 2018 : 22:26.11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class Appearance {
	//
	public static final short[] DEFAULT_MALE_APPEARANCE = new short[] { 0, 10, 18, 26, 33, 36, 42 };
	public static final short[] DEFAULT_FEMALE_APPEARANCE = new short[] { 45, 1000, 56, 61, 68, 70, 79 };

	@Getter
	private transient RenderAnimation renderAnimation;
	private final transient Player player;
	@Getter
	@Setter
	transient private int npcId;
	@Getter
	private transient boolean invisible, hideEquipment;
	@Expose
	@Getter
	@Setter
	private boolean male;
	@Expose
	@Getter
	@Setter
	private short[] appearance;
	@Expose
	@Getter
	@Setter
	private byte[] colours;
	@Expose
	@Getter
	private byte headIcon;
	@Getter private transient final Byte2ShortOpenHashMap forcedAppearance = new Byte2ShortOpenHashMap();

	@Getter private transient RSBuffer buffer = new RSBuffer(150);

	public Appearance(final Player player) {
		this.player = player;
		renderAnimation = RenderAnimation.DEFAULT_RENDER;
		appearance = Arrays.copyOf(DEFAULT_MALE_APPEARANCE, 7);
		male = true;
		npcId = -1;
		colours = new byte[5];
		headIcon = -1;
	}

	public final void initialize(final Appearance appearance) {
		male = appearance.male;
		this.appearance = appearance.appearance;
		colours = appearance.colours;
	}

	public void forceAppearance(final int slot, final int id) {
		forcedAppearance.put((byte) slot, (short) id);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void clearForcedAppearance() {
		if (!forcedAppearance.isEmpty()) {
			forcedAppearance.clear();
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	/**
	 * Transforms the Player to specified NPC.
	 * 
	 * @param id
	 *            id of the npc to which the player is requested to transform to.
	 */
	public void transform(final int id) {
		npcId = id;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Changes the head-icon of the player
	 * 
	 * @param id
	 *            (id of the head-icon that needs to be changed.)
	 */
	public void setHeadIcon(final byte id) {
		headIcon = id;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Sets the player's visibility state to the specified state.
	 * 
	 * @param invisible
	 *            whether the player will turn invisible or not.
	 */
	public void setInvisible(final boolean invisible) {
		this.invisible = invisible;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * 
	 * @param invisible
	 */
	public void setHideEquipment(final boolean invisible) {
		hideEquipment = invisible;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Modifies the body part to a certain value
	 * 
	 * @param index
	 *            (The body part that needs to be modified)
	 * @param value
	 *            (The value that the body part needs to be modified to)
	 */
	public void modifyAppearance(final byte index, final short value) {
		appearance[index] = value;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * 
	 * @param index
	 *            (The colour of the body part that needs to be modified)
	 * @param value
	 *            (The value that the colour needs to be modified to)
	 */
	public void modifyColour(final byte index, final byte value) {
		colours[index] = value;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Generates a RenderAnimation object which holds the animation ids of the currently held weapon as a render animation. If the player
	 * isn't wielding a weapon, the default state is returned.
	 * 
	 * @return render animation object.
	 */
	public final RenderAnimation generateRenderAnimation() {
		final Item weapon = player.getWeapon();
		if (weapon == null) {
			return RenderAnimation.DEFAULT_RENDER;
		}
		final ItemDefinitions defs = weapon.getDefinitions();
		return new RenderAnimation(defs.getStandAnimation(), defs.getStandTurnAnimation(), defs.getWalkAnimation(),
				defs.getRotate180Animation(), defs.getRotate90Animation(), defs.getRotate270Animation(), defs.getRunAnimation());
	}

	/**
	 * Resets the player's render animation to default, which is dependant on the weapon the player is wielding. If the player isn't
	 * wielding a weapon, returns the default stance, if they are, will return the render animation of the weapon. Flags the appearance
	 * mask.
	 */
	public void resetRenderAnimation() {
		renderAnimation = generateRenderAnimation();
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Sets the player's render animation to the specified render animation object. Flags the appearance mask.
	 * 
	 * @param anim
	 *            render animation object to set the player's render animation to.
	 */
	public void setRenderAnimation(final RenderAnimation anim) {
		renderAnimation = anim;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	private static final byte[] EMPTY_EQUIPMENT_DATA = new byte[12];

	/**
	 * Writes the appearance data of the player, containing information such as: The gender, whether the player is skulled or not, the
	 * current headicon based on active prayers, the worn equipment data, or if the player is transformed to a npc, the npc id, the colours
	 * of the clothes of the player, the render animation, display name, combat level, visibility state and skill total.
	 * 
	 * @param data
	 *            the stream to which this information is written to.
	 */
	public synchronized final void writeAppearanceData(final RSBuffer data) {
	    if (!buffer.isReadable()) {
            val definitions = NPCDefinitions.get(npcId);
            buffer.writeByte(male ? 0 : 1);
            buffer.writeByte(player.getVariables().isSkulled() ? 0 : -1);
            buffer.writeByte(headIcon);

            if (definitions != null) {
                buffer.writeShort(-1);
                buffer.writeShort(npcId);
            } else if (!invisible && !hideEquipment) {
                writeEquipmentData(buffer);
            } else {
                buffer.writeBytes(EMPTY_EQUIPMENT_DATA);
            }

            buffer.writeBytes(colours);
            val renderAnimation = Utils.getOrDefault(definitions, this.renderAnimation);
            buffer.writeShort(renderAnimation.getStand());
            buffer.writeShort(renderAnimation.getStandTurn());
            buffer.writeShort(renderAnimation.getWalk());
            buffer.writeShort(renderAnimation.getRotate180());
            buffer.writeShort(renderAnimation.getRotate90());
            buffer.writeShort(renderAnimation.getRotate270());
            buffer.writeShort(renderAnimation.getRun());

            buffer.writeString(player.getPlayerInformation().getDisplayname());
            buffer.writeByte(player.getSkills().getCombatLevel());
            buffer.writeShort(0);
            buffer.writeByte(invisible ? 1 : 0);
        }
        val length = buffer.readableBytes();
		data.write128Byte(length);
		data.writeBytes128(buffer);
    }

	/**
	 * Writes the equipment data to the temporary data stream. Every worn item's id is written here, along with whether the body is a full
	 * body - hides the arms, whether the helm is a full mask - hiding the head & whether the helm is a full helm - hiding parts of the
	 * head.
	 * 
	 * @param tempData
	 *            temporary data stream to write to.
	 */
	private void writeEquipmentData(final RSBuffer tempData) {
		for (int i = 0; i < 4; i++) {
			final int item = getId(i);
			if (item == -1) {
				tempData.writeByte(0);
			} else {
				tempData.writeShort(item + 0x200);
			}
		}
		final int chest = getId(EquipmentSlot.PLATE.getSlot());
		tempData.writeShort(chest == -1 ? (0x100 + appearance[2]) : (chest + 0x200));
		final int shield = getId(EquipmentSlot.SHIELD.getSlot());
		if (shield == -1) {
			tempData.writeByte(0);
		} else {
			tempData.writeShort(shield + 0x200);
		}
		if (chest == -1 || ItemDefinitions.get(chest).getEquipmentType() != EquipmentType.FULL_BODY) {
			tempData.writeShort(0x100 + appearance[3]);
		} else {
			tempData.writeByte(0);
		}
		final int legs = getId(EquipmentSlot.LEGS.getSlot());
		tempData.writeShort(legs == -1 ? (0x100 + appearance[5]) : (legs + 0x200));
		final int helm = getId(EquipmentSlot.HELMET.getSlot());
		if (helm == -1 || ItemDefinitions.get(helm).getEquipmentType() == EquipmentType.DEFAULT) {
			tempData.writeShort(0x100 + appearance[0]);
		} else {
			tempData.writeByte(0);
		}
		final int gloves = getId(EquipmentSlot.HANDS.getSlot());
		tempData.writeShort(gloves == -1 ? (0x100 + appearance[4]) : (gloves + 0x200));
		final int boots = getId(EquipmentSlot.BOOTS.getSlot());
		tempData.writeShort(boots == -1 ? (0x100 + appearance[6]) : (boots + 0x200));
		if (helm == -1 || ItemDefinitions.get(helm).getEquipmentType() != EquipmentType.FULL_MASK) {
			tempData.writeShort(0x100 + appearance[1]);
		} else {
			tempData.writeByte(0);
		}
	}
	
	private final int getId(final int slot) {
		if (!forcedAppearance.isEmpty() && forcedAppearance.containsKey((byte) slot)) {
			return forcedAppearance.get((byte) slot);
		}
		return player.getEquipment().getId(slot);
	}

	public String getGender() {
		val ironmanmode = player.isIronman();
		return ironmanmode ? isMale() ? "Ironman" : "Ironwoman" : isMale() ? "Man" : "Woman";
	}
	
	public boolean isTransformedIntoNpc() {
		return npcId != -1;
	}

}