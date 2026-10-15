package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.NecromancerPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NecromancerShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      NecromancerPetEntity necromancer = new NecromancerPetEntity(
         (EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.NECROMANCER_PET.get(), worldIn
      );
      necromancer.setIsImpostor(false);
      necromancer.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      necromancer.setYRot(-player.getYRot());
      worldIn.addFreshEntity(necromancer);
   }
}
