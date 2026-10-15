package it.crystalnest.prometheus.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.platform.Services;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FireResourceReloadListener extends SimpleJsonResourceReloadListener {
   private static final ArrayList<ResourceLocation> ddfiresUnregister = new ArrayList<>();
   private static final ArrayList<ResourceLocation> ddfiresRegister = new ArrayList<>();
   private static final String SOURCE_FIELD_NAME = "source";
   private static final String CAMPFIRE_FIELD_NAME = "campfire";
   private static final String LANTERN_FIELD_NAME = "campfire";
   private static final String TORCH_FIELD_NAME = "campfire";
   private static final String WALL_TORCH_FIELD_NAME = "campfire";

   public FireResourceReloadListener() {
      super(new Gson(), "fires");
   }

   public static void handle(@Nullable ServerPlayer player) {
      for (ResourceLocation fireType : ddfiresUnregister) {
         Services.NETWORK.sendToClient(player, fireType);
      }

      for (ResourceLocation fireType : ddfiresRegister) {
         Services.NETWORK.sendToClient(player, FireManager.getFire(fireType));
      }
   }

   private static JsonObject getJsonObject(String identifier, JsonElement element) throws IllegalStateException {
      try {
         return element.getAsJsonObject();
      } catch (IllegalStateException var3) {
         Constants.LOGGER.error("prometheus encountered a non-blocking DDFire error!\nError parsing ddfire [{}]: not a JSON object.", identifier);
         throw var3;
      }
   }

   private static <T> T parse(String identifier, String field, JsonObject data, Function<JsonElement, T> parser) throws NullPointerException, UnsupportedOperationException, IllegalStateException, NumberFormatException {
      try {
         return parser.apply(data.get(field));
      } catch (UnsupportedOperationException | IllegalStateException | NumberFormatException | NullPointerException var5) {
         Constants.LOGGER
            .error(
               "prometheus encountered a non-blocking DDFire error!\nError parsing required field \"{}\" for ddfire [{}]: missing or malformed field.",
               field,
               identifier
            );
         throw var5;
      }
   }

   private static <T> T parse(String identifier, String field, JsonObject data, Function<JsonElement, T> parser, T fallback) throws UnsupportedOperationException, IllegalStateException, NumberFormatException {
      try {
         return parser.apply(data.get(field));
      } catch (NullPointerException var6) {
         return fallback;
      } catch (IllegalStateException | NumberFormatException | UnsupportedOperationException var7) {
         Constants.LOGGER
            .error(
               "prometheus encountered a non-blocking DDFire error!\nError parsing optional field \"{}\" for ddfire [{}]: malformed field.", field, identifier
            );
         throw var7;
      }
   }

   private static void unregisterFires() {
      for (ResourceLocation fireType : ddfiresRegister) {
         if (FireManager.unregisterFire(fireType) != null) {
            ddfiresUnregister.add(fireType);
         }
      }

      ddfiresRegister.clear();
   }

   private static void registerFire(ResourceLocation fireType, Fire fire) {
      if (FireManager.registerFire(fire) != null) {
         ddfiresRegister.add(fireType);
      } else {
         Constants.LOGGER.error("Unable to register ddfire [{}].", fireType);
      }
   }

   private static void registerFire(JsonObject jsonFire, String mod, String jsonIdentifier) {
      ResourceLocation fireType = ResourceLocation.fromNamespaceAndPath(mod, parse(jsonIdentifier, "fire", jsonFire, JsonElement::getAsString));
      String fireTypeString = fireType.toString();
      Fire.Builder builder = FireManager.fireBuilder(fireType)
         .setDamage(parse(fireTypeString, "damage", jsonFire, JsonElement::getAsFloat, 1.0F))
         .setInvertHealAndHarm(parse(fireTypeString, "invertHealAndHarm", jsonFire, JsonElement::getAsBoolean, false))
         .removeComponents(Fire.Component.CAMPFIRE_ITEM, Fire.Component.LANTERN_ITEM, Fire.Component.TORCH_ITEM, Fire.Component.FLAME_PARTICLE);
      removeOrSet(fireTypeString, builder, jsonFire, "source", Fire.Component.SOURCE_BLOCK);
      removeOrSet(fireTypeString, builder, jsonFire, "campfire", Fire.Component.CAMPFIRE_BLOCK);
      removeOrSet(fireTypeString, builder, jsonFire, "campfire", Fire.Component.LANTERN_BLOCK);
      removeOrSet(fireTypeString, builder, jsonFire, "campfire", Fire.Component.TORCH_BLOCK);
      removeOrSet(fireTypeString, builder, jsonFire, "campfire", Fire.Component.WALL_TORCH_BLOCK);
      registerFire(fireType, builder.build());
   }

   private static void removeOrSet(String fireType, Fire.Builder builder, JsonObject data, String field, Fire.Component<?, ?> component) {
      if (data.has(field)) {
         JsonElement element = data.get(field);
         List<String> list = element.isJsonArray()
            ? element.getAsJsonArray().asList().stream().<String>map(JsonElement::getAsString).toList()
            : List.of(element.getAsString());
         if (!list.isEmpty() && !list.getFirst().equals("remove")) {
            builder.setComponent(component, list.stream().map(ResourceLocation::parse).toArray(ResourceLocation[]::new));
         } else {
            builder.removeComponent(component);
         }
      }
   }

   protected void apply(Map<ResourceLocation, JsonElement> fires, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
      unregisterFires();

      for (Entry<ResourceLocation, JsonElement> fire : fires.entrySet()) {
         String jsonIdentifier = fire.getKey().getPath();

         try {
            JsonObject jsonData = getJsonObject(jsonIdentifier, fire.getValue());
            String mod = parse(jsonIdentifier, "mod", jsonData, JsonElement::getAsString);
            if (Services.PLATFORM.isModLoaded(mod)) {
               parse(jsonIdentifier, "fires", jsonData, JsonElement::getAsJsonArray)
                  .forEach(element -> registerFire(getJsonObject(jsonIdentifier, element), mod, jsonIdentifier));
            } else {
               Constants.LOGGER.warn("Registering of ddfires for [{}] is canceled: {} is not loaded.", mod, mod);
            }
         } catch (UnsupportedOperationException | IllegalStateException | NumberFormatException | NullPointerException var9) {
            Constants.LOGGER.error("Registering of ddfires for [{}] is canceled.", jsonIdentifier);
         }
      }
   }
}
