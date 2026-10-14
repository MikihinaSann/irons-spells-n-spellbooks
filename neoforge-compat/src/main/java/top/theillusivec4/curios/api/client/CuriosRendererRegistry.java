package top.theillusivec4.curios.api.client;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Registers Curios-style renderers by wrapping them in Trinkets'
 * {@link TrinketRenderer}.
 */
public final class CuriosRendererRegistry {

    private CuriosRendererRegistry() {
    }

    private static final Map<Item, Supplier<ICurioRenderer>> FACTORIES = new ConcurrentHashMap<>();
    private static final Map<Item, ICurioRenderer> RENDERERS = new ConcurrentHashMap<>();

    public static void register(Item item, Supplier<ICurioRenderer> renderer) {
        FACTORIES.put(item, renderer);
        RENDERERS.remove(item);
        TrinketRendererRegistry.registerRenderer(item, new TrinketRenderer() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public void render(ItemStack stack, SlotReference slotReference,
                               EntityModel<? extends LivingEntity> model,
                               com.mojang.blaze3d.vertex.PoseStack matrices,
                               MultiBufferSource vertexConsumers, int light,
                               LivingEntity entity, float limbAngle, float limbDistance,
                               float tickDelta, float animationProgress,
                               float headYaw, float headPitch) {
                LivingEntity living = entity;
                SlotContext ctx = new SlotContext(slotReference.inventory().getSlotType().getName(),
                        living, slotReference.index(), false, true);
                EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
                EntityRenderer<?> renderer = dispatcher.getRenderer(entity);
                getRenderer(item).ifPresent(curioRenderer -> curioRenderer.render(stack, ctx, matrices,
                        (RenderLayerParent) renderer, vertexConsumers,
                        light, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch));
            }
        });
    }

    /**
     * Curios signature returns an {@link java.util.Optional}; renderers are
     * built lazily on first request (equivalent to Curios' deferred load()).
     */
    public static java.util.Optional<ICurioRenderer> getRenderer(Item item) {
        Supplier<ICurioRenderer> factory = FACTORIES.get(item);
        return factory == null
                ? java.util.Optional.empty()
                : java.util.Optional.of(RENDERERS.computeIfAbsent(item, k -> factory.get()));
    }
}
