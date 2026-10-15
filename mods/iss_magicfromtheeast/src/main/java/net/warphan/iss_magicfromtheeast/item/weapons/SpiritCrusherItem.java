package net.warphan.iss_magicfromtheeast.item.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.warphan.iss_magicfromtheeast.configs.MFTEServerConfigs;
import net.warphan.iss_magicfromtheeast.damage.MFTEDamageTypes;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantmentHelper;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantments;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;

public class SpiritCrusherItem extends ExtendedSwordItem {
   private static final int BASE_PERCENT_FROM_TARGET_HEALTH = 2;

   public SpiritCrusherItem(MFTEExtendedWeaponTier tier, Properties properties) {
      super(tier, properties);
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      float health = target.getHealth();
      float bonusDamage = health / 100.0F * (2 + getBonusPercent(stack, attacker) * 2);
      if ((Boolean)MFTEServerConfigs.ALLOW_SPIRIT_CRUSHER_DAMAGE_CAP.get()) {
         double maxDamage = (Double)MFTEServerConfigs.SPIRIT_CRUSHER_DAMAGE_CAP.get();
         if (bonusDamage > maxDamage) {
            target.hurt(attacker.damageSources().source(MFTEDamageTypes.SOUL_WEAPONS_BONUS), (float)maxDamage);
         } else {
            target.hurt(attacker.damageSources().source(MFTEDamageTypes.SOUL_WEAPONS_BONUS), bonusDamage);
         }
      } else {
         target.hurt(attacker.damageSources().source(MFTEDamageTypes.SOUL_WEAPONS_BONUS), bonusDamage);
      }

      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      if (target.getHealth() < target.getMaxHealth() * 0.3F) {
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
         target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 2));
      }
   }

   public static int getBonusPercent(ItemStack stack, LivingEntity livingEntity) {
      if (livingEntity.level instanceof ServerLevel serverLevel) {
         int bonusPercent = MFTEEnchantmentHelper.modifySoulDamage(serverLevel, stack, livingEntity, 0.0F);
         return Mth.floor(bonusPercent);
      } else {
         return 0;
      }
   }

   public static double getDisplaySoulDamage(ItemStack stack, Entity entity) {
      double basePercent = 2.0;
      if (!stack.isEmpty() && stack.has(DataComponents.ENCHANTMENTS) && entity != null) {
         basePercent += Utils.processEnchantment(
               entity.level,
               MFTEEnchantments.INNER_IMPACT,
               (DataComponentType)MFTEDataComponentRegistries.SOUL_DAMAGE.get(),
               (ItemEnchantments)stack.get(DataComponents.ENCHANTMENTS)
            )
            * 2.0F;
      }

      return basePercent;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.literal(" ")
            .append(
               Component.translatable(
                  this.getDescriptionId() + ".description",
                  new Object[]{
                     Component.literal(Utils.stringTruncation(getDisplaySoulDamage(stack, MinecraftInstanceHelper.getPlayer()), 1))
                        .withStyle(ChatFormatting.YELLOW)
                  }
               )
            )
            .withStyle(ChatFormatting.AQUA)
      );
   }
}
