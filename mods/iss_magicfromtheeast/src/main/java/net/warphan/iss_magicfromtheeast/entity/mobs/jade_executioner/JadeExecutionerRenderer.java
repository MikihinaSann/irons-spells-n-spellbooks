package net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JadeExecutionerRenderer extends GeoEntityRenderer<JadeExecutionerEntity> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_executioner.png");

   public JadeExecutionerRenderer(Context renderManager) {
      super(renderManager, new JadeExecutionerModel());
      this.shadowRadius = 1.25F;
   }

   public ResourceLocation getTextureLocation(JadeExecutionerEntity animatable) {
      return textureLocation;
   }

   public void render(
      JadeExecutionerEntity jadeExecutioner, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
   ) {
      Level level = jadeExecutioner.getCommandSenderWorld();
      Optional<GeoBone> anchor = this.model.getBone("particle_anchor");
      float radius = 0.5F;
      anchor.ifPresent(bone -> {
         Vector3d pos = bone.getWorldPosition();

         for (float i = 0.0F; i < radius; i++) {
            float angle = (float) (Math.PI * 2) / radius * (i + jadeExecutioner.level.random.nextFloat());
            double x = pos.x;
            double z = pos.z;
            double xPos = x + radius * Mth.sin(angle);
            double zPos = z + radius * Mth.cos(angle);
            level.addParticle(ParticleTypes.SCRAPE, xPos, pos.y, zPos, 0.0, 5.0, 0.05);
         }
      });
      super.render(jadeExecutioner, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   protected float getDeathMaxRotation(JadeExecutionerEntity degree) {
      return 0.0F;
   }
}
