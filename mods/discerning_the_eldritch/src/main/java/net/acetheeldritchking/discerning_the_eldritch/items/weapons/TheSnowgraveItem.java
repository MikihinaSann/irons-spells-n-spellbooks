package net.acetheeldritchking.discerning_the_eldritch.items.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public class TheSnowgraveItem extends MagicSwordItem {
   public TheSnowgraveItem() {
      super(
         DTEWeaponTiers.SNOWGRAVE,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)ASRarities.GLACIAL_RARITY_PROXY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(DTEWeaponTiers.SNOWGRAVE)),
         SpellDataRegistryHolder.of(
            new SpellDataRegistryHolder[]{
               new SpellDataRegistryHolder(SpellRegistry.ICE_TOMB_SPELL, 10), new SpellDataRegistryHolder(SpellRegistries.CRYSTALLINE_CARVER, 5)
            }
         )
      );
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.translatable("item.discerning_the_eldritch.snowgrave.description_1").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)
      );
      tooltipComponents.add(
         Component.translatable("item.discerning_the_eldritch.snowgrave.description_2").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)
      );
   }
}
