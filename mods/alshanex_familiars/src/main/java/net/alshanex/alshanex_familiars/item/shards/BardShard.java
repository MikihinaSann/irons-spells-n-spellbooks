package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.BardPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BardShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      BardPetEntity bard = new BardPetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.BARD_PET.get(), worldIn);
      bard.setIsImpostor(false);
      bard.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      bard.setYRot(-player.getYRot());
      worldIn.addFreshEntity(bard);
   }
}
