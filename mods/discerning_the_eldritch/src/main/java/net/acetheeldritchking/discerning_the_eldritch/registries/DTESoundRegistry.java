package net.acetheeldritchking.discerning_the_eldritch.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTESoundRegistry {
   private static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(Registries.SOUND_EVENT, "discerning_the_eldritch");
   public static DeferredHolder<SoundEvent, SoundEvent> SILENCE_SPELL_CAST = registerSoundEvent("silence_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> CLAP_SPELL_CAST = registerSoundEvent("clap");
   public static DeferredHolder<SoundEvent, SoundEvent> ESOTERIC_EDGE_SLASH = registerSoundEvent("esoteric_edge_slash");
   public static DeferredHolder<SoundEvent, SoundEvent> ESOTERIC_EDGE_SLASH_2 = registerSoundEvent("esoteric_edge_slash_2");
   public static DeferredHolder<SoundEvent, SoundEvent> MEND_FLESH_SOUND = registerSoundEvent("mend_flesh");
   public static DeferredHolder<SoundEvent, SoundEvent> INSANITY_VOICES = registerSoundEvent("insanity_voices");
   public static DeferredHolder<SoundEvent, SoundEvent> GLACIAL_CLEAVE_FULL = registerSoundEvent("glacial_cleave_full");
   public static DeferredHolder<SoundEvent, SoundEvent> RIFT_WALKER_CAST = registerSoundEvent("rift_walker_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> SCULK_LANTERN_TOLL = registerSoundEvent("sculk_lantern_ominous_normal");
   public static DeferredHolder<SoundEvent, SoundEvent> SCULK_LANTERN_TOLL_SUCCESS = registerSoundEvent("sculk_lantern_ominous_success");
   public static DeferredHolder<SoundEvent, SoundEvent> ESOTERIC_STRIKE = registerSoundEvent("esoteric_strike");
   public static DeferredHolder<SoundEvent, SoundEvent> CRYSTALLINE_CARVER_BASE = registerSoundEvent("crystalline_carver_base");
   public static DeferredHolder<SoundEvent, SoundEvent> CRYSTALLINE_CARVER_FINISH = registerSoundEvent("crystalline_carver_finish");
   public static DeferredHolder<SoundEvent, SoundEvent> YMIR_SLASH = registerSoundEvent("ymir_slash");
   public static DeferredHolder<SoundEvent, SoundEvent> RAZOR_UNSHEATHED = registerSoundEvent("razor_unsheathed");
   public static DeferredHolder<SoundEvent, SoundEvent> RAZOR_SHEATHED = registerSoundEvent("razor_sheathed");
   public static DeferredHolder<SoundEvent, SoundEvent> MAGIC_SWORD_CAST = registerSoundEvent("magic_sword_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> DEVOURER_WRETCH = registerSoundEvent("devourer_wretch");
   public static DeferredHolder<SoundEvent, SoundEvent> DARK_MAGIC_SWORD_CAST = registerSoundEvent("dark_magic_sword_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> DARK_MAGIC_CHARGE_1 = registerSoundEvent("dark_magic_charge_1");
   public static DeferredHolder<SoundEvent, SoundEvent> SOUL_SLAM = registerSoundEvent("soul_slam");
   public static DeferredHolder<SoundEvent, SoundEvent> SOUL_SLAM_ECHO = registerSoundEvent("soul_slam_echo");
   public static DeferredHolder<SoundEvent, SoundEvent> MAGIC_SWORD_BLOOD_CAST = registerSoundEvent("magic_sword_blood_cast");
   public static DeferredHolder<SoundEvent, SoundEvent> GAOLER_HURT = registerSoundEvent("entity.gaoler.hurt");
   public static DeferredHolder<SoundEvent, SoundEvent> GAOLER_DEATH = registerSoundEvent("entity.gaoler.death");
   public static DeferredHolder<SoundEvent, SoundEvent> GAOLER_STEP = registerSoundEvent("entity.gaoler.step");
   public static DeferredHolder<SoundEvent, SoundEvent> GAOLER_AMBIENT = registerSoundEvent("entity.gaoler.ambient");
   public static DeferredHolder<SoundEvent, SoundEvent> GAOLER_ATTACK_IMPACT = registerSoundEvent("entity.gaoler.attack_impact");
   public static DeferredHolder<SoundEvent, SoundEvent> GAOLER_ROAR = registerSoundEvent("entity.gaoler.roar");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_HURT = registerSoundEvent("entity.ascended_one.hurt");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_DEATH = registerSoundEvent("entity.ascended_one.death");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_TAUNT_ONE = registerSoundEvent("entity.ascended_one.taunt_1");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_TAUNT_TWO = registerSoundEvent("entity.ascended_one.taunt_2");
   public static DeferredHolder<SoundEvent, SoundEvent> TEST_BOSS_MUSIC = registerSoundEvent("test_boss_music");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_THEME = registerSoundEvent("ascended_one_theme");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_TRANSITION = registerSoundEvent("ascended_one_transition");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_FINAL_PHASE = registerSoundEvent("ascended_one_final_phase");
   public static DeferredHolder<SoundEvent, SoundEvent> ASCENDED_ONE_FINAL_PHASE_FULL = registerSoundEvent("ascended_one_final_phase_full");

   private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
      return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", name)));
   }

   public static void register(IEventBus eventBus) {
      SOUND_EVENT.register(eventBus);
   }
}
