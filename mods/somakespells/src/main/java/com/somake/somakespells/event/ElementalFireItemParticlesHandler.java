package com.somake.somakespells.event;

import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.registries.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

@EventBusSubscriber(
   modid = "somakespells",
   value = {Dist.CLIENT},
   bus = Bus.GAME
)
public class ElementalFireItemParticlesHandler {
   @SubscribeEvent
   public static void onEntityTick(Pre event) {
      if (event.getEntity() instanceof ItemEntity itemEntity && itemEntity.level().isClientSide) {
         ItemStack stack = itemEntity.getItem();
         if (isElementalFire(stack)) {
            Level level = itemEntity.level();
            RandomSource random = level.random;
            if (random.nextInt(4) == 0) {
               ParticleOptions particle = getParticle(stack);
               double x = itemEntity.getX() + (random.nextDouble() - 0.5) * 0.18;
               double y = itemEntity.getY() + 0.52 + random.nextDouble() * 0.14;
               double z = itemEntity.getZ() + (random.nextDouble() - 0.5) * 0.18;
               double vx = (random.nextDouble() - 0.5) * 0.01;
               double vy = 0.01 + random.nextDouble() * 0.02;
               double vz = (random.nextDouble() - 0.5) * 0.01;
               level.addParticle(particle, x, y, z, vx, vy, vz);
            }
         }
      }
   }

   private static boolean isElementalFire(ItemStack stack) {
      return stack.getItem() == ModItems.FIRE.get() || stack.getItem() == ModItems.SOUL_FIRE.get() || ModCompatItems.isInfernalFireItem(stack.getItem());
   }

   private static ParticleOptions getParticle(ItemStack stack) {
      if (stack.getItem() == ModItems.SOUL_FIRE.get()) {
         return ParticleTypes.SOUL_FIRE_FLAME;
      } else {
         return ModCompatItems.isInfernalFireItem(stack.getItem())
            ? ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get())
            : ParticleTypes.FLAME;
      }
   }
}
