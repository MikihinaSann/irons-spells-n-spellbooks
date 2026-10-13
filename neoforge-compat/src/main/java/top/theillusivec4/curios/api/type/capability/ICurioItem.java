package top.theillusivec4.curios.api.type.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

/**
 * Item-level curio contract — implemented on items directly (Curios-style).
 */
public interface ICurioItem {

    default ICurio getCurio(ItemStack stack) {
        return new ICurio() {
        };
    }

    default void curioTick(SlotContext slotContext, ItemStack stack) {
        getCurio(stack).curioTick(slotContext);
    }

    default void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        getCurio(stack).onEquip(slotContext, prevStack);
    }

    default void onUnequip(SlotContext slotContext, ItemStack stack) {
        getCurio(stack).onUnequip(slotContext);
    }

    default boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return getCurio(stack).canEquip(slotContext);
    }

    default boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return getCurio(stack).canUnequip(slotContext);
    }

    default Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                               ResourceLocation id,
                                                                               ItemStack stack) {
        return getCurio(stack).getAttributeModifiers(slotContext, id);
    }

    default boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return getCurio(stack).canEquipFromUse(slotContext);
    }

    default void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        getCurio(stack).onEquipFromUse(slotContext);
    }

    @Nullable
    default ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return getCurio(stack).getEquipSound(slotContext);
    }

    default void curioBreak(SlotContext slotContext, ItemStack stack) {
        getCurio(stack).curioBreak(slotContext);
    }

    default ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel,
                                        boolean recentlyHit, ItemStack stack) {
        return getCurio(stack).getDropRule(slotContext, source, lootingLevel, recentlyHit);
    }

    /**
     * Tooltip lines describing curio attribute modifiers. Mutates and returns
     * the provided list.
     */
    default java.util.List<net.minecraft.network.chat.Component> getAttributesTooltip(
            java.util.List<net.minecraft.network.chat.Component> tooltips,
            net.minecraft.world.item.Item.TooltipContext tooltipContext, ItemStack stack) {
        return tooltips;
    }
}
