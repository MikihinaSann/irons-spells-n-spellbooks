package net.acetheeldritchking.discerning_the_eldritch.entity.spells.blade_of_rancor;

import io.redspace.ironsspellbooks.render.RenderHelper.CustomerRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;

public class BladeOfRancorModel extends GeoModel<BladeOfRancorProjectile> {
   public ResourceLocation getModelResource(BladeOfRancorProjectile bladeOfRancorProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/rancor_blade.geo.json");
   }

   public ResourceLocation getTextureResource(BladeOfRancorProjectile bladeOfRancorProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/rancor_blade/rancor_blade.png");
   }

   public ResourceLocation getAnimationResource(BladeOfRancorProjectile bladeOfRancorProjectile) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "animations/blade_of_rancor.animation.json");
   }

   @Nullable
   public RenderType getRenderType(BladeOfRancorProjectile animatable, ResourceLocation texture) {
      return CustomerRenderType.magic(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/rancor_blade/rancor_blade.png"));
   }
}
