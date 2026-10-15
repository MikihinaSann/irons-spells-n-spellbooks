package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.BossbarManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.api.util.BossbarManager.BossbarSprite;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingBoss;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.cultist.CultistEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.ExtendedServerBossEvent;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
import io.redspace.ironsspellbooks.network.EntityEventPacket;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericBossEntity;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericBossEntity.Phase;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.goals.WizardSpellComboGoal;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.BossMusicManager;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistCaptainEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistMageEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistWitchEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.minibosses.blood_matriarch.BloodMatriarchEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;

public class AscendedOneBoss extends GenericBossEntity implements IAnimatedAttacker {
   private static final BossbarSprite BOSSBAR_SPRITE = new BossbarSprite(DiscerningTheEldritch.id("boss_bars/ascended_one_boss_bar"), 192, 18, 3, -1);
   private ExtendedServerBossEvent bossEvent;
   private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(AscendedOneBoss.class, EntityDataSerializers.INT);
   public static final byte CLIENT_STOP_TRACKING = 0;
   public static final byte CLIENT_START_TRACKING = 1;
   public static SoundEvent bossMusic = (SoundEvent)DTESoundRegistry.ASCENDED_ONE_THEME.get();
   public static SoundEvent bossTransitionMusic = (SoundEvent)DTESoundRegistry.ASCENDED_ONE_TRANSITION.get();
   public static SoundEvent bossFinalMusic = (SoundEvent)DTESoundRegistry.ASCENDED_ONE_FINAL_PHASE_FULL.get();
   public int transitionAnimationTime = 73;
   public int deathAnimationTime = 55;
   public int jumpAnimationTime = 20;
   SimpleContainer deathLoot = null;
   private int destroyBlockDelay;
   private final RawAnimation transitionPhaseAnimation = RawAnimation.begin().thenPlay("ascended_desperation");
   private final RawAnimation deathAnimation = RawAnimation.begin().thenPlay("ascended_death");
   private final RawAnimation jumpAnimation = RawAnimation.begin().thenPlay("ascended_jump");
   private final AnimationController<AscendedOneBoss> transitionController = new AnimationController(
      this, "ascended_one_transition", 0, this::transitionPredicate
   );
   private final AnimationController<AscendedOneBoss> deathController = new AnimationController(this, "ascended_one_death", 0, this::deathPredicate);
   private final AnimationController<AscendedOneBoss> jumpController = new AnimationController(this, "ascended_one_jump", 0, this::jumpPredicate);
   RawAnimation animationToPlay = null;
   public boolean isJumping;

   public AscendedOneBoss(Level level) {
      this((EntityType<? extends AbstractSpellCastingMob>)DTEEntityRegistry.ASCENDED_ONE.get(), level);
      this.setPersistenceRequired();
   }

   public AscendedOneBoss(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setPersistenceRequired();
      this.xpReward = 60;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
      this.createBossEvent();
   }

   public boolean hasCustomMusic() {
      return true;
   }

   public boolean changeMusicOnPhaseChange() {
      return true;
   }

   public boolean hasTransitionPhase() {
      return true;
   }

   public int usePhaseAsTransition() {
      return 2;
   }

   public int usePhaseForMusicChange() {
      return 3;
   }

   public SoundEvent getBossMusic() {
      return bossMusic;
   }

   public SoundEvent getTransitionMusic() {
      return bossTransitionMusic;
   }

   public SoundEvent getOtherPhaseMusic() {
      return bossFinalMusic;
   }

   public void handleClientEvent(byte eventId) {
      switch (eventId) {
         case 0:
            BossbarManager.stopTracking(this.uuid);
            BossMusicManager.stop(this);
            break;
         case 1:
            BossbarManager.startTracking(this.uuid, BOSSBAR_SPRITE);
            BossMusicManager.createOrResumeInstance(this);
      }
   }

   public void startSeenByPlayer(ServerPlayer serverPlayer) {
      super.startSeenByPlayer(serverPlayer);
      this.bossEvent.addPlayer(serverPlayer);
      PacketDistributor.sendToPlayer(serverPlayer, new EntityEventPacket(this, (byte)1), new CustomPacketPayload[0]);
   }

