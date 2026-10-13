package io.redspace.ironslib.kinetic_weapon;

import io.redspace.ironslib.kinetic_weapon.network.ClientboundKineticHitPacket;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Stop;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "irons_lib"
)
public final class KineticWeaponHandler {
   private static final float PLAYER_ACTION_FACTOR = 1.0F;
   private static final float MOB_ACTION_FACTOR = 0.2F;
   private static final float STAB_KNOCKBACK_BONUS = 0.4F;
   private static final Map<LivingEntity, Map<Entity, Long>> RECENT_KINETIC_ENEMIES = new WeakHashMap<>();

   @SubscribeEvent
   public static void onUseTick(Tick event) {
      LivingEntity entity = event.getEntity();
      ItemStack stack = event.getItem();
      if (!entity.level().isClientSide() && KineticWeapon.has(stack)) {
         EquipmentSlot weaponSlot = entity.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
         tickCharge(stack, entity, event.getDuration(), weaponSlot);
      }
   }

   @SubscribeEvent
   public static void onUseStop(Stop event) {
      endCharge(event);
   }

   @SubscribeEvent
   public static void onUseFinish(Finish event) {
      endCharge(event);
   }

   private static void endCharge(LivingEntityUseItemEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide() && KineticWeapon.has(event.getItem())) {
         endCharge(entity);
      }
   }

   public static void tickCharge(ItemStack stack, LivingEntity attacker, int ticksRemaining, EquipmentSlot weaponSlot) {
      KineticWeapon kineticWeapon = KineticWeapon.get(stack);
      if (kineticWeapon != null && attacker.level() instanceof ServerLevel serverLevel) {
         int ticksUsed = stack.getUseDuration(attacker) - ticksRemaining;
         if (ticksUsed <= 0) {
            RECENT_KINETIC_ENEMIES.put(attacker, new IdentityHashMap<>());
         }

         if (ticksUsed >= kineticWeapon.delayTicks()) {
            ticksUsed -= kineticWeapon.delayTicks();
            Vec3 attackerLookVector = attacker.getLookAngle();
            double attackerSpeedProjection = attackerLookVector.dot(getMotion(attacker));
            float actionFactor = attacker instanceof Player ? 1.0F : 0.2F;
            AttackRange attackRange = AttackRange.of(attacker, stack);
            double baseMobDamage = attacker.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            boolean affected = false;

            for (Entity hit : entitiesAlongReach(attacker, attackRange)) {
               Entity target = (Entity)(hit instanceof EnderDragonPart dragonPart ? dragonPart.parentMob : hit);
               if (!wasRecentlyStabbed(attacker, target, kineticWeapon.contactCooldownTicks())) {
                  rememberStabbedEntity(attacker, target);
                  double targetSpeedProjection = attackerLookVector.dot(getMotion(target));
                  double relativeSpeed = Math.max(0.0, attackerSpeedProjection - targetSpeedProjection);
                  boolean dealsDismount = testCondition(
                     kineticWeapon.dismountConditions(), ticksUsed, attackerSpeedProjection, relativeSpeed, (double)actionFactor
                  );
                  boolean dealsKnockback = testCondition(
                     kineticWeapon.knockbackConditions(), ticksUsed, attackerSpeedProjection, relativeSpeed, (double)actionFactor
                  );
                  boolean dealsDamage = testCondition(kineticWeapon.damageConditions(), ticksUsed, attackerSpeedProjection, relativeSpeed, (double)actionFactor);
                  if (dealsDismount || dealsKnockback || dealsDamage) {
                     float damageDealt = (float)baseMobDamage + (float)Mth.floor(relativeSpeed * (double)kineticWeapon.damageMultiplier());
                     affected |= stabAttack(serverLevel, attacker, weaponSlot, target, damageDealt, dealsDamage, dealsKnockback, dealsDismount);
                  }
               }
            }

            if (affected) {
               PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                  attacker, new ClientboundKineticHitPacket(attacker.getId(), kineticWeapon.hitSound()), new CustomPacketPayload[0]
               );
            }
         }
      }
   }

   public static void endCharge(LivingEntity attacker) {
      RECENT_KINETIC_ENEMIES.remove(attacker);
   }

   public static int stabbedEntities(LivingEntity attacker, Predicate<Entity> predicate) {
      Map<Entity, Long> memory = RECENT_KINETIC_ENEMIES.get(attacker);
      return memory == null ? 0 : (int)memory.keySet().stream().filter(predicate).count();
   }

   public static Vec3 getMotion(Entity entity) {
      Entity source = !(entity instanceof Player) && entity.isPassenger() ? entity.getRootVehicle() : entity;
      float ticksPerSecond = 20.0F;
      return source.getKnownMovement().scale((double)ticksPerSecond);
   }

   private static boolean testCondition(
      Optional<KineticWeapon.Condition> condition, int ticksUsed, double attackerSpeed, double relativeSpeed, double actionFactor
   ) {
      return condition.isPresent() && condition.get().test(ticksUsed, attackerSpeed, relativeSpeed, actionFactor);
   }

   private static List<Entity> entitiesAlongReach(LivingEntity attacker, AttackRange attackRange) {
      Level level = attacker.level();
      Vec3 look = attacker.getLookAngle();
      Vec3 eyePosition = attacker.getEyePosition();
      Vec3 from = eyePosition.add(look.scale((double)attackRange.effectiveMinRange(attacker)));
      double movementComponent = attacker.getKnownMovement().dot(look);
      Vec3 to = eyePosition.add(look.scale((double)attackRange.effectiveMaxRange(attacker) + Math.max(0.0, movementComponent)));
      BlockHitResult blocked = level.clip(new ClipContext(eyePosition, to, Block.COLLIDER, Fluid.NONE, attacker));
      if (blocked.getType() != Type.MISS) {
         to = blocked.getLocation();
         if (eyePosition.distanceToSqr(to) < eyePosition.distanceToSqr(from)) {
            return List.of();
         }
      }

      float margin = attackRange.hitboxMargin();
      AABB searchArea = AABB.ofSize(from, (double)margin, (double)margin, (double)margin).expandTowards(to.subtract(from)).inflate(1.0);
      List<Entity> collector = new ArrayList<>();

      for (Entity candidate : level.getEntities(attacker, searchArea, entity -> canHitEntity(attacker, entity))) {
         AABB hitbox = candidate.getBoundingBox();
         if (hitbox.contains(from) || hitbox.clip(from, to).isPresent()) {
            collector.add(candidate);
         } else if (margin > 0.0F && hitbox.inflate((double)margin).clip(from, to).isPresent()) {
            collector.add(candidate);
         }
      }

      return collector;
   }

   private static boolean canHitEntity(Entity attacker, Entity target) {
      if (!target.isInvulnerable() && target.isAlive() && target.canBeHitByProjectile()) {
         if (target instanceof Player targetPlayer && attacker instanceof Player attackingPlayer && !attackingPlayer.canHarmPlayer(targetPlayer)) {
            return false;
         }

         return !attacker.isPassengerOfSameVehicle(target);
      } else {
         return false;
      }
   }

   private static boolean wasRecentlyStabbed(LivingEntity attacker, Entity target, int allowedTime) {
      Map<Entity, Long> memory = RECENT_KINETIC_ENEMIES.get(attacker);
      if (memory == null) {
         return false;
      } else {
         Long stampedAt = memory.get(target);
         return stampedAt != null && attacker.level().getGameTime() - stampedAt < (long)allowedTime;
      }
   }

   private static void rememberStabbedEntity(LivingEntity attacker, Entity target) {
      Map<Entity, Long> memory = RECENT_KINETIC_ENEMIES.get(attacker);
      if (memory != null) {
         memory.put(target, attacker.level().getGameTime());
      }
   }

   private static boolean stabAttack(
      ServerLevel serverLevel,
      LivingEntity attacker,
      EquipmentSlot weaponSlot,
      Entity target,
      float baseDamage,
      boolean dealsDamage,
      boolean dealsKnockback,
      boolean dismounts
   ) {
      ItemStack weaponItem = attacker.getItemBySlot(weaponSlot);
      DamageSource damageSource = attacker instanceof Player player
         ? attacker.damageSources().playerAttack(player)
         : attacker.damageSources().mobAttack(attacker);
      float postEnchantmentDamage = EnchantmentHelper.modifyDamage(serverLevel, weaponItem, target, damageSource, baseDamage);
      Vec3 oldMovement = target.getDeltaMovement();
      boolean dealtDamage = dealsDamage && target.hurt(damageSource, postEnchantmentDamage);
      boolean affected = dealsKnockback | dealtDamage;
      if (dealsKnockback) {
         causeExtraKnockback(attacker, target, 0.4F + knockbackWith(serverLevel, attacker, weaponItem, target, damageSource));
      }

      if (dismounts && target.isPassenger()) {
         affected = true;
         target.stopRiding();
      }

      if (target instanceof LivingEntity livingTarget && attacker instanceof Player attackingPlayer) {
         weaponItem.hurtEnemy(livingTarget, attackingPlayer);
      }

      if (dealtDamage) {
         EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
      }

      if (!affected) {
         return false;
      } else {
         attacker.setLastHurtMob(target);
         return true;
      }
   }

   private static float knockbackWith(ServerLevel serverLevel, LivingEntity attacker, ItemStack weaponItem, Entity target, DamageSource damageSource) {
      float base = (float)attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      return EnchantmentHelper.modifyKnockback(serverLevel, weaponItem, target, damageSource, base);
   }

   private static void causeExtraKnockback(LivingEntity attacker, Entity target, float knockback) {
      if (knockback > 0.0F && target instanceof LivingEntity livingTarget) {
         livingTarget.knockback(
            (double)knockback,
            (double)Mth.sin(attacker.getYRot() * (float) (Math.PI / 180.0)),
            (double)(-Mth.cos(attacker.getYRot() * (float) (Math.PI / 180.0)))
         );
         attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6, 1.0, 0.6));
      }
   }
}
