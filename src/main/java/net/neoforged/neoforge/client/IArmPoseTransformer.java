package net.neoforged.neoforge.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

/**
 * Transforms an arm pose — NeoForge client extension used by ISS's
 * {@code ArmPose.IRONS_SPELLBOOKS_STAFF_ARM_POS}.
 */
@FunctionalInterface
public interface IArmPoseTransformer {
    void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm);
}
