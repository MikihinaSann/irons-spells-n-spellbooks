package io.redspace.ironsspellbooks.mixin.fabric;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redspace.ironsspellbooks.render.SeparateTransformsModel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Swaps {@link SeparateTransformsModel} for its perspective-specific delegate
 * at the single point where the item model is resolved for rendering.
 */
@Mixin(ItemRenderer.class)
public class ItemRendererSeparateTransformsMixin {

    @ModifyExpressionValue(
            method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel irons_spellbooks$applyPerspective(BakedModel original,
                                                       net.minecraft.world.entity.LivingEntity entity,
                                                       net.minecraft.world.item.ItemStack stack,
                                                       ItemDisplayContext context) {
        return original instanceof SeparateTransformsModel stm ? stm.getModelForContext(context) : original;
    }
}
