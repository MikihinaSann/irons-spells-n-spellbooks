package net.neoforged.neoforge.common.conditions;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;

/**
 * Injected onto {@code SimpleJsonResourceReloadListener} (Mojmap 1.21 name) to
 * provide NeoForge's patched {@code makeConditionalOps()} method. NeoForge's
 * real implementation also adds {@code neoforge:conditions} handling — ours
 * returns plain registry ops since the conditions system is not ported.
 */
public interface IConditionalOpsProvider {
    default RegistryOps<JsonElement> makeConditionalOps() {
        var server = ServerLifecycleHooks.getCurrentServer();
        RegistryAccess access = server != null ? server.registryAccess() : RegistryAccess.EMPTY;
        return RegistryOps.create(JsonOps.INSTANCE, access);
    }
}
