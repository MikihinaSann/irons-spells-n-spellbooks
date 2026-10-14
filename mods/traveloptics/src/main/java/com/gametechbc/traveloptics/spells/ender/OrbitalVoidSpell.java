package com.gametechbc.traveloptics.spells.ender;

import com.gametechbc.traveloptics.init.TOEffects;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Orb_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class OrbitalVoidSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "orbital_void");
   private int baseEffectLevel = 1;
   private int effectLevelIncreaseRate = 1;
   private final double trackingRange = 20.0;
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(25.0)
      .build();

   public OrbitalVoidSpell() {
      super();
      this.baseManaCost = 40;
      this.manaCostPerLevel = 40;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
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
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SPIT_FINISH_ANIMATION;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.WARDEN_SONIC_CHARGE);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.LEVIATHAN_STUN_ROAR.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
      if (!level.isClientSide()) {
         float getDamage = this.getDamage(spellLevel, entity);
         int orbCount = this.getOrbCount(spellLevel);
         int effectLevel = this.baseEffectLevel + (spellLevel - 1) * this.effectLevelIncreaseRate;
         if (entity instanceof Player player) {
            player.addEffect(new MobEffectInstance(TOEffects.ORBITAL_VOID_EFFECT, 200, effectLevel - 1));
         }

         double angleBetween = (Math.PI * 2) / orbCount;

         for (int i = 0; i < orbCount; i++) {
            double angle = angleBetween * i;
            double offsetX = Math.sin(angle) * 6.0;
            double offsetZ = Math.cos(angle) * 6.0;
            double motionScale = 3.0;
            Vec3 motion = new Vec3(offsetX, 0.0, offsetZ).normalize().scale(motionScale);
            EntityType<?> entityType = (EntityType<?>)EntityType.byString("cataclysm:abyss_orb").orElse(null);
            if (entityType != null) {
               Projectile projectile = (Projectile)entityType.create(level);
               if (projectile != null) {
                  projectile.moveTo(entity.getX(), entity.getY() + 1.5, entity.getZ(), 0.0F, 0.0F);
                  projectile.setDeltaMovement(motion.x(), motion.y(), motion.z());
                  LivingEntity target = this.findNearestTarget(level, entity);
                  if (target != null && projectile instanceof Abyss_Orb_Entity abyssOrb) {
                     abyssOrb.setTracking(true);
                     abyssOrb.setDamage(getDamage);
                     abyssOrb.setOwner(entity);

                     try {
                        Field finalTargetField = Abyss_Orb_Entity.class.getDeclaredField("finalTarget");
                        finalTargetField.setAccessible(true);
                        finalTargetField.set(abyssOrb, target);
                     } catch (IllegalAccessException | NoSuchFieldException var26) {
                        var26.printStackTrace();
                     }
                  }

                  level.addFreshEntity(projectile);
                  CameraShakeManager.addCameraShake(new CameraShakeData(28, entity.position(), 15.0F));
               }
            }
         }
      }
   }

   private LivingEntity findNearestTarget(Level level, LivingEntity caster) {
      AABB boundingBox = new AABB(
         caster.getX() - 20.0, caster.getY() - 20.0, caster.getZ() - 20.0, caster.getX() + 20.0, caster.getY() + 20.0, caster.getZ() + 20.0
      );
      List<LivingEntity> possibleTargets = level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> {
         if (entity == caster) {
            return false;
         } else {
            return caster.isAlliedTo(entity) ? false : !(entity instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == caster);
         }
      });
      return possibleTargets.isEmpty() ? null : possibleTargets.stream().min(Comparator.comparingDouble(caster::distanceToSqr)).orElse(null);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 2.5F + this.getSpellPower(spellLevel, caster) * 2.0F;
   }

   private int getOrbCount(int spellLevel) {
      return spellLevel * 3;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float getDamage = this.getDamage(spellLevel, caster);
      float getOrbCount = this.getOrbCount(spellLevel);
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(getDamage, 2)}),
         Component.translatable("ui.traveloptics.abyssal_orb_count", new Object[]{Utils.stringTruncation(getOrbCount, 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
