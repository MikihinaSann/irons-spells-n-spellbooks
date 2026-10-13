package top.theillusivec4.curios.api.type.capability;

import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

@FunctionalInterface
public interface ICuriosItemHandlerProvider {
    Optional<ICuriosItemHandler> getCuriosInventory(LivingEntity entity);
}
