package net.warphan.iss_magicfromtheeast.item.weapons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.CrossbowItem.ChargingSounds;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantmentHelper;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantments;
import net.warphan.iss_magicfromtheeast.item.LoadableWeaponContents;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.setup.ClientLoadableWeaponTooltip;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RepeatingCrossbow extends ProjectileWeaponItem {
   private static final int DEFAULT_PROJECTILE_AMOUNT = 5;
   public static final String AMMO_AMOUNT = "ammo_amount";
   public static final String RC_LOADING = "loading";
   public static final String RC_CHARGING = "charging";
   private static final ChargingSounds DEFAULT_SOUNDS = new ChargingSounds(
      Optional.of(SoundEvents.CROSSBOW_LOADING_START), Optional.of(SoundEvents.CROSSBOW_LOADING_MIDDLE), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
   );
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;
   int loadingTick = 1;

   public RepeatingCrossbow(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pHand) {
      ItemStack itemstack = player.getItemInHand(pHand);
      if (itemstack == player.getMainHandItem()) {
         if (getAmmoAmount(itemstack) > 0 && isCharged(itemstack)) {
            this.performShooting(pLevel, player, pHand, itemstack, 3.0F, 1.0F, null);
            return InteractionResultHolder.consume(itemstack);
         } else if (!player.getProjectile(itemstack).isEmpty() && getAmmoAmount(itemstack) <= 0) {
            player.startUsingItem(pHand);
            if (isCharged(itemstack)) {
               startLoadingAmmo(itemstack);
            }

            return InteractionResultHolder.consume(itemstack);
         } else {
            return InteractionResultHolder.fail(itemstack);
         }
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack crossbow, int i) {
      int PROJECTILE_AMOUNT = getBonusProjectileAmount(crossbow, livingEntity);
      ItemStack projectile = livingEntity.getProjectile(crossbow);
      if (!level.isClientSide) {
         if (isCharged(crossbow) && isLoading(crossbow)) {
            this.loadingTick++;
            if (getAmmoAmount(crossbow) >= PROJECTILE_AMOUNT || projectile.isEmpty()) {
               if ((getAmmoAmount(crossbow) == PROJECTILE_AMOUNT || livingEntity.getProjectile(crossbow).isEmpty()) && livingEntity instanceof Player player) {
                  stopLoadingAmmo(player, crossbow);
               }
            } else if (this.loadingTick % 10 == 0) {
               setAmmoAmount(crossbow, getAmmoAmount(crossbow) + 1);
               LoadableWeaponContents contents = (LoadableWeaponContents)crossbow.get(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS);
               if (contents != null) {
                  LoadableWeaponContents.Mutable mutable = new LoadableWeaponContents.Mutable(contents);
                  mutable.tryInsert(projectile);
                  crossbow.set(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS, mutable.toImmutable());
               }

               level.playSound(
                  (Player)null,
                  livingEntity.getX(),
                  livingEntity.getY(),
                  livingEntity.getZ(),
                  MFTESoundRegistries.PROJECTILE_LOAD,
                  SoundSource.PLAYERS,
                  1.0F,
                  1.0F
               );
            }
         }

         this.playingSound(level, livingEntity, crossbow, i);
      }
   }

   public void performShooting(
      Level level, LivingEntity livingEntity, InteractionHand hand, ItemStack crossbow, float f1, float f2, @Nullable LivingEntity nullableEntity
   ) {
      if (level instanceof ServerLevel serverlevel) {
         if (livingEntity instanceof Player player && EventHooks.onArrowLoose(crossbow, livingEntity.level(), player, 1, true) < 0) {
            return;
         }

         LoadableWeaponContents contents = crossbow.get(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS);
         crossbow.set(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS, LoadableWeaponContents.EMPTY);
         if (getAmmoAmount(crossbow) > 0 && contents != null) {
            LoadableWeaponContents.Mutable mutable = new LoadableWeaponContents.Mutable(contents);
            float spread = EnchantmentHelper.processProjectileSpread(serverlevel, crossbow, livingEntity, 0.0F);
            List<ItemStack> itemStacks = mutable.getItems();
            List<ItemStack> projectileStack = new ArrayList<>(1);
            projectileStack.add(itemStacks.getFirst());
            if (spread > 0.0F) {
               this.shoot(serverlevel, livingEntity, hand, crossbow, itemStacks, f1, f2, livingEntity instanceof Player, nullableEntity);
            } else {
               this.shoot(serverlevel, livingEntity, hand, crossbow, projectileStack, f1, f2, livingEntity instanceof Player, nullableEntity);
            }

            mutable.removeOne();
            crossbow.set(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS, mutable.toImmutable());
            if (getAmmoAmount(crossbow) <= 1) {
               crossbow.remove(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS);
               crossbow.remove(MFTEDataComponentRegistries.CROSSBOW_CHARGE_STATE);
            }

            if (spread > 0.0F) {
               setAmmoAmount(crossbow, getAmmoAmount(crossbow) - getAmmoAmount(crossbow));
               crossbow.remove(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS);
               crossbow.remove(MFTEDataComponentRegistries.CROSSBOW_CHARGE_STATE);
            } else {
               setAmmoAmount(crossbow, getAmmoAmount(crossbow) - 1);
            }

            if (livingEntity instanceof ServerPlayer serverplayer) {
               CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, crossbow);
               serverplayer.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
            }
         }
      }
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int tick) {
      int i = this.getUseDuration(itemStack, livingEntity) - tick;
      float f = getPowerForTime(i, itemStack, livingEntity);
      if (f >= 1.0F && !isCharged(itemStack) && tryChargeCrossbowUp(livingEntity, itemStack)) {
         ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(itemStack);
         crossbowitem$chargingsounds.end()
            .ifPresent(
               p_352852_ -> level.playSound(
                  (Player)null,
                  livingEntity.getX(),
                  livingEntity.getY(),
                  livingEntity.getZ(),
                  (SoundEvent)p_352852_.value(),
                  livingEntity.getSoundSource(),
                  1.0F,
                  1.0F / (livingEntity.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
               )
            );
      }
   }

   public static boolean tryChargeCrossbowUp(LivingEntity livingEntity, ItemStack stack) {
      List<ItemStack> list = noProjectileTakenDraw(stack, livingEntity.getProjectile(stack), livingEntity);
      if (!list.isEmpty()) {
         stack.set(
            MFTEDataComponentRegistries.CROSSBOW_CHARGE_STATE,
            ((RepeatingCrossbow.ChargeStateComponent)stack.getOrDefault(
                  MFTEDataComponentRegistries.CROSSBOW_CHARGE_STATE, new RepeatingCrossbow.ChargeStateComponent(false)
               ))
               .setChargeCrossbow(true)
         );
         stack.set(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS, LoadableWeaponContents.EMPTY);
         return true;
      } else {
         return false;
      }
   }

   protected static List<ItemStack> noProjectileTakenDraw(ItemStack p_331565_, ItemStack stack, LivingEntity livingEntity) {
      if (stack.isEmpty()) {
         return List.of();
      } else {
         int var10000;
         if (livingEntity.level() instanceof ServerLevel serverlevel) {
            var10000 = EnchantmentHelper.processProjectileCount(serverlevel, p_331565_, livingEntity, 1);
         } else {
            var10000 = 1;
         }

         List<ItemStack> list = new ArrayList<>(var10000);
         ItemStack itemstack1 = stack.copy();
         list.add(itemstack1);
         return list;
      }
   }

   public static boolean isCharged(ItemStack stack) {
      return stack.has(MFTEDataComponentRegistries.CROSSBOW_CHARGE_STATE)
         && ((RepeatingCrossbow.ChargeStateComponent)stack.get(MFTEDataComponentRegistries.CROSSBOW_CHARGE_STATE)).charge();
   }

   protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float f1, float f2, float f3, @Nullable LivingEntity nullableEntity) {
      Vector3f vector3f;
      if (nullableEntity != null) {
         double d0 = nullableEntity.getX() - livingEntity.getX();
         double d1 = nullableEntity.getZ() - livingEntity.getZ();
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         double d3 = nullableEntity.getY(0.3333333333333333) - projectile.getY() + d2 * 0.2F;
         vector3f = getProjectileShotVector(livingEntity, new Vec3(d0, d3, d1), f3);
      } else {
         Vec3 vec3 = livingEntity.getUpVector(1.0F);
         Quaternionf quaternionf = new Quaternionf().setAngleAxis(f3 * (float) (Math.PI / 180.0), vec3.x, vec3.y, vec3.z);
         Vec3 vec31 = livingEntity.getViewVector(1.0F);
         vector3f = vec31.toVector3f().rotate(quaternionf);
      }

      projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), f1, f2);
      float f = getShotPitch(livingEntity.getRandom(), i);
      livingEntity.level()
         .playSound(
            (Player)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CROSSBOW_SHOOT, livingEntity.getSoundSource(), 1.0F, f
         );
   }

   private static Vector3f getProjectileShotVector(LivingEntity livingEntity, Vec3 vec, float f) {
      Vector3f vector3f = vec.toVector3f().normalize();
      Vector3f vector3f1 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if (vector3f1.lengthSquared() <= 1.0E-7) {
         Vec3 vec3 = livingEntity.getUpVector(1.0F);
         vector3f1 = new Vector3f(vector3f).cross(vec3.toVector3f());
      }

      Vector3f vector3f2 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f1.x, vector3f1.y, vector3f1.z);
      return new Vector3f(vector3f).rotateAxis(f * (float) (Math.PI / 180.0), vector3f2.x, vector3f2.y, vector3f2.z);
   }

   protected Projectile createProjectile(Level level, LivingEntity livingEntity, ItemStack stack, ItemStack itemStack, boolean var1000) {
      Projectile projectile = super.createProjectile(level, livingEntity, stack, itemStack, var1000);
      if (projectile instanceof AbstractArrow abstractarrow) {
         abstractarrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
      }

      return projectile;
   }

   private static float getShotPitch(RandomSource randomSource, int i) {
      return i == 0 ? 1.0F : getRandomShotPitch((i & 1) == 1, randomSource);
   }

   private static float getRandomShotPitch(boolean var1000, RandomSource randomSource) {
      float f = var1000 ? 0.63F : 0.43F;
      return 1.0F / (randomSource.nextFloat() * 0.5F + 1.8F) + f;
   }

   public static int getBonusProjectileAmount(ItemStack itemStack, LivingEntity livingEntity) {
      if (livingEntity.level instanceof ServerLevel serverLevel) {
         int bonusProjectileAmount = MFTEEnchantmentHelper.increaseAmmoLoad(serverLevel, itemStack, 0) + 5;
         return Mth.floor(bonusProjectileAmount);
      } else {
         return 0;
      }
   }

   public static double getDisplayProjectile(ItemStack stack, Entity entity) {
      double baseAmmoAmount = 5.0;
      if (!stack.isEmpty() && stack.has(DataComponents.ENCHANTMENTS) && entity != null) {
         baseAmmoAmount += Utils.processEnchantment(
            entity.level, MFTEEnchantments.EXPANDING, EnchantmentEffectComponents.AMMO_USE, (ItemEnchantments)stack.get(DataComponents.ENCHANTMENTS)
         );
      }

      return baseAmmoAmount;
   }

   public static int getLoadDuration(ItemStack stack, LivingEntity livingEntity) {
      return getBonusProjectileAmount(stack, livingEntity) * 12;
   }

   public static int getChargeDuration(ItemStack stack, LivingEntity livingEntity) {
      float f = EnchantmentHelper.modifyCrossbowChargingTime(stack, livingEntity, 1.25F);
      return Mth.floor(f * 20.0F);
   }

   public static int getUsingDuration(ItemStack stack, LivingEntity livingEntity) {
      return !isCharged(stack) ? getChargeDuration(stack, livingEntity) : getLoadDuration(stack, livingEntity);
   }

   public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
      return !isCharged(stack) ? getChargeDuration(stack, livingEntity) + 3 : getLoadDuration(stack, livingEntity) + 3;
   }

   private static float getPowerForTime(int i, ItemStack itemStack, LivingEntity livingEntity) {
      float f = (float)i / getChargeDuration(itemStack, livingEntity);
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return ARROW_ONLY;
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.CROSSBOW;
   }

   public int getDefaultProjectileRange() {
      return 15;
   }

   public boolean useOnRelease(ItemStack stack) {
      return stack.is(this);
   }

   public void playingSound(Level level, LivingEntity livingEntity, ItemStack stack, int i) {
      if (!isCharged(stack)) {
         ChargingSounds crossbowitem$chargingsounds = this.getChargingSounds(stack);
         float f = (float)(stack.getUseDuration(livingEntity) - i) / getChargeDuration(stack, livingEntity);
         if (f < 0.2F) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
         }

         if (f >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            crossbowitem$chargingsounds.start()
               .ifPresent(
                  p_352849_ -> level.playSound(
                     (Player)null,
                     livingEntity.getX(),
                     livingEntity.getY(),
                     livingEntity.getZ(),
                     (SoundEvent)p_352849_.value(),
                     SoundSource.PLAYERS,
                     0.5F,
                     1.0F
                  )
               );
         }

         if (f >= 0.5F && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            crossbowitem$chargingsounds.mid()
               .ifPresent(
                  p_352855_ -> level.playSound(
                     (Player)null,
                     livingEntity.getX(),
                     livingEntity.getY(),
                     livingEntity.getZ(),
                     (SoundEvent)p_352855_.value(),
                     SoundSource.PLAYERS,
                     0.5F,
                     1.0F
                  )
               );
         }
      }
   }

   ChargingSounds getChargingSounds(ItemStack stack) {
      return EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS).orElse(DEFAULT_SOUNDS);
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
      return Optional.ofNullable((LoadableWeaponContents)itemStack.get(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS))
         .map(ClientLoadableWeaponTooltip.LoadableWeaponTooltipComponent::new);
   }

   public static void startLoadingAmmo(ItemStack crossbow) {
      setLoadingAmmo(crossbow, true);
   }

   public static void stopLoadingAmmo(Player player, ItemStack crossbow) {
      setLoadingAmmo(crossbow, false);
   }

   public static boolean isLoading(ItemStack crossbow) {
      return crossbow.has(MFTEDataComponentRegistries.CROSSBOW_LOADING_STATE)
         && ((RepeatingCrossbow.LoadingStateComponent)crossbow.get(MFTEDataComponentRegistries.CROSSBOW_LOADING_STATE)).isLoading();
   }

   public static void setLoadingAmmo(ItemStack crossbow, boolean isLoading) {
      crossbow.set(
         MFTEDataComponentRegistries.CROSSBOW_LOADING_STATE,
         ((RepeatingCrossbow.LoadingStateComponent)crossbow.getOrDefault(
               MFTEDataComponentRegistries.CROSSBOW_LOADING_STATE, new RepeatingCrossbow.LoadingStateComponent(false)
            ))
            .setLoadingAmmo(isLoading)
      );
   }

   public static int getAmmoAmount(ItemStack crossbow) {
      return crossbow.has(MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT)
         ? ((RepeatingCrossbow.ProjectileAmountComponent)crossbow.get(MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT)).ammoAmount()
         : 0;
   }

   public static void setAmmoAmount(ItemStack crossbow, int amount) {
      crossbow.set(
         MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT,
         ((RepeatingCrossbow.ProjectileAmountComponent)crossbow.getOrDefault(
               MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT, new RepeatingCrossbow.ProjectileAmountComponent(0)
            ))
            .setAmmoAmount(amount)
      );
   }

   public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> pTooltip, TooltipFlag pFlag) {
      super.appendHoverText(pStack, context, pTooltip, pFlag);
      int ammoAmount = getAmmoAmount(pStack);
      pTooltip.add(
         Component.translatable("item.iss_magicfromtheeast.repeating_crossbow.ammo_amount")
            .append(CommonComponents.SPACE)
            .append(String.valueOf(ammoAmount))
            .append(" / ")
            .append(Component.literal(Utils.stringTruncation(getDisplayProjectile(pStack, MinecraftInstanceHelper.getPlayer()), 1)))
      );
      TooltipsUtils.addShiftTooltip(pTooltip, List.of(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.YELLOW)));
   }

   public record ChargeStateComponent(boolean charge) {
      public static final Codec<RepeatingCrossbow.ChargeStateComponent> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(Codec.BOOL.optionalFieldOf("charging", false).forGetter(RepeatingCrossbow.ChargeStateComponent::charge))
            .apply(builder, RepeatingCrossbow.ChargeStateComponent::new)
      );
      public static final StreamCodec<FriendlyByteBuf, RepeatingCrossbow.ChargeStateComponent> STREAM_CODEC = StreamCodec.of(
         (buf, data) -> buf.writeBoolean(data.charge), buf -> new RepeatingCrossbow.ChargeStateComponent(buf.readBoolean())
      );

      public RepeatingCrossbow.ChargeStateComponent setChargeCrossbow(boolean charge) {
         return new RepeatingCrossbow.ChargeStateComponent(charge);
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this || obj instanceof RepeatingCrossbow.ChargeStateComponent chargeStateComponent && chargeStateComponent.charge == this.charge;
      }
   }

   public record LoadingStateComponent(boolean isLoading) {
      public static final Codec<RepeatingCrossbow.LoadingStateComponent> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(Codec.BOOL.optionalFieldOf("loading", false).forGetter(RepeatingCrossbow.LoadingStateComponent::isLoading))
            .apply(builder, RepeatingCrossbow.LoadingStateComponent::new)
      );
      public static final StreamCodec<FriendlyByteBuf, RepeatingCrossbow.LoadingStateComponent> STREAM_CODEC = StreamCodec.of(
         (buf, data) -> buf.writeBoolean(data.isLoading), buf -> new RepeatingCrossbow.LoadingStateComponent(buf.readBoolean())
      );

      public RepeatingCrossbow.LoadingStateComponent setLoadingAmmo(boolean isLoading) {
         return new RepeatingCrossbow.LoadingStateComponent(isLoading);
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this
            || obj instanceof RepeatingCrossbow.LoadingStateComponent loadingStateComponent && loadingStateComponent.isLoading == this.isLoading;
      }
   }

   public record ProjectileAmountComponent(int ammoAmount) {
      public static final Codec<RepeatingCrossbow.ProjectileAmountComponent> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(Codec.INT.optionalFieldOf("ammo_amount", 0).forGetter(RepeatingCrossbow.ProjectileAmountComponent::ammoAmount))
            .apply(builder, RepeatingCrossbow.ProjectileAmountComponent::new)
      );
      public static final StreamCodec<FriendlyByteBuf, RepeatingCrossbow.ProjectileAmountComponent> STREAM_CODEC = StreamCodec.of(
         (buf, data) -> buf.writeInt(data.ammoAmount), buf -> new RepeatingCrossbow.ProjectileAmountComponent(buf.readInt())
      );

      public RepeatingCrossbow.ProjectileAmountComponent setAmmoAmount(int ammoAmount) {
         return new RepeatingCrossbow.ProjectileAmountComponent(ammoAmount);
      }

      @Override
      public boolean equals(Object obj) {
         return obj == this
            || obj instanceof RepeatingCrossbow.ProjectileAmountComponent projectileAmountComponent && projectileAmountComponent.ammoAmount == this.ammoAmount;
      }
   }
}
