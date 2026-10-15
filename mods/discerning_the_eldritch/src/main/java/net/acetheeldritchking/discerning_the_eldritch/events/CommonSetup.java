package net.acetheeldritchking.discerning_the_eldritch.events;

import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicAcolyteEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicCrusaderEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicSummonerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.apothic_cultists.ApothicTraitorEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistCaptainEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistMageEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.blood_cultists.BloodCultistWitchEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.ascended_one.AscendedOneBoss;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.bosses.minibosses.blood_matriarch.BloodMatriarchEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.eldritch_caster.TheApostleEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.electromancer.ElectromancerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw.SightlessMawEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.untold_behemoth.UntoldBehemothEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.razor_blade.RazorBlade;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(
   modid = "discerning_the_eldritch",
   bus = Bus.MOD
)
public class CommonSetup {
   @SubscribeEvent
   public static void onAttributeCreateEvent(EntityAttributeCreationEvent event) {
      event.put((EntityType)DTEEntityRegistry.APOTHIC_SUMMONER.get(), ApothicSummonerEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.APOTHIC_CRUSADER.get(), ApothicCrusaderEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.APOTHIC_ACOLYTE.get(), ApothicAcolyteEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.GAOLER_ENTITY.get(), GaolerEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.ASCENDED_ONE.get(), AscendedOneBoss.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.ASCENDED_ONE_CULTIST.get(), AscendedOneBoss.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.APOSTLE_ENTITY.get(), TheApostleEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.SIGHTLESS_MAW.get(), SightlessMawEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.UNTOLD_BEHEMOTH.get(), UntoldBehemothEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.APOTHIC_TRAITOR.get(), ApothicTraitorEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.RAZOR_BLADE.get(), RazorBlade.createLivingAttributes().build());
      event.put((EntityType)DTEEntityRegistry.BLOOD_CULTIST_CAPTAIN.get(), BloodCultistCaptainEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.ELECTROMANCER_MAGE.get(), ElectromancerEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.BLOOD_CULTIST_MAGE.get(), BloodCultistMageEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.BLOOD_CULTIST_WITCH.get(), BloodCultistWitchEntity.createAttributes().build());
      event.put((EntityType)DTEEntityRegistry.BLOOD_MATRIARCH.get(), BloodMatriarchEntity.createAttributes().build());
   }
}
