package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Binds registered {@link AttachmentType}s to their registry id no matter which
 * registration path produced them (DeferredRegister, DeferredHelper/RegisterEvent).
 */
@Mixin(MappedRegistry.class)
public abstract class AttachmentTypeBindMixin {

    @Inject(method = "register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lnet/minecraft/core/RegistrationInfo;)Lnet/minecraft/core/Holder$Reference;",
            at = @At("RETURN"))
    private void neoforge_compat$bindAttachmentType(ResourceKey<?> key, Object value, RegistrationInfo info,
            CallbackInfoReturnable<?> cir) {
        if (value instanceof AttachmentType<?> attachmentType) {
            attachmentType.bindFabric(key.location());
        }
    }
}
