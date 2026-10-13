package io.redspace.ironslib.transmog.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironslib.transmog.TransmogItemData;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class TransmogArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> {
   protected final ArrayList<TransmogArmorRenderer<T>.AsyncBone> asyncBones = new ArrayList<>();
   protected final ArrayList<TransmogArmorRenderer.CapeBone> capeBones;
   private boolean hideHat;
   private boolean hideJacket;
   private boolean dyeable;

   public boolean hideHat() {
      return this.hideHat;
   }

   public boolean hideJacket() {
      return this.hideJacket;
   }

   public ResourceLocation getTextureLocation(T animatable) {
      return super.getTextureLocation(animatable);
   }

   public TransmogArmorRenderer(GeoModel<T> model) {
      super(model);
      this.asyncBones.add(new AsyncBone("armorLeggingTorsoLayer", EquipmentSlot.LEGS, m -> m.body, Vec3.ZERO));
      this.asyncBones.add(new AsyncBone("armorTorsoExtensionRightLeg", EquipmentSlot.CHEST, m -> m.rightLeg, new Vec3(2.0, 12.0, 0.0)));
      this.asyncBones.add(new AsyncBone("armorTorsoExtensionLeftLeg", EquipmentSlot.CHEST, m -> m.leftLeg, new Vec3(-2.0, 12.0, 0.0)));
      this.capeBones = new ArrayList<>();
   }

   public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      color = -1;
      if (this.dyeable && bone.getName().startsWith("dye") && this.currentStack != null) {
         if (TransmogItemData.has(this.currentStack)) {
            color = TransmogItemData.get(this.currentStack).dyeColor() | 0xFF000000;
         } else {
            color = Minecraft.getInstance().itemColors.getColor(this.currentStack, 0) | 0xFF000000;
         }
      }

      super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, color);
   }

   public TransmogArmorRenderer<T> setHideHat() {
      this.hideHat = true;
      return this;
   }

   public TransmogArmorRenderer<T> setHideJacket() {
      this.hideJacket = true;
      return this;
   }

   public TransmogArmorRenderer<T> setDyeable() {
      this.dyeable = true;
      return this;
   }

   public TransmogArmorRenderer<T> withCapeBone(String bone) {
      this.capeBones.add(new CapeBone(bone, new Vec3(0.5, 0.0, 0.0)));
      return this;
   }

   public TransmogArmorRenderer<T> withCapeBone(TransmogArmorRenderer.CapeBone... bones) {
      for (TransmogArmorRenderer.CapeBone bone : bones) {
         this.capeBones.add(bone);
      }

      return this;
   }

   protected void grabRelevantBones(BakedGeoModel bakedModel) {
      if (this.lastModel != bakedModel) {
         this.asyncBones.forEach(bone -> bone.grabBone(this.model));
      }

      super.grabRelevantBones(bakedModel);
   }

   protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
      super.applyBoneVisibilityBySlot(currentSlot);
      this.asyncBones.forEach(bone -> bone.applyVisibilityBySlot(currentSlot));
   }

   public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
      super.applyBoneVisibilityByPart(currentSlot, currentPart, model);
      this.asyncBones.forEach(bone -> bone.applyVisibilityByPart(model, currentPart));
   }

   protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
      super.applyBaseTransformations(baseModel);
      this.asyncBones
         .forEach(
            bonex -> {
               if (bonex.actualBone != null) {
                  ModelPart bodyPart = bonex.partToFollow.apply(baseModel);
                  RenderUtil.matchModelPartRot(bodyPart, bonex.actualBone);
                  bonex.actualBone
                     .updatePosition((float)bonex.partOffset.x + bodyPart.x, (float)bonex.partOffset.y + -bodyPart.y, (float)bonex.partOffset.z + bodyPart.z);
               }
            }
         );
      if (!this.capeBones.isEmpty() && this.currentEntity != null) {
         if (this.currentEntity instanceof ISupportTransmog && this.currentEntity instanceof LivingEntity livingEntity) {
            CapeData capeData = ((ICapeDataProvider)(Object) this.currentEntity).irons_lib$getCapeData();
            capeData.renderUpdate(livingEntity);
            Vec3 rotations = capeData.calculateRotationsDegrees(livingEntity, this.partialTick);

            for (TransmogArmorRenderer.CapeBone capeBone : this.capeBones) {
               Optional<GeoBone> bone = this.model.getBone(capeBone.name());
               if (bone.isPresent()) {
                  bone.get()
                     .setRotX(
                        bone.get().getInitialSnapshot().getRotX() - (float)rotations.x * (float) (Math.PI / 180.0) * (float)capeBone.rotationMultipliers.x
                     );
                  bone.get()
                     .setRotY(
                        bone.get().getInitialSnapshot().getRotY() - (float)rotations.y * (float) (Math.PI / 180.0) * (float)capeBone.rotationMultipliers.y
                     );
                  bone.get()
                     .setRotZ(
                        bone.get().getInitialSnapshot().getRotZ() - (float)rotations.z * (float) (Math.PI / 180.0) * (float)capeBone.rotationMultipliers.z
                     );
               }
            }
         }
      }
   }

   public void setAllVisible(boolean pVisible) {
      super.setAllVisible(pVisible);
      this.asyncBones.forEach(bone -> this.setBoneVisible(bone.actualBone, pVisible));
   }

   public void prepForRender(
      Entity entity,
      ItemStack stack,
      EquipmentSlot slot,
      HumanoidModel<?> baseModel,
      MultiBufferSource bufferSource,
      float partialTick,
      float limbSwing,
      float limbSwingAmount,
      float netHeadYaw,
      float headPitch
   ) {
      super.prepForRender(entity, stack, slot, baseModel, bufferSource, partialTick, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
      this.animatable = null;
   }

   public void actuallyRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0F, 1.5F, 0.0F);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());
      if (buffer != null) {
         for (GeoBone group : model.topLevelBones()) {
            this.renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
         }
      }

      poseStack.popPose();
   }

   public class AsyncBone {
      @Nullable
      private GeoBone actualBone = null;
      private final String boneName;
      private final EquipmentSlot itemSlot;
      private final Function<HumanoidModel<?>, ModelPart> partToFollow;
      private final Vec3 partOffset;

      public AsyncBone(String boneName, EquipmentSlot itemSlot, Function<HumanoidModel<?>, ModelPart> partToFollow, Vec3 partOffset) {
         this.boneName = boneName;
         this.itemSlot = itemSlot;
         this.partToFollow = partToFollow;
         this.partOffset = partOffset;
      }

      public void grabBone(GeoModel<?> model) {
         this.actualBone = (GeoBone)model.getBone(this.boneName).orElse(null);
      }

      public void applyVisibilityBySlot(EquipmentSlot currentSlot) {
         if (currentSlot == this.itemSlot) {
            TransmogArmorRenderer.this.setBoneVisible(this.actualBone, true);
         } else {
            TransmogArmorRenderer.this.setBoneVisible(this.actualBone, false);
         }
      }

      public void applyVisibilityByPart(HumanoidModel<?> model, ModelPart part) {
         if (part == this.partToFollow.apply(model)) {
            TransmogArmorRenderer.this.setBoneVisible(this.actualBone, true);
         } else {
            TransmogArmorRenderer.this.setBoneVisible(this.actualBone, false);
         }
      }
   }

   public static record CapeBone(String name, Vec3 rotationMultipliers) {
   }
}
