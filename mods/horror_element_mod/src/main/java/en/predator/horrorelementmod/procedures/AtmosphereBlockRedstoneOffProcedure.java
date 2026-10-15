package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class AtmosphereBlockRedstoneOffProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (HorrorElementModModVariables.MapVariables.get(world).DayReset) {
         if (world instanceof ServerLevel _level) {
            _level.setDayTime(500L);
         }

         if (world instanceof ServerLevel _level) {
            _level.getServer()
               .getCommands()
               .performPrefixedCommand(
                  new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null)
                     .withSuppressedOutput(),
                  "weather clear"
               );
         }
      }
   }
}
