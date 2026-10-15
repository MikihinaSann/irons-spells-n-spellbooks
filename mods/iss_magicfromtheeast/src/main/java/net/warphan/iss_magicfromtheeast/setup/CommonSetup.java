package net.warphan.iss_magicfromtheeast.setup;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.warphan.iss_magicfromtheeast.configs.MFTEServerConfigs;
import net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands.BoneHandsEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner.JadeExecutionerEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi.JiangshiEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi.SummonedJiangshiEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.kitsune.SummonedKitsune;
import net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.onmyoji.OnmyojiEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.taoist.TaoistEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru.SpiritAshigaruEntity;
import net.warphan.iss_magicfromtheeast.entity.mobs.spirit_samurai.SpiritSamuraiEntity;
import net.warphan.iss_magicfromtheeast.entity.spells.spirit_challenging.ExtractedSoul;
import net.warphan.iss_magicfromtheeast.entity.spells.summoned_cloud.SummonCloudEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEUtils;

@EventBusSubscriber(
   modid = "iss_magicfromtheeast"
)
public class CommonSetup {
   @SubscribeEvent
   public static void onModConfigLoadingEvent(Loading event) {
      if (event.getConfig().getType() == Type.SERVER) {
         MFTESpellRegistries.onConfigReload();
         MFTEServerConfigs.onConfigReload();
      }
   }

   @SubscribeEvent
   public static void onModConfigReloadingEvent(Reloading event) {
      if (event.getConfig().getType() == Type.SERVER) {
         MFTESpellRegistries.onConfigReload();
         MFTEServerConfigs.onConfigReload();
      }
   }

   @SubscribeEvent
   public static void onAttributeCreate(EntityAttributeCreationEvent event) {
      event.put((EntityType)MFTEEntityRegistries.JIANGSHI.get(), JiangshiEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.SUMMONED_JIANGSHI.get(), SummonedJiangshiEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.JADE_EXECUTIONER.get(), JadeExecutionerEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.SUMMON_CLOUD_ENTITY.get(), SummonCloudEntity.createAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.EXTRACTED_SOUL.get(), ExtractedSoul.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.BONE_HAND_ENTITY.get(), BoneHandsEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.SUMMONED_KITSUNE.get(), SummonedKitsune.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.REVENANT.get(), SpiritSamuraiEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.ASHIGARU.get(), SpiritAshigaruEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.TAOIST.get(), TaoistEntity.prepareAttributes().build());
      event.put((EntityType)MFTEEntityRegistries.ONMYOJI.get(), OnmyojiEntity.prepareAttributes().build());
   }

   @SubscribeEvent
   public static void spawnPlacement(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)MFTEEntityRegistries.JIANGSHI.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         (type, serverLevelAccessor, spawnType, blockPos, random) -> MFTEUtils.checkMonsterSpawnRules(serverLevelAccessor, spawnType, blockPos, random),
         Operation.OR
      );
   }
}
