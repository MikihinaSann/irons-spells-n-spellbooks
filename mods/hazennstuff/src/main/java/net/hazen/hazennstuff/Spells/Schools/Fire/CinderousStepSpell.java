package net.hazen.hazennstuff.Spells.Schools.Fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell.TeleportData;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class CinderousStepSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "cinderous_step");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(12.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}));
   }

   public CinderousStepSpell() {
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 4;
      this.baseManaCost = 30;
      this.manaCostPerLevel = 10;
      this.castTime = 0;
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
      return Optional.of((SoundEvent)SoundRegistry.FIRE_CAST.get());
   }

   public void onClientPreCast(Level level, int spellLevel, LivingEntity entity, InteractionHand hand, @Nullable MagicData playerMagicData) {
      super.onClientPreCast(level, spellLevel, entity, hand, playerMagicData);
      Vec3 forward = entity.getForward().normalize();

      for (int i = 0; i < 35; i++) {
         Vec3 motion = forward.scale(Utils.random.nextDouble() * 0.25);
         level.addParticle(ParticleHelper.FIERY_SMOKE, entity.getRandomX(0.4F), entity.getRandomY(), entity.getRandomZ(0.4F), motion.x, motion.y, motion.z);
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 dest = null;
      TeleportData teleportData = (TeleportData)playerMagicData.getAdditionalCastData();
      if (teleportData != null) {
         Vec3 potentialTarget = teleportData.getTeleportTargetPosition();
         if (potentialTarget != null) {
            dest = potentialTarget;
            Utils.handleSpellTeleport(this, entity, potentialTarget);
         }
      } else {
         HitResult hitResult = Utils.raycastForEntity(level, entity, this.getDistance(spellLevel, entity), true);
         if (entity.isPassenger()) {
            entity.stopRiding();
         }

         if (hitResult.getType() == Type.ENTITY && ((EntityHitResult)hitResult).getEntity() instanceof LivingEntity target) {
            for (int i = 0; i < 8; i++) {
               dest = target.position().subtract(new Vec3(0.0, 0.0, 1.5).yRot(-(target.getYRot() + i * 45) * (float) (Math.PI / 180.0)));
               if (level.getBlockState(BlockPos.containing(dest).above()).isAir()) {
                  break;
               }
            }

            Utils.handleSpellTeleport(this, entity, dest.add(0.0, 1.0, 0.0));
            entity.lookAt(Anchor.EYES, target.getEyePosition().subtract(0.0, 0.15, 0.0));
         } else {
            dest = TeleportSpell.findTeleportLocation(level, entity, this.getDistance(spellLevel, entity));
            Utils.handleSpellTeleport(this, entity, dest);
         }
      }

      entity.resetFallDistance();
      level.playSound(null, dest.x, dest.y, dest.z, this.getCastFinishSound().get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
      entity.addEffect(
         new MobEffectInstance(HnSEffects.FLAME_KISSED, (int)(this.getSpellPower(spellLevel, entity) * 20.0F), spellLevel - 1, false, false, true)
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDistance(int spellLevel, LivingEntity sourceEntity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(sourceEntity)) * this.getSpellPower(spellLevel, null));
   }

   public AnimationHolder getCastStartAnimation() {
      return AnimationHolder.none();
   }
}
