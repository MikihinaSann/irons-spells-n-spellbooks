package net.acetheeldritchking.aces_spell_utils.items.example;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtilsClientRenderers;

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

public class ExtendedGeoArmorItem extends ArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final Supplier<ItemAttributeModifiers> defaultModifiers;

   public ExtendedGeoArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, AttributeContainer... attributeContainers) {
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

   public static AttributeContainer[] schoolAttributesWithResistance(
      Holder<Attribute> school, Holder<Attribute> resistSchool, int mana, float schoolSpellPower, float spellPower, float resistSpellPower
   ) {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, mana, Operation.ADD_VALUE),
         new AttributeContainer(school, schoolSpellPower, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(resistSchool, resistSpellPower, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, spellPower, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers() {
      return this.defaultModifiers.get();
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controler", this::predicate));
   }

   private PlayState predicate(AnimationState<ExtendedGeoArmorItem> itemAnimationState) {
      itemAnimationState.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         consumer.accept(AcesSpellUtilsClientRenderers.armorProvider(this::supplyRenderer));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return AcesSpellUtilsClientRenderers.extendedGeoArmorItem(); }
}
