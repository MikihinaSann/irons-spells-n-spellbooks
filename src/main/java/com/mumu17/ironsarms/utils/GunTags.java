package com.mumu17.ironsarms.utils;

import com.tacz.guns.api.item.IGun;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Stores the charged mana in the gun stack's {@code minecraft:custom_data}
 * component under {@code ironsarms:Mana} (the original mod used the root NBT
 * tag, which does not exist on 1.21.1 itemstacks).
 */
public class GunTags {
    public static final String MANA_KEY = "ironsarms:Mana";
    public static final String INSCRIBED_SPELL_KEY = "InscribedSpell";

    public static void addMana(ItemStack stack, int mana) {
        setMana(stack, getMana(stack) + mana);
    }

    public static void setMana(ItemStack stack, int mana) {
        if (isTargetItem(stack) && mana >= 0) {
            int value = mana;
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(MANA_KEY, value));
        }
    }

    public static int getMana(ItemStack stack) {
        return containsManaTag(stack) ? stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(MANA_KEY) : 0;
    }

    public static boolean containsManaTag(ItemStack stack) {
        return isTargetItem(stack) && stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(MANA_KEY);
    }

    public static boolean isTargetItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof IGun;
    }
}
