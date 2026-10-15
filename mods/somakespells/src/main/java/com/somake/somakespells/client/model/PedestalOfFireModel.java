package com.somake.somakespells.client.model;

import com.somake.somakespells.block.entity.PedestalOfFireBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PedestalOfFireModel extends GeoModel<PedestalOfFireBlockEntity> {
   private static final ResourceLocation FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/block/altar_of_fire/altar_of_fire.png");
   private static final ResourceLocation SOUL_FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/block/altar_of_soulfire.png");
   private static final ResourceLocation INFERNAL_FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/block/altar_of_infernalfire.png"
   );

   public ResourceLocation getModelResource(PedestalOfFireBlockEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "geo/altar/pedestal_of_fire.geo.json");
   }

   public ResourceLocation getTextureResource(PedestalOfFireBlockEntity animatable) {
      return switch (animatable.getRitualMode()) {
         case SOUL_FIRE -> SOUL_FIRE_TEXTURE;
         case INFERNAL_FIRE -> INFERNAL_FIRE_TEXTURE;
         default -> FIRE_TEXTURE;
      };
   }

   public ResourceLocation getAnimationResource(PedestalOfFireBlockEntity animatable) {
      return null;
   }
}
