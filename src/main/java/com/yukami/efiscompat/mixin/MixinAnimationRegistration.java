package com.yukami.efiscompat.mixin;

import com.yukami.efiscompat.animation.Animation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.EpicFightFabric;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;

import java.util.List;

/**
 * The EpicFight Fabric port constructs {@link AnimationRegistryEvent} in
 * {@code onInitialize} and hardcodes its own registrations — there is no event
 * bus to subscribe to. Redirecting {@code getBuilders()} lets us register the
 * merged spell animations before the builders are consumed.
 */
@Mixin(value = EpicFightFabric.class, remap = false)
public abstract class MixinAnimationRegistration {
    @Redirect(method = "onInitialize",
            at = @At(value = "INVOKE",
                    target = "Lyesman/epicfight/api/animation/AnimationManager$AnimationRegistryEvent;getBuilders()Ljava/util/List;",
                    remap = false))
    private List<AnimationBuilder> efis$registerSpellAnimations(AnimationRegistryEvent event) {
        Animation.registerAnimations(event);
        return event.getBuilders();
    }
}
