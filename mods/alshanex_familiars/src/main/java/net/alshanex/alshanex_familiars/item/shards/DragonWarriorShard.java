package net.alshanex.alshanex_familiars.item.shards;

import net.alshanex.alshanex_familiars.entity.DragonWarriorPetEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractMeleeSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractRuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DragonWarriorShard extends AbstractRuneItem {
   protected void spawnFamiliar(Level worldIn, Player player, BlockPos pos) {
      DragonWarriorPetEntity dragoon = new DragonWarriorPetEntity(
         (EntityType<? extends AbstractMeleeSpellCastingPet>)EntityRegistry.DRAGON_WARRIOR_PET.get(), worldIn
      );
      dragoon.setIsImpostor(false);
      dragoon.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      dragoon.setYRot(-player.getYRot());
      worldIn.addFreshEntity(dragoon);
   }
}
