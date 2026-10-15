package net.alshanex.alshanex_familiars.setup;

import net.alshanex.alshanex_familiars.entity.ArchmagePetEntity;
import net.alshanex.alshanex_familiars.entity.BardPetEntity;
import net.alshanex.alshanex_familiars.entity.ClericPetEntity;
import net.alshanex.alshanex_familiars.entity.DragonWarriorPetEntity;
import net.alshanex.alshanex_familiars.entity.DruidPetEntity;
import net.alshanex.alshanex_familiars.entity.FrostlingPetEntity;
import net.alshanex.alshanex_familiars.entity.HunterPetEntity;
import net.alshanex.alshanex_familiars.entity.IllusionistPetEntity;
import net.alshanex.alshanex_familiars.entity.MagePetEntity;
import net.alshanex.alshanex_familiars.entity.NecromancerPetEntity;
import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.alshanex.alshanex_familiars.entity.ScorcherPetEntity;
import net.alshanex.alshanex_familiars.entity.ShadowEntity;
import net.alshanex.alshanex_familiars.entity.SummonerPetEntity;
import net.alshanex.alshanex_familiars.entity.misc.BlueBirdEntity;
import net.alshanex.alshanex_familiars.entity.misc.FrozenEntity;
import net.alshanex.alshanex_familiars.entity.misc.SoulEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(
   modid = "alshanex_familiars",
   bus = Bus.MOD
)
public class CommonSetup {
   @SubscribeEvent
   public static void onAttributeCreate(EntityAttributeCreationEvent event) {
      event.put((EntityType)EntityRegistry.MAGE_PET.get(), MagePetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.ARCHMAGE_PET.get(), ArchmagePetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.SUMMONER_PET.get(), SummonerPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.NECROMANCER_PET.get(), NecromancerPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.HUNTER_PET.get(), HunterPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.DRUID_PET.get(), DruidPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.ILLUSIONIST_PET.get(), IllusionistPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.SCORCHER_PET.get(), ScorcherPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.CLERIC_PET.get(), ClericPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.PLAGUE_PET.get(), PlaguePetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.BARD_PET.get(), BardPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.FROSTLING_PET.get(), FrostlingPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.DRAGON_WARRIOR_PET.get(), DragonWarriorPetEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.SHADOW_SUMMON.get(), ShadowEntity.createAttributes().build());
      event.put((EntityType)EntityRegistry.DECOY.get(), AbstractSpellCastingPet.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.BLUE_BIRD.get(), BlueBirdEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.SOUL.get(), SoulEntity.prepareAttributes().build());
      event.put((EntityType)EntityRegistry.FROZEN_ENTITY.get(), FrozenEntity.frozenEntity().build());
   }
}
