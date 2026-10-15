package net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireCommander;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LegionnaireCommanderArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public LegionnaireCommanderArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.LEGIONNAIRE_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 100.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.1, Operation.ADD_VALUE),
         new AttributeContainer(Attributes.FIRE_DAMAGE, 2.0, Operation.ADD_VALUE),
         new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
         new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_VALUE)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.legionnaireCommanderArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.TYRANTS_GRACE_EFFECT)) {
         player.addEffect(new MobEffectInstance(HnSEffects.TYRANTS_GRACE_EFFECT, 320, 0, false, false, false));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof LegionnaireCommanderArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof LegionnaireCommanderArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof LegionnaireCommanderArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof LegionnaireCommanderArmorItem;
   }
}
