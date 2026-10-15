package com.somake.somakespells.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModLootModifiers;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class SpellScrollLootModifier extends LootModifier {
   public static final MapCodec<SpellScrollLootModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> codecStart(instance)
         .and(ResourceLocation.CODEC.fieldOf("entity_id").forGetter(modifier -> modifier.entityId))
         .and(ResourceLocation.CODEC.fieldOf("spell_id").forGetter(modifier -> modifier.spellId))
         .and(Codec.INT.optionalFieldOf("spell_level", 1).forGetter(modifier -> modifier.spellLevel))
         .and(Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(modifier -> modifier.chance))
         .apply(instance, SpellScrollLootModifier::new)
   );
   private final ResourceLocation entityId;
   private final ResourceLocation spellId;
   private final int spellLevel;
   private final float chance;

   public SpellScrollLootModifier(LootItemCondition[] conditions, ResourceLocation entityId, ResourceLocation spellId, int spellLevel, float chance) {
      super(conditions);
      this.entityId = entityId;
      this.spellId = spellId;
      this.spellLevel = Math.max(1, spellLevel);
      this.chance = Math.max(0.0F, Math.min(1.0F, chance));
   }

   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      if (context.hasParam(LootContextParams.DAMAGE_SOURCE)
         && context.hasParam(LootContextParams.THIS_ENTITY)
         && ((Entity)context.getParam(LootContextParams.THIS_ENTITY)).getType().builtInRegistryHolder().key().location().equals(this.entityId)
         && context.getRandom().nextFloat() < this.chance) {
         AbstractSpell spell = SpellRegistry.getSpell(this.spellId);
         if (spell != SpellRegistry.none()) {
            ItemStack scroll = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
            ISpellContainer.createScrollContainer(spell, Math.min(this.spellLevel, spell.getMaxLevel()), scroll);
            generatedLoot.add(scroll);
         }
      }

      return generatedLoot;
   }

   public MapCodec<? extends IGlobalLootModifier> codec() {
      return (MapCodec<? extends IGlobalLootModifier>)ModLootModifiers.SPELL_SCROLL.get();
   }
}
