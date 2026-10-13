package io.redspace.ironslib.internal.registry;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.soulfire.SoulFireHelper;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class CommandHandler {
   @SubscribeEvent
   public static void onCommandsRegister(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
      CommandBuildContext commandBuildContext = event.getBuildContext();
      register(commandDispatcher);
   }

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("irons_lib")
                  .requires(p_138819_ -> p_138819_.hasPermission(2)))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("statue")
                           .then(
                              Commands.literal("generate")
                                 .then(
                                    Commands.argument("textures/entity/player/wide/steve.png", ResourceLocationArgument.id())
                                       .executes(
                                          context -> {
                                             try {
                                                ResourceLocation resource = ResourceLocationArgument.getId(context, "textures/entity/player/wide/steve.png");
                                                if (!resource.getPath().endsWith(".png")) {
                                                   resource = resource.withSuffix(".png");
                                                }

                                                NativeImage image = NativeImage.read(
                                                   ((Resource)Minecraft.getInstance().getResourceManager().getResource(resource).get()).open()
                                                );
                                                String[] split = resource.getPath().split("/");
                                                File file = StatueTextureManager.export(split[split.length - 1], StatueTextureManager.transformTexture(image));
                                                if (file == null) {
                                                   ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("failure"));
                                                   return 0;
                                                } else {
                                                   ((CommandSourceStack)context.getSource())
                                                      .sendSuccess(
                                                         () -> Component.literal("success")
                                                               .withStyle(
                                                                  Style.EMPTY
                                                                     .withUnderlined(true)
                                                                     .withClickEvent(new ClickEvent(Action.OPEN_FILE, file.getAbsolutePath()))
                                                               ),
                                                         true
                                                      );
                                                   return 1;
                                                }
                                             } catch (Exception var5) {
                                                ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("failure: " + var5.getMessage()));
                                                return 0;
                                             }
                                          }
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("set_player")
                              .then(
                                 Commands.argument("pos", BlockPosArgument.blockPos())
                                    .then(Commands.argument("username", StringArgumentType.string()).executes(context -> {
                                       String username = StringArgumentType.getString(context, "username");
                                       Optional<GameProfile> optionalProfile = getProfileByUsername(
                                          ((CommandSourceStack)context.getSource()).getServer(), username
                                       );
                                       if (optionalProfile.isPresent()) {
                                          GameProfile profile = optionalProfile.get();
                                          BlockPos blockpos = BlockPosArgument.getBlockPos(context, "pos");
                                          if (((CommandSourceStack)context.getSource()).getLevel().getBlockEntity(blockpos) instanceof PlayerStatueBlockEntity statue
                                             )
                                           {
                                             statue.setPlayerUuid(profile.getId());
                                             statue.setChanged();
                                             return 1;
                                          }
                                       }

                                       return 0;
                                    }))
                              )
                        ))
                     .then(
                        Commands.literal("give_all")
                           .executes(
                              context -> {
                                 List<ProfileResult> wizards = PatreonData.getInstance()
                                    .getProfileValues()
                                    .stream()
                                    .filter(profilex -> profilex.permissions().compareTo(PatreonPermissions.Wizard) >= 0)
                                    .toList();

                                 for (ProfileResult profile : wizards) {
                                    ItemStack stack = ((Item)IronsLibRegistries.ItemRegistry.PLAYER_STATUE_BLOCK_ITEM.get()).getDefaultInstance();
                                    StatueData.set(stack, new StatueData(profile.uuid(), PlayerStatuePose.defaultPose(), false));
                                    ((CommandSourceStack)context.getSource()).getPlayer().getInventory().add(stack);
                                 }

                                 return wizards.size();
                              }
                           )
                     )
               ))
            .then(
               Commands.literal("soulfire")
                  .then(
                     Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes(context -> {
                           Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                           int ticks = IntegerArgumentType.getInteger(context, "seconds") * 20;

                           for (Entity target : targets) {
                              SoulFireHelper.igniteSoul(target, ticks);
                           }

                           int count = targets.size();
                           ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Applied soul fire to " + count + " entities"), true);
                           return count;
                        }))
                  )
            )
      );
   }

   public static Optional<GameProfile> getProfileByUsername(MinecraftServer server, String username) {
      final AtomicReference<GameProfile> profileRef = new AtomicReference<>();
      final AtomicBoolean success = new AtomicBoolean(false);
      server.getProfileRepository().findProfilesByNames(new String[]{username}, new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile profile) {
            profileRef.set(profile);
            success.set(true);
         }

         public void onProfileLookupFailed(String profileName, Exception exception) {
            success.set(false);
         }
      });
      return success.get() ? Optional.ofNullable(profileRef.get()) : Optional.empty();
   }
}
