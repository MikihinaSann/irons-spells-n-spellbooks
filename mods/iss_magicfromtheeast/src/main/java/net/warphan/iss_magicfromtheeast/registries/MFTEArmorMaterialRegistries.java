package net.warphan.iss_magicfromtheeast.registries;

import io.redspace.ironsspellbooks.util.ModTags;
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
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;

public class MFTEArmorMaterialRegistries {
   private static final DeferredRegister<ArmorMaterial> MFTE_ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "iss_magicfromtheeast");
   public static DeferredHolder<ArmorMaterial, ArmorMaterial> JADE = register(
      "jade",
      makeArmorMap(4, 10, 6, 4, 16),
      40,
      SoundEvents.ARMOR_EQUIP_CHAIN,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)MFTEItemRegistries.REFINED_JADE_INGOT.get()}),
      2.0F,
      0.05F
   );
   public static DeferredHolder<ArmorMaterial, ArmorMaterial> ELEMENTAL_COMMANDER = register(
      "elemental", magicArmorMap(), 40, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ModTags.MITHRIL_INGOT), 0.0F, 0.0F
   );
   public static DeferredHolder<ArmorMaterial, ArmorMaterial> BOOTS_OF_MIST = register(
      "mist", magicArmorMap(), 40, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ModTags.MITHRIL_INGOT), 0.0F, 0.0F
   );

   public static void register(IEventBus eventBus) {
      MFTE_ARMOR_MATERIALS.register(eventBus);
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
      List<Layer> list = List.of(new Layer(ISS_MagicFromTheEast.id(name)));
      return MFTE_ARMOR_MATERIALS.register(
         name, () -> new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, list, toughness, knockbackResistance)
      );
   }

   public static EnumMap<Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots, int body) {
      return (EnumMap<Type, Integer>)Util.make(new EnumMap(Type.class), p_266655_ -> {
         p_266655_.put(Type.BOOTS, boots);
         p_266655_.put(Type.LEGGINGS, leggings);
         p_266655_.put(Type.CHESTPLATE, chestplate);
         p_266655_.put(Type.HELMET, helmet);
         p_266655_.put(Type.BODY, body);
      });
   }

   public static EnumMap<Type, Integer> magicArmorMap() {
      return makeArmorMap(3, 8, 6, 3, 0);
   }
}
