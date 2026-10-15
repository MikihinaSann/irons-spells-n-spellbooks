package net.neoforged.neoforge.client.event.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.neoforged.bus.api.Event;

/**
 * Fired from {@link SoundEngine#play(SoundInstance)} before a sound starts.
 * Setting the sound to {@code null} prevents it from playing.
 */
public class PlaySoundEvent extends Event {
    private final SoundEngine engine;
    private SoundInstance sound;

    public PlaySoundEvent(SoundEngine engine, SoundInstance sound) {
        this.engine = engine;
        this.sound = sound;
    }

    public SoundEngine getEngine() {
        return this.engine;
    }

    public SoundInstance getSound() {
        return this.sound;
    }

    public void setSound(SoundInstance result) {
        this.sound = result;
    }
}
