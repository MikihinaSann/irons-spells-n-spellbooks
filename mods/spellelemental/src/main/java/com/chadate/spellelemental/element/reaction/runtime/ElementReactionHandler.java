package com.chadate.spellelemental.element.reaction.runtime;

import com.chadate.spellelemental.SpellElemental;
import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.element.reaction.data.ElementReactionRegistry;
import com.chadate.spellelemental.event.element.ElementDecaySystem;
import com.chadate.spellelemental.network.ElementData;
import com.chadate.spellelemental.register.ModAttributes;
import com.chadate.spellelemental.util.ReactionInjuryFormula;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

public class ElementReactionHandler {
   public static void damageTypeReaction(LivingIncomingDamageEvent event) {
      if (!ElementReactionRegistry.getDamageReactions().isEmpty()) {
         if (ElementReactionRegistry.hasAnyDamageCombos()) {
            LivingEntity victim = event.getEntity();
            DamageSource dmgSource = event.getSource();
            Entity direct = dmgSource.getDirectEntity();
            Entity attacker = dmgSource.getEntity();
            if (victim instanceof Player player) {
               boolean checkKnockout = false;
               if (checkKnockout) {
                  return;
               }
            }

            List<String> targetCandidates = collectPresentElements(victim);
            List<String> sourceCandidates = new ArrayList<>();
            if (attacker instanceof LivingEntity livingAttacker) {
               sourceCandidates = collectPresentElements(livingAttacker);
            } else if (direct instanceof LivingEntity livingDirect) {
               sourceCandidates = collectPresentElements(livingDirect);
            }

            boolean intraTargetMode = sourceCandidates.isEmpty() && !targetCandidates.isEmpty();
            if (!targetCandidates.isEmpty() || !sourceCandidates.isEmpty()) {
               String matchedSource = null;
               String matchedTarget = null;
               String reactionId = null;
               if (!intraTargetMode) {
                  label132:
                  for (String s : sourceCandidates) {
                     for (String t : targetCandidates) {
                        String rid = ElementReactionRegistry.findDamageReactionId(s, t);
                        if (rid != null) {
                           matchedSource = s;
                           matchedTarget = t;
                           reactionId = rid;
                           break label132;
                        }
                     }
                  }
               } else {
                  String inferredSource = null;
                  ElementContainerAttachment vc = (ElementContainerAttachment)victim.getData(SpellAttachments.ELEMENTS_CONTAINER);
                  long bestTime = Long.MIN_VALUE;

                  for (String c : targetCandidates) {
                     long tx = vc.getLastApplied(c);
                     if (tx > bestTime) {
                        bestTime = tx;
                        inferredSource = c;
                     }
                  }

                  if (inferredSource == null) {
                     inferredSource = inferSourceFromDamage(dmgSource, targetCandidates);
                  }

                  if (inferredSource != null) {
                     for (String tx : targetCandidates) {
                        if (!tx.equalsIgnoreCase(inferredSource)) {
                           String rid = ElementReactionRegistry.findDamageReactionId(inferredSource, tx);
                           if (rid != null) {
                              matchedSource = inferredSource;
                              matchedTarget = tx;
                              reactionId = rid;
                              break;
                           }
                        }
                     }
                  }

                  if (reactionId == null) {
                     label103:
                     for (int i = 0; i < targetCandidates.size(); i++) {
                        for (int j = 0; j < targetCandidates.size(); j++) {
                           if (i != j) {
                              String s = targetCandidates.get(i);
                              String txx = targetCandidates.get(j);
                              String rid = ElementReactionRegistry.findDamageReactionId(s, txx);
                              if (rid != null) {
                                 matchedSource = s;
                                 matchedTarget = txx;
                                 reactionId = rid;
                                 break label103;
                              }
                           }
                        }
                     }
                  }
               }

               if (reactionId != null) {
                  boolean shouldConsume = ElementReactionRegistry.shouldConsumeElements(reactionId);
                  int targetElementAmount = getElementValue(victim, matchedTarget);
                  int sourceElementAmount = getElementValue(victim, matchedSource);
                  if (shouldConsume) {
                     if (sourceElementAmount > 0) {
                        consumeElementOnEntity(victim, matchedSource, sourceElementAmount);
                     }

                     if (sourceElementAmount > 0) {
                        double ratio = ElementReactionRegistry.getConsumeRatio(matchedSource, matchedTarget);
                        int targetConsumeAmount = (int)Math.ceil(sourceElementAmount * ratio);
                        consumeElementOnEntity(victim, matchedTarget, targetConsumeAmount);
                     }
                  }

                  int afterVictimTarget = shouldConsume ? getElementValue(victim, matchedTarget) : targetElementAmount;
                  int actualConsumedTarget = shouldConsume ? Math.max(0, targetElementAmount - afterVictimTarget) : 0;
                  applyEffectsForReaction(event, reactionId, matchedSource, matchedTarget, attacker, victim, actualConsumedTarget);
               }
            }
         }
      }
   }

