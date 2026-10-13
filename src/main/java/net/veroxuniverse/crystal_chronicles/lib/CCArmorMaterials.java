package net.veroxuniverse.crystal_chronicles.lib;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
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
import net.neoforged.neoforge.registries.DeferredRegister;
import net.veroxuniverse.crystal_chronicles.registry.CCItems;

public class CCArmorMaterials {
   public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "crystal_chronicles");
   public static final Holder<ArmorMaterial> BLOOD = ARMOR_MATERIALS.register(
      "blood",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.HEMALITE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "blood"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> ENDER = ARMOR_MATERIALS.register(
      "ender",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.VOIDSTONE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "ender"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> LIGHTNING = ARMOR_MATERIALS.register(
      "lightning",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.VOLTITE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "lightning"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> HOLY = ARMOR_MATERIALS.register(
      "holy",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.DIVINITE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "holy"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> NATURE = ARMOR_MATERIALS.register(
      "nature",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.FLORALITE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "nature"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> FIRE = ARMOR_MATERIALS.register(
      "fire",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.VOLCANITE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "fire"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> ICE = ARMOR_MATERIALS.register(
      "ice",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of((ItemLike)CCItems.ICE_SHARD.get()),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "ice"))),
            3.0F,
            0.2F
         )
   );
   public static final Holder<ArmorMaterial> EVOCATION = ARMOR_MATERIALS.register(
      "evocation",
      () -> new ArmorMaterial(
            Util.make(new EnumMap<>(Type.class), map -> {
               map.put(Type.BOOTS, 5);
               map.put(Type.LEGGINGS, 8);
               map.put(Type.CHESTPLATE, 10);
               map.put(Type.HELMET, 5);
            }),
            20,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(Items.TOTEM_OF_UNDYING),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "evocation"))),
            3.0F,
            0.2F
         )
   );

   public static void register(IEventBus eventBus) {
      ARMOR_MATERIALS.register(eventBus);
   }
}
