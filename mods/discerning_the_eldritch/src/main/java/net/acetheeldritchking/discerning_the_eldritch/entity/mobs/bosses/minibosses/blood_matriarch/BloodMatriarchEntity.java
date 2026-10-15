package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.minibosses.blood_matriarch;

import com.google.common.collect.Sets;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.FocusOnTradingPlayerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.loot.SpellFilter;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades.InkBuyTrade;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades.RandomScrollTrade;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades.SimpleBuy;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades.SimpleSell;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.goals.WizardSpellComboGoal;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicAcolyteEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicCrusaderEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicSummonerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodMageEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneBoss;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTEServerConfig;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;

public class BloodMatriarchEntity extends BloodMageEntity implements Enemy, IMerchantWizard, IAnimatedAttacker {
   @Nullable
   private Player tradingPartner;
   @Nullable
   protected MerchantOffers offer;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final List<ItemListing> filler = List.of(
      new SimpleBuy(1, new ItemCost((ItemLike)ItemRegistry.BLOOD_VIAL.get(), 1), 6, 12),
      new SimpleSell(1, new ItemStack((ItemLike)ItemRegistry.BLOOD_RUNE.get(), 2), 25, 55),
      new SimpleSell(1, new ItemStack((ItemLike)ItemRegistry.BLOOD_VIAL.get(), 1), 6, 12),
      new SimpleSell(1, new ItemStack((ItemLike)ItemRegistry.BLOODY_VELLUM.get(), 4), 3, 8)
   );
   private static final List<MerchantOffer> stagTrades = List.of(
      new MerchantOffer(
         new ItemCost((ItemLike)ItemRegistries.BOTTLE_OF_LIQUID_MALICE.get(), 16),
         new ItemStack((ItemLike)ItemRegistries.CRIMSON_STAG_ANTLERS.get(), 1),
         1,
         0,
         1.0F
      ),
      new MerchantOffer(
         new ItemCost((ItemLike)ItemRegistries.BOTTLE_OF_LIQUID_MALICE.get(), 16),
         new ItemStack((ItemLike)ItemRegistries.CRIMSON_STAG_ROBES.get(), 1),
         1,
         0,
         1.0F
      ),
      new MerchantOffer(
         new ItemCost((ItemLike)ItemRegistries.BOTTLE_OF_LIQUID_MALICE.get(), 16),
         new ItemStack((ItemLike)ItemRegistries.CRIMSON_STAG_LEGGINGS.get(), 1),
         1,
         0,
         1.0F
      ),
      new MerchantOffer(
         new ItemCost((ItemLike)ItemRegistries.BOTTLE_OF_LIQUID_MALICE.get(), 16),
         new ItemStack((ItemLike)ItemRegistries.CRIMSON_STAG_BOOTS.get(), 1),
         1,
         0,
         1.0F
      )
   );
   RawAnimation animationToPlay = null;
   private final AnimationController<BloodMatriarchEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);

   public BloodMatriarchEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 45;
      this.lookControl = this.createLookControl();
      this.moveControl = this.createMoveControl();
   }

   protected LookControl createLookControl() {
      return new LookControl(this) {
         protected float rotateTowards(float from, float to, float maxDelta) {
            return super.rotateTowards(from, to, maxDelta * 2.5F);
         }

         protected boolean resetXRotOnTick() {
            return BloodMatriarchEntity.this.getTarget() == null;
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

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FocusOnTradingPlayerGoal(this));
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, SpellRegistries.BLADES_OF_RANCOR.get(), 3, 5, 100, 250, 6));
      this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 35.0F, 0.75));
      this.goalSelector
         .addGoal(
            3,
            new GenericAnimatedWarlockAttackGoal(this, 1.5, 25, 40)
               .setMoveset(
                  List.of(
                     new AttackAnimationData(9, "simple_sword_upward_swipe", new int[]{5}),
                     new AttackAnimationData(8, "simple_sword_lunge_stab", new int[]{6}),
                     new AttackAnimationData(37, "sword_slash_stab", new int[]{14, 34}),
                     new AttackAnimationData(10, "simple_sword_stab_alternate", new int[]{8}),
                     new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe", new int[]{8}),
                     new AttackAnimationData(24, "spin_slash_normal", new int[]{12, 15, 19, 22})
                  )
               )
               .setComboChance(0.3F)
               .setMeleeAttackInverval(15, 25)
               .setMeleeMovespeedModifier(1.5F)
               .setMeleeBias(0.1F, 0.2F)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.BLOOD_NEEDLES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.WITHER_SKULL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BLOOD_SLASH_SPELL.get(),
                     SpellRegistries.BLADES_OF_RANCOR.get(),
                     SpellRegistries.VEIN_RIPPER.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.RAY_OF_SIPHONING_SPELL.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(),
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     SpellRegistries.VEIN_RIPPER.get()
                  ),
                  List.of()
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get(), 50, 100, 2, 5)
               .setSpellQuality(1.1F, 1.1F)
               .setDrinksPotions()
         );
      this.goalSelector
         .addGoal(
            2,
            new WizardSpellComboGoal(
               this,
               List.of(
                  (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(),
                  (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                  (AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get()
               ),
               1.3F,
               1.3F,
               100,
               250
            )
         );
      this.goalSelector
         .addGoal(
            2,
            new WizardSpellComboGoal(
               this,
               List.of(
                  (AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get(), (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(), SpellRegistries.VEIN_RIPPER.get()
               ),
               1.3F,
               1.3F,
               100,
               250
            )
         );
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApothicCrusaderEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApothicAcolyteEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ApothicSummonerEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AscendedOneBoss.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, e -> this.isHostileTowards((net.minecraft.world.entity.LivingEntity) e)));
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, true));
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_CRIMSON_STAG_ANTLERS.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_CRIMSON_STAG_ROBES.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_CRIMSON_STAG_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_CRIMSON_STAG_BOOTS.get()));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ItemRegistries.DEVOURER.get()));
      this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)ItemRegistries.DIARY_OF_DECAY.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
      this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
   }

   public boolean isAlliedTo(Entity entityIn) {
      return entityIn instanceof IMagicSummon summon && summon.getSummoner() == this ? true : entityIn.getType().is(DTETags.BLOOD_ALLIES);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public boolean isPersistenceRequired() {
      return true;
   }

   public static Builder createAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 10.5)
         .add(Attributes.ATTACK_KNOCKBACK, 0.5)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.ARMOR_TOUGHNESS, 5.0)
         .add(Attributes.MAX_HEALTH, 130.0)
         .add(Attributes.FOLLOW_RANGE, 45.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.5)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(AttributeRegistry.MAX_MANA, 500.0)
         .add(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.1)
         .add(Attributes.SCALE, 1.05);
   }

   public boolean shouldSheathSword() {
      return true;
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      boolean preventTrade = !this.level().isClientSide && this.getOffers().isEmpty() || this.getTarget() != null || this.isAngryAt(player);
      if (hand == InteractionHand.MAIN_HAND && preventTrade && !this.level().isClientSide) {
      }

      if (!preventTrade) {
         if (!this.level().isClientSide && !this.getOffers().isEmpty()) {
            if (this.shouldRestock()) {
               this.restock();
            }

            this.startTrading(player);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(player, hand);
      }
   }

   private void startTrading(Player player) {
      this.setTradingPlayer(player);
      this.lookControl.setLookAt(player);
      this.openTradingScreen(player, this.getDisplayName(), 0);
   }

   public int getRestocksToday() {
      return this.numberOfRestocksToday;
   }

   public void setRestocksToday(int restocks) {
      this.numberOfRestocksToday = restocks;
   }

   public long getLastRestockGameTime() {
      return this.lastRestockGameTime;
   }

   public void setLastRestockGameTime(long time) {
      this.lastRestockGameTime = time;
   }

   public long getLastRestockCheckDayTime() {
      return this.lastRestockCheckDayTime;
   }

   public void setLastRestockCheckDayTime(long time) {
      this.lastRestockCheckDayTime = time;
   }

   public void setTradingPlayer(@Nullable Player player) {
      this.tradingPartner = player;
   }

   @Nullable
   public Player getTradingPlayer() {
      return this.tradingPartner;
   }

   public MerchantOffers getOffers() {
      if (this.offer == null) {
         this.offer = new MerchantOffers();
         this.offer.addAll(this.createRandomOffers(2, 3));
         if (this.random.nextFloat() < 0.25F) {
            this.offer.add(new InkBuyTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.25F) {
            this.offer.add(new InkBuyTrade((InkItem)ItemRegistry.INK_RARE.get()).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.25F) {
            this.offer.add(new InkBuyTrade((InkItem)ItemRegistry.INK_EPIC.get()).getOffer(this, this.random));
         }

         this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.BLOOD.get()), 0.0F, 0.25F).getOffer(this, this.random));
         if (this.random.nextFloat() < 0.8F) {
            this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.BLOOD.get()), 0.3F, 0.7F).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.8F) {
            this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.BLOOD.get()), 0.8F, 1.0F).getOffer(this, this.random));
         }

         this.offer
            .add(
               new MerchantOffer(
                  new ItemCost((ItemLike)ItemRegistry.CURSED_DOLL_SPELLBOOK.get(), 1),
                  new ItemStack((ItemLike)ItemRegistries.DIARY_OF_DECAY.get(), 1),
                  2,
                  0,
                  1.2F
               )
            );
         this.offer.addAll(this.createRandomStagOffers(2, 4));
         this.offer.removeIf(Objects::isNull);
         this.numberOfRestocksToday++;
      }

      return this.offer;
   }

   private Collection<MerchantOffer> createRandomOffers(int min, int max) {
      Set<Integer> set = Sets.newHashSet();
      int fillerTrades = this.random.nextIntBetweenInclusive(min, max);

      for (int i = 0; i < 10 && set.size() < fillerTrades; i++) {
         set.add(this.random.nextInt(filler.size()));
      }

      Collection<MerchantOffer> offers = new ArrayList<>();

      for (Integer integer : set) {
         offers.add(filler.get(integer).getOffer(this, this.random));
      }

      return offers;
   }

   private Collection<MerchantOffer> createRandomStagOffers(int min, int max) {
      Set<Integer> set = Sets.newHashSet();
      int fillerTrades = this.random.nextIntBetweenInclusive(min, max);

      for (int i = 0; i < 10 && set.size() < fillerTrades; i++) {
         set.add(this.random.nextInt(stagTrades.size()));
      }

      Collection<MerchantOffer> offers = new ArrayList<>();

      for (Integer integer : set) {
         offers.add(stagTrades.get(integer));
      }

      return offers;
   }

   public void overrideOffers(MerchantOffers merchantOffers) {
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isTrading();
   }

   public void notifyTrade(MerchantOffer merchantOffer) {
      merchantOffer.increaseUses();
      this.ambientSoundTime = -this.getAmbientSoundInterval();
   }

   public void notifyTradeUpdated(ItemStack itemStack) {
      if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.playSound(this.getTradeUpdatedSound(!itemStack.isEmpty()), this.getSoundVolume(), this.getSoundVolume());
      }
   }

   protected SoundEvent getTradeUpdatedSound(boolean affirm) {
      return affirm ? (SoundEvent)SoundRegistry.TRADER_YES.get() : (SoundEvent)SoundRegistry.TRADER_NO.get();
   }

   public SoundEvent getNotifyTradeSound() {
      return (SoundEvent)SoundRegistry.TRADER_YES.get();
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.meleeController);
      super.registerControllers(controllerRegistrar);
   }

   public void playAnimation(String animationId) {
      this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
   }

   private PlayState predicate(AnimationState<BloodMatriarchEntity> animationState) {
      AnimationController<BloodMatriarchEntity> controller = animationState.getController();
      if (this.animationToPlay != null) {
         controller.forceAnimationReset();
         controller.setAnimation(this.animationToPlay);
         this.animationToPlay = null;
      }

      return PlayState.CONTINUE;
   }

   public boolean isAnimating() {
      return this.meleeController.getAnimationState() != State.STOPPED || super.isAnimating();
   }

   protected PathNavigation createNavigation(Level level) {
      return new NotIdioticNavigation(this, level);
   }

   public boolean isHostileTowards(LivingEntity pTarget) {
      return !DTEServerConfig.enableBloodCultistAggression
         ? super.isHostileTowards(pTarget)
         : super.isHostileTowards(pTarget) || pTarget.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER) < 1.15;
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      this.serializeMerchant(pCompound, this.offer, this.lastRestockGameTime, this.numberOfRestocksToday);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.deserializeMerchant(pCompound, c -> this.offer = c);
   }
}
