package net.neoforged.neoforge.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge FluidType — a descriptor for a fluid's physical/rendering behavior.
 */
public class FluidType {
    public static final int BUCKET_VOLUME = 1000;
    public static final int BLOCK_SIZE = 1000;

    private final String descriptionId;
    private final int lightLevel;
    private final int density;
    private final int temperature;
    private final int viscosity;
    private final boolean canSwim;
    private final boolean canDrown;
    private final boolean canPushEntity;
    private final boolean canHydrate;
    private final boolean canConvertToSource;
    private final boolean supportsBoating;
    private final boolean canExtinguish;
    private final PathType pathType;
    private final PathType adjacentPathType;
    private final int rarity;
    private final int slopeFindDistance;
    private final int levelDecreasePerBlock;
    private final float explosionResistance;
    private final float motionScale;
    private final boolean fallDistanceModifier;
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final ResourceLocation overlayTexture;
    @Nullable
    private final ResourceLocation renderOverlayTexture;
    private final java.util.Map<net.neoforged.neoforge.common.SoundAction, net.minecraft.sounds.SoundEvent> sounds;

    public FluidType(Properties properties) {
        this.descriptionId = properties.descriptionId;
        this.lightLevel = properties.lightLevel;
        this.density = properties.density;
        this.temperature = properties.temperature;
        this.viscosity = properties.viscosity;
        this.canSwim = properties.canSwim;
        this.canDrown = properties.canDrown;
        this.canPushEntity = properties.canPushEntity;
        this.canHydrate = properties.canHydrate;
        this.canConvertToSource = properties.canConvertToSource;
        this.supportsBoating = properties.supportsBoating;
        this.canExtinguish = properties.canExtinguish;
        this.pathType = properties.pathType;
        this.adjacentPathType = properties.adjacentPathType;
        this.rarity = properties.rarity;
        this.slopeFindDistance = properties.slopeFindDistance;
        this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
        this.explosionResistance = properties.explosionResistance;
        this.motionScale = properties.motionScale;
        this.fallDistanceModifier = properties.fallDistanceModifier;
        this.stillTexture = properties.stillTexture;
        this.flowingTexture = properties.flowingTexture;
        this.overlayTexture = properties.overlayTexture;
        this.renderOverlayTexture = properties.renderOverlayTexture;
        this.sounds = properties.sounds;
    }

    /**
     * The sound registered for the given {@link SoundAction} (bucket
     * fill/empty, vaporize), or null when unset.
     */
    @Nullable
    public net.minecraft.sounds.SoundEvent getSound(net.neoforged.neoforge.common.SoundAction action) {
        return sounds.get(action);
    }

    /**
     * NeoForge's contextual sound lookup — the positional variant used for
     * vaporize/bucket sounds at a location.
     */
    @Nullable
    public net.minecraft.sounds.SoundEvent getSound(@Nullable net.minecraft.world.entity.player.Player player,
            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.neoforged.neoforge.common.SoundAction action) {
        return getSound(action);
    }

    /** NeoForge: whether this fluid vaporizes on placement (e.g. in the nether). */
    public boolean isVaporizedOnPlacement(net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos pos, FluidStack stack) {
        return false;
    }

    /** NeoForge: side effect run when this fluid vaporizes on placement. */
    public void onVaporize(@Nullable net.minecraft.world.entity.player.Player player,
            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, FluidStack stack) {
    }

    public final String getDescriptionId() {
        return this.descriptionId != null ? this.descriptionId
                : "fluid_type." + getRegistryName().replace(':', '.');
    }

    private String getRegistryName() {
        var key = NeoForgeFluidTypeRegistry.getKeyFor(this);
        return key != null ? key.toString() : "unknown";
    }

    public String getDescriptionId(FluidStack stack) {
        return getDescriptionId();
    }

    public Component getDescription() {
        return Component.translatable(getDescriptionId());
    }

    public Component getDescription(FluidStack stack) {
        return Component.translatable(getDescriptionId(stack));
    }

