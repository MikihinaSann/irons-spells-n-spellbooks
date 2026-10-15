package net.warphan.iss_magicfromtheeast.mixins.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.item.weapons.RepeatingCrossbow;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin({PlayerRenderer.class})
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public PlayerRendererMixin(Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
      super(p_174289_, p_174290_, p_174291_);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getArmPose"},
      cancellable = true
   )
   private static void S$getArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<ArmPose> callback) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (!player.swinging
         && itemStack.getItem() instanceof RepeatingCrossbow
         && !player.isUsingItem()
         && itemStack.has(MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT)
         && ((RepeatingCrossbow.ProjectileAmountComponent)itemStack.get(MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT)).ammoAmount() > 0) {
         callback.setReturnValue(ArmPose.CROSSBOW_HOLD);
      }
   }
}
