package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.redspace.ironslib.soulfire.SoulFireHelper;
import io.redspace.ironslib.soulfire.client.SoulFireMaterials;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EntityRenderDispatcher.class})
public class SoulFireEntityRenderDispatcherMixin {
   @ModifyExpressionValue(
      method = {"renderFlame"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/resources/model/ModelBakery;FIRE_0:Lnet/minecraft/client/resources/model/Material;"
      )}
   )
   private Material irons_lib$soulFlame0(Material original, @Local(argsOnly = true) Entity entity) {
      return SoulFireHelper.isSoulFire(entity) ? SoulFireMaterials.SOUL_FIRE_0 : original;
   }

   @ModifyExpressionValue(
      method = {"renderFlame"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/resources/model/ModelBakery;FIRE_1:Lnet/minecraft/client/resources/model/Material;"
      )}
   )
   private Material irons_lib$soulFlame1(Material original, @Local(argsOnly = true) Entity entity) {
      return SoulFireHelper.isSoulFire(entity) ? SoulFireMaterials.SOUL_FIRE_1 : original;
   }
}
