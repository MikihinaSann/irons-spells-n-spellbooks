package net.acetheeldritchking.cataclysm_spellbooks.events;

import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedAmethystCrab;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedAptrgangr;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedEliteDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedIgnitedBerserker;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedIgnitedRevenant;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedKoboldiator;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedKoboleton;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedPhantomRemnant;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedRoyalDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(
   modid = "cataclysm_spellbooks",
   bus = Bus.MOD
)
public class ModSetup {
   @SubscribeEvent
   public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
      event.put((EntityType)CSEntityRegistry.SUMMONED_IGNITED_REVENANT.get(), SummonedIgnitedRevenant.buildAttributes().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_IGNITED_BERSERKER.get(), SummonedIgnitedBerserker.buildAttributes().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_KOBOLDIATOR.get(), SummonedKoboldiator.buildAttributes().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_KOBOLETON.get(), SummonedKoboleton.koboleton().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_DRAUGUR.get(), SummonedDraugur.draugr().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_ROYAL_DRAUGUR.get(), SummonedRoyalDraugur.royal_draugr().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_ELITE_DRAUGUR.get(), SummonedEliteDraugur.elite_draugr().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_APTRGANGR.get(), SummonedAptrgangr.buildAttributes().build());
      event.put((EntityType)CSEntityRegistry.SUMMONED_AMETHYST_CRAB.get(), SummonedAmethystCrab.buildAttributes().build());
      event.put((EntityType)CSEntityRegistry.PHANTOM_ANCIENT_REMNANT.get(), SummonedPhantomRemnant.buildAttributes().build());
   }
}