   public void stopSeenByPlayer(ServerPlayer serverPlayer) {
      super.stopSeenByPlayer(serverPlayer);
      this.bossEvent.removePlayer(serverPlayer);
      PacketDistributor.sendToPlayer(serverPlayer, new EntityEventPacket(this, (byte)0), new CustomPacketPayload[0]);
   }

   public void aiStep() {
      super.aiStep();
      this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float from, float to, float maxDelta) {
            return super.rotateTowards(from, to, maxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return AscendedOneBoss.this.getTarget() == null;
         }
      };
   }

   protected MoveControl createMoveControl() {
      return new MoveControl(this) {
         protected float rotlerp(float sourceAngle, float targetAngle, float maximumChange) {
            double x = this.wantedX - this.mob.getX();
            double z = this.wantedZ - this.mob.getZ();
            return x * x + z * z < 0.5 ? sourceAngle : super.rotlerp(sourceAngle, targetAngle, maximumChange * 0.25F);
         }
      };
   }

   protected PathNavigation createNavigation(Level level) {
      return new NotIdioticNavigation(this, level);
   }

   protected void registerGoals() {
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.firstPhaseGoals();
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, KeeperEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PriestEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, CultistEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FireBossEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DeadKingBoss.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodCultistCaptainEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodCultistMageEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodCultistWitchEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, BloodMatriarchEntity.class, true));
   }

   private void firstPhaseGoals() {
      this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
      this.goalSelector.removeAllGoals(x -> true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 3, 80, 150, 1));
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.25, 50, 75)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.SONIC_BOOM_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SHOCKWAVE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHAIN_LIGHTNING_SPELL.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHARGE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(),
                     (AbstractSpell)SpellRegistry.THUNDERSTORM_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get()),
                  List.of(
                     (AbstractSpell)SpellRegistry.RAISE_DEAD_SPELL.get(),
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SACRIFICE_SPELL.get()
                  )
               )
               .setSingleUseSpell(SpellRegistries.BOOGIE_WOOGIE.get(), 70, 100, 3, 5)
               .setSpellQuality(1.0F, 1.0F)
         );
      this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32.0F, 0.9));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   private void secondPhaseGoals() {
      this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
      this.goalSelector.removeAllGoals(x -> true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 3, 50, 80, 3));
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.25, 35, 50)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.SONIC_BOOM_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SHOCKWAVE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get(),
                     (AbstractSpell)SpellRegistry.MAGIC_ARROW_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHAIN_LIGHTNING_SPELL.get(),
                     (AbstractSpell)SpellRegistry.LIGHTNING_LANCE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SUMMON_SWORDS.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHARGE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLIGHT_SPELL.get(),
                     (AbstractSpell)SpellRegistry.THUNDERSTORM_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get()),
                  List.of(
                     (AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(),
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SACRIFICE_SPELL.get()
                  )
               )
               .setSingleUseSpell(SpellRegistries.CONJURE_FORSAKE_AID.get(), 70, 100, 3, 5)
               .setSpellQuality(1.2F, 1.2F)
         );
      this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32.0F, 0.9));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   private void finalPhaseGoals() {
      this.goalSelector.getAvailableGoals().forEach(WrappedGoal::stop);
      this.goalSelector.removeAllGoals(x -> true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 3, 5, 30, 50, 5));
      this.goalSelector
         .addGoal(
            3,
            new WizardSpellComboGoal(
               this,
               List.of(
                  (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(), (AbstractSpell)SpellRegistry.ROOT_SPELL.get(), SpellRegistries.ESOTERIC_EDGE.get()
               ),
               1.3F,
               1.3F,
               100,
               250
            )
         );
      this.goalSelector
         .addGoal(
            3,
            new WizardSpellComboGoal(
               this,
               List.of(
                  (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                  (AbstractSpell)SpellRegistry.BLIGHT_SPELL.get(),
                  (AbstractSpell)SpellRegistry.SLOW_SPELL.get(),
                  (AbstractSpell)SpellRegistry.LIGHTNING_LANCE_SPELL.get()
               ),
               1.3F,
               1.3F,
               100,
               250
            )
         );
      this.goalSelector
         .addGoal(
            3,
            new WizardSpellComboGoal(
               this,
               List.of(
                  (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                  (AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(),
                  (AbstractSpell)SpellRegistry.CHARGE_SPELL.get()
               ),
               1.3F,
               1.3F,
               75,
               100
            )
         );
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.25, 20, 35)
               .setSpells(
                  List.of(
                     SpellRegistries.ESOTERIC_EDGE.get(),
                     (AbstractSpell)SpellRegistry.SONIC_BOOM_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SHOCKWAVE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.LIGHTNING_LANCE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SCULK_TENTACLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.FIRE_ARROW_SPELL.get(),
                     (AbstractSpell)SpellRegistry.MAGIC_ARROW_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SUMMON_SWORDS.get(),
                     (AbstractSpell)SpellRegistry.SACRIFICE_SPELL.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHARGE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLIGHT_SPELL.get(),
                     (AbstractSpell)SpellRegistry.SLOW_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(),
                     SpellRegistries.CONJURE_FORSAKE_AID.get(),
                     (AbstractSpell)SpellRegistry.THUNDERSTORM_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(), (AbstractSpell)SpellRegistry.PLANAR_SIGHT_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(), (AbstractSpell)SpellRegistry.SACRIFICE_SPELL.get())
               )
               .setSingleUseSpell(SpellRegistries.ABRACADABRA.get(), 70, 100, 3, 5)
               .setSpellQuality(1.3F, 1.3F)
         );
      this.goalSelector.addGoal(5, new PatrolNearLocationGoal(this, 32.0F, 0.9));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   public void tick() {
      super.tick();
      float health = this.getHealth();
      float MAX_HEALTH = this.getMaxHealth();
      float halfHealth = MAX_HEALTH / 2.0F;
      float almostDead = MAX_HEALTH / 4.0F;
      if (this.isPhase(Phase.FirstPhase)) {
         if (this.getHealth() <= halfHealth) {
            int radius = 15;

            for (LivingEntity targets : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
               if (targets instanceof ServerPlayer player) {
                  this.playSound((SoundEvent)DTESoundRegistry.ASCENDED_ONE_TAUNT_ONE.get(), 1.5F, 1.0F);
                  player.connection
                     .send(
                        new ClientboundSetTitleTextPacket(
                           Component.translatable("display.discerning_the_eldritch.ascended_one_taunt_1")
                              .withStyle(s -> s.withColor(TextColor.fromRgb(13048715)))
                        )
                     );
               }
            }

            this.setPhase(Phase.SecondPhase);
            if (!this.isDeadOrDying()) {
               this.setHealth(halfHealth);
            }

            this.secondPhaseGoals();
            this.getAttributes().getInstance(AttributeRegistry.SPELL_POWER).setBaseValue(1.1F);
            this.getAttributes().getInstance(AttributeRegistry.SPELL_RESIST).setBaseValue(1.5);
            Player player = this.level().getNearestPlayer(this, 16.0);
            if (player != null) {
               this.lookAt(player, 360.0F, 360.0F);
               this.jumpBackwards(this, player);
            }
         }
      } else if (this.isPhase(Phase.SecondPhase)) {
         if (this.getHealth() <= almostDead) {
            this.setPhase(Phase.ThirdPhase);
            if (!this.isDeadOrDying()) {
               this.setHealth(almostDead);
            }

            Player player = this.level().getNearestPlayer(this, 16.0);
            if (player != null) {
               this.lookAt(player, 360.0F, 360.0F);
            }
         }
      } else if (this.isPhase(Phase.ThirdPhase)) {
         if (--this.transitionAnimationTime <= 0) {
            int radius = 15;

            for (LivingEntity targetsx : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius))) {
               if (targetsx instanceof ServerPlayer player) {
                  this.playSound((SoundEvent)DTESoundRegistry.ASCENDED_ONE_TAUNT_TWO.get(), 1.5F, 1.0F);
                  player.connection
                     .send(
                        new ClientboundSetTitleTextPacket(
                           Component.translatable("display.discerning_the_eldritch.ascended_one_taunt_2")
                              .withStyle(s -> s.withColor(TextColor.fromRgb(13048715)))
                        )
                     );
               }
            }

            this.setPhase(Phase.FourthPhase);
            if (!this.isDeadOrDying()) {
               this.setHealth(almostDead);
            }

            this.finalPhaseGoals();
            this.getAttributes().getInstance(AttributeRegistry.SPELL_POWER).setBaseValue(1.5);
            this.getAttributes().getInstance(AttributeRegistry.SPELL_RESIST).setBaseValue(3.5);
            this.getAttributes().getInstance(Attributes.ARMOR_TOUGHNESS).setBaseValue(55.0);
            this.getAttributes().getInstance(Attributes.ARMOR).setBaseValue(65.0);
            Player player = this.level().getNearestPlayer(this, 16.0);
            if (player != null) {
               this.lookAt(player, 360.0F, 360.0F);
               this.jumpBackwards(this, player);
            }
         }
      } else if (this.isPhase(Phase.FourthPhase)) {
         this.setInvulnerable(false);
         this.bossEvent.setProgress(health / (halfHealth - almostDead));
      }
   }

   private void jumpBackwards(LivingEntity entity, LivingEntity player) {
      this.isJumping = true;
      this.cancelCast();
      if (++this.jumpAnimationTime >= 5) {
         int xTarget = (int)entity.getX();
         int zTarget = (int)entity.getZ();
         int xAttacker = (int)player.getX();
         int zAttacker = (int)player.getZ();
         Vec3 vec3 = new Vec3(xAttacker, 0.0, zAttacker).subtract(xTarget, 0.0, zTarget).normalize();
         Vec3 vec3r = new Vec3(xTarget, 0.0, zTarget).subtract(xAttacker, 0.0, zAttacker).normalize();
         entity.push(vec3r.x, 0.5, vec3r.z);
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      if (source == this.level().damageSources().drown()) {
         return false;
      } else if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return this.isTransitionPhase() ? false : super.hurt(source, amount);
      } else {
         return super.hurt(source, amount);
      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)DTESoundRegistry.ASCENDED_ONE_HURT.get();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return (SoundEvent)DTESoundRegistry.ASCENDED_ONE_DEATH.get();
   }

   public void kill() {
      if (this.isDeadOrDying()) {
         this.discard();
      } else {
         super.kill();
      }
   }

   public void die(DamageSource damageSource) {
      super.die(damageSource);
      if (this.isDeadOrDying() && !this.level().isClientSide) {
         this.castComplete();
         this.serverTriggerAnimation("ascended_death");
         this.serverTriggerEvent((byte)0);
      }
   }

   protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
      this.dropEquipment();
      this.dropExperience(damageSource.getEntity());
      boolean deathByPlayer = this.lastHurtByPlayerTime > 0;
      this.dropCustomDeathLoot(level, damageSource, deathByPlayer);
      ResourceKey<LootTable> lootTable = this.getLootTable();
      LootTable mainLoot = this.level().getServer().reloadableRegistries().getLootTable(lootTable);
      Builder builder = new Builder(level)
         .withParameter(LootContextParams.THIS_ENTITY, this)
         .withParameter(LootContextParams.ORIGIN, this.position())
         .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
         .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
         .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity());
      if (deathByPlayer && this.lastHurtByPlayer != null) {
         builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
      }

      LootParams lootParams = builder.create(LootContextParamSets.ENTITY);
      ObjectArrayList<ItemStack> objectArrayList = new ObjectArrayList();
      mainLoot.getRandomItems(lootParams, this.getLootTableSeed(), objectArrayList::add);
      this.deathLoot = new SimpleContainer(objectArrayList.size());
      objectArrayList.forEach(this.deathLoot::addItem);
   }

   protected void tickDeath() {
      this.deathTime++;
      if (!this.level().isClientSide && this.deathTime >= this.deathAnimationTime && !this.level().isClientSide() && !this.isRemoved()) {
         if (this.deathLoot != null) {
            this.deathLoot.getItems().forEach(this::spawnAtLocation);
         }

         this.remove(RemovalReason.KILLED);
      }
   }

   public boolean isAlliedTo(Entity entityIn) {
      return entityIn instanceof IMagicSummon summon && summon.getSummoner() == this ? true : entityIn.getType().is(DTETags.APOTHIC_ALLIES);
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistries.ASCENDED_ONE_HOOD.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistries.CAPELESS_ASCENDED_ONE_ROBES.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistries.ASCENDED_ONE_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistries.ASCENDED_ONE_GREAVES.get()));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistries.STAFF_OF_ASCENSION.get()));
      this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)ItemRegistries.BLACK_BOOK_SPELLBOOK.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
      this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 14.5)
         .add(Attributes.ATTACK_KNOCKBACK, 0.5)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
         .add(Attributes.MAX_HEALTH, 600.0)
         .add(Attributes.ARMOR, 50.0)
         .add(Attributes.ARMOR_TOUGHNESS, 45.0)
         .add(Attributes.FOLLOW_RANGE, 80.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(AttributeRegistry.SPELL_POWER, 1.4)
         .add(AttributeRegistry.SPELL_RESIST, 1.65)
         .add(AttributeRegistry.MAX_MANA, 1000.0)
         .add(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.12);
   }

   public void setPhase(int phase) {
      this.entityData.set(PHASE, phase);
   }

   public int getPhase() {
      return (Integer)this.entityData.get(PHASE);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }

      pCompound.putInt("phase", this.getPhase());
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setPhase(pCompound.getInt("phase"));
      if (this.isPhase(Phase.SecondPhase)) {
         this.secondPhaseGoals();
      }

      if (this.isPhase(Phase.FourthPhase)) {
         this.finalPhaseGoals();
      }

      if (this.deathLoot != null) {
         pCompound.put("deathLootItems", this.deathLoot.createTag(this.registryAccess()));
      }

      if (pCompound.contains("deathLootItems", 9)) {
         ListTag tag = pCompound.getList("deathLootItems", 10);
         this.deathLoot = new SimpleContainer(tag.size());
         this.deathLoot.fromTag(tag, this.registryAccess());
      }

      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(PHASE, 0);
   }

   public void load(CompoundTag compound) {
      super.load(compound);
      if (!this.level().isClientSide) {
         this.createBossEvent();
      }
   }

   protected void createBossEvent() {
      this.bossEvent = (ExtendedServerBossEvent)new ExtendedServerBossEvent(
            this.getUUID(), this.getDisplayName().copy().withStyle(ChatFormatting.LIGHT_PURPLE), BossBarColor.PURPLE, BossBarOverlay.PROGRESS
         )
         .setCreateWorldFog(false);
   }

   protected boolean isImmobile() {
      return this.isPhase(Phase.ThirdPhase) || super.isImmobile();
   }

   public boolean isPushable() {
      return !this.isTransitionPhase();
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.transitionController);
      controllerRegistrar.add(this.deathController);
      controllerRegistrar.add(this.jumpController);
      super.registerControllers(controllerRegistrar);
   }

   private PlayState transitionPredicate(AnimationState<AscendedOneBoss> animationState) {
      AnimationController<AscendedOneBoss> controller = animationState.getController();
      if (this.isTransitionPhase()) {
         controller.setAnimation(this.transitionPhaseAnimation);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState deathPredicate(AnimationState<AscendedOneBoss> animationState) {
      AnimationController<AscendedOneBoss> controller = animationState.getController();
      if (this.isDeadOrDying()) {
         controller.setAnimation(this.deathAnimation);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState jumpPredicate(AnimationState<AscendedOneBoss> animationState) {
      AnimationController<AscendedOneBoss> controller = animationState.getController();
      if (this.isJumpingBack()) {
         controller.forceAnimationReset();
         controller.setAnimation(this.jumpAnimation);
         this.isJumping = false;
      }

      return this.transitionController.getAnimationState() == State.STOPPED ? PlayState.CONTINUE : PlayState.STOP;
   }

   public boolean isTransitionPhase() {
      return this.isPhase(Phase.ThirdPhase);
   }

   public boolean isJumpingBack() {
      return this.isJumping;
   }

   public void playAnimation(String animationId) {
      this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
   }

   public boolean shouldAlwaysAnimateHead() {
      return !this.isTransitionPhase() || this.isDeadOrDying();
   }
}
