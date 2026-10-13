package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Fires {@link BabyEntitySpawnEvent} on the proposed offspring during
 * {@code Animal#spawnChildFromBreeding}, letting subscribers substitute or
 * mutate the child before it is added to the world.
 */
@Mixin(Animal.class)
public abstract class BreedOutcomeDispatchMixin {

    @ModifyExpressionValue(method = "spawnChildFromBreeding",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/AgeableMob;"))
    private AgeableMob irons_spellbooks$babySpawn(AgeableMob child, ServerLevel level, Animal mate) {
        var event = new BabyEntitySpawnEvent((Animal) (Object) this, mate, child);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return null;
        }
        return event.getChild();
    }
}
