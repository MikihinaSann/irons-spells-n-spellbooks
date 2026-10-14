package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Diagnostic: vanilla {@code DataValue.read} throws a bare DecoderException when
 * a wire serializer id has no local serializer — without saying WHICH id. Log
 * the offending id plus how many serializers this side actually knows.
 */
@Mixin(EntityDataSerializers.class)
public abstract class EntityDataSerializersDebugMixin {

    @Inject(method = "getSerializer", at = @At("RETURN"), cancellable = true)
    private static void neoforge_compat$logUnknownSerializerId(int id,
            CallbackInfoReturnable<EntityDataSerializer<?>> cir) {
        if (cir.getReturnValue() == null) {
            LoggerFactory.getLogger("neoforge_compat").error(
                    "set_entity_data: received unknown serializer id {} (vanilla ids end at 33; {} = unregistered serializer instance)",
                    id, id == -1 ? "id -1" : "id >= 34");
        }
    }
}
