package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redspace.ironslib.soulfire.SoulFireHelper;
import io.redspace.ironslib.soulfire.client.SoulFireMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ScreenEffectRenderer.class})
public class ScreenEffectRendererMixin {
   @ModifyExpressionValue(
      method = {"renderFire"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/resources/model/ModelBakery;FIRE_1:Lnet/minecraft/client/resources/model/Material;"
      )}
   )
   private static Material irons_lib$soulFireOverlay(Material original) {
      LocalPlayer player = Minecraft.getInstance().player;
      return player != null && SoulFireHelper.isSoulFire(player) ? SoulFireMaterials.SOUL_FIRE_1 : original;
   }
}
