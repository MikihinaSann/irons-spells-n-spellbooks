package net.neoforged.neoforge.common.extensions;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * NeoForge's Particle patches — injected onto Particle via Loom
 * {@code injected_interfaces}.
 */
public interface IParticleExtension {
    /** Current particle position as a vector. */
    default Vec3 getPos() {
        Particle self = (Particle) this;
        return new Vec3(self.x, self.y, self.z);
    }

    /** Render bounding box — NeoForge lets particles opt out of frustum culling. */
    default AABB getRenderBoundingBox(float partialTicks) {
        return ((Particle) this).getBoundingBox();
    }
}
