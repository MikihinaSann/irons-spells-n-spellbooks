package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.Camera;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link ViewportEvent.ComputeCameraAngles} at the end of {@code Camera.setup}
 * and applies yaw/pitch/roll mutations back onto the camera.
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
}
