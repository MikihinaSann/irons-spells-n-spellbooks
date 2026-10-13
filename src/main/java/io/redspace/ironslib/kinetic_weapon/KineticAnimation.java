package io.redspace.ironslib.kinetic_weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface KineticAnimation {
   void firstPersonUse(float var1, PoseStack var2, float var3, HumanoidArm var4, ItemStack var5);

   default void thirdPersonArmPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticksUsingItem, ItemStack itemStack) {
   }

   default void thirdPersonItem(PoseStack poseStack, LivingEntity entity, HumanoidArm arm, float ticksUsingItem, float partialTick, ItemStack itemStack) {
   }
}
