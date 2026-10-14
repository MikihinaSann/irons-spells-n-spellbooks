package net.neoforged.neoforge.internal.fabric.mixin.api;

import net.minecraft.client.model.HumanoidModel;
import net.neoforged.neoforge.client.IArmPoseExtension;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.common.extensions.IEnumExtensionCarrier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * NeoForge patches {@code ArmPose} with a {@code (boolean, IArmPoseTransformer)}
 * constructor and a {@code forgeArmPose} field consumed by
 * {@code HumanoidModel.poseRightArm/poseLeftArm}. Fabric cannot add
 * constructors, so extended constants are created through the vanilla
 * {@code (boolean)} constructor and the transformer is stored here via
 * {@link IEnumExtensionCarrier}.
 */
@Mixin(HumanoidModel.ArmPose.class)
public abstract class ArmPoseApiMixin implements IEnumExtensionCarrier, IArmPoseExtension {

    @Unique
    private IArmPoseTransformer neoforge$forgeArmPose;

    @Nullable
    @Override
    public IArmPoseTransformer neoforge$getForgeArmPose() {
        return neoforge$forgeArmPose;
    }

    @Override
    public void neoforge$setExtraCtorArg(Class<?> declaredType, Object value) {
        if (value instanceof IArmPoseTransformer transformer) {
            neoforge$forgeArmPose = transformer;
        }
    }
}
