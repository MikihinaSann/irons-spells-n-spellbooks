package net.hazen.hazennstuff.Setup;

import com.mojang.datafixers.util.Either;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import net.hazen.hazennstuff.Registries.HnSDamageTypes;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.Tooltips.LightningClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.GatherComponents;

@EventBusSubscriber
public class HnSServerEvents {
   @SubscribeEvent
   public static void onPlayerCastEvent(SpellPreCastEvent event) {
      Player entity = event.getEntity();
      AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
      boolean hasHexedEffect = entity.hasEffect(HnSEffects.HEXED);
      if (entity instanceof ServerPlayer player && !player.level().isClientSide() && hasHexedEffect) {
         float percentDamage = 0.25F;
         float maxHealth = entity.getMaxHealth();
         float damage = maxHealth * percentDamage;
         damage = Math.max(1.0F, damage);
         DamageSource damageSource = new DamageSource(DamageSources.getHolderFromResource(entity, HnSDamageTypes.CORRUPT_MAGIC));
         entity.hurt(damageSource, damage);
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HnSSounds.BRIMSTONE_HELLBLAST_IMPACT, SoundSource.PLAYERS, 0.5F, 1.0F);
      }
   }

   @SubscribeEvent
   public static void onGatherTooltipComponents(GatherComponents event) {
      List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
      String marker = "⚡";

      for (int[] i = new int[]{0}; i[0] < elements.size(); i[0]++) {
         Either<FormattedText, TooltipComponent> element = elements.get(i[0]);
         element.left().ifPresent(text -> {
            String rawText = text.getString();
            if (rawText.contains(marker)) {
               int start = rawText.indexOf(marker);
               int end = rawText.lastIndexOf(marker);
               if (start != end) {
                  String prefix = rawText.substring(0, start);
                  String lightningPart = rawText.substring(start + 1, end);
                  String suffix = rawText.substring(end + 1);
                  elements.remove(i[0]);
                  if (!prefix.isEmpty()) {
                     elements.add(i[0], Either.left(Component.literal(prefix)));
                     i[0]++;
                  }

                  elements.add(i[0], Either.right(new LightningClientTooltipComponent.LightningTooltipData(Component.literal(lightningPart))));
                  i[0]++;
                  if (!suffix.isEmpty()) {
                     elements.add(i[0], Either.left(Component.literal(suffix)));
                  }
               }
            }
         });
      }
   }
}
