package net.neoforged.neoforge.internal.fabric.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.internal.fabric.EnumExtender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's enum extensions grow an enum's constant array at runtime, but
 * {@code StringRepresentable} codecs snapshot the values array into their
 * inner name/id resolvers during class init. When the baked resolver misses
 * on a string input, fall back to the live {@code getEnumConstants()} view
 * (which enum-extension support keeps refreshed) so datapack codecs can see
 * dynamically-added constants like custom MobCategories and Rarities.
 */
@Mixin(targets = "net.minecraft.util.StringRepresentable$StringRepresentableCodec")
public abstract class EnumCodecApiMixin {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "decode", at = @At("RETURN"), cancellable = true)
    private <T> void neoforge$liveEnumFallback(DynamicOps<T> ops, T input,
            CallbackInfoReturnable<DataResult<Pair<?, T>>> cir) {
        if (cir.getReturnValue().error().isEmpty()) {
            return;
        }
        String name = ops.getStringValue(input).result().orElse(null);
        if (name == null) {
            return;
        }
        for (Class<? extends Enum<?>> enumClass : EnumExtender.extendedEnumClasses()) {
            for (Enum<?> constant : enumClass.getEnumConstants()) {
                if (constant instanceof StringRepresentable rep && rep.getSerializedName().equals(name)) {
                    cir.setReturnValue(DataResult.success(Pair.of(constant, ops.empty())));
                    return;
                }
            }
        }
    }
}
