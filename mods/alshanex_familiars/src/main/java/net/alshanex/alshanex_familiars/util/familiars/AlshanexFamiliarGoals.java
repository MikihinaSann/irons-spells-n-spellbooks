package net.alshanex.alshanex_familiars.util.familiars;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.block.pedestal.PedestalTile;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.alshanex.alshanex_familiars.entity.misc.IllusionistDecoy;
import net.alshanex.alshanex_familiars.util.AlshanexFamiliarsTags;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.entity.AbstractMeleeSpellCastingPet;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.util.HelperMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

public class AlshanexFamiliarGoals {
   public static class ApplyBonemealGoal extends Goal {
      private final AbstractSpellCastingPet controllerEntity;
      private final Supplier<LivingEntity> targetEntity;
      private final int interval;
      private int tickCounter;

      public ApplyBonemealGoal(AbstractSpellCastingPet controllerEntity, Supplier<LivingEntity> targetEntity, int interval) {
         this.controllerEntity = controllerEntity;
         this.interval = interval;
         this.targetEntity = targetEntity;
         this.tickCounter = 0;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         LivingEntity owner = this.targetEntity.get();
         if (owner == null || !(owner instanceof Player)) {
            return false;
         } else if (this.controllerEntity.getMovementDisabled()) {
            return false;
         } else {
            return !this.controllerEntity.canExecuteGoalsInHouse() ? false : this.controllerEntity.isAlive() && owner.isAlive();
         }
      }

      public void tick() {
         this.tickCounter++;
         if (this.tickCounter >= this.interval) {
            this.tickCounter = 0;
            BlockPos targetPos = this.controllerEntity.blockPosition();
            Level level = this.controllerEntity.level();
            Player player = (Player)this.targetEntity.get();
            this.controllerEntity.triggerAnim("interact_controller", "interact");

            for (int x = -5; x <= 5; x++) {
               for (int y = -5; y <= 5; y++) {
                  for (int z = -5; z <= 5; z++) {
                     BlockPos currentPos = targetPos.offset(x, y, z);
                     if (this.applyBonemeal(level, currentPos, player) && !level.isClientSide) {
                        level.levelEvent(1505, currentPos, 15);
                     }
                  }
               }
            }
         }
      }

      boolean applyBonemeal(Level level, BlockPos pos, Player player) {
         BlockState blockstate = level.getBlockState(pos);
         BonemealEvent event = EventHooks.fireBonemealEvent(player, level, pos, blockstate, new ItemStack(Items.BONE_MEAL));
         if (event.isCanceled()) {
            return event.isSuccessful();
         } else if (blockstate.is(AlshanexFamiliarsTags.DRUID_GROWABLES) && blockstate.getBlock() instanceof BonemealableBlock bonemealableblock) {
            if (level instanceof ServerLevel && bonemealableblock.isBonemealSuccess(level, level.random, pos, blockstate)) {
               bonemealableblock.performBonemeal((ServerLevel)level, level.random, pos, blockstate);
            }

            return true;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }
   }

   public static class CustomBreedGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private Animal animal;

      public CustomBreedGoal(AbstractSpellCastingPet pet) {
         this.pet = pet;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         FamiliarsLib.LOGGER.debug("Probando breed");
         if (this.animal != null) {
            return false;
         } else {
            FamiliarsLib.LOGGER.debug("No hay animal:" + this.animal);
            if (this.pet.getMovementDisabled()) {
               return false;
            } else {
               FamiliarsLib.LOGGER.debug("Movimiento permitido");
               if (!this.pet.canExecuteGoalsInHouse()) {
                  return false;
               } else {
                  FamiliarsLib.LOGGER.debug("Puede hacer goals en casa");
                  this.animal = this.pet
                     .level()
                     .getEntitiesOfClass(
                        Animal.class,
                        this.pet.getBoundingBox().inflate(10.0),
                        a -> a.canFallInLove()
                           && !a.isPanicking()
                           && !a.isBaby()
                           && a.getAge() == 0
                           && !a.getType().is(AlshanexFamiliarsTags.DRUID_CANNOT_BREED)
                     )
                     .stream()
                     .min(Comparator.comparingDouble(this.pet::distanceTo))
                     .orElse(null);
                  FamiliarsLib.LOGGER.debug("Encontrado animal?" + this.animal);
                  return this.animal != null;
               }
            }
         }
      }

