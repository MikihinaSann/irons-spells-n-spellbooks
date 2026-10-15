package net.warphan.iss_magicfromtheeast.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.item.weapons.RepeatingCrossbow;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;

@OnlyIn(Dist.CLIENT)
public class MFTEItemProperties {
   public static void addCustomItemProperties() {
      makeCustomBow((Item)MFTEItemRegistries.SOULPIERCER.get());
      makeRepeatingCrossbow((Item)MFTEItemRegistries.REPEATING_CROSSBOW.get());
   }

   private static void makeCustomBow(Item item) {
      ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"), (p_344163_, p_344164_, p_344165_, p_344166_) -> {
         if (p_344165_ == null) {
            return 0.0F;
         } else {
            return p_344165_.getUseItem() != p_344163_ ? 0.0F : (p_344163_.getUseDuration(p_344165_) - p_344165_.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("pulling"),
         (p_174630_, p_174631_, p_174632_, p_174633_) -> p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F
      );
   }

   private static void makeRepeatingCrossbow(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("pull"),
         (itemStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null) {
               return 0.0F;
            } else {
               return RepeatingCrossbow.isCharged(itemStack)
                  ? 0.0F
                  : (float)(itemStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks())
                     / RepeatingCrossbow.getChargeDuration(itemStack, livingEntity);
            }
         }
      );
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("pulling"),
         (itemStack, level, livingEntity, i) -> livingEntity != null
               && livingEntity.isUsingItem()
               && livingEntity.getUseItem() == itemStack
               && !RepeatingCrossbow.isCharged(itemStack)
            ? 1.0F
            : 0.0F
      );
      ItemProperties.register(
         item, ResourceLocation.withDefaultNamespace("charged"), (itemStack, level, livingEntity, i) -> RepeatingCrossbow.isCharged(itemStack) ? 1.0F : 0.0F
      );
   }
}
