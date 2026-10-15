package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.ArchmagePetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractFlyingSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ArchmageShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      ArchmagePetEntity archmage = new ArchmagePetEntity((EntityType<? extends AbstractFlyingSpellCastingPet>)EntityRegistry.ARCHMAGE_PET.get(), worldIn);
      archmage.setIsImpostor(false);
      archmage.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      archmage.setYRot(-player.getYRot());
      worldIn.addFreshEntity(archmage);
   }
}
