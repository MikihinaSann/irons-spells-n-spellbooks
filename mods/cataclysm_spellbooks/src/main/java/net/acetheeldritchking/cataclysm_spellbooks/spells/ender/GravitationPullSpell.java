package net.acetheeldritchking.cataclysm_spellbooks.spells.ender;

import com.github.L_Ender.cataclysm.client.particle.Options.StormParticleOptions;
import com.github.L_Ender.cataclysm.init.ModItems;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GravitationPullSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "gravitation_pull");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.gravitation"),
         Component.translatable("ui.cataclysm_spellbooks.range", new Object[]{spellLevel}),
         Component.translatable("ui.cataclysm_spellbooks.difference", new Object[]{this.getDifference(spellLevel, caster)})
      );
   }

   public GravitationPullSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 5;
      this.castTime = 100;
      this.baseManaCost = 10;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.PORTAL_AMBIENT);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CAST_KNEELING_PRAYER;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Item gauntlet = (Item)ModItems.GAUNTLET_OF_GUARD.get();
      double radius;
      if (!entity.getOffhandItem().is(gauntlet) && !entity.getMainHandItem().is(gauntlet)) {
         radius = spellLevel * 2;
      } else {
         radius = spellLevel * 8;
      }

      List<LivingEntity> entitiesNearby = level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius));
      entitiesNearby.forEach(
         target -> {
            if (target != entity && !(target instanceof Player player && player.getAbilities().invulnerable)) {
               Vec3 difference = entity.position().subtract(target.position());
               difference = difference.normalize().scale(this.getDifference(spellLevel, entity));
               if (entity.isCrouching()) {
                  target.setDeltaMovement(
                     difference.multiply(
                           -this.getDifference(spellLevel, entity), -this.getDifference(spellLevel, entity), -this.getDifference(spellLevel, entity)
                        )
                        .normalize()
                  );
               } else {
                  float distance = target.distanceTo(entity);
                  if (distance <= 1.0F) {
                     return;
                  }

                  double overshot = difference.length() / distance;
                  if (overshot > 1.0) {
                     difference = new Vec3(difference.x() / overshot, difference.y() / overshot, difference.z() / overshot);
                  }

                  difference = difference.multiply(
                        this.getDifference(spellLevel, entity), this.getDifference(spellLevel, entity), this.getDifference(spellLevel, entity)
                     )
                     .normalize();
                  target.setDeltaMovement(difference);
               }
            }
         }
      );
      float r = 0.4F;
      float g = 0.1F;
      float b = 0.8F;
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 2.75F + entity.getRandom().nextFloat() * 0.6F, 3.75F + entity.getRandom().nextFloat() * 0.6F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 2.5F + entity.getRandom().nextFloat() * 0.45F, 3.0F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 2.25F + entity.getRandom().nextFloat() * 0.45F, 2.25F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new StormParticleOptions(r, g, b, 1.25F + entity.getRandom().nextFloat() * 0.45F, 1.25F + entity.getRandom().nextFloat() * 0.45F, entity.getId()),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         1.0,
         true
      );
      ASUtils.spawnParticlesInRing(16, 1.25F, 3.25F, 5.25F, 0.5F, 0.1F, entity, ParticleTypes.DRAGON_BREATH);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDifference(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) / 5.0F;
   }
}
