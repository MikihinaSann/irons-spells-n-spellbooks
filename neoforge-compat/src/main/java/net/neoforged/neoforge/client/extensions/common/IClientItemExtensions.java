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

    default Model getGenericArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot,
                                       HumanoidModel<?> original) {
        HumanoidModel<?> replacement = getHumanoidArmorModel(entityLiving, itemStack, armorSlot, original);
        if (replacement != original && replacement != null) {
            net.neoforged.neoforge.client.ClientHooks.copyModelProperties(original, replacement);
            return replacement;
        }
        return original;
    }

    /**
     * Humanoid armor model override — the generic model hook adapts it.
     */
    @Nullable
    default HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack,
            EquipmentSlot armorSlot, HumanoidModel<?> original) {
        return original;
    }

    /**
     * Called to apply custom model animations to a humanoid armor model.
     */
    default void setupModelAnimations(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot equipmentSlot,
            Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
            float netHeadYaw, float headPitch) {
    }

    /**
     * Default dye color for this armor stack.
     */
    default int getDefaultDyeColor(ItemStack stack) {
        return stack.is(net.minecraft.tags.ItemTags.DYEABLE)
                ? net.minecraft.util.FastColor.ARGB32.opaque(net.minecraft.world.item.component.DyedItemColor
                        .getOrDefault(stack, net.minecraft.world.item.component.DyedItemColor.LEATHER_COLOR))
                : 0xFFFFFFFF;
    }

    /**
     * Tint color for an armor material layer.
     */
    default int getArmorLayerTintColor(ItemStack stack, LivingEntity entity,
            net.minecraft.world.item.ArmorMaterial.Layer layer, int layerIdx, int fallbackColor) {
        return layer.dyeable() ? fallbackColor : 0xFFFFFFFF;
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
