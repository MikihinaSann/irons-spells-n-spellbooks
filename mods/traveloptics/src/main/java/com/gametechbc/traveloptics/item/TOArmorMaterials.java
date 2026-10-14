package com.gametechbc.traveloptics.item;

import com.gametechbc.traveloptics.TravelopticsMod;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
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

public class TOArmorMaterials {
   private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "irons_spellbooks");
   public static DeferredHolder<ArmorMaterial, ArmorMaterial> DEEPLING_MAGE = register(
      "deepling_mage",
      makeArmorMap(0, 0, 0, 0),
      15,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MAGIC_CLOTH.get()}),
      0.0F,
      0.0F
   );

   public TOArmorMaterials() {
      super();
   }

   public static void register(IEventBus eventBus) {
      ARMOR_MATERIALS.register(eventBus);
   }

   private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
      String name,
      EnumMap<Type, Integer> defense,
      int enchantmentValue,
      Holder<SoundEvent> equipSound,
      Supplier<Ingredient> repairIngredient,
      float toughness,
      float knockbackResistance
   ) {
      List<Layer> list = List.of(new Layer(TravelopticsMod.id(name)));
      return ARMOR_MATERIALS.register(
         name, () -> new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance)
      );
   }

   public static EnumMap<Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
      return (EnumMap<Type, Integer>)Util.make(new EnumMap(Type.class), p_266655_ -> {
         p_266655_.put(Type.BOOTS, boots);
         p_266655_.put(Type.LEGGINGS, leggings);
         p_266655_.put(Type.CHESTPLATE, chestplate);
         p_266655_.put(Type.HELMET, helmet);
      });
   }

   public static EnumMap<Type, Integer> schoolArmorMap() {
      return makeArmorMap(3, 8, 6, 3);
   }
}
