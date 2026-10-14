package net.neoforged.neoforge.internal.fabric.mixin;

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.util.AttributeTemplateCurves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's AttributeTemplate curve support: when a curve was registered for the
 * template's id, {@code create} uses it instead of the linear amount*level.
 */
@Mixin(MobEffect.AttributeTemplate.class)
public abstract class MobEffectAttributeTemplateMixin {
    @Shadow
    public abstract net.minecraft.resources.ResourceLocation id();

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private void neoforge_compat$curve(int level, CallbackInfoReturnable<AttributeModifier> cir) {
        Int2DoubleFunction curve = AttributeTemplateCurves.get(this.id());
        if (curve != null) {
            cir.setReturnValue(new AttributeModifier(this.id(), curve.apply(level), ((MobEffect.AttributeTemplate) (Object) this).operation()));
        }
    }
}
