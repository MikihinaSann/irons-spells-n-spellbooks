package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import net.minecraft.world.level.LevelAccessor;

public class CursedPaintingScreamerBlockDestroyedByPlayerProcedure {
   public static void execute(LevelAccessor world) {
      HorrorElementModModVariables.MapVariables.get(world).SCREAMER = false;
      HorrorElementModModVariables.MapVariables.get(world).syncData(world);
   }
}
