package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;

public class DayresetfalseProcedure {
   public static void execute(LevelAccessor world) {
      HorrorElementModModVariables.MapVariables.get(world).DayReset = false;
      HorrorElementModModVariables.MapVariables.get(world).syncData(world);
      if (!world.isClientSide() && world.getServer() != null) {
         world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Atmosphere blocks disabled. Day time will no longer reset"), false);
      }
   }
}
