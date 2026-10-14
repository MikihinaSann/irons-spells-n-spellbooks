package com.gametechbc.traveloptics.item.bossweapon.mechanized_wraithblade;

import com.gametechbc.traveloptics.api.TODataComponents;
import com.gametechbc.traveloptics.api.item.GeoMagicSwordItem;
import com.gametechbc.traveloptics.data_manager.PlasmaCoreManager;
import com.gametechbc.traveloptics.entity.item.mechanized_wraithblade.base.MechanizedWraithbladeItemRenderer;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.item.TOWeaponTier;
import com.github.L_Ender.cataclysm.client.particle.Options.LightningParticleOptions;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
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

public class MechanizedWraithbladeItem extends GeoMagicSwordItem {
   private static ItemDisplayContext transformType;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public String externalAnimation = "empty";
   private static final String SWING_COUNTER_TAG = "SwingCounter";
   private ItemStack currentStack;
   String prevAnim = "empty";

   public MechanizedWraithbladeItem() {
      super(
         TOWeaponTier.MECHANIZED_WRAITHBLADE,
         ItemPropertiesHelper.equipment().rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(TOWeaponTier.MECHANIZED_WRAITHBLADE)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(SpellRegistry.COUNTERSPELL_SPELL, 1)})
      );
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (attacker instanceof Player player) {
         int swingCounter = this.getSwingCounter(stack);

         PlasmaCoreManager.addPlasmaCore(stack, player, switch (swingCounter) {
            case 1 -> 5;
            case 2 -> 10;
            case 3 -> 15;
            default -> 5;
         });
         this.setSwingCounter(stack, swingCounter == 3 ? 1 : swingCounter + 1);

         for (int i = 0; i < 20; i++) {
            double offsetX = (target.getRandom().nextDouble() - 0.5) * target.getBbWidth();
            double offsetY = target.getRandom().nextDouble() * target.getBbHeight();
            double offsetZ = (target.getRandom().nextDouble() - 0.5) * target.getBbWidth();
            MagicManager.spawnParticles(
               target.level(),
               new LightningParticleOptions(255, 26, 0),
               target.getX() + offsetX,
               target.getY() + offsetY,
               target.getZ() + offsetZ,
               1,
               0.0,
               0.0,
               0.0,
               0.3,
               false
            );
         }
      }

      return super.hurtEnemy(stack, target, attacker);
   }

   private int getSwingCounter(ItemStack stack) {
      return stack.has(TODataComponents.WEAPON_SWING_COUNTER) ? ((CompoundTag)stack.get(TODataComponents.WEAPON_SWING_COUNTER)).getInt("SwingCounter") : 1;
   }

   private void setSwingCounter(ItemStack stack, int counter) {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("SwingCounter", counter);
      stack.set(TODataComponents.WEAPON_SWING_COUNTER, nbt);
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      if (handIn == InteractionHand.MAIN_HAND) {
         playerIn.startUsingItem(handIn);
         return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
      } else {
         return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
      }
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BOW;
   }

   public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
      return 72000;
   }

   public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
      if (entity instanceof Player player && player.getMainHandItem() == stack) {
         int plasmaCore = PlasmaCoreManager.getPlasmaCore(stack);
         if (plasmaCore <= 0) {
            player.stopUsingItem();
            if (!level.isClientSide) {
               player.displayClientMessage(Component.literal("⚡ Plasma Core depleted!").withStyle(style -> style.withColor(11412276)), true);
            }

            return;
         }

         int chargeTime = this.getUseDuration(stack, entity) - count;
         if (chargeTime < 8) {
            if (!level.isClientSide && chargeTime == 1) {
               player.displayClientMessage(Component.literal("⚡ Charging.").withStyle(style -> style.withColor(16476957)), true);
            }

            if (!level.isClientSide && chargeTime == 3) {
               player.displayClientMessage(Component.literal("⚡ Charging..").withStyle(style -> style.withColor(16476957)), true);
            }

            if (!level.isClientSide && chargeTime == 6) {
               player.displayClientMessage(Component.literal("⚡ Charging...").withStyle(style -> style.withColor(16476957)), true);
            }

            if (!level.isClientSide && chargeTime == 2) {
               level.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.LASER_CHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.2F);
            }

            return;
         }

         if (count % 3 == 0) {
            if (level.isClientSide) {
               RandomSource randomSource = level.getRandom();
               Vec3 lookVec = entity.getViewVector(1.0F).normalize();
               Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
               Vec3 rightVec = lookVec.cross(upVec).normalize().scale(0.22);
               Vec3 particleSpawnPos = entity.getEyePosition().add(lookVec.scale(0.6)).add(rightVec);
               double d = randomSource.nextFloat() - 0.5F;
               level.addParticle(new LightningParticleOptions(255, 26, 0), particleSpawnPos.x, particleSpawnPos.y, particleSpawnPos.z, d * 2.0, d, d * 2.0);
            }

            if (!level.isClientSide) {
               float damage;
               if (plasmaCore > 200) {
                  damage = 14.0F;
               } else if (plasmaCore > 100) {
                  damage = 12.0F;
               } else if (plasmaCore > 50) {
                  damage = 10.0F;
               } else {
                  damage = 8.0F;
               }

               this.spawnLaserBeam(level, entity, damage, 1.0F);
               float pitch;
               if (plasmaCore < 5) {
                  pitch = 1.2F;
               } else if (plasmaCore < 15) {
                  pitch = 1.1F;
               } else {
                  pitch = 1.0F;
               }

               level.playSound(null, entity.blockPosition(), (SoundEvent)TOSounds.LASER_SHOOT.get(), SoundSource.PLAYERS, 0.7F, pitch);
               player.displayClientMessage(Component.literal("⚡ Plasma Core: " + (plasmaCore - 1)).withStyle(style -> style.withColor(11582917)), true);
            }

            entity.gameEvent(GameEvent.ITEM_INTERACT_START);
            PlasmaCoreManager.setPlasmaCore(stack, plasmaCore - 1);
         }
      }
   }

   private void spawnLaserBeam(Level level, LivingEntity caster, float damage, float speed) {
      Vec3 lookVec = caster.getViewVector(1.0F).normalize();
      Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
      Vec3 rightVec = lookVec.cross(upVec).normalize().scale(0.22);
      Vec3 laserSpawnPos = caster.getEyePosition().add(lookVec.scale(1.0)).add(rightVec);
      RandomSource randomSource = level.getRandom();
      double spread = 0.1;
      double offsetX = (randomSource.nextDouble() - 0.5) * spread;
      double offsetY = (randomSource.nextDouble() - 0.5) * spread;
      double offsetZ = (randomSource.nextDouble() - 0.5) * spread;
      Vec3 spreadVec = lookVec.add(offsetX, offsetY, offsetZ).normalize().scale(speed);
      float yRot = (float)(Mth.atan2(lookVec.z, lookVec.x) * (180.0 / Math.PI)) + 90.0F;
      float xRot = (float)(-(Mth.atan2(lookVec.y, Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z)) * (180.0 / Math.PI)));
      Laser_Beam_Entity laserBeam = new Laser_Beam_Entity(
         (EntityType)ModEntities.LASER_BEAM.get(), caster, laserSpawnPos.x, laserSpawnPos.y - 0.21, laserSpawnPos.z, spreadVec, damage, level
      );
      laserBeam.setYRot(yRot);
      laserBeam.setXRot(xRot);
      level.addFreshEntity(laserBeam);
      ScreenShake_Entity.ScreenShake(level, caster.position(), 8.0F, 0.012F, 2, 3);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
      tooltip.add(Component.translatable("item.traveloptics.mechanized_wraithblade.tooltip").withStyle(ChatFormatting.YELLOW));
      int plasmaCoreValue = PlasmaCoreManager.getPlasmaCore(stack);
      tooltip.add(Component.literal("⚡ Plasma Core: " + plasmaCoreValue));
      tooltip.add(Component.translatable("ui.traveloptics.weapon.evolution_zero").withStyle(ChatFormatting.YELLOW));
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("item.traveloptics.mechanized_wraithblade.tooltip1"));
         tooltip.add(Component.literal(""));
         tooltip.add(Component.translatable("item.traveloptics.weapons.evolution_benefits.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.mechanized_wraithblade.evo_one.inactive.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.mechanized_wraithblade.evo_two.inactive.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.mechanized_wraithblade.evo_three.inactive.tooltip"));
         tooltip.add(Component.literal(""));
      } else {
         tooltip.add(Component.translatable("item.traveloptics.weapons.evolution.stars_zero.tooltip"));
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
      return new MechanizedWraithbladeItemRenderer();
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
      AnimationController procedureController = new AnimationController(this, "externalController", 0, this::externalPredicate);
      data.add(procedureController);
      AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
      data.add(idleController);
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void setStack(ItemStack stack) {
      this.currentStack = stack;
   }

   public ItemStack getStack() {
      return this.currentStack;
   }
}
