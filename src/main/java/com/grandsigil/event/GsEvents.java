package com.grandsigil.event;

import com.grandsigil.entity.GlacierEntity;
import com.grandsigil.entity.TreeEntity;
import com.grandsigil.registry.GsEffects;
import com.grandsigil.spell.DomainSpell;
import io.redspace.ironsspellbooks.api.events.InscribeSpellEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(
   modid = "grandsigil"
)
public final class GsEvents {
   private static final double CANOPY = 48.0;
   private static final double FIELD = 72.0;

   private GsEvents() {
   }

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      LivingEntity dead = event.getEntity();
      if (!dead.level().isClientSide) {
         if (TreeEntity.anyStanding()) {
            ServerLevel level = (ServerLevel)dead.level();

            for (TreeEntity tree : level.getEntitiesOfClass(TreeEntity.class, dead.getBoundingBox().inflate(48.0), treex -> treex.covers(dead))) {
               if (tree.feed(level)) {
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onAttacked(LivingIncomingDamageEvent event) {
      LivingEntity target = event.getEntity();
      if (!target.level().isClientSide && GlacierEntity.anyStanding()) {
         ServerLevel level = (ServerLevel)target.level();
         Iterator var3 = level.getEntitiesOfClass(GlacierEntity.class, target.getBoundingBox().inflate(72.0), field -> field.seals(target)).iterator();
         if (var3.hasNext()) {
            GlacierEntity glacier = (GlacierEntity)var3.next();
            glacier.absorb(target, event.getAmount());
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onInscribe(InscribeSpellEvent event) {
      Player player = event.getEntity();
      if (player != null && event.getSpellData() != null) {
         AbstractSpell inscribing = event.getSpellData().getSpell();
         if (inscribing != null && ours(inscribing)) {
            ItemStack book = InscriptionTable.bookIn(player.containerMenu);
            if (!book.isEmpty()) {
               ISpellContainer container = ISpellContainer.get(book);
               if (container != null) {
                  for (SpellSlot slot : container.getActiveSpells()) {
                     AbstractSpell held = slot.getSpell();
                     if (held != null && ours(held) && !held.getSpellResource().equals(inscribing.getSpellResource()) && domain(held) == domain(inscribing)) {
                        event.setCanceled(true);
                        player.displayClientMessage(
                           Component.translatable(domain(inscribing) ? "ui.grandsigil.one_domain" : "ui.grandsigil.one_per_book", held.getDisplayName(player)),
                           true
                        );
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean ours(AbstractSpell spell) {
      return "grandsigil".equals(spell.getSpellResource().getNamespace());
   }

   private static boolean domain(AbstractSpell spell) {
      return spell instanceof DomainSpell;
   }

   @SubscribeEvent
   public static void onPreCast(SpellPreCastEvent event) {
      Player player = event.getEntity();
      if (player != null && GsEffects.transfixed(player)) {
         event.setCanceled(true);
      }
   }
}
