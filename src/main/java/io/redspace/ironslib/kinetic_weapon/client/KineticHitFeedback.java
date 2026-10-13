package io.redspace.ironslib.kinetic_weapon.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

public final class KineticHitFeedback {
   private static final long FORGET_AFTER_TICKS = 200L;
   private static final Map<Integer, Long> LAST_HIT_TIME = new HashMap<>();

   public static void onKineticHit(int attackerId, Optional<Holder<SoundEvent>> hitSound) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         long now = minecraft.level.getGameTime();
         Long last = LAST_HIT_TIME.get(attackerId);
         if (last == null || now - last > 10L) {
            LAST_HIT_TIME.put(attackerId, now);
            forgetStaleEntries(now);
            Entity attacker = minecraft.level.getEntity(attackerId);
            if (attacker != null) {
               hitSound.ifPresent(sound -> minecraft.level.playLocalSound(attacker, (SoundEvent)sound.value(), attacker.getSoundSource(), 1.0F, 1.0F));
            }
         }
      }
   }

   public static float ticksSinceHit(Entity entity, float partialTick) {
      Long last = LAST_HIT_TIME.get(entity.getId());
      return last != null && entity.level() != null ? (float)(entity.level().getGameTime() - last) + partialTick : Float.MAX_VALUE;
   }

   public static void reset() {
      LAST_HIT_TIME.clear();
   }

   private static void forgetStaleEntries(long now) {
      LAST_HIT_TIME.values().removeIf(stamp -> now - stamp > 200L);
   }
}
