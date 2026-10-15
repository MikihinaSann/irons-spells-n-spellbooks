package en.predator.horrorelementmod.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import en.predator.horrorelementmod.procedures.DayresetfalseProcedure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class DoDayResetCommand {
   @SubscribeEvent
   public static void registerCommand(RegisterCommandsEvent event) {
      event.getDispatcher()
         .register((LiteralArgumentBuilder)Commands.literal("HMdayreset").then(Commands.argument("false", BoolArgumentType.bool()).executes(arguments -> {
            Level world = ((CommandSourceStack)arguments.getSource()).getLevel();
            double x = ((CommandSourceStack)arguments.getSource()).getPosition().x();
            double y = ((CommandSourceStack)arguments.getSource()).getPosition().y();
            double z = ((CommandSourceStack)arguments.getSource()).getPosition().z();
            Entity entity = ((CommandSourceStack)arguments.getSource()).getEntity();
            if (entity == null && world instanceof ServerLevel _servLevel) {
               entity = FakePlayerFactory.getMinecraft(_servLevel);
            }

            Direction direction = Direction.DOWN;
            if (entity != null) {
               direction = entity.getDirection();
            }

            DayresetfalseProcedure.execute(world);
            return 0;
         })));
   }
}
