package com.zenyte.game.content.preset;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.events.InitializationEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author Tommeh | 20/04/2020 | 00:32
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PresetManager {
    
    private final transient WeakReference<Player> player;
    
    @Getter
    private List<Preset> presets;
    @Getter
    private int defaultPreset, unlockedSlots;
    
    public PresetManager(@NotNull final Player player) {
        this.player = new WeakReference<>(player);
        this.presets = new ObjectArrayList<>();
    }
    
    @Subscribe
    public static final void onInitialization(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val manager = savedPlayer.getPresetManager();
        if (manager == null) {
            return;
        }
        val thisManager = player.getPresetManager();
        thisManager.defaultPreset = manager.defaultPreset;
        thisManager.unlockedSlots = manager.unlockedSlots;
        if (manager.presets != null) {
            for (val preset : manager.presets) {
                thisManager.presets.add(new Preset(preset));
            }
        }
    }

    public void revalidatePresets() {
        val max = getMaximumAllowedPresets();
        val current = getTotalPresets();
        for (int i = 0; i < current; i++) {
            val preset = presets.get(i);
            preset.setAvailable(i < max);
        }
        if (defaultPreset >= max) {
            defaultPreset = 0;
        }
    }

    public void addPreset(final int index, final String name) {
        val player = this.player.get();
        if (player == null) {
            return;
        }
        presets.add(index, new Preset(name, player));
        revalidatePresets();
    }

    public void setPreset(final int index, @NotNull final Preset preset) {
        presets.set(index, preset);
    }

    @Nullable
    public Preset getPreset(final int index) {
        if (index < 0 || index >= getTotalPresets()) {
            return null;
        }
        return presets.get(index);
    }
    
    public int getTotalPresets() {
        return presets.size();
    }
    
    public int getMaximumPresets() {
        //Everyone gets two presets for free.
        int availablePresets = 2;
        //Vote points slots.
        availablePresets += this.unlockedSlots;
        return availablePresets;
    }

    public int getMaximumAllowedPresets() {
        //Everyone gets two presets for free.
        int availablePresets = 2;
        val player = this.player.get();
        if (player == null) {
            throw new IllegalStateException();
        }
        val unlockableSlots = (player.isMember() ? 8 : 3) + player.getMemberRank().ordinal();
        //Vote points slots.
        availablePresets += unlockableSlots;
        return availablePresets;
    }

    public boolean canPurchaseFreeSlot() {
        return getUnlockableSlotsCount() > 0;
    }

    public void addUnlockedSlot() {
        val player = this.player.get();
        if (player == null) {
            throw new IllegalStateException();
        }
        unlockedSlots = Math.min((player.isMember() ? 8 : 3) + player.getMemberRank().ordinal(), unlockedSlots + 1);
    }

    public int getUnlockableSlotsCount() {
        val player = this.player.get();
        if (player == null) {
            throw new IllegalStateException();
        }
        val unlockableSlotsCount = (player.isMember() ? 8 : 3) + player.getMemberRank().ordinal();
        return unlockableSlotsCount - unlockedSlots;
    }

    public void setDefaultPreset(final int slot) {
        if (slot < 0 || slot >= getTotalPresets()) {
            return;
        }
        this.defaultPreset = slot;
    }
}
