package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.FrostlingPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FrostlingShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      FrostlingPetEntity frostling = new FrostlingPetEntity(
         (EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.FROSTLING_PET.get(), worldIn
      );
      frostling.setIsImpostor(false);
      frostling.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      frostling.setYRot(-player.getYRot());
      worldIn.addFreshEntity(frostling);
   }
}
