package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link PlaySoundEvent} at the head of {@code SoundEngine.play}.
 * If the event's sound is set to null the play call is cancelled.
 */
@Mixin(SoundEngine.class)
public abstract class PlaySoundEventMixin {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$playSound(SoundInstance sound, CallbackInfo ci) {
        PlaySoundEvent event = new PlaySoundEvent((SoundEngine) (Object) this, sound);
        NeoForge.EVENT_BUS.post(event);
        if (event.getSound() == null) {
            ci.cancel();
        }
    }
}
