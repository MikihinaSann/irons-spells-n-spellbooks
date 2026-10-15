package net.alshanex.alshanex_familiars.mixin;

import net.alshanex.alshanex_familiars.entity.MagePetEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Creeper.class})
public class ScareCreeperMixin {
   @Inject(
      method = {"registerGoals"},
      at = {@At("TAIL")}
   )
   void registerGoals(CallbackInfo ci) {
      Creeper creeper = (Creeper)(Object)this;
      creeper.goalSelector.addGoal(0, new AvoidEntityGoal(creeper, MagePetEntity.class, 6.0F, 1.0, 1.2));
   }
}
