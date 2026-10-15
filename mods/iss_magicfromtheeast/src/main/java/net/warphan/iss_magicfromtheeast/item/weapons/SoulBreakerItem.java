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
import net.warphan.iss_magicfromtheeast.damage.MFTEDamageTypes;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantmentHelper;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantments;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;

public class SoulBreakerItem extends ExtendedSwordItem {
   public static final float BASE_SOUL_DAMAGE = 3.0F;

   public SoulBreakerItem(MFTEExtendedWeaponTier tier, Properties properties) {
      super(tier, properties);
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      target.hurt(attacker.damageSources().source(MFTEDamageTypes.SOUL_WEAPONS_BONUS), 3.0F + getBonusSoulDamage(stack, attacker));
      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      if (target.getHealth() < target.getMaxHealth() * 0.6F) {
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
      }
   }

   public static int getBonusSoulDamage(ItemStack stack, LivingEntity livingEntity) {
      if (livingEntity.level instanceof ServerLevel serverLevel) {
         int bonusSoulDamage = MFTEEnchantmentHelper.modifySoulDamage(serverLevel, stack, livingEntity, 0.0F);
         return Mth.floor(bonusSoulDamage);
      } else {
         return 0;
      }
   }

   public static double getDisplaySoulDamage(ItemStack stack, Entity entity) {
      double baseDamage = 3.0;
      if (!stack.isEmpty() && stack.has(DataComponents.ENCHANTMENTS) && entity != null) {
         baseDamage += Utils.processEnchantment(
            entity.level,
            MFTEEnchantments.INNER_IMPACT,
            (DataComponentType)MFTEDataComponentRegistries.SOUL_DAMAGE.get(),
            (ItemEnchantments)stack.get(DataComponents.ENCHANTMENTS)
         );
      }

      return baseDamage;
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
