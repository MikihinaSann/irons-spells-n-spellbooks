package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side item extensions (BEWLR, custom arm poses, nametag).
 */
public interface IClientItemExtensions {

    @Nullable
    default net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return null;
    }

    @Nullable
    default HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return null;
    }

    @Nullable
    default IArmPoseTransformer getArmPoseTransformer() {
        return null;
    }

    @Nullable
    default Model getGenericArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot,
                                       HumanoidModel<?> original) {
        return null;
    }

    @Nullable
    default Font getFont(ItemStack stack, Font context) {
        return null;
    }

    default boolean shouldDrawItemName(ItemStack stack) {
        return true;
    }

    /** Lookup helper. */
    static IClientItemExtensions of(ItemStack stack) {
        return ClientExtensionsRegistry.getItemExtension(stack.getItem());
    }
}
