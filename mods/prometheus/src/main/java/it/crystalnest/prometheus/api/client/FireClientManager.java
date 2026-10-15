package it.crystalnest.prometheus.api.client;

import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class FireClientManager {
   private static final ConcurrentHashMap<ResourceLocation, FireClient> FIRES = new ConcurrentHashMap<>();

   private FireClientManager() {
   }

   @Nullable
   private static synchronized FireClient registerFire(FireClient fire) {
      if (FIRES.putIfAbsent(fire.getFireType(), fire) != null) {
         ResourceLocation fireType = fire.getFireType();
         Constants.LOGGER.error("FireClient [{}] was already registered with the following value: {}", fireType, FIRES.get(fireType));
         return null;
      } else {
         return fire;
      }
   }

   @Nullable
   public static synchronized FireClient registerFire(Fire fire) {
      return registerFire(new FireClient(fire.getFireType()));
   }

   public static synchronized Map<ResourceLocation, FireClient> registerFires(List<Fire> fires) {
      HashMap<ResourceLocation, FireClient> outcomes = new HashMap<>();

      for (Fire fire : fires) {
         outcomes.put(fire.getFireType(), registerFire(fire));
      }

      return outcomes;
   }

   public static synchronized Map<ResourceLocation, FireClient> registerFires(Fire... fires) {
      return registerFires(List.of(fires));
   }

   @Nullable
   @Internal
   public static synchronized FireClient unregisterFire(ResourceLocation fireType) {
      return FIRES.remove(fireType);
   }

   public static Material getMaterial0(ResourceLocation fireType) {
      return FireManager.isRegisteredType(fireType) && FIRES.containsKey(fireType) ? FIRES.get(fireType).getMaterial0() : ModelBakery.FIRE_0;
   }

   public static Material getMaterial1(ResourceLocation fireType) {
      return FireManager.isRegisteredType(fireType) && FIRES.containsKey(fireType) ? FIRES.get(fireType).getMaterial1() : ModelBakery.FIRE_1;
   }

   public static TextureAtlasSprite getSprite0(ResourceLocation fireType) {
      return FireManager.isRegisteredType(fireType) && FIRES.containsKey(fireType) ? FIRES.get(fireType).getSprite0() : ModelBakery.FIRE_0.sprite();
   }

   public static TextureAtlasSprite getSprite1(ResourceLocation fireType) {
      return FireManager.isRegisteredType(fireType) && FIRES.containsKey(fireType) ? FIRES.get(fireType).getSprite1() : ModelBakery.FIRE_1.sprite();
   }
}
