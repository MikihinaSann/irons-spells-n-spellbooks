package com.somake.somakespells.mixin;

import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class SomakeMixinPlugin implements IMixinConfigPlugin {
   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.contains("FallingSoulBladeEntityMixin")
         || mixinClassName.contains("SoulPillarEntityMixin")
         || mixinClassName.contains("SoulPillarExplosionEntityMixin")) {
         return FMLLoader.getLoadingModList().getModFileById("legendary_monsters") != null;
      } else if (mixinClassName.contains("PlayerFamiliarDataMixin") || mixinClassName.contains("FamiliarManagerMixin")) {
         return FMLLoader.getLoadingModList().getModFileById("familiarslib") != null
            && FMLLoader.getLoadingModList().getModFileById("alshanex_familiars") != null;
      } else if (mixinClassName.contains("KrampusAttackProcedureMixin")
         || mixinClassName.contains("KrampusHenchmanAttackProcedureMixin")
         || mixinClassName.contains("MissionerPriRanieniiSushchnostiProcedureMixin")
         || mixinClassName.contains("MissionaryRaiderHitProcedureMixin")) {
         return FMLLoader.getLoadingModList().getModFileById("born_in_chaos_v1") != null;
      } else {
         return !mixinClassName.contains("StonePersistenceProcedureMixin") && !mixinClassName.contains("TransformingEasterCakeProcedureMixin")
            ? true
            : FMLLoader.getLoadingModList().getModFileById("born_in_chaos_v1") != null;
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
