package net.neoforged.neoforge.client.event;

import net.minecraft.client.Camera;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the distance of a detached (third-person) camera is calculated.
 * Allows overriding the distance. Ported from NeoForge 21.1.
 */
public class CalculateDetachedCameraDistanceEvent extends Event {
    private final Camera camera;
    private final boolean cameraFlipped;
    private final float entityScale;

    private float distance;

    @ApiStatus.Internal
    public CalculateDetachedCameraDistanceEvent(Camera camera, boolean cameraFlipped, float entityScale, float distance) {
        this.camera = camera;
        this.cameraFlipped = cameraFlipped;
        this.entityScale = entityScale;
        this.distance = distance;
    }

    /**
     * {@return the camera being positioned}
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * {@return whether the camera is flipped to face the player}
     */
    public boolean isCameraFlipped() {
        return cameraFlipped;
    }

    /**
     * {@return the scaling factor of the camera entity}
     */
    public float getEntityScalingFactor() {
        return entityScale;
    }

    /**
     * {@return the currently-calculated distance}
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Sets the camera distance.
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }
}
