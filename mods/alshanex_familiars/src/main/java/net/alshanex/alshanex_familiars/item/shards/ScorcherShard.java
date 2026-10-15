package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.ScorcherPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ScorcherShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      ScorcherPetEntity scorcher = new ScorcherPetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.SCORCHER_PET.get(), worldIn);
      scorcher.setIsImpostor(false);
      scorcher.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      scorcher.setYRot(-player.getYRot());
      worldIn.addFreshEntity(scorcher);
   }
}