    public int getLightLevel(FluidStack stack) {
        return lightLevel;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public int getDensity() {
        return density;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getViscosity() {
        return viscosity;
    }

    public boolean canSwimIn(Entity entity) {
        return canSwim;
    }

    /**
     * NeoForge's swim check for entities in this fluid type.
     */
    public boolean canSwim(net.minecraft.world.entity.Entity entity) {
        return canSwim;
    }

    public boolean canDrownIn(LivingEntity entity) {
        return canDrown;
    }

    public boolean canPushEntity(Entity entity) {
        return canPushEntity;
    }

    public boolean canHydrate(Entity entity) {
        return canHydrate;
    }

    public boolean supportsBoating(net.minecraft.world.entity.vehicle.Boat boat) {
        return supportsBoating;
    }

    // --- Client-facing lookups (NeoForge routes these through
    // IClientFluidTypeExtensions) ---

    public net.minecraft.resources.ResourceLocation getStillTexture(net.minecraft.world.level.material.FluidState state,
            net.minecraft.world.level.BlockAndTintGetter getter, net.minecraft.core.BlockPos pos) {
        return net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(this)
                .getStillTexture(state, getter, pos);
    }

    public net.minecraft.resources.ResourceLocation getStillTexture(FluidStack stack,
            net.minecraft.world.level.BlockAndTintGetter getter, net.minecraft.core.BlockPos pos) {
        return net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(this)
                .getStillTexture(stack);
    }

    public net.minecraft.resources.ResourceLocation getFlowingTexture(net.minecraft.world.level.material.FluidState state,
            net.minecraft.world.level.BlockAndTintGetter getter, net.minecraft.core.BlockPos pos) {
        return net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(this)
                .getFlowingTexture(state, getter, pos);
    }

    public int getTintColor(net.minecraft.world.level.material.FluidState state,
            net.minecraft.world.level.BlockAndTintGetter getter, net.minecraft.core.BlockPos pos) {
        return net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(this)
                .getTintColor(state, getter, pos);
    }

    public int getTintColor(FluidStack stack) {
        return net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(this)
                .getTintColor(stack);
    }

    public net.minecraft.resources.ResourceLocation getOverlayTexture(net.minecraft.world.level.material.FluidState state,
            net.minecraft.world.level.BlockAndTintGetter getter, net.minecraft.core.BlockPos pos) {
        return net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(this)
                .getOverlayTexture(state, getter, pos);
    }

    public boolean isLighterThanAir() {
        return density < 0;
    }

    public boolean isLighterThanAir(FluidStack stack) {
        return isLighterThanAir();
    }

    public boolean canDrownIn(net.minecraft.world.entity.LivingEntity entity, FluidStack stack) {
        return canDrown;
    }

    public boolean canSwimIn(net.minecraft.world.entity.Entity entity, FluidStack stack) {
        return canSwim;
    }

    public boolean canHydrate(net.minecraft.world.entity.Entity entity, FluidStack stack) {
        return canHydrate;
    }

    public boolean canPushEntity(net.minecraft.world.entity.Entity entity, FluidStack stack) {
        return canPushEntity;
    }

    public boolean canConvertToSource(FluidStack stack) {
        return false;
    }

    public void setItemMovement(net.minecraft.world.entity.item.ItemEntity entity) {
    }

    public boolean canExtinguish(Entity entity) {
        return canExtinguish;
    }

    public PathType getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable net.minecraft.world.entity.Mob mob, boolean canFluidLog) {
        return pathType;
    }

    public PathType getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable net.minecraft.world.entity.Mob mob, PathType originalType) {
        return adjacentPathType;
    }

    public int getRarity() {
        return rarity;
    }

    public int getSlopeFindDistance(LevelReader level) {
        return slopeFindDistance;
    }

    public int getDropOff(LevelReader level) {
        return levelDecreasePerBlock;
    }

    public float getExplosionResistance() {
        return explosionResistance;
    }

    public double motionScale(Entity entity) {
        return motionScale;
    }

