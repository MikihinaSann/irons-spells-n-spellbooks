package net.alshanex.alshanex_familiars.event;

import net.alshanex.alshanex_familiars.AlshanexFamiliarsMod;
import net.alshanex.alshanex_familiars.datafix.DataFixerHelpers;
import net.alshanex.alshanex_familiars.datafix.DataFixerStorage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Load;

@EventBusSubscriber(
   modid = "alshanex_familiars"
)
public class DataFixerEvents {
   @SubscribeEvent
   public static void onWorldLoad(Load event) {
      if (!event.getLevel().isClientSide()) {
         if (DataFixerStorage.INSTANCE != null && DataFixerStorage.INSTANCE.needsUpgrade()) {
            AlshanexFamiliarsMod.LOGGER.debug("Familiars: Initializing data migration " + DataFixerStorage.INSTANCE.getDataVersion() + " a 1");
         }
      }
   }

   @SubscribeEvent
   public static void onChunkLoad(net.neoforged.neoforge.event.level.ChunkEvent.Load event) {
      if (!event.getLevel().isClientSide()) {
         if (DataFixerStorage.INSTANCE != null && DataFixerStorage.INSTANCE.needsUpgrade()) {
            if (event.getChunk() instanceof LevelChunk chunk) {
               chunk.getBlockEntities().forEach((pos, blockEntity) -> {
                  RegistryAccess registryAccess = event.getLevel().registryAccess();
                  CompoundTag blockEntityTag = blockEntity.saveWithoutMetadata(registryAccess);
                  if (DataFixerHelpers.doFixUps(blockEntityTag)) {
                     blockEntity.loadWithComponents(blockEntityTag, registryAccess);
                     blockEntity.setChanged();
                  }
               });
               AlshanexFamiliarsMod.LOGGER.debug("Familiars: Processed data migration in chunks");
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityJoin(EntityJoinLevelEvent event) {
      if (!event.getLevel().isClientSide()) {
         if (DataFixerStorage.INSTANCE != null && DataFixerStorage.INSTANCE.needsUpgrade()) {
            fixEntity(event.getEntity());
            AlshanexFamiliarsMod.LOGGER.debug("Familiars: Migrated data from entity " + event.getEntity());
         }
      }
   }

   private static void fixEntity(Entity entity) {
      RegistryAccess registryAccess = entity.level().registryAccess();
      CompoundTag entityTag = new CompoundTag();
      entity.save(entityTag);
      if (DataFixerHelpers.doFixUps(entityTag)) {
         entity.load(entityTag);
      }
   }
}
