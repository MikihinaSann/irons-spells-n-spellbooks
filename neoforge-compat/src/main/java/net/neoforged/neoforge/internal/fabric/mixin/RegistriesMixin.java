package net.neoforged.neoforge.internal.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

/**
 * NeoForge patches {@link Registries#elementsDirPath} so datapack registries
 * outside the {@code minecraft} namespace resolve their elements under
 * {@code data/<ns>/<registry-namespace>/<registry-path>/}. Without this, the
 * {@code neoforge:biome_modifier} datapack registry would look in
 * {@code data/<ns>/biome_modifier/} instead of NeoForge's
 * {@code data/<ns>/neoforge/biome_modifier/}.
 */
@Mixin(Registries.class)
public abstract class RegistriesMixin {
    @Inject(method = "elementsDirPath", at = @At("HEAD"), cancellable = true)
    private static void neoforge$namespacedElementsDirPath(ResourceKey<?> registryKey,
            CallbackInfoReturnable<String> cir) {
        var location = registryKey.location();
        if (!location.getNamespace().equals(net.minecraft.resources.ResourceLocation.DEFAULT_NAMESPACE)) {
            cir.setReturnValue(location.getNamespace() + "/" + location.getPath());
        }
    }
}
