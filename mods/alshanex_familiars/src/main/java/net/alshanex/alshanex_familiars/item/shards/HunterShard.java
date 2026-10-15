package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.HunterPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractTerrestrianSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HunterShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      HunterPetEntity hunter = new HunterPetEntity((EntityType<? extends AbstractTerrestrianSpellCastingPet>)EntityRegistry.HUNTER_PET.get(), worldIn);
      hunter.setIsImpostor(false);
      hunter.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      hunter.setYRot(-player.getYRot());
      worldIn.addFreshEntity(hunter);
   }
}
