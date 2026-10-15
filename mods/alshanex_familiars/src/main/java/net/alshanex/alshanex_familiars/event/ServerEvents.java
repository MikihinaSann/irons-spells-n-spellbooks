package net.alshanex.alshanex_familiars.event;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.List;
import java.util.Random;
import net.alshanex.alshanex_familiars.entity.BardPetEntity;
import net.alshanex.alshanex_familiars.entity.DragonWarriorPetEntity;
import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.alshanex.alshanex_familiars.entity.ScorcherPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.alshanex.familiarslib.util.HelperMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber(
   modid = "alshanex_familiars"
)
public class ServerEvents {
   @SubscribeEvent
   public static void onSpellCasted(SpellOnCastEvent event) {
      if (event.getEntity() instanceof ServerPlayer player
         && !player.level.isClientSide
         && player.getMainHandItem().is((Item)ItemRegistry.BARD_HARP.get())
         && event.getSchoolType() == FSchoolRegistry.SOUND.get()) {
         int radiusSqr = 16;
         player.level
            .getEntitiesOfClass(
               LivingEntity.class,
               new AABB(player.position().subtract(4.0, 4.0, 4.0), player.position().add(4.0, 4.0, 4.0)),
               livingEntity -> livingEntity instanceof AbstractSpellCastingPet pet
                  && pet.getSummoner() == null
                  && HelperMethods.horizontalDistanceSqr(livingEntity, player.position()) < radiusSqr
            )
            .forEach(
               livingEntity -> {
                  BardPetEntity bard = new BardPetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.BARD_PET.get(), player.level);
                  bard.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                  bard.setYRot(livingEntity.getYRot());
                  player.level.addFreshEntity(bard);
                  MagicManager.spawnParticles(
                     player.level,
                     new BlastwaveParticleOptions(((SchoolType)FSchoolRegistry.SOUND.get()).getTargetingColor(), 2.0F),
                     livingEntity.getX(),
                     livingEntity.getY() + 0.165F,
                     livingEntity.getZ(),
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true
                  );
                  livingEntity.remove(RemovalReason.DISCARDED);
               }
            );
      }
   }

   @SubscribeEvent
   public static void onDamageTaken(Pre event) {
      if (event.getEntity() instanceof ScorcherPetEntity
         && event.getSource().getEntity() != null
         && event.getSource().getEntity() instanceof LivingEntity entity) {
         entity.setRemainingFireTicks(100);
      }

      if (event.getEntity() instanceof PlaguePetEntity pet
         && event.getSource().getEntity() != null
         && event.getSource().getEntity() instanceof LivingEntity entity) {
         Vec3 vec3 = entity.getDeltaMovement();
         double d0 = entity.getX() + vec3.x - pet.getX();
         double d1 = entity.getEyeY() - 1.1F - pet.getY();
         double d2 = entity.getZ() + vec3.z - pet.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         Holder<Potion> holder = Potions.STRONG_POISON;
         ThrownPotion thrownpotion = new ThrownPotion(pet.level(), pet);
         thrownpotion.setItem(PotionContents.createItemStack(Items.LINGERING_POTION, holder));
         thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
         thrownpotion.shoot(d0, d1 + d3 * 0.2, d2, 0.75F, 8.0F);
         pet.triggerAnim("block_controller", "block");
         pet.level().addFreshEntity(thrownpotion);
      }

      if (event.getEntity() instanceof AbstractSpellCastingPet pet) {
         if (pet.getEnragedStacks() > 0) {
            Random random = new Random();
            if (random.nextInt(100) < 10) {
               pet.setEnragedStacks(pet.getEnragedStacks() - 1);
               pet.addEffect(new MobEffectInstance(MobEffectRegistry.HEARTSTOP, 100, 0, false, false, true));
               pet.triggerAnim("interact_controller", "interact");
               MagicManager.spawnParticles(
                  pet.level(),
                  new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.BLOOD.get()).getTargetingColor(), 2.0F),
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
            }
         }

         if (pet.getIsBlocking()) {
            Random random = new Random();
            if (random.nextInt(100) <= 10) {
               event.setNewDamage(0.0F);
               pet.triggerAnim("block_controller", "block");
               MagicManager.spawnParticles(
                  pet.level(),
                  new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.EVOCATION.get()).getTargetingColor(), 2.0F),
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
               pet.level().playSound(null, pet.getX(), pet.getY(), pet.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEnderTeleport(EnderEntity event) {
      if (event.getEntity() instanceof EnderMan enderman) {
         Level var7 = enderman.level();
         BlockPos enderPos = enderman.blockPosition();
         double range = 10.0;
         List<DragonWarriorPetEntity> nearbyEntities = var7.getEntitiesOfClass(
            DragonWarriorPetEntity.class, AABB.ofSize(enderPos.getCenter(), range * 2.0, range * 2.0, range * 2.0)
         );
         if (!nearbyEntities.isEmpty()) {
            event.setCanceled(true);
         }
      }
   }
}
