package net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T3;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.function.Consumer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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

public class IonicSplitterT3Item extends ExtendedSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<IonicSplitterT3Item> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public IonicSplitterT3Item() {
      super(
         HnSExtendedWeaponsTiers.IONIC_SPLITTER_T3,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.IONIC_SPLITTER_T3))
      );
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (!attacker.level().isClientSide) {
         attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(), HnSSounds.ELECTRIC_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);
      }

      return super.hurtEnemy(stack, target, attacker);
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<IonicSplitterT3Item> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::ionicSplitterT3Item));
      }
   }
}
