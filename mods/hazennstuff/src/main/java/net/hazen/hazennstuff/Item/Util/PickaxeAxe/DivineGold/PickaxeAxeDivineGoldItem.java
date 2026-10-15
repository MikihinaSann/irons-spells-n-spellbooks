package net.hazen.hazennstuff.Item.Util.PickaxeAxe.DivineGold;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import java.util.function.Consumer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Item.Util.HnSToolTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.state.BlockState;
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

public class PickaxeAxeDivineGoldItem extends ExtendedSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<PickaxeAxeDivineGoldItem> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public PickaxeAxeDivineGoldItem() {
      super(
         HnSExtendedWeaponsTiers.PICKAXE_AXE,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.PICKAXE_AXE))
            .component(DataComponents.TOOL, HnSToolTiers.MITHRIL.createToolProperties(BlockTags.MINEABLE_WITH_PICKAXE))
      );
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.terraria.description").withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.ITALIC}));
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE);
   }

   public float getDestroySpeed(ItemStack stack, BlockState state) {
      if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
         return HnSToolTiers.MITHRIL.getSpeed();
      } else {
         return state.is(BlockTags.MINEABLE_WITH_AXE) ? HnSToolTiers.MITHRIL.getSpeed() : 1.0F;
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<PickaxeAxeDivineGoldItem> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::pickaxeAxeDivineGoldItem));
      }
   }
}
