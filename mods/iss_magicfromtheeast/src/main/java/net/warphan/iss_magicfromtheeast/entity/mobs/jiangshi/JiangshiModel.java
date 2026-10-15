package net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;

public class JiangshiModel extends GeoModel<JiangshiEntity> {
   public static final ResourceLocation modelResource = new ResourceLocation("iss_magicfromtheeast", "geo/jiangshi.geo.json");
   public static final ResourceLocation textureResource = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jiangshi.png");
   public static final ResourceLocation animationResource = new ResourceLocation("iss_magicfromtheeast", "animations/jiangshi.animation.json");
   protected TransformStack transformStack = new TransformStack();
   private long lastRendererInstance = -1L;

   public ResourceLocation getModelResource(JiangshiEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureResource(JiangshiEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationResource(JiangshiEntity animatable) {
      return animationResource;
   }

   public void handleAnimations(JiangshiEntity animatable, long instanceId, AnimationState<JiangshiEntity> animationState, float partialTick) {
      AnimatableManager<GeoAnimatable> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
      Double currentTick = (Double)animationState.getData(DataTickets.TICK);
      double currentFrameTime = !(animatable instanceof Entity) && !(animatable instanceof GeoReplacedEntity)
         ? currentTick - manager.getFirstTickTime()
         : currentTick + partialTick;
      boolean isReRender = !manager.isFirstTick() && currentFrameTime == manager.getLastUpdateTime();
      if (!isReRender || instanceId != this.lastRendererInstance) {
         this.lastRendererInstance = instanceId;
         this.transformStack.resetDirty();
         super.handleAnimations(animatable, instanceId, animationState, partialTick);
         this.transformStack.popStack();
      }
   }

   public void setCustomAnimations(JiangshiEntity animatable, long instanceId, AnimationState<JiangshiEntity> animationState) {
      super.setCustomAnimations(animatable, instanceId, animationState);
      if (animatable.shouldBeExtraAnimated()) {
         float partialTick = animationState.getPartialTick();
         GeoBone head = this.getAnimationProcessor().getBone("head");
         GeoBone hair = this.getAnimationProcessor().getBone("hair");
         if (animatable.shouldAlwaysAnimateHead()) {
            this.transformStack
               .pushRotation(
                  head,
                  Mth.lerp(partialTick, -animatable.xRotO, -animatable.getXRot()) * (float) (Math.PI / 180.0),
                  Mth.lerp(
                     partialTick,
                     Mth.wrapDegrees(-animatable.yHeadRotO + animatable.yBodyRotO) * (float) (Math.PI / 180.0),
                     Mth.wrapDegrees(-animatable.yHeadRot + animatable.yBodyRot) * (float) (Math.PI / 180.0)
                  ),
                  0.0F
               );
            this.transformStack.pushRotation(hair, -(Mth.lerp(partialTick, -animatable.xRotO, -animatable.getXRot()) * (float) (Math.PI / 180.0)), 0.0F, 0.0F);
         }
      }
   }
}
