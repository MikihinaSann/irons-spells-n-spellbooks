package net.acetheeldritchking.cataclysm_spellbooks.events;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.github.L_Ender.lionfishapi.server.event.StandOnFluidEvent;
import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedAptrgangr;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedKoboldiator;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedPhantomRemnant;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttachmentRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttributeRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSConfig;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;

@EventBusSubscriber
public class ServerEvents {
   @SubscribeEvent
   public static void onLivingPreDamageEvent(Pre event) {
      Entity entity = event.getSource().getEntity();
      if (entity instanceof LivingEntity attacker && attacker.hasEffect(CSPotionEffectRegistry.ABYSSAL_PREDATOR_EFFECT)) {
         int effectLevel = attacker.getEffect(CSPotionEffectRegistry.ABYSSAL_PREDATOR_EFFECT).getAmplifier() + 1;
         float baseAmount = event.getOriginalDamage();
         float damageBonusPerLevel = 0.1F * effectLevel;
         float bonusDamage = baseAmount * damageBonusPerLevel;
         float totalDamage = baseAmount + bonusDamage;
         if (attacker.isInWaterOrRain()) {
            event.setNewDamage(totalDamage);
         }
      }

      LivingEntity livingEntity = event.getEntity();
      ItemStack legEquipment = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
      if (!legEquipment.isEmpty()
         && event.getEntity() != null
         && event.getSource().getEntity() != null
         && legEquipment.getItem() == ItemRegistries.IGNITIUM_WIZARD_LEGGINGS.get()) {
         Entity attackerx = event.getSource().getEntity();
         if (attackerx instanceof LivingEntity livingAttacker && attackerx != event.getEntity() && event.getEntity().getRandom().nextFloat() < 0.5F) {
            MobEffectInstance mobEffectInstance = livingAttacker.getEffect(ModEffect.EFFECTBLAZING_BRAND);
            int i = 1;
            if (mobEffectInstance != null) {
               i += mobEffectInstance.getAmplifier();
               livingAttacker.removeEffectNoUpdate(ModEffect.EFFECTBLAZING_BRAND);
            } else {
               i++;
            }

            i = Mth.clamp(i, 0, 2);
            MobEffectInstance effectInstance = new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND, 100, i, false, false, true);
            livingAttacker.addEffect(effectInstance);
            if (!attackerx.isOnFire()) {
               attackerx.setRemainingFireTicks(5);
            }
         }
      }

