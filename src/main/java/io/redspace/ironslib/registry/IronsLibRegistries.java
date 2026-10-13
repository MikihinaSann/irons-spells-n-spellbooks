package io.redspace.ironslib.registry;

import io.redspace.ironslib.kinetic_weapon.AttackRange;
import io.redspace.ironslib.kinetic_weapon.KineticWeapon;
import io.redspace.ironslib.kinetic_weapon.UseEffects;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.LegacyPlayerStatueBlock;
import io.redspace.ironslib.statue.block.statue_block.player.LegacyPlayerStatueBlockEntity;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlock;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockItem;
import io.redspace.ironslib.statue.worldgen.RandomizePlayerStatueProcessor;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.block.transmog_table.LegacyTransmogTableBlock;
import io.redspace.ironslib.transmog.block.transmog_table.TransmogTableBlock;
import io.redspace.ironslib.transmog.block.transmog_table.TransmogTableMenu;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IronsLibRegistries {
   public static void register(IEventBus eventBus) {
      IronsLibRegistries.ItemRegistry.ITEMS.register(eventBus);
      IronsLibRegistries.BlockRegistry.BLOCKS.register(eventBus);
      IronsLibRegistries.BlockRegistry.BLOCK_ENTITIES.register(eventBus);
      IronsLibRegistries.ComponentRegistry.COMPONENTS.register(eventBus);
      IronsLibRegistries.MenuRegistry.MENUS.register(eventBus);
      IronsLibRegistries.SoundRegistry.SOUND_EVENTS.register(eventBus);
      IronsLibRegistries.StructureProcessorRegistry.STRUCTURE_PROCESSORS.register(eventBus);
      IronsLibRegistries.AttributeRegistry.ATTRIBUTES.register(eventBus);
      IronsLibRegistries.Legacy.ItemRegistry.ITEMS.register(eventBus);
      IronsLibRegistries.Legacy.BlockRegistry.BLOCKS.register(eventBus);
      IronsLibRegistries.Legacy.BlockRegistry.BLOCK_ENTITIES.register(eventBus);
      IronsLibRegistries.Legacy.ComponentRegistry.COMPONENTS.register(eventBus);
      IronsLibRegistries.Legacy.StructureProcessorRegistry.STRUCTURE_PROCESSORS.register(eventBus);
   }

   @EventBusSubscriber(
      modid = "irons_lib"
   )
   public static final class AttributeRegistry {
      private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, "irons_lib");
      public static final DeferredHolder<Attribute, Attribute> ARMOR_PIERCE = registerRanged("armor_pierce");
      public static final DeferredHolder<Attribute, Attribute> MINING_SPEED = registerPercent("mining_speed");
      public static final DeferredHolder<Attribute, Attribute> EXPERIENCE_GAINED = registerPercent("experience_gained");
      public static final DeferredHolder<Attribute, Attribute> ARROW_DAMAGE = registerPercent("arrow_damage");
      public static final DeferredHolder<Attribute, Attribute> PROJECTILE_DAMAGE = registerPercent("projectile_damage");
      public static final DeferredHolder<Attribute, Attribute> CRIT_DAMAGE = registerPercent("crit_damage");
      public static final DeferredHolder<Attribute, Attribute> DODGE_CHANCE = registerPercent("dodge_chance");
      public static final DeferredHolder<Attribute, Attribute> HEALING_RECEIVED = registerPercent("healing_received");

      private static DeferredHolder<Attribute, Attribute> registerPercent(String name) {
         String descriptionId = "attribute.name.irons_lib." + name;
         return ATTRIBUTES.register(name, () -> new PercentageAttribute(descriptionId, 1.0, 0.0, 1024.0).setSyncable(true));
      }

      private static DeferredHolder<Attribute, Attribute> registerRanged(String name) {
         String descriptionId = "attribute.name.irons_lib." + name;
         return ATTRIBUTES.register(name, () -> new RangedAttribute(descriptionId, 0.0, 0.0, 1024.0).setSyncable(true));
      }

      @SubscribeEvent
      public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
         event.getTypes().forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> {
               if (!event.has(entity, attribute)) {
                  event.add(entity, attribute);
               }
            }));
      }
   }

   public static class BlockRegistry {
      private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "irons_lib");
      private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "irons_lib");
      public static final DeferredHolder<Block, Block> TRANSMOG_TABLE = BLOCKS.register(
         "transmog_table", () -> new TransmogTableBlock(Properties.ofFullCopy(Blocks.CRAFTING_TABLE))
      );
      public static final DeferredHolder<Block, Block> PLAYER_STATUE_BLOCK = BLOCKS.register("player_statue", PlayerStatueBlock::new);
      public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlayerStatueBlockEntity>> STATUE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
         "statue",
         () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(PlayerStatueBlockEntity::new, new Block[]{(Block)PLAYER_STATUE_BLOCK.get()})
               .build(null)
      );
   }

   public static class ComponentRegistry {
      private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "irons_lib");
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<TransmogItemData>> TRANSMOG = register(
         "transmog", builder -> builder.persistent(TransmogItemData.CODEC).networkSynchronized(TransmogItemData.STREAM_CODEC).cacheEncoding()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<StatueData>> STATUE_ITEM_DATA = register(
         "statue_item_data", builder -> builder.persistent(StatueData.CODEC).cacheEncoding()
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<KineticWeapon>> KINETIC_WEAPON = register(
         "kinetic_weapon", builder -> builder.persistent(KineticWeapon.CODEC).networkSynchronized(KineticWeapon.STREAM_CODEC)
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<UseEffects>> USE_EFFECTS = register(
         "use_effects", builder -> builder.persistent(UseEffects.CODEC).networkSynchronized(UseEffects.STREAM_CODEC)
      );
      public static final DeferredHolder<DataComponentType<?>, DataComponentType<AttackRange>> ATTACK_RANGE = register(
         "attack_range", builder -> builder.persistent(AttackRange.CODEC).networkSynchronized(AttackRange.STREAM_CODEC)
      );

      private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String pName, UnaryOperator<Builder<T>> pBuilder) {
         return COMPONENTS.register(pName, () -> pBuilder.apply(DataComponentType.builder()).build());
      }
   }

   public static class ItemRegistry {
      private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "irons_lib");
      public static final DeferredHolder<Item, Item> PLAYER_STATUE_BLOCK_ITEM = ITEMS.register(
         "player_statue", () -> new PlayerStatueBlockItem(new net.minecraft.world.item.Item.Properties())
      );
      public static final DeferredHolder<Item, Item> TRANSMOG_TABLE_BLOCK_ITEM = ITEMS.register(
         "transmog_table", () -> new BlockItem((Block)IronsLibRegistries.BlockRegistry.TRANSMOG_TABLE.get(), new net.minecraft.world.item.Item.Properties())
      );
   }

   @Deprecated
   public static final class Legacy {
      @Deprecated
      public static class BlockRegistry {
         private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "irons_patreon_lib");
         private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "irons_patreon_lib");
         public static final DeferredHolder<Block, Block> TRANSMOG_TABLE = BLOCKS.register(
            "transmog_table", () -> new LegacyTransmogTableBlock(Properties.ofFullCopy(Blocks.CRAFTING_TABLE))
         );
         public static final DeferredHolder<Block, Block> PLAYER_STATUE_BLOCK = BLOCKS.register("player_statue", LegacyPlayerStatueBlock::new);
         public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LegacyPlayerStatueBlockEntity>> STATUE_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "statue",
            () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
                     LegacyPlayerStatueBlockEntity::new, new Block[]{(Block)PLAYER_STATUE_BLOCK.get()}
                  )
                  .build(null)
         );
      }

      @Deprecated
      public static class ComponentRegistry {
         private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "irons_patreon_lib");
         public static final DeferredHolder<DataComponentType<?>, DataComponentType<TransmogItemData>> TRANSMOG = register(
            "transmog", builder -> builder.persistent(TransmogItemData.CODEC).networkSynchronized(TransmogItemData.STREAM_CODEC).cacheEncoding()
         );
         public static final DeferredHolder<DataComponentType<?>, DataComponentType<StatueData>> STATUE_ITEM_DATA = register(
            "statue_item_data", builder -> builder.persistent(StatueData.CODEC).cacheEncoding()
         );

         private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String pName, UnaryOperator<Builder<T>> pBuilder) {
            return COMPONENTS.register(pName, () -> pBuilder.apply(DataComponentType.builder()).build());
         }
      }

      @Deprecated
      public static class ItemRegistry {
         private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "irons_patreon_lib");
      }

      @Deprecated
      public static class StructureProcessorRegistry {
         private static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS = DeferredRegister.create(
            Registries.STRUCTURE_PROCESSOR, "irons_patreon_lib"
         );
         public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<RandomizePlayerStatueProcessor>> RANDOMIZE_PLAYER_STATUE_PROCESSOR_TYPE = STRUCTURE_PROCESSORS.register(
            "randomize_player_statue", () -> () -> RandomizePlayerStatueProcessor.MAP_CODEC
         );
      }
   }

   public static class MenuRegistry {
      private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "irons_lib");
      public static final Supplier<MenuType<TransmogTableMenu>> TRANSMOG_TABLE_MENU = registerMenuType(TransmogTableMenu::new, "transmog_menu");

      private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
         return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
      }
   }

   public static class SoundRegistry {
      private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "irons_lib");
      public static DeferredHolder<SoundEvent, SoundEvent> TRANSMOG_TABLE_FORGE = registerSoundEvent("block.transmog_table.forge");

      private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
         return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("irons_lib", name)));
      }
   }

   public static class StructureProcessorRegistry {
      private static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS = DeferredRegister.create(
         Registries.STRUCTURE_PROCESSOR, "irons_lib"
      );
      public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<RandomizePlayerStatueProcessor>> RANDOMIZE_PLAYER_STATUE_PROCESSOR_TYPE = STRUCTURE_PROCESSORS.register(
         "randomize_player_statue", () -> () -> RandomizePlayerStatueProcessor.MAP_CODEC
      );
   }
}
