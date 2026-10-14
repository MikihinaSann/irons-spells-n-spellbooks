package net.neoforged.neoforge.internal.fabric.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Dispatches {@link EntityRenderersEvent.CreateSkullModels} from
 * {@link SkullBlockRenderer#createSkullRenderers(EntityModelSet)}: mod-registered
 * skull models are merged into the map vanilla built.
 */
@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererModelsMixin {

    @WrapMethod(method = "createSkullRenderers(Lnet/minecraft/client/model/geom/EntityModelSet;)Ljava/util/Map;")
    private static Map<SkullBlock.Type, SkullModelBase> neoforge$createSkullRenderers(EntityModelSet modelSet,
            Operation<Map<SkullBlock.Type, SkullModelBase>> original) {
        Map<SkullBlock.Type, SkullModelBase> map = new HashMap<>(original.call(modelSet));
        var event = new EntityRenderersEvent.CreateSkullModels(modelSet);
        NeoForge.EVENT_BUS.post(event);
        map.putAll(event.getModels());
        return Map.copyOf(map);
    }
}
