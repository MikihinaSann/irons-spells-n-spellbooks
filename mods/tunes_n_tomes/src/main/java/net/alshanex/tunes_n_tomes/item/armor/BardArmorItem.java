package net.alshanex.tunes_n_tomes.item.armor;
import net.alshanex.tunes_n_tomes.TunesNTomesClientRenderers;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import java.util.List;
import net.alshanex.tunes_n_tomes.entity.armor.BardArmorModel;
import net.alshanex.tunes_n_tomes.registry.TArmorSets;
import net.alshanex.tunes_n_tomes.registry.TAttributeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.alshanex.tunes_n_tomes.TunesNTomesClientRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BardArmorItem extends ImbuableChestplateArmorItem {
   public BardArmorItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(TAttributeRegistry.MELODY_SPELL_POWER));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(
      @NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag
   ) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
         TunesNTomesClientRenderers.addSetTooltip(TArmorSets.BARD_SET, tooltipComponents);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return TunesNTomesClientRenderers.bardArmorItem(); }
}
