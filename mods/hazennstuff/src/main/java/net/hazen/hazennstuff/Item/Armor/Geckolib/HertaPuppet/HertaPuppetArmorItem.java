package net.hazen.hazennstuff.Item.Armor.Geckolib.HertaPuppet;
import net.hazen.hazennstuff.HazennstuffClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.HnSUtilities.ImbuableGeckolibHnSArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HertaPuppetArmorItem extends ImbuableGeckolibHnSArmorItem implements IDisableJacket, IDisableHat {
   public HertaPuppetArmorItem(Type type, Properties settings) {
      super(
         HnSArmorMaterials.CRYOGENIC_RULER_MATERIAL,
         type,
         settings,
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.125, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
      );
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      lines.add(
         Component.translatable("item.hazennstuff.honkai_star_rail.description").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.ITALIC})
      );
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public Object supplyRenderer() { return HazennstuffClientRenderers.hertaPuppetArmorItem(); }
}
