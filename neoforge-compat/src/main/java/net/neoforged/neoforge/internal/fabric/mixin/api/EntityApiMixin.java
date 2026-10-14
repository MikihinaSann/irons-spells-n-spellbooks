package net.neoforged.neoforge.internal.fabric.mixin.api;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.NeoForgeFluidTypeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/** Runtime interface bridge: {@code net.minecraft.world.entity.Entity} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev.
 * Also implements NeoForge's drop-capture and per-fluid-type height lookups. */
@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EntityApiMixin implements net.neoforged.neoforge.attachment.IAttachmentHolder, net.neoforged.neoforge.entity.IEntityExtension {

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Unique
    private Collection<ItemEntity> neoforgeCompat$captureDrops;

    @Override
    public Collection<ItemEntity> captureDrops() {
        return neoforgeCompat$captureDrops;
    }

    @Override
    public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
        if (value == null) {
            Collection<ItemEntity> previous = neoforgeCompat$captureDrops;
            neoforgeCompat$captureDrops = null;
            return previous;
        }
        neoforgeCompat$captureDrops = value;
        return value;
    }

    /** NeoForge: while captureDrops is set, spawned item entities collect into the list instead of spawning. */
    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    private void neoforgeCompat$captureDropSpawn(ItemStack stack, float offsetY,
            CallbackInfoReturnable<ItemEntity> cir) {
        if (neoforgeCompat$captureDrops != null) {
            Entity self = (Entity) (Object) this;
            ItemEntity itemEntity = new ItemEntity(self.level(), self.getX(), self.getY() + offsetY, self.getZ(), stack);
            itemEntity.setDefaultPickUpDelay();
            neoforgeCompat$captureDrops.add(itemEntity);
            cir.setReturnValue(itemEntity);
        }
    }

    @Override
    public double getFluidTypeHeight(FluidType type) {
        if (type == null || type == NeoForgeMod.EMPTY_TYPE.value()) {
            return 0;
        }
        for (var entry : fluidHeight.object2DoubleEntrySet()) {
            if (entry.getDoubleValue() <= 0) {
                continue;
            }
            var holders = BuiltInRegistries.FLUID.getTag(entry.getKey());
            if (holders.isPresent() && holders.get().stream()
                    .map(Holder::value)
                    .map(NeoForgeFluidTypeRegistry::getTypeFor)
                    .anyMatch(t -> t == type)) {
                return entry.getDoubleValue();
            }
        }
        return 0;
    }

    @Override
    public FluidType getMaxHeightFluidType() {
        TagKey<Fluid> best = null;
        double bestHeight = 0;
        for (var entry : fluidHeight.object2DoubleEntrySet()) {
            if (entry.getDoubleValue() > bestHeight) {
                best = entry.getKey();
                bestHeight = entry.getDoubleValue();
            }
        }
        if (best == null) {
            return NeoForgeMod.EMPTY_TYPE.value();
        }
        var holders = BuiltInRegistries.FLUID.getTag(best);
        if (holders.isPresent()) {
            return holders.get().stream()
                    .map(Holder::value)
                    .map(NeoForgeFluidTypeRegistry::getTypeFor)
                    .filter(t -> t != null)
                    .findFirst()
                    .orElse(NeoForgeMod.EMPTY_TYPE.value());
        }
        return NeoForgeMod.EMPTY_TYPE.value();
    }
}
