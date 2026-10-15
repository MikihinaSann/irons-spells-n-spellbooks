package net.neoforged.neoforge.client.event;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when the vanilla {@code Minecraft#getSituationalMusic()} picks the
 * background music to play. Upstream NeoForge exposes the class but never
 * dispatches it; the compat layer posts it at the end of
 * {@code getSituationalMusic} so listeners can actually override music.
 */
public class SelectMusicEvent extends Event implements ICancellableEvent {
    @Nullable
    private Music music;
    private final Music originalMusic;
    @Nullable
    private final SoundInstance playingMusic;

    public SelectMusicEvent(Music music, @Nullable SoundInstance playingMusic) {
        this.music = music;
        this.originalMusic = music;
        this.playingMusic = playingMusic;
    }

    public Music getOriginalMusic() {
        return this.originalMusic;
    }

    @Nullable
    public SoundInstance getPlayingMusic() {
        return this.playingMusic;
    }

    @Nullable
    public Music getMusic() {
        return this.music;
    }

    public void setMusic(@Nullable Music newMusic) {
        this.music = newMusic;
    }

    public void overrideMusic(@Nullable Music newMusic) {
        this.music = newMusic;
        this.setCanceled(true);
    }
}
