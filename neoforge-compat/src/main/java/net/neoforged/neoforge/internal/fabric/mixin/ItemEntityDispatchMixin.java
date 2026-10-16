package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dispatches {@link ItemExpireEvent} (expiry discard interception) and
 * {@link ItemEntityPickupEvent.Pre} (head of playerTouch).
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityDispatchMixin {
    private static final int neoforge$VANILLA_LIFETIME = 6000;

    @Shadow
    private int age;

    @Shadow
    public abstract int getAge();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"), cancellable = true)
    private void neoforge$itemExpire(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.level().isClientSide || getAge() < neoforge$VANILLA_LIFETIME) {
            return;
        }
        ItemExpireEvent event = new ItemExpireEvent(self, 0);
        NeoForge.EVENT_BUS.post(event);
        if (event.getExtraLife() != 0) {
            this.age = getAge() - event.getExtraLife();
            ci.cancel();
        }
    }

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void neoforge$itemEntityPickupPre(net.minecraft.world.entity.player.Player player, CallbackInfo ci) {
        ItemEntityPickupEvent.Pre event = new ItemEntityPickupEvent.Pre(player, (ItemEntity) (Object) this);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
