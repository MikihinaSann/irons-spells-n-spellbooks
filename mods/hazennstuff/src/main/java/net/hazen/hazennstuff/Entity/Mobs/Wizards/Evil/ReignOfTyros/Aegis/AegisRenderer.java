package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aegis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.entity.mobs.keeper.GeoKeeperGhostLayer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class AegisRenderer extends AbstractSpellCastingMobRenderer {
   public AegisRenderer(Context context) {
      super(context, new AegisModel());
      this.addRenderLayer(new GeoKeeperGhostLayer(this));
      this.shadowRadius = 0.65F;
   }

   public void render(AbstractSpellCastingMob entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
   }

   public void preRender(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      poseStack.scale(1.3F, 1.3F, 1.3F);
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }

   public int getPackedOverlay(AbstractSpellCastingMob animatable, float u, float partialTick) {
      return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(animatable.deathTime > 0));
   }
}
