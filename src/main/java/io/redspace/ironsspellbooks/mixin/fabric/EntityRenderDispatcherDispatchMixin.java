package io.redspace.ironsspellbooks.mixin.fabric;

import io.redspace.ironsspellbooks.IronsSpellbooksFabric;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Fires {@link EntityRenderersEvent.AddLayers} whenever the dispatcher's
 * renderer tables are (re)built. Deduped on the map instance so the ctor +
 * reload paths don't double-register layers onto the same renderers.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherDispatchMixin {
    @Shadow
    public Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Shadow
    private Map<PlayerSkin.Model, EntityRenderer<? extends net.minecraft.world.entity.player.Player>> playerRenderers;

    private static Map<?, ?> irons_spellbooks$lastRenderers;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void irons_spellbooks$fireAddLayers() {
        if (renderers == null || renderers == irons_spellbooks$lastRenderers) {
            return;
        }
        irons_spellbooks$lastRenderers = renderers;
        IronsSpellbooksFabric.MOD_BUS.post(new EntityRenderersEvent.AddLayers(renderers,
                (Map) playerRenderers));
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void irons_spellbooks$initTail(CallbackInfo ci) {
        irons_spellbooks$fireAddLayers();
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void irons_spellbooks$reloadTail(CallbackInfo ci) {
        irons_spellbooks$fireAddLayers();
    }
}
