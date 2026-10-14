package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.IArmPoseExtension;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge ends {@code poseRightArm}/{@code poseLeftArm} with
 * {@code armPose.applyTransform(this, entity, arm)} so extended poses can
 * adjust the model. Reproduced here for Fabric.
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelArmPoseMixin {

    @Shadow
    public HumanoidModel.ArmPose leftArmPose;

    @Shadow
    public HumanoidModel.ArmPose rightArmPose;

    @Inject(method = "poseRightArm", at = @At("TAIL"))
    private void neoforge$transformRightArm(LivingEntity entity, CallbackInfo ci) {
        neoforge$apply(rightArmPose, entity, HumanoidArm.RIGHT);
    }

    @Inject(method = "poseLeftArm", at = @At("TAIL"))
    private void neoforge$transformLeftArm(LivingEntity entity, CallbackInfo ci) {
        neoforge$apply(leftArmPose, entity, HumanoidArm.LEFT);
    }

    @Unique
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void neoforge$apply(HumanoidModel.ArmPose pose, LivingEntity entity, HumanoidArm arm) {
        if ((Object) pose instanceof IArmPoseExtension ext) {
            IArmPoseTransformer transformer = ext.neoforge$getForgeArmPose();
            if (transformer != null) {
                transformer.applyTransform((HumanoidModel) (Object) this, entity, arm);
            }
        }
    }
}
