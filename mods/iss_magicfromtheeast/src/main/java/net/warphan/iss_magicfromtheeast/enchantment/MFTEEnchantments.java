package net.warphan.iss_magicfromtheeast.enchantment;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.Enchantment.Builder;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.warphan.iss_magicfromtheeast.enchantment.enchantment_effects.GhostlyColdEnchantmentEffect;
import net.warphan.iss_magicfromtheeast.enchantment.enchantment_effects.SoulFlameEnchantmentEffect;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEEnchantmentTags;
import net.warphan.iss_magicfromtheeast.util.MFTETags;

public class MFTEEnchantments {
   public static final ResourceKey<Enchantment> SOUL_FLAME = ResourceKey.create(
      Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "soul_flame")
   );
   public static final ResourceKey<Enchantment> GHOSTLY_COLD = ResourceKey.create(
      Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "ghostly_cold")
   );
   public static final ResourceKey<Enchantment> SPIRITUAL_FOCUS = ResourceKey.create(
      Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "spiritual_focus")
   );
   public static final ResourceKey<Enchantment> WISELY_WILL = ResourceKey.create(
      Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "wisely_will")
   );
   public static final ResourceKey<Enchantment> INNER_IMPACT = ResourceKey.create(
      Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "inner_impact")
   );
   public static final ResourceKey<Enchantment> EXPANDING = ResourceKey.create(
      Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "expanding")
   );

   public static void bootstrap(BootstrapContext<Enchantment> context) {
      HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
      HolderGetter<Item> items = context.lookup(Registries.ITEM);
      register(
         context,
         SOUL_FLAME,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(MFTETags.SOULPIERCER),
                  4,
                  1,
                  Enchantment.constantCost(25),
                  Enchantment.constantCost(50),
                  4,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(MFTEEnchantmentTags.SOULPIERCER_EFFECT_EXCLUSIVE))
            .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED, new SoulFlameEnchantmentEffect())
      );
      register(
         context,
         GHOSTLY_COLD,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(MFTETags.SOULPIERCER),
                  4,
                  1,
                  Enchantment.constantCost(25),
                  Enchantment.constantCost(50),
                  4,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(MFTEEnchantmentTags.SOULPIERCER_EFFECT_EXCLUSIVE))
            .withEffect(EnchantmentEffectComponents.PROJECTILE_SPAWNED, new GhostlyColdEnchantmentEffect())
      );
      register(
         context,
         SPIRITUAL_FOCUS,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(MFTETags.SOULPIERCER),
                  10,
                  5,
                  Enchantment.dynamicCost(4, 12),
                  Enchantment.dynamicCost(20, 12),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(MFTEEnchantmentTags.SOULPIERCER_FUNCTION_EXCLUSIVE))
            .withEffect((DataComponentType)MFTEDataComponentRegistries.SOUL_DAMAGE.get(), new AddValue(LevelBasedValue.perLevel(1.0F)))
      );
      register(
         context,
         WISELY_WILL,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(MFTETags.SOULPIERCER),
                  5,
                  3,
                  Enchantment.dynamicCost(3, 9),
                  Enchantment.dynamicCost(18, 9),
                  0,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(MFTEEnchantmentTags.SOULPIERCER_FUNCTION_EXCLUSIVE))
            .withEffect((DataComponentType)MFTEDataComponentRegistries.MANA_USE.get(), new AddValue(LevelBasedValue.perLevel(-15.0F)))
      );
      register(
         context,
         INNER_IMPACT,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(MFTETags.SOUL_MELEE_WEAPON),
                  10,
                  5,
                  Enchantment.dynamicCost(4, 12),
                  Enchantment.dynamicCost(20, 12),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
            .withEffect((DataComponentType)MFTEDataComponentRegistries.SOUL_DAMAGE.get(), new AddValue(LevelBasedValue.perLevel(1.0F)))
      );
      register(
         context,
         EXPANDING,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(MFTETags.AMMO_LOAD_WEAPON),
                  10,
                  5,
                  Enchantment.dynamicCost(5, 10),
                  Enchantment.dynamicCost(15, 10),
                  0,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(EnchantmentEffectComponents.AMMO_USE, new AddValue(LevelBasedValue.perLevel(1.0F)))
      );
   }

   private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Builder builder) {
      registry.register(key, builder.build(key.location()));
   }
}
