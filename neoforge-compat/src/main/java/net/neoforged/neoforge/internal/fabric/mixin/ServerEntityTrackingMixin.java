package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link PlayerEvent.StartTracking} / {@link PlayerEvent.StopTracking}
 * from {@code ServerEntity} pairing.
 */
@Mixin(ServerEntity.class)
public abstract class ServerEntityTrackingMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At("TAIL"))
    private void irons_spellbooks$startTracking(ServerPlayer player, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, this.entity));
    }

    @Inject(method = "removePairing", at = @At("HEAD"))
    private void irons_spellbooks$stopTracking(ServerPlayer player, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, this.entity));
    }
}
