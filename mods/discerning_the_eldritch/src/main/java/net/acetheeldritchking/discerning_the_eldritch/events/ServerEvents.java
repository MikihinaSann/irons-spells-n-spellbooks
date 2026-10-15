package net.acetheeldritchking.discerning_the_eldritch.events;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.ChainLightning;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import it.crystalnest.prometheus.api.FireManager;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneBoss;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile.CataclysmBladeSmallProjectile;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.gore_bile.GoreBileAoE;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.CataclysmBladeAwakenedItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.DevourerAxeAwakenedItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.IceSpearItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.MourningStarMaceAwakenedItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.SoulFireScytheItem;
import net.acetheeldritchking.discerning_the_eldritch.items.weapons.TheSnowgraveItem;
import net.acetheeldritchking.discerning_the_eldritch.networking.DTEAttachmentSync;
import net.acetheeldritchking.discerning_the_eldritch.networking.devour.GetSyncDevourStacksPacket;
import net.acetheeldritchking.discerning_the_eldritch.networking.devour.ResetSyncDevourStacksPacket;
import net.acetheeldritchking.discerning_the_eldritch.networking.devour.SetSyncDevourStacksPacket;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttachmentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEDataComponentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent.XpChange;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class ServerEvents {
   @SubscribeEvent
   public static void onPlayerLogInEvent(PlayerLoggedInEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof ServerPlayer player) {
         PacketDistributor.sendToPlayer(player, new GetSyncDevourStacksPacket(player), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public static void onPlayerLogOutEvent(PlayerLoggedOutEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof ServerPlayer player) {
         PacketDistributor.sendToPlayer(player, new GetSyncDevourStacksPacket(player), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public static void onPlayerCastEvent(SpellPreCastEvent event) {
      Player entity = event.getEntity();
      AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
      boolean hasSilenceEffect = entity.hasEffect(DTEPotionEffectRegistry.SILENCE_POTION_EFFECT);
      if (entity instanceof ServerPlayer player && !player.level().isClientSide() && hasSilenceEffect) {
         event.setCanceled(true);
         int time = player.getEffect(DTEPotionEffectRegistry.SILENCE_POTION_EFFECT).getDuration();
         String formattedTime = ASUtils.convertTicksToTime(time);
         if (player instanceof ServerPlayer) {
            player.connection
               .send(
                  new ClientboundSetActionBarTextPacket(
                     Component.translatable("display.discerning_the_eldritch.silence_warning")
                        .append(formattedTime)
                        .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                  )
               );
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }

      if (DTEServerConfig.enableInsanitySystem) {
         entity.getData(DTEAttachmentRegistry.INSANITY_METER);
         entity.getData(DTEAttachmentRegistry.IS_INSANE);
         if (spell.getSchoolType() == SchoolRegistry.ELDRITCH.get()
            && (Integer)entity.getData(DTEAttachmentRegistry.INSANITY_METER) <= DTEServerConfig.maxInsanityValue - 1) {
            entity.setData(DTEAttachmentRegistry.INSANITY_METER, (Integer)entity.getData(DTEAttachmentRegistry.INSANITY_METER) + 1);
            DiscerningTheEldritch.LOGGER.debug("Meter: " + entity.getData(DTEAttachmentRegistry.INSANITY_METER));
            DiscerningTheEldritch.LOGGER.debug("Max: " + DTEServerConfig.maxInsanityValue);
            if ((Integer)entity.getData(DTEAttachmentRegistry.INSANITY_METER) == DTEServerConfig.maxInsanityValue) {
               entity.setData(DTEAttachmentRegistry.IS_INSANE, true);
               if (entity instanceof ServerPlayer playerx) {
                  if (playerx.getRandom().nextFloat() * 100.0F < 32.0F) {
                     playerx.connection
                        .send(
                           new ClientboundSetTitleTextPacket(
                              Component.translatable("display.discerning_the_eldritch.insanity_warning_1")
                                 .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                           )
                        );
                  }

                  if (playerx.getRandom().nextFloat() * 100.0F < 32.0F) {
                     playerx.connection
                        .send(
                           new ClientboundSetTitleTextPacket(
                              Component.translatable("display.discerning_the_eldritch.insanity_warning_2")
                                 .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                           )
                        );
                  }

                  if (playerx.getRandom().nextFloat() * 100.0F < 32.0F) {
                     playerx.connection
                        .send(
                           new ClientboundSetTitleTextPacket(
                              Component.translatable("display.discerning_the_eldritch.insanity_warning_3")
                                 .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                           )
                        );
                  }

                  if (playerx.getRandom().nextFloat() * 100.0F < 4.0F) {
                     playerx.connection
                        .send(
                           new ClientboundSetTitleTextPacket(
                              Component.translatable("display.discerning_the_eldritch.insanity_warning_4")
                                 .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                           )
                        );
                  }
               }
            }
         }
      }

      boolean hasPresenceEffect = entity.hasEffect(DTEPotionEffectRegistry.METAPHYSICAL_POTION_EFFECT);
      if (entity instanceof ServerPlayer playerx && !playerx.level().isClientSide() && hasPresenceEffect) {
         event.setCanceled(true);
         int time2 = playerx.getEffect(DTEPotionEffectRegistry.METAPHYSICAL_POTION_EFFECT).getDuration();
         String formattedTime2 = ASUtils.convertTicksToTime(time2);
         if (playerx instanceof ServerPlayer) {
            playerx.connection
               .send(
                  new ClientboundSetActionBarTextPacket(
                     Component.translatable("display.discerning_the_eldritch.presence_warning")
                        .append(formattedTime2)
                        .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                  )
               );
            playerx.level().playSound(null, playerx.getX(), playerx.getY(), playerx.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTickEvent(Post event) {
      LivingEntity entity = event.getEntity();
      entity.getData(DTEAttachmentRegistry.IS_INSANE);
      if ((Boolean)entity.getData(DTEAttachmentRegistry.IS_INSANE) && entity.tickCount % 100 == 0) {
         entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 110, 9, false, false, false));
      }
   }

   @SubscribeEvent
   public static void livingDamageEventPost(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      Entity sourceEntity = event.getSource().getEntity();
      LivingEntity target = event.getEntity();
      Entity projectile = event.getSource().getDirectEntity();
      if (sourceEntity != null
         && sourceEntity instanceof Player player
         && ASUtils.hasCurio(player, (Item)ItemRegistries.DIARY_OF_DECAY.get())
         && projectile instanceof Projectile) {
         target.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.BLOOD_ROT_EFFECT, 60, 0, true, true, true));
      }

      if (sourceEntity instanceof LivingEntity livingEntity) {
         ItemStack mainhandItem = livingEntity.getMainHandItem();
         if (mainhandItem.getItem() instanceof IceSpearItem && target instanceof LivingEntity) {
            target.setTicksFrozen(100);
         }

         if (mainhandItem.getItem() instanceof TheSnowgraveItem && target instanceof LivingEntity) {
            target.setTicksFrozen(300);
         }

         if (livingEntity.hasEffect(DTEPotionEffectRegistry.MEND_FLESH_EFFECT) && DTEServerConfig.mendFleshLifesteal) {
            livingEntity.heal(1.5F);
            int count = 8;
            float radius = 0.25F;
            ASUtils.spawnParticlesInCircle(count, radius, 0.5F, 0.1F, livingEntity, ParticleTypes.SCULK_SOUL);
            if (!livingEntity.level().isClientSide) {
               livingEntity.level().playSound(livingEntity, livingEntity.blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
         }

         if (mainhandItem.getItem() instanceof CataclysmBladeAwakenedItem
            && !(livingEntity instanceof Player player && player.getCooldowns().isOnCooldown((Item)ItemRegistries.CATACLYSM_AWAKENED.get()))) {
            float MAX_HEALTH = target.getMaxHealth();
            float baseHealth = target.getHealth();
            double percent = baseHealth / MAX_HEALTH * 100.0F;
            if (percent > 50.0) {
               MagicManager.spawnParticles(
                  target.level(),
                  new BlastwaveParticleOptions(DTESchoolRegistry.RITUAL.get().getTargetingColor(), 1.5F),
                  target.getX(),
                  target.getY() + 0.165F,
                  target.getZ(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  true
               );

               for (int i = 0; i < 5; i++) {
                  CataclysmBladeSmallProjectile cataclysmSmallBlade = new CataclysmBladeSmallProjectile(livingEntity.level(), livingEntity);
                  Vec3 origin = target.getEyePosition().add(target.getForward().normalize().scale(1.2F)).subtract(0.0, 0.15, 0.0);
                  cataclysmSmallBlade.setPos(origin.subtract(0.0, cataclysmSmallBlade.getBbHeight() - 1.5, 0.0));
                  Vec3 vec3 = target.getForward().add(0.0, 0.05, 0.0).normalize();
                  cataclysmSmallBlade.shoot(vec3.scale(0.5), 0.4F);
                  cataclysmSmallBlade.setDamage(2.0F);
                  cataclysmSmallBlade.setHomingTarget(target);
                  livingEntity.level().addFreshEntity(cataclysmSmallBlade);
               }

               if (target instanceof LivingEntity) {
                  target.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.MALIGNANT_BURN_EFFECT, 60, 0, true, true, true));
               }

               if (livingEntity instanceof Player playerx) {
                  playerx.getCooldowns().addCooldown((Item)ItemRegistries.CATACLYSM_AWAKENED.get(), 300);
               }
            }
         }

         if (mainhandItem.getItem() instanceof DevourerAxeAwakenedItem
            && !(livingEntity instanceof Player playerx && playerx.getCooldowns().isOnCooldown((Item)ItemRegistries.DEVOURER_AWAKENED.get()))
            && (Integer)livingEntity.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES) == 5) {
            GoreBileAoE goreBileAoE = new GoreBileAoE(livingEntity.level());
            goreBileAoE.setOwner(livingEntity);
            goreBileAoE.moveTo(target.position());
            goreBileAoE.setDamage(5.0F);
            goreBileAoE.setRadius(4.0F);
            goreBileAoE.setDuration(100);
            goreBileAoE.setCircular();
            livingEntity.level().addFreshEntity(goreBileAoE);
            livingEntity.level()
               .playLocalSound(
                  livingEntity.getX(),
                  livingEntity.getY(),
                  livingEntity.getZ(),
                  (SoundEvent)DTESoundRegistry.DEVOURER_WRETCH.get(),
                  SoundSource.PLAYERS,
                  1.0F,
                  1.0F,
                  false
               );
            if (livingEntity instanceof Player playerxx) {
               PacketDistributor.sendToPlayer((ServerPlayer)playerxx, new ResetSyncDevourStacksPacket(playerxx), new CustomPacketPayload[0]);
               DTEAttachmentSync.resetDevour(livingEntity);
               playerxx.getCooldowns().addCooldown((Item)ItemRegistries.DEVOURER_AWAKENED.get(), 300);
            } else {
               livingEntity.setData(DTEAttachmentRegistry.DEVOURED_ENTITIES, 0);
            }
         }

         if (mainhandItem.getItem() instanceof SoulFireScytheItem) {
            Integer soulFireStacks = (Integer)mainhandItem.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS);

            assert soulFireStacks != null;

            if (soulFireStacks >= 5) {
               FireManager.setOnFire(target, 5.0F, FireManager.SOUL_FIRE_TYPE);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathEvent(LivingDeathEvent event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker instanceof LivingEntity livingAttacker) {
         ItemStack mainhandItem = livingAttacker.getMainHandItem();
         if (mainhandItem.getItem() instanceof DevourerAxeAwakenedItem) {
            attacker.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES);
            if (livingAttacker instanceof Player player && target != player) {
               PacketDistributor.sendToPlayer((ServerPlayer)player, new SetSyncDevourStacksPacket(player), new CustomPacketPayload[0]);
               DTEAttachmentSync.setDevour(1, player);
            } else {
               attacker.setData(DTEAttachmentRegistry.DEVOURED_ENTITIES, (Integer)attacker.getData(DTEAttachmentRegistry.DEVOURED_ENTITIES) + 1);
            }
         }

         if (mainhandItem.getItem() instanceof SoulFireScytheItem && target != livingAttacker) {
            Integer soulFireStacks = (Integer)mainhandItem.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS);
            if (soulFireStacks != null) {
               mainhandItem.set(DTEDataComponentRegistry.SOUL_FIRE_STACKS, soulFireStacks + 1);
            }
         }
      }
   }

   @SubscribeEvent
   public static void livingIncomingDamageEvent(LivingIncomingDamageEvent event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      Entity directAttacker = event.getSource().getDirectEntity();
      if (target instanceof LivingEntity && target.hasEffect(DTEPotionEffectRegistry.METAPHYSICAL_POTION_EFFECT)) {
         event.setCanceled(true);
      }

      if (attacker instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(DTEPotionEffectRegistry.METAPHYSICAL_POTION_EFFECT)) {
         event.setCanceled(true);
      }

      if (directAttacker instanceof LivingEntity livingAttacker) {
         ItemStack mainhandItem = livingAttacker.getMainHandItem();
         if (mainhandItem.getItem() instanceof MourningStarMaceAwakenedItem
            && !(livingAttacker instanceof Player player && player.getCooldowns().isOnCooldown((Item)ItemRegistries.MOURNING_STAR_AWAKENED.get()))) {
            float MAX_HEALTH = target.getMaxHealth();
            float baseHealth = target.getHealth();
            double percent = baseHealth / MAX_HEALTH * 100.0F;
            if (percent < 50.0) {
               if (Utils.random.nextFloat() <= 0.5) {
                  if (livingAttacker instanceof ServerPlayer serverPlayer) {
                     serverPlayer.connection
                        .send(
                           new ClientboundSetActionBarTextPacket(
                              Component.translatable("display.discerning_the_eldritch.judgement_decree_1")
                                 .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                           )
                        );
                  }

                  float baseDamage = event.getOriginalAmount();
                  float newDamage = baseDamage * Float.valueOf(DTEServerConfig.mourningStarMortalDamageMultiplier);
                  event.setAmount(newDamage);
               } else {
                  if (livingAttacker instanceof ServerPlayer serverPlayer) {
                     serverPlayer.connection
                        .send(
                           new ClientboundSetActionBarTextPacket(
                              Component.translatable("display.discerning_the_eldritch.judgement_decree_2")
                                 .withStyle(s -> s.withColor(TextColor.fromRgb(15949663)))
                           )
                        );
                  }

                  if (target instanceof LivingEntity) {
                     target.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.ACCURSED_EFFECT, 100, 0, true, true, true));
                  }
               }

               if (livingAttacker instanceof Player playerx) {
                  playerx.getCooldowns().addCooldown((Item)ItemRegistries.MOURNING_STAR_AWAKENED.get(), 300);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onFallEvent(LivingFallEvent event) {
      if (event.getEntity() instanceof Player player && ASUtils.hasCurio(player, (Item)ItemRegistries.IRONBOUND_FEATHER.get())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onExperienceGainedEvent(XpChange event) {
      LivingEntity attacker = event.getEntity();
      if (attacker.hasEffect(DTEPotionEffectRegistry.MEND_FLESH_EFFECT) && DTEServerConfig.mendFleshEXPGain) {
         float experienceGained = event.getAmount() / 2.0F;
         attacker.heal(experienceGained);
         int count = 8;
         float radius = 0.25F;
         ASUtils.spawnParticlesInCircle(count, radius, 0.5F, 0.1F, attacker, ParticleTypes.SCULK_SOUL);
         if (!attacker.level().isClientSide) {
            attacker.level().playSound(attacker, attacker.blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.PLAYERS, 1.0F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public static void livingDamageEventPre(Pre event) {
      LivingEntity entity = event.getEntity();
      DamageSource source = event.getSource();
      Entity attacker = event.getSource().getEntity();
      if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && entity instanceof LivingEntity
         && entity instanceof AscendedOneBoss ascendedOneBoss
         && DTEServerConfig.enableAscendedOneDamageCap) {
         float baseDamage = event.getOriginalDamage();
         float newDamage = ASUtils.basicDamageCap(baseDamage, 0.0F, DTEServerConfig.ascendedOneDamageCap);
         event.setNewDamage(newDamage);
      }

      if (entity != null
         && entity instanceof Player player
         && ASUtils.hasCurio(player, (Item)ItemRegistries.DIARY_OF_DECAY.get())
         && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.DIARY_OF_DECAY.get())
         && attacker instanceof LivingEntity livingAttacker) {
         livingAttacker.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.BLOOD_ROT_EFFECT, 100, 0, true, true, true));
         player.getCooldowns().addCooldown((Item)ItemRegistries.DIARY_OF_DECAY.get(), 100);
      }

      if (attacker != null
         && attacker instanceof Player player
         && ASUtils.hasCurio(player, (Item)ItemRegistries.TEMPESTUOUS_TOME.get())
         && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.TEMPESTUOUS_TOME.get())) {
         if (event.getSource().is(ISSDamageTypes.LIGHTNING_MAGIC)) {
            ChainLightning chainLightning = new ChainLightning(player.level(), player, entity);
            chainLightning.setDamage(8.0F);
            chainLightning.range = 10.0F;
            chainLightning.maxConnections = 7;
            player.level().addFreshEntity(chainLightning);
         }

         player.getCooldowns().addCooldown((Item)ItemRegistries.TEMPESTUOUS_TOME.get(), 100);
      }
   }
}
