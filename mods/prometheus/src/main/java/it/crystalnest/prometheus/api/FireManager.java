package it.crystalnest.prometheus.api;

import it.crystalnest.cobweb.api.block.entity.DynamicBlockEntityType;
import it.crystalnest.cobweb.api.registry.CobwebEntry;
import it.crystalnest.cobweb.api.registry.CobwebRegistry;
import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomLanternBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.api.block.entity.CustomCampfireBlockEntity;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import it.crystalnest.prometheus.api.type.FireTyped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class FireManager {
   public static final ResourceLocation DEFAULT_FIRE_TYPE = ResourceLocation.withDefaultNamespace("");
   public static final ResourceLocation SOUL_FIRE_TYPE = ResourceLocation.withDefaultNamespace("soul");
   public static final Fire DEFAULT_FIRE = new Fire(
      DEFAULT_FIRE_TYPE,
      15,
      1.0F,
      false,
      true,
      Fire.Builder.DEFAULT_ON_CAMPFIRE_GETTER,
      Fire.Builder.DEFAULT_IN_FIRE_GETTER,
      Fire.Builder.DEFAULT_ON_FIRE_GETTER,
      Fire.Builder.DEFAULT_BEHAVIOR,
      Map.ofEntries(
         Map.entry(Fire.Component.SOURCE_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.FIRE))),
         Map.entry(Fire.Component.CAMPFIRE_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.CAMPFIRE))),
         Map.entry(Fire.Component.CAMPFIRE_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.CAMPFIRE))),
         Map.entry(Fire.Component.LANTERN_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.LANTERN))),
         Map.entry(Fire.Component.LANTERN_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.LANTERN))),
         Map.entry(Fire.Component.TORCH_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.TORCH))),
         Map.entry(Fire.Component.WALL_TORCH_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.WALL_TORCH))),
         Map.entry(Fire.Component.TORCH_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.TORCH))),
         Map.entry(Fire.Component.FLAME_PARTICLE, List.of(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.FLAME))),
         Map.entry(Fire.Component.FIRE_CHARGE_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.FIRE_CHARGE)))
      )
   );
   private static final ConcurrentHashMap<ResourceLocation, Fire> FIRES = new ConcurrentHashMap<>();
   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   @Internal
   public static CobwebEntry<DynamicBlockEntityType<CustomCampfireBlockEntity>> CUSTOM_CAMPFIRE_ENTITY_TYPE;
   private static boolean LOADED = false;

   private FireManager() {
   }

   public static CobwebEntry<DynamicBlockEntityType<CustomCampfireBlockEntity>> getCustomCampfireEntityType() {
      return CUSTOM_CAMPFIRE_ENTITY_TYPE;
   }

   @Internal
   public static synchronized void load() {
      if (LOADED) {
         throw new IllegalStateException("FireManager was already loaded");
      } else {
         LOADED = true;
         CUSTOM_CAMPFIRE_ENTITY_TYPE = CobwebRegistry.of(Registries.BLOCK_ENTITY_TYPE, "prometheus")
            .register(
               "custom_campfire",
               () -> DynamicBlockEntityType.of(
                  CustomCampfireBlockEntity::new,
                  state -> getComponentList(Fire.Component.CAMPFIRE_BLOCK)
                     .stream()
                     .filter(CustomCampfireBlock.class::isInstance)
                     .toList()
                     .contains(state.getBlock())
               )
            );
         FireRegistrar.load();
      }
   }

   public static Fire.Builder fireBuilder(String modId, String fireId) {
      return new Fire.Builder(modId, fireId);
   }

   public static Fire.Builder fireBuilder(ResourceLocation fireType) {
      return new Fire.Builder(fireType);
   }

   @Nullable
   public static synchronized Fire registerFire(Fire fire) {
      Fire previous = FIRES.computeIfAbsent(fire.getFireType(), key -> {
         Fire.Component.SOURCE_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
         Fire.Component.CAMPFIRE_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
         Fire.Component.LANTERN_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
         Fire.Component.TORCH_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
         Fire.Component.WALL_TORCH_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
         return fire;
      });
      if (previous != fire) {
         ResourceLocation fireType = fire.getFireType();
         Constants.LOGGER.error("Fire [{}] was already registered with the following value: {}", fireType, getFire(fireType));
         return null;
      } else {
         Constants.LOGGER.debug("Successfully registered Fire [{}]", fire);
         return fire;
      }
   }

   @NotNull
   private static Consumer<Optional<Block>> setTypeOrWarn(ResourceLocation fireType) {
      return value -> value.ifPresent(
         block -> {
            if (block instanceof FireTypeChanger fireTypeChanger) {
               fireTypeChanger.setFireType(fireType);
            } else {
               Constants.LOGGER
                  .warn(
                     "Could not set Fire Type [{}] for block [{}]\nThings might not work as expected!\nYou can ignore this warning if this was intended",
                     fireType,
                     block
                  );
            }
         }
      );
   }

   public static synchronized Map<ResourceLocation, Fire> registerFires(Fire... fires) {
      return registerFires(List.of(fires));
   }

   public static synchronized Map<ResourceLocation, Fire> registerFires(List<Fire> fires) {
      HashMap<ResourceLocation, Fire> outcomes = new HashMap<>();

      for (Fire fire : fires) {
         outcomes.put(fire.getFireType(), registerFire(fire));
      }

      return outcomes;
   }

   @Nullable
   @Internal
   public static synchronized Fire unregisterFire(ResourceLocation fireType) {
      return FIRES.remove(fireType);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, MapColor color, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return FireRegistrar.registerFireSource(fireType, color, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, TagKey<Block> base, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, Properties, T> constructor
   ) {
      return FireRegistrar.registerFireSource(fireType, base, color, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(
      ResourceLocation fireType, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return FireRegistrar.registerCampfire(fireType, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(
      ResourceLocation fireType, boolean spawnParticles, TriFunction<ResourceLocation, Boolean, Properties, T> constructor
   ) {
      return FireRegistrar.registerCampfire(fireType, spawnParticles, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static CobwebEntry<BlockItem> registerCampfireItem(ResourceLocation fireType) {
      return FireRegistrar.registerCampfireItem(fireType, BlockItem::new);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(
      ResourceLocation fireType, BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return FireRegistrar.registerCampfireItem(fireType, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static CobwebEntry<SimpleParticleType> registerParticle(ResourceLocation fireType) {
      return FireRegistrar.registerParticle(fireType);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends SimpleParticleType> CobwebEntry<T> registerParticle(ResourceLocation fireType, Supplier<T> supplier) {
      return FireRegistrar.registerParticle(fireType, supplier);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static Pair<CobwebEntry<CustomTorchBlock>, CobwebEntry<CustomWallTorchBlock>> registerTorch(ResourceLocation fireType) {
      return FireRegistrar.registerTorch(fireType);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends CustomTorchBlock, W extends CustomWallTorchBlock> Pair<CobwebEntry<T>, CobwebEntry<W>> registerTorch(
      ResourceLocation fireType,
      TriFunction<ResourceLocation, Supplier<SimpleParticleType>, Properties, T> torchSupplier,
      TriFunction<ResourceLocation, Supplier<SimpleParticleType>, Properties, W> wallTorchSupplier
   ) {
      return FireRegistrar.registerTorch(fireType, torchSupplier, wallTorchSupplier);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static CobwebEntry<StandingAndWallBlockItem> registerTorchItem(ResourceLocation fireType) {
      return FireRegistrar.registerTorchItem(fireType);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(
      ResourceLocation fireType, TriFunction<Block, Block, net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return FireRegistrar.registerTorchItem(fireType, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(
      ResourceLocation fireType,
      TriFunction<Block, Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return FireRegistrar.registerTorchItem(fireType, constructor, properties);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static CobwebEntry<CustomLanternBlock> registerLantern(ResourceLocation fireType) {
      return FireRegistrar.registerLantern(fireType);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends CustomLanternBlock> CobwebEntry<T> registerLantern(
      ResourceLocation fireType, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return FireRegistrar.registerLantern(fireType, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static CobwebEntry<BlockItem> registerLanternItem(ResourceLocation fireType) {
      return FireRegistrar.registerLanternItem(fireType);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(
      ResourceLocation fireType, BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return FireRegistrar.registerLanternItem(fireType, constructor);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.2.0"
   )
   public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(
      ResourceLocation fireType,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return FireRegistrar.registerLanternItem(fireType, constructor, properties);
   }

   public static Fire getFire(@Nullable String modId, @Nullable String fireId) {
      return isValidModId(modId) && isValidFireId(fireId) ? getFire(fireType(modId, fireId)) : DEFAULT_FIRE;
   }

   public static Fire getFire(@Nullable ResourceLocation fireType) {
      return FIRES.getOrDefault(ensure(fireType), DEFAULT_FIRE);
   }

   public static List<Fire> getFires() {
      return FIRES.values().stream().toList();
   }

   public static <T> T getProperty(ResourceLocation fireType, Function<Fire, T> getter) {
      return getter.apply(getFire(fireType));
   }

   public static <T> List<T> getPropertyList(Function<Fire, T> getter) {
      return FIRES.values().stream().map(getter).toList();
   }

   public static DamageSource getDamageSource(Entity entity, ResourceLocation fireType, BiFunction<Fire, Entity, DamageSource> getter) {
      return getter.apply(getFire(fireType), entity);
   }

   @Nullable
   public static ResourceLocation getComponentId(ResourceLocation fireType, Fire.Component<?, ?> component) {
      return getFire(fireType).getComponent(component);
   }

   @Nullable
   public static List<ResourceLocation> getComponentIds(ResourceLocation fireType, Fire.Component<?, ?> component) {
      return getFire(fireType).getComponentList(component);
   }

   @Nullable
   public static <R, T extends R> T getComponent(ResourceLocation fireType, Fire.Component<R, T> component) {
      return component.getValue(getComponentId(fireType, component));
   }

   @Nullable
   public static <R, T extends R> List<T> getComponentList(ResourceLocation fireType, Fire.Component<R, T> component) {
      List<ResourceLocation> list = getComponentIds(fireType, component);
      return list == null ? null : list.stream().map(component::getValue).toList();
   }

   @NotNull
   static String getComponentPath(ResourceLocation fireType, Fire.Component<?, ?> component) {
      return Objects.requireNonNull(getComponentId(fireType, component)).getPath();
   }

   @NotNull
   private static List<String> getComponentPaths(ResourceLocation fireType, Fire.Component<?, ?> component) {
      return Objects.requireNonNull(getComponentIds(fireType, component)).stream().<String>map(ResourceLocation::getPath).toList();
   }

   @NotNull
   public static <R, T extends R> T getRequiredComponent(ResourceLocation fireType, Fire.Component<R, T> component) throws NullPointerException {
      return Objects.requireNonNull(component.getValue(getComponentId(fireType, component)));
   }

   @NotNull
   public static <R, T extends R> T getRequiredComponent(ResourceLocation fireType, Fire.Component<R, T> component, String id) throws NullPointerException {
      return Objects.requireNonNull(
         component.getValue(
            Objects.requireNonNull(getComponentIds(fireType, component)).stream().filter(path -> path.getPath().equals(id)).findFirst().orElseThrow()
         )
      );
   }

   @NotNull
   public static <R, T extends R> List<T> getRequiredComponentList(ResourceLocation fireType, Fire.Component<R, T> component) throws NullPointerException {
      return Objects.requireNonNull(getComponentIds(fireType, component)).stream().map(component::getValue).toList();
   }

   public static List<ResourceLocation> getComponentIdList(Fire.Component<?, ?> component) {
      return FIRES.values().stream().map(fire -> fire.getComponent(component)).filter(Objects::nonNull).toList();
   }

   public static List<List<ResourceLocation>> getComponentIdsList(Fire.Component<?, ?> component) {
      return FIRES.values().stream().map(fire -> fire.getComponentList(component)).filter(l -> l != null && !l.isEmpty()).toList();
   }

   public static <R, T extends R> List<T> getComponentList(Fire.Component<R, T> component) {
      return FIRES.values().stream().map(component::getValue).filter(Objects::nonNull).toList();
   }

   public static <R, T extends R> List<List<T>> getComponentListList(Fire.Component<R, T> component) {
      return FIRES.values().stream().map(component::getValues).filter(l -> l != null && !l.isEmpty()).toList();
   }

   public static boolean isValidType(@Nullable String modId, @Nullable String fireId) {
      return isValidModId(modId) && isValidFireId(fireId);
   }

   public static boolean isValidType(@Nullable ResourceLocation fireType) {
      return fireType != null && Strings.isNotBlank(fireType.getNamespace()) && Strings.isNotBlank(fireType.getPath());
   }

   public static boolean isRegisteredType(@Nullable String modId, @Nullable String fireId) {
      return isValidModId(modId) && isValidFireId(fireId) && isRegisteredType(fireType(modId, fireId));
   }

   public static boolean isRegisteredType(@Nullable ResourceLocation fireType) {
      return fireType != null && FIRES.containsKey(fireType);
   }

   public static boolean isValidFireId(@Nullable String fireId) {
      return Strings.isNotBlank(fireId) && ResourceLocation.isValidPath(fireId);
   }

   public static boolean isRegisteredFireId(@Nullable String fireId) {
      return isValidFireId(fireId) && FIRES.keySet().stream().anyMatch(fireType -> fireType.getPath().equals(fireId));
   }

   public static boolean isValidModId(@Nullable String modId) {
      return Strings.isNotBlank(modId) && ResourceLocation.isValidNamespace(modId);
   }

   public static boolean isRegisteredModId(@Nullable String modId) {
      return isValidModId(modId) && FIRES.keySet().stream().anyMatch(fireType -> fireType.getNamespace().equals(modId));
   }

   public static ResourceLocation sanitize(@Nullable String modId, @Nullable String fireId) {
      return isValidModId(modId) && isValidModId(fireId) ? sanitize(fireType(modId, fireId)) : DEFAULT_FIRE_TYPE;
   }

   public static ResourceLocation sanitize(@Nullable ResourceLocation fireType) {
      return isValidType(fireType) ? fireType : DEFAULT_FIRE_TYPE;
   }

   public static ResourceLocation ensure(@Nullable String modId, @Nullable String fireId) {
      String trimmedModId = modId == null ? "" : modId.trim();
      String trimmedFireId = fireId == null ? "" : fireId.trim();
      return isValidModId(trimmedModId) && isValidFireId(trimmedFireId)
         ? ensure(ResourceLocation.fromNamespaceAndPath(trimmedModId, trimmedFireId))
         : DEFAULT_FIRE_TYPE;
   }

   public static ResourceLocation ensure(@Nullable ResourceLocation fireType) {
      return isRegisteredType(fireType) ? fireType : DEFAULT_FIRE_TYPE;
   }

   public static List<ResourceLocation> getFireTypes() {
      return FIRES.keySet().stream().toList();
   }

   public static List<String> getFireIds() {
      return FIRES.keySet().stream().<String>map(ResourceLocation::getPath).toList();
   }

   public static List<String> getModIds() {
      return FIRES.keySet().stream().<String>map(ResourceLocation::getNamespace).toList();
   }

   public static <R, T extends R> ResourceLocation getFireType(Fire.Component<R, T> component, T object) {
      return getFireTypes().stream().filter(type -> {
         List<T> list = getComponentList(type, component);
         return list != null && list.contains(object);
      }).findFirst().orElse(DEFAULT_FIRE_TYPE);
   }

   public static void setOnFire(Entity entity, float seconds, ResourceLocation fireType) {
      setOnFire(entity, seconds, fireType, Entity::igniteForSeconds);
   }

   @Internal
   public static void setOnFire(Entity entity, float duration, ResourceLocation fireType, BiConsumer<Entity, Float> setOnFireFunction) {
      setOnFireFunction.accept(entity, duration);
      ((FireTypeChanger)entity).setFireType(ensure(fireType));
   }

   public static boolean affect(Entity entity, ResourceLocation fireType, BiFunction<Fire, Entity, DamageSource> damageSourceGetter) {
      return affect(entity, fireType, damageSourceGetter, Entity::hurt);
   }

   @Internal
   public static boolean affect(
      Entity entity,
      ResourceLocation fireType,
      BiFunction<Fire, Entity, DamageSource> damageSourceGetter,
      TriFunction<Entity, DamageSource, Float, Boolean> hurtFunction
   ) {
      ((FireTypeChanger)entity).setFireType(ensure(fireType));
      return affect(
         entity,
         getDamageSource(entity, fireType, damageSourceGetter),
         getProperty(fireType, Fire::getDamage),
         getProperty(fireType, Fire::invertHealAndHarm),
         hurtFunction
      );
   }

   private static boolean affect(
      Entity entity, DamageSource damageSource, float damage, boolean invertHealAndHarm, TriFunction<Entity, DamageSource, Float, Boolean> hurtFunction
   ) {
      Predicate<Entity> behavior = getProperty(((FireTyped)entity).getFireType(), Fire::getBehavior);
      if (entity.level() instanceof ServerLevel level && behavior.test(entity) && Float.compare(damage, 0.0F) != 0) {
         if (damage > 0.0F) {
            if (entity instanceof LivingEntity livingEntity) {
               if (livingEntity.isInvertedHealAndHarm() && invertHealAndHarm) {
                  livingEntity.heal(damage);
                  return false;
               }

               return (Boolean)hurtFunction.apply(livingEntity, damageSource, damage);
            }

            return (Boolean)hurtFunction.apply(entity, damageSource, damage);
         }

         if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isInvertedHealAndHarm() && invertHealAndHarm) {
               return (Boolean)hurtFunction.apply(livingEntity, damageSource, -damage);
            }

            livingEntity.heal(-damage);
            return false;
         }
      }

      return false;
   }

   private static ResourceLocation fireType(@Nullable String modId, @Nullable String fireId) {
      return ResourceLocation.fromNamespaceAndPath(Objects.requireNonNull(modId), Objects.requireNonNull(fireId));
   }

   public static int light(ResourceLocation fireType) {
      return getProperty(fireType, Fire::getLight);
   }

   public static ToIntFunction<BlockState> lightLevel(ResourceLocation fireType) {
      return state -> light(fireType);
   }
}
