package net.neoforged.neoforge.internal.fabric.mixin.api;

import org.spongepowered.asm.mixin.Mixin;

/** Runtime interface bridge (client): {@code net.minecraft.client.particle.Particle} implements the extension(s) listed
 * in fabric.mod.json loom:injected_interfaces, which only applies in dev. */
@Mixin(net.minecraft.client.particle.Particle.class)
public abstract class ParticleApiMixin implements net.neoforged.neoforge.common.extensions.IParticleExtension {
}
