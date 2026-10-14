package issprobe;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.extensions.IBakedModelExtension;
import net.neoforged.neoforge.client.extensions.IDimensionSpecialEffectsExtension;
import net.neoforged.neoforge.client.extensions.IGuiGraphicsExtension;
import net.neoforged.neoforge.client.extensions.IKeyMappingExtension;
import net.neoforged.neoforge.client.extensions.common.IBlockEntityRendererExtension;
import net.neoforged.neoforge.client.extensions.common.IScreenExtension;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.common.extensions.IParticleExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client-side canonical-table checks (six client-only rows + the two covered
 * rows). Deferred to CLIENT_STARTED so all client mixins have applied.
 * Aggregates failures and reports a hard FAILURES line — never prints success
 * after a failure. */
public final class IssProdProbeClient implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("iss_prod_probe");
    private static final List<String> FAILURES = new ArrayList<>();

    private static void check(List<String> failures, String name, Class<?> target, Class<?> ext) {
        if (ext.isAssignableFrom(target)) {
            LOG.info("[ISS-PROBE] ok {} -> {}", name, ext.getSimpleName());
        } else {
            failures.add(name + " !instanceof " + ext.getSimpleName());
            LOG.error("[ISS-PROBE] FAIL {} !instanceof {}", name, ext.getSimpleName());
        }
    }

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> client.execute(() -> {
            check(FAILURES, "Particle", Particle.class, IParticleExtension.class);
            check(FAILURES, "KeyMapping", KeyMapping.class, IKeyMappingExtension.class);
            check(FAILURES, "DimensionSpecialEffects", DimensionSpecialEffects.class, IDimensionSpecialEffectsExtension.class);
            check(FAILURES, "AbstractContainerScreen", AbstractContainerScreen.class, IScreenExtension.class);
            check(FAILURES, "BlockEntityRenderer", BlockEntityRenderer.class, IBlockEntityRendererExtension.class);
            check(FAILURES, "BakedModel", BakedModel.class, IBakedModelExtension.class);
            check(FAILURES, "GuiGraphics", GuiGraphics.class, IGuiGraphicsExtension.class);
            check(FAILURES, "ClientPacketListener", ClientPacketListener.class, ICommonPacketListener.class);
            if (FAILURES.isEmpty()) {
                LOG.info("[ISS-PROBE] client ABI check PASSED (0 failures)");
            } else {
                LOG.error("[ISS-PROBE] client ABI check FAILED: {}", FAILURES);
            }
        }));
    }
}
