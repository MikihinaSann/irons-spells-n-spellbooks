package com.snackpirate.aeromancy;

import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public final class AeroAdditionsClientRenderers {
   private AeroAdditionsClientRenderers() {
   }

   public static GeoRenderProvider itemProvider(Supplier<Object> supplier) {
      return new GeoRenderProvider() {
         private net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer;

         @Override
         public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = (net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer) supplier.get();
            }
            return this.renderer;
         }
      };
   }

   public static GeoRenderProvider armorProvider(Supplier<Object> supplier) {
      return new GeoRenderProvider() {
         private software.bernie.geckolib.renderer.GeoArmorRenderer<?> renderer;

         @Override
         public <T extends LivingEntity> net.minecraft.client.model.HumanoidModel<?> getGeoArmorRenderer(
            @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable net.minecraft.client.model.HumanoidModel<T> original
         ) {
            if (this.renderer == null) {
               this.renderer = (software.bernie.geckolib.renderer.GeoArmorRenderer<?>) supplier.get();
            }
            return this.renderer;
         }
      };
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
         public static Object windmakerRobeItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new com.snackpirate.aeromancy.entity.armor.WindmakerArmorModel());
   }
}
