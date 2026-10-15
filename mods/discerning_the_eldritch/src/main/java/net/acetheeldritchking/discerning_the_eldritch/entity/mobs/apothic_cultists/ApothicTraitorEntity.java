package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists;

import com.google.common.collect.Sets;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
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
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class ApothicTraitorEntity extends NeutralWizard implements IMerchantWizard, IAnimatedAttacker {
   @Nullable
   private Player tradingPartner;
   @Nullable
   protected MerchantOffers offer;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final List<ItemListing> filler = List.of(
      new SimpleBuy(16, new ItemCost(Items.CANDLE, 1), 1, 2),
      new SimpleSell(8, new ItemStack(Items.ECHO_SHARD, 2), 5, 15),
      new SimpleSell(12, new ItemStack(Items.LANTERN, 3), 6, 10)
   );
   RawAnimation animationToPlay = null;
   private final AnimationController<ApothicTraitorEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);

   public ApothicTraitorEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 25;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.ELDRITCH_BLAST_SPELL.get(), 1, 5, 100, 250, 4));
      this.goalSelector
         .addGoal(
            3,
            new GenericAnimatedWarlockAttackGoal(this, 1.5, 25, 40)
               .setMoveset(
                  List.of(
                     new AttackAnimationData(9, "simple_sword_upward_swipe", new int[]{5}),
                     new AttackAnimationData(8, "simple_sword_lunge_stab", new int[]{6}),
                     new AttackAnimationData(10, "simple_sword_stab_alternate", new int[]{8}),
                     new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe", new int[]{8}),
                     new AttackAnimationData(10, "simple_sword_downstrike", new int[]{8}),
                     new AttackAnimationData(10, "sword_slash_stab", new int[]{20})
                  )
               )
               .setComboChance(0.4F)
               .setMeleeAttackInverval(15, 35)
               .setMeleeMovespeedModifier(1.5F)
               .setMeleeBias(0.1F, 0.5F)
               .setSpells(
                  List.of(
                     SpellRegistries.ESOTERIC_STRIKE.get(),
                     (AbstractSpell)SpellRegistry.ECHOING_STRIKES_SPELL.get(),
                     (AbstractSpell)SpellRegistry.MAGIC_ARROW_SPELL.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHARGE_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.BLOOD_STEP_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.ABYSSAL_SHROUD_SPELL.get(), SpellRegistries.ABRACADABRA.get())
               )
               .setSingleUseSpell(SpellRegistries.ESOTERIC_EDGE.get(), 80, 400, 1, 3)
               .setSpellQuality(1.0F, 1.0F)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, KeeperEntity.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PriestEntity.class, true));
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_ROBES.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistries.GECKOLIB_ELDRITCH_WARLOCK_GREAVES.get()));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.AUTOLOADER_CROSSBOW));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
      this.setDropChance(EquipmentSlot.MAINHAND, 0.05F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomsource = Utils.random;
      this.populateDefaultEquipmentSlots(randomsource, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
   }

   public static Builder createAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.5)
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.5)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(AttributeRegistry.MAX_MANA, 450.0);
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
         this.offer.addAll(this.createRandomOffers(1, 2));
         if (this.random.nextFloat() < 0.25F) {
            this.offer.add(new InkBuyTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.25F) {
            this.offer.add(new InkBuyTrade((InkItem)ItemRegistry.INK_RARE.get()).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.25F) {
            this.offer.add(new InkBuyTrade((InkItem)ItemRegistry.INK_EPIC.get()).getOffer(this, this.random));
         }

         this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.ELDRITCH.get()), 0.0F, 0.25F).getOffer(this, this.random));
         if (this.random.nextFloat() < 0.8F) {
            this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.ELDRITCH.get()), 0.3F, 0.7F).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.8F) {
            this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.ELDRITCH.get()), 0.8F, 1.0F).getOffer(this, this.random));
         }

         this.offer
            .add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 16), new ItemStack((ItemLike)ItemRegistries.ECHO_VIBRATION_RING.get(), 1), 2, 0, 0.2F));
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

   private PlayState predicate(AnimationState<ApothicTraitorEntity> animationState) {
      AnimationController<ApothicTraitorEntity> controller = animationState.getController();
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

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      this.serializeMerchant(pCompound, this.offer, this.lastRestockGameTime, this.numberOfRestocksToday);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.deserializeMerchant(pCompound, c -> this.offer = c);
   }
}
