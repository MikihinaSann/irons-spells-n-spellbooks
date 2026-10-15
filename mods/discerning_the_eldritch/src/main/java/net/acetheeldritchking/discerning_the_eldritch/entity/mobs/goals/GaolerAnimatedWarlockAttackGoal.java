package net.acetheeldritchking.discerning_the_eldritch.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackKeyframe;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerEntity;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

public class GaolerAnimatedWarlockAttackGoal extends GenericAnimatedWarlockAttackGoal<GaolerEntity> {
   final GaolerEntity gaoler;

   public GaolerAnimatedWarlockAttackGoal(GaolerEntity entity, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(entity, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.gaoler = entity;
      this.wantsToMelee = true;
   }

   public void playSwingSound() {
      this.gaoler.playSound((SoundEvent)DTESoundRegistry.GAOLER_ATTACK_IMPACT.get(), 10.0F, this.gaoler.getVoicePitch());
   }

   protected void handleAttackLogic(double distanceSquared) {
      super.handleAttackLogic(distanceSquared);
      if (this.currentAttack != null && this.currentAttack.animationId.equals("slam_1")) {
         if (this.currentAttack.lengthInTicks >= 24 && this.isMeleeing()) {
            ((GaolerEntity)this.mob).getNavigation().stop();
            ((GaolerEntity)this.mob).lerpMotion(0.0, 0.0, 0.0);
         }

         if (this.currentAttack.isHitFrame(this.meleeAnimTimer)) {
            AttackKeyframe attackData = this.currentAttack.getHitFrame(this.meleeAnimTimer);

            for (LivingEntity target : ((GaolerEntity)this.mob)
               .level()
               .getEntitiesOfClass(this.target.getClass(), ((GaolerEntity)this.mob).getBoundingBox().inflate(3.5))) {
               this.handleDamaging(target, attackData);
            }

            this.spawnVisualEarthquake();
         }
      }
   }

   private void spawnVisualEarthquake() {
      EarthquakeAoe aoe = new EarthquakeAoe(((GaolerEntity)this.mob).level());
      aoe.moveTo(this.target.position());
      aoe.setOwner(this.mob);
      aoe.setCircular();
      aoe.setRadius(8.0F);
      aoe.setDuration(10);
      aoe.setDamage(1.0F);
      aoe.setSlownessAmplifier(0);
      ((GaolerEntity)this.mob).level().addFreshEntity(aoe);
   }
}
