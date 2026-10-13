package io.redspace.ironsspellbooks.render;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Supplier;

/**
 * Client-only factory for the geckolib armor {@link GeoRenderProvider}. Kept out of
 * {@code ExtendedArmorItem} because anonymous classes whose method signatures mention
 * client-only types (HumanoidModel) are verified eagerly and crash dedicated servers.
 */
@OnlyIn(Dist.CLIENT)
public final class ArmorRenderProviders {
    private ArmorRenderProviders() {
    }

    public static GeoRenderProvider create(Supplier<Object> rendererSupplier) {
        return new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> net.minecraft.client.model.HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable net.minecraft.client.model.HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = (GeoArmorRenderer<?>) rendererSupplier.get();
                }
                return this.renderer;
            }
        };
    }
}
