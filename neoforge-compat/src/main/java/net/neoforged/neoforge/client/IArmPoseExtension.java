package net.neoforged.neoforge.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge patches {@code HumanoidModel$ArmPose} with a
 * {@code forgeArmPose} field and an {@code applyTransform} method that
 * delegates to it. Fabric mixes both onto the enum; callers go through this
 * interface instead of the (nonexistent) patched method.
 */
public interface IArmPoseExtension {

    /** NeoForge's {@code ArmPose.forgeArmPose} field. */
    @Nullable
    IArmPoseTransformer neoforge$getForgeArmPose();

    /** NeoForge's {@code ArmPose.applyTransform(model, entity, arm)}. */
    @SuppressWarnings("unchecked")
    default <T extends LivingEntity> void neoforge$applyTransform(HumanoidModel<T> model, T entity, HumanoidArm arm) {
        IArmPoseTransformer transformer = neoforge$getForgeArmPose();
        if (transformer != null) {
            transformer.applyTransform(model, entity, arm);
        }
    }
}
