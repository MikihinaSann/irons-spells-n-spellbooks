package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Player-side NeoForge event dispatch: item toss, break speed, critical hits.
 */
@Mixin(Player.class)
public abstract class PlayerEventDispatchMixin {

    /** ItemTossEvent — fired on the returned ItemEntity; the caller spawns it. */
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "RETURN"), cancellable = true)
    private void irons_spellbooks$onItemToss(ItemStack stack, boolean throwRandomly, boolean includeThrowerName,
                                             CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity itemEntity = cir.getReturnValue();
        if (itemEntity == null) {
            return;
        }
        var event = new ItemTossEvent(itemEntity, (Player) (Object) this);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(null);
        }
    }

    /** PlayerEvent.BreakSpeed — modifies dig speed via the event. */
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void irons_spellbooks$breakSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        var self = (Player) (Object) this;
        var event = new PlayerEvent.BreakSpeed(self, state, cir.getReturnValue(), null, self.level());
        NeoForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.isCanceled() ? -1.0f : event.getNewSpeed());
    }

    /** CriticalHitEvent — replaces the vanilla 1.5x crit multiplier. */
    @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5f))
    private float irons_spellbooks$critMultiplier(float multiplier, Entity target) {
        var event = new CriticalHitEvent((Player) (Object) this, target, multiplier, true);
        NeoForge.EVENT_BUS.post(event);
        return event.getDamageMultiplier();
    }
}
