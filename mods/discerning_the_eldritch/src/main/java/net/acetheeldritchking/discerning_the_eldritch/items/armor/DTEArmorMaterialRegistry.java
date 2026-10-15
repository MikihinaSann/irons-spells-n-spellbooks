package net.acetheeldritchking.discerning_the_eldritch.items.armor;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTEArmorMaterialRegistry {
   private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "discerning_the_eldritch");
   public static DeferredHolder<ArmorMaterial, ArmorMaterial> ELDRITCH_WARLOCK = register(
      "eldritch_mage_armor",
      warlockArmorMap(),
      25,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistries.CORRUPTED_CLOTH.get()}),
      3.0F,
      0.1F
   );
   public static DeferredHolder<ArmorMaterial, ArmorMaterial> CRIMSON_STAG = register(
      "crimson_stag_armor",
      warlockArmorMap(),
      25,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistries.CORRUPTED_CLOTH.get()}),
      3.0F,
      0.1F
   );

   private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
      String name,
      EnumMap<Type, Integer> defense,
      int enchantmentValue,
      Holder<SoundEvent> equipSound,
      Supplier<Ingredient> repairIngredient,
      float toughness,
      float knockbackResistance
   ) {
      List<Layer> list = List.of(new Layer(DiscerningTheEldritch.id(name)));
      return ARMOR_MATERIALS.register(
         name, () -> new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance)
      );
   }

   public static EnumMap<Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
      return (EnumMap<Type, Integer>)Util.make(new EnumMap(Type.class), typeIntegerEnumMap -> {
         typeIntegerEnumMap.put(Type.HELMET, helmet);
         typeIntegerEnumMap.put(Type.CHESTPLATE, chestplate);
         typeIntegerEnumMap.put(Type.LEGGINGS, leggings);
         typeIntegerEnumMap.put(Type.BOOTS, boots);
      });
   }

   public static EnumMap<Type, Integer> warlockArmorMap() {
      return makeArmorMap(4, 9, 7, 4);
   }

   public static EnumMap<Type, Integer> eldritchKingArmorMap() {
      return makeArmorMap(6, 11, 9, 6);
   }

   public static void register(IEventBus eventBus) {
      ARMOR_MATERIALS.register(eventBus);
   }
}
