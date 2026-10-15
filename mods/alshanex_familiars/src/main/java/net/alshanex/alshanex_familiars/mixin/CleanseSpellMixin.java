package net.alshanex.alshanex_familiars.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.spells.holy.CleanseSpell;
import net.alshanex.alshanex_familiars.entity.ClericPetEntity;
import net.alshanex.alshanex_familiars.entity.NecromancerPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CleanseSpell.class})
public abstract class CleanseSpellMixin extends AbstractSpell {
   @Inject(
      method = {"onCast"},
      at = {@At("TAIL")},
      remap = false
   )
   private void alshanex_familiars$cleanseNecromancerPet(
      Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData, CallbackInfo ci
   ) {
      level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(entity.getBoundingBox().getCenter(), 6.0, 6.0, 6.0))
         .forEach(
            livingEntity -> {
               if (livingEntity instanceof NecromancerPetEntity pet && pet.getSummoner() == null) {
                  ClericPetEntity cleric = new ClericPetEntity(
                     (EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.CLERIC_PET.get(), pet.level()
                  );
                  cleric.setPos(pet.getX(), pet.getY(), pet.getZ());
                  cleric.setYRot(pet.getYRot());
                  pet.level().addFreshEntity(cleric);
                  MagicManager.spawnParticles(
                     pet.level(),
                     new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.HOLY.get()).getTargetingColor(), 2.0F),
                     pet.getX(),
                     pet.getY() + 0.165F,
                     pet.getZ(),
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true
                  );
                  pet.remove(RemovalReason.DISCARDED);
               }
            }
         );
   }
}
