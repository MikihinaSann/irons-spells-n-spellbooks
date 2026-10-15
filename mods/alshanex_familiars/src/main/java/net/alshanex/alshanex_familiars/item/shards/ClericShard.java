package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.ClericPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClericShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      ClericPetEntity cleric = new ClericPetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.CLERIC_PET.get(), worldIn);
      cleric.setIsImpostor(false);
      cleric.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      cleric.setYRot(-player.getYRot());
      worldIn.addFreshEntity(cleric);
   }
}
