package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.entity.red_arrow.RedArrowEntity;
import net.alshanex.tunes_n_tomes.entity.red_circle.RedCircleEntity;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.alshanex.tunes_n_tomes.util.HelperMethods;
import net.alshanex.tunes_n_tomes.util.music.SpellMusicHelper;
import net.alshanex.tunes_n_tomes.util.music.SpellMusicState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PiercingSoloSpell extends AbstractSpell {
   private static final int MUSIC_DURATION = 200;
   private static final ConcurrentHashMap<UUID, SpellMusicState> activeStates = new ConcurrentHashMap<>();
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "piercing_solo");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(40.0)
      .build();

   public PiercingSoloSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 1;
      this.castTime = 200;
      this.baseManaCost = 7;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(), 1)})
      );
   }

   public boolean allowCrafting() {
      return false;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
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
      return Optional.empty();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      SpellMusicState state = activeStates.computeIfAbsent(entity.getUUID(), k -> new SpellMusicState());
      if (state.getTrackedEntity() == null && !world.isClientSide) {
         RedCircleEntity circle = new RedCircleEntity(world, entity);
         circle.setPos(entity.position());
         world.addFreshEntity(circle);
         state.setTrackedEntity(circle);
      }

      if (state.getTrackedEntity() != null && !state.getTrackedEntity().isAlive()) {
         RedCircleEntity circle = new RedCircleEntity(world, entity);
         circle.setPos(entity.position());
         world.addFreshEntity(circle);
         state.setTrackedEntity(circle);
      }

      SpellMusicHelper.tickMusicLoop(world, entity, state, (SoundEvent)TSoundRegistry.PIERCING_SOLO.get(), 200);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 20 == 0) {
         this.spawnArrows(level, entity, spellLevel);
      }
   }

   private double getRadius() {
      return 15.0;
   }

   public void spawnArrows(Level world, LivingEntity entity, int spellLevel) {
      double radius = this.getRadius();
      double radiusSqr = radius * radius;

      for (LivingEntity livingEntity : world.getEntitiesOfClass(
         LivingEntity.class,
         new AABB(entity.position().subtract(radius, radius, radius), entity.position().add(radius, radius, radius)),
         livingEntityx -> HelperMethods.horizontalDistanceSqr(livingEntityx, entity.position()) < radiusSqr
            && !livingEntityx.is(entity)
            && !(livingEntityx instanceof SegnoMarkerEntity)
            && !livingEntityx.getUUID().equals(entity.getUUID())
            && !entity.isAlliedTo(livingEntityx)
      )) {
         RedArrowEntity redArrowEntity = new RedArrowEntity(world);
         redArrowEntity.setOwner(entity);
         redArrowEntity.setCircular();
         redArrowEntity.setDamage(this.getDamage(spellLevel, entity));
         redArrowEntity.moveTo(livingEntity.position());
         livingEntity.hurt(
            new DamageSource(DamageSources.getHolderFromResource(livingEntity, ISSDamageTypes.FIRE_FIELD), redArrowEntity, entity),
            this.getDamage(spellLevel, entity)
         );
         livingEntity.addEffect(new MobEffectInstance(TEffectRegistry.DEAFENED, 60, 0));
         livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0.0, 0.5, 0.0));
         livingEntity.hurtMarked = true;
         world.addFreshEntity(redArrowEntity);
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.25F;
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      SpellMusicHelper.cleanup(level, entity.getUUID(), activeStates);
      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
   }
}
