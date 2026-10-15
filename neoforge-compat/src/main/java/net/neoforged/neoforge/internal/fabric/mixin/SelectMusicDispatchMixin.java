package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Dispatches NeoForge's {@link SelectMusicEvent} at the end of
 * {@code Minecraft#getSituationalMusic} — listeners may replace the chosen
 * track via {@code setMusic}/{@code overrideMusic}. (Upstream NeoForge defines
 * the event but never posts it; this compat layer wires it so mods relying on
 * it actually work.)
 */
@Mixin(Minecraft.class)
public abstract class SelectMusicDispatchMixin {

    @Shadow
    public abstract MusicManager getMusicManager();

    @Inject(method = "getSituationalMusic", at = @At("RETURN"), cancellable = true)
    private void neoforge$selectMusic(CallbackInfoReturnable<Music> cir) {
        SelectMusicEvent event = new SelectMusicEvent(cir.getReturnValue(),
                ((MusicManagerAccessor) getMusicManager()).neoforge$getCurrentMusic());
        NeoForge.EVENT_BUS.post(event);
        if (event.getMusic() != cir.getReturnValue()) {
            cir.setReturnValue(event.getMusic());
        }
    }
}
