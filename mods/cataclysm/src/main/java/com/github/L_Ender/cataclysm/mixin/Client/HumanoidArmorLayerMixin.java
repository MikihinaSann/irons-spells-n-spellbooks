package com.github.L_Ender.cataclysm.mixin.Client;


import com.github.L_Ender.cataclysm.client.render.item.CustomArmorRenderProperties;
import com.github.L_Ender.cataclysm.items.LayerArmorPostRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin extends RenderLayer {

    private ItemStack lastArmorItemStackRendered = ItemStack.EMPTY;

    @Unique
    private float cataclysm$limbSwing;
    @Unique
    private float cataclysm$limbSwingAmount;
    @Unique
    private float cataclysm$partialTick;
    @Unique
    private float cataclysm$ageInTicks;
    @Unique
    private float cataclysm$netHeadYaw;
    @Unique
    private float cataclysm$headPitch;

    @Shadow
    protected abstract void setPartVisibility(HumanoidModel humanoidModel, EquipmentSlot equipmentSlot);


    public HumanoidArmorLayerMixin(RenderLayerParent renderLayerParent) {
        super(renderLayerParent);
    }

    /**
     * Vanilla 1.21.1's {@code renderArmorPiece} no longer receives the animation
     * floats — capture them from {@code render}, which calls it once per slot.
     */
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD")
    )
    private void cataclysm$captureAnimationArgs(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
            float netHeadYaw, float headPitch, CallbackInfo ci) {
        this.cataclysm$limbSwing = limbSwing;
        this.cataclysm$limbSwingAmount = limbSwingAmount;
        this.cataclysm$partialTick = partialTick;
        this.cataclysm$ageInTicks = ageInTicks;
        this.cataclysm$netHeadYaw = netHeadYaw;
        this.cataclysm$headPitch = headPitch;
    }

    @Inject(
            method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void CMrenderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity livingEntity,
            EquipmentSlot slot, int packedLight, HumanoidModel<?> p_model, CallbackInfo ci) {
        ItemStack itemstack = livingEntity.getItemBySlot(slot);
        if (!(itemstack.getItem() instanceof LayerArmorPostRender)) {
            return;
        }
        ci.cancel();
        lastArmorItemStackRendered = itemstack;
        if (itemstack.getItem() instanceof ArmorItem armoritem && armoritem.getEquipmentSlot() == slot) {
            boolean flag = slot == EquipmentSlot.LEGS;

            Model armorModel = ClientHooks.getArmorModel(livingEntity, itemstack, slot, p_model);

            if (this.getParentModel() instanceof HumanoidModel<?> parentHumanoid && armorModel instanceof HumanoidModel<?> armorHumanoid) {
                parentHumanoid.copyPropertiesTo((HumanoidModel) armorModel);
            }
            setPartVisibility((HumanoidModel) armorModel, slot);

            ArmorMaterial armormaterial = armoritem.getMaterial().value();

            net.neoforged.neoforge.client.extensions.common.IClientItemExtensions extensions = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemstack);

            extensions.setupModelAnimations(livingEntity, itemstack, slot, armorModel, cataclysm$limbSwing,
                    cataclysm$limbSwingAmount, cataclysm$partialTick, cataclysm$ageInTicks, cataclysm$netHeadYaw,
                    cataclysm$headPitch);

            int fallbackColor = extensions.getDefaultDyeColor(itemstack);
            for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                ArmorMaterial.Layer armormaterial$layer = armormaterial.layers().get(layerIdx);
                int j = extensions.getArmorLayerTintColor(itemstack, livingEntity, armormaterial$layer, layerIdx, fallbackColor);
                if (j != 0) {
                    var texture = net.neoforged.neoforge.client.ClientHooks.getArmorTexture(livingEntity, itemstack, armormaterial$layer, flag, slot);
                    CustomArmorRenderProperties.renderCustomArmor(poseStack, bufferSource, packedLight, lastArmorItemStackRendered, armoritem, armorModel, flag, texture);
                }
            }
        }
    }
}
