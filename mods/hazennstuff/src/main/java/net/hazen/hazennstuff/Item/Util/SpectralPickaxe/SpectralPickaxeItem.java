package net.hazen.hazennstuff.Item.Util.SpectralPickaxe;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSExtendedWeaponsTiers;
import net.hazen.hazennstuff.Item.Util.HnSToolTiers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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

public class SpectralPickaxeItem extends MagicSwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private final AnimationController<SpectralPickaxeItem> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public SpectralPickaxeItem() {
      super(
         HnSExtendedWeaponsTiers.SPECTRAL_PICKAXE,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .attributes(ExtendedSwordItem.createAttributes(HnSExtendedWeaponsTiers.DIVINE_GREATSWORD))
            .component(DataComponents.TOOL, HnSToolTiers.MITHRIL.createToolProperties(BlockTags.MINEABLE_WITH_PICKAXE)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.SPECTRAL_HAMMER_SPELL, 3)})
      );
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
   }

   public float getDestroySpeed(ItemStack stack, BlockState state) {
      return state.is(BlockTags.MINEABLE_WITH_PICKAXE) ? HnSToolTiers.MITHRIL.getSpeed() : 1.0F;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   private PlayState predicate(AnimationState<SpectralPickaxeItem> event) {
      event.getController().setAnimation(IDLE_ANIMATION);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HazennstuffClientRenderers.itemProvider(HazennstuffClientRenderers::spectralPickaxeItem));
      }
   }

   public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(itemStack);
      if (!affinityData.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.hazennstuff.spellbook_spell_count");
         lines.addAll(i < 0 ? lines.size() : i + 1, affinityData.getDescriptionComponent());
      }
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         itemStack.set(
            ComponentRegistry.AFFINITY_COMPONENT, new AffinityData(Map.of(((AbstractSpell)SpellRegistry.SPECTRAL_HAMMER_SPELL.get()).getSpellResource(), 1))
         );
      }
   }

   @EventBusSubscriber
   public class SpellEvents {
      @SubscribeEvent
      public static void onModifySpellLevel(ModifySpellLevelEvent event) {
         LivingEntity caster = event.getEntity();
         if (caster != null) {
            if (event.getSpell() == SpellRegistry.SPECTRAL_HAMMER_SPELL.get()) {
               ItemStack mainHand = caster.getMainHandItem();
               ItemStack offHand = caster.getOffhandItem();
               boolean usingSpectralPickaxe = mainHand.getItem() instanceof SpectralPickaxeItem || offHand.getItem() instanceof SpectralPickaxeItem;
               if (usingSpectralPickaxe) {
                  event.addLevels(1);
               }
            }
         }
      }
   }
}
