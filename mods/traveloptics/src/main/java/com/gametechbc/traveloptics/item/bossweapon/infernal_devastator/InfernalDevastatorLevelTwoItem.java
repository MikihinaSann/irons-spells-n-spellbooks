package com.gametechbc.traveloptics.item.bossweapon.infernal_devastator;

import com.gametechbc.traveloptics.api.item.GeoMagicSwordItem;
import com.gametechbc.traveloptics.entity.extended.ExtendedFlareBombEntity;
import com.gametechbc.traveloptics.entity.item.infernal_devastator.InfernalDevastatorAnimation;
import com.gametechbc.traveloptics.entity.item.infernal_devastator.evo_two.InfernalDevastatorLevelTwoRenderer;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.init.TOSpells;
import com.gametechbc.traveloptics.item.TOWeaponTier;
import com.github.L_Ender.cataclysm.client.particle.Options.LightningParticleOptions;
import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class InfernalDevastatorLevelTwoItem extends GeoMagicSwordItem {
   private static ItemDisplayContext transformType;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public String externalAnimation = "empty";
   String prevAnim = "empty";

   public InfernalDevastatorLevelTwoItem() {
      super(
         TOWeaponTier.INFERNAL_DEVASTATOR,
         ItemPropertiesHelper.equipment().rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(TOWeaponTier.INFERNAL_DEVASTATOR)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(TOSpells.GYRO_SLASH_SPELL, 1)})
      );
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
      super.inventoryTick(stack, level, entity, slot, selected);
      if (entity instanceof Player player) {
         MagicData magicData = MagicData.getPlayerMagicData(player);
         if (magicData.isCasting() && "traveloptics:gyro_slash".equals(magicData.getCastingSpellId())) {
            this.externalAnimation = "blade";
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (hand == InteractionHand.MAIN_HAND) {
         MobEffectInstance overloadedEffect = player.getEffect(TOEffects.OVERLOADED_EFFECT);
         if (overloadedEffect != null) {
            if (!world.isClientSide) {
               player.displayClientMessage(Component.translatable("effect.traveloptics.overloaded.warning").withStyle(ChatFormatting.RED), true);
            }

            return new InteractionResultHolder(InteractionResult.FAIL, player.getItemInHand(hand));
         } else {
            ItemStack stack = player.getItemInHand(hand);
            player.startUsingItem(hand);
            return new InteractionResultHolder(InteractionResult.SUCCESS, stack);
         }
      } else {
         return new InteractionResultHolder(InteractionResult.FAIL, player.getItemInHand(hand));
      }
   }

   public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
      if (entity instanceof Player player && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
         int chargeDuration = this.getUseDuration(stack, entity) - timeLeft;
         if (chargeDuration >= 7 && !world.isClientSide) {
            player.getCooldowns().addCooldown(this, 160);
         }
      }
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BOW;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 58;
   }

   public void onUseTick(Level level, LivingEntity caster, ItemStack stack, int count) {
      if (caster instanceof Player player && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
         int chargeTime = this.getUseDuration(stack, caster) - count;
         if (chargeTime == 7) {
            caster.addEffect(new MobEffectInstance(TOEffects.OVERLOADED_EFFECT, 80, 0, false, false, false));
            caster.playSound((SoundEvent)TOSounds.DEVASTATOR_CANON_TRANSFORM.get(), 1.0F, 1.0F);
            caster.playSound((SoundEvent)TOSounds.DEVASTATOR_CANON_TRANSFORM_OVER.get(), 1.0F, 1.0F);
            InfernalDevastatorAnimation.executeCanonAnimation(caster, stack);
         }

         if (chargeTime > 43) {
            this.makeChargeUpParticles(level, caster);
         }

         if (chargeTime == 44) {
            caster.playSound((SoundEvent)TOSounds.LASER_CHARGE.get(), 1.0F, 0.8F);
         }

         if (chargeTime == 57) {
            this.handleShooting(level, caster);
            player.getCooldowns().addCooldown(this, 280);
         }
      }
   }

   private void handleShooting(Level level, LivingEntity caster) {
      this.spawnFlareBomb(level, caster);
      caster.playSound((SoundEvent)ModSounds.MONSTROSITYSHOOT.get(), 1.2F, 1.0F);
      ScreenShake_Entity.ScreenShake(level, caster.position(), 12.0F, 0.025F, 10, 20);
   }

   private void spawnFlareBomb(Level world, LivingEntity player) {
      Vec3 lookVec = player.getLookAngle();
      int projectileCount = 5;
      double spreadAngle = Math.toRadians(60.0);
      double angleBetween = spreadAngle / (projectileCount - 1);
      Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
      Vec3 rightVec = lookVec.cross(upVec).normalize().scale(0.2);

      for (int i = 0; i < projectileCount; i++) {
         double angle = -spreadAngle / 2.0 + angleBetween * i;
         double x = lookVec.x * Math.cos(angle) - lookVec.z * Math.sin(angle);
         double z = lookVec.x * Math.sin(angle) + lookVec.z * Math.cos(angle);
         Vec3 motion = new Vec3(x, lookVec.y, z).normalize().scale(0.8);
         Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(1.0)).add(rightVec);
         ExtendedFlareBombEntity flareBomb = new ExtendedFlareBombEntity(world, player);
         flareBomb.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
         flareBomb.setDeltaMovement(motion);
         flareBomb.setFlameJetDamage(20.0F);
         world.addFreshEntity(flareBomb);
      }

      Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(1.2)).add(rightVec.scale(0.3));
      float yaw = (float)Math.atan2(-lookVec.z, lookVec.x) + (float) (Math.PI / 2);
      float pitch = 0.0F;
      world.addParticle(new RingParticleOptions(yaw, pitch, 20, 1, 1, 1, 1.0F, 20.0F, false, 1), spawnPos.x, spawnPos.y, spawnPos.z, 0.0, 0.0, 0.0);
   }

   private void makeChargeUpParticles(Level level, LivingEntity caster) {
      if (!level.isClientSide) {
         Vec3 lookVec = caster.getLookAngle();
         Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
         Vec3 rightVec = lookVec.cross(upVec).normalize().scale(0.22);
         Vec3 particleSpawnPos = caster.getEyePosition().add(lookVec.scale(0.6)).add(rightVec);
         MagicManager.spawnParticles(
            level, new LightningParticleOptions(230, 100, 16), particleSpawnPos.x, particleSpawnPos.y, particleSpawnPos.z, 1, 0.0, 0.0, 0.0, 0.2, false
         );
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
      tooltip.add(Component.translatable("item.traveloptics.infernal_devastator.tooltip").withStyle(ChatFormatting.YELLOW));
      tooltip.add(Component.translatable("ui.traveloptics.weapon.evolution_two").withStyle(ChatFormatting.YELLOW));
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("item.traveloptics.infernal_devastator.tooltip2"));
         tooltip.add(Component.literal(""));
         tooltip.add(Component.translatable("item.traveloptics.weapons.evolution_benefits.tooltip").withStyle(ChatFormatting.YELLOW));
         tooltip.add(Component.translatable("item.traveloptics.infernal_devastator.evo_one.active.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.infernal_devastator.evo_two.active.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.infernal_devastator.evo_three.inactive.tooltip"));
         tooltip.add(Component.literal(""));
      } else {
         tooltip.add(Component.translatable("item.traveloptics.weapons.evolution.stars_two.tooltip"));
         tooltip.add(Component.literal("§8[Hold Shift for more info]"));
      }

      super.appendHoverText(stack, context, tooltip, flags);
   }

   public void getTransformType(ItemDisplayContext type) {
      transformType = type;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   protected Object createRenderer() {
      return new InfernalDevastatorLevelTwoRenderer();
   }

   private PlayState idlePredicate(AnimationState event) {
      if (this.externalAnimation.equals("empty")) {
         event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState externalPredicate(AnimationState event) {
      if (!this.externalAnimation.equals("empty") && event.getController().getAnimationState() == State.STOPPED
         || !this.externalAnimation.equals(this.prevAnim) && !this.externalAnimation.equals("empty")) {
         if (!this.externalAnimation.equals(this.prevAnim)) {
            event.getController().forceAnimationReset();
         }

         event.getController().setAnimation(RawAnimation.begin().thenPlay(this.externalAnimation));
         if (event.getController().getAnimationState() == State.STOPPED) {
            this.externalAnimation = "empty";
            event.getController().forceAnimationReset();
         }
      } else if (this.externalAnimation.equals("empty")) {
         this.prevAnim = "empty";
         return PlayState.STOP;
      }

      this.prevAnim = this.externalAnimation;
      return PlayState.CONTINUE;
   }

   @Override
   public void registerControllers(ControllerRegistrar data) {
      AnimationController externalController = new AnimationController(this, "externalController", 0, this::externalPredicate);
      data.add(externalController);
      AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
      data.add(idleController);
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
