package com.gametechbc.traveloptics.api.item;
import com.gametechbc.traveloptics.render.TORenderProviders;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
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
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class GeoArmorItem extends ArmorItem implements GeoItem {
   private final Supplier<ItemAttributeModifiers> defaultModifiers;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GeoArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties, AttributeContainer... attributes) {
      super(pMaterial, pType, pProperties);
      this.defaultModifiers = Suppliers.memoize(() -> {
         int i = ((ArmorMaterial)pMaterial.value()).getDefense(pType);
         float f = ((ArmorMaterial)pMaterial.value()).toughness();
         Builder builder = ItemAttributeModifiers.builder();
         EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(pType.getSlot());
         ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + pType.getName());
         builder.add(Attributes.ARMOR, new AttributeModifier(resourcelocation, i, Operation.ADD_VALUE), equipmentslotgroup);
         builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourcelocation, f, Operation.ADD_VALUE), equipmentslotgroup);
         float f1 = ((ArmorMaterial)pMaterial.value()).knockbackResistance();
         if (f1 > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourcelocation, f1, Operation.ADD_VALUE), equipmentslotgroup);
         }

         for (AttributeContainer holder : attributes) {
            builder.add(holder.attribute(), holder.createModifier(pType.getSlot().getName()), equipmentslotgroup);
         }

         return builder.build();
      });
   }

   public static AttributeContainer[] schoolAttributes(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 125.0, Operation.ADD_VALUE),
         new AttributeContainer(school, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] withManaAttribute(int mana) {
      return new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADD_VALUE)};
   }

   public static AttributeContainer[] withManaAndSpellPowerAttribute(int mana, double spellPower) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return this.defaultModifiers.get();
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 20, this::predicate));
   }

   private PlayState predicate(AnimationState<GeoArmorItem> extendedArmorItemAnimationState) {
      extendedArmorItemAnimationState.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      // Fabric: the provider references client-only types; only create it client-side.
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(com.gametechbc.traveloptics.render.TORenderProviders.createArmorRenderer(this::supplyRenderer));
      }
   }

   /**
    * Fabric: declared {@code Object} return keeps {@code GeoArmorRenderer}/{@code HumanoidModel}
    * out of the class signature (verified eagerly on dedicated servers). The actual
    * value is always a {@code GeoArmorRenderer} produced on the client.
    */
   @OnlyIn(Dist.CLIENT)
   public abstract Object supplyRenderer();
}
