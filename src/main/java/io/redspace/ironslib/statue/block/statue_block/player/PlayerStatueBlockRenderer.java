package io.redspace.ironslib.statue.block.statue_block.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironslib.statue.PlayerStatueModelType;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import io.redspace.ironslib.statue.block.statue_block.StaticModel;
import io.redspace.ironslib.statue.client.StatueTextureHolder;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStatueBlockRenderer implements BlockEntityRenderer<PlayerStatueBlockEntity> {
   StaticModel nullModel = new StaticModel("irons_lib", "sbeve");
   StaticModel statueBaseModel = new StaticModel("irons_lib", "statue_base");
   private static final Map<PlayerStatueModelType, StaticModel> PLAYER_MODELS = Map.of(
      PlayerStatueModelType.WIDE,
      new StaticModel("irons_lib", "player"),
      PlayerStatueModelType.SLIM,
      new StaticModel("irons_lib", "player_slim"),
      PlayerStatueModelType.LEGACY,
      new StaticModel("irons_lib", "player_legacy")
   );

   public PlayerStatueBlockRenderer(Context context) {
   }

   public void render(
      @NotNull PlayerStatueBlockEntity statueBlock,
      float partialTick,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      if (statueBlock.isPrimary()) {
         if (statueBlock.getBlockState().getBlock() instanceof AbstractStatueBlock statue) {
            poseStack.pushPose();
            poseStack.translate((float)statue.xSize * 0.5F, 0.0F, (float)statue.zSize * 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees((Integer)statueBlock.getBlockState().getValue(SkullBlock.ROTATION))));
            this.statueBaseModel.render(poseStack, RenderType::entityCutout, bufferSource, packedLight, packedOverlay);
            float var12 = 0.125F;
            poseStack.translate(0.0F, var12, 0.0F);
            StatueTextureHolder statueTextureHolder = this.resolvePlayerStatue(statueBlock.getStatueData());
            if (statueTextureHolder == StatueTextureManager.NULL) {
               this.nullModel.render(poseStack, RenderType::entityCutoutNoCull, bufferSource, packedLight, packedOverlay);
            } else {
               StaticModel playermodel = PLAYER_MODELS.get(statueTextureHolder.modelType());
               RenderType rendertype = RenderType.entityCutoutNoCull(statueTextureHolder.textureLocation());
               playermodel.setupPose(statueBlock);
               playermodel.render(poseStack, rendertype, bufferSource, packedLight, packedOverlay);
               playermodel.flushPose();
            }

            poseStack.popPose();
         }
      }
   }

   @NotNull
   private StatueTextureHolder resolvePlayerStatue(@Nullable StatueData statueData) {
      return statueData == null ? StatueTextureManager.NULL : StatueTextureManager.lookupUUID(statueData.uuid());
   }

   @NotNull
   public AABB getRenderBoundingBox(@NotNull PlayerStatueBlockEntity blockEntity) {
      return net.neoforged.neoforge.common.util.NeoForgeBounds.INFINITE;
   }
}
