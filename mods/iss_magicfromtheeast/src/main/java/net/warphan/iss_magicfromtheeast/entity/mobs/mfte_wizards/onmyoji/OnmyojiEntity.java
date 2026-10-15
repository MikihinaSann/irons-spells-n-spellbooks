package net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.onmyoji;

import com.google.common.collect.Sets;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
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
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class OnmyojiEntity extends NeutralWizard implements IMerchantWizard {
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final List<ItemListing> fillerOffers = List.of(
      new SimpleSell(16, new ItemStack(Items.SOUL_TORCH, 3), 6, 8),
      new SimpleSell(16, new ItemStack(Items.CHERRY_SAPLING, 1), 4, 7),
      new SimpleSell(8, new ItemStack(Items.SOUL_LANTERN, 1), 7, 10),
      new SimpleSell(8, new ItemStack(ItemRegistry.SOUL_BRAZIER_ITEM, 1), 11, 16),
      new SimpleBuy(16, new ItemCost(Items.SOUL_SAND, 8), 2, 4),
      new SimpleBuy(8, new ItemCost(Items.SOUL_LANTERN, 1), 3, 5),
      new SimpleBuy(16, new ItemCost((ItemLike)MFTEItemRegistries.BOTTLE_OF_SOULS.get(), 1), 6, 8)
   );

   public OnmyojiEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
      super(entityType, level);
      this.xpReward = 25;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector
         .addGoal(
            2,
            new WizardAttackGoal(this, 1.25, 25, 50)
               .setSpells(
                  List.of(
                     MFTESpellRegistries.SOUL_CATALYST_SPELL.get(),
                     MFTESpellRegistries.PHANTOM_CHARGE_SPELL.get(),
                     MFTESpellRegistries.SPLITTING_SHURIKEN.get()
                  ),
                  List.of(MFTESpellRegistries.SPIRIT_CHALLENGING.get(), MFTESpellRegistries.SOUL_BURST_SPELL.get()),
                  List.of(),
                  List.of()
               )
               .setSingleUseSpell(MFTESpellRegistries.KITSUNE_PACK_SPELL.get(), 80, 320, 6, 8)
               .setDrinksPotions()
         );
      this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30.0F, 0.75));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new WizardRecoverGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, e -> this.isHostileTowards((net.minecraft.world.entity.LivingEntity) e)));
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));
   }

   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficultyInstance, MobSpawnType reason, @Nullable SpawnGroupData spawnGroupData
   ) {
      RandomSource randomSource = Utils.random;
      this.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
      return super.finalizeSpawn(level, difficultyInstance, reason, spawnGroupData);
   }

   protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
      this.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)MFTEItemRegistries.ONMYOJI_HAT.get()));
      this.setItemSlot(EquipmentSlot.CHEST, new ItemStack((ItemLike)MFTEItemRegistries.ONMYOJI_ROBES.get()));
      this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
   }

   public static Builder prepareAttributes() {
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.0)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public Optional<SoundEvent> getAngerSound() {
      return Optional.of((SoundEvent)SoundRegistry.TRADER_NO.get());
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      boolean preventTrading = !this.level.isClientSide && this.getOffers().isEmpty() || this.getTarget() != null || this.isAngryAt(player);
      if (hand == InteractionHand.MAIN_HAND && preventTrading && !this.level.isClientSide) {
      }

      if (!preventTrading) {
         if (!this.level.isClientSide && !this.getOffers().isEmpty()) {
            if (this.shouldRestock()) {
               this.restock();
            }

            this.startTrading(player);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
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

   public Level level() {
      return this.level;
   }

   public void setTradingPlayer(@org.jetbrains.annotations.Nullable Player pTradingPlayer) {
      this.tradingPlayer = pTradingPlayer;
   }

   public Player getTradingPlayer() {
      return this.tradingPlayer;
   }

   public MerchantOffers getOffers() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.offers.addAll(this.createRandomOffers(2, 3));
         if (this.random.nextFloat() < 0.25F) {
            this.offers.add(new InkBuyTrade((InkItem)ItemRegistry.INK_COMMON.get()).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.25F) {
            this.offers.add(new InkBuyTrade((InkItem)ItemRegistry.INK_UNCOMMON.get()).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.25F) {
            this.offers.add(new InkBuyTrade((InkItem)ItemRegistry.INK_RARE.get()).getOffer(this, this.random));
         }

         this.offers.add(new RandomScrollTrade(new SpellFilter(MFTESchoolRegistries.SPIRIT.get()), 0.0F, 0.25F).getOffer(this, this.random));
         if (this.random.nextFloat() < 0.8F) {
            this.offers.add(new RandomScrollTrade(new SpellFilter(MFTESchoolRegistries.SPIRIT.get()), 0.3F, 0.7F).getOffer(this, this.random));
         }

         if (this.random.nextFloat() < 0.8F) {
            this.offers.add(new RandomScrollTrade(new SpellFilter(MFTESchoolRegistries.SPIRIT.get()), 0.8F, 1.0F).getOffer(this, this.random));
         }

         this.offers
            .add(
               new MerchantOffer(
                  new ItemCost((ItemLike)MFTEItemRegistries.CRYSTALLIZED_SOUL.get(), 4),
                  Optional.of(new ItemCost((ItemLike)ItemRegistry.RUINED_BOOK.get(), 1)),
                  new ItemStack((ItemLike)MFTEItemRegistries.RITUAL_ORIHON.get(), 1),
                  1,
                  1,
                  5.0F
               )
            );
         this.offers.removeIf(Objects::isNull);
         this.numberOfRestocksToday++;
      }

      return this.offers;
   }

   private Collection<MerchantOffer> createRandomOffers(int min, int max) {
      Set<Integer> set = Sets.newHashSet();
      int fillerTrades = this.random.nextIntBetweenInclusive(min, max);

      for (int i = 0; i < 10 && set.size() < fillerTrades; i++) {
         set.add(this.random.nextInt(fillerOffers.size()));
      }

      Collection<MerchantOffer> offers = new ArrayList<>();

      for (Integer integer : set) {
         offers.add(fillerOffers.get(integer).getOffer(this, this.random));
      }

      return offers;
   }

   public void overrideOffers(MerchantOffers pOffers) {
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isTrading();
   }

   public void notifyTrade(MerchantOffer pOffer) {
      pOffer.increaseUses();
      this.ambientSoundTime = -this.getAmbientSoundInterval();
   }

   public void notifyTradeUpdated(ItemStack pStack) {
      if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.playSound(this.getTradeUpdatedSound(!pStack.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
      }
   }

   protected SoundEvent getTradeUpdatedSound(boolean pIsYesSound) {
      return pIsYesSound ? (SoundEvent)SoundRegistry.TRADER_YES.get() : (SoundEvent)SoundRegistry.TRADER_NO.get();
   }

   public SoundEvent getNotifyTradeSound() {
      return (SoundEvent)SoundRegistry.TRADER_YES.get();
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      this.serializeMerchant(pCompound, this.offers, this.lastRestockGameTime, this.numberOfRestocksToday);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.deserializeMerchant(pCompound, c -> this.offers = c);
   }
}
