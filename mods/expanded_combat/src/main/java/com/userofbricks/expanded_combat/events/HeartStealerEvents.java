package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.init.DataAttachments;
import com.userofbricks.expanded_combat.item.HeartStealerItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;

@EventBusSubscriber(
   modid = "expanded_combat"
)
public class HeartStealerEvents {
   @SubscribeEvent
   public static void playerHeartPenalties(Pre event) {
      Player player = event.getEntity();
      float stolenHearts = ((Integer)player.getData(DataAttachments.STOLEN_HEALTH)).intValue() / 2.0F;
      int time = Math.round((float)(player.level().getDayTime() % 24000L));
      if (!player.getInventory().hasAnyMatching(stack -> stack.getItem() instanceof HeartStealerItem)
         && (stolenHearts >= 50.0F && time % 100 == 0 || stolenHearts >= 25.0F && time % 200 == 0 || stolenHearts >= 5.0F && time % 400 == 0)) {
         player.setData(DataAttachments.STOLEN_HEALTH, (Integer)player.getData(DataAttachments.STOLEN_HEALTH) - 1);
         player.hurt(player.damageSources().magic(), 1.0F);
      }

      if (player.level().canSeeSky(player.blockPosition())
         && !player.level().isNight()
         && (
            stolenHearts >= 40.0F && time % 20 == 0
               || stolenHearts >= 30.0F && time % 40 == 0
               || stolenHearts >= 20.0F && time % 80 == 0
               || stolenHearts >= 10.0F && time % 160 == 0
         )) {
         player.hurt(player.damageSources().onFire(), 2.0F);
      }

      Level level = event.getEntity().level();
      Vec3 location = event.getEntity().position();
      RandomSource random = level.random;
      if (random.nextInt(0, 50) < (Integer)event.getEntity().getData(DataAttachments.STOLEN_HEALTH)) {
         level.addParticle(
            ParticleTypes.MYCELIUM,
            location.x + random.nextDouble() - 0.5,
            location.y + random.nextDouble() * 2.0,
            location.z + random.nextDouble() - 0.5,
            0.0,
            0.0,
            0.0
         );
      }
   }

   @SubscribeEvent
   public static void PlayerHearts(Post event) {
      Player player = event.getEntity();
      if (player instanceof ServerPlayer) {
         AttributeInstance attributeinstance = player.getAttribute(Attributes.MAX_HEALTH);

         assert attributeinstance != null;

         AttributeModifier stolen_modifier = attributeinstance.getModifier(ExpandedCombat.modLoc("heartstealer_stolen_modifier"));
         AttributeModifier added_modifier = attributeinstance.getModifier(ExpandedCombat.modLoc("heartstealer_added_modifier"));
         if (stolen_modifier == null || stolen_modifier.amount() != ((Integer)player.getData(DataAttachments.STOLEN_HEALTH)).intValue()) {
            attributeinstance.addOrUpdateTransientModifier(
               new AttributeModifier(
                  ExpandedCombat.modLoc("heartstealer_stolen_modifier"),
                  ((Integer)player.getData(DataAttachments.STOLEN_HEALTH)).intValue(),
                  Operation.ADD_VALUE
               )
            );
         }

         if (added_modifier == null || added_modifier.amount() != ((Integer)player.getData(DataAttachments.ADDED_HEALTH)).intValue()) {
            attributeinstance.addOrUpdateTransientModifier(
               new AttributeModifier(
                  ExpandedCombat.modLoc("heartstealer_added_modifier"), ((Integer)player.getData(DataAttachments.ADDED_HEALTH)).intValue(), Operation.ADD_VALUE
               )
            );
         }
      }
   }
}
