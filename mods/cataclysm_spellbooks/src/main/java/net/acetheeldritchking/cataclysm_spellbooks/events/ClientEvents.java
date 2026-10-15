package net.acetheeldritchking.cataclysm_spellbooks.events;

import net.acetheeldritchking.cataclysm_spellbooks.registries.ItemRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent.OverlayType;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "cataclysm_spellbooks",
   value = {Dist.CLIENT}
)
public class ClientEvents {
   @SubscribeEvent
   public static void onFogDensity(RenderFog event) {
      FogType fogType = event.getCamera().getFluidInCamera();
      ItemStack itemStack = Minecraft.getInstance().player.getInventory().getArmor(3);
      if (itemStack.is((Item)ItemRegistries.IGNITIUM_WIZARD_HELMET.get()) && fogType == FogType.LAVA) {
         event.setNearPlaneDistance(0.0F);
         event.setFarPlaneDistance((float)event.getRenderer().getLastViewDistance() * 0.5F);
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void renderBlockScreenEvent(RenderBlockScreenEffectEvent event) {
      ItemStack itemStack = Minecraft.getInstance().player.getInventory().getArmor(3);
      if (itemStack.is((Item)ItemRegistries.IGNITIUM_WIZARD_HELMET.get())
         && Minecraft.getInstance().player.isInLava()
         && event.getOverlayType() == OverlayType.FIRE) {
         event.setCanceled(true);
      }
   }
}
