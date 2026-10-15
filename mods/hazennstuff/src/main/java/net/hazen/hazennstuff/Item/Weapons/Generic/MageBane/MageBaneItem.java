package net.hazen.hazennstuff.Item.Weapons.Generic.MageBane;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.function.Consumer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Rarity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MageBaneItem extends ExtendedSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<MageBaneItem> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public MageBaneItem() {
      super(
         HnSExtendedWeaponsTiers.MAGE_BANE,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.MAGE_BANE))
      );
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<MageBaneItem> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::mageBaneItem));
      }
   }
}
