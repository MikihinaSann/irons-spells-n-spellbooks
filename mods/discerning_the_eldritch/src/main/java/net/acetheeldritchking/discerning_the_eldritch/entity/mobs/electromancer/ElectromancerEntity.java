package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.electromancer;

import com.google.common.collect.Sets;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import io.redspace.ironsspellbooks.entity.mobs.wizards.IMerchantWizard;
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
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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

public class ElectromancerEntity extends NeutralWizard implements IMerchantWizard {
   @Nullable
   private Player tradingPartner;
   @Nullable
   protected MerchantOffers offer;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final List<ItemListing> filler = List.of(
      new SimpleBuy(1, new ItemCost((ItemLike)ItemRegistry.LIGHTNING_BOTTLE.get(), 1), 6, 12),
      new SimpleSell(1, new ItemStack((ItemLike)ItemRegistry.LIGHTNING_RUNE.get(), 2), 25, 55),
      new SimpleSell(1, new ItemStack((ItemLike)ItemRegistry.LIGHTNING_BOTTLE.get(), 1), 6, 12)
   );

   public ElectromancerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 25;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SpellBarrageGoal(this, (AbstractSpell)SpellRegistry.VOLT_STRIKE_SPELL.get(), 1, 3, 100, 250, 3));
      this.goalSelector
         .addGoal(
            3,
            new WizardAttackGoal(this, 1.5, 25, 40)
               .setSpells(
                  List.of(
                     (AbstractSpell)SpellRegistry.CHAIN_LIGHTNING_SPELL.get(),
                     (AbstractSpell)SpellRegistry.ELECTROCUTE_SPELL.get(),
                     (AbstractSpell)SpellRegistry.BALL_LIGHTNING_SPELL.get()
                  ),
                  List.of(
                     (AbstractSpell)SpellRegistry.COUNTERSPELL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.HEAL_SPELL.get(),
                     (AbstractSpell)SpellRegistry.CHARGE_SPELL.get()
                  ),
                  List.of((AbstractSpell)SpellRegistry.TELEPORT_SPELL.get()),
                  List.of((AbstractSpell)SpellRegistry.CHARGE_SPELL.get())
               )
               .setSingleUseSpell((AbstractSpell)SpellRegistry.LIGHTNING_BOLT_SPELL.get(), 80, 400, 1, 3)
               .setSpellQuality(0.25F, 0.25F)
               .setDrinksPotions()
               .setAllowFleeing(true)
         );
      this.goalSelector
         .addGoal(
            2,
            new WizardSpellComboGoal(
               this,
               List.of(
                  (AbstractSpell)SpellRegistry.ASCENSION_SPELL.get(),
                  (AbstractSpell)SpellRegistry.VOLT_STRIKE_SPELL.get(),
                  (AbstractSpell)SpellRegistry.SHOCKWAVE_SPELL.get()
               ),
               1.3F,
               1.3F,
               100,
               250
            )
         );
      this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, KeeperEntity.class, true));
   }

   protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)ItemRegistry.ELECTROMANCER_HELMET.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)ItemRegistry.ELECTROMANCER_CHESTPLATE.get()));
      this.setItemSlot(EquipmentSlot.LEGS, new ItemStack((ItemLike)ItemRegistry.ELECTROMANCER_LEGGINGS.get()));
      this.setItemSlot(EquipmentSlot.FEET, new ItemStack((ItemLike)ItemRegistry.ELECTROMANCER_BOOTS.get()));
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.LIGHTNING_ROD_STAFF));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      this.setDropChance(EquipmentSlot.LEGS, 0.0F);
      this.setDropChance(EquipmentSlot.FEET, 0.0F);
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
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
         .add(Attributes.ATTACK_DAMAGE, 2.5)
         .add(Attributes.ATTACK_KNOCKBACK, 0.2)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.5)
         .add(Attributes.MOVEMENT_SPEED, 0.35)
         .add(AttributeRegistry.MAX_MANA, 100.0);
   }

   public boolean isAlliedTo(Entity entityIn) {
      return entityIn instanceof IMagicSummon summon && summon.getSummoner() == this ? true : entityIn instanceof ElectromancerEntity;
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
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

         this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.LIGHTNING.get()), 0.0F, 0.25F).getOffer(this, this.random));
         if (this.random.nextFloat() < 0.8F) {
            this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.LIGHTNING.get()), 0.3F, 0.7F).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.8F) {
            this.offer.add(new RandomScrollTrade(new SpellFilter((SchoolType)SchoolRegistry.LIGHTNING.get()), 0.8F, 1.0F).getOffer(this, this.random));
         }

         this.offer
            .add(
               new MerchantOffer(
                  new ItemCost((ItemLike)ItemRegistry.CHAINED_BOOK.get(), 1), new ItemStack((ItemLike)ItemRegistries.TEMPESTUOUS_TOME.get(), 1), 2, 0, 1.2F
               )
            );
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

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      this.serializeMerchant(pCompound, this.offer, this.lastRestockGameTime, this.numberOfRestocksToday);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.deserializeMerchant(pCompound, c -> this.offer = c);
   }
}
