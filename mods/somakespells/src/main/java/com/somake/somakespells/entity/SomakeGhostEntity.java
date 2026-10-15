package com.somake.somakespells.entity;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;

public class SomakeGhostEntity extends AbstractSpellCastingMob {
   private static final int TARGET_LOCK_TICKS = 80;
   private static final int RETARGET_COOLDOWN_TICKS = 20;
   private static final int TARGET_INVALID_GRACE_TICKS = 20;
   private static final double RETARGET_MAX_DISTANCE = 44.0;
   private static final double RETARGET_MAX_DISTANCE_SQR = 1936.0;
   private static final double IDLE_DRIFT_HORIZONTAL_RADIUS = 6.0;
   private static final double IDLE_DRIFT_VERTICAL_RADIUS = 2.5;
   private static final double IDLE_DRIFT_SPEED = 0.55;
   private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.somake_ghost.idle");
   private static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("animation.somake_ghost.fly");
   private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.somake_ghost.attack");
   private final SomakeGhostEntity.Element element;
   private final List<AbstractSpell> attackSpells;
   private final List<AbstractSpell> supportSpells;
   private long targetLockUntilTick;
   private long nextRetargetAllowedTick;
   private int invalidTargetTicks;

   public SomakeGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level, SomakeGhostEntity.Element element) {
      super(entityType, level);
      this.element = element;
      this.attackSpells = buildAttackSpells(element);
      this.supportSpells = buildSupportSpells(element);
      this.xpReward = 15;
      this.moveControl = new SomakeGhostEntity.GhostMoveControl(this);
      this.initGhostGoals();
   }

   public SomakeGhostEntity.Element getElement() {
      return this.element;
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FOLLOW_RANGE, 28.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.15);
   }

   protected void registerGoals() {
   }

   private void initGhostGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector
         .addGoal(
            2,
            new WizardAttackGoal(this, 1.1, 24, 44).setSpells(this.attackSpells, List.of(), List.of(), this.supportSpells).setIsFlying().setAllowFleeing(true)
         );
      this.goalSelector.addGoal(6, new SomakeGhostEntity.RandomFloatAroundGoal(this));
      this.goalSelector.addGoal(7, new SomakeGhostEntity.GhostLookAtPlayerGoal(this, Player.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "ghost_controller", 0, this::animationPredicate));
   }

   private PlayState animationPredicate(AnimationState<SomakeGhostEntity> event) {
      if (this.isCasting()) {
         return event.setAndContinue(ATTACK_ANIM);
      } else {
         return this.getDeltaMovement().horizontalDistanceSqr() > 3.5E-4 ? event.setAndContinue(FLY_ANIM) : event.setAndContinue(IDLE_ANIM);
      }
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
      return false;
   }

   private static List<AbstractSpell> buildAttackSpells(SomakeGhostEntity.Element element) {
      return switch (element) {
         case AQUA -> List.of((AbstractSpell)ModSpells.WATER_BALL.get(), (AbstractSpell)ModSpells.TIDAL_GRASP.get());
         case FIRE -> List.of((AbstractSpell)SpellRegistry.FIREBOLT_SPELL.get(), (AbstractSpell)SpellRegistry.FIREBALL_SPELL.get());
         case ICE -> List.of((AbstractSpell)SpellRegistry.ICICLE_SPELL.get(), (AbstractSpell)SpellRegistry.CONE_OF_COLD_SPELL.get());
         case LIGHTNING -> List.of((AbstractSpell)SpellRegistry.LIGHTNING_BOLT_SPELL.get(), (AbstractSpell)SpellRegistry.CHAIN_LIGHTNING_SPELL.get());
         case HOLY -> List.of((AbstractSpell)SpellRegistry.GUIDING_BOLT_SPELL.get(), (AbstractSpell)SpellRegistry.SUNBEAM_SPELL.get());
         case ENDER -> List.of((AbstractSpell)SpellRegistry.TELEPORT_SPELL.get(), (AbstractSpell)SpellRegistry.BLACK_HOLE_SPELL.get());
         case BLOOD -> List.of(
            (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
            (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
            (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get()
         );
         case EVOCATION -> List.of((AbstractSpell)SpellRegistry.MAGIC_ARROW_SPELL.get(), (AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get());
         case NATURE -> List.of((AbstractSpell)SpellRegistry.POISON_ARROW_SPELL.get(), (AbstractSpell)SpellRegistry.ROOT_SPELL.get());
         case ELDRITCH -> List.of((AbstractSpell)SpellRegistry.ELDRITCH_BLAST_SPELL.get(), (AbstractSpell)SpellRegistry.SCULK_TENTACLES_SPELL.get());
         case SPIRIT -> List.of((AbstractSpell)ModSpells.RENDER_RUSH.get(), (AbstractSpell)ModSpells.SOUL_GRAB.get());
         case SYMMETRY -> List.of(
            MagicFromTheEastCompat.isLoaded() ? (AbstractSpell)ModSpells.MIRROR_STRIKE.get() : (AbstractSpell)ModSpells.AXE_CLEAVE.get(),
            (AbstractSpell)ModSpells.HALBERD_STRIKE.get()
         );
         case GEO -> List.of((AbstractSpell)SpellRegistry.STOMP_SPELL.get(), (AbstractSpell)SpellRegistry.EARTHQUAKE_SPELL.get());
         case SOUND -> List.of(soundAttackSpellA(), soundAttackSpellB());
      };
   }

   private static List<AbstractSpell> buildSupportSpells(SomakeGhostEntity.Element element) {
      return switch (element) {
         case AQUA -> List.of((AbstractSpell)ModSpells.THUNDER_CLOUD.get());
         case FIRE -> List.of((AbstractSpell)SpellRegistry.HEAT_SURGE_SPELL.get());
         case ICE -> List.of((AbstractSpell)ModSpells.PERMAFROST.get());
         case LIGHTNING -> List.of((AbstractSpell)ModSpells.LIGHTNING_BALL.get());
         case HOLY -> List.of((AbstractSpell)SpellRegistry.HASTE_SPELL.get());
         case ENDER -> List.of((AbstractSpell)ModSpells.ENDER_CORRUPTION.get());
         case BLOOD -> List.of((AbstractSpell)ModSpells.BLOOD_RUSH.get());
         case EVOCATION -> List.of((AbstractSpell)ModSpells.EVOCATION_FORTITUDE.get());
         case NATURE -> List.of((AbstractSpell)ModSpells.OVERGROWTH.get());
         case ELDRITCH -> List.of((AbstractSpell)ModSpells.ELDRITCH_GAMBIT.get());
         case SPIRIT -> ModSpells.SPIRIT_EMPOWERMENT != null
            ? List.of((AbstractSpell)ModSpells.SPIRIT_EMPOWERMENT.get())
            : List.of((AbstractSpell)ModSpells.PERMAFROST.get());
         case SYMMETRY -> MagicFromTheEastCompat.isLoaded()
            ? List.of((AbstractSpell)ModSpells.SYMMETRY_EMPOWERMENT.get())
            : List.of((AbstractSpell)ModSpells.LIGHTNING_BALL.get());
         case GEO -> List.of((AbstractSpell)ModSpells.EARTHBOUND.get());
         case SOUND -> List.of(soundSupportSpell());
      };
   }

   private static AbstractSpell soundAttackSpellA() {
      return TunesCompat.isLoaded() ? (AbstractSpell)ModSpells.RESONANT_PULSE.get() : (AbstractSpell)SpellRegistry.SONIC_BOOM_SPELL.get();
   }

   private static AbstractSpell soundAttackSpellB() {
      return TunesCompat.isLoaded() ? (AbstractSpell)ModSpells.RAM_TCHUM.get() : (AbstractSpell)SpellRegistry.SHOCKWAVE_SPELL.get();
   }

   private static AbstractSpell soundSupportSpell() {
      return TunesCompat.isLoaded() ? (AbstractSpell)ModSpells.REVERBERATION.get() : (AbstractSpell)SpellRegistry.HASTE_SPELL.get();
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.validateCurrentTarget();
         if (this.element == SomakeGhostEntity.Element.GEO && !ModList.get().isLoaded("gtbcs_geomancy_plus")) {
            this.discard();
         }

         if (this.element == SomakeGhostEntity.Element.SOUND && !TunesCompat.isLoaded()) {
            this.discard();
         }

         if ((this.element == SomakeGhostEntity.Element.SPIRIT || this.element == SomakeGhostEntity.Element.SYMMETRY) && !MagicFromTheEastCompat.isLoaded()) {
            this.discard();
         }
      }
   }

   private void syncRotationToMotionOrTarget(Vec3 motion, float maxTurnDegrees) {
      LivingEntity target = this.getTarget();
      float targetYaw;
      if (target != null && target.isAlive()) {
         targetYaw = (float)(-Mth.atan2(target.getX() - this.getX(), target.getZ() - this.getZ()) * (180.0 / Math.PI));
      } else {
         if (motion.horizontalDistanceSqr() < 1.0E-4) {
            return;
         }

         targetYaw = (float)(Mth.atan2(motion.z, motion.x) * (180.0 / Math.PI)) - 90.0F;
      }

      float smoothedYaw = Mth.approachDegrees(this.getYRot(), targetYaw, maxTurnDegrees);
      this.setYRot(smoothedYaw);
      this.setYBodyRot(smoothedYaw);
      this.yRotO = smoothedYaw;
      this.yBodyRotO = smoothedYaw;
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean hurt = super.hurt(source, amount);
      if (hurt && !this.level().isClientSide) {
         LivingEntity attacker = source.getEntity() instanceof LivingEntity living ? living : null;
         this.considerRetarget(attacker);
         return hurt;
      } else {
         return hurt;
      }
   }

   private void considerRetarget(LivingEntity candidate) {
      if (this.isRetargetCandidateValid(candidate)) {
         long now = this.level().getGameTime();
         LivingEntity current = this.getTarget();
         boolean hasValidCurrentTarget = this.isTargetStillValid(current);
         if (current != candidate) {
            if (!hasValidCurrentTarget || now >= this.targetLockUntilTick) {
               if (!hasValidCurrentTarget || now >= this.nextRetargetAllowedTick) {
                  this.setTarget(candidate);
                  this.targetLockUntilTick = now + 80L;
                  this.nextRetargetAllowedTick = now + 20L;
                  this.invalidTargetTicks = 0;
               }
            }
         }
      }
   }

   private void validateCurrentTarget() {
      LivingEntity current = this.getTarget();
      if (current == null) {
         this.invalidTargetTicks = 0;
      } else if (this.isTargetStillValid(current)) {
         this.invalidTargetTicks = 0;
      } else {
         this.invalidTargetTicks++;
         if (this.invalidTargetTicks >= 20) {
            this.setTarget(null);
            this.invalidTargetTicks = 0;
            this.targetLockUntilTick = 0L;
         }
      }
   }

   private boolean isRetargetCandidateValid(LivingEntity candidate) {
      if (candidate instanceof Player player) {
         if (player.isAlive() && !player.isSpectator() && !player.isCreative()) {
            return this.distanceToSqr(player) > 1936.0 ? false : this.hasLineOfSight(player);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isTargetStillValid(LivingEntity target) {
      if (target instanceof Player player) {
         if (player.isAlive() && !player.isSpectator() && !player.isCreative()) {
            return this.distanceToSqr(player) > 1936.0 ? false : this.hasLineOfSight(player);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isAlliedTo(Entity entity) {
      return entity instanceof LivingEntity living && living == this.getTarget() ? false : super.isAlliedTo(entity);
   }

   public static class AquaGhostEntity extends SomakeGhostEntity {
      public AquaGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.AQUA);
      }
   }

   public static class BloodGhostEntity extends SomakeGhostEntity {
      public BloodGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.BLOOD);
      }
   }

   public static class EldritchGhostEntity extends SomakeGhostEntity {
      public EldritchGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.ELDRITCH);
      }
   }

   public static enum Element {
      AQUA("aqua"),
      FIRE("fire"),
      ICE("ice"),
      LIGHTNING("lightning"),
      HOLY("holy"),
      ENDER("ender"),
      BLOOD("blood"),
      EVOCATION("evocation"),
      NATURE("nature"),
      ELDRITCH("eldritch"),
      SPIRIT("spirit"),
      SYMMETRY("symmetry"),
      GEO("geo"),
      SOUND("sound");

      private final String id;

      private Element(String id) {
         this.id = id;
      }

      public String id() {
         return this.id;
      }

      public String texturePath() {
         return "textures/entity/entity/" + this.id + "_ghost.png";
      }
   }

   public static class EnderGhostEntity extends SomakeGhostEntity {
      public EnderGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.ENDER);
      }
   }

   public static class EvocationGhostEntity extends SomakeGhostEntity {
      public EvocationGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.EVOCATION);
      }
   }

   public static class FireGhostEntity extends SomakeGhostEntity {
      public FireGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.FIRE);
      }
   }

   public static class GeoGhostEntity extends SomakeGhostEntity {
      public GeoGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.GEO);
      }
   }

   private static class GhostLookAtPlayerGoal extends LookAtPlayerGoal {
      private final SomakeGhostEntity ghost;

      private GhostLookAtPlayerGoal(SomakeGhostEntity ghost, Class<? extends LivingEntity> lookAtType, float lookDistance) {
         super(ghost, lookAtType, lookDistance);
         this.ghost = ghost;
      }

      public boolean canUse() {
         return this.ghost.getTarget() == null && super.canUse();
      }

      public boolean canContinueToUse() {
         return this.ghost.getTarget() == null && super.canContinueToUse();
      }
   }

   private static class GhostMoveControl extends MoveControl {
      private final SomakeGhostEntity ghost;
      private int courseChangeCooldown;

      private GhostMoveControl(SomakeGhostEntity ghost) {
         super(ghost);
         this.ghost = ghost;
      }

      public void tick() {
         if (this.operation == Operation.STRAFE) {
            float forward = this.strafeForwards;
            float right = this.strafeRight;
            float length = Mth.sqrt(forward * forward + right * right);
            if (length < 1.0F) {
               length = 1.0F;
            }

            float scale = (float)(0.08 * this.speedModifier) / length;
            forward *= scale;
            right *= scale;
            float sin = Mth.sin(this.ghost.getYRot() * (float) (Math.PI / 180.0));
            float cos = Mth.cos(this.ghost.getYRot() * (float) (Math.PI / 180.0));
            double motionX = forward * cos - right * sin;
            double motionZ = right * cos + forward * sin;
            Vec3 newMotion = this.ghost.getDeltaMovement().add(motionX, 0.0, motionZ);
            this.ghost.setDeltaMovement(newMotion);
            this.ghost.syncRotationToMotionOrTarget(newMotion, 10.0F);
            this.operation = Operation.WAIT;
         } else if (this.operation == Operation.MOVE_TO && this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = this.courseChangeCooldown + this.ghost.getRandom().nextInt(5) + 4;
            Vec3 wanted = new Vec3(this.wantedX - this.ghost.getX(), this.wantedY - this.ghost.getY(), this.wantedZ - this.ghost.getZ());
            double distance = wanted.length();
            if (distance < 1.0E-4) {
               this.operation = Operation.WAIT;
               return;
            }

            Vec3 direction = wanted.normalize();
            if (this.canReach(direction, Mth.ceil(distance))) {
               Vec3 newMotion = this.ghost.getDeltaMovement().add(direction.scale(0.08 * this.speedModifier));
               this.ghost.setDeltaMovement(newMotion);
               this.ghost.syncRotationToMotionOrTarget(newMotion, 12.0F);
            } else {
               this.operation = Operation.WAIT;
            }
         }
      }

      private boolean canReach(Vec3 step, int length) {
         AABB bounds = this.ghost.getBoundingBox();

         for (int i = 1; i < length; i++) {
            bounds = bounds.move(step);
            if (!this.ghost.level().noCollision(this.ghost, bounds)) {
               return false;
            }
         }

         return true;
      }
   }

   public static class HolyGhostEntity extends SomakeGhostEntity {
      public HolyGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.HOLY);
      }
   }

   public static class IceGhostEntity extends SomakeGhostEntity {
      public IceGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.ICE);
      }
   }

   public static class LightningGhostEntity extends SomakeGhostEntity {
      public LightningGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.LIGHTNING);
      }
   }

   public static class NatureGhostEntity extends SomakeGhostEntity {
      public NatureGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.NATURE);
      }
   }

   private static class RandomFloatAroundGoal extends Goal {
      private final SomakeGhostEntity ghost;

      private RandomFloatAroundGoal(SomakeGhostEntity ghost) {
         this.ghost = ghost;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         if (this.ghost.getTarget() == null && !this.ghost.isCasting()) {
            MoveControl moveControl = this.ghost.getMoveControl();
            if (!moveControl.hasWanted()) {
               return true;
            } else {
               double x = moveControl.getWantedX() - this.ghost.getX();
               double y = moveControl.getWantedY() - this.ghost.getY();
               double z = moveControl.getWantedZ() - this.ghost.getZ();
               double distanceSqr = x * x + y * y + z * z;
               return distanceSqr < 1.0 || distanceSqr > 144.0;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         RandomSource random = this.ghost.getRandom();
         double targetX = this.ghost.getX() + (random.nextDouble() * 2.0 - 1.0) * 6.0;
         double targetY = this.ghost.getY() + (random.nextDouble() * 2.0 - 1.0) * 2.5;
         double targetZ = this.ghost.getZ() + (random.nextDouble() * 2.0 - 1.0) * 6.0;
         this.ghost.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 0.55);
      }
   }

   public static class SoundGhostEntity extends SomakeGhostEntity {
      public SoundGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.SOUND);
      }
   }

   public static class SpiritGhostEntity extends SomakeGhostEntity {
      public SpiritGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.SPIRIT);
      }
   }

   public static class SymmetryGhostEntity extends SomakeGhostEntity {
      public SymmetryGhostEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
         super(entityType, level, SomakeGhostEntity.Element.SYMMETRY);
      }
   }
}
