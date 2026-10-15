package net.hazen.hazennstuff.Item.Weapons.Generic.DivineGreatsword;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
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

public class DivineGreatswordItem extends MagicSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<DivineGreatswordItem> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public DivineGreatswordItem() {
      super(
         HnSExtendedWeaponsTiers.DIVINE_GREATSWORD,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.HOLY_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.DIVINE_GREATSWORD)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.DIVINE_SMITE_SPELL, 8)})
      );
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<DivineGreatswordItem> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::divineGreatswordItem));
      }
   }
}
