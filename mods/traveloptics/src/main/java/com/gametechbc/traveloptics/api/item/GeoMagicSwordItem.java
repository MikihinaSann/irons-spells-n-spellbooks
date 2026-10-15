package com.gametechbc.traveloptics.api.item;
import com.gametechbc.traveloptics.render.TORenderProviders;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class GeoMagicSwordItem extends ExtendedSwordItem implements GeoItem, IPresetSpellContainer {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   List<SpellData> spellData = null;
   SpellDataRegistryHolder[] spellDataRegistryHolders;

   public GeoMagicSwordItem(Tier pTier, Properties pProperties, SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(pTier, pProperties);
      this.spellDataRegistryHolders = spellDataRegistryHolders;
   }

   public List<SpellData> getSpells() {
      if (this.spellData == null) {
         this.spellData = Arrays.stream(this.spellDataRegistryHolders).<SpellData>map(SpellDataRegistryHolder::getSpellData).toList();
         this.spellDataRegistryHolders = null;
      }

      return this.spellData;
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (!ISpellContainer.isSpellContainer(itemStack)) {
            List<SpellData> spells = this.getSpells();
            ISpellContainerMutable spellContainer = ISpellContainer.create(spells.size(), true, false).mutableCopy();
            spells.forEach(spellData -> spellContainer.addSpell(spellData.getSpell(), spellData.getLevel(), true));
            itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
         }
      }
   }

   /**
    * Fabric: declared {@code Object} return keeps the client-only renderer type out of
    * the class signature (verified eagerly on dedicated servers).
    */
   @OnlyIn(Dist.CLIENT)
   protected abstract Object createRenderer();

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      // Fabric: the provider references client-only types; only create it client-side.
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(com.gametechbc.traveloptics.render.TORenderProviders.createItemRenderer(this::createRenderer));
      }
   }

   private PlayState idlePredicate(AnimationState<GeoMagicSwordItem> event) {
      event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar data) {
      data.add(new AnimationController(this, "idleController", 20, this::idlePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
