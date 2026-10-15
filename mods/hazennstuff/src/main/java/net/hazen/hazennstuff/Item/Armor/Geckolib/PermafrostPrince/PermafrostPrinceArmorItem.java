package net.hazen.hazennstuff.Item.Armor.Geckolib.PermafrostPrince;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.Map;
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

public class PermafrostPrinceArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public PermafrostPrinceArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.HUNTER_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.15, Operation.ADD_VALUE),
         new AttributeContainer(Attributes.ARROW_DAMAGE, 0.15, Operation.ADD_VALUE),
         new AttributeContainer(Attributes.ARROW_VELOCITY, 0.15, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15, Operation.ADD_VALUE),
         new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.25, Operation.ADD_MULTIPLIED_TOTAL)
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.permafrostPrinceArmorItem(); }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      if (entity instanceof Player player && !level.isClientSide() && this.isWearingFullSet(player)) {
         this.evaluateArmorEffects(player);
      }
   }

   private void evaluateArmorEffects(Player player) {
      if (!player.hasEffect(HnSEffects.MAGE_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.MAGE_SET_BONUS, 320, 0, false, false, false));
      }

      if (!player.hasEffect(HnSEffects.ARCHER_SET_BONUS)) {
         player.addEffect(new MobEffectInstance(HnSEffects.ARCHER_SET_BONUS, 320, 0, false, false, false));
      }
   }

   private boolean isWearingFullSet(Player player) {
      return player.getItemBySlot(Type.HELMET.getSlot()).getItem() instanceof PermafrostPrinceArmorItem
         && player.getItemBySlot(Type.CHESTPLATE.getSlot()).getItem() instanceof PermafrostPrinceArmorItem
         && player.getItemBySlot(Type.LEGGINGS.getSlot()).getItem() instanceof PermafrostPrinceArmorItem
         && player.getItemBySlot(Type.BOOTS.getSlot()).getItem() instanceof PermafrostPrinceArmorItem;
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         itemStack.set(
            ComponentRegistry.AFFINITY_COMPONENT, new AffinityData(Map.of(((AbstractSpell)SpellRegistry.FROSTBITE_SPELL.get()).getSpellResource(), 1))
         );
      }
   }
}
