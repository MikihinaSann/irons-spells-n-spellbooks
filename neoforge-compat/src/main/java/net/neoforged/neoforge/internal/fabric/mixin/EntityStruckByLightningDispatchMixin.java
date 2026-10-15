package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Dispatches NeoForge's {@link EntityStruckByLightningEvent} at the
 * {@code LightningBolt.tick} site where vanilla calls {@code entity.thunderHit}.
 */
@Mixin(LightningBolt.class)
public abstract class EntityStruckByLightningDispatchMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"))
    private void neoforge$onEntityStruckByLightning(Entity entity, ServerLevel level,
            LightningBolt lightning, Operation<Void> original) {
        if (!NeoForge.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, lightning)).isCanceled()) {
            original.call(entity, level, lightning);
        }
    }
}
