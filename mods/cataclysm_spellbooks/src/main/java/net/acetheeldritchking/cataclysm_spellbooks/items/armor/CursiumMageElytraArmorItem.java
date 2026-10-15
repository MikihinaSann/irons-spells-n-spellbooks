package net.acetheeldritchking.cataclysm_spellbooks.items.armor;

import com.github.L_Ender.cataclysm.init.ModKeybind;
import com.github.L_Ender.cataclysm.items.KeybindUsingArmor;
import com.github.L_Ender.cataclysm.message.MessageArmorKey;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class CursiumMageElytraArmorItem extends ImbuableCSArmorItem implements KeybindUsingArmor {
   public CursiumMageElytraArmorItem(Type slot, Properties settings) {
      super(
         CSArmorMaterialRegistry.CURSIUM_WARLOCK_ARMOR,
         slot,
         settings,
         schoolAttributesWithResistance(AttributeRegistry.ICE_SPELL_POWER, AttributeRegistry.ICE_MAGIC_RESIST, 150, 0.2F, 0.05F, 0.1F)
      );
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return true;
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return ElytraItem.isFlyEnabled(stack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
      if (this.type == Type.HELMET) {
         tooltip.add(Component.translatable("item.cataclysm.cursium_helmet.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(
            Component.translatable("item.cataclysm.cursium_helmet.desc2", new Object[]{ModKeybind.HELMET_KEY_ABILITY.getTranslatedKeyMessage()})
               .withStyle(ChatFormatting.DARK_GREEN)
         );
      }

      if (this.type == Type.CHESTPLATE) {
         tooltip.add(Component.translatable("item.cataclysm.cursium_chestplate.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(Component.translatable("item.cataclysm.cursium_chestplate.desc2").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(Component.translatable("item.cataclysm.cursium_chestplate.desc3").withStyle(ChatFormatting.DARK_GREEN));
      }

      if (this.type == Type.LEGGINGS) {
         tooltip.add(Component.translatable("item.cataclysm.cursium_leggings.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(Component.translatable("item.cataclysm.cursium_leggings.desc2").withStyle(ChatFormatting.DARK_GREEN));
      }

      if (this.type == Type.BOOTS) {
         tooltip.add(Component.translatable("item.cataclysm.cursium_boots.desc").withStyle(ChatFormatting.DARK_GREEN));
         tooltip.add(
            Component.translatable("item.cataclysm.cursium_boots.desc2", new Object[]{ModKeybind.BOOTS_KEY_ABILITY.getTranslatedKeyMessage()})
               .withStyle(ChatFormatting.DARK_GREEN)
         );
      }
   }

   @Override
   public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
      if (pEntity instanceof Player player) {
         if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ItemRegistries.CURSIUM_MAGE_HELMET.get()
            && pLevel.isClientSide()
            && ModKeybind.HELMET_KEY_ABILITY.isDown()) {
            PacketDistributor.sendToServer(new MessageArmorKey(EquipmentSlot.HEAD.ordinal(), player.getId(), 5), new CustomPacketPayload[0]);
            this.onKeyPacket(player, pStack, 5);
         }

         if (player.getItemBySlot(EquipmentSlot.FEET).getItem() == ItemRegistries.CURSIUM_MAGE_BOOTS.get()
            && pLevel.isClientSide()
            && ModKeybind.BOOTS_KEY_ABILITY.isDown()) {
            PacketDistributor.sendToServer(new MessageArmorKey(EquipmentSlot.HEAD.ordinal(), player.getId(), 7), new CustomPacketPayload[0]);
            this.onKeyPacket(player, pStack, 7);
         }
      }
   }

   public void onKeyPacket(Player player, ItemStack itemStack, int i) {
      if (i == 5 && player != null && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.CURSIUM_MAGE_HELMET.get())) {
         boolean flag = false;

         for (Entity entities : player.level().getEntities(player, player.getBoundingBox().inflate(24.0))) {
            if (entities instanceof LivingEntity livingEntity) {
               flag = true;
               livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160));
            }
         }

         if (flag) {
            player.getCooldowns().addCooldown((Item)ItemRegistries.CURSIUM_MAGE_HELMET.get(), 200);
         }
      }

      if (i == 7 && player != null && !player.getCooldowns().isOnCooldown((Item)ItemRegistries.CURSIUM_MAGE_BOOTS.get())) {
         float speed = -1.8F;
         float yaw = (float)Math.toRadians(player.getYRot() + 90.0F);
         Vec3 vec3 = player.getDeltaMovement().add(speed * Math.cos(yaw), 0.0, speed * Math.sin(yaw));
         player.setDeltaMovement(vec3.x, 0.4, vec3.z);
         player.getCooldowns().addCooldown((Item)ItemRegistries.CURSIUM_MAGE_BOOTS.get(), 200);
      }
   }
}
