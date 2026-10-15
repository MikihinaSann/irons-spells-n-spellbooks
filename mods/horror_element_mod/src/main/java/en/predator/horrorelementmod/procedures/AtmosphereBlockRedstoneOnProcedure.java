package en.predator.horrorelementmod.procedures;

import en.predator.horrorelementmod.network.HorrorElementModModVariables;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class AtmosphereBlockRedstoneOnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (HorrorElementModModVariables.MapVariables.get(world).DayReset) {
         if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0), e -> true).isEmpty()
            || !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 5.0, 5.0, 5.0), e -> true).isEmpty()) {
            if (world instanceof ServerLevel _level) {
               _level.getServer()
                  .getCommands()
                  .performPrefixedCommand(
                     new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null)
                        .withSuppressedOutput(),
                     "weather thunder"
                  );
            }

            if (world instanceof ServerLevel _level) {
               _level.setDayTime(18000L);
            }
         }
      } else if (!world.isClientSide() && world.getServer() != null) {
         world.getServer()
            .getPlayerList()
            .broadcastSystemMessage(Component.literal("atmosphere blocks are disabled. Use /HMdayreset true to reenable it"), false);
      }
   }
}
