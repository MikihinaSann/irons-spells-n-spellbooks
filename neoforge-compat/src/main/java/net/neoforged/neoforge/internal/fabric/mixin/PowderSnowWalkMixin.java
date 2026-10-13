package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * NeoForge routes the leather-boots check through
 * {@code Item.canWalkOnPowderedSnow(stack, wearer)}; additionally honors
 * curio/trinket stacks (e.g. Frostward Ring).
 */
@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowWalkMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("RETURN"), cancellable = true)
    private static void irons_spellbooks$equippedWalkCheck(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || !(entity instanceof LivingEntity living)) {
            return;
        }
        ItemStack feet = living.getItemBySlot(EquipmentSlot.FEET);
        if (feet.getItem().canWalkOnPowderedSnow(feet, living)) {
            cir.setReturnValue(true);
            return;
        }
        CuriosApi.getTrinketComponent(living).ifPresent(component -> {
            for (var pair : component.getAllEquipped()) {
                ItemStack stack = pair.getB();
                if (stack.getItem().canWalkOnPowderedSnow(stack, living)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        });
    }
}
