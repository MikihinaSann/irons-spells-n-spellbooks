package it.crystalnest.prometheus.api;

import it.crystalnest.cobweb.api.pack.dynamic.DynamicDataPack;
import it.crystalnest.cobweb.api.pack.dynamic.DynamicTagBuilder;
import it.crystalnest.cobweb.api.registry.CobwebEntry;
import it.crystalnest.cobweb.api.registry.CobwebRegister;
import it.crystalnest.cobweb.api.registry.CobwebRegistry;
import it.crystalnest.cobweb.platform.model.Platform;
import it.crystalnest.prometheus.QuadriFunction;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomLanternBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.platform.Services;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class FireRegistrar {
   private static final DynamicDataPack FIRE_SOURCE_TAGS = DynamicDataPack.named(ResourceLocation.fromNamespaceAndPath("prometheus", "fire_source_tags"));
   private static final DynamicDataPack CAMPFIRE_TAGS = DynamicDataPack.named(ResourceLocation.fromNamespaceAndPath("prometheus", "campfire_tags"));
   private static final DynamicDataPack FIRE_CHARGE_TAGS = DynamicDataPack.named(ResourceLocation.fromNamespaceAndPath("prometheus", "fire_charge_tags"));
   private static boolean LOADED = false;

   @Internal
   static synchronized void load() {
      if (LOADED) {
         throw new IllegalStateException("FireManager was already loaded");
      } else {
         LOADED = true;
         FIRE_SOURCE_TAGS.register();
         CAMPFIRE_TAGS.register();
         FIRE_CHARGE_TAGS.register();
      }
   }

   private FireRegistrar() {
   }

   public static void registerDefaultFireComponents(ResourceLocation fireType, Fire.Component<?, ?>... components) {
      for (Fire.Component<?, ?> component : components) {
         component.register(fireType);
      }
   }

   public static CobwebEntry<CustomFireBlock> registerFireSource(ResourceLocation fireType) {
      return registerFireSource(fireType, MapColor.FIRE, CustomFireBlock::new);
   }

   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, Properties, T> constructor
   ) {
      return registerFireSource(
         fireType,
         TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(fireMod(fireType), fireType.getPath() + "_fire_base_blocks")),
         color,
         constructor
      );
   }

   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, TagKey<Block> base, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, Properties, T> constructor
   ) {
      return registerFireSource(
         fireType, color, (BiFunction<ResourceLocation, Properties, T>)((type, properties) -> (T)constructor.apply(type, base, properties))
      );
   }

   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, MapColor color, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return registerFireSource(fireType, FireManager.getComponentPath(fireType, Fire.Component.SOURCE_BLOCK), color, constructor);
   }

   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, String blockId, TagKey<Block> base, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, Properties, T> constructor
   ) {
      return registerFireSource(fireType, blockId, color, (type, properties) -> (T)constructor.apply(type, base, properties));
   }

   public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(
      ResourceLocation fireType, String blockId, MapColor color, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      CobwebEntry<T> source = CobwebRegistry.ofBlocks(fireMod(fireType)).register(blockId, () -> constructor.apply(fireType, Properties.of().mapColor(color)));
      FIRE_SOURCE_TAGS.add(new Supplier[]{() -> DynamicTagBuilder.of(Registries.BLOCK, new TagKey[]{BlockTags.FIRE}).addElement((Block)source.get())});
      return source;
   }

   public static CobwebEntry<CustomCampfireBlock> registerCampfire(ResourceLocation fireType) {
      return registerCampfire(fireType, false, CustomCampfireBlock::new);
   }

   public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(
      ResourceLocation fireType, boolean spawnParticles, TriFunction<ResourceLocation, Boolean, Properties, T> constructor
   ) {
      return registerCampfire(fireType, (type, properties) -> (T)constructor.apply(type, spawnParticles, properties));
   }

   public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(
      ResourceLocation fireType, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return registerCampfire(fireType, FireManager.getComponentPath(fireType, Fire.Component.CAMPFIRE_BLOCK), constructor);
   }

   public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(
      ResourceLocation fireType, String blockId, boolean spawnParticles, TriFunction<ResourceLocation, Boolean, Properties, T> constructor
   ) {
      return registerCampfire(fireType, blockId, (type, properties) -> (T)constructor.apply(type, spawnParticles, properties));
   }

   public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(
      ResourceLocation fireType, String blockId, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      CobwebEntry<T> campfire = CobwebRegistry.ofBlocks(fireMod(fireType)).register(blockId, () -> constructor.apply(fireType, Properties.of()));
      CAMPFIRE_TAGS.add(new Supplier[]{() -> DynamicTagBuilder.of(Registries.BLOCK, new TagKey[]{BlockTags.CAMPFIRES}).addElement((Block)campfire.get())});
      return campfire;
   }

   public static CobwebEntry<BlockItem> registerCampfireItem(ResourceLocation fireType) {
      return registerCampfireItem(fireType, BlockItem::new);
   }

   public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(
      ResourceLocation fireType, BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return registerCampfireItem(fireType, constructor, new net.minecraft.world.item.Item.Properties());
   }

   public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(
      ResourceLocation fireType,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return registerCampfireItem(
         fireType,
         FireManager.getComponentPath(fireType, Fire.Component.CAMPFIRE_ITEM),
         FireManager.getComponentPath(fireType, Fire.Component.CAMPFIRE_BLOCK),
         constructor,
         properties
      );
   }

   public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(
      ResourceLocation fireType,
      String itemId,
      String blockId,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return CobwebRegistry.ofItems(fireMod(fireType))
         .register(
            itemId,
            () -> constructor.apply(
               FireManager.getRequiredComponent(fireType, Fire.Component.CAMPFIRE_BLOCK, blockId),
               properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
            )
         );
   }

   public static CobwebEntry<SimpleParticleType> registerParticle(ResourceLocation fireType) {
      return registerParticle(fireType, () -> new SimpleParticleType(false));
   }

   public static <T extends SimpleParticleType> CobwebEntry<T> registerParticle(ResourceLocation fireType, Supplier<T> supplier) {
      return registerParticle(fireType, FireManager.getComponentPath(fireType, Fire.Component.FLAME_PARTICLE), supplier);
   }

   public static <T extends SimpleParticleType> CobwebEntry<T> registerParticle(ResourceLocation fireType, String particleId, Supplier<T> supplier) {
      return CobwebRegistry.of(Registries.PARTICLE_TYPE, fireMod(fireType)).register(particleId, supplier);
   }

   public static Pair<CobwebEntry<CustomTorchBlock>, CobwebEntry<CustomWallTorchBlock>> registerTorch(ResourceLocation fireType) {
      return registerTorch(fireType, CustomTorchBlock::new, CustomWallTorchBlock::new);
   }

   public static <T extends CustomTorchBlock, W extends CustomWallTorchBlock> Pair<CobwebEntry<T>, CobwebEntry<W>> registerTorch(
      ResourceLocation fireType,
      TriFunction<ResourceLocation, Supplier<SimpleParticleType>, Properties, T> torchSupplier,
      TriFunction<ResourceLocation, Supplier<SimpleParticleType>, Properties, W> wallTorchSupplier
   ) {
      return registerTorch(
         fireType,
         FireManager.getComponentPath(fireType, Fire.Component.TORCH_BLOCK),
         FireManager.getComponentPath(fireType, Fire.Component.WALL_TORCH_BLOCK),
         FireManager.getComponentPath(fireType, Fire.Component.FLAME_PARTICLE),
         torchSupplier,
         (res, sup, id, props) -> (W)wallTorchSupplier.apply(res, sup, props)
      );
   }

   public static <T extends CustomTorchBlock, W extends CustomWallTorchBlock> Pair<CobwebEntry<T>, CobwebEntry<W>> registerTorch(
      ResourceLocation fireType,
      String torchId,
      String wallTorchId,
      String particleId,
      TriFunction<ResourceLocation, Supplier<SimpleParticleType>, Properties, T> torchSupplier,
      QuadriFunction<ResourceLocation, Supplier<SimpleParticleType>, String, Properties, W> wallTorchSupplier
   ) {
      CobwebRegister<Block> blocks = CobwebRegistry.ofBlocks(fireMod(fireType));
      Supplier<SimpleParticleType> particle = () -> FireManager.getRequiredComponent(fireType, Fire.Component.FLAME_PARTICLE, particleId);
      return Pair.of(
         blocks.<T>register(torchId, () -> torchSupplier.apply(fireType, particle, Properties.of())),
         blocks.<W>register(wallTorchId, () -> wallTorchSupplier.apply(fireType, particle, torchId, Properties.of()))
      );
   }

   public static CobwebEntry<StandingAndWallBlockItem> registerTorchItem(ResourceLocation fireType) {
      return registerTorchItem(fireType, (torch, wallTorch, properties) -> new StandingAndWallBlockItem(torch, wallTorch, properties, Direction.DOWN));
   }

   public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(
      ResourceLocation fireType, TriFunction<Block, Block, net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return registerTorchItem(fireType, constructor, new net.minecraft.world.item.Item.Properties());
   }

   public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(
      ResourceLocation fireType,
      TriFunction<Block, Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return registerTorchItem(
         fireType,
         FireManager.getComponentPath(fireType, Fire.Component.TORCH_ITEM),
         FireManager.getComponentPath(fireType, Fire.Component.TORCH_BLOCK),
         FireManager.getComponentPath(fireType, Fire.Component.WALL_TORCH_BLOCK),
         constructor,
         properties
      );
   }

   public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(
      ResourceLocation fireType,
      String itemId,
      String torchId,
      String wallTorchId,
      TriFunction<Block, Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return CobwebRegistry.ofItems(fireMod(fireType))
         .<T>register(
            itemId,
            () -> constructor.apply(
               FireManager.getRequiredComponent(fireType, Fire.Component.TORCH_BLOCK, torchId),
               FireManager.getRequiredComponent(fireType, Fire.Component.WALL_TORCH_BLOCK, wallTorchId),
               properties
            )
         );
   }

   public static CobwebEntry<CustomLanternBlock> registerLantern(ResourceLocation fireType) {
      return registerLantern(fireType, CustomLanternBlock::new);
   }

   public static <T extends CustomLanternBlock> CobwebEntry<T> registerLantern(
      ResourceLocation fireType, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return registerLantern(fireType, FireManager.getComponentPath(fireType, Fire.Component.LANTERN_BLOCK), constructor);
   }

   public static <T extends CustomLanternBlock> CobwebEntry<T> registerLantern(
      ResourceLocation fireType, String blockId, BiFunction<ResourceLocation, Properties, T> constructor
   ) {
      return CobwebRegistry.ofBlocks(fireMod(fireType)).register(blockId, () -> constructor.apply(fireType, Properties.of()));
   }

   public static CobwebEntry<BlockItem> registerLanternItem(ResourceLocation fireType) {
      return registerLanternItem(fireType, BlockItem::new);
   }

   public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(
      ResourceLocation fireType, BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return registerLanternItem(fireType, constructor, new net.minecraft.world.item.Item.Properties());
   }

   public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(
      ResourceLocation fireType,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return registerLanternItem(
         fireType,
         FireManager.getComponentPath(fireType, Fire.Component.LANTERN_ITEM),
         FireManager.getComponentPath(fireType, Fire.Component.LANTERN_BLOCK),
         constructor,
         properties
      );
   }

   public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(
      ResourceLocation fireType,
      String itemId,
      String blockId,
      BiFunction<Block, net.minecraft.world.item.Item.Properties, T> constructor,
      net.minecraft.world.item.Item.Properties properties
   ) {
      return CobwebRegistry.ofItems(fireMod(fireType))
         .register(itemId, () -> constructor.apply(FireManager.getRequiredComponent(fireType, Fire.Component.LANTERN_BLOCK, blockId), properties));
   }

   public static CobwebEntry<FireChargeItem> registerFireCharge(ResourceLocation fireType) {
      return registerFireCharge(fireType, FireChargeItem::new);
   }

   public static <T extends FireChargeItem> CobwebEntry<T> registerFireCharge(
      ResourceLocation fireType, Function<net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      return registerFireCharge(fireType, FireManager.getComponentPath(fireType, Fire.Component.FIRE_CHARGE_ITEM), constructor);
   }

   public static <T extends FireChargeItem> CobwebEntry<T> registerFireCharge(
      ResourceLocation fireType, String itemId, Function<net.minecraft.world.item.Item.Properties, T> constructor
   ) {
      CobwebEntry<T> charge = CobwebRegistry.ofItems(fireMod(fireType))
         .register(itemId, () -> constructor.apply(new net.minecraft.world.item.Item.Properties()));
      FIRE_CHARGE_TAGS.add(new Supplier[]{() -> DynamicTagBuilder.of(Registries.ITEM, new TagKey[]{ItemTags.CREEPER_IGNITERS}).addElement((Item)charge.get())});
      if (Services.PLATFORM.getPlatformName() == Platform.FABRIC) {
         DispenserBlock.registerProjectileBehavior((ItemLike)charge.get());
      }

      return charge;
   }

   private static String fireMod(ResourceLocation fireType) {
      return fireType.getNamespace();
   }
}
