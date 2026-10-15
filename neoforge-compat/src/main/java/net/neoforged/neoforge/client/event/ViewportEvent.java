package net.neoforged.neoforge.client.event;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class ViewportEvent extends Event {
    private final Camera camera;
    private final float partialTick;

    public ViewportEvent(Camera camera, float partialTick) {
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTick() {
        return partialTick;
    }

    /** Compute camera orientation info (FOV etc). */
    public static class ComputeCameraAngles extends ViewportEvent {
        private float yaw;
        private float pitch;
        private float roll;

        public ComputeCameraAngles(Camera camera, float yaw, float pitch, float roll, float partialTick) {
            super(camera, partialTick);
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getRoll() {
            return roll;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }
    }

    /** Field of view computation. */
    public static class ComputeFov extends ViewportEvent implements ICancellableEvent {
        private double fov;
        private final boolean usedConfiguredFov;

        public ComputeFov(Camera camera, float partialTick, double fov, boolean usedConfiguredFov) {
            super(camera, partialTick);
            this.fov = fov;
            this.usedConfiguredFov = usedConfiguredFov;
        }

        public double getFOV() {
            return fov;
        }

        public void setFOV(double fov) {
            this.fov = fov;
        }

        public boolean usedConfiguredFov() {
            return usedConfiguredFov;
        }
    }

    /** Fog color computation. */
    public static class ComputeFogColor extends ViewportEvent {
        private float red;
        private float green;
        private float blue;

        public ComputeFogColor(Camera camera, float partialTick, float red, float green, float blue) {
            super(camera, partialTick);
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public float getRed() {
            return red;
        }

        public void setRed(float red) {
            this.red = red;
        }

        public float getGreen() {
            return green;
        }

        public void setGreen(float green) {
            this.green = green;
        }

        public float getBlue() {
            return blue;
        }

        public void setBlue(float blue) {
            this.blue = blue;
        }
    }

    /** Fog density/shape. */
    public static class RenderFog extends ViewportEvent implements ICancellableEvent {
        private final FogRenderer.FogMode type;
        private float nearPlaneDistance;
        private float farPlaneDistance;
        private final com.mojang.blaze3d.shaders.FogShape fogShape;

        public RenderFog(FogRenderer.FogMode type, Camera camera, float partialTick,
                         float nearPlaneDistance, float farPlaneDistance, com.mojang.blaze3d.shaders.FogShape fogShape) {
            super(camera, partialTick);
            this.type = type;
            this.nearPlaneDistance = nearPlaneDistance;
            this.farPlaneDistance = farPlaneDistance;
            this.fogShape = fogShape;
        }

        public FogRenderer.FogMode getType() {
            return type;
        }

        /** NeoForge name for {@link #getType()}. */
        public FogRenderer.FogMode getMode() {
            return type;
        }

        /** NeoForge exposes the LevelRenderer here; lazily fetch the vanilla one. */
        public net.minecraft.client.renderer.LevelRenderer getRenderer() {
            return net.minecraft.client.Minecraft.getInstance().levelRenderer;
        }

        public float getNearPlaneDistance() {
            return nearPlaneDistance;
        }

        public void setNearPlaneDistance(float distance) {
            this.nearPlaneDistance = distance;
        }

        public float getFarPlaneDistance() {
            return farPlaneDistance;
        }

        public void setFarPlaneDistance(float distance) {
            this.farPlaneDistance = distance;
        }

        public com.mojang.blaze3d.shaders.FogShape getFogShape() {
            return fogShape;
        }

        public void scaleNearPlaneDistance(float factor) {
            this.nearPlaneDistance *= factor;
        }

        public void scaleFarPlaneDistance(float factor) {
            this.farPlaneDistance *= factor;
        }
    }
}
