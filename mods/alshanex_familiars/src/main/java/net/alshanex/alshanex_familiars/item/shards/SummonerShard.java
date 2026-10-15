package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.SummonerPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractFlyingSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SummonerShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      SummonerPetEntity summoner = new SummonerPetEntity((EntityType<? extends AbstractFlyingSpellCastingPet>)EntityRegistry.SUMMONER_PET.get(), worldIn);
      summoner.setIsImpostor(false);
      summoner.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      summoner.setYRot(-player.getYRot());
      worldIn.addFreshEntity(summoner);
   }
}
