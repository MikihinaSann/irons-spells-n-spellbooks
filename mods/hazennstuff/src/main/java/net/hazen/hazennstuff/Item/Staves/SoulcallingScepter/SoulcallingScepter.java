package net.hazen.hazennstuff.Item.Staves.SoulcallingScepter;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSStaffTier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SoulcallingScepter extends StaffItem {
   public final HnSDispatcher dispatcher = new HnSDispatcher();

   public SoulcallingScepter() {
      super(
         ItemPropertiesHelper.equipment(1).fireResistant().rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(HnSStaffTier.SOULCALLING_SCEPTER))
      );
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && entity instanceof Player player) {
         this.dispatcher.idle(player, stack);
      }
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      boolean result = super.hurtEnemy(stack, target, attacker);
      if (!attacker.level().isClientSide) {
         boolean isCrit = false;
         if (attacker instanceof Player player) {
            isCrit = player.fallDistance > 0.0F
               && !player.onGround()
               && !player.isInWater()
               && !player.hasEffect(MobEffects.BLINDNESS)
               && !player.isPassenger()
               && !player.isSprinting()
               && player.getMainHandItem() == stack;
         }

         if (isCrit) {
            attacker.level()
               .playSound(null, target.getX(), target.getY(), target.getZ(), SoundRegistry.SOULCALLER_TOLL_SUCCESS, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }

      return result;
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.soulcalling_scepter.description").withStyle(Style.EMPTY.withColor(15881518).withItalic(true)));
   }
}
