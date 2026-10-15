package net.acetheeldritchking.discerning_the_eldritch;

import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public final class DiscerningTheEldritchClientRenderers {
   private DiscerningTheEldritchClientRenderers() {
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

            public static Object ascendedArmorItem() {
      net.minecraft.resources.ResourceLocation layer = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/ascended_armor_glowmask.png"
   );
      return new net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer<>(new net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.AscendedArmorModel(), layer, net.minecraft.client.renderer.RenderType.breezeEyes(layer));
   }

            public static Object capelessAscendedArmorItem() {
      net.minecraft.resources.ResourceLocation layer = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/ascended_armor_glowmask.png"
   );
      return new net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer<>(new net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.CapelessAscendedArmorModel(), layer, net.minecraft.client.renderer.RenderType.breezeEyes(layer));
   }

   public static Object dTEGeckolibArmorItem() {
      return null;
   }

            public static Object geckolibCrimsonStagArmorItem() {
      net.minecraft.resources.ResourceLocation layer = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/crimson_stag_gecko_glowmask.png"
   );
      return new net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer<>(new net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.GeckolibCrimsonStagArmorModel(), layer, net.minecraft.client.renderer.RenderType.breezeEyes(layer));
   }

            public static Object geckolibEldritchWarlockArmorItem() {
      net.minecraft.resources.ResourceLocation layer = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/eldritch_mage_armor_glowmask.png"
   );
      return new net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer<>(new net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.GeckolibEldritchWarlockArmorModel(), layer, net.minecraft.client.renderer.RenderType.breezeEyes(layer));
   }

            public static Object geckolibEldritchWarlockHelmetItem() {
      net.minecraft.resources.ResourceLocation layer = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/eldritch_armor_helmet_glowmask.png"
   );
      return new net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer<>(new net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.GeckolibEldritchWarlockHelmetModel(), layer, net.minecraft.client.renderer.RenderType.breezeEyes(layer));
   }

            public static Object geckolibEldritchWarlockMaskItem() {
      net.minecraft.resources.ResourceLocation layer = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
      "discerning_the_eldritch", "textures/models/armor/geckolib/eldritch_warlock_mask_glowmask.png"
   );
      return new net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer<>(new net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib.GeckolibEldritchWarlockMaskModel(), layer, net.minecraft.client.renderer.RenderType.breezeEyes(layer));
   }
}
