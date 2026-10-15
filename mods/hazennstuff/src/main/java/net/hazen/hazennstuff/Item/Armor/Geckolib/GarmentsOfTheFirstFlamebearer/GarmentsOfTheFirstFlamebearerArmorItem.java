package net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import java.util.List;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GarmentsOfTheFirstFlamebearerArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public GarmentsOfTheFirstFlamebearerArmorItem(Type type, Properties settings) {
      super(HnSArmorMaterials.TYROS_MATERIAL, type, settings, paragonTier(AttributeRegistry.FIRE_SPELL_POWER));
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
      lines.add(Component.translatable("item.hazennstuff.tyros.description").withStyle(Style.EMPTY.withColor(15881518).withItalic(true)));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.garmentsOfTheFirstFlamebearerArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.MAGE_SET_BONUS, 320, 0, false, false, false));
      }

      if (!player.hasEffect(HnSEffects.TYRANTS_GRACE_EFFECT)) {
         player.addEffect(new MobEffectInstance(HnSEffects.TYRANTS_GRACE_EFFECT, 320, 0, false, false, true));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerChestplateArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof GarmentsOfTheFirstFlamebearerArmorItem;
   }
}
