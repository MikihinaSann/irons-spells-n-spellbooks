package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Vanilla 1.21.1 renders particles by iterating the static {@code RENDER_ORDER}
 * list — custom {@link ParticleRenderType} instances (NeoForge-style sheets)
 * are queued in {@code particles} but never reached by {@code begin()}, so they
 * silently never draw. NeoForge patches this loop to cover every registered
 * type; we emulate it by appending any render type that actually has particles.
 */
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineRenderOrderMixin {
    @Shadow
    @Final
    private Map<ParticleRenderType, Queue<Particle>> particles;

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ParticleEngine;RENDER_ORDER:Ljava/util/List;", opcode = org.objectweb.asm.Opcodes.GETSTATIC))
    private List<ParticleRenderType> neoforge_compat$withCustomRenderTypes(List<ParticleRenderType> original) {
        List<ParticleRenderType> extended = null;
        for (ParticleRenderType type : this.particles.keySet()) {
            if (!original.contains(type)) {
                if (extended == null) {
                    extended = new ArrayList<>(original);
                }
                extended.add(type);
            }
        }
        return extended == null ? original : extended;
    }
}