   private static int getElementValue(LivingEntity entity, String elementId) {
      if (entity != null && elementId != null) {
         ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
         return container.getValue(elementId);
      } else {
         return -1;
      }
   }

   public static void tickTypeReaction(Post event) {
      if (!ElementReactionRegistry.getTickReactions().isEmpty()) {
         if (event != null) {
            MinecraftServer server = event.getServer();

            try {
               Set<Integer> visited = new HashSet<>();

               for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                  if (player != null && !player.level().isClientSide()) {
                     Level level = player.level();
                     AABB aabb = player.getBoundingBox().inflate(32.0);

                     for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb, LivingEntity::isAlive)) {
                        if (entity != null) {
                           int id = entity.getId();
                           if (visited.add(id)) {
                              processTickReactionsOnEntity(entity);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var11) {
            }
         }
      }
   }

   private static void consumeElementOnEntity(LivingEntity entity, String elementId, int amount) {
      if (entity != null && elementId != null && amount > 0) {
         ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
         int cur = container.getValue(elementId);
         if (cur > 0) {
            int next = cur - amount;
            if (next <= 0) {
               container.remove(elementId);
               PacketDistributor.sendToAllPlayers(new ElementData(entity.getId(), elementId, 0), new CustomPacketPayload[0]);
            } else {
               container.setValue(elementId, next);
               PacketDistributor.sendToAllPlayers(new ElementData(entity.getId(), elementId, next), new CustomPacketPayload[0]);
            }
         }
      }
   }

   private static List<String> collectPresentElements(LivingEntity entity) {
      List<String> out = new ArrayList<>();
      if (entity == null) {
         return out;
      } else {
         ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
         Map<String, Integer> snapshot = container.snapshot();

         for (Entry<String, Integer> en : snapshot.entrySet()) {
            if (en.getValue() != null && en.getValue() > 0) {
               out.add(en.getKey().toLowerCase());
            }
         }

         return out;
      }
   }

   private static String inferSourceFromDamage(DamageSource source, List<String> candidatesLowercase) {
      if (source == null) {
         return null;
      } else {
         try {
            String id = source.getMsgId();
            if (id.isBlank()) {
               return null;
            } else {
               String low = id.toLowerCase();

               for (String c : candidatesLowercase) {
                  if (low.contains(c.toLowerCase())) {
                     return c;
                  }
               }

               return null;
            }
         } catch (Throwable var6) {
            return null;
         }
      }
   }

   private static void applyEffectsForReaction(
      LivingIncomingDamageEvent event, String reactionId, String matchedSource, String matchedTarget, Entity attacker, LivingEntity victim, int elementsAmount
   ) {
      if (reactionId != null) {
         List<ElementReactionRegistry.ReactionEffect> effects = ElementReactionRegistry.getDirectionalEffects(matchedSource, matchedTarget);
         if (effects.isEmpty()) {
            effects = ElementReactionRegistry.getEffects(reactionId);
         }

         if (!effects.isEmpty()) {
            float damage = event.getAmount();

            for (ElementReactionRegistry.ReactionEffect eff : effects) {
               if (eff != null && !eff.type.isEmpty()) {
                  String var11 = eff.type;
                  switch (var11) {
                     case "damage_amplify":
                        damage = applyDamageAmplify(damage, eff, attacker);
                        break;
                     case "aoe":
                        applyAoeEffect(event, damage, eff, attacker, victim, elementsAmount);
                        break;
                     case "attachment":
                        applyElementAttachmentEffect(attacker, victim, eff);
                  }
               }
            }

            if (damage != event.getAmount()) {
               event.setAmount(damage);
            }

            applyAttributeEffects(reactionId, matchedSource, matchedTarget, victim);
         }
      }
   }

   private static void applyElementAttachmentEffect(Entity attacker, LivingEntity victim, ElementReactionRegistry.ReactionEffect eff) {
      if (victim != null && !victim.level().isClientSide()) {
         if (eff != null && eff.elementId != null && !eff.elementId.isBlank()) {
            float chance = eff.elementChance <= 0.0F ? 1.0F : eff.elementChance;
            if (!(victim.getRandom().nextFloat() > chance)) {
               String elementKeyLower = eff.elementId.toLowerCase();
               int amount = Math.max(0, eff.elementAmount);
               if (amount != 0) {
                  ElementContainerAttachment container = (ElementContainerAttachment)victim.getData(SpellAttachments.ELEMENTS_CONTAINER);
                  container.setValue(elementKeyLower, amount);
                  long gameTime = victim.level().getGameTime();
                  int attackerId = attacker != null ? attacker.getId() : -1;
                  container.markAppliedWithAttacker(elementKeyLower, gameTime, attackerId);
                  ElementDecaySystem.track(victim);
                  PacketDistributor.sendToAllPlayers(new ElementData(victim.getId(), elementKeyLower, amount), new CustomPacketPayload[0]);
               }
            }
         }
      }
   }

   private static void applyAttributeEffects(String reactionId, String matchedSource, String matchedTarget, LivingEntity target) {
      if (reactionId != null && target != null && !target.level().isClientSide()) {
         List<ElementReactionRegistry.AttributeEffect> attributeEffects = ElementReactionRegistry.getDirectionalAttributeEffects(matchedSource, matchedTarget);
         boolean usingDirectional = !attributeEffects.isEmpty();
         if (attributeEffects.isEmpty()) {
            attributeEffects = ElementReactionRegistry.getAttributeEffects(reactionId);
         }

         if (!attributeEffects.isEmpty()) {
            for (ElementReactionRegistry.AttributeEffect effect : attributeEffects) {
               if (effect != null && !effect.attributeId().isEmpty()) {
                  ResourceLocation attrLocation;
                  try {
                     attrLocation = ResourceLocation.parse(effect.attributeId());
                  } catch (Exception var17) {
                     continue;
                  }

                  Registry<Attribute> attributeRegistry = target.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE);
                  Holder<Attribute> attributeHolder = (Holder<Attribute>)attributeRegistry.getHolder(attrLocation).orElse(null);
                  if (attributeHolder != null) {
                     String modifierOp = effect.operation().toLowerCase();

                     Operation operation = switch (modifierOp) {
                        case "multiply_base" -> Operation.ADD_MULTIPLIED_BASE;
                        case "multiply_total" -> Operation.ADD_MULTIPLIED_TOTAL;
                        default -> Operation.ADD_VALUE;
                     };
                     ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(
                        "spellelemental", "reaction_" + reactionId + "_" + effect.attributeId().replace(":", "_")
                     );
                     AttributeModifier modifier = new AttributeModifier(modifierId, effect.value(), operation);
                     AttributeInstance instance = target.getAttribute(attributeHolder);
                     if (instance != null) {
                        double beforeValue = instance.getValue();
                        instance.removeModifier(modifierId);
                        instance.addTransientModifier(modifier);
                        if (effect.duration() > 0) {
                           AttributeEffectManager.registerTimedEffect(target, attributeHolder, modifierId, effect.duration());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static float applyDamageAmplify(float baseDamage, ElementReactionRegistry.ReactionEffect eff, Entity attackerEntity) {
      float out = baseDamage;
      if ("QuickenReactionDamage".equals(eff.formula)) {
         float astral = 0.0F;
         if (attackerEntity instanceof LivingEntity attacker) {
            astral = (float)attacker.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
         }

         return ReactionInjuryFormula.QuickenReactionDamage(baseDamage, eff.multiplier, astral);
      } else {
         if (eff.multiplier != 0.0F) {
            out = baseDamage * eff.multiplier;
         }

         float factor = 1.0F;
         if (eff.formula != null && !eff.formula.isEmpty()) {
            float astral = 0.0F;
            if (attackerEntity instanceof LivingEntity attacker) {
               astral = (float)attacker.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
            }

            factor *= evaluateFormula(eff.formula, astral);
         }

         return out * factor;
      }
   }

   private static void applyAoeEffect(
      LivingIncomingDamageEvent event,
      float baseDamage,
      ElementReactionRegistry.ReactionEffect eff,
      Entity attackerEntity,
      LivingEntity victim,
      int elementsAmount
   ) {
      if (victim != null && !victim.level().isClientSide()) {
         float radius = eff.radius;
         if (!(radius <= 0.0F)) {
            float finalDamage;
            if ("CalculateOverloadDamage".equals(eff.formula)) {
               float astral = 0.0F;
               if (attackerEntity instanceof LivingEntity attacker) {
                  astral = (float)attacker.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
               }

               int effectiveAmount = elementsAmount == 0 ? 10 : elementsAmount;
               finalDamage = Math.max(0.0F, ReactionInjuryFormula.CalculateOverloadDamage(effectiveAmount, eff.multiplier, astral));
            } else {
               float out = baseDamage;
               if (eff.multiplier != 0.0F) {
                  out = baseDamage * eff.multiplier;
               }

               float factor = 1.0F;
               if (eff.formula != null && !eff.formula.isEmpty()) {
                  float astral = 0.0F;
                  if (attackerEntity instanceof LivingEntity attacker) {
                     astral = (float)attacker.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
                  }

                  factor *= evaluateFormula(eff.formula, astral);
               }

               finalDamage = Math.max(0.0F, out * factor);
            }

            if (!(finalDamage <= 0.0F)) {
               DamageSource src = event.getSource();
               if (eff.damageType != null && !eff.damageType.isEmpty()) {
                  try {
                     ResourceLocation rl = ResourceLocation.tryParse(eff.damageType);
                     if (rl == null) {
                        throw new IllegalArgumentException("Invalid damage_type RL: " + eff.damageType);
                     }

                     Registry<DamageType> reg = victim.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
                     ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, rl);
                     Holder<DamageType> holder = (Holder<DamageType>)reg.getHolder(key).orElse(null);
                     if (holder != null) {
                        Entity direct = event.getSource().getDirectEntity();
                        Entity causing = attackerEntity != null ? attackerEntity : event.getSource().getEntity();
                        src = new DamageSource(holder, direct, causing);
                     }
                  } catch (Throwable var15) {
                  }
               }

               AABB aabb = victim.getBoundingBox().inflate(radius);
               DamageSource srcFinal = src;
               LivingEntity attackerLiving = attackerEntity instanceof LivingEntity le ? le : null;

               for (LivingEntity ent : victim.level().getEntitiesOfClass(LivingEntity.class, aabb, LivingEntity::isAlive)) {
                  if ((eff.damageVictim || ent != victim)
                     && (eff.damageAttacker || attackerLiving == null || ent != attackerLiving)
                     && ent.invulnerableTime <= 0) {
                     ent.hurt(srcFinal, finalDamage);
                  }
               }
            }
         }
      }
   }

   private static void processTickReactionsOnEntity(LivingEntity entity) {
      if (entity != null && !entity.level().isClientSide()) {
         Set<String> allTick = ElementReactionRegistry.getTickReactions();
         if (!allTick.isEmpty()) {
            ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
            long time = entity.level().getGameTime();

            for (String rid : allTick) {
               List<ElementReactionRegistry.TickRule> rules = ElementReactionRegistry.getTickRules(rid);
               if (!rules.isEmpty()) {
                  for (ElementReactionRegistry.TickRule rule : rules) {
                     if (rule.interval() <= 1 || time % rule.interval() == 0L) {
                        boolean meetsRequirements = true;

                        for (Entry<String, Integer> req : rule.requirements().entrySet()) {
                           String elem = req.getKey();
                           int need = Math.max(0, req.getValue());
                           if (need != 0) {
                              int cur = container.getValue(elem);
                              if (cur < need) {
                                 meetsRequirements = false;
                                 break;
                              }
                           }
                        }

                        if (meetsRequirements) {
                           int totalConsumed = 0;

                           for (Entry<String, Integer> con : rule.consume().entrySet()) {
                              String elem = con.getKey();
                              int want = Math.max(0, con.getValue());
                              int before = container.getValue(elem);
                              if (before > 0 && want != 0) {
                                 int real = Math.min(before, want);
                                 consumeElementOnEntity(entity, elem, real);
                                 totalConsumed += real;
                              }
                           }

                           if (!rule.effects().isEmpty()) {
                              for (ElementReactionRegistry.ReactionEffect eff : rule.effects()) {
                                 if (eff != null && !eff.type.isEmpty()) {
                                    String var24 = eff.type;
                                    switch (var24) {
                                       case "aoe":
                                          applyAoeEffectFromTick(entity, eff, totalConsumed);
                                          break;
                                       case "extra":
                                          applyExtraDamageFromTick(entity, eff, totalConsumed);
                                          break;
                                       case "attachment":
                                          applyAttachmentFromTick(entity, eff);
                                          break;
                                       case "potion":
                                          applyPotionEffectFromTick(entity, eff);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void applyAoeEffectFromTick(LivingEntity center, ElementReactionRegistry.ReactionEffect eff, int elementsAmount) {
      if (center != null && !center.level().isClientSide()) {
         float radius = eff.radius;
         if (!(radius <= 0.0F)) {
            float astral = 0.0F;
            if (center instanceof LivingEntity) {
               astral = (float)center.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
            }

            float finalDamage;
            if (eff.formula == null || eff.formula.isEmpty()) {
               float out = elementsAmount;
               if (eff.multiplier != 0.0F) {
                  out *= eff.multiplier;
               }

               finalDamage = Math.max(0.0F, out);
            } else if ("CalculateOverloadDamage".equals(eff.formula)) {
               int effectiveAmount = elementsAmount == 0 ? 10 : elementsAmount;
               finalDamage = Math.max(0.0F, ReactionInjuryFormula.CalculateOverloadDamage(effectiveAmount, eff.multiplier, astral));
            } else {
               float out = elementsAmount;
               if (eff.multiplier != 0.0F) {
                  out *= eff.multiplier;
               }

               float factor = evaluateFormula(eff.formula, astral);
               finalDamage = Math.max(0.0F, out * factor);
            }

            if (!(finalDamage <= 0.0F)) {
               DamageSource src = null;

               try {
                  if (eff.damageType != null && !eff.damageType.isEmpty()) {
                     ResourceLocation rl = ResourceLocation.tryParse(eff.damageType);
                     if (rl != null) {
                        Registry<DamageType> reg = center.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
                        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, rl);
                        Holder<DamageType> holder = (Holder<DamageType>)reg.getHolder(key).orElse(null);
                        if (holder != null) {
                           src = new DamageSource(holder, center, center);
                        }
                     }
                  }
               } catch (Throwable var11) {
               }

               if (src != null) {
                  AABB aabb = center.getBoundingBox().inflate(radius);
                  DamageSource srcFinal = src;

                  for (LivingEntity ent : center.level().getEntitiesOfClass(LivingEntity.class, aabb, LivingEntity::isAlive)) {
                     if ((eff.damageAttacker || ent != center) && ent.invulnerableTime <= 0) {
                        ent.hurt(srcFinal, finalDamage);
                     }
                  }
               }
            }
         }
      }
   }

   private static void applyExtraDamageFromTick(LivingEntity center, ElementReactionRegistry.ReactionEffect eff, int elementsAmount) {
      if (center != null && !center.level().isClientSide()) {
         float astral = 0.0F;
         ElementContainerAttachment container = (ElementContainerAttachment)center.getData(SpellAttachments.ELEMENTS_CONTAINER);
         LivingEntity attacker = null;

         for (String elementId : container.snapshot().keySet()) {
            int attackerId = container.getLastAttackerId(elementId);
            if (attackerId != -1 && center.level().getEntity(attackerId) instanceof LivingEntity livingAttacker) {
               attacker = livingAttacker;
               break;
            }
         }

         if (attacker != null) {
            astral = (float)attacker.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
         } else {
            astral = (float)center.getAttributeValue(ModAttributes.ASTRAL_BLESSING);
         }

         float finalDamage;
         if (eff.formula == null || eff.formula.isEmpty()) {
            float out = elementsAmount;
            if (eff.multiplier != 0.0F) {
               out *= eff.multiplier;
            }

            finalDamage = Math.max(0.0F, out);
         } else if ("CalculateOverloadDamage".equals(eff.formula)) {
            int effectiveAmount = elementsAmount == 0 ? 10 : elementsAmount;
            finalDamage = Math.max(0.0F, ReactionInjuryFormula.CalculateOverloadDamage(effectiveAmount, eff.multiplier, astral));
         } else {
            float out = elementsAmount;
            if (eff.multiplier != 0.0F) {
               out *= eff.multiplier;
            }

            float factor = evaluateFormula(eff.formula, astral);
            finalDamage = Math.max(0.0F, out * factor);
         }

         if (!(finalDamage <= 0.0F)) {
            DamageSource src = null;

            try {
               if (eff.damageType != null && !eff.damageType.isEmpty()) {
                  ResourceLocation rl = ResourceLocation.tryParse(eff.damageType);
                  if (rl != null) {
                     Registry<DamageType> reg = center.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
                     ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, rl);
                     Holder<DamageType> holder = (Holder<DamageType>)reg.getHolder(key).orElse(null);
                     if (holder != null) {
                        src = new DamageSource(holder, center, center);
                     }
                  }
               }
            } catch (Throwable var12) {
            }

            if (src != null) {
               center.hurt(src, finalDamage);
            }
         }
      }
   }

   private static void applyAttachmentFromTick(LivingEntity entity, ElementReactionRegistry.ReactionEffect eff) {
      if (entity != null && !entity.level().isClientSide()) {
         if (eff != null) {
            String element = eff.elementId;
            int amount = Math.max(0, eff.elementAmount);
            float chance = eff.elementChance <= 0.0F ? 1.0F : eff.elementChance;
            if (element != null && !element.isEmpty() && amount != 0) {
               try {
                  if (entity.getRandom().nextFloat() > chance) {
                     return;
                  }
               } catch (Throwable var11) {
               }

               ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
               String key = element.toLowerCase();
               container.setValue(key, amount);
               long gameTime = entity.level().getGameTime();
               container.markAppliedWithAttacker(key, gameTime, -1);
               ElementDecaySystem.track(entity);

               try {
                  PacketDistributor.sendToAllPlayers(new ElementData(entity.getId(), key, amount), new CustomPacketPayload[0]);
               } catch (Throwable var10) {
               }
            }
         }
      }
   }

   private static void applyPotionEffectFromTick(LivingEntity entity, ElementReactionRegistry.ReactionEffect eff) {
      if (entity != null && !entity.level().isClientSide()) {
         if (eff.potionId != null && !eff.potionId.isEmpty()) {
            float chance = Math.max(0.0F, Math.min(1.0F, eff.potionChance));

            try {
               if (entity.getRandom().nextFloat() > chance) {
                  return;
               }
            } catch (Throwable var9) {
               return;
            }

            try {
               ResourceLocation potionEffectId = ResourceLocation.parse(eff.potionId);
               MobEffect potionEffect = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(potionEffectId);
               if (potionEffect == null) {
                  SpellElemental.LOGGER.warn("未找到药水效果: {}", eff.potionId);
                  return;
               }

               int duration = Math.max(1, eff.potionDuration);
               int amplifier = Math.max(0, eff.potionLevel);
               MobEffectInstance effectInstance = new MobEffectInstance(Holder.direct(potionEffect), duration, amplifier, false, true, true);
               entity.addEffect(effectInstance);
            } catch (Exception var8) {
               SpellElemental.LOGGER.error("应用药水效果时出错: {}", eff.potionId, var8);
            }
         }
      }
   }

   private static float evaluateFormula(String formula, float astral) {
      return "AmplifiedReactionBonus".equals(formula) ? ReactionInjuryFormula.AmplifiedReactionBonus(astral) : 1.0F;
   }
}
