package net.warphan.iss_magicfromtheeast.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MFTESoundRegistries {
   private static final DeferredRegister<SoundEvent> MFTESOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "iss_magicfromtheeast");
   public static DeferredHolder<SoundEvent, SoundEvent> SYMMETRY_CAST = registerSoundEvent("cast.generic.symmetry");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_CAST = registerSoundEvent("cast.generic.spirit");
   public static DeferredHolder<SoundEvent, SoundEvent> DUNE_CAST = registerSoundEvent("cast.generic.dune");
   public static DeferredHolder<SoundEvent, SoundEvent> LOONG_CAST = registerSoundEvent("cast.generic.loong");
   public static DeferredHolder<SoundEvent, SoundEvent> JADE_SWORD_IMPACT = registerSoundEvent("cast.generic.jade_sword_impact");
   public static DeferredHolder<SoundEvent, SoundEvent> JADE_DAO_IMPACT = registerSoundEvent("cast.generic.jade_dao_impact");
   public static DeferredHolder<SoundEvent, SoundEvent> JADE_DEFLECT = registerSoundEvent("cast.generic.jade_deflect");
   public static DeferredHolder<SoundEvent, SoundEvent> JADE_SLASH = registerSoundEvent("cast.generic.jade_slash");
   public static DeferredHolder<SoundEvent, SoundEvent> JADE_EMERGE = registerSoundEvent("cast.generic.jade_emerge");
   public static DeferredHolder<SoundEvent, SoundEvent> SOUL_CAST = registerSoundEvent("cast.generic.soul_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> BONE_HURT = registerSoundEvent("cast.generic.bone_hit");
   public static DeferredHolder<SoundEvent, SoundEvent> BONE_SLAM = registerSoundEvent("cast.generic.bone_slam");
   public static DeferredHolder<SoundEvent, SoundEvent> KATANA_WIND_UP = registerSoundEvent("cast.generic.katana_wind_up");
   public static DeferredHolder<SoundEvent, SoundEvent> SWORD_STRIKE = registerSoundEvent("cast.generic.sword_strike");
   public static DeferredHolder<SoundEvent, SoundEvent> DEATH_BELL = registerSoundEvent("cast.generic.death_bell");
   public static DeferredHolder<SoundEvent, SoundEvent> MAGIC_PROJECTILE_THROW = registerSoundEvent("cast.generic.projectile_throw");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_EXTRACTING = registerSoundEvent("cast.generic.soul_challenging");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_ARROW_SHOT = registerSoundEvent("cast.generic.spirit_arrow_shot");
   public static DeferredHolder<SoundEvent, SoundEvent> PROJECTILE_LOAD = registerSoundEvent("cast.generic.projectile_loading");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_INVOKING = registerSoundEvent("cast.generic.soul_rise");
   public static DeferredHolder<SoundEvent, SoundEvent> SOUL_CRUSHED = registerSoundEvent("cast.generic.soul_crushed");
   public static DeferredHolder<SoundEvent, SoundEvent> AXE_CHOP = registerSoundEvent("executioner.generic.axe_chop");
   public static DeferredHolder<SoundEvent, SoundEvent> AXE_SWEEP = registerSoundEvent("executioner.generic.axe_sweep");
   public static DeferredHolder<SoundEvent, SoundEvent> EXECUTIONER_AMBIENT = registerSoundEvent("executioner.generic.executioner_ambient");
   public static DeferredHolder<SoundEvent, SoundEvent> EXECUTIONER_HURT = registerSoundEvent("executioner.generic.executioner_hurt");
   public static DeferredHolder<SoundEvent, SoundEvent> ROAR = registerSoundEvent("executioner.generic.roar");
   public static DeferredHolder<SoundEvent, SoundEvent> EXECUTIONER_SHOWDOWN = registerSoundEvent("executioner.generic.showdown");
   public static DeferredHolder<SoundEvent, SoundEvent> ASHIGARU_SPEAR = registerSoundEvent("spiritmob.generic.ashigaru_spear");
   public static DeferredHolder<SoundEvent, SoundEvent> ASHIGARU_AMBIENT = registerSoundEvent("spiritmob.generic.ashigaru_voice");
   public static DeferredHolder<SoundEvent, SoundEvent> ASHIGARU_GUNSHOT = registerSoundEvent("spiritmob.generic.ashigaru_gun");
   public static DeferredHolder<SoundEvent, SoundEvent> SAMURAI_AMBIENT = registerSoundEvent("spiritmob.generic.samurai_ambient");
   public static DeferredHolder<SoundEvent, SoundEvent> SAMURAI_SLASH = registerSoundEvent("spiritmob.generic.samurai_slash");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_MOB_HURT = registerSoundEvent("spiritmob.generic.spirit_hurt");
   public static DeferredHolder<SoundEvent, SoundEvent> SPIRIT_MOB_DEATH = registerSoundEvent("spiritmob.generic.spirit_dead");

   public static void register(IEventBus eventBus) {
      MFTESOUND_EVENTS.register(eventBus);
   }

   private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
      return MFTESOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("iss_magicfromtheeast", name)));
   }
}
