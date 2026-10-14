package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge allows shaders and other resources to be referenced with a namespaced
 * program name (e.g. "alexscaves:rendertype_ferrouslime_gel"), which vanilla then
 * bakes into a path like "shaders/core/alexscaves:name.json" before calling
 * {@link ResourceLocation#withDefaultNamespace}. Rewrites such paths so the
 * embedded namespace becomes the real namespace.
 */
@Mixin(ResourceLocation.class)
public abstract class ResourceLocationNamespacedPathMixin {

    @Inject(method = "withDefaultNamespace", at = @At("HEAD"), cancellable = true)
    private static void neoforge_compat$rewriteEmbeddedNamespace(String path,
            CallbackInfoReturnable<ResourceLocation> cir) {
        int colon = path.indexOf(':');
        if (colon <= 0) return;
        int slash = path.lastIndexOf('/', colon);
        if (slash < 0) return;
        String namespace = path.substring(slash + 1, colon);
        String realPath = path.substring(0, slash + 1) + path.substring(colon + 1);
        if (ResourceLocation.isValidNamespace(namespace) && ResourceLocation.isValidPath(realPath)) {
            cir.setReturnValue(ResourceLocation.fromNamespaceAndPath(namespace, realPath));
        }
    }
}
