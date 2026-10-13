package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fires {@link LivingChangeTargetEvent} from {@code Mob#setTarget} and
 * {@link FinalizeSpawnEvent} from {@code Mob#finalizeSpawn}.
 */
@Mixin(Mob.class)
public abstract class MobEventDispatchMixin {

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private LivingEntity irons_spellbooks$onSetTarget(LivingEntity target) {
        Mob self = (Mob) (Object) this;
        if (self.level().isClientSide || target == self.getTarget()) {
            return target;
        }
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(self, target,
                LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return self.getTarget();
        }
        return event.getNewAboutToBeSetTarget();
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType spawnType, SpawnGroupData spawnGroupData,
                                                  CallbackInfoReturnable<SpawnGroupData> cir) {
        Mob self = (Mob) (Object) this;
        FinalizeSpawnEvent event = new FinalizeSpawnEvent(self, level, difficulty, spawnType, spawnGroupData);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(spawnGroupData);
        }
    }
}
