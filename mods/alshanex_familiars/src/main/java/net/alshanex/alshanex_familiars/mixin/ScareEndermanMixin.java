package net.alshanex.alshanex_familiars.mixin;

import net.alshanex.alshanex_familiars.entity.ArchmagePetEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnderMan.class})
public class ScareEndermanMixin {
   @Inject(
      method = {"registerGoals"},
      at = {@At("TAIL")}
   )
   void registerGoals(CallbackInfo ci) {
      EnderMan enderman = (EnderMan)(Object)this;
      enderman.goalSelector.addGoal(0, new AvoidEntityGoal(enderman, ArchmagePetEntity.class, 10.0F, 1.0, 1.2));
   }
}
