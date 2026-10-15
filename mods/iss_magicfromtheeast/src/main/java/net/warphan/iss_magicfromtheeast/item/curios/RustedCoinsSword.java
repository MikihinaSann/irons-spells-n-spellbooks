package net.warphan.iss_magicfromtheeast.item.curios;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.setup.MFTERarity;
import org.jetbrains.annotations.NotNull;

public class RustedCoinsSword extends SimpleDescriptiveCurio implements IPresetSpellContainer {
   public RustedCoinsSword(Properties properties) {
      super(properties.rarity((Rarity)MFTERarity.BLOODFUL_RARITY_PROXY.getValue()));
   }

   public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, components, flag);
      AffinityData affinity = AffinityData.getAffinityData(stack);
      if (!affinity.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(components, "tooltip.irons_spellbooks.spellbook_spell_count");
         components.addAll(i < 0 ? components.size() : i + 1, affinity.getDescriptionComponent());
      }
   }

   public void initializeSpellContainer(ItemStack stack) {
      if (stack != null) {
         stack.set(ComponentRegistry.AFFINITY_COMPONENT, new AffinityData(Map.of(MFTESpellRegistries.JIANGSHI_INVOKE_SPELL.get().getSpellResource(), 2)));
      }
   }
}
