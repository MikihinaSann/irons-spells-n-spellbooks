package io.redspace.ironslib.statue.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.blaze3d.platform.NativeImage;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.statue.PlayerStatueModelType;
import io.redspace.ironslib.util.Color;
import io.redspace.ironslib.util.MeanShiftCluster;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

public class StatueTextureManager {
   public static final UUID TEST_UUID = uuidFromUndashed("c3adad79e88a4f15bd61c58766d725e9");
   public static final UUID TEST_UUID2 = uuidFromUndashed("afb939b1f2684ebcb1f1261fad41bc33");
   public static final UUID TEST_UUID3 = uuidFromUndashed("93b459bece4f4700b457c1aa91b3b687");
   private static final ConcurrentHashMap<UUID, StatueTextureHolder> TEXTURES = new ConcurrentHashMap<>();
   private static final ResourceLocation NULL_TEXTURE = IronsLib.id("steve");
   public static final StatueTextureHolder NULL = new StatueTextureHolder(PatreonPermissions.None, NULL_TEXTURE, PlayerStatueModelType.WIDE);

   public static void _debugClear() {
      TEXTURES.clear();
   }

   public static void refresh() {
      synchronized (TEXTURES) {
         TEXTURES.keys().asIterator().forEachRemaining(StatueTextureManager::createTextureAsync);
      }
   }

   public static UUID uuidFromUndashed(String s) {
      if (s != null && s.length() == 32) {
         String dashed = s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5");
         return UUID.fromString(dashed);
      } else {
         throw new IllegalArgumentException("Invalid UUID string");
      }
   }

