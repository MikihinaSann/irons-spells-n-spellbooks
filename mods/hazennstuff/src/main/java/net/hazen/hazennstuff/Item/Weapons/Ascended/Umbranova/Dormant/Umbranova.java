package net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.Dormant;
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

public class Umbranova extends ExtendedSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<Umbranova> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public Umbranova() {
      super(
         HnSExtendedWeaponsTiers.UMBRANOVA_DORMANT,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.ENDER_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.UMBRANOVA_DORMANT))
      );
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.animationController);
   }

   private PlayState predicate(AnimationState<Umbranova> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::umbranova));
      }
   }
}
