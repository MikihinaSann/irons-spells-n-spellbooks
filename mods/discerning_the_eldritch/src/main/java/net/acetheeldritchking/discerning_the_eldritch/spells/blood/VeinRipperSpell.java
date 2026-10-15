package net.acetheeldritchking.discerning_the_eldritch.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.TraceParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.particle.VeinRipperVerticalParticleOptions;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class VeinRipperSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "vein_ripper");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public VeinRipperSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 30;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.MAGIC_SWORD_BLOOD_CAST.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_VERTICAL_UPSWING_ANIMATION;
   }

   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      super.onClientCast(level, spellLevel, entity, castData);
      entity.setYBodyRot(entity.getYRot());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float distance = 7 + spellLevel;
      Vec3 forward = entity.getForward();
      Vec3 end = Utils.raycastForBlock(level, entity.getEyePosition(), entity.getEyePosition().add(forward.scale(distance)), Fluid.NONE).getLocation();
      AABB hit = entity.getHitbox().expandTowards(end.subtract(entity.getEyePosition())).inflate(2.0);
      List<Entity> targetedEntities = level.getEntities(
         entity,
         hit,
         e -> !e.isSpectator()
            && e instanceof LivingEntity
            && e.getBoundingBox().getCenter().subtract(entity.getBoundingBox().getCenter()).normalize().dot(entity.getForward()) >= 0.85
      );
      targetedEntities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(entity)));
      if (!targetedEntities.isEmpty() && targetedEntities.getFirst().distanceToSqr(entity) < distance * distance) {
         Entity closest = targetedEntities.getFirst();
         float radius = 4.5F;
         AABB aoe = AABB.ofSize(closest.getBoundingBox().getCenter(), radius, radius + 1.0F, radius).move(forward.scale(radius / 2.0F));
         end = aoe.getCenter().add(end).scale(0.5);
         List<Entity> damagedEntities = level.getEntities(entity, aoe);
         SpellDamageSource damageSource = this.getDamageSource(entity);

         for (Entity target : damagedEntities) {
            if (target.isAlive()
               && target.isPickable()
               && Utils.hasLineOfSight(level, entity.getEyePosition(), target.getBoundingBox().getCenter(), true)
               && DamageSources.applyDamage(target, this.getDamage(spellLevel, entity), damageSource)) {
               MagicManager.spawnParticles(
                  level,
                  ParticleHelper.BLOOD,
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5F,
                  target.getZ(),
                  15,
                  target.getBbWidth() * 0.5F,
                  target.getBbHeight() * 0.5F,
                  target.getBbWidth() * 0.5F,
                  0.25,
                  false
               );
               EnchantmentHelper.doPostAttackEffects((ServerLevel)level, target, damageSource);
               Vec3 kb = target.position().subtract(entity.position()).normalize().add(0.0, 0.15, 0.0).normalize();
               kb.scale(Utils.random.nextIntBetweenInclusive(70, 100) / 100.0F * Utils.clampedKnockbackResistanceFactor(target, 0.2F, 1.0F) * 0.1F);
               target.setDeltaMovement(target.getDeltaMovement().add(kb));
               target.hurtMarked = true;
               if (target instanceof LivingEntity livingTarget) {
                  livingTarget.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.BLOOD_ROT_EFFECT, 60, 0, true, true, true));
               }
            }
         }
      }

      Vec3 rayVec = end.subtract(entity.getEyePosition());
      Vec3 impulse = rayVec.scale(0.16666667F).add(0.0, 0.1, 0.0);
      entity.setDeltaMovement(entity.getDeltaMovement().scale(0.1).add(impulse));
      entity.hurtMarked = true;
      entity.addEffect(new MobEffectInstance(MobEffectRegistry.FALL_DAMAGE_IMMUNITY, 20, 0, false, false, true));
      forward = impulse.normalize();
      Vec3 up = new Vec3(0.0, 1.0, 0.0);
      if (forward.dot(up) > 0.999) {
         up = new Vec3(1.0, 0.0, 0.0);
      }

      Vec3 right = up.cross(forward);
      Vec3 particlePos = end.subtract(forward.scale(3.0)).add(right.scale(-0.3));
      MagicManager.spawnParticles(
         level,
         new VeinRipperVerticalParticleOptions((float)forward.x, (float)forward.y, (float)forward.z, (float)right.x, (float)right.y, (float)right.z, 1.0F),
         particlePos.x,
         particlePos.y + 0.3,
         particlePos.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      int trailParticles = 15;
      double speed = rayVec.length() / 12.0 * 0.75;

      for (int i = 0; i < trailParticles; i++) {
         Vec3 particleStart = entity.getBoundingBox().getCenter().add(Utils.getRandomVec3(1.0F + entity.getBbWidth()));
         Vec3 particleEnd = particleStart.add(rayVec);
         MagicManager.spawnParticles(
            level,
            new TraceParticleOptions(Utils.v3f(particleEnd), new Vector3f(0.941F, 0.169F, 0.231F)),
            particleStart.x,
            particleStart.y,
            particleStart.z,
            1,
            0.0,
            0.0,
            0.0,
            speed,
            false
         );
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setLifestealPercent(0.15F);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity);
   }

   private String getDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float weaponDamage = Utils.getWeaponDamage(caster);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }
}
