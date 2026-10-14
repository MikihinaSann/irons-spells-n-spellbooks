package net.neoforged.neoforge.internal.fabric.mixin;

import java.util.Stack;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * NeoForge's LivingEntity.damageContainers — a stack of {@link DamageContainer}s
 * live for the duration of {@code hurt}. Wrapped with try/finally so the stack
 * stays balanced across canceled hurts, thrown listeners, and nested damage.
 */
@Mixin(value = LivingEntity.class, priority = 900)
public abstract class DamageContainersMixin implements net.neoforged.neoforge.common.extensions.ILivingEntityExtension {
    @Unique
    private final Stack<DamageContainer> neoforge_compat$damageContainers = new Stack<>();

    /**
     * Concrete implementation so {@code self()} resolves to a class method —
     * the interface default alone produced AbstractMethodError at runtime.
     */
    @Override
    public LivingEntity self() {
        return (LivingEntity) (Object) this;
    }

    @Override
    public Stack<DamageContainer> getDamageContainers() {
        return this.neoforge_compat$damageContainers;
    }

    @WrapMethod(method = "hurt")
    private boolean neoforge_compat$hurtContainer(DamageSource source, float amount,
            Operation<Boolean> original) {
        Stack<DamageContainer> stack = this.neoforge_compat$damageContainers;
        stack.push(new DamageContainer(source, (LivingEntity) (Object) this, amount));
        try {
            return original.call(source, amount);
        } finally {
            stack.pop();
        }
    }
}