    public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
        return canConvertToSource;
    }

    public float getFallDistanceModifier(Entity entity) {
        return fallDistanceModifier ? 0.5f : 1.0f;
    }

    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        return false;
    }

    public boolean canBePlacedInLevel(BlockAndTintGetter level, BlockPos pos, FluidState state) {
        return true;
    }

    @Nullable
    public ItemStack getBucket(FluidStack stack) {
        return null;
    }

    public static Properties create() {
        return new Properties();
    }

    public static class Properties {
        public static Properties create() {
            return new Properties();
        }

        private String descriptionId;
        private int lightLevel = 0;
        private int density = 1000;
        private int temperature = 300;
        private int viscosity = 1000;
        private boolean canSwim = true;
        private boolean canDrown = true;
        private boolean canPushEntity = true;
        private boolean canHydrate = false;
        private boolean canConvertToSource = false;
        private boolean supportsBoating = false;
        private boolean canExtinguish = false;
        private PathType pathType = PathType.BLOCKED;
        private PathType adjacentPathType = null;
        private int rarity = net.minecraft.world.item.Rarity.COMMON.ordinal();
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 100.0F;
        private float motionScale = 0.014f;
        private boolean fallDistanceModifier = true;
        private ResourceLocation stillTexture = ResourceLocation.withDefaultNamespace("block/water_still");
        private ResourceLocation flowingTexture = ResourceLocation.withDefaultNamespace("block/water_flow");
        private ResourceLocation overlayTexture;
        private ResourceLocation renderOverlayTexture;
        private final java.util.Map<net.neoforged.neoforge.common.SoundAction, net.minecraft.sounds.SoundEvent> sounds = new java.util.HashMap<>();

        public Properties descriptionId(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Properties lightLevel(int lightLevel) {
            this.lightLevel = lightLevel;
            return this;
        }

        public Properties density(int density) {
            this.density = density;
            return this;
        }

        public Properties temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Properties viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public Properties canSwim(boolean canSwim) {
            this.canSwim = canSwim;
            return this;
        }

        public Properties canDrown(boolean canDrown) {
            this.canDrown = canDrown;
            return this;
        }

        public Properties canPushEntity(boolean canPushEntity) {
            this.canPushEntity = canPushEntity;
            return this;
        }

        public Properties canHydrate(boolean canHydrate) {
            this.canHydrate = canHydrate;
            return this;
        }

        public Properties canConvertToSource(boolean canConvertToSource) {
            this.canConvertToSource = canConvertToSource;
            return this;
        }

        public Properties supportsBoating(boolean supportsBoating) {
            this.supportsBoating = supportsBoating;
            return this;
        }

        public Properties canExtinguish(boolean canExtinguish) {
            this.canExtinguish = canExtinguish;
            return this;
        }

        public Properties pathType(PathType pathType) {
            this.pathType = pathType;
            return this;
        }

        public Properties adjacentPathType(PathType adjacentPathType) {
            this.adjacentPathType = adjacentPathType;
            return this;
        }

        public Properties rarity(net.minecraft.world.item.Rarity rarity) {
            this.rarity = rarity.ordinal();
            return this;
        }

        public Properties slopeFindDistance(int slopeFindDistance) {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        public Properties fallDistanceModifier(float modifier) {
            this.fallDistanceModifier = modifier != 1.0f;
            return this;
        }

        public Properties canExtinguishSource(boolean canExtinguish) {
            this.canExtinguish = canExtinguish;
            return this;
        }

        public Properties motionScale(double motionScale) {
            this.motionScale = (float) motionScale;
            return this;
        }

        public Properties stillTexture(ResourceLocation stillTexture) {
            this.stillTexture = stillTexture;
            return this;
        }

        public Properties flowingTexture(ResourceLocation flowingTexture) {
            this.flowingTexture = flowingTexture;
            return this;
        }

        public Properties overlayTexture(ResourceLocation overlayTexture) {
            this.overlayTexture = overlayTexture;
            return this;
        }

        public Properties renderOverlayTexture(ResourceLocation renderOverlayTexture) {
            this.renderOverlayTexture = renderOverlayTexture;
            return this;
        }

        public Properties sound(net.neoforged.neoforge.common.SoundAction action, net.minecraft.sounds.SoundEvent sound) {
            this.sounds.put(action, sound);
            return this;
        }
    }

    /**
     * NeoForge's per-fluid-type client-extension hook — invoked once during
     * client init so fluid types can provide {@link IClientFluidTypeExtensions}.
     */
    public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
    }
}
