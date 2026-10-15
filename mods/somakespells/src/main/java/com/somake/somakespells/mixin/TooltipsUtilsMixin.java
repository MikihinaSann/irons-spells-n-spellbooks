package com.somake.somakespells.mixin;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TooltipsUtils.class})
public abstract class TooltipsUtilsMixin {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<ResourceLocation> LOGGED_TOOLTIP_FAILURES = ConcurrentHashMap.newKeySet();

   @Redirect(
      method = {"formatScrollTooltip"},
      at = @At(
         value = "INVOKE",
         target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getUniqueInfo(ILnet/minecraft/world/entity/LivingEntity;)Ljava/util/List;"
      ),
      remap = false
   )
   private static List<MutableComponent> somake$safeScrollUniqueInfo(AbstractSpell spell, int spellLevel, LivingEntity caster) {
      return getSafeUniqueInfo(spell, spellLevel, caster);
   }

   @Redirect(
      method = {"formatActiveSpellTooltip"},
      at = @At(
         value = "INVOKE",
         target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getUniqueInfo(ILnet/minecraft/world/entity/LivingEntity;)Ljava/util/List;"
      ),
      remap = false
   )
   private static List<MutableComponent> somake$safeActiveUniqueInfo(AbstractSpell spell, int spellLevel, LivingEntity caster) {
      return getSafeUniqueInfo(spell, spellLevel, caster);
   }

   private static List<MutableComponent> getSafeUniqueInfo(AbstractSpell spell, int spellLevel, LivingEntity caster) {
      try {
         return spell.getUniqueInfo(spellLevel, caster);
      } catch (RuntimeException | LinkageError var5) {
         ResourceLocation spellId = spell.getSpellResource();
         if (LOGGED_TOOLTIP_FAILURES.add(spellId)) {
            LOGGER.warn("Failed to build unique tooltip info for spell {}. Falling back to reduced tooltip output.", spellId, var5);
         }

         return List.of();
      }
   }
}
