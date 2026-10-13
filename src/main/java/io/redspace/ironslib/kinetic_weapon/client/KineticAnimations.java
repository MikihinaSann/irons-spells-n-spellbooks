package io.redspace.ironslib.kinetic_weapon.client;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.kinetic_weapon.KineticWeapon;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class KineticAnimations {
   private static final Map<ResourceLocation, KineticAnimation> ANIMATIONS = new HashMap<>();

   private KineticAnimations() {
   }

   public static void register(ResourceLocation id, KineticAnimation animation) {
      KineticAnimation previous = ANIMATIONS.put(id, animation);
      if (previous != null) {
         IronsLib.LOGGER.warn("Kinetic animation {} registered twice; keeping the latest", id);
      }
   }

   public static KineticAnimation get(ResourceLocation id) {
      KineticAnimation animation = ANIMATIONS.get(id);
      if (animation == null) {
         IronsLib.LOGGER.warn("No kinetic animation registered as {}; using the spear", id);
         ANIMATIONS.put(id, SpearAnimations.INSTANCE);
         return SpearAnimations.INSTANCE;
      } else {
         return animation;
      }
   }

   @Nullable
   public static KineticAnimation get(ItemStack stack) {
      KineticWeapon kineticWeapon = KineticWeapon.get(stack);
      return kineticWeapon == null ? null : get(kineticWeapon.animation());
   }

   @Nullable
   public static KineticAnimation activeFor(LivingEntity entity, HumanoidArm arm, ItemStack stack) {
      return entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0 && armFor(entity, entity.getUsedItemHand()) == arm ? get(stack) : null;
   }

   public static HumanoidArm armFor(LivingEntity entity, InteractionHand hand) {
      return hand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
   }

   public static float ticksUsingItem(LivingEntity entity, float partialTick) {
      return (float)entity.getTicksUsingItem() + partialTick;
   }

   public static float partialTick() {
      return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
   }

   static {
      register(KineticWeapon.SPEAR_ANIMATION, SpearAnimations.INSTANCE);
   }
}
