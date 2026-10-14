package com.github.L_Ender.cataclysm.items.CuriosItem;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;


public class Chitin_Claw extends CuriosItem {

    public Chitin_Claw(Properties group) {
        super(group);

    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_NETHERITE.value(), 1.0F, 1.0F);
    }

    /*
    public static CurioAttributeModifiers createAttributes() {
        return CurioAttributeModifiers.builder()
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BASE_ENTITY_INTERACTION_RANGE_ID, 0.5F, AttributeModifier.Operation.ADD_VALUE), "hands"
                )
                .add(
                        Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(BASE_BLOCK_INTERACTION_RANGE_ID, 1.0F, AttributeModifier.Operation.ADD_VALUE), "hands"
                )
                .build();
    }

     */


}
