package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetedTargetAreaCastData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.alshanex.tunes_n_tomes.entity.fortissimo.FortissimoChargeEntity;
import net.alshanex.tunes_n_tomes.entity.hymn_of_hope.HopeBurstEntity;
import net.alshanex.tunes_n_tomes.entity.hymn_of_hope.HopeShieldEntity;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class HymnOfHopeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "hymn_of_hope");
   private static final ConcurrentHashMap<UUID, FortissimoChargeEntity> activeCharges = new ConcurrentHashMap<>();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(80.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}));
   }

   public HymnOfHopeSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 5;
      this.castTime = 44;
      this.baseManaCost = 50;
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
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TSoundRegistry.HOPE_BURST.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (!Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F, false)) {
         playerMagicData.setAdditionalCastData(new TargetEntityCastData(entity));
         if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection
               .send(
                  new ClientboundSetActionBarTextPacket(
                     Component.translatable("ui.irons_spellbooks.spell_target_success_self", new Object[]{this.getDisplayName(serverPlayer)})
                        .withStyle(ChatFormatting.GREEN)
                  )
               );
         }
      }

      float radius = 10.0F;
      LivingEntity target = ((TargetEntityCastData)playerMagicData.getAdditionalCastData()).getTarget((ServerLevel)level);
      TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, target.position(), radius, Utils.packRGB(this.getTargetingColor()), target);
      playerMagicData.setAdditionalCastData(new TargetedTargetAreaCastData(target, area));
      return true;
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      FortissimoChargeEntity charge = new FortissimoChargeEntity(level);
      charge.moveTo(entity.position());
      level.addFreshEntity(charge);
      activeCharges.put(entity.getUUID(), charge);
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetedTargetAreaCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)world);
         if (targetEntity != null) {
            float radius = 10.0F;
            targetEntity.level()
               .getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(radius))
               .forEach(
                  victim -> {
                     if (victim.distanceToSqr(targetEntity) < radius * radius
                        && Utils.shouldHealEntity(entity, victim)
                        && !(victim instanceof SegnoMarkerEntity)
                        && victim.getActiveEffects().stream().noneMatch(this::isHopeEffect)) {
                        int duration = this.getDuration(spellLevel, entity);
                        if (victim.getHealth() >= victim.getMaxHealth() * 0.75) {
                           victim.addEffect(new MobEffectInstance(TEffectRegistry.VALOR, duration, this.getAmplifier(spellLevel, entity)));
                        } else if (victim.getHealth() > victim.getMaxHealth() * 0.25) {
                           victim.addEffect(new MobEffectInstance(TEffectRegistry.HARDENED, duration, this.getAmplifier(spellLevel, entity)));
                        } else {
                           duration /= 2;
                           victim.addEffect(new MobEffectInstance(TEffectRegistry.LAST_STAND, duration, this.getAmplifier(spellLevel, entity)));
                        }

                        HopeShieldEntity shield = new HopeShieldEntity(world, victim, duration);
                        shield.moveTo(victim.position());
                        world.addFreshEntity(shield);
                     }
                  }
               );
            HopeBurstEntity burst = new HopeBurstEntity(world);
            burst.moveTo(entity.position());
            world.addFreshEntity(burst);
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      FortissimoChargeEntity charge = activeCharges.remove(entity.getUUID());
      if (charge != null && charge.isAlive()) {
         charge.discard();
      }

      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
   }

   public int getAmplifier(int spellLevel, LivingEntity caster) {
      return spellLevel - 1;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F / 2.0F);
   }

   private boolean isHopeEffect(MobEffectInstance effect) {
      return effect.is(TEffectRegistry.HARDENED) || effect.is(TEffectRegistry.VALOR) || effect.is(TEffectRegistry.LAST_STAND);
   }
}
