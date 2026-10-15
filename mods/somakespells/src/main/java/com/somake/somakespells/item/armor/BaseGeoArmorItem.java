package com.somake.somakespells.item.armor;

import com.somake.somakespells.client.renderer.armor.SomakeArmorRenderProvider;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class BaseGeoArmorItem extends ArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BaseGeoArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
      super(material, type, applyArmorDefaults(material, type, properties));
   }

   private static Properties applyArmorDefaults(Holder<ArmorMaterial> material, Type type, Properties properties) {
      int durability = switch (type) {
         case HELMET -> 407;
         case CHESTPLATE -> 592;
         case LEGGINGS -> 555;
         case BOOTS -> 481;
         default -> 400;
      };
      return properties.durability(durability);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
consumer.accept(SomakeArmorRenderProvider.create(this));
      }
   }

   private PlayState predicate(AnimationState<BaseGeoArmorItem> event) {
      return PlayState.STOP;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
