package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * NeoForge reroutes the enchantability lookups in {@link EnchantmentHelper}
 * through the stack-aware {@code Item.getEnchantmentValue(ItemStack)}; vanilla
 * only calls the no-arg overload. These are the only two call sites in 1.21.1.
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @WrapOperation(method = "getEnchantmentCost", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;getEnchantmentValue()I"))
    private static int irons_spellbooks$stackAwareEnchantmentCost(Item item, Operation<Integer> original,
                                                                  @Local(argsOnly = true) ItemStack stack) {
        return item.getEnchantmentValue(stack);
    }

    @WrapOperation(method = "selectEnchantment", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;getEnchantmentValue()I"))
    private static int irons_spellbooks$stackAwareSelectEnchantment(Item item, Operation<Integer> original,
                                                                    @Local(argsOnly = true) ItemStack stack) {
        return item.getEnchantmentValue(stack);
    }
}