      if (entity instanceof LivingEntity attackerx && attackerx.hasEffect(CSPotionEffectRegistry.WRATHFUL) && attackerx instanceof Player player) {
         player.getData(CSAttachmentRegistry.FORGONE_RAGE);
         if (player.hasData(CSAttachmentRegistry.FORGONE_RAGE)) {
            if ((Integer)player.getData(CSAttachmentRegistry.FORGONE_RAGE) < 4) {
               setRageValue(player, 1);
            }

            float baseAmount = event.getOriginalDamage();
            float damageBonusPerLevel = 0.2F * ((Integer)player.getData(CSAttachmentRegistry.FORGONE_RAGE)).intValue();
            float bonusDamage = baseAmount * damageBonusPerLevel;
            float totalDamage = baseAmount + bonusDamage;
            event.setNewDamage(totalDamage);
         }

         player.level()
            .playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)ModSounds.MALEDICTUS_SHORT_ROAR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      }

      if (entity instanceof LivingEntity attackerx && attackerx.hasEffect(CSPotionEffectRegistry.KINGS_WRATH_EFFECT) && attackerx instanceof Player player) {
         player.getData(CSAttachmentRegistry.KINGS_WRATH);
         if (player.hasData(CSAttachmentRegistry.KINGS_WRATH)) {
            float baseAmount = event.getOriginalDamage();
            float damageBonusPerLevel = 0.2F * ((Integer)player.getData(CSAttachmentRegistry.KINGS_WRATH)).intValue();
            float bonusDamage = baseAmount * damageBonusPerLevel;
            float totalDamage = baseAmount + bonusDamage;
            event.setNewDamage(totalDamage);
         }

         player.level()
            .playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent)ModSounds.REMNANT_CHARGE_ROAR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   public static void setRageValue(LivingEntity entity, int val) {
      int newVal = Math.min(val, 4);
      entity.setData(CSAttachmentRegistry.FORGONE_RAGE, (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE) + newVal);
   }

   public static void setWrathValue(LivingEntity entity, int val) {
      int newVal = Math.min(val, 4);
      entity.setData(CSAttachmentRegistry.KINGS_WRATH, (Integer)entity.getData(CSAttachmentRegistry.KINGS_WRATH) + newVal);
   }

   @SubscribeEvent
   public static void onLivingTickEvent(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
      Entity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(CSPotionEffectRegistry.ABYSSAL_PREDATOR_EFFECT)) {
         int effectLevel = livingEntity.getEffect(CSPotionEffectRegistry.ABYSSAL_PREDATOR_EFFECT).getAmplifier() + 1;
         float baseMovementSpeed = livingEntity.getSpeed();
         float speedBonusPerLevel = 0.1F * effectLevel;
         float bonusSpeed = baseMovementSpeed * speedBonusPerLevel;
         float totalSpeed = baseMovementSpeed + bonusSpeed;
         if (livingEntity.isInWaterOrRain()) {
            livingEntity.setSpeed(totalSpeed);
         } else {
            livingEntity.setSpeed(baseMovementSpeed);
         }
      }
   }

   @SubscribeEvent
   public static void handleResistanceAttributeSpawn(EntityJoinLevelEvent event) {
      boolean resistancesConfig = (Boolean)CSConfig.BOSSES_RESISTANCES.get();
      if (!ModList.get().isLoaded("potatospellbookstweaks")) {
         if (resistancesConfig) {
            if (!(event.getEntity() instanceof LivingEntity mob)) {
               return;
            }

            if (mob.getType() == ModEntities.IGNIS.get()) {
               addIfNonNull(mob, CSAttributeRegistry.ABYSSAL_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.FIRE_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.THE_LEVIATHAN.get()) {
               addIfNonNull(mob, AttributeRegistry.LIGHTNING_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, CSAttributeRegistry.ABYSSAL_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.ENDER_GUARDIAN.get()) {
               addIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.ENDER_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.THE_HARBINGER.get()) {
               addIfNonNull(mob, AttributeRegistry.LIGHTNING_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.BLOOD_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.ANCIENT_REMNANT.get()) {
               addIfNonNull(mob, AttributeRegistry.HOLY_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.FIRE_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.NETHERITE_MONSTROSITY.get()) {
               addIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.FIRE_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.MALEDICTUS.get()) {
               addIfNonNull(mob, AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 1.5);
            }

            if (mob.getType() == ModEntities.SCYLLA.get()) {
               addIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
               addIfNonNull(mob, AttributeRegistry.LIGHTNING_MAGIC_RESIST, 1.5);
            }
         }
      }
   }

   @SubscribeEvent
   public static void standOnFluidEvent(StandOnFluidEvent event) {
      LivingEntity entity = event.getEntity();
      ItemStack bootEquipment = entity.getItemBySlot(EquipmentSlot.FEET);
      if (!bootEquipment.isEmpty()
         && bootEquipment.getItem() == ItemRegistries.IGNITIUM_WIZARD_BOOTS.get()
         && !entity.isShiftKeyDown()
         && (event.getFluidState().is(Fluids.LAVA) || event.getFluidState().is(Fluids.FLOWING_LAVA))) {
         event.setCanceled(true);
      }
   }

   private static void addIfNonNull(LivingEntity entity, Holder<Attribute> attribute, double value) {
      AttributeInstance instance = entity.getAttributes().getInstance(attribute);
      if (instance != null) {
         instance.setBaseValue(value);
      }
   }

   @SubscribeEvent
   public static void onSpellModifyEvent(ModifySpellLevelEvent event) {
   }

   @SubscribeEvent
   public static void onEffectRemove(Remove event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof LivingEntity && entity.hasEffect(CSPotionEffectRegistry.WRATHFUL)) {
         entity.getData(CSAttachmentRegistry.FORGONE_RAGE);
         if (entity.hasData(CSAttachmentRegistry.FORGONE_RAGE)) {
            entity.setData(CSAttachmentRegistry.FORGONE_RAGE, 0);
         }

         entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1, false, false, false));
         entity.addEffect(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true)
         );
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true));
      }

      if (entity instanceof LivingEntity && entity.hasEffect(CSPotionEffectRegistry.KINGS_WRATH_EFFECT)) {
         entity.getData(CSAttachmentRegistry.KINGS_WRATH);
         if (entity.hasData(CSAttachmentRegistry.KINGS_WRATH)) {
            entity.setData(CSAttachmentRegistry.KINGS_WRATH, 0);
         }

         entity.addEffect(new MobEffectInstance(ModEffect.EFFECTCURSE_OF_DESERT, 60, 1, false, false, false));
         entity.addEffect(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true)
         );
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true));
      }
   }

   @SubscribeEvent
   public static void onEffectExpire(Expired event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof LivingEntity && entity.hasEffect(CSPotionEffectRegistry.CURSED_FRENZY) && !entity.level().isClientSide()) {
         CSUtils.spawnHalberdWindmill(5, 5, 1.0, 0.5, 0.5, 1, entity, entity.level(), 5.0F, 1);
      }

      if (entity instanceof LivingEntity && entity.hasEffect(CSPotionEffectRegistry.WRATHFUL)) {
         entity.getData(CSAttachmentRegistry.FORGONE_RAGE);
         if (entity.hasData(CSAttachmentRegistry.FORGONE_RAGE)) {
            entity.setData(CSAttachmentRegistry.FORGONE_RAGE, 0);
         }

         entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 1, false, false, false));
         entity.addEffect(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true)
         );
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true));
      }

      if (entity instanceof LivingEntity && entity.hasEffect(CSPotionEffectRegistry.KINGS_WRATH_EFFECT)) {
         entity.getData(CSAttachmentRegistry.KINGS_WRATH);
         if (entity.hasData(CSAttachmentRegistry.KINGS_WRATH)) {
            entity.setData(CSAttachmentRegistry.KINGS_WRATH, 0);
         }

         entity.addEffect(new MobEffectInstance(ModEffect.EFFECTCURSE_OF_DESERT, 60, 1, false, false, false));
         entity.addEffect(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true)
         );
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1 + (Integer)entity.getData(CSAttachmentRegistry.FORGONE_RAGE), false, true, true));
      }
   }

   @SubscribeEvent
   public static void onFallEvent(LivingFallEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity livingEntity
         && !livingEntity.getItemBySlot(EquipmentSlot.FEET).isEmpty()
         && livingEntity.getItemBySlot(EquipmentSlot.FEET).getItem() == ItemRegistries.CURSIUM_MAGE_BOOTS.get()) {
         event.setDistance(event.getDistance() * 0.3F);
      }
   }

   @SubscribeEvent
   public static void onLivingDeathEvent(LivingDeathEvent event) {
      DamageSource damageSource = event.getSource();
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity livingEntity
         && !livingEntity.level().isClientSide
         && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && CSUtils.tryCurisumChestplateRebirth(livingEntity)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onLivingAttackEvent(LivingIncomingDamageEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity livingEntity
         && !livingEntity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
         && livingEntity.getItemBySlot(EquipmentSlot.LEGS).getItem() == ItemRegistries.CURSIUM_MAGE_LEGGINGS.get()) {
         if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
            if (livingEntity.getRandom().nextFloat() < 0.15F) {
               event.setCanceled(true);
            }
         } else if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && livingEntity.getRandom().nextFloat() < 0.08F) {
            event.setCanceled(true);
         }
      }

      if (entity instanceof LivingEntity attacker && attacker.hasEffect(CSPotionEffectRegistry.KINGS_WRATH_EFFECT) && attacker instanceof Player player) {
         player.getData(CSAttachmentRegistry.KINGS_WRATH);
         if (player.hasData(CSAttachmentRegistry.KINGS_WRATH) && (Integer)player.getData(CSAttachmentRegistry.KINGS_WRATH) < 4) {
            setWrathValue(player, 1);
         }
      }
   }

   @SubscribeEvent
   public static void mobGriefingEvent(LivingDestroyBlockEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof SummonedPhantomRemnant || entity instanceof SummonedKoboldiator || entity instanceof SummonedAptrgangr) {
         event.setCanceled(true);
      }
   }
}
