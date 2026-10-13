package net.neoforged.neoforge.internal.fabric.mixin;

import net.neoforged.neoforge.internal.fabric.BuiltinPackSources;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Appends repository sources collected via the NeoForge-style
 * {@code AddPackFindersEvent} onto every {@link PackRepository} (client
 * resource packs and server datapacks alike).
 */
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {
    @Shadow
    @Final
    @Mutable
    private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void iss$addBuiltinSources(RepositorySource[] sources, CallbackInfo ci) {
        List<RepositorySource> extra = BuiltinPackSources.all();
        if (!extra.isEmpty()) {
            Set<RepositorySource> merged = new LinkedHashSet<>(this.sources);
            merged.addAll(extra);
            this.sources = merged;
        }
    }
}
