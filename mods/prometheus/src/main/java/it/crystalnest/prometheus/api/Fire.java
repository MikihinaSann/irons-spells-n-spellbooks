package it.crystalnest.prometheus.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.crystalnest.cobweb.api.registry.CobwebEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Fire {
   public static final StreamCodec<FriendlyByteBuf, Fire> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, Fire>() {
      public void encode(FriendlyByteBuf buffer, Fire fire) {
         buffer.writeResourceLocation(fire.getFireType());
         buffer.writeFloat(fire.getDamage());
         buffer.writeBoolean(fire.invertHealAndHarm());
         ResourceLocation source = fire.getComponent(Fire.Component.SOURCE_BLOCK);
         buffer.writeBoolean(source != null);
         if (source != null) {
            buffer.writeResourceLocation(source);
         }

         ResourceLocation campfire = fire.getComponent(Fire.Component.CAMPFIRE_BLOCK);
         buffer.writeBoolean(campfire != null);
         if (campfire != null) {
            buffer.writeResourceLocation(campfire);
         }
      }

      @NotNull
      public Fire decode(FriendlyByteBuf buffer) {
         Fire.Builder builder = FireManager.fireBuilder(buffer.readResourceLocation())
            .setDamage(buffer.readFloat())
            .setInvertHealAndHarm(buffer.readBoolean())
            .removeComponents(
               Fire.Component.CAMPFIRE_ITEM,
               Fire.Component.LANTERN_BLOCK,
               Fire.Component.LANTERN_ITEM,
               Fire.Component.TORCH_BLOCK,
               Fire.Component.TORCH_ITEM,
               Fire.Component.WALL_TORCH_BLOCK,
               Fire.Component.FLAME_PARTICLE
            );
         if (buffer.readBoolean()) {
            builder.setComponent(Fire.Component.SOURCE_BLOCK, buffer.readResourceLocation());
         } else {
            builder.removeComponent(Fire.Component.SOURCE_BLOCK);
         }

         if (buffer.readBoolean()) {
            builder.setComponent(Fire.Component.CAMPFIRE_BLOCK, buffer.readResourceLocation());
         } else {
            builder.removeComponent(Fire.Component.CAMPFIRE_BLOCK);
         }

         return builder.build();
      }
   };
   private final ResourceLocation fireType;
   private final int light;
   private final float damage;
   private final boolean invertHealAndHarm;
   private final boolean canRainDouse;
   private final Function<Entity, DamageSource> onCampfireGetter;
   private final Function<Entity, DamageSource> inFireGetter;
   private final Function<Entity, DamageSource> onFireGetter;
   private final Predicate<Entity> behavior;
   private final ImmutableMap<Fire.Component<?, ?>, ImmutableList<ResourceLocation>> components;

   Fire(
      ResourceLocation fireType,
      int light,
      float damage,
      boolean invertHealAndHarm,
      boolean canRainDouse,
      Function<Entity, DamageSource> onCampfireGetter,
      Function<Entity, DamageSource> inFireGetter,
      Function<Entity, DamageSource> onFireGetter,
      Predicate<Entity> behavior,
      Map<Fire.Component<?, ?>, List<ResourceLocation>> components
   ) {
      this.fireType = fireType;
      this.light = light;
      this.damage = damage;
      this.invertHealAndHarm = invertHealAndHarm;
      this.canRainDouse = canRainDouse;
      this.onCampfireGetter = onCampfireGetter;
      this.inFireGetter = inFireGetter;
      this.onFireGetter = onFireGetter;
      this.behavior = behavior;
      this.components = components.entrySet()
         .stream()
         .collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue())));
   }

   public ResourceLocation getFireType() {
      return this.fireType;
   }

   public int getLight() {
      return this.light;
   }

   public float getDamage() {
      return this.damage;
   }

   public boolean invertHealAndHarm() {
      return this.invertHealAndHarm;
   }

   public boolean canRainDouse() {
      return this.canRainDouse;
   }

   public DamageSource getOnCampfire(Entity entity) {
      return this.onCampfireGetter.apply(entity);
   }

   public DamageSource getInFire(Entity entity) {
      return this.inFireGetter.apply(entity);
   }

   public DamageSource getOnFire(Entity entity) {
      return this.onFireGetter.apply(entity);
   }

   public Predicate<Entity> getBehavior() {
      return this.behavior;
   }

   @Nullable
   public ResourceLocation getComponent(Fire.Component<?, ?> component) {
      ImmutableList<ResourceLocation> values = (ImmutableList<ResourceLocation>)this.components.get(component);
      return values != null && !values.isEmpty() ? (ResourceLocation)values.getFirst() : null;
   }

   @Nullable
   public List<ResourceLocation> getComponentList(Fire.Component<?, ?> component) {
      return (List<ResourceLocation>)this.components.get(component);
   }

   @Override
   public String toString() {
      return "Fire{fireType="
         + this.fireType
         + ", light="
         + this.light
         + ", damage="
         + this.damage
         + ", invertHealAndHarm="
         + this.invertHealAndHarm
         + ", canRainDouse="
         + this.canRainDouse
         + ", components="
         + this.components
         + "}";
   }

   public static final class Builder {
      public static final int DEFAULT_LIGHT = 15;
      public static final float DEFAULT_DAMAGE = 1.0F;
      public static final boolean DEFAULT_INVERT_HEAL_AND_HARM = false;
      public static final boolean DEFAULT_CAN_RAIN_DOUSE = false;
      public static final Function<Entity, DamageSource> DEFAULT_ON_CAMPFIRE_GETTER = entity -> entity.damageSources().campfire();
      public static final Function<Entity, DamageSource> DEFAULT_IN_FIRE_GETTER = entity -> entity.damageSources().inFire();
      public static final Function<Entity, DamageSource> DEFAULT_ON_FIRE_GETTER = entity -> entity.damageSources().onFire();
      public static final Predicate<Entity> DEFAULT_BEHAVIOR = entity -> true;
      private String modId;
      private String fireId;
      private int light;
      private float damage;
      private boolean invertHealAndHarm;
      private boolean canRainDouse;
      private Function<Entity, DamageSource> onCampfireGetter;
      private Function<Entity, DamageSource> inFireGetter;
      private Function<Entity, DamageSource> onFireGetter;
      private Predicate<Entity> behavior;
      private Map<Fire.Component<?, ?>, List<ResourceLocation>> components;

      Builder(String modId, String fireId) {
         this.reset(modId, fireId);
      }

      Builder(ResourceLocation fireType) {
         this.reset(fireType);
      }

      @Nullable
      private static <T> T get(@Nullable Optional<T> optional) {
         return optional != null && optional.isPresent() ? optional.get() : null;
      }

      public Fire.Builder setLight(int light) {
         if (light >= 0 && light <= 15) {
            this.light = light;
         }

         return this;
      }

      public Fire.Builder setDamage(float damage) {
         this.damage = damage;
         return this;
      }

      public Fire.Builder setInvertHealAndHarm(boolean invertHealAndHarm) {
         this.invertHealAndHarm = invertHealAndHarm;
         return this;
      }

      public Fire.Builder setCanRainDouse(boolean canRainDouse) {
         this.canRainDouse = canRainDouse;
         return this;
      }

      public Fire.Builder setOnCampfire(Function<Entity, DamageSource> getter) {
         this.onCampfireGetter = getter;
         return this;
      }

      public Fire.Builder setInFire(Function<Entity, DamageSource> getter) {
         this.inFireGetter = getter;
         return this;
      }

      public Fire.Builder setOnFire(Function<Entity, DamageSource> getter) {
         this.onFireGetter = getter;
         return this;
      }

      public Fire.Builder setBehavior(@NotNull Predicate<Entity> behavior) {
         this.behavior = behavior;
         return this;
      }

      public Fire.Builder setBehavior(@NotNull Consumer<Entity> behavior) {
         this.behavior = entity -> {
            behavior.accept(entity);
            return true;
         };
         return this;
      }

      public Fire.Builder setComponent(Fire.Component<?, ?> component, ResourceLocation id) {
         this.components.put(component, new ArrayList<>(List.of(id)));
         return this;
      }

      public Fire.Builder setComponent(Fire.Component<?, ?> component, ResourceLocation... ids) {
         this.components.put(component, new ArrayList<>(List.of(ids)));
         return this;
      }

      public Fire.Builder addToComponent(Fire.Component<?, ?> component, ResourceLocation... ids) {
         this.components.computeIfAbsent(component, k -> new ArrayList<>()).addAll(List.of(ids));
         return this;
      }

      public Fire.Builder setDefaultComponents() {
         this.components = new HashMap<>(
            Map.ofEntries(
               Fire.Component.SOURCE_BLOCK.getEntry(this.modId, this.fireId),
               Fire.Component.CAMPFIRE_BLOCK.getEntry(this.modId, this.fireId),
               Fire.Component.CAMPFIRE_ITEM.getEntry(this.modId, this.fireId),
               Fire.Component.LANTERN_BLOCK.getEntry(this.modId, this.fireId),
               Fire.Component.LANTERN_ITEM.getEntry(this.modId, this.fireId),
               Fire.Component.TORCH_BLOCK.getEntry(this.modId, this.fireId),
               Fire.Component.TORCH_ITEM.getEntry(this.modId, this.fireId),
               Fire.Component.WALL_TORCH_BLOCK.getEntry(this.modId, this.fireId),
               Fire.Component.FLAME_PARTICLE.getEntry(this.modId, this.fireId),
               Fire.Component.FIRE_CHARGE_ITEM.getEntry(this.modId, this.fireId)
            )
         );
         return this;
      }

      public Fire.Builder removeComponent(Fire.Component<?, ?> component) {
         this.components.remove(component);
         return this;
      }

      public Fire.Builder removeComponents(Fire.Component<?, ?>... components) {
         for (Fire.Component<?, ?> component : components) {
            this.components.remove(component);
         }

         return this;
      }

      public Fire.Builder reset(ResourceLocation fireType) {
         return this.reset(fireType.getNamespace(), fireType.getPath());
      }

      public Fire.Builder reset(String modId, String fireId) {
         this.modId = modId;
         return this.reset(fireId);
      }

      public Fire.Builder reset(String fireId) {
         this.fireId = fireId;
         this.light = 15;
         this.damage = 1.0F;
         this.invertHealAndHarm = false;
         this.canRainDouse = false;
         this.onCampfireGetter = DEFAULT_ON_CAMPFIRE_GETTER;
         this.inFireGetter = DEFAULT_IN_FIRE_GETTER;
         this.onFireGetter = DEFAULT_ON_FIRE_GETTER;
         this.behavior = DEFAULT_BEHAVIOR;
         this.components = new HashMap<>();
         return this;
      }

      public Fire build() throws IllegalStateException {
         if (FireManager.isValidFireId(this.fireId) && FireManager.isValidModId(this.modId)) {
            return new Fire(
               FireManager.sanitize(this.modId, this.fireId),
               this.light,
               this.damage,
               this.invertHealAndHarm,
               this.canRainDouse,
               this.onCampfireGetter,
               this.inFireGetter,
               this.onFireGetter,
               this.behavior,
               this.components
            );
         } else {
            throw new IllegalStateException("Attempted to build a Fire with a non-valid fireId [" + this.fireId + "] or modId [" + this.modId + "].");
         }
      }
   }

   public static final class Component<R, T extends R> {
      public static final Fire.Component<Block, Block> SOURCE_BLOCK = new Fire.Component<>(Registries.BLOCK, "_fire", FireRegistrar::registerFireSource);
      public static final Fire.Component<Block, Block> CAMPFIRE_BLOCK = new Fire.Component<>(Registries.BLOCK, "_campfire", FireRegistrar::registerCampfire);
      public static final Fire.Component<Item, BlockItem> CAMPFIRE_ITEM = new Fire.Component<>(
         Registries.ITEM, "_campfire", FireRegistrar::registerCampfireItem
      );
      public static final Fire.Component<Block, Block> LANTERN_BLOCK = new Fire.Component<>(Registries.BLOCK, "_lantern", FireRegistrar::registerLantern);
      public static final Fire.Component<Item, BlockItem> LANTERN_ITEM = new Fire.Component<>(Registries.ITEM, "_lantern", FireRegistrar::registerLanternItem);
      public static final Fire.Component<Block, Block> TORCH_BLOCK = new Fire.Component<>(
         Registries.BLOCK, "_torch", type -> (CobwebEntry<? extends Block>)FireRegistrar.registerTorch(type).getLeft()
      );
      public static final Fire.Component<Item, StandingAndWallBlockItem> TORCH_ITEM = new Fire.Component<>(
         Registries.ITEM, "_torch", FireRegistrar::registerTorchItem
      );
      public static final Fire.Component<Block, Block> WALL_TORCH_BLOCK = new Fire.Component<>(
         Registries.BLOCK, "_wall_torch", type -> (CobwebEntry<? extends Block>)FireRegistrar.registerTorch(type).getRight()
      );
      public static final Fire.Component<ParticleType<?>, SimpleParticleType> FLAME_PARTICLE = new Fire.Component<>(
         Registries.PARTICLE_TYPE, "_flame", FireRegistrar::registerParticle
      );
      public static final Fire.Component<Item, FireChargeItem> FIRE_CHARGE_ITEM = new Fire.Component<>(
         Registries.ITEM, "_fire_charge", FireRegistrar::registerFireCharge
      );
      private final ResourceKey<? extends Registry<R>> key;
      private final String suffix;
      private final Function<ResourceLocation, CobwebEntry<? extends T>> register;

      private Component(ResourceKey<? extends Registry<R>> key, String suffix, Function<ResourceLocation, CobwebEntry<? extends T>> register) {
         this.key = key;
         this.suffix = suffix;
         this.register = register;
      }

      public CobwebEntry<? extends T> register(ResourceLocation fireType) {
         return this.register.apply(fireType);
      }

      @NotNull
      Registry<R> getRegistry() {
         return (Registry<R>)BuiltInRegistries.REGISTRY.get(this.key.location());
      }

      @Nullable
      T getValue(ResourceLocation id) {
         return (T)this.getRegistry().getOptional(id).orElse(null);
      }

      @Nullable
      T getValue(Fire fire) {
         return this.getValue(fire.getComponent(this));
      }

      @Nullable
      List<T> getValues(Fire fire) {
         List<ResourceLocation> list = fire.getComponentList(this);
         return list == null ? null : list.stream().map(this::getValue).toList();
      }

      @SuppressWarnings("unchecked")
      Optional<T> getOptionalValue(ResourceLocation id) {
         return (Optional<T>)this.getRegistry().getOptional(id);
      }

      Optional<T> getOptionalValue(Fire fire) {
         return this.getOptionalValue(fire.getComponent(this));
      }

      List<Optional<T>> getOptionalValues(Fire fire) {
         List<ResourceLocation> list = fire.getComponentList(this);
         return list == null ? List.of() : list.stream().map(this::getOptionalValue).toList();
      }

      Entry<Fire.Component<R, T>, List<ResourceLocation>> getEntry(String modId, String fireId) {
         return Map.entry(this, List.of(ResourceLocation.fromNamespaceAndPath(modId, fireId + this.suffix)));
      }
   }
}
