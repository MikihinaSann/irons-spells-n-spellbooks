package net.hazen.hazennstuff.Item.Block.Statues.HazelStatue;

import net.hazen.hazennstuff.Item.Block.HnSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HazelStatueBlockEntity extends BlockEntity implements GeoBlockEntity {
   private static final RawAnimation DEPLOY_ANIM = RawAnimation.begin().then("deploy", LoopType.PLAY_ONCE).thenLoop("idle");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HazelStatueBlockEntity(BlockPos pos, BlockState state) {
      super(HnSBlockEntities.HAZEL_STATUE.get(), pos, state);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, this::deployAnimController));
   }

   private PlayState deployAnimController(AnimationState<HazelStatueBlockEntity> state) {
      state.setAnimation(DEPLOY_ANIM);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
