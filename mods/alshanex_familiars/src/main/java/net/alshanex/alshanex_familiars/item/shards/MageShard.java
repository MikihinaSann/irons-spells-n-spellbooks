package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.MagePetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractFlyingSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MageShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      MagePetEntity mage = new MagePetEntity((EntityType<? extends AbstractFlyingSpellCastingPet>)EntityRegistry.MAGE_PET.get(), worldIn);
      mage.setIsImpostor(false);
      mage.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      mage.setYRot(-player.getYRot());
      worldIn.addFreshEntity(mage);
   }
}
