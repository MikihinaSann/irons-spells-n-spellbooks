package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link EntityTickEvent} on both sides. */
@Mixin(Entity.class)
public abstract class EntityTickDispatchMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$tickPre(CallbackInfo ci) {
        EntityTickEvent.Pre event = new EntityTickEvent.Pre((Entity) (Object) this);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void irons_spellbooks$tickPost(CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new EntityTickEvent.Post((Entity) (Object) this));
    }
}
