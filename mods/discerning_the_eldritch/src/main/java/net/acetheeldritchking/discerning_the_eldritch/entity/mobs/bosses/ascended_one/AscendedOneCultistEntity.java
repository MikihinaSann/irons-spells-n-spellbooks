package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;

public class AscendedOneCultistEntity extends AbstractSpellCastingMob {
   private static final EntityDataAccessor<Boolean> TRIGGERED = SynchedEntityData.defineId(AscendedOneCultistEntity.class, EntityDataSerializers.BOOLEAN);
   private int currentAnimTime;
   private final int animLength = 40;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("ascended_pose1");
   private final RawAnimation rise = RawAnimation.begin().thenPlay("ascended_pose2");

   public AscendedOneCultistEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setPersistenceRequired();
   }

   public boolean isPickable() {
      return true;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public boolean isPersistenceRequired() {
      return true;
   }

   protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      if (!this.triggered()) {
         this.trigger();
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   private void trigger() {
      if (!this.triggered()) {
         this.entityData.set(TRIGGERED, true);
      }
   }

   public boolean triggered() {
      return (Boolean)this.entityData.get(TRIGGERED);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         this.discard();
         return true;
      } else {
         Player player = this.level().getNearestPlayer(this, 8.0);
         if (player != null) {
            this.trigger();
         }

         return false;
      }
   }

   public void tick() {
      super.tick();
      if (this.triggered()) {
         this.currentAnimTime++;
         if (!this.level().isClientSide && this.currentAnimTime > 40) {
            AscendedOneBoss ascendedOneBoss = new AscendedOneBoss(this.level());
            ascendedOneBoss.moveTo(this.position());
            ascendedOneBoss.finalizeSpawn(
               (ServerLevelAccessor)this.level(), this.level().getCurrentDifficultyAt(ascendedOneBoss.getOnPos()), MobSpawnType.TRIGGERED, null
            );
            ascendedOneBoss.setPersistenceRequired();
            this.level().addFreshEntity(ascendedOneBoss);
            this.discard();
         }
      }
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistries.ASCENDED_ONE_HOOD.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistries.CAPELESS_ASCENDED_ONE_ROBES.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistries.ASCENDED_ONE_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistries.ASCENDED_ONE_GREAVES.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "idle", 0, this::idlePredicate));
   }

   private PlayState idlePredicate(AnimationState<AscendedOneCultistEntity> event) {
      if (this.triggered()) {
         event.getController().setAnimation(this.rise);
      } else {
         event.getController().setAnimation(this.idle);
      }

      return PlayState.CONTINUE;
   }

   public boolean shouldBeExtraAnimated() {
      return false;
   }

   public boolean shouldAlwaysAnimateHead() {
      return false;
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(TRIGGERED, false);
   }
}