      public boolean canContinueToUse() {
         return this.animal != null && this.animal.isAlive() && this.animal.canFallInLove() && this.pet.canExecuteGoalsInHouse();
      }

      public void start() {
         this.pet.getNavigation().moveTo(this.animal, 1.0);
      }

      public void tick() {
         if (this.animal != null) {
            if (this.pet.distanceTo(this.animal) < 2.0) {
               this.pet.getLookControl().setLookAt(this.animal, 10.0F, 10.0F);
               if (this.animal.canFallInLove() && this.animal.getAge() == 0) {
                  this.animal.setInLove(null);
               }
            } else {
               this.pet.getNavigation().moveTo(this.animal, 1.0);
            }
         }
      }

      public void stop() {
         this.animal = null;
      }
   }

   public static class DecoyGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private final int interval;
      private int tickCounter;

      public DecoyGoal(AbstractSpellCastingPet pet, int interval) {
         this.pet = pet;
         this.interval = interval;
         this.tickCounter = 0;
      }

      public boolean canUse() {
         return ++this.tickCounter >= this.interval && this.pet.getHealth() <= this.pet.getMaxHealth() / 2.0F && this.pet.canExecuteGoalsInHouse();
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.tickCounter = 0;
         this.pet.triggerAnim("interact_controller", "interact");
         Level level = this.pet.level();
         if (!level.isClientSide) {
            this.pet.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY, 100, 0, false, false, true));
            this.pet.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 5, false, false, true));
            IllusionistDecoy decoy = new IllusionistDecoy(this.pet.level(), this.pet);
            decoy.moveTo(this.pet.position());
            decoy.setYRot(this.pet.getYRot());
            this.pet.level().addFreshEntity(decoy);
         }
      }
   }

   public static class ProtectiveSwapGoal extends Goal {
      private final AbstractMeleeSpellCastingPet dragonWarrior;
      private final Supplier<LivingEntity> petOwner;
      private final double meleeRangeThreshold;
      private final int cooldownTicks;
      private int cooldownTimer = 0;
      private AbstractSpellCastingPet targetAlly = null;
      private LivingEntity attacker = null;

      public ProtectiveSwapGoal(AbstractMeleeSpellCastingPet dragonWarrior, Supplier<LivingEntity> owner, double meleeRangeThreshold, int cooldownSeconds) {
         this.dragonWarrior = dragonWarrior;
         this.petOwner = owner;
         this.meleeRangeThreshold = meleeRangeThreshold;
         this.cooldownTicks = cooldownSeconds * 20;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.cooldownTimer > 0) {
            return false;
         } else if (!this.dragonWarrior.getIsSitting() && this.dragonWarrior.isAlive()) {
            if (!this.dragonWarrior.isOnBed() && !this.dragonWarrior.getMovementDisabled() && !this.dragonWarrior.getIsInHouse()) {
               LivingEntity owner = this.petOwner.get();
               if (owner == null) {
                  return false;
               } else {
                  AABB searchArea = this.dragonWarrior.getBoundingBox().inflate(24.0);

                  for (AbstractSpellCastingPet ally : this.dragonWarrior
                     .level()
                     .getEntitiesOfClass(AbstractSpellCastingPet.class, searchArea, this::isValidAllyToProtect)) {
                     LivingEntity allyAttacker = this.findMeleeAttacker(ally);
                     if (allyAttacker != null) {
                        this.targetAlly = ally;
                        this.attacker = allyAttacker;
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private boolean isValidAllyToProtect(AbstractSpellCastingPet pet) {
         if (pet == this.dragonWarrior) {
            return false;
         } else if (!pet.isAlive() || pet.getIsSitting()) {
            return false;
         } else if (pet instanceof AbstractMeleeSpellCastingPet) {
            return false;
         } else {
            LivingEntity dragonOwner = this.petOwner.get();
            LivingEntity allyOwner = pet.getSummoner();
            return dragonOwner != null && allyOwner != null ? dragonOwner.getUUID().equals(allyOwner.getUUID()) : false;
         }
      }

      private LivingEntity findMeleeAttacker(AbstractSpellCastingPet ally) {
         LivingEntity lastHurtBy = ally.getLastHurtByMob();
         if (lastHurtBy != null && lastHurtBy.isAlive()) {
            if (ally.getLastHurtByMobTimestamp() + 60 < ally.tickCount) {
               return null;
            } else {
               double distance = lastHurtBy.distanceTo(ally);
               if (distance > this.meleeRangeThreshold) {
                  return null;
               } else {
                  LivingEntity owner = this.petOwner.get();
                  if (owner != null && lastHurtBy.getUUID().equals(owner.getUUID())) {
                     return null;
                  } else {
                     if (lastHurtBy instanceof AbstractSpellCastingPet otherPet) {
                        LivingEntity otherOwner = otherPet.getSummoner();
                        if (otherOwner != null && owner != null && otherOwner.getUUID().equals(owner.getUUID())) {
                           return null;
                        }
                     }

                     return lastHurtBy;
                  }
               }
            }
         } else {
            return null;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         if (this.targetAlly != null && this.attacker != null && this.dragonWarrior.level() instanceof ServerLevel serverLevel) {
            this.performSwap(serverLevel);
         }

         this.cooldownTimer = this.cooldownTicks;
         if (this.attacker != null && this.attacker.isAlive()) {
            this.dragonWarrior.setTarget(this.attacker);
         }

         this.targetAlly = null;
         this.attacker = null;
      }

      private void performSwap(ServerLevel serverLevel) {
         Vec3 dragonPos = this.dragonWarrior.position();
         Vec3 allyPos = this.targetAlly.position();
         float dragonYRot = this.dragonWarrior.getYRot();
         float allyYRot = this.targetAlly.getYRot();
         this.spawnTeleportParticles(serverLevel, dragonPos);
         this.spawnTeleportParticles(serverLevel, allyPos);
         serverLevel.playSound(null, dragonPos.x, dragonPos.y, dragonPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
         serverLevel.playSound(null, allyPos.x, allyPos.y, allyPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
         this.dragonWarrior.teleportTo(allyPos.x, allyPos.y, allyPos.z);
         this.dragonWarrior.setYRot(allyYRot);
         this.dragonWarrior.setYHeadRot(allyYRot);
         this.targetAlly.teleportTo(dragonPos.x, dragonPos.y, dragonPos.z);
         this.targetAlly.setYRot(dragonYRot);
         this.targetAlly.setYHeadRot(dragonYRot);
         this.spawnTeleportParticles(serverLevel, dragonPos);
         this.spawnTeleportParticles(serverLevel, allyPos);
      }

      private void spawnTeleportParticles(ServerLevel level, Vec3 pos) {
         for (int i = 0; i < 32; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.0;
            double offsetY = level.random.nextDouble() * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.0;
            level.sendParticles(ParticleTypes.PORTAL, pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }

      public void tick() {
      }

      public void tickCooldown() {
         if (this.cooldownTimer > 0) {
            this.cooldownTimer--;
         }
      }

      public boolean isOnCooldown() {
         return this.cooldownTimer > 0;
      }

      public int getRemainingCooldown() {
         return this.cooldownTimer;
      }
   }

   public static class RerollScrollGoal extends Goal {
      private final AbstractSpellCastingPet controllerEntity;
      private final Supplier<LivingEntity> targetEntity;
      private final int radius;
      private final int interval;
      private int tickCounter;

      public RerollScrollGoal(AbstractSpellCastingPet controllerEntity, Supplier<LivingEntity> targetEntity, int radius, int interval) {
         this.controllerEntity = controllerEntity;
         this.targetEntity = targetEntity;
         this.radius = radius;
         this.interval = interval;
         this.tickCounter = 0;
         this.setFlags(EnumSet.noneOf(Flag.class));
      }

      public boolean canUse() {
         LivingEntity owner = this.targetEntity.get();
         if (owner == null) {
            return false;
         } else if (this.controllerEntity.getMovementDisabled()) {
            return false;
         } else {
            return !this.controllerEntity.canExecuteGoalsInHouse() ? false : this.controllerEntity.isAlive() && owner.isAlive();
         }
      }

      public void tick() {
         if (this.tickCounter < this.interval) {
            this.tickCounter++;
         }

         if (this.tickCounter >= this.interval) {
            LivingEntity owner = this.targetEntity.get();
            BlockPos controllerPos = owner.blockPosition();
            PedestalTile nearestPedestal = null;
            double minDistance = Double.MAX_VALUE;

            for (int x = -this.radius; x <= this.radius; x++) {
               for (int y = -this.radius; y <= this.radius; y++) {
                  for (int z = -this.radius; z <= this.radius; z++) {
                     BlockPos currentPos = controllerPos.offset(x, y, z);
                     BlockState blockState = this.controllerEntity.level().getBlockState(currentPos);
                     if (blockState.is(BlockRegistry.PEDESTAL_BLOCK)) {
                        PedestalTile pedestal = (PedestalTile)this.controllerEntity.level().getBlockEntity(currentPos);
                        if (pedestal != null
                           && pedestal.getHeldItem() != null
                           && !pedestal.getHeldItem().isEmpty()
                           && pedestal.getHeldItem().getItem() instanceof Scroll) {
                           double distance = controllerPos.distSqr(currentPos);
                           if (distance < minDistance) {
                              minDistance = distance;
                              nearestPedestal = pedestal;
                           }
                        }
                     }
                  }
               }
            }

            if (nearestPedestal != null) {
               BlockState prevState = nearestPedestal.getBlockState();
               ItemStack item = nearestPedestal.getHeldItem();
               SpellData spellData = ISpellContainer.get(item).getSpellAtIndex(0);
               if (!HelperMethods.isSpellInTag(spellData.getSpell(), AlshanexFamiliarsTags.ARCHMAGE_CANNOT_REROLL) && spellData.getSpell().allowCrafting()
                  || AlshanexFamiliarsTags.isAlshanexFamiliarsSpell(spellData.getSpell())) {
                  this.controllerEntity.triggerAnim("interact_controller", "interact");
                  ItemStack finalItem = this.getResultSpell(spellData.getRarity());
                  SpellData spellDataFinal = ISpellContainer.get(finalItem).getSpellAtIndex(0);
                  nearestPedestal.setHeldItem(finalItem);
                  BlockState nextState = nearestPedestal.getBlockState();
                  this.controllerEntity.level.sendBlockUpdated(nearestPedestal.getBlockPos(), prevState, nextState, 2);
                  float radius = 2.0F;
                  MagicManager.spawnParticles(
                     this.controllerEntity.level,
                     new BlastwaveParticleOptions(spellDataFinal.getSpell().getSchoolType().getTargetingColor(), radius),
                     nearestPedestal.getBlockPos().getCenter().x,
                     nearestPedestal.getBlockPos().getCenter().y + 1.0,
                     nearestPedestal.getBlockPos().getCenter().z,
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true
                  );
                  this.tickCounter = 0;
               }
            }
         }
      }

      private ItemStack getResultSpell(SpellRarity rarity) {
         ItemStack resultStack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
         resultStack.setCount(1);
         List<AbstractSpell> spells = SpellRegistry.getEnabledSpells();
         List<AbstractSpell> finalSpells = spells.stream()
            .filter(spellx -> !HelperMethods.isSpellInTag(spellx, AlshanexFamiliarsTags.ARCHMAGE_CANNOT_CREATE))
            .toList();
         boolean spellSelected = false;
         Random random = new Random();
         int chance = random.nextInt(100);
         if (chance < 5 && rarity != SpellRarity.LEGENDARY) {
            int rarityValue = rarity.getValue() + 1;
            rarity = Arrays.stream(SpellRarity.values()).filter(rarityAux -> rarityAux.getValue() == rarityValue).findFirst().get();
         }

         while (!spellSelected) {
            Random spellRandom = new Random();
            int randomSpellIndex = spellRandom.nextInt(finalSpells.size());
            AbstractSpell spell = finalSpells.get(randomSpellIndex);
            if (spell.getMaxRarity() >= rarity.getValue() && spell.getMinRarity() <= rarity.getValue()) {
               ISpellContainer.createScrollContainer(spell, spell.getMinLevelForRarity(rarity), resultStack);
               spellSelected = true;
            }
         }

         return resultStack;
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }
   }

   public static class UndeadChaosGoal extends Goal {
      private final AbstractSpellCastingPet pet;
      private final Supplier<LivingEntity> petOwner;
      private final double range;
      private final int interval;
      private int tickCounter;

      public UndeadChaosGoal(AbstractSpellCastingPet pet, Supplier<LivingEntity> owner, double range, int interval) {
         this.pet = pet;
         this.petOwner = owner;
         this.range = range;
         this.interval = interval;
         this.tickCounter = 0;
      }

      public boolean canUse() {
         LivingEntity owner = this.petOwner.get();
         if (owner == null) {
            return false;
         } else {
            return !this.pet.canExecuteGoalsInHouse() ? false : ++this.tickCounter >= this.interval;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.tickCounter = 0;
         this.pet.triggerAnim("interact_controller", "interact");
         Level level = this.pet.level();
         if (!level.isClientSide) {
            LivingEntity owner = this.petOwner.get();
            List<LivingEntity> undeadEntities = level.getEntitiesOfClass(
               LivingEntity.class,
               this.pet.getBoundingBox().inflate(this.range),
               entity -> entity != this.pet
                  && entity != owner
                  && entity.getType().is(EntityTypeTags.UNDEAD)
                  && !entity.getType().is(AlshanexFamiliarsTags.NECROMANCER_BLACKLIST)
            );
            if (!undeadEntities.isEmpty()) {
               List<LivingEntity> potentialTargets = level.getEntitiesOfClass(
                  LivingEntity.class,
                  this.pet.getBoundingBox().inflate(this.range),
                  entity -> entity != this.pet && entity != owner && !this.pet.isAlliedTo(entity)
               );
               if (!potentialTargets.isEmpty()) {
                  for (LivingEntity undead : undeadEntities) {
                     if (undead instanceof Mob mob) {
                        if (undead instanceof IMagicSummon summon) {
                           if (summon.getSummoner() != null && summon.getSummoner() != owner && summon.getSummoner() != this.pet) {
                              this.selectTarget(potentialTargets, mob);
                           }
                        } else {
                           this.selectTarget(potentialTargets, mob);
                        }
                     }
                  }
               }
            }
         }
      }

      private void selectTarget(List<LivingEntity> potentialTargets, Mob mob) {
         List<LivingEntity> localTargets = new ArrayList<>(potentialTargets);
         localTargets.remove(mob);
         if (!localTargets.isEmpty()) {
            Random random = new Random();
            LivingEntity randomTarget = localTargets.get(random.nextInt(localTargets.size()));
            mob.setTarget(randomTarget);
         }
      }
   }
}
