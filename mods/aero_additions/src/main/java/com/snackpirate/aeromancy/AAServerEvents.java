package com.snackpirate.aeromancy;

import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import com.snackpirate.aeromancy.data.AAEntityTypeTags;
import com.snackpirate.aeromancy.network.AeromancySpellData;
import com.snackpirate.aeromancy.spells.AASpells;
import com.snackpirate.aeromancy.spells.summon_breeze.SummonedBreeze;
import com.snackpirate.aeromancy.spells.wind_shield.WindShieldSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;

@EventBusSubscriber(
   modid = "aero_additions"
)
public class AAServerEvents {
   @SubscribeEvent
   public static void addAttributes(EntityAttributeModificationEvent event) {
      event.add(EntityType.PLAYER, AASpells.Attributes.WIND_SPELL_POWER);
      event.add(EntityType.PLAYER, AASpells.Attributes.WIND_MAGIC_RESIST);
   }

   @SubscribeEvent
   public static void onAttributeCreate(EntityAttributeCreationEvent event) {
      event.put((EntityType)AASpells.Entities.SUMMONED_BREEZE.get(), SummonedBreeze.createAttributes().build());
   }

   @EventBusSubscriber(
      modid = "aero_additions"
   )
   public static class Game {
      @SubscribeEvent
      public static void windShieldDeflection(ProjectileImpactEvent event) {
         if (event.getRayTraceResult() instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity entity) {
            if (entity.hasEffect(AASpells.MobEffects.WIND_SHIELD) && !event.getProjectile().getType().is(AAEntityTypeTags.REFLECTION_IMMUNE)) {
               if (entity.getRandom().nextFloat() < WindShieldSpell.chanceToDeflect(entity.getEffect(AASpells.MobEffects.WIND_SHIELD).getAmplifier())) {
                  event.setCanceled(true);
                  entity.level().playSound(null, entity, SoundEvents.BREEZE_DEFLECT, entity.getSoundSource(), 1.0F, 1.0F);
                  event.getProjectile().deflect(ProjectileDeflection.REVERSE, entity, entity, entity instanceof Player);
               }
            } else if (entity.getType() == AASpells.Entities.SUMMONED_BREEZE.get()) {
               event.setCanceled(true);
               entity.level().playSound(null, entity, SoundEvents.BREEZE_DEFLECT, entity.getSoundSource(), 1.0F, 1.0F);
               event.getProjectile().deflect(ProjectileDeflection.REVERSE, entity, SummonManager.getOwner(entity), false);
            }
         }
      }

      @SubscribeEvent
      public static void onStartTracking(StartTracking event) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof ServerPlayer targetPlayer) {
            AeromancySpellData.getAeromancyData(serverPlayer).syncToPlayer(targetPlayer);
         }
      }

      @SubscribeEvent
      public static void onRespawn(PlayerRespawnEvent event) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            AeromancySpellData.getAeromancyData(serverPlayer).syncToPlayer(serverPlayer);
         }
      }

      @SubscribeEvent
      public static void onChangeDim(PlayerChangedDimensionEvent event) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            AeromancySpellData.getAeromancyData(serverPlayer).syncToPlayer(serverPlayer);
         }
      }

      @SubscribeEvent
      public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            AeromancySpellData.getAeromancyData(serverPlayer).syncToPlayer(serverPlayer);
         }
      }

      @SubscribeEvent
      public static void activateFlight(PlayerSwimEvent event) {
         if (event.getEntity().hasEffect(AASpells.MobEffects.FLIGHT)) {
            event.setResult(EventResult.SUCCESS);
         }
      }
   }
}
