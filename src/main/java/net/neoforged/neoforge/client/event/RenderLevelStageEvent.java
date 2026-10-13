package net.neoforged.neoforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.bus.api.Event;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Consumer;

public class RenderLevelStageEvent extends Event {
    private final Stage stage;
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f projectionMatrix;
    private final int renderTick;
    private final Camera camera;
    private final Frustum frustum;
    private final DeltaTracker partialTick;

    public RenderLevelStageEvent(Stage stage, LevelRenderer levelRenderer, PoseStack poseStack,
                                 Matrix4f modelViewMatrix, Matrix4f projectionMatrix, int renderTick,
                                 DeltaTracker partialTick, Camera camera, Frustum frustum) {
        this.stage = stage;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack != null ? poseStack : new PoseStack();
        this.modelViewMatrix = modelViewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.camera = camera;
        this.frustum = frustum;
        this.partialTick = partialTick;
    }

    public Stage getStage() {
        return stage;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getModelViewMatrix() {
        return modelViewMatrix;
    }

    public int getRenderTick() {
        return renderTick;
    }

    public Camera getCamera() {
        return camera;
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public float getPartialTick() {
        return partialTick.getGameTimeDeltaPartialTick(true);
    }

    public DeltaTracker getPartialTickTracker() {
        return partialTick;
    }

    /** Stage descriptor; code hooks stage ids that FAPI's WorldRenderEvents support. */
    public static final class Stage {
        private final String name;
        private final RenderType renderType;

        private Stage(String name, RenderType renderType) {
            this.name = name;
            this.renderType = renderType;
        }

        public String name() {
            return name;
        }

        public RenderType getRenderType() {
            return renderType;
        }

        public static Stage fromRenderType(RenderType type) {
            return new Stage("render_type:" + type, type);
        }

        // Stage constants
        public static final Stage AFTER_SKY = new Stage("after_sky", null);
        public static final Stage AFTER_SOLID_BLOCKS = new Stage("after_solid_blocks", RenderType.solid());
        public static final Stage AFTER_CUTOUT_MIPPED_BLOCKS = new Stage("after_cutout_mipped_blocks", RenderType.cutoutMipped());
        public static final Stage AFTER_CUTOUT_BLOCKS = new Stage("after_cutout_blocks", RenderType.cutout());
        public static final Stage AFTER_ENTITIES = new Stage("after_entities", null);
        public static final Stage AFTER_BLOCK_ENTITIES = new Stage("after_block_entities", null);
        public static final Stage AFTER_TRANSLUCENT_BLOCKS = new Stage("after_translucent_blocks", RenderType.translucent());
        public static final Stage AFTER_TRIPWIRE_BLOCKS = new Stage("after_tripwire_blocks", RenderType.tripwire());
        public static final Stage AFTER_PARTICLES = new Stage("after_particles", null);
        public static final Stage AFTER_WEATHER = new Stage("after_weather", null);
        public static final Stage AFTER_LEVEL = new Stage("after_level", null);
    }

    /** Registers a stage→RenderType association; call at bootstrap. */
    public static void registerStage(String name, RenderType type) {
    }
}
