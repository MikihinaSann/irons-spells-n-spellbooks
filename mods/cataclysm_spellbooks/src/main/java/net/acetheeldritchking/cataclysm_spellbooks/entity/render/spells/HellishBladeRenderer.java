package net.acetheeldritchking.cataclysm_spellbooks.entity.render.spells;

import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.hellish_blade.HellishBladeModel;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.hellish_blade.HellishBladeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HellishBladeRenderer extends GeoEntityRenderer<HellishBladeProjectile> {
   public HellishBladeRenderer(Context context) {
      super(context, new HellishBladeModel());
      this.shadowRadius = 0.5F;
   }

   public ResourceLocation getTextureLocation(HellishBladeProjectile animatable) {
      return animatable.getIsSoul()
         ? ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/entity/hellish_blade/hellish_blade_soul.png")
         : ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/entity/hellish_blade/hellish_blade.png");
   }

   public boolean shouldRender(HellishBladeProjectile livingEntity, Frustum camera, double camX, double camY, double camZ) {
      return super.shouldRender(livingEntity, camera, camX, camY, camZ);
   }

   @Nullable
   public RenderType getRenderType(HellishBladeProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.breezeEyes(texture);
   }
}
