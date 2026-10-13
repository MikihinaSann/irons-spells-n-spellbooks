package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.ModifyCustomSpawnersEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Fires {@link ModifyCustomSpawnersEvent} at the end of {@code ServerLevel}
 * construction (letting subscribers append custom spawners) and
 * {@link LevelEvent.Load} once the level exists.
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelDispatchMixin {
    @Shadow
    @Final
    @Mutable
    private List<CustomSpawner> customSpawners;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void irons_spellbooks$onLevelInit(CallbackInfo ci) {
        var self = (ServerLevel) (Object) this;
        // vanilla's list is immutable — give the event a mutable copy and write back
        var spawners = new java.util.ArrayList<>(this.customSpawners);
        var event = new ModifyCustomSpawnersEvent(self, spawners);
        NeoForge.EVENT_BUS.post(event);
        this.customSpawners = java.util.List.copyOf(spawners);
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(self));
    }
}
