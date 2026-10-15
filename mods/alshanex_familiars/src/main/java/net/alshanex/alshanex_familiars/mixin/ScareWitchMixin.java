package net.alshanex.alshanex_familiars.mixin;

import net.alshanex.alshanex_familiars.entity.SummonerPetEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Witch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Witch.class})
public class ScareWitchMixin {
   @Inject(
      method = {"registerGoals"},
      at = {@At("TAIL")}
   )
   void registerGoals(CallbackInfo ci) {
      Witch witch = (Witch)(Object)this;
      witch.goalSelector.addGoal(0, new AvoidEntityGoal(witch, SummonerPetEntity.class, 10.0F, 1.0, 1.2));
   }
}
