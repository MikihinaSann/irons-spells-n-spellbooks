package net.neoforged.neoforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;

/**
 * NeoForge's DimensionSpecialEffects render hooks — injected onto
 * {@code DimensionSpecialEffects} via Loom {@code injected_interfaces}.
 * Each returns whether vanilla rendering should be skipped.
 */
public interface IDimensionSpecialEffectsExtension {
    default boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix,
                              Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        return false;
    }

    default boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                                 double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return false;
    }

    default boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture,
                                      double camX, double camY, double camZ) {
        return false;
    }

    default boolean tickWeather(ClientLevel level, int ticks, Camera camera) {
        return false;
    }

    /**
     * NeoForge's per-dimension lightmap color adjustment hook — the vanilla
     * default applies no changes.
     */
    default void adjustLightmapColors(net.minecraft.client.multiplayer.ClientLevel level,
            float partialTicks, float skyDarken, float blockLightRedFlicker,
            float skyLight, int minBlockLight, int maxSkyLight, org.joml.Vector3f combined) {
    }
}
