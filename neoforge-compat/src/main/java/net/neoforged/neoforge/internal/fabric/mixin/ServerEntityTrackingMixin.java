package net.neoforged.neoforge.internal.fabric.mixin;

import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.internal.fabric.SpawnDataPayload;
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
        if (this.entity instanceof IEntityWithComplexSpawn complex) {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                    Unpooled.buffer(), this.entity.level().registryAccess());
            complex.writeSpawnData(buf);
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            buf.release();
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
                    player, new SpawnDataPayload(this.entity.getId(), data));
        }
    }

    @Inject(method = "removePairing", at = @At("HEAD"))
    private void irons_spellbooks$stopTracking(ServerPlayer player, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, this.entity));
    }
}
