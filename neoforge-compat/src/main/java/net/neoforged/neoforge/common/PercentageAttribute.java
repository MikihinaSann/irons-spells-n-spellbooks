package net.neoforged.neoforge.common;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/**
 * Stand-in for NeoForge's PercentageAttribute: a ranged attribute displayed as a percentage.
 */
public class PercentageAttribute extends RangedAttribute {
    public PercentageAttribute(String translationKey, double defaultValue, double min, double max) {
        super(translationKey, defaultValue, min, max);
    }

    public PercentageAttribute(String translationKey, double defaultValue, double min, double max, boolean syncable) {
        super(translationKey, defaultValue, min, max);
        if (syncable) {
            setSyncable(true);
        }
    }

    @Override
    public Attribute setSyncable(boolean syncable) {
        return super.setSyncable(syncable);
    }
}
