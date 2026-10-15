package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.DruidPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DruidShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      DruidPetEntity druid = new DruidPetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.DRUID_PET.get(), worldIn);
      druid.setIsImpostor(false);
      druid.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      druid.setYRot(-player.getYRot());
      worldIn.addFreshEntity(druid);
   }
}
