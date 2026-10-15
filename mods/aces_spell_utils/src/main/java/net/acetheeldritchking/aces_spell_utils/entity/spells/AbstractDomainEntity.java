package net.acetheeldritchking.aces_spell_utils.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class AbstractDomainEntity extends AbstractMagicProjectile {
   private int radius;
   private int refinement;
   private boolean open;
   private ArrayList<AbstractDomainEntity> clashingWith = new ArrayList<>();
   private boolean hasTransported;
   private boolean finishedSpawnAnim;

   public AbstractDomainEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
      this.hasTransported = false;
   }

   public void onActivation() {
      this.level()
         .getEntitiesOfClass(
            AbstractDomainEntity.class,
            new AABB(
               this.position().subtract(this.radius / 2.0, this.radius / 2.0, this.radius / 2.0),
               this.position().add(this.radius / 2.0, this.radius / 2.0, this.radius / 2.0)
            )
         )
         .stream()
         .forEach(e -> {
            if (e.distanceTo(this) < this.radius && !Objects.equals(e, this)) {
               if (e.getRefinement() > this.refinement) {
                  this.destroyDomain();
               } else if (e.getRefinement() < this.refinement) {
                  e.destroyDomain();
               } else {
                  if (!this.clashingWith.contains(e)) {
                     this.clashingWith.add(e);
                  }

                  if (!e.getClashingWith().contains(this)) {
                     e.getClashingWith().add(this);
                  }
               }
            }
         });
   }

   public void destroyDomain() {
      this.discard();
   }

   private boolean canTransport() {
      return !this.open && !this.hasTransported && this.clashingWith.isEmpty() && this.finishedSpawnAnim;
   }

   public void handleTransportation() {
      this.hasTransported = true;
   }

   public void handleDomainClash(AbstractDomainEntity opposingDomain) {
   }

   public void targetSureHit() {
      this.level()
         .getEntitiesOfClass(
            Entity.class,
            new AABB(
               this.position().subtract(this.radius / 2.0, this.radius / 2.0, this.radius / 2.0),
               this.position().add(this.radius / 2.0, this.radius / 2.0, this.radius / 2.0)
            )
         )
         .stream()
         .forEach(e -> {
            if (e.distanceTo(this) < this.radius && this.canTarget(e)) {
               this.handleSureHit(e);
            }
         });
   }

   public void handleSureHit(Entity e) {
   }

   public boolean isClashing() {
      return !this.clashingWith.isEmpty();
   }

   public boolean canTarget(Entity e) {
      boolean shareOwner = false;
      if (e instanceof TamableAnimal tame) {
         shareOwner = Objects.equals(tame.getOwner(), ((TamableAnimal)e).getOwner());
      }

      if (e instanceof Projectile proj) {
         shareOwner = Objects.equals(proj.getOwner(), e);
      }

      return !Objects.equals(e, this) && !Objects.equals(e, this.getOwner()) && !shareOwner;
   }

   public int getRefinement() {
      return this.refinement;
   }

   public void setRefinement(int refinement) {
      this.refinement = refinement;
   }

   public void setRadius(int radius) {
      this.radius = radius;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public ArrayList<AbstractDomainEntity> getClashingWith() {
      return this.clashingWith;
   }

   public void tick() {
      if (this.tickCount == 0) {
         this.onActivation();
      }

      if (this.getOwner() instanceof LivingEntity living && living.isDeadOrDying()) {
         this.destroyDomain();
      }

      if (!this.clashingWith.isEmpty()) {
         for (AbstractDomainEntity e : this.clashingWith) {
            if (e != null) {
               this.handleDomainClash(e);
            } else {
               this.clashingWith.remove(e);
            }
         }
      }

      if (this.canTransport()) {
         this.handleTransportation();
      }

      this.targetSureHit();
      super.tick();
   }

   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 0.0F;
   }

   protected void rotateWithMotion() {
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public void onAntiMagic(MagicData playerMagicData) {
   }

   public int getRadius() {
      return this.radius;
   }

   public boolean isOpen() {
      return this.open;
   }

   public void setClashingWith(ArrayList<AbstractDomainEntity> clashingWith) {
      this.clashingWith = clashingWith;
   }

   public boolean hasTransported() {
      return this.hasTransported;
   }

   public void setHasTransported(boolean hasTransported) {
      this.hasTransported = hasTransported;
   }

   public boolean isFinishedSpawnAnim() {
      return this.finishedSpawnAnim;
   }

   public void setFinishedSpawnAnim(boolean finishedSpawnAnim) {
      this.finishedSpawnAnim = finishedSpawnAnim;
   }
}
