package net.warphan.iss_magicfromtheeast.util;

import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.Tags.Biomes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;

public class MFTEUtils {
   public static MFTEUtils.EnchantmentEffectComponents createEnchantmentEffectComponent(String modID) {
      return new MFTEUtils.EnchantmentEffectComponents(modID);
   }

   public static boolean checkMonsterSpawnRules(ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
      return !pLevel.getBiome(pPos).is(Biomes.NO_DEFAULT_MONSTERS)
         && pLevel.getDifficulty() != Difficulty.PEACEFUL
         && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom)
         && Monster.checkMobSpawnRules((EntityType)MFTEEntityRegistries.JIANGSHI.get(), pLevel, pSpawnType, pPos, pRandom);
   }

   public static class EnchantmentEffectComponents extends DeferredRegister<DataComponentType<?>> {
      protected EnchantmentEffectComponents(String name) {
         super(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, name);
      }

      public <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerComponentType(String name, UnaryOperator<Builder<T>> builder) {
         return this.register(name, () -> builder.apply(DataComponentType.builder()).build());
      }
   }
}
