package net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
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

public class ArbitriumRobesArmorItem extends ImbuableHnSArmorItem implements IDisableJacket {
   public final HnSDispatcher dispatcher = new HnSDispatcher();

   public ArbitriumRobesArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.NERFED_DEUS_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 300.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
      );
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addArcaneResonance(attributes, group);
      ArsNoveauCompat.addMaxMana(attributes, group);
      ArsNoveauCompat.addManaRegen(attributes, group);
      return attributes.build().modifiers();
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(
         Component.translatable("item.hazennstuff.arbitrium_robes.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC})
      );
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide) {
         boolean isFlying = player.isFallFlying();
         player.getArmorSlots().forEach(wornArmor -> {
            if (wornArmor != null && wornArmor.getItem() instanceof ArbitriumRobesArmorItem) {
               if (isFlying) {
                  this.dispatcher.flight(player, wornArmor);
               } else {
                  this.dispatcher.idle(player, wornArmor);
               }
            }
         });
         if (this.isWearingFullSet(player) && !player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
            this.evaluateArmorEffects(player);
         }
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.MAGE_SET_BONUS, 320, 0, false, false, false));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof ArbitriumRobesArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof ArbitriumRobesElytraArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof ArbitriumRobesArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof ArbitriumRobesArmorItem;
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return true;
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return true;
   }
}
