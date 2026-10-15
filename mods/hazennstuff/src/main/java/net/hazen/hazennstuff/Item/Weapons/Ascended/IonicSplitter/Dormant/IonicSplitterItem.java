package net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.Dormant;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import java.util.function.Consumer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IonicSplitterItem extends ExtendedSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<IonicSplitterItem> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public IonicSplitterItem() {
      super(
         HnSExtendedWeaponsTiers.IONIC_SPLITTER_DORMANT,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.IONIC_SPLITTER_DORMANT))
      );
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(
         Component.translatable("item.hazennstuff.ionic_splitter_dormant.description")
            .withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD, ChatFormatting.ITALIC})
      );
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<IonicSplitterItem> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::ionicSplitterItem));
      }
   }
}
