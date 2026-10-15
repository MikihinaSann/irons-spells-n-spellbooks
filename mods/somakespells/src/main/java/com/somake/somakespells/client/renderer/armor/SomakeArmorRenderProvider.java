package com.somake.somakespells.client.renderer.armor;

import com.somake.somakespells.item.armor.AbyssiumArmorItem;
import com.somake.somakespells.item.armor.AquamancerArmorItem;
import com.somake.somakespells.item.armor.BaseGeoArmorItem;
import com.somake.somakespells.item.armor.CeraniumArmorItem;
import com.somake.somakespells.item.armor.DarkMetalBattlemageArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class SomakeArmorRenderProvider {
   private SomakeArmorRenderProvider() {
   }

   public static GeoRenderProvider create(final BaseGeoArmorItem armorItem) {
      return new GeoRenderProvider() {
         private GeoArmorRenderer<?> renderer;

         public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
            @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
         ) {
            if (this.renderer == null) {
               this.renderer = SomakeArmorRenderProvider.createRenderer(armorItem);
            }

            if (livingEntity != null && equipmentSlot != null && original != null) {
               Minecraft minecraft = Minecraft.getInstance();
               BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
               float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
               this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original, bufferSource, partialTick, 0.0F, 0.0F, 0.0F, 0.0F);
            }

            return this.renderer;
         }
      };
   }

   private static GeoArmorRenderer<?> createRenderer(BaseGeoArmorItem armorItem) {
      if (armorItem instanceof CeraniumArmorItem) {
         return new CeraniumArmorRenderer();
      } else if (armorItem instanceof AbyssiumArmorItem) {
         return new AbyssiumArmorRenderer();
      } else if (armorItem instanceof AquamancerArmorItem) {
         return new AquamancerArmorRenderer();
      } else if (armorItem instanceof DarkMetalBattlemageArmorItem) {
         return new DarkMetalBattlemageArmorRenderer();
      } else {
         throw new IllegalArgumentException("No GeckoLib armor renderer registered for " + armorItem.getClass().getName());
      }
   }
}
