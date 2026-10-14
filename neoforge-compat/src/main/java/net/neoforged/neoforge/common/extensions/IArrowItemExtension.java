package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * NeoForge's {@code ArrowItem} extension — injected onto
 * {@code net.minecraft.world.item.ArrowItem}. NeoForge's default: infinity only
 * applies to vanilla's own ArrowItem.
 */
public interface IArrowItemExtension {
    private ArrowItem self() {
        return (ArrowItem) this;
    }

    default boolean isInfinite(ItemStack ammo, ItemStack bow, LivingEntity living) {
        int enchant = EnchantmentHelper.getItemEnchantmentLevel(
                living.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.INFINITY), bow);
        return enchant <= 0 ? false : self().getClass() == ArrowItem.class;
    }
}
