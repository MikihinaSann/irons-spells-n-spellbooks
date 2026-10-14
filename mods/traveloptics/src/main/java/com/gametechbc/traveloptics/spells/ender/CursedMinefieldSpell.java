package com.gametechbc.traveloptics.spells.ender;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Mine_Entity;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class CursedMinefieldSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "cursed_minefield");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(30.0)
      .build();

   public CursedMinefieldSpell() {
      super();
      this.baseManaCost = 10;
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 45;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.CURSED_MINEFIELD_CAST;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.LEVIATHAN_ROAR.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.mine_count", new Object[]{(int)Math.ceil(this.getSpellPower(spellLevel, caster) * 4.0F)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      SphereParticleManager.spawnParticles(level, entity, 5, ParticleHelper.UNSTABLE_ENDER, ParticleDirection.OUTWARD, 6.0);
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      ScreenShake_Entity.ScreenShake(level, entity.position(), 20.0F, 0.01F, 45, 50);
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
      if (!level.isClientSide()) {
         int numEntities = (int)Math.ceil(this.getSpellPower(spellLevel, entity) * 4.0F);

         for (int i = 0; i < numEntities; i++) {
            double randomX = entity.getX() + (level.random.nextDouble() - 0.5) * 40.0;
            double randomY = entity.getY() + level.random.nextDouble() * 3.0;
            double randomZ = entity.getZ() + (level.random.nextDouble() - 0.5) * 40.0;
            new BlockPos((int)randomX, (int)randomY, (int)randomZ);
            EntityType<?> abyssMineType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("cataclysm", "abyss_mine"));
            if (abyssMineType != null && abyssMineType.create(level) instanceof Abyss_Mine_Entity mine) {
               mine.moveTo(randomX, randomY, randomZ);
               mine.setCaster(entity);
               level.addFreshEntity(mine);
            }
         }
      }
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
