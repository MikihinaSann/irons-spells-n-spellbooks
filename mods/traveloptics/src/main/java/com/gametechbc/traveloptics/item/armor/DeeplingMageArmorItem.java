package com.gametechbc.traveloptics.item.armor;

import com.gametechbc.traveloptics.api.item.AbstractImbuableArmorItem;
import com.gametechbc.traveloptics.item.TOArmorMaterials;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.ArchevokerArmorModel;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DeeplingMageArmorItem extends AbstractImbuableArmorItem {
   public DeeplingMageArmorItem(Type type, Properties settings) {
      super(TOArmorMaterials.DEEPLING_MAGE, type, settings, new AttributeContainer(AttributeRegistry.SUMMON_DAMAGE, 0.025, Operation.ADD_MULTIPLIED_BASE));
   }

   @Override
   protected Set<Type> getImbuableArmorTypes() {
      return Set.of(Type.CHESTPLATE, Type.HELMET);
   }

   @Override
   protected Map<Type, Integer> getMaxSpellSlots() {
      return Map.of(Type.CHESTPLATE, 1, Type.HELMET, 1);
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
      super.inventoryTick(stack, level, entity, i, held);
      if (entity instanceof Player player && !level.isClientSide && this.isWearingFullSet(player) && player.isUnderWater()) {
         this.grantWaterBreathing(player);
      }
   }

   private void grantWaterBreathing(Player player) {
      if (!player.hasEffect(MobEffects.WATER_BREATHING)) {
         player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
      tooltip.add(Component.translatable("item.traveloptics.deepling_mage_armor.tooltip").withStyle(ChatFormatting.DARK_GREEN));
      tooltip.add(Component.translatable("item.traveloptics.deepling_mage_armor.tooltip1"));
      tooltip.add(Component.literal(""));
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof DeeplingMageArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof DeeplingMageArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof DeeplingMageArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof DeeplingMageArmorItem;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() {
      return new GenericCustomArmorRenderer(new ArchevokerArmorModel());
   }
}
