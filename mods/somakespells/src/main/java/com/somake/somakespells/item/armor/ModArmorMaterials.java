package com.somake.somakespells.item.armor;

import com.somake.somakespells.registries.ModItems;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModArmorMaterials {
   public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "somakespells");
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CERANIUM = ARMOR_MATERIALS.register(
      "ceranium",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 6);
            map.put(Type.LEGGINGS, 9);
            map.put(Type.CHESTPLATE, 11);
            map.put(Type.HELMET, 6);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CERANIUM_INGOT.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("somakespells", "ceranium"))),
         4.0F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DARK_METAL = ARMOR_MATERIALS.register(
      "dark_metal",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 7);
            map.put(Type.CHESTPLATE, 10);
            map.put(Type.HELMET, 4);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> BuiltInRegistries.ITEM
            .getOptional(ResourceLocation.parse("born_in_chaos_v1:dark_metal_ingot"))
            .map(xva$0 -> Ingredient.of(new ItemLike[]{xva$0}))
            .orElseGet(() -> Ingredient.of(new ItemLike[]{Items.NETHERITE_INGOT})),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("somakespells", "dark_metal_battle_mage"))),
         5.0F,
         0.1F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AQUAMANCER = ARMOR_MATERIALS.register(
      "aquamancer",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
         }),
         20,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CERANIUM_INGOT.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("somakespells", "aquamancer"))),
         0.0F,
         0.0F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ABYSSIUM = ARMOR_MATERIALS.register(
      "abyssium",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 6);
            map.put(Type.LEGGINGS, 9);
            map.put(Type.CHESTPLATE, 11);
            map.put(Type.HELMET, 6);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)ModItems.CERANIUM_INGOT.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("somakespells", "abyssium"))),
         4.0F,
         0.15F
      )
   );

   public static void register(IEventBus eventBus) {
      ARMOR_MATERIALS.register(eventBus);
   }
}
