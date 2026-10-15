package net.alshanex.tunes_n_tomes.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TSoundRegistry {
   private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "tunes_n_tomes");
   public static DeferredHolder<SoundEvent, SoundEvent> CLEF = registerSoundEvent("clef");
   public static DeferredHolder<SoundEvent, SoundEvent> HARP_WAVE = registerSoundEvent("harp_wave");
   public static DeferredHolder<SoundEvent, SoundEvent> HARP_SYMPHONY = registerSoundEvent("harp_symphony");
   public static DeferredHolder<SoundEvent, SoundEvent> HARP_EXPLOSION_CAST = registerSoundEvent("harp_explosion_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> HARP_EXPLOSION = registerSoundEvent("harp_explosion");
   public static DeferredHolder<SoundEvent, SoundEvent> ANGEL_CAST = registerSoundEvent("angel_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> HEALING_AURA = registerSoundEvent("healing_aura");
   public static DeferredHolder<SoundEvent, SoundEvent> ANGELIC_HARP = registerSoundEvent("angelic_harp");
   public static DeferredHolder<SoundEvent, SoundEvent> BIRD_CHIRP = registerSoundEvent("bird_chirp");
   public static DeferredHolder<SoundEvent, SoundEvent> BIRDS_CAST = registerSoundEvent("birds_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> BIRDS_SPAWN = registerSoundEvent("birds_spawn");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_1 = registerSoundEvent("bard_song_1");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_2 = registerSoundEvent("bard_song_2");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_3 = registerSoundEvent("bard_song_3");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_4 = registerSoundEvent("bard_song_4");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_5 = registerSoundEvent("bard_song_5");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_6 = registerSoundEvent("bard_song_6");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_7 = registerSoundEvent("bard_song_7");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_8 = registerSoundEvent("bard_song_8");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_9 = registerSoundEvent("bard_song_9");
   public static DeferredHolder<SoundEvent, SoundEvent> BARD_SONG_10 = registerSoundEvent("bard_song_10");
   public static DeferredHolder<SoundEvent, SoundEvent> DAL_SEGNO = registerSoundEvent("dal_segno");
   public static DeferredHolder<SoundEvent, SoundEvent> CRESCENDO = registerSoundEvent("crescendo");
   public static DeferredHolder<SoundEvent, SoundEvent> FORTISSIMO = registerSoundEvent("fortissimo");
   public static DeferredHolder<SoundEvent, SoundEvent> BALL_THROW = registerSoundEvent("ball_throw");
   public static DeferredHolder<SoundEvent, SoundEvent> HOPE_BURST = registerSoundEvent("hope_burst");
   public static DeferredHolder<SoundEvent, SoundEvent> FORTISSIMO_CHARGE = registerSoundEvent("fortissimo_charge");
   public static DeferredHolder<SoundEvent, SoundEvent> PIERCING_SOLO = registerSoundEvent("piercing_solo");

   public static void register(IEventBus eventBus) {
      SOUND_EVENTS.register(eventBus);
   }

   private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
      return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", name)));
   }
}
