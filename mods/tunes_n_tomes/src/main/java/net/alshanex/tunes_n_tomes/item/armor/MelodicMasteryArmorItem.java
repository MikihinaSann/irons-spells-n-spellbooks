package net.alshanex.tunes_n_tomes.item.armor;
import net.alshanex.tunes_n_tomes.TunesNTomesClientRenderers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import java.util.List;
import net.alshanex.tunes_n_tomes.entity.armor.MelodicMasteryArmorModel;
import net.alshanex.tunes_n_tomes.registry.TArmorSets;
import net.alshanex.tunes_n_tomes.registry.TAttributeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.alshanex.tunes_n_tomes.TunesNTomesClientRenderers;
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

public class MelodicMasteryArmorItem extends ImbuableChestplateArmorItem {
   public MelodicMasteryArmorItem(Type slot, Properties settings) {
      super(ArmorMaterialRegistry.SCHOOL, slot, settings, melodicMasteryAttributes());
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(
      @NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag
   ) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
         TunesNTomesClientRenderers.addSetTooltip(TArmorSets.MELODIC_MASTERY_SET, tooltipComponents);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Object supplyRenderer() { return TunesNTomesClientRenderers.melodicMasteryArmorItem(); }

   public static AttributeContainer[] melodicMasteryAttributes() {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 150.0, Operation.ADD_VALUE),
         new AttributeContainer(TAttributeRegistry.MELODY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.1, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.CASTING_MOVESPEED, 0.1, Operation.ADD_MULTIPLIED_BASE)
      };
   }
}
