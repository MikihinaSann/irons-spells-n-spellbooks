package com.chadate.spellelemental.register;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Builder;
import net.minecraft.world.level.block.Block;

public class ModEnchantments {
   public static final ResourceKey<Enchantment> ASTRAL_BLESSING = key("astral_blessing");
   public static final ResourceKey<Enchantment> FLAME_AFFIX = key("flame_affix");

   public static void bootstrap(BootstrapContext<Enchantment> context) {
      HolderGetter<DamageType> holdergetter = context.lookup(Registries.DAMAGE_TYPE);
      HolderGetter<Enchantment> holdergetter1 = context.lookup(Registries.ENCHANTMENT);
      HolderGetter<Item> holdergetter2 = context.lookup(Registries.ITEM);
      HolderGetter<Block> holdergetter3 = context.lookup(Registries.BLOCK);
      register(
         context,
         ASTRAL_BLESSING,
         Enchantment.enchantment(
            Enchantment.definition(
               holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
               5,
               4,
               Enchantment.dynamicCost(15, 5),
               Enchantment.dynamicCost(100, 5),
               2,
               new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
            )
         )
      );
      register(
         context,
         FLAME_AFFIX,
         Enchantment.enchantment(
            Enchantment.definition(
               holdergetter2.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
               5,
               4,
               Enchantment.dynamicCost(15, 5),
               Enchantment.dynamicCost(100, 5),
               2,
               new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
            )
         )
      );
   }

   private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Builder builder) {
      context.register(key, builder.build(key.location()));
   }

   private static ResourceKey<Enchantment> key(String name) {
      return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("spellelemental", name));
   }
}
