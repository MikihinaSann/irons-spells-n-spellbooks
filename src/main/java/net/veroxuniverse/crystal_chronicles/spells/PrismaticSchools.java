package net.veroxuniverse.crystal_chronicles.spells;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

public class PrismaticSchools {
   public static final ResourceLocation PRISMATIC_ID = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "prismatic");
   public static SchoolType PRISMATIC;

   public static void register(IEventBus modEventBus) {
      TagKey<Item> focusTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "prismatic_foci"));
      MutableComponent displayName = Component.translatable("school.crystal_chronicles.prismatic").withStyle(style -> style.withColor(13382604));
      Holder<Attribute> powerAttributeHolder = Attributes.ATTACK_DAMAGE;
      Holder<Attribute> resistanceAttributeHolder = Attributes.ARMOR;
      Holder<SoundEvent> castSoundHolder = Holder.direct(SoundEvents.WARDEN_HEARTBEAT);
      ResourceKey<DamageType> damageTypeKey = DamageTypes.MAGIC;
      PRISMATIC = new SchoolType(
         PRISMATIC_ID, focusTag, displayName, powerAttributeHolder, resistanceAttributeHolder, castSoundHolder, damageTypeKey, false, false
      );
   }
}
