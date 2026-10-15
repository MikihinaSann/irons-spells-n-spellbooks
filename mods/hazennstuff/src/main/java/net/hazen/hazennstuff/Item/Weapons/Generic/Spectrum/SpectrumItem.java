package net.hazen.hazennstuff.Item.Weapons.Generic.Spectrum;
import net.minecraft.Util;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.awt.Color;
import java.util.function.Consumer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Rarity.HnSRarities;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
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

public class SpectrumItem extends ExtendedSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<SpectrumItem> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public SpectrumItem() {
      super(
         HnSExtendedWeaponsTiers.SPECTRUM,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.RADIANCE_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.SPECTRUM))
      );
   }

   public Component getName(ItemStack stack) {
      String baseName = super.getName(stack).getString();
      MutableComponent rainbowName = Component.literal("");
      long ticks = Util.getMillis() / 50L;

      for (int i = 0; i < baseName.length(); i++) {
         float hue = (i * 40 + (float)ticks * 2.25F) % 360.0F / 360.0F;
         int rgb = Color.HSBtoRGB(hue, 1.0F, 1.0F);
         String hex = String.format("#%06X", 16777215 & rgb);
         TextColor color = (TextColor)TextColor.parseColor(hex).getOrThrow();
         rainbowName = rainbowName.append(Component.literal(String.valueOf(baseName.charAt(i))).withStyle(style -> style.withColor(color)));
      }

      return rainbowName;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<SpectrumItem> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::spectrumItem));
      }
   }
}