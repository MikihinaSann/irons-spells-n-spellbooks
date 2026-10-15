package net.hazen.hazennstuff.Item.Armor.AzureLib.GabrielULTRAKILL;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Animations.HnSDispatcher;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableHnSArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import org.jetbrains.annotations.NotNull;

public class GabrielElytraArmorItem extends ImbuableHnSArmorItem implements IDisableJacket {
   public final HnSDispatcher dispatcher = new HnSDispatcher();

   public GabrielElytraArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.NERFED_DEUS_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
      );
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addArcaneResonance(attributes, group);
      ArsNoveauCompat.addManaRegen(attributes, group);
      ArsNoveauCompat.addMaxMana(attributes, group);
      return attributes.build().modifiers();
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(Component.translatable("item.hazennstuff.ultrakill.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC}));
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return true;
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return true;
   }
}
