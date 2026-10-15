package com.legacy.dash;

import com.legacy.dash.network.PacketHandler;
import com.legacy.dash.network.c_to_s.DashVisualsPayload;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;

public class DashEntityEvents {
   byte cooldownInTicks = 0;

   @SubscribeEvent
   public void onKeyPressed(Key event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         Player player = mc.player;
         boolean currentStatus = isParCoolInactive();
         if (DashMod.DashClient.DASH_KEYBIND.isDown() && this.cooldownInTicks <= 0 && !player.isInWater() && player.getFallFlyingTicks() <= 10 && currentStatus
            )
          {
            PacketHandler.sendToServer(new DashVisualsPayload());
            float rot = (player.getYRot() + 90.0F) * (float) (Math.PI / 180.0);
            Vec3 playerLook = Vec3.ZERO.add(Mth.cos(rot), 0.0, Mth.sin(rot)).normalize().scale(1.2F);
            Vec3 dashVec = new Vec3(playerLook.x(), player.getDeltaMovement().y(), playerLook.z());
            player.setDeltaMovement(dashVec);
            this.cooldownInTicks = 50;
         }
      }
   }

   @SubscribeEvent
   public void onTick(Pre event) {
      if (this.cooldownInTicks > 0 && event.getEntity().level().isClientSide && event.getEntity().equals(Minecraft.getInstance().player)) {
         this.cooldownInTicks--;
      }
   }

   private static boolean isParCoolInactive() {
      try {
         Class<?> booleansClass = Class.forName("com.alrex.parcool.config.ParCoolConfig$Client$Booleans");
         Field isActiveField = booleansClass.getDeclaredField("ParCoolIsActive");
         isActiveField.setAccessible(true);
         Object valueRef = isActiveField.get(null);
         if (valueRef == null) {
            return true;
         }

         Method getMethod = valueRef.getClass().getMethod("get");
         if (getMethod.invoke(valueRef) instanceof Boolean active) {
            return !active;
         }
      } catch (ClassNotFoundException var6) {
      } catch (ReflectiveOperationException var7) {
      }

      return true;
   }
}
