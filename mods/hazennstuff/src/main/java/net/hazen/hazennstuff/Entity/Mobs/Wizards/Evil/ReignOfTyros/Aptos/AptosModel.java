package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aptos;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class AptosModel extends AbstractSpellCastingMobModel {
   public static final ResourceLocation textureResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/aptos.png");
   public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/mobs/aptos.geo.json");
   public static final ResourceLocation animationResource = ResourceLocation.fromNamespaceAndPath(
      "hazennstuff", "animations/entities/mobs/aptos.animation.json"
   );

   public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
      return textureResource;
   }

   public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
      return modelResource;
   }

   public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
      return animationResource;
   }
}
