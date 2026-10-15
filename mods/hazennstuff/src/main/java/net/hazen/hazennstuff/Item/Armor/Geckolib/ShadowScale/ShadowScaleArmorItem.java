package net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowScale;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSAttributeRegistry;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ShadowScaleArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public ShadowScaleArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.PURE_ARMOR_TIER_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(HnSAttributeRegistry.SHADOW_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(Attributes.CRIT_CHANCE, 0.05, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
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
      lines.add(Component.translatable("item.hazennstuff.terraria.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC}));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.shadowScaleArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.MAGE_SET_BONUS, 320, 0, false, false, false));
      }

      if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
         player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 320, 0, false, false, false));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof ShadowScaleArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof ShadowScaleArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof ShadowScaleArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof ShadowScaleArmorItem;
   }
}
