package net.hazen.hazennstuff.Item.Block.Statues.HazelStatue;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class HazelStatueModel extends DefaultedBlockGeoModel<HazelStatueBlockEntity> {
   public HazelStatueModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(HazelStatueBlockEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/block/statue/hazel_statue.geo.json");
   }

   public ResourceLocation getTextureResource(HazelStatueBlockEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/block/statue/hazel_statue.png");
   }

   public ResourceLocation getAnimationResource(HazelStatueBlockEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/block/hazel_statue.animation.json");
   }
}
