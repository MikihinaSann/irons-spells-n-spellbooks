package com.gametechbc.traveloptics.entity.item.mechanized_wraithblade;

import com.gametechbc.traveloptics.item.bossweapon.mechanized_wraithblade.MechanizedWraithbladeItem;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;
import software.bernie.geckolib.animatable.GeoItem;

@EventBusSubscriber
public class MechanizedWraithbladeAnimation {
   public MechanizedWraithbladeAnimation() {
      super();
   }

   @SubscribeEvent
   public static void onPlayerTick(Pre event) {
      Player player = event.getEntity();
      ItemStack mainHandItem = player.getMainHandItem();
      if (mainHandItem.getItem() instanceof MechanizedWraithbladeItem && player.isUsingItem() && player.getUseItem() == mainHandItem) {
         CustomData.update(DataComponents.CUSTOM_DATA, mainHandItem, tag -> tag.putString("geckolibAnimation", "attack"));
      }
   }

   private static void playAnimation(@Nullable Event event, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() instanceof MechanizedWraithbladeItem) {
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckolibAnimation", "attack"));
         }
      }
   }

   @SubscribeEvent
   public static void getAnimationItems(Post event) {
      String animation = "";
      ItemStack mainhandItem = event.getEntity().getMainHandItem().copy();
      ItemStack offhandItem = event.getEntity().getOffhandItem().copy();
      if (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem) {
         if (mainhandItem.getItem() instanceof MechanizedWraithbladeItem animatable) {
            animation = ((CustomData)mainhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getMainHandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((MechanizedWraithbladeItem)event.getEntity().getMainHandItem().getItem()).externalAnimation = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof MechanizedWraithbladeItem animatablex) {
            animation = ((CustomData)offhandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getString("geckolibAnimation");
            if (!animation.isEmpty()) {
               CustomData.update(DataComponents.CUSTOM_DATA, event.getEntity().getOffhandItem(), tag -> tag.putString("geckolibAnimation", ""));
               if (event.getEntity().level().isClientSide()) {
                  ((MechanizedWraithbladeItem)event.getEntity().getOffhandItem().getItem()).externalAnimation = animation;
               }
            }
         }
      }
   }
}
