package net.hazen.hazennstuff.Item.HnSUtilities;

import com.google.common.base.Suppliers;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
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
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
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

public class HnSGeckolibArmorItem extends ArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final Supplier<ItemAttributeModifiers> defaultModifiers;

   public HnSGeckolibArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributeContainers) {
      super(material, type, properties);
      this.defaultModifiers = Suppliers.memoize(() -> {
         int i = ((ArmorMaterial)material.value()).getDefense(type);
         float f = ((ArmorMaterial)material.value()).toughness();
         Builder builder = ItemAttributeModifiers.builder();
         EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
         ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
         builder.add(Attributes.ARMOR, new AttributeModifier(resourceLocation, i, Operation.ADD_VALUE), equipmentSlotGroup);
         builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourceLocation, f, Operation.ADD_VALUE), equipmentSlotGroup);
         float resistance = ((ArmorMaterial)material.value()).knockbackResistance();
         if (resistance > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(resourceLocation, resistance, Operation.ADD_VALUE), equipmentSlotGroup);
         }

         for (AttributeContainer holder : attributeContainers) {
            builder.add(holder.attribute(), holder.createModifier(type.getSlot().getName()), equipmentSlotGroup);
         }

         return builder.build();
      });
   }

   public static AttributeContainer[] schoolAttributes(Holder<Attribute> school, int mana, float schoolSpellPower, float spellPower) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADD_VALUE),
         new AttributeContainer(school, schoolSpellPower, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] pureTier(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(school, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] pureTierMulti(Holder<Attribute> school1, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
      };
   }

   public static AttributeContainer[] pureTierTri(Holder<Attribute> school1, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
      };
   }

   public static AttributeContainer[] paragonTier(Holder<Attribute> school) {
      return new AttributeContainer[]{
         new AttributeContainer(school, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] paragonTierMulti(Holder<Attribute> school1, Holder<Attribute> school2) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public static AttributeContainer[] paragonTierTri(Holder<Attribute> school1, Holder<Attribute> school2, Holder<Attribute> school3) {
      return new AttributeContainer[]{
         new AttributeContainer(school1, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school2, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(school3, 0.2F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15F, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return this.defaultModifiers.get();
   }

   public List<Entry> createExtraAttributes() {
      return List.of();
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ItemAttributeModifiers modifiers = super.getDefaultAttributeModifiers(stack);
      Builder builder = ItemAttributeModifiers.builder();

      for (Entry entry : modifiers.modifiers()) {
         builder.add(entry.attribute(), entry.modifier(), entry.slot());
      }

      for (Entry entry : this.createExtraAttributes()) {
         builder.add(entry.attribute(), entry.modifier(), entry.slot());
      }

      return builder.build();
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controler", this::predicate));
   }

   private PlayState predicate(AnimationState<HnSGeckolibArmorItem> itemAnimationState) {
      itemAnimationState.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(HnSArmorRenderProviders.create(this::supplyRenderer));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() {
      return null;
   }
}
