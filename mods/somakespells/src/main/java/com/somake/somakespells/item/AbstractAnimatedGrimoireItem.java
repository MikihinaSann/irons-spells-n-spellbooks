package com.somake.somakespells.item;
import com.somake.somakespells.SomakespellsClientRenderers;

import com.somake.somakespells.client.renderer.item.BookStaffRenderer;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.render.ClientStaffItemExtensions;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class AbstractAnimatedGrimoireItem extends StaffItem implements GeoItem {
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private static final RawAnimation AMBIENT_PAGE_FLIP_ANIMATION = RawAnimation.begin().thenPlayAndHold("cast_spell");
   private static final float AMBIENT_ANIMATION_LENGTH_SECONDS = 2.125F;
   private static final int AMBIENT_ANIMATION_TICKS = Math.round(42.5F);
   private static final int MIN_AMBIENT_DELAY_TICKS = 90;
   private static final int MAX_AMBIENT_DELAY_VARIATION = 130;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final Map<ItemDisplayContext, AbstractAnimatedGrimoireItem.AmbientAnimationState> ambientStates = new EnumMap<>(ItemDisplayContext.class);

   protected AbstractAnimatedGrimoireItem(Properties properties) {
      super(properties);
      GeoItem.registerSyncedAnimatable(this);
   }

   public abstract ResourceLocation getGrimoireTexture();

   public boolean isPerspectiveAware() {
      return true;
   }

   public boolean hasCustomRendering() {
      return true;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(SomakespellsClientRenderers.itemProvider(SomakespellsClientRenderers::abstractAnimatedGrimoireItem));
      }
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new ClientStaffItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return GeoRenderProvider.of(AbstractAnimatedGrimoireItem.this).getGeoItemRenderer();
         }
      });
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "main_controller",
            0,
            state -> {
               ItemStack stack = (ItemStack)state.getData(DataTickets.ITEMSTACK);
               ItemDisplayContext perspective = (ItemDisplayContext)state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
               net.minecraft.world.entity.Entity holder = (net.minecraft.world.entity.Entity) state.getData(DataTickets.ENTITY);
               net.minecraft.world.entity.player.Player player = holder instanceof net.minecraft.world.entity.player.Player p ? p : null;
               net.minecraft.world.level.Level level = holder != null ? holder.level() : null;
               if (stack == null || stack.isEmpty() || player == null || level == null) {
                  return state.setAndContinue(IDLE_ANIMATION);
               } else if (perspective == null) {
                  return state.setAndContinue(IDLE_ANIMATION);
               } else {
                  boolean handPerspective = perspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                     || perspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                     || perspective == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                     || perspective == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  if (!handPerspective) {
                     return state.setAndContinue(IDLE_ANIMATION);
                  } else {
                     ItemStack mainHand = player.getMainHandItem();
                     ItemStack offHand = player.getOffhandItem();
                     boolean isHeldGrimoire = stack.getItem() == mainHand.getItem() || stack.getItem() == offHand.getItem();
                     if (!isHeldGrimoire) {
                        return state.setAndContinue(IDLE_ANIMATION);
                     } else {
                        long gameTime = level.getGameTime();
                        AbstractAnimatedGrimoireItem.AmbientAnimationState ambientState = this.ambientStates
                           .computeIfAbsent(perspective, key -> new AbstractAnimatedGrimoireItem.AmbientAnimationState());
                        if (ambientState.nextAnimationStartTick < 0L) {
                           ambientState.nextAnimationStartTick = gameTime + this.computeAmbientDelay(stack, perspective, ambientState.triggerCount);
                        }

                        if (gameTime >= ambientState.nextAnimationStartTick && gameTime > ambientState.activeAnimationEndTick) {
                           ambientState.triggerCount++;
                           ambientState.activeAnimationEndTick = gameTime + AMBIENT_ANIMATION_TICKS;
                           ambientState.nextAnimationStartTick = ambientState.activeAnimationEndTick
                              + this.computeAmbientDelay(stack, perspective, ambientState.triggerCount);
                           state.resetCurrentAnimation();
                        }

                        if (gameTime <= ambientState.activeAnimationEndTick) {
                           if (!state.isCurrentAnimation(AMBIENT_PAGE_FLIP_ANIMATION)) {
                              state.setAnimation(AMBIENT_PAGE_FLIP_ANIMATION);
                           }

                           return PlayState.CONTINUE;
                        } else {
                           return state.setAndContinue(IDLE_ANIMATION);
                        }
                     }
                  }
               }
            }
         )
      );
   }

   private long computeAmbientDelay(ItemStack stack, ItemDisplayContext perspective, int triggerCount) {
      long seed = 1469598103934665603L;
      seed ^= stack.getItem().hashCode();
      seed *= 1099511628211L;
      seed ^= perspective.ordinal();
      seed *= 1099511628211L;
      seed ^= triggerCount * 31L;
      seed *= 1099511628211L;
      return 90 + Math.floorMod(seed, 130);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   private static final class AmbientAnimationState {
      private long nextAnimationStartTick = -1L;
      private long activeAnimationEndTick = -1L;
      private int triggerCount = 0;
   }
}