   @Nullable
   public static File export(String name, NativeImage image) {
      try {
         if (!name.endsWith(".png")) {
            name = name + ".png";
         }

         Path path = Path.of("screenshots/irons_lib").resolve(name);
         if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
         }

         File file = path.toFile();
         image.writeToFile(file);
         return file;
      } catch (Exception var4) {
         IronsLib.LOGGER.debug(var4.getMessage());
         return null;
      }
   }

   public static ResourceLocation resourceLocationFromUuid(UUID uuid) {
      return IronsLib.id(uuid.toString());
   }

   public static StatueTextureHolder lookupUUID(@NotNull UUID playerUuid) {
      if (TEXTURES.containsKey(playerUuid)) {
         return TEXTURES.get(playerUuid);
      } else {
         TEXTURES.put(playerUuid, NULL);
         createTextureAsync(playerUuid);
         return NULL;
      }
   }

   private static void createTextureAsync(UUID playerUuid) {
      CompletableFuture.runAsync(() -> createTexture(playerUuid), Util.backgroundExecutor());
   }

   private static void createTexture(UUID playerUuid) {
      IronsLib.LOGGER.debug("processing texture for {}", playerUuid);
      MinecraftSessionService sessionService = Minecraft.getInstance().getMinecraftSessionService();
      ProfileResult profileResult = sessionService.fetchProfile(playerUuid, true);
      if (profileResult != null) {
         GameProfile gameProfile = profileResult.profile();
         MinecraftProfileTextures playerTextures = sessionService.getTextures(gameProfile);
         if (playerTextures.skin() != null) {
            PatreonPermissions permissions = PatreonData.getPatreonPermissions(playerUuid);
            if (permissions.supportsStatues()) {
               NativeImage skinTexture = downloadSkin(gameProfile, playerTextures.skin().getUrl());
               Model modelType = Model.byName(playerTextures.skin().getMetadata("model"));
               skinTexture = transformTexture(skinTexture);
               ResourceLocation textureId = resourceLocationFromUuid(playerUuid);
               Minecraft.getInstance().getTextureManager().register(textureId, new DynamicTexture(skinTexture));
               PlayerStatueModelType statueModelType;
               if (skinTexture.getHeight() != skinTexture.getWidth()) {
                  statueModelType = PlayerStatueModelType.LEGACY;
               } else {
                  statueModelType = modelType == Model.SLIM ? PlayerStatueModelType.SLIM : PlayerStatueModelType.WIDE;
               }

               TEXTURES.put(playerUuid, new StatueTextureHolder(permissions, textureId, statueModelType));
            }
         }
      }
   }

   public static NativeImage steve() {
      try {
         return NativeImage.read(
            ((Resource)Minecraft.getInstance()
                  .getResourceManager()
                  .getResource(ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png"))
                  .get())
               .open()
         );
      } catch (Exception var1) {
         throw new RuntimeException();
      }
   }

   private static NativeImage normalizeValues(NativeImage texture, float fMin, float fMax) {
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      NativeImage textureOut = texture.mappedCopy(i -> i);

      for (int color : textureOut.getPixelsRGBA()) {
         if (color != 0) {
            Color colorStruct = Color.rgba(color);
            int v = colorStruct.luminance();
            if (v < min) {
               min = v;
            }

            if (v > max) {
               max = v;
            }
         }
      }

      if (max <= min) {
         return textureOut;
      } else {
         for (int x = 0; x < textureOut.getWidth(); x++) {
            for (int y = 0; y < textureOut.getHeight(); y++) {
               Color colorx = Color.rgba(textureOut.getPixelRGBA(x, y));
               if (colorx.empty()) {
                  textureOut.setPixelRGBA(x, y, 0);
               } else {
                  Color normalized = new Color(
                     (int)Math.clamp(Mth.lerp((float)(colorx.red() - min) / (float)(max - min), fMin * 255.0F, fMax * 255.0F), 0.0F, 255.0F),
                     (int)Math.clamp(Mth.lerp((float)(colorx.green() - min) / (float)(max - min), fMin * 255.0F, fMax * 255.0F), 0.0F, 255.0F),
                     (int)Math.clamp(Mth.lerp((float)(colorx.blue() - min) / (float)(max - min), fMin * 255.0F, fMax * 255.0F), 0.0F, 255.0F)
                  );
                  textureOut.setPixelRGBA(x, y, normalized.toRgba());
               }
            }
         }

         return textureOut;
      }
   }

   private static Color stonePalette(Color color, int min, int max) {
      int v = color.luminance();
      float f = (float)(v - min) / (float)(max - min);
      Color a = new Color(6842474).scale(0.95F);
      Color b = new Color(11250586).scale(1.1F);
      return Color.lerp(f, a, b);
   }

   public static NativeImage transformTexture(NativeImage skinTexture) {
      skinTexture = normalizeValues(skinTexture, 0.0F, 1.0F);
      int[] pixelData = skinTexture.getPixelsRGBA();
      IntArraySet packedColors = new IntArraySet();

      for (int i : pixelData) {
         if (i != 0) {
            packedColors.add(i);
         }
      }

      if (packedColors.isEmpty()) {
         return skinTexture;
      } else {
         MeanShiftCluster.MeanShiftClusterResult meanShiftCluster = MeanShiftCluster.meanShiftCluster(new ArrayList<>(packedColors));
         NativeImage stoneOverlay = ((TextureAtlasSprite)Minecraft.getInstance()
               .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
               .apply(ResourceLocation.withDefaultNamespace("block/stone")))
            .contents()
            .originalImage;
         stoneOverlay = normalizeValues(stoneOverlay, 0.75F, 1.0F);

         for (int x = 0; x < skinTexture.getWidth(); x++) {
            for (int y = 0; y < skinTexture.getHeight(); y++) {
               int rgba = skinTexture.getPixelRGBA(x, y);
               if (rgba != 0) {
                  Color replacement = meanShiftCluster.lookupTable().computeIfAbsent(rgba, Color::new);
                  Color color = Color.rgba(replacement.packedARGB());
                  color = stonePalette(color, 0, 255);
                  Color stoneSample = Color.rgba(stoneOverlay.getPixelRGBA(x % stoneOverlay.getWidth(), y % stoneOverlay.getHeight()));
                  color = color.multiply(stoneSample);
                  skinTexture.setPixelRGBA(x, y, color.toRgba());
               }
            }
         }

         return skinTexture;
      }
   }

   @Nullable
   private static NativeImage downloadSkin(GameProfile gameProfile, String skinUrl) {
      try {
         HttpURLConnection c = (HttpURLConnection)new URL(skinUrl).openConnection();
         c.setConnectTimeout(6000);
         c.setReadTimeout(6000);

         NativeImage var4;
         try (InputStream in = c.getInputStream()) {
            var4 = NativeImage.read(in);
         }

         return var4;
      } catch (Exception var8) {
         IronsLib.LOGGER.error("Failed to download skin for player {} at url {}", gameProfile.getName(), skinUrl);
         return null;
      }
   }

   static {
      CompletableFuture.supplyAsync(StatueTextureManager::steve, Util.backgroundExecutor())
         .thenApply(StatueTextureManager::transformTexture)
         .thenAccept(image -> Minecraft.getInstance().getTextureManager().register(NULL_TEXTURE, new DynamicTexture(image)));
   }
}
