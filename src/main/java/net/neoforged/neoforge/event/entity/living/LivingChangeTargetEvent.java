package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a living entity's target changes; mutable target.
 */
public class LivingChangeTargetEvent extends LivingEvent implements ICancellableEvent {
    @Nullable
    private LivingEntity newTarget;
    @Nullable
    private final LivingEntity originalTarget;
    private final ILivingTargetType targetType;

    public LivingChangeTargetEvent(LivingEntity entity, @Nullable LivingEntity originalTarget,
                                   ILivingTargetType targetType) {
        super(entity);
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
        this.targetType = targetType;
    }

    @Nullable
    public LivingEntity getOriginalAboutToBeSetTarget() {
        return originalTarget;
    }

    @Nullable
    public LivingEntity getOriginalTarget() {
        return originalTarget;
    }

    @Nullable
    public LivingEntity getNewTarget() {
        return newTarget;
    }

    @Nullable
    public LivingEntity getNewAboutToBeSetTarget() {
        return newTarget;
    }

    public void setNewAboutToBeSetTarget(@Nullable LivingEntity target) {
        this.newTarget = target;
    }

    public void setNewTarget(@Nullable LivingEntity target) {
        this.newTarget = target;
    }

    public ILivingTargetType getTargetType() {
        return targetType;
    }

    public interface ILivingTargetType {
    }

    public enum LivingTargetType implements ILivingTargetType {
        BRAIN_TARGET,
        MOB_TARGET,
        ENTITY_TARGET
    }
}
