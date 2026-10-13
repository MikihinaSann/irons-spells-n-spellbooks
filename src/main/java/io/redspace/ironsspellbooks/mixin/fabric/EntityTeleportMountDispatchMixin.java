package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link EntityTeleportEvent} and {@link EntityMountEvent} from the
 * vanilla entity teleport/mount entry points.
 */
@Mixin(Entity.class)
public abstract class EntityTeleportMountDispatchMixin {

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$teleport(double x, double y, double z, CallbackInfo ci) {
        var event = new EntityTeleportEvent((Entity) (Object) this, x, y, z);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$mount(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        var event = new EntityMountEvent((Entity) (Object) this, vehicle, vehicle, true);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$dismount(CallbackInfo ci) {
        var self = (Entity) (Object) this;
        var vehicle = self.getVehicle();
        if (vehicle == null) {
            return;
        }
        var event = new EntityMountEvent(self, vehicle, vehicle, false);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
