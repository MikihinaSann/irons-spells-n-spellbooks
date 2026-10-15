package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.IllusionistPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class IllusionistShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      IllusionistPetEntity illusionist = new IllusionistPetEntity(
         (EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.ILLUSIONIST_PET.get(), worldIn
      );
      illusionist.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      illusionist.setYRot(-player.getYRot());
      worldIn.addFreshEntity(illusionist);
   }
}
