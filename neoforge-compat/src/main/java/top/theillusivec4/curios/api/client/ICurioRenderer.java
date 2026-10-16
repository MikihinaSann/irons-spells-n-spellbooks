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

    /**
     * Copies the body rotations from the entity's renderer model onto the given
     * models. Ported from Curios' {@code ICurioRenderer.followBodyRotations}.
     *
     * @param livingEntity the wearer of the curio
     * @param models       the models to align to body movement
     */
    @SafeVarargs
    static void followBodyRotations(final LivingEntity livingEntity,
            final net.minecraft.client.model.HumanoidModel<LivingEntity>... models) {
        net.minecraft.client.renderer.entity.EntityRenderer<? super LivingEntity> render =
                net.minecraft.client.Minecraft.getInstance().getEntityRenderDispatcher()
                        .getRenderer(livingEntity);
        if (render instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?> livingRenderer) {
            @SuppressWarnings("unchecked")
            net.minecraft.client.model.EntityModel<LivingEntity> entityModel =
                    (net.minecraft.client.model.EntityModel<LivingEntity>)
                            ((net.minecraft.client.renderer.entity.LivingEntityRenderer<LivingEntity, ?>) livingRenderer)
                                    .getModel();
            if (entityModel instanceof net.minecraft.client.model.HumanoidModel<?> bipedModel) {
                for (net.minecraft.client.model.HumanoidModel<LivingEntity> model : models) {
                    @SuppressWarnings("unchecked")
                    net.minecraft.client.model.HumanoidModel<LivingEntity> humanoid =
                            (net.minecraft.client.model.HumanoidModel<LivingEntity>) bipedModel;
                    humanoid.copyPropertiesTo(model);
                }
            }
        }
    }

    /** Curios' {@code translateIfSneaking} — shifts the model down while sneaking. */
    static void translateIfSneaking(final PoseStack poseStack, final LivingEntity livingEntity) {
        if (livingEntity.isCrouching()) {
            poseStack.translate(0.0F, 0.2F, 0.0F);
        }
    }

    /** Curios' {@code rotateIfSneaking} — tilts the model forward while sneaking. */
    static void rotateIfSneaking(final PoseStack poseStack, final LivingEntity livingEntity) {
        if (livingEntity.isCrouching()) {
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F / (float) Math.PI));
        }
    }

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
