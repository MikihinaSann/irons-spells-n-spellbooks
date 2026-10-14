package com.gametechbc.traveloptics.setup;

import com.gametechbc.traveloptics.entity.summons.SummonedAptrgangr;
import com.gametechbc.traveloptics.entity.summons.SummonedDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedEliteDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedEnderGolem;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedBerserker;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedRevenant;
import com.gametechbc.traveloptics.entity.summons.SummonedKobolediator;
import com.gametechbc.traveloptics.entity.summons.SummonedKoboleton;
import com.gametechbc.traveloptics.entity.summons.SummonedRoyalDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedTheProwler;
import com.gametechbc.traveloptics.entity.summons.SummonedTheWatcher;
import com.gametechbc.traveloptics.entity.summons.SummonedWadjet;
import com.gametechbc.traveloptics.init.TOEntities;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD
)
public class TOEntityAttributes {
   public TOEntityAttributes() {
      super();
   }

   @SubscribeEvent
   public static void onAttributeCreate(EntityAttributeCreationEvent event) {
      event.put((EntityType)TOEntities.SUMMONED_KOBOLETON.get(), SummonedKoboleton.koboleton().build());
      event.put((EntityType)TOEntities.SUMMONED_KOBOLEDIATOR.get(), SummonedKobolediator.kobolediator().build());
      event.put((EntityType)TOEntities.SUMMONED_WADJET.get(), SummonedWadjet.wadjet().build());
      event.put((EntityType)TOEntities.SUMMONED_THE_PROWLER.get(), SummonedTheProwler.the_prowler().build());
      event.put((EntityType)TOEntities.SUMMONED_THE_WATCHER.get(), SummonedTheWatcher.the_watcher().build());
      event.put((EntityType)TOEntities.SUMMONED_IGNITED_BERSERKER.get(), SummonedIgnitedBerserker.ignited_berserker().build());
      event.put((EntityType)TOEntities.SUMMONED_IGNITED_REVENANT.get(), SummonedIgnitedRevenant.ignited_revenant().build());
      event.put((EntityType)TOEntities.SUMMONED_ENDER_GOLEM.get(), SummonedEnderGolem.ender_golem().build());
      event.put((EntityType)TOEntities.SUMMONED_DRAUGR.get(), SummonedDraugr.draugr().build());
      event.put((EntityType)TOEntities.SUMMONED_ROYAL_DRAUGR.get(), SummonedRoyalDraugr.royal_draugr().build());
      event.put((EntityType)TOEntities.SUMMONED_ELITE_DRAUGR.get(), SummonedEliteDraugr.elite_draugr().build());
      event.put((EntityType)TOEntities.SUMMONED_APTRGANGR.get(), SummonedAptrgangr.aptrgangr().build());
   }
}
