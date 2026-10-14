package com.gametechbc.traveloptics.render;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * Client-only factories for the geckolib {@link GeoRenderProvider}s used by the
 * mod's items. Kept out of the item classes because anonymous classes whose
 * method signatures mention client-only types (BlockEntityWithoutLevelRenderer,
 * HumanoidModel) are verified eagerly and crash dedicated servers.
 */
@OnlyIn(Dist.CLIENT)
public final class TORenderProviders {
   private TORenderProviders() {
   }

   public static GeoRenderProvider createItemRenderer(Supplier<Object> rendererSupplier) {
      return new GeoRenderProvider() {
         private BlockEntityWithoutLevelRenderer renderer;

         public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = (BlockEntityWithoutLevelRenderer)rendererSupplier.get();
            }

            return this.renderer;
         }
      };
   }

   public static GeoRenderProvider createArmorRenderer(Supplier<Object> rendererSupplier) {
      return new GeoRenderProvider() {
         private GeoArmorRenderer<?> renderer;

         public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
            @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
         ) {
            if (this.renderer == null) {
               this.renderer = (GeoArmorRenderer<?>)rendererSupplier.get();
            }

            return this.renderer;
         }
      };
   }
}
