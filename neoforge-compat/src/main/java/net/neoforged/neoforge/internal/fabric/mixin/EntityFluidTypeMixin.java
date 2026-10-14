package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.entity.IEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ANDs {@link Entity#isPushedByFluid()} with the NeoForge FluidType-typed
 * overload. The {@link IEntityExtension} default delegates back to the no-arg
 * method, so the typed call is only made when the entity class overrides it —
 * otherwise this would recurse.
 */
@Mixin(Entity.class)
public abstract class EntityFluidTypeMixin implements IEntityExtension {

    /**
     * Concrete implementation so {@code self()} resolves to a class method —
     * the interface default alone produced AbstractMethodError at runtime.
     */
    @Override
    public Entity self() {
        return (Entity) (Object) this;
    }

    private static final ClassValue<Boolean> OVERRIDES_PUSHED_BY_FLUID = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                return type.getMethod("isPushedByFluid", FluidType.class).getDeclaringClass() != IEntityExtension.class;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
    };

    @Inject(method = "isPushedByFluid", at = @At("RETURN"), cancellable = true)
    private void irons_spellbooks$typedFluidPush(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && OVERRIDES_PUSHED_BY_FLUID.get(this.getClass())) {
            cir.setReturnValue(((Entity) (Object) this).isPushedByFluid(NeoForgeMod.WATER_TYPE.value()));
        }
    }
}
