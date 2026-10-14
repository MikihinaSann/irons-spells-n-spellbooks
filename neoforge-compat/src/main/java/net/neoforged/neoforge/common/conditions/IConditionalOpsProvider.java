package net.neoforged.neoforge.common.conditions;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;

/**
 * Injected onto {@code SimpleJsonResourceReloadListener} (Mojmap 1.21 name) to
 * provide NeoForge's patched {@code makeConditionalOps()} method. NeoForge's
 * version returns {@link ConditionalOps} carrying a condition context; ours
 * supplies the empty context (mods can register conditions through it).
 */
public interface IConditionalOpsProvider {
    default ConditionalOps<JsonElement> makeConditionalOps() {
        var server = ServerLifecycleHooks.getCurrentServer();
        RegistryAccess access = server != null ? server.registryAccess() : RegistryAccess.EMPTY;
        return new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, access), ICondition.IContext.EMPTY);
    }
}
