package net.neoforged.neoforge.common.util;

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

/**
 * Backing store for NeoForge's 4-arg {@code MobEffect.AttributeTemplate(id, amount, op, curve)}
 * record variant — a record ctor can't be added to a vanilla record on Fabric, so mods
 * register the custom curve by template id here and {@code MobEffect$AttributeTemplate.create}
 * applies it (MobEffectAttributeTemplateMixin).
 */
public final class AttributeTemplateCurves {
    private static final Map<ResourceLocation, Int2DoubleFunction> CURVES = new ConcurrentHashMap<>();

    public static void register(ResourceLocation id, Int2DoubleFunction curve) {
        CURVES.put(id, curve);
    }

    /** Nullable. */
    public static Int2DoubleFunction get(ResourceLocation id) {
        return CURVES.get(id);
    }

    private AttributeTemplateCurves() {
    }
}
