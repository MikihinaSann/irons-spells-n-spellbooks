package com.gametechbc.traveloptics.spells.eldritch;

import com.gametechbc.traveloptics.api.utils.CurioUtils;
import com.gametechbc.traveloptics.effects.reversal.ReversalEffectInstance;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.gametechbc.traveloptics.util.TOTags;
import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class ReversalSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "reversal");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(3.0)
      .build();

   public ReversalSpell() {
      super();
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TOSounds.REVERSAL.get());
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.REVERSAL;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      boolean hasCurio = CurioUtils.getWearingCurio(entity, (Item)TOItems.NIGHTSTALKERS_BAND.get());
      if (!hasCurio && !entity.getMainHandItem().is(TOTags.CAN_CAST_REVERSAL)) {
         if (entity instanceof Player player && !level.isClientSide()) {
            player.displayClientMessage(Component.translatable("spell.traveloptics.reversal.warning").withStyle(ChatFormatting.RED), true);
         }

         return false;
      } else {
         return true;
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float damageMultiplier = this.calculateDamageMultiplier(spellLevel, entity);
      ReversalEffectInstance reversalEffectInstance = new ReversalEffectInstance(TOEffects.REVERSAL, 8, 0, true, false, false, damageMultiplier);
      entity.addEffect(reversalEffectInstance);
      MagicManager.spawnParticles(
         level,
         (ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.5F,
         entity.getZ(),
         30,
         entity.getBbWidth() * 0.5F,
         entity.getBbHeight() * 0.5F,
         entity.getBbWidth() * 0.5F,
         0.03,
         false
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float calculateDamageMultiplier(int spellLevel, LivingEntity caster) {
      return 1.0F + this.getSpellPower(spellLevel, caster) * 0.25F;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float damageMultiplier = this.calculateDamageMultiplier(spellLevel, caster);
      return List.of(
         Component.translatable("ui.traveloptics.reversal_damage_multiplier", new Object[]{String.format("%.1f", damageMultiplier)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
