package net.acetheeldritchking.discerning_the_eldritch.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.rift_walker.UnstableRiftEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class RiftWalkerSpell extends AbstractSpell {
   public static final float RADIUS = 3.0F;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "rift_walker");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(12.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getDistance(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public RiftWalkerSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
      this.baseManaCost = 35;
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
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.ABYSSAL_TELEPORT.get());
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return !ItemRegistries.RIFT_RIPPER_EMBLEM.get().isEquippedBy(entity) && !ItemRegistries.KINGS_EFFIGY.get().isEquippedBy(entity) ? 1 : 3;
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
      UnstableRiftEntity unstableRift = new UnstableRiftEntity(level, entity, this.getDamage(spellLevel, entity), 3.0F);
      unstableRift.setPos(entity.getBoundingBox().getCenter().subtract(0.0, unstableRift.getBbHeight() * 0.5F, 0.0));
      level.addFreshEntity(unstableRift);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 100, castSource, null), playerMagicData);
      }

      Vec3 dest = null;
      io.redspace.ironsspellbooks.spells.ender.TeleportSpell.TeleportData teleportData = (io.redspace.ironsspellbooks.spells.ender.TeleportSpell.TeleportData)playerMagicData.getAdditionalCastData();
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
      entity.setInvisible(true);
      entity.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY, 60, 0, false, false, true));
      UnstableRiftEntity unstableRift = new UnstableRiftEntity(level, entity, this.getDamage(spellLevel, entity), 3.0F);
      unstableRift.setPos(entity.getBoundingBox().getCenter().subtract(0.0, unstableRift.getBbHeight() * 0.5F, 0.0));
      level.addFreshEntity(unstableRift);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.15F;
   }

   private float getDistance(int spellLevel, LivingEntity entity) {
      return (float)(Utils.softCapFormula(this.getEntityPowerMultiplier(entity)) * (this.getSpellPower(spellLevel, null) / 2.0F));
   }

   public static class TeleportData implements ICastData {
      private Vec3 teleportTargetPosition;

      public TeleportData(Vec3 teleportTargetPosition) {
         this.teleportTargetPosition = teleportTargetPosition;
      }

      public void setTeleportTargetPosition(Vec3 targetPosition) {
         this.teleportTargetPosition = targetPosition;
      }

      public Vec3 getTeleportTargetPosition() {
         return this.teleportTargetPosition;
      }

      public void reset() {
      }
   }
}
