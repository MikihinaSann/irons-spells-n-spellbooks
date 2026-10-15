package net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler.Soul;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SoulLegionnaireRulerArmorItem extends ImbuableGeckolibHnSArmorItem {
   public SoulLegionnaireRulerArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.LEGIONNAIRE_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.SUMMON_DAMAGE, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(Attributes.MAX_HEALTH, 1.25, Operation.ADD_VALUE)
      );
   }

   @Override
   public List<Entry> createExtraAttributes() {
      EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());
      Builder attributes = ItemAttributeModifiers.builder();
      MalumCompat.addScytheProfeciency(attributes, group);
      return attributes.build().modifiers();
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.soulLegionnaireRulerArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.TYRANTS_GRACE_EFFECT)) {
         player.addEffect(new MobEffectInstance(HnSEffects.TYRANTS_GRACE_EFFECT, 320, 0, false, false, true));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof SoulLegionnaireRulerArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof SoulLegionnaireRulerArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof SoulLegionnaireRulerArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof SoulLegionnaireRulerArmorItem;
   }
}
