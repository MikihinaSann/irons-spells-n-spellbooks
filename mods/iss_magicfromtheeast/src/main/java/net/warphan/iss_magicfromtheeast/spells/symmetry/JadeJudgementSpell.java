package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.warphan.iss_magicfromtheeast.entity.spells.jade_judgement.JadeDao;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

@AutoSpellConfig
public class JadeJudgementSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "jade_judgement");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(60.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public JadeJudgementSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 4;
      this.castTime = 40;
      this.baseManaCost = 65;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.SYMMETRY_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.BEACON_ACTIVATE);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 0.35F, false);
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 spawn = null;
      LivingEntity target = null;
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         target = castTargetingData.getTarget((ServerLevel)level);
         if (target != null) {
            spawn = target.position();
         }
      }

      if (spawn == null) {
         HitResult raycast = Utils.raycastForEntity(level, entity, 32.0F, true, 0.25F);
         if (raycast.getType() == Type.ENTITY) {
            spawn = ((EntityHitResult)raycast).getEntity().position();
            if (((EntityHitResult)raycast).getEntity() instanceof LivingEntity livingEntity) {
               target = livingEntity;
            }
         } else {
            spawn = raycast.getLocation().subtract(entity.getForward().normalize());
         }
      }

      JadeDao jadeDao = new JadeDao(level, entity, target);
      jadeDao.moveTo(this.raiseWithCollision(spawn, 16, level));
      jadeDao.setAirTime(target == null ? 25 : 35);
      jadeDao.setDamage(this.getDamage(spellLevel, entity));
      level.addFreshEntity(jadeDao);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private Vec3 raiseWithCollision(Vec3 start, int block, Level level) {
      for (int i = 0; i < block; i++) {
         Vec3 raise = start.add(0.0, 1.0, 0.0);
         if (!level.getBlockState(BlockPos.containing(raise)).isAir()) {
            break;
         }

         start = raise;
      }

      return start;
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }
}
