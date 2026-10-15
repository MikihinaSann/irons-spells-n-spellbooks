package net.warphan.iss_magicfromtheeast.item.armor;
import net.warphan.iss_magicfromtheeast.IssMagicfromtheeastClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.entity.armor.BootsOfMistArmorModel;
import net.warphan.iss_magicfromtheeast.registries.MFTEArmorMaterialRegistries;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BootsOfMistArmorItem extends ImbuableChestplateArmorItem {
   public static final int COOLDOWN_TICKS = 300;

   public BootsOfMistArmorItem(Type type, Properties properties) {
      super(
         MFTEArmorMaterialRegistries.BOOTS_OF_MIST,
         type,
         properties,
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
            new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(Attributes.SNEAKING_SPEED, 0.5, Operation.ADD_MULTIPLIED_BASE)
         }
      );
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, components, tooltipFlag);
      components.add(
         Component.translatable(
               "tooltip.irons_spellbooks.passive_ability", new Object[]{Component.literal(Utils.timeFromTicks(300.0F, 1)).withStyle(ChatFormatting.AQUA)}
            )
            .withStyle(ChatFormatting.DARK_PURPLE)
      );
      components.add(Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".description")).withStyle(ChatFormatting.LIGHT_PURPLE));
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return IssMagicfromtheeastClientRenderers.bootsOfMistArmorItem(); }
}
