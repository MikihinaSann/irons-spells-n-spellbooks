package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link LivingDestroyBlockEvent} from {@code Level#destroyBlock(pos, drop, entity, depth)}
 * — NeoForge hooks the same vanilla path for wither/dragon/door-breaking griefing.
 */
@Mixin(Level.class)
public abstract class LivingDestroyBlockDispatchMixin {

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z",
            at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onEntityDestroyBlock(BlockPos pos, boolean dropBlock, Entity entity,
                                                       int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        LivingDestroyBlockEvent event = new LivingDestroyBlockEvent(living, pos,
                ((Level) (Object) this).getBlockState(pos));
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }
}
