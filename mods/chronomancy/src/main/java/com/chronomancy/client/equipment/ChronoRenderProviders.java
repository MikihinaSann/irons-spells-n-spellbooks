package com.chronomancy.client.equipment;

import com.chronomancy.item.ClocksmithArmorItem;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * Client-only construction sites for chronomancy's geckolib renderers.
 * GeoArmorRenderer extends HumanoidModel (client-only under the Fabric loader),
 * so {@code new} expressions live here while item classes delegate via
 * {@code Object}-typed factories — same pattern as ISS's
 * {@code ClientArmorRenderers}/{@code ArmorRenderProviders}.
 */
@OnlyIn(Dist.CLIENT)
public final class ChronoRenderProviders {
    private ChronoRenderProviders() {
    }

    public static GeoArmorRenderer<?> clocksmith() {
        return new GenericCustomArmorRenderer<>(
                new ClockworkGeoModel<ClocksmithArmorItem>("clocksmith"));
    }

    /** GeoItem render provider for {@code ClockHandItem}. */
    public static GeoRenderProvider clockHand() {
        return new GeoRenderProvider() {
            private ClockHandRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) renderer = new ClockHandRenderer();
                return renderer;
            }
        };
    }
}
