package net.neoforged.neoforge.client.event;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Collects particle provider registrations; drained into FAPI's particle
 * registry at client init.
 */
public class RegisterParticleProvidersEvent extends Event {
    private final List<Runnable> registrations = new ArrayList<>();

    public <T extends net.minecraft.core.particles.ParticleOptions> void registerSpecial(
            ParticleType<T> type, ParticleProvider<T> provider) {
        registrations.add(() -> net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(type, provider));
    }

    public <T extends net.minecraft.core.particles.ParticleOptions> void registerSprite(
            ParticleType<T> type, ParticleProvider.Sprite<T> provider) {
        // NeoForge semantics: the provider creates a TextureSheetParticle and the
        // particle type's sprite set is applied via pickSprite automatically.
        registrations.add(() -> net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance()
                .register(type, spriteSet -> (options, level, x, y, z, xSpeed, ySpeed, zSpeed) -> {
                    net.minecraft.client.particle.Particle particle =
                            provider.createParticle(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
                    ((net.minecraft.client.particle.TextureSheetParticle) particle).pickSprite(spriteSet);
                    return particle;
                }));
    }

    public <T extends net.minecraft.core.particles.ParticleOptions> void registerSpriteSet(
            ParticleType<T> type, SpriteParticleRegistration<T> provider) {
        registrations.add(() -> net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance()
                .register(type, spriteSet -> provider.create(spriteSet)));
    }

    public <T extends net.minecraft.core.particles.ParticleOptions> void register(
            ParticleType<T> type, ParticleProvider<T> provider) {
        registerSpecial(type, provider);
    }

    public void drain() {
        registrations.forEach(Runnable::run);
    }

    @FunctionalInterface
    public interface SpriteParticleRegistration<T extends net.minecraft.core.particles.ParticleOptions> {
        ParticleProvider<T> create(SpriteSet sprites);
    }
}
