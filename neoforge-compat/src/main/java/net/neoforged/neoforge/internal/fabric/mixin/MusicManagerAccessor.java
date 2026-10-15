package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes the currently-playing music SoundInstance for {@code SelectMusicEvent}. */
@Mixin(MusicManager.class)
public interface MusicManagerAccessor {

    @Accessor("currentMusic")
    @Nullable
    SoundInstance neoforge$getCurrentMusic();
}
