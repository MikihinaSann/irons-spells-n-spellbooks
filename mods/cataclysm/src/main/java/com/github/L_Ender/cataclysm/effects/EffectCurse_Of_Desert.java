package com.github.L_Ender.cataclysm.effects;


import com.github.L_Ender.cataclysm.Cataclysm;
import com.github.L_Ender.cataclysm.init.ModAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectCurse_Of_Desert extends MobEffect {
   // private static final ResourceLocation CURSE_SPEED = (ResourceLocation.fromNamespaceAndPath(Cataclysm.MODID,"curse_speed"));
   // private static final ResourceLocation CURSE_NATURE_HEAL = (ResourceLocation.fromNamespaceAndPath(Cataclysm.MODID,"curse_nature_heal"));

    public EffectCurse_Of_Desert() {
        super(MobEffectCategory.HARMFUL, 0xfff2cb);

    }


    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }
}
