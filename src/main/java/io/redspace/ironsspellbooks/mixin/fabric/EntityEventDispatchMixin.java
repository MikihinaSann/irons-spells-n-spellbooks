package io.redspace.ironsspellbooks.mixin.fabric;

import io.redspace.ironsspellbooks.platform.fabric.DeathDropCapture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link EntityTravelToDimensionEvent} from {@code Entity#changeDimension}
 * and captures death-drop {@link ItemEntity} spawns for {@code LivingDropsEvent}.
 */
@Mixin(Entity.class)
public abstract class EntityEventDispatchMixin {

    @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onTravelToDimension(DimensionTransition transition, CallbackInfoReturnable<Entity> cir) {
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide) {
            return;
        }
        EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(self, transition.newLevel().dimension());
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN"))
    private void irons_spellbooks$captureSpawnedDrop(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        DeathDropCapture.capture((Entity) (Object) this, cir.getReturnValue());
    }
}
