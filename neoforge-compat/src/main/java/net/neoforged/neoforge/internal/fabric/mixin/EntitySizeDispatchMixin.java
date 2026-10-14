package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Fires {@link EntityEvent.Size} inside {@code refreshDimensions} — the event
 * may replace the new dimensions before they're applied.
 */
@Mixin(Entity.class)
public abstract class EntitySizeDispatchMixin {
    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    private float eyeHeight;

    @ModifyVariable(method = "refreshDimensions", at = @At("STORE"), ordinal = 1)
    private EntityDimensions neoforgeCompat$entitySize(EntityDimensions newSize) {
        Entity self = (Entity) (Object) this;
        EntityEvent.Size event = new EntityEvent.Size(
                self, this.dimensions, newSize, this.eyeHeight, newSize.eyeHeight());
        NeoForge.EVENT_BUS.post(event);
        return event.getNewSize();
    }
}
