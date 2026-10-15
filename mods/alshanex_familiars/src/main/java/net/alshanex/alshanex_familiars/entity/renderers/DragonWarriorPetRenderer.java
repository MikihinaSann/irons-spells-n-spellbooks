package net.alshanex.alshanex_familiars.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.alshanex.alshanex_familiars.entity.DragonWarriorPetEntity;
import net.alshanex.alshanex_familiars.entity.models.DragonWarriorPetModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DragonWarriorPetRenderer extends HumanoidRenderer<DragonWarriorPetEntity> {
   public DragonWarriorPetRenderer(Context renderManager, DragonWarriorPetModel model) {
      super(renderManager, model);
      this.shadowRadius = 0.3F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public ResourceLocation getTextureLocation(DragonWarriorPetEntity entity) {
      return DragonWarriorPetEntity.textureResource;
   }

   public Vec3 getRenderOffset(DragonWarriorPetEntity entity, float partialTicks) {
      return entity.isOnBed() ? super.getRenderOffset(entity, partialTicks).add(0.0, 0.3, 0.0) : super.getRenderOffset(entity, partialTicks).add(0.0, 0.5, 0.0);
   }

   public void render(DragonWarriorPetEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      SpellRenderingHelper.renderSpellHelper(
         ClientMagicData.getSyncedSpellData((LivingEntity)this.animatable), (LivingEntity)this.animatable, poseStack, bufferSource, partialTick
      );
   }

   public RenderType getRenderType(DragonWarriorPetEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return animatable.isInvisible() ? RenderType.entityTranslucent(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
   }
}
