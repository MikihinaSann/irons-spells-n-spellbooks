package net.alshanex.alshanex_familiars.item;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import net.alshanex.alshanex_familiars.entity.IllusionistPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.advancements.SimpleAdvancementTrigger;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.NotNull;

public class TruthMirror extends Item {
   public TruthMirror() {
      super(new Properties().rarity(Rarity.EPIC).stacksTo(1).durability(3));
   }

   @NotNull
   public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      if (target instanceof AbstractSpellCastingPet pet && !pet.level().isClientSide()) {
         if (pet.getIsImpostor()) {
            if (pet.getSummoner() == null) {
               IllusionistPetEntity illusionist = new IllusionistPetEntity(
                  (EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.ILLUSIONIST_PET.get(), pet.level()
               );
               illusionist.setPos(pet.getX(), pet.getY(), pet.getZ());
               illusionist.setYRot(pet.getYRot());
               pet.level().addFreshEntity(illusionist);
               MagicManager.spawnParticles(pet.level(), ParticleTypes.POOF, pet.getX(), pet.getY(), pet.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
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
               pet.remove(RemovalReason.DISCARDED);
               if (playerIn instanceof ServerPlayer serverPlayer) {
                  this.triggerRevealAdvancement(serverPlayer);
               }
            } else if (pet.getSummoner().is(playerIn)) {
               IllusionistPetEntity illusionist = new IllusionistPetEntity(
                  (EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.ILLUSIONIST_PET.get(), pet.level()
               );
               illusionist.setArmorStacks(pet.getArmorStacks());
               illusionist.setHealthStacks(pet.getHealthStacks());
               illusionist.setEnragedStacks(pet.getEnragedStacks());
               illusionist.setIsBlocking(pet.getIsBlocking());
               illusionist.setOwnerUUID(pet.getOwnerUUID());
               illusionist.setPos(pet.getX(), pet.getY(), pet.getZ());
               illusionist.setYRot(pet.getYRot());
               pet.level().addFreshEntity(illusionist);
               MagicManager.spawnParticles(pet.level(), ParticleTypes.POOF, pet.getX(), pet.getY(), pet.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
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
               pet.remove(RemovalReason.DISCARDED);
               if (playerIn instanceof ServerPlayer serverPlayer) {
                  this.triggerRevealAdvancement(serverPlayer);
               }
            }
         }

         stack.hurtAndBreak(1, playerIn, EquipmentSlot.MAINHAND);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   private void triggerRevealAdvancement(ServerPlayer player) {
      ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.ILLUSIONIST_REVEAL_TRIGGER.get()).trigger(player);
   }
}
