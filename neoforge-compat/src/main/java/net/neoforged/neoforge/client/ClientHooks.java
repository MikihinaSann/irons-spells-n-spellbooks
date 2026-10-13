package net.neoforged.neoforge.client;

import java.util.function.Supplier;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

/** NeoForge ClientHooks - client helpers; layer definitions drain into Fabric's EntityModelLayerRegistry. */
public class ClientHooks {

    /**
     * Registers a model layer definition. NeoForge queues these into
     * EntityRenderersEvent.RegisterLayerDefinitions; Fabric accepts direct
     * registration at any point before model baking.
     */
    public static void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> supplier) {
        net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
                .registerModelLayer(location, supplier::get);
    }

    private ClientHooks() {
    }
}
