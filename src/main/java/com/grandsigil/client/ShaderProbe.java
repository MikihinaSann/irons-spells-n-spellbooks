package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.ArenaEntity;
import com.grandsigil.entity.ArsenalEntity;
import com.grandsigil.entity.BloomEntity;
import com.grandsigil.entity.BoltEntity;
import com.grandsigil.entity.CleaveEntity;
import com.grandsigil.entity.CoffinEntity;
import com.grandsigil.entity.GateEntity;
import com.grandsigil.entity.GazeEntity;
import com.grandsigil.entity.GripEntity;
import com.grandsigil.entity.IceAgeEntity;
import com.grandsigil.entity.PhoenixEntity;
import com.grandsigil.entity.ReckoningEntity;
import com.grandsigil.entity.SunEntity;
import com.grandsigil.entity.ThreadsEntity;
import com.grandsigil.entity.ThunderDragonEntity;
import com.grandsigil.entity.TreeEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
   modid = "grandsigil",
   value = {Dist.CLIENT}
)
public final class ShaderProbe {
   public static final boolean ENABLED = Boolean.getBoolean("grandsigil.probe");
   private static final boolean RECORD = Boolean.getBoolean("grandsigil.probe.record");
   private static int frame;
   private static final int SETTLE = 120;
   private static final int AFTER = 12;
   private static final int BETWEEN = 30;
   private static final List<ShaderProbe.Subject> SUBJECTS = List.of(
      new ShaderProbe.Subject("heaven_gate", at(90), 30.0, 24.0, (l, at, p) -> l.addFreshEntity(new GateEntity(l, at, p, 30.0F, 28.0F))),
      new ShaderProbe.Subject("reckoning", at(45), 30.0, 18.0, (l, at, p) -> {
         ReckoningEntity star = new ReckoningEntity(l, p, 30.0F, 14.0F, 100);
         l.addFreshEntity(star);
         star.release(at);
      }),
      new ShaderProbe.Subject("black_coffin", at(75), 26.0, 8.0, (l, at, p) -> l.addFreshEntity(new CoffinEntity(l, at, p, 30.0F, 5.5F))),
      new ShaderProbe.Subject("sunfall", at(120), 50.0, 60.0, (l, at, p) -> l.addFreshEntity(new SunEntity(l, at, p, 30.0F, 24.0F, l.getSunAngle(0.0F)))),
      new ShaderProbe.Subject("the_gaze", at(90), 60.0, 100.0, (l, at, p) -> l.addFreshEntity(new GazeEntity(l, at, p, 30.0F, 30.0F))),
      new ShaderProbe.Subject("skybreak", at(90), 40.0, 30.0, (l, at, p) -> l.addFreshEntity(new BoltEntity(l, at, p, 30.0F, 24.0F))),
      new ShaderProbe.Subject("colosseum", at(90), 40.0, 14.0, (l, at, p) -> l.addFreshEntity(new ArenaEntity(l, at, p, 30.0F, 17.0F))),
      new ShaderProbe.Subject("world_tree", at(110), 40.0, 20.0, (l, at, p) -> l.addFreshEntity(new TreeEntity(l, at, p, 30.0F, 20.0F))),
      new ShaderProbe.Subject("voidgrip", at(60), 40.0, 24.0, (l, at, p) -> l.addFreshEntity(new GripEntity(l, at, p, 30.0F, 18.0F))),
      new ShaderProbe.Subject(
         "thousand_blades",
         at(20, 70, 120, 150, 172, 186, 196, 230),
         40.0,
         22.0,
         (l, at, p) -> {
            ArmorStand stand = new ArmorStand(l, at.x, at.y, at.z);
            stand.setYRot(180.0F);
            stand.setYBodyRot(180.0F);
            stand.setYHeadRot(180.0F);
            stand.addTag("grandsigil_probe");
            l.addFreshEntity(stand);
            double[][] spots = new double[][]{
               {-10.0, -10.0}, {10.0, -12.0}, {-16.0, -18.0}, {14.0, -20.0}, {-6.0, -24.0}, {8.0, -26.0}, {-22.0, -6.0}, {22.0, -8.0}
            };

            for (double[] spot : spots) {
               Husk husk = EntityType.HUSK.create(l);
               if (husk != null) {
                  double x = at.x + spot[0];
                  double z = at.z + spot[1];
                  int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                  husk.moveTo(x, (double)y, z, 0.0F, 0.0F);
                  husk.setNoAi(true);
                  husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 4, false, false));
                  husk.addTag("grandsigil_probe");
                  l.addFreshEntity(husk);
               }
            }

            l.addFreshEntity(new ArsenalEntity(l, stand, 30.0F, 180.0F));
         }
      ),
      new ShaderProbe.Subject("phoenix", at(14, 44, 100, 170, 214, 230, 238, 256, 270, 310), 68.0, 30.0, (l, at, p) -> {
         ArmorStand stand = new ArmorStand(l, at.x + 10.0, at.y, at.z - 48.0);
         stand.setYRot(0.0F);
         stand.addTag("grandsigil_probe");
         int sy = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(at.x + 10.0), Mth.floor(at.z - 48.0));
         stand.moveTo(at.x + 10.0, (double)sy, at.z - 48.0, 0.0F, 0.0F);
         l.addFreshEntity(stand);
         double[][] spots = new double[][]{{0.0, 0.0}, {4.0, 5.0}, {-20.0, 0.0}, {20.0, 3.0}, {0.0, 20.0}, {-34.0, 0.0}, {34.0, 2.0}, {0.0, 34.0}};

         for (double[] spot : spots) {
            Husk husk = EntityType.HUSK.create(l);
            if (husk != null) {
               double x = at.x + spot[0];
               double z = at.z + spot[1];
               int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
               husk.moveTo(x, (double)y, z, 180.0F, 0.0F);
               husk.setNoAi(true);
               husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 4, false, false));
               husk.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0, false, false));
               husk.addTag("grandsigil_probe");
               l.addFreshEntity(husk);
            }
         }

         l.addFreshEntity(new PhoenixEntity(l, stand, at, 30.0F));
      }),
      new ShaderProbe.Subject(
         "thunder_dragon",
         at(40, 120, 200, 260, 290, 305, 314, 318, 330, 365),
         135.0,
         48.0,
         (l, at, p) -> {
            int sy = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(at.x), Mth.floor(at.z - 4.0));
            ArmorStand stand = new ArmorStand(l, at.x, (double)sy, at.z - 4.0);
            stand.setYRot(0.0F);
            stand.moveTo(at.x, (double)sy, at.z - 4.0, 0.0F, 0.0F);
            stand.addTag("grandsigil_probe");
            l.addFreshEntity(stand);
            double[][] spots = new double[][]{
               {0.0, 0.0}, {12.0, 6.0}, {-14.0, 4.0}, {6.0, -12.0}, {-8.0, 14.0}, {20.0, -4.0}, {-20.0, -8.0}, {2.0, 22.0}, {16.0, 16.0}, {-16.0, 18.0}
            };

            for (double[] spot : spots) {
               Husk husk = EntityType.HUSK.create(l);
               if (husk != null) {
                  double x = at.x + spot[0];
                  double z = at.z + spot[1];
                  int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                  husk.moveTo(x, (double)y, z, 180.0F, 0.0F);
                  husk.setNoAi(true);
                  husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 4, false, false));
                  husk.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 900, 0, false, false));
                  husk.addTag("grandsigil_probe");
                  l.addFreshEntity(husk);
               }
            }

            l.addFreshEntity(new ThunderDragonEntity(l, stand, 30.0F));
         }
      ),
      new ShaderProbe.Subject(
         "full_bloom",
         at(20, 45, 75, 100, 150, 220, 280, 300, 320),
         85.0,
         24.0,
         (l, at, p) -> {
            int sy = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(at.x), Mth.floor(at.z - 50.0));
            ArmorStand stand = new ArmorStand(l, at.x, (double)sy, at.z - 50.0);
            stand.moveTo(at.x, (double)sy, at.z - 50.0, 0.0F, 0.0F);
            stand.addTag("grandsigil_probe");
            l.addFreshEntity(stand);
            double[][] spots = new double[][]{
               {0.0, 8.0}, {12.0, 4.0}, {-14.0, 6.0}, {6.0, -14.0}, {-8.0, 18.0}, {24.0, -6.0}, {-26.0, -10.0}, {4.0, 30.0}, {30.0, 22.0}, {-30.0, 24.0}
            };

            for (double[] spot : spots) {
               Husk husk = EntityType.HUSK.create(l);
               if (husk != null) {
                  double x = at.x + spot[0];
                  double z = at.z + spot[1];
                  int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                  husk.moveTo(x, (double)y, z, 180.0F, 0.0F);
                  husk.setNoAi(true);
                  husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 4, false, false));
                  husk.addTag("grandsigil_probe");
                  l.addFreshEntity(husk);
               }
            }

            l.addFreshEntity(new BloomEntity(l, stand, at, 30.0F));
         }
      ),
      new ShaderProbe.Subject(
         "ice_age",
         at(15, 35, 60, 100, 150, 200, 232, 247, 252, 262, 285),
         125.0,
         24.0,
         (l, at, p) -> {
            int sy = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(at.x), Mth.floor(at.z - 50.0));
            ArmorStand stand = new ArmorStand(l, at.x, (double)sy, at.z - 50.0);
            stand.moveTo(at.x, (double)sy, at.z - 50.0, 0.0F, 0.0F);
            stand.addTag("grandsigil_probe");
            l.addFreshEntity(stand);
            double[][] spots = new double[][]{
               {0.0, 8.0}, {12.0, 4.0}, {-14.0, 6.0}, {6.0, -14.0}, {-8.0, 18.0}, {24.0, -6.0}, {-26.0, -10.0}, {4.0, 30.0}, {30.0, 22.0}, {-30.0, 24.0}
            };

            for (double[] spot : spots) {
               Husk husk = EntityType.HUSK.create(l);
               if (husk != null) {
                  double x = at.x + spot[0];
                  double z = at.z + spot[1];
                  int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                  husk.moveTo(x, (double)y, z, 180.0F, 0.0F);
                  husk.setNoAi(true);
                  husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 4, false, false));
                  husk.addTag("grandsigil_probe");
                  l.addFreshEntity(husk);
               }
            }

            l.addFreshEntity(new IceAgeEntity(l, stand, at, 30.0F));
         }
      ),
      new ShaderProbe.Subject(
         "crimson_threads",
         at(15, 35, 50, 80, 130, 190, 206, 212, 225, 232, 245, 262),
         82.0,
         20.0,
         (l, at, p) -> {
            int sy = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(at.x), Mth.floor(at.z));
            ArmorStand stand = new ArmorStand(l, at.x, (double)sy, at.z);
            stand.moveTo(at.x, (double)sy, at.z, 0.0F, 0.0F);
            stand.addTag("grandsigil_probe");
            l.addFreshEntity(stand);
            double[][] spots = new double[][]{
               {0.0, 12.0},
               {12.0, 6.0},
               {-14.0, 8.0},
               {8.0, -16.0},
               {-10.0, 20.0},
               {24.0, -6.0},
               {-26.0, -10.0},
               {4.0, 30.0},
               {30.0, 18.0},
               {-28.0, 22.0},
               {18.0, 26.0},
               {-18.0, -22.0},
               {32.0, 4.0},
               {-34.0, 0.0}
            };

            for (double[] spot : spots) {
               Husk husk = EntityType.HUSK.create(l);
               if (husk != null) {
                  double x = at.x + spot[0];
                  double z = at.z + spot[1];
                  int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                  husk.moveTo(x, (double)y, z, 180.0F, 0.0F);
                  husk.setNoAi(true);
                  husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 4, false, false));
                  husk.addTag("grandsigil_probe");
                  l.addFreshEntity(husk);
               }
            }

            l.addFreshEntity(new ThreadsEntity(l, stand, 30.0F));
         }
      ),
      new ShaderProbe.Subject(
         "dimension_cut",
         at(10, 18, 21, 22, 24, 30, 45, 70, 85, 90, 92, 96, 110),
         66.0,
         12.0,
         (l, at, p) -> {
            int sy = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(at.x), Mth.floor(at.z - 25.0));
            ArmorStand stand = new ArmorStand(l, at.x, (double)sy, at.z - 25.0);
            stand.moveTo(at.x, (double)sy, at.z - 25.0, 0.0F, 0.0F);
            stand.addTag("grandsigil_probe");
            l.addFreshEntity(stand);
            double[][] spots = new double[][]{
               {0.0, 0.0},
               {8.0, 2.0},
               {-9.0, -2.0},
               {16.0, -1.0},
               {-18.0, 1.0},
               {26.0, 3.0},
               {-27.0, -3.0},
               {34.0, 0.0},
               {-36.0, 2.0},
               {4.0, 12.0},
               {-6.0, -13.0},
               {20.0, 14.0},
               {-22.0, -12.0}
            };

            for (double[] spot : spots) {
               Husk husk = EntityType.HUSK.create(l);
               if (husk != null) {
                  double x = at.x + spot[0];
                  double z = at.z + spot[1];
                  int y = l.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
                  husk.moveTo(x, (double)y, z, 180.0F, 0.0F);
                  husk.setNoAi(true);
                  husk.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 900, 4, false, false));
                  husk.addTag("grandsigil_probe");
                  l.addFreshEntity(husk);
               }
            }

            l.addFreshEntity(new CleaveEntity(l, stand, at, 0.0F, 30.0F));
         }
      )
   );
   private static final Map<String, Double> LIFT = Map.of(
      "thunder_dragon", 20.0, "full_bloom", 10.0, "ice_age", 18.0, "crimson_threads", 12.0, "dimension_cut", 6.0
   );
   private static final String PROP = "grandsigil_probe";
   private static final List<ShaderProbe.Subject> RUN = only();
   private static int clock = -1;
   private static boolean quieted;
   private static int shot;
   private static int spawnedAt;
   private static int index;
   private static int phase;
   private static int phaseAt;
   private static Vec3 ground;

   private static int[] at(int... ticks) {
      return ticks;
   }

   private static List<ShaderProbe.Subject> only() {
      String want = System.getProperty("grandsigil.probe.only", "");
      if (want.isBlank()) {
         return SUBJECTS;
      } else {
         List<String> names = List.of(want.split(","));
         return SUBJECTS.stream().filter(sub -> names.contains(sub.name())).toList();
      }
   }

   private ShaderProbe() {
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      if (ENABLED) {
         Minecraft mc = Minecraft.getInstance();
         IntegratedServer server = mc.getSingleplayerServer();
         if (mc.level != null && mc.player != null && server != null) {
            mc.options.pauseOnLostFocus = false;
            mc.options.getSoundSourceOptionInstance(SoundSource.MASTER).set(0.0);
            mc.options.framerateLimit().set(RECORD ? 60 : 30);
            mc.options.enableVsync().set(false);
            if (!quieted) {
               quieted = true;
               GrandSigil.LOGGER
                  .info(
                     "[probe] window visible={} focused={}",
                     GLFW.glfwGetWindowAttrib(mc.getWindow().getWindow(), 131076),
                     GLFW.glfwGetWindowAttrib(mc.getWindow().getWindow(), 131073)
                  );
            }

            if (mc.getWindow().isFullscreen()) {
               mc.getWindow().toggleFullScreen();
               mc.options.fullscreen().set(false);
            }

            if (mc.screen != null) {
               mc.setScreen(null);
            }

            clock++;
            if (clock == 120) {
               mc.options.hideGui = true;
               server.execute(() -> setUp(server));
               phase = 0;
               phaseAt = clock + 40;
            } else if (clock >= 120 && clock >= phaseAt) {
               if (index >= RUN.size()) {
                  GrandSigil.LOGGER.info("[probe] done, {} subjects", RUN.size());
                  mc.stop();
               } else {
                  ShaderProbe.Subject subject = RUN.get(index);
                  switch (phase) {
                     case 0:
                        server.execute(() -> spawn(server, subject));
                        phase = 1;
                        shot = 0;
                        spawnedAt = clock;
                        phaseAt = clock + subject.shots()[0];
                        if (RECORD) {
                           frame = 0;
                           phaseAt = clock + 1;
                        }
                        break;
                     case 1:
                        if (RECORD) {
                           String mode = Shaders.packInUse() ? "on" : "off";
                           String file = String.format("rec_%s_%s_%05d.png", mode, subject.name(), frame++);
                           Screenshot.grab(mc.gameDirectory, file, mc.getMainRenderTarget(), message -> {
                           });
                           int[] shots = subject.shots();
                           if (clock - spawnedAt >= shots[shots.length - 1] + 30) {
                              phase = 2;
                              phaseAt = clock + 12;
                           } else {
                              phaseAt = clock + 1;
                           }

                           return;
                        }

                        String mode = Shaders.packInUse() ? "on" : "off";
                        int[] shots = subject.shots();
                        String file = "probe_" + mode + "_" + subject.name() + (shots.length > 1 ? "_" + shots[shot] : "") + ".png";
                        Screenshot.grab(mc.gameDirectory, file, mc.getMainRenderTarget(), message -> {
                        });
                        GrandSigil.LOGGER.info("[probe] {} -> {}", subject.name(), file);
                        shot++;
                        if (shot < shots.length) {
                           phaseAt = spawnedAt + shots[shot];
                        } else {
                           phase = 2;
                           phaseAt = clock + 12;
                        }
                        break;
                     default:
                        server.execute(() -> clear(server));
                        index++;
                        phase = 0;
                        phaseAt = clock + 30;
                  }
               }
            }
         }
      }
   }

   private static void setUp(IntegratedServer server) {
      ServerLevel level = server.overworld();
      ServerPlayer player = server.getPlayerList().getPlayers().get(0);
      player.setGameMode(GameType.CREATIVE);
      player.getAbilities().flying = true;
      player.onUpdateAbilities();
      level.setDayTime(6000L);
      level.setWeatherParameters(12000, 0, false, false);
      server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
      server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
      BlockPos spawn = level.getSharedSpawnPos();
      int x = spawn.getX();
      int z = spawn.getZ() + 40;
      int y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z);
      ground = new Vec3((double)x + 0.5, (double)y, (double)z + 0.5);
      GrandSigil.LOGGER.info("[probe] subjects stand at {}", ground);
   }

   private static void spawn(IntegratedServer server, ShaderProbe.Subject subject) {
      ServerLevel level = server.overworld();
      ServerPlayer player = server.getPlayerList().getPlayers().get(0);
      player.removeAllEffects();
      int highest = (int)ground.y;

      for (int step = 0; step <= (int)subject.back(); step++) {
         highest = Math.max(highest, level.getHeight(Types.MOTION_BLOCKING, Mth.floor(ground.x), Mth.floor(ground.z - (double)step)));
      }

      double feet = (double)highest + 2.0 + LIFT.getOrDefault(subject.name(), 0.0);
      double eyeY = feet + (double)player.getEyeHeight();
      double lookY = ground.y + subject.lookUp();
      float pitch = (float)(-Math.toDegrees(Math.atan2(lookY - eyeY, subject.back())));
      player.teleportTo(level, ground.x, feet, ground.z - subject.back(), 0.0F, Mth.clamp(pitch, -89.0F, 89.0F));
      subject.spawn().spawn(level, ground, player);
   }

   private static void clear(IntegratedServer server) {
      ServerLevel level = server.overworld();
      List<Entity> ours = new ArrayList<>();

      for (Entity entity : level.getAllEntities()) {
         if (entity.getClass().getName().startsWith("com.grandsigil.entity") || entity.getTags().contains("grandsigil_probe")) {
            ours.add(entity);
         }
      }

      ours.forEach(Entity::discard);
      server.getPlayerList().getPlayers().get(0).removeAllEffects();
   }

   @FunctionalInterface
   private interface Spawner {
      void spawn(ServerLevel var1, Vec3 var2, ServerPlayer var3);
   }

   private static record Subject(String name, int[] shots, double back, double lookUp, ShaderProbe.Spawner spawn) {
   }
}
