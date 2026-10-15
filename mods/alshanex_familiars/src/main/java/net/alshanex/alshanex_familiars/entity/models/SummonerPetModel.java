package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.SummonerPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SummonerPetModel extends DefaultedEntityGeoModel<SummonerPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/summoner_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/summoner_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public SummonerPetModel() {
      super(new ResourceLocation("alshanex_familiars", "summoner_pet"));
   }

   public ResourceLocation getModelResource(SummonerPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(SummonerPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(SummonerPetEntity entity, long instanceId, AnimationState<SummonerPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
