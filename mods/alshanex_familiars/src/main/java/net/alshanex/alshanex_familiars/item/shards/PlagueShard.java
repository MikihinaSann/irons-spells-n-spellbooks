package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlagueShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      PlaguePetEntity plague = new PlaguePetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.PLAGUE_PET.get(), worldIn);
      plague.setIsImpostor(false);
      plague.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      plague.setYRot(-player.getYRot());
      worldIn.addFreshEntity(plague);
   }
}
