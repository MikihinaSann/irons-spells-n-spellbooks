package net.hazen.hazennstuff.Item.Armor.AzureLib.FireblossomBattlemage;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import java.util.List;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Datagen.HnSTags;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FireblossomBattlemageArmorItem extends ImbuableHnSArmorItem implements IDisableJacket {
   public FireblossomBattlemageArmorItem(Type type, Properties settings) {
      super(HnSArmorMaterials.PURE_ARMOR_TIER_MATERIAL, type, settings, pureTierMulti(AttributeRegistry.NATURE_SPELL_POWER, AttributeRegistry.FIRE_SPELL_POWER));
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addArcaneResonance(attributes, group);
      MalumCompat.addMagicProfeciency(attributes, group);
      ArsNoveauCompat.addManaRegen(attributes, group);
      ArsNoveauCompat.addMaxMana(attributes, group);
      return attributes.build().modifiers();
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(
         Component.translatable("item.hazennstuff.fireblossom_armor.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC})
      );
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (!level.isClientSide && entity instanceof Player player) {
         player.getArmorSlots().forEach(wornArmor -> {
            if (wornArmor != null && wornArmor.is(HnSTags.ARMORS_FOR_IDLE)) {
               this.dispatcher.idle(player, wornArmor);
            }
         });
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.FIREBLOSSOM_RULER_EFFECT)) {
         player.addEffect(new MobEffectInstance(HnSEffects.FIREBLOSSOM_RULER_EFFECT, 320, 0, false, false, false));
      }
   }
}
