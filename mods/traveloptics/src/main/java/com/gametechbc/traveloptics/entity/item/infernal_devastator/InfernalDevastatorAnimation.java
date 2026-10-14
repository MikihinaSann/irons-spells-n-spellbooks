package com.gametechbc.traveloptics.entity.item.infernal_devastator;

import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelOneItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelThreeItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelTwoItem;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import software.bernie.geckolib.animatable.GeoItem;

@EventBusSubscriber
public class InfernalDevastatorAnimation {
   public InfernalDevastatorAnimation() {
      super();
   }

   public static void executeCanonAnimation(Entity entity, ItemStack itemStack) {
      playCanonAnimation(null, entity, itemStack);
   }

   private static void playCanonAnimation(@Nullable Event event, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "canon"));
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorLevelOneItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "canon"));
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorLevelTwoItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "canon"));
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorLevelThreeItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "canon"));
         }
      }
   }

   public static void playBladeAnimation(Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "blade"));
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorLevelOneItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "blade"));
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorLevelTwoItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "blade"));
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof InfernalDevastatorLevelThreeItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "blade"));
         }
      }
   }

   @SubscribeEvent
   public static void getAnimationItems(Post event) {
      String animation = "";
      ItemStack mainhandItem = event.getEntity().getMainHandItem().copy();
      ItemStack offhandItem = event.getEntity().getOffhandItem().copy();
      if (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem) {
         if (mainhandItem.getItem() instanceof InfernalDevastatorItem animatable) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorItem)event.getEntity().getMainHandItem().getItem()).externalAnimation = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof InfernalDevastatorItem animatablex) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorItem)event.getEntity().getOffhandItem().getItem()).externalAnimation = animation;
               }
            }
         }
      }

      if (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem) {
         if (mainhandItem.getItem() instanceof InfernalDevastatorLevelOneItem animatablexx) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorLevelOneItem)event.getEntity().getMainHandItem().getItem()).externalAnimation = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof InfernalDevastatorLevelOneItem animatablexxx) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorLevelOneItem)event.getEntity().getOffhandItem().getItem()).externalAnimation = animation;
               }
            }
         }
      }

      if (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem) {
         if (mainhandItem.getItem() instanceof InfernalDevastatorLevelTwoItem animatablexxxx) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorLevelTwoItem)event.getEntity().getMainHandItem().getItem()).externalAnimation = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof InfernalDevastatorLevelTwoItem animatablexxxxx) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorLevelTwoItem)event.getEntity().getOffhandItem().getItem()).externalAnimation = animation;
               }
            }
         }
      }

      if (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem) {
         if (mainhandItem.getItem() instanceof InfernalDevastatorLevelThreeItem animatablexxxxxx) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorLevelThreeItem)event.getEntity().getMainHandItem().getItem()).externalAnimation = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof InfernalDevastatorLevelThreeItem animatablexxxxxxx) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((InfernalDevastatorLevelThreeItem)event.getEntity().getOffhandItem().getItem()).externalAnimation = animation;
               }
            }
         }
      }
   }
}
