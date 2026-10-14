package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Camera;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link ViewportEvent.ComputeCameraAngles} at the end of {@code Camera.setup}
 * and applies yaw/pitch/roll mutations back onto the camera. Also fires
 * {@link CalculateDetachedCameraDistanceEvent} inside the detached-camera zoom
 * call, matching NeoForge's ClientHooks.getDetachedCameraDistance placement.
 */
@Mixin(Camera.class)
public abstract class CameraAnglesMixin {
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Shadow
    private Quaternionf rotation;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "setup", at = @At("RETURN"))
    private void irons_spellbooks$cameraAngles(CallbackInfo ci) {
        Camera self = (Camera) (Object) this;
        ViewportEvent.ComputeCameraAngles event = new ViewportEvent.ComputeCameraAngles(
                self, this.yRot, this.xRot, 0.0f,
                net.minecraft.client.Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        if (event.getYaw() != this.yRot || event.getPitch() != this.xRot) {
            this.setRotation(event.getYaw(), event.getPitch());
        }
        if (event.getRoll() != 0.0f) {
            this.rotation.mul(new Quaternionf().rotateZ(org.joml.Math.toRadians(event.getRoll())));
        }
    }

    // NeoForge: getMaxZoom is fed by ClientHooks.getDetachedCameraDistance;
    // the event result replaces the requested distance before the raycast.
    @WrapOperation(method = "setup", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float irons_spellbooks$detachedCameraDistance(Camera self, float startingDistance,
            Operation<Float> original, net.minecraft.world.level.BlockGetter area,
            net.minecraft.world.entity.Entity entity, boolean detached, boolean mirrored, float partialTick) {
        var event = new CalculateDetachedCameraDistanceEvent(self, mirrored, 1.0f, startingDistance);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        return original.call(self, event.getDistance());
    }
}
