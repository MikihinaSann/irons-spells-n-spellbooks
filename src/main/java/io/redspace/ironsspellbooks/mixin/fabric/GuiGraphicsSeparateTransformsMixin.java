package io.redspace.ironsspellbooks.mixin.fabric;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redspace.ironsspellbooks.render.SeparateTransformsModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Inventory/GUI item rendering resolves the baked model inside
 * {@code GuiGraphics.renderItem} (never passing through
 * {@code ItemRenderer.renderStatic}), so the perspective swap from
 * {@link ItemRendererSeparateTransformsMixin} does not apply there. Swap in the
 * {@code gui} perspective at the point the model is fetched.
 */
@Mixin(GuiGraphics.class)
public class GuiGraphicsSeparateTransformsMixin {

    @ModifyExpressionValue(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel irons_spellbooks$guiPerspective(BakedModel original) {
        if (original instanceof SeparateTransformsModel stm) {
            return stm.getModelForContext(ItemDisplayContext.GUI);
        }
        return original;
    }
}
