package net.neoforged.neoforge.client.event;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

/**
 * NeoForge-style shader registration event backed by Fabric's
 * {@link CoreShaderRegistrationCallback}.
 */
public class RegisterShadersEvent extends Event {
    private final CoreShaderRegistrationCallback.RegistrationContext context;

    public RegisterShadersEvent(CoreShaderRegistrationCallback.RegistrationContext context) {
        this.context = context;
    }

    public ResourceProvider getResourceProvider() {
        return net.minecraft.client.Minecraft.getInstance().getResourceManager();
    }

    public void registerShader(net.minecraft.resources.ResourceLocation id,
                               com.mojang.blaze3d.vertex.VertexFormat format,
                               Consumer<ShaderInstance> callback) throws java.io.IOException {
        context.register(id, format, callback::accept);
    }
}
