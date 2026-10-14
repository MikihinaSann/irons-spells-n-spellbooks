package net.neoforged.neoforge.internal.fabric.mixin;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.extensions.IMobEffectInstanceExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge's MobEffectInstance patch: a {@code cures} set populated through
 * {@code MobEffect#fillEffectCures} when the instance is constructed.
 */
@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceCuresMixin implements IMobEffectInstanceExtension {
    @Shadow
    private Holder<MobEffect> effect;

    @Unique
    private final Set<EffectCure> neoforge_compat$cures = Sets.newIdentityHashSet();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neoforge_compat$fillCures(CallbackInfo ci) {
        this.effect.value().fillEffectCures(this.neoforge_compat$cures, (MobEffectInstance) (Object) this);
    }

    @Override
    public Set<EffectCure> getCures() {
        return this.neoforge_compat$cures;
    }
}
