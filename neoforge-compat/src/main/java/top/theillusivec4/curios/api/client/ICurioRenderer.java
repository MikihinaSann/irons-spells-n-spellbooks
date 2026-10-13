package top.theillusivec4.curios.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

/**
 * Curios-style renderer contract, adapted to Trinkets' render callback.
 */
public interface ICurioRenderer {

    <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack,
                                                                   SlotContext slotContext,
                                                                   PoseStack poseStack,
                                                                   RenderLayerParent<T, M> renderLayerParent,
                                                                   MultiBufferSource renderTypeBuffer,
                                                                   int light,
                                                                   float limbSwing,
                                                                   float limbSwingAmount,
                                                                   float partialTicks,
                                                                   float ageInTicks,
                                                                   float netHeadYaw,
                                                                   float headPitch);
}
