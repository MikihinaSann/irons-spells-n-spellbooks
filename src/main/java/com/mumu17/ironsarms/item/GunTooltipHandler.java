package com.mumu17.ironsarms.item;

import com.mumu17.ironsarms.utils.GunTags;
import com.tacz.guns.api.item.IGun;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "ironsarms")
public class GunTooltipHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IGun) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = data == null ? null : data.copyTag();
            if (tag != null && tag.contains(GunTags.INSCRIBED_SPELL_KEY)) {
                CompoundTag spellTag = tag.getCompound(GunTags.INSCRIBED_SPELL_KEY);
                String spellId = spellTag.getString("SpellID");
                int level = spellTag.getInt("Level");
                AbstractSpell spell = SpellRegistry.getSpell(spellId);
                if (spell != null && spell != SpellRegistry.none()) {
                    List<Component> tooltip = event.getToolTip();
                    tooltip.add(Component.literal(""));
                    tooltip.add(Component.translatable("tooltip.ironsarms.inscribed_title").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
                    tooltip.add(
                            Component.translatable("tooltip.ironsarms.spell_name")
                                    .append(Component.literal(": "))
                                    .append(spell.getDisplayName(event.getEntity()).copy().withStyle(ChatFormatting.GREEN))
                    );
                    tooltip.add(
                            Component.translatable("tooltip.ironsarms.spell_level")
                                    .append(Component.literal(": "))
                                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GOLD))
                    );
                    tooltip.add(
                            Component.translatable("tooltip.ironsarms.mana")
                                    .append(Component.literal(": "))
                                    .append(Component.literal(String.valueOf(GunTags.getMana(stack))).withStyle(ChatFormatting.AQUA))
                    );
                }
            }
        }
    }
}
