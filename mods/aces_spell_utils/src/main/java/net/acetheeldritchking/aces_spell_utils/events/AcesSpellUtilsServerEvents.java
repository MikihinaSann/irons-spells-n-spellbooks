package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager.SelectionOption;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASTags;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class AcesSpellUtilsServerEvents {
   @SubscribeEvent
   public static void onUseItem(RightClickItem event) {
      Player player = event.getEntity();
      Level level = player.level();
      InteractionHand hand = event.getHand();
      ItemStack itemStack = player.getItemInHand(hand);
      ItemStack mainHand = player.getMainHandItem();
      if (itemStack.getItem() instanceof MagicGunItem gunItem && !gunItem.isHeavyGun()) {
         SpellSelectionManager spellSelectionManager = new SpellSelectionManager(player);
         SelectionOption selectionOption = spellSelectionManager.getSelection();
         if (selectionOption == null || selectionOption.spellData.equals(SpellData.EMPTY)) {
            return;
         }

         SpellData spellData = selectionOption.spellData;
         int spellLevel = spellData.getSpell().getLevelFor(spellData.getLevel(), player);
         if (level.isClientSide()) {
            if (ClientMagicData.isCasting()) {
               event.setCancellationResult(InteractionResult.CONSUME);
            } else {
               if (ClientMagicData.getPlayerMana() < spellData.getSpell().getManaCost(spellLevel)
                  || ClientMagicData.getCooldowns().isOnCooldown(spellData.getSpell())
                  || !ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellData.getSpell())) {
                  return;
               }

               event.setCancellationResult(InteractionResult.CONSUME);
            }
         }

         String castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND : SpellSelectionManager.OFFHAND;
         if (spellData.getSpell().attemptInitiateCast(itemStack, spellLevel, level, player, selectionOption.getCastSource(), true, castingSlot)) {
            event.setCancellationResult(InteractionResult.CONSUME);
         } else {
            event.setCancellationResult(InteractionResult.FAIL);
         }

         event.setCanceled(true);
      } else if (itemStack.getItem() instanceof MagicGunItem gunItem && hand.equals(InteractionHand.MAIN_HAND) && gunItem.isHeavyGun()) {
         SpellSelectionManager spellSelectionManagerx = new SpellSelectionManager(player);
         SelectionOption selectionOptionx = spellSelectionManagerx.getSelection();
         if (selectionOptionx == null || selectionOptionx.spellData.equals(SpellData.EMPTY)) {
            return;
         }

         SpellData spellDatax = selectionOptionx.spellData;
         int spellLevelx = spellDatax.getSpell().getLevelFor(spellDatax.getLevel(), player);
         if (level.isClientSide()) {
            if (ClientMagicData.isCasting()) {
               event.setCancellationResult(InteractionResult.CONSUME);
            } else {
               if (ClientMagicData.getPlayerMana() < spellDatax.getSpell().getManaCost(spellLevelx)
                  || ClientMagicData.getCooldowns().isOnCooldown(spellDatax.getSpell())
                  || !ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellDatax.getSpell())) {
                  return;
               }

               event.setCancellationResult(InteractionResult.CONSUME);
            }
         }

         if (spellDatax.getSpell()
            .attemptInitiateCast(itemStack, spellLevelx, level, player, selectionOptionx.getCastSource(), true, SpellSelectionManager.MAINHAND)) {
            event.setCancellationResult(InteractionResult.CONSUME);
         } else {
            event.setCancellationResult(InteractionResult.FAIL);
         }

         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void manaStealEvent(Post event) {
      Entity sourceEntity = event.getSource().getEntity();
      LivingEntity target = event.getEntity();
      Entity directEntity = event.getSource().getDirectEntity();
      if (sourceEntity instanceof LivingEntity livingEntity) {
         if (livingEntity instanceof ServerPlayer serverPlayer) {
            if (directEntity != null) {
               if (directEntity.getType().is(ASTags.MANA_STEAL_WHITELIST) || directEntity.is(serverPlayer)) {
                  AttributeInstance hasManaSteal = serverPlayer.getAttribute(ASAttributeRegistry.MANA_STEAL);
                  if (hasManaSteal != null) {
                     float manaStealAttr = (float)serverPlayer.getAttributeValue(ASAttributeRegistry.MANA_STEAL);
                     int maxAttackerMana = (int)serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                     MagicData attackerPlayerMagicData = MagicData.getPlayerMagicData(serverPlayer);
                     if (!(manaStealAttr <= 0.0F)) {
                        int addMana = (int)Math.min(manaStealAttr * event.getOriginalDamage() + attackerPlayerMagicData.getMana(), (float)maxAttackerMana);
                        attackerPlayerMagicData.setMana(addMana);
                        PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(attackerPlayerMagicData), new CustomPacketPayload[0]);
                        if (AcesSpellUtilsConfig.manaStealDrain && target instanceof ServerPlayer serverTargetPlayer) {
                           int maxTargetMana = (int)serverTargetPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                           MagicData targetPlayerMagicData = MagicData.getPlayerMagicData(serverTargetPlayer);
                           int subMana = (int)Math.min(manaStealAttr * event.getOriginalDamage() - attackerPlayerMagicData.getMana(), (float)maxAttackerMana);
                           if (maxTargetMana <= 0) {
                              return;
                           }

                           targetPlayerMagicData.setMana(subMana);
                           PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(targetPlayerMagicData), new CustomPacketPayload[0]);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void manaRendEvent(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      Entity directEntity = event.getSource().getDirectEntity();
      if (attacker instanceof LivingEntity livingEntity) {
         if (directEntity != null) {
            if (directEntity.getType().is(ASTags.MANA_REND_WHITELIST) || directEntity.is(attacker)) {
               AttributeInstance hasManaRend = livingEntity.getAttribute(ASAttributeRegistry.MANA_REND);
               AttributeInstance targetHasMana = victim.getAttribute(AttributeRegistry.MAX_MANA);
               if (hasManaRend != null && targetHasMana != null) {
                  double manaRendAttr = livingEntity.getAttributeValue(ASAttributeRegistry.MANA_REND);
                  double victimMaxMana = victim.getAttributeValue(AttributeRegistry.MAX_MANA);
                  double victimBaseMana = victim.getAttributeBaseValue(AttributeRegistry.MAX_MANA);
                  if (!(manaRendAttr <= 0.0) && !(victimMaxMana <= 0.0)) {
                     double bonusManaFromBase = victimMaxMana / victimBaseMana;
                     double step = bonusManaFromBase * 0.01;
                     double totalExtraDamagerPercent = 1.0 + step * manaRendAttr;
                     event.setAmount((float)(event.getAmount() * totalExtraDamagerPercent));
                     AcesSpellUtils.LOGGER.debug("Old Damage amount: " + event.getOriginalAmount());
                     AcesSpellUtils.LOGGER.debug("New Damage amount: " + event.getAmount());
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void goliathSlayerEvent(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      if (event.getSource().getEntity() instanceof LivingEntity livingEntity) {
         AttributeInstance hasGoliathSlayer = livingEntity.getAttribute(ASAttributeRegistry.GOLIATH_SLAYER);
         if (hasGoliathSlayer != null) {
            double goliathSlayerAttr = livingEntity.getAttributeValue(ASAttributeRegistry.GOLIATH_SLAYER);
            if (!(goliathSlayerAttr <= 0.0)) {
               if (victim.getType().is(ASTags.BOSS_LIKE_ENTITES)) {
                  float baseDamage = event.getOriginalAmount();
                  float bonusDamage = (float)(baseDamage * goliathSlayerAttr);
                  float totalDamage = baseDamage + bonusDamage;
                  event.setAmount(totalDamage);
                  AcesSpellUtils.LOGGER.debug("OG Damage: " + baseDamage);
                  AcesSpellUtils.LOGGER.debug("Bonus Damage: " + bonusDamage);
                  AcesSpellUtils.LOGGER.debug("Total Damage: " + event.getAmount());
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void hungerStealEvent(Pre event) {
      Entity sourceEntity = event.getSource().getEntity();
      LivingEntity target = event.getEntity();
      if (sourceEntity instanceof LivingEntity livingEntity) {
         if (livingEntity instanceof ServerPlayer serverPlayer) {
            AttributeInstance hasHungerSteal = serverPlayer.getAttribute(ASAttributeRegistry.HUNGER_STEAL);
            if (hasHungerSteal != null) {
               double hungerStealAttr = serverPlayer.getAttributeValue(ASAttributeRegistry.HUNGER_STEAL);
               if (!(hungerStealAttr <= 0.0)) {
                  FoodData playerFood = serverPlayer.getFoodData();
                  int foodLevel = playerFood.getFoodLevel();
                  int addFood = (int)Math.max(hungerStealAttr + foodLevel, (double)foodLevel);
                  playerFood.setFoodLevel(addFood);
                  if (target instanceof Player targetPlayer) {
                     FoodData targetFood = targetPlayer.getFoodData();
                     int targetFoodLevel = playerFood.getFoodLevel();
                     int subFood = (int)Math.min(hungerStealAttr - targetFoodLevel, (double)targetFoodLevel);
                     targetFood.setFoodLevel(subFood);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void spellResPenetrationEvent(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      if (event.getSource().getEntity() instanceof LivingEntity livingEntity) {
         AttributeInstance hasSpellResPen = livingEntity.getAttribute(ASAttributeRegistry.SPELL_RES_PENETRATION);
         if (hasSpellResPen != null) {
            double spellResPenAttr = livingEntity.getAttributeValue(ASAttributeRegistry.SPELL_RES_PENETRATION);
            double spellResAttr = victim.getAttributeValue(AttributeRegistry.SPELL_RESIST);
            if (!(spellResPenAttr <= 0.0)) {
               if (event.getSource() instanceof SpellDamageSource) {
                  float baseDamage = event.getOriginalAmount();
                  float bonusDamage = (float)(baseDamage * (spellResPenAttr + spellResAttr));
                  float totalDamage = baseDamage + bonusDamage;
                  event.setAmount(totalDamage);
                  AcesSpellUtils.LOGGER.debug("OG Damage: " + baseDamage);
                  AcesSpellUtils.LOGGER.debug("Bonus Damage: " + bonusDamage);
                  AcesSpellUtils.LOGGER.debug("Total Damage: " + event.getAmount());
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void evasiveEvent(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (victim instanceof LivingEntity) {
         AttributeInstance hasEvasive = victim.getAttribute(ASAttributeRegistry.EVASIVE);
         if (hasEvasive != null) {
            double evasiveAttr = victim.getAttributeValue(ASAttributeRegistry.EVASIVE);
            if (!(evasiveAttr <= 0.0)) {
               int postInvulTicks = event.getContainer().getPostAttackInvulnerabilityTicks();
               postInvulTicks *= (int)evasiveAttr;
               event.setInvulnerabilityTicks(postInvulTicks);
               AcesSpellUtils.LOGGER.debug("I Frames: " + victim.invulnerableTime);
               if (!victim.level().isClientSide()) {
                  MagicManager.spawnParticles(victim.level(), ParticleTypes.SMOKE, victim.getX(), victim.getY(), victim.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void magicDamageCriticalStrike(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      Entity directEntity = event.getSource().getDirectEntity();
      if (attacker instanceof LivingEntity livingEntity) {
         AttributeInstance hasMagicCritChance = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_CHANCE);
         AttributeInstance hasMagicCritDmg = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_DAMAGE);
         if (hasMagicCritChance != null) {
            if (hasMagicCritDmg != null) {
               double magicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_CHANCE);
               double magicCritDmg = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_DAMAGE);
               double baseMagicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_CHANCE);
               if (!(magicCritChance <= 0.05)) {
                  if (!(magicCritDmg <= 1.0)) {
                     if (event.getSource() instanceof SpellDamageSource) {
                        RandomSource random = victim.getRandom();

                        float damage;
                        for (damage = event.getAmount(); random.nextFloat() <= magicCritChance && magicCritDmg > 1.0; magicCritDmg *= 0.85F) {
                           magicCritChance--;
                           damage += (float)(event.getAmount() * (magicCritDmg - 1.0));
                        }

                        if (damage > event.getAmount() && !attacker.level().isClientSide()) {
                           AcesSpellUtils.LOGGER.debug("--CRIT!--");
                           attacker.level()
                              .playLocalSound(
                                 victim.getX(), victim.getY(), victim.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F, false
                              );
                           if (attacker instanceof Player player) {
                              player.crit(victim);
                           } else {
                              ASUtils.spawnParticlesInCircle(16, 0.75F, 1.5F, 0.15F, victim, ParticleTypes.CRIT);
                           }
                        }

                        event.setAmount(damage);
                        AcesSpellUtils.LOGGER.debug("OG Damage: " + event.getOriginalAmount());
                        AcesSpellUtils.LOGGER.debug("Damage: " + damage);
                        AcesSpellUtils.LOGGER.debug("Base Chance: " + baseMagicCritChance);
                        AcesSpellUtils.LOGGER.debug("Current Chance: " + magicCritChance);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void magicProjectileDamageCriticalStrike(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      Entity directEntity = event.getSource().getDirectEntity();
      if (attacker instanceof LivingEntity livingEntity) {
         if (directEntity instanceof AbstractMagicProjectile) {
            AttributeInstance hasMagicCritChance = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_CHANCE);
            AttributeInstance hasMagicCritDmg = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_DAMAGE);
            if (hasMagicCritChance != null) {
               if (hasMagicCritDmg != null) {
                  double magicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_CHANCE);
                  double magicCritDmg = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_DAMAGE);
                  double baseMagicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_CHANCE);
                  if (!(magicCritChance <= 0.05)) {
                     if (!(magicCritDmg <= 1.0)) {
                        if (event.getSource() instanceof SpellDamageSource && directEntity instanceof AbstractMagicProjectile) {
                           RandomSource random = victim.getRandom();

                           float damage;
                           for (damage = event.getAmount(); random.nextFloat() <= magicCritChance && magicCritDmg > 1.0; magicCritDmg *= 0.85F) {
                              magicCritChance--;
                              damage += (float)(event.getAmount() * (magicCritDmg - 1.0));
                           }

                           if (damage > event.getAmount() && !attacker.level().isClientSide()) {
                              AcesSpellUtils.LOGGER.debug("--PROJ CRIT!--");
                              attacker.level()
                                 .playLocalSound(
                                    victim.getX(), victim.getY(), victim.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F, false
                                 );
                              if (attacker instanceof Player player) {
                                 player.crit(victim);
                              } else {
                                 ASUtils.spawnParticlesInCircle(16, 0.75F, 1.5F, 0.15F, victim, ParticleTypes.CRIT);
                              }
                           }

                           event.setAmount(damage);
                           AcesSpellUtils.LOGGER.debug("OG Damage: " + event.getOriginalAmount());
                           AcesSpellUtils.LOGGER.debug("Damage: " + damage);
                           AcesSpellUtils.LOGGER.debug("Base Chance: " + baseMagicCritChance);
                           AcesSpellUtils.LOGGER.debug("Current Chance: " + magicCritChance);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void magicProjectileBonusDamage(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      Entity directEntity = event.getSource().getDirectEntity();
      if (attacker instanceof LivingEntity livingEntity) {
         if (directEntity instanceof AbstractMagicProjectile projectile) {
            AttributeInstance hasMagicProjDmg = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_PROJECTILE_DAMAGE);
            if (hasMagicProjDmg != null) {
               double magicProjDmg = 1.0 + livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_DAMAGE);
               if (!(magicProjDmg <= 0.0)) {
                  if (event.getSource() instanceof SpellDamageSource && directEntity instanceof AbstractMagicProjectile) {
                     float baseDamage = event.getOriginalAmount();
                     float totalDamage = (float)(baseDamage * magicProjDmg);
                     event.setAmount(totalDamage);
                     AcesSpellUtils.LOGGER.debug("OG Proj Damage: " + baseDamage);
                     AcesSpellUtils.LOGGER.debug("Bonus Proj Damage: " + baseDamage * (magicProjDmg - 1.0));
                     AcesSpellUtils.LOGGER.debug("Total Proj Damage: " + event.getAmount());
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void lifeRecovery(Post event) {
      LivingEntity victim = event.getEntity();
      if (event.getSource().getEntity() instanceof LivingEntity livingEntity) {
         AttributeInstance hasLifeRecovery = livingEntity.getAttribute(ASAttributeRegistry.LIFE_RECOVERY);
         if (hasLifeRecovery != null) {
            double lifeRecoveryAttr = livingEntity.getAttributeValue(ASAttributeRegistry.LIFE_RECOVERY);
            if (!(lifeRecoveryAttr <= 0.0)) {
               float MAX_HEALTH = livingEntity.getMaxHealth();
               float recoveryAmount = (float)(MAX_HEALTH * lifeRecoveryAttr);
               livingEntity.heal(recoveryAmount);
               AcesSpellUtils.LOGGER.debug("HP: " + livingEntity.getHealth());
               AcesSpellUtils.LOGGER.debug("Healed for: " + recoveryAmount);
            }
         }
      }
   }
}
