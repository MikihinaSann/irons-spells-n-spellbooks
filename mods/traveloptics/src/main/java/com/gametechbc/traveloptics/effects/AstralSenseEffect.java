package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.TOParticleHelper;
import com.gametechbc.traveloptics.util.TOTags;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.FogRenderer.FogData;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AstralSenseEffect extends MobEffect {
   private static final int TNT_RADIUS = 8;
   private static final int THREAT_RADIUS = 20;
   private static final int PROJECTILE_RADIUS = 30;
   private static final int ENTITY_SENSE_RADIUS = 8;

   public AstralSenseEffect() {
      super(MobEffectCategory.BENEFICIAL, 16766720);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player) {
         Level level = player.level();
         this.detectPrimedTNT(player, level, 8);
         this.detectEntityThreats(player, level, 20);
         this.detectIncomingProjectiles(player, level, 30);
         this.detectAndHighlightEntities(player, level, 8);
         return true;
      } else {
         return false;
      }
   }

   private void detectAndHighlightEntities(Player player, Level level, int radius) {
      AABB searchBox = player.getBoundingBox().inflate(radius);
      level.getEntitiesOfClass(Entity.class, searchBox, e -> e.getType().is(TOTags.SENSED_ENTITIES)).forEach(sensedEntity -> {
         Vec3 pos = sensedEntity.position().add(0.0, sensedEntity.getBbHeight() / 2.0F, 0.0);
         MagicManager.spawnParticles(level, TOParticleHelper.ABYSS_SPIKE_PARTICLE, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.01, false);
      });
   }

   private boolean detectPrimedTNT(Player player, Level level, int radius) {
      return !level.getEntitiesOfClass(PrimedTnt.class, player.getBoundingBox().inflate(radius)).isEmpty()
         ? this.sendMessage(player, Component.translatable("spell.traveloptics.message.astral_sense.tnt").withStyle(ChatFormatting.RED))
         : false;
   }

   private void detectEntityThreats(Player player, Level level, int radius) {
      AABB searchBox = player.getBoundingBox().inflate(radius);
      List<Mob> nearbyMobs = level.getEntitiesOfClass(
         Mob.class, searchBox, mobx -> mobx.isAlive() && !mobx.getType().is(TOTags.APPROACH_ENTITY_BLACKLIST) && Math.abs(mobx.getY() - player.getY()) <= 4.0
      );
      Mob targetingMob = null;
      Mob approachingMob = null;
      double targetDistanceSqr = Double.MAX_VALUE;
      double approachDistanceSqr = Double.MAX_VALUE;

      for (Mob mob : nearbyMobs) {
         double distanceSqr = mob.distanceToSqr(player);
         if (mob.getTarget() == player && distanceSqr < targetDistanceSqr) {
            targetingMob = mob;
            targetDistanceSqr = distanceSqr;
         } else if (mob.getNavigation().getPath() != null && distanceSqr < approachDistanceSqr) {
            approachingMob = mob;
            approachDistanceSqr = distanceSqr;
         }
      }

      if (targetingMob != null) {
         this.sendMessage(
            player,
            Component.translatable(
                  "spell.traveloptics.message.astral_sense.targeting", new Object[]{targetingMob.getDisplayName(), (int)Math.sqrt(targetDistanceSqr)}
               )
               .withStyle(ChatFormatting.RED)
         );
      } else if (approachingMob != null) {
         this.sendMessage(
            player,
            Component.translatable(
                  "spell.traveloptics.message.astral_sense.approaching", new Object[]{approachingMob.getDisplayName(), (int)Math.sqrt(approachDistanceSqr)}
               )
               .withStyle(ChatFormatting.GOLD)
         );
      }
   }

   private void detectIncomingProjectiles(Player player, Level level, int radius) {
      if (!level.getEntitiesOfClass(
            Projectile.class,
            player.getBoundingBox().inflate(radius),
            proj -> proj.getOwner() != player && proj.getDeltaMovement().dot(player.position().subtract(proj.position()).normalize()) > 0.5
         )
         .isEmpty()) {
         this.sendMessage(player, Component.translatable("spell.traveloptics.message.astral_sense.projectile").withStyle(ChatFormatting.RED));
      }
   }

   private boolean sendMessage(Player player, Component message) {
      player.displayClientMessage(message, true);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 5 == 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static class AstralSenseFogFunction implements MobEffectFogFunction {
      public AstralSenseFogFunction() {
         super();
      }

      public Holder<MobEffect> getMobEffect() {
         return TOEffects.ASTRAL_SENSE;
      }

      public void setupFog(FogData fogData, LivingEntity entity, MobEffectInstance mobEffectInstance, float p_234184_, float p_234185_) {
         float f = 160.0F;
         if (fogData.mode == FogMode.FOG_SKY) {
            fogData.start = 0.0F;
            fogData.end = f * 0.25F;
         } else {
            fogData.start = -f * 0.5F;
            fogData.end = f;
         }
      }
   }
}
