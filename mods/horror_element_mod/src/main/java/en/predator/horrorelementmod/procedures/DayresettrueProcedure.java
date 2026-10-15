package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;

public class DayresettrueProcedure {
   public static void execute(LevelAccessor world) {
      HorrorElementModModVariables.MapVariables.get(world).DayReset = true;
      HorrorElementModModVariables.MapVariables.get(world).syncData(world);
      if (!world.isClientSide() && world.getServer() != null) {
         world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Atmosphere blocks reenabled. Day time will be able to be reset"), false);
      }
   }
}
