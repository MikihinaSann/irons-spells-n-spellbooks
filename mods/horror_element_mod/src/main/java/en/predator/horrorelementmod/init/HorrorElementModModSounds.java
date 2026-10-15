package en.predator.horrorelementmod.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HorrorElementModModSounds {
   public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, "horror_element_mod");
   public static final DeferredHolder<SoundEvent, SoundEvent> KIDS_LAUGHING = REGISTRY.register(
      "kids_laughing", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "kids_laughing"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> BREAKING_GLASS = REGISTRY.register(
      "breaking_glass", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "breaking_glass"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> LABORATORY_ALARM = REGISTRY.register(
      "laboratory_alarm", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "laboratory_alarm"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> HEART_BEATING = REGISTRY.register(
      "heart_beating", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "heart_beating"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> ACOUPHENE = REGISTRY.register(
      "acouphene", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "acouphene"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> PREDATOR_CLICKING_NOISE = REGISTRY.register(
      "predator_clicking_noise",
      () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "predator_clicking_noise"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> SCREAMER = REGISTRY.register(
      "screamer", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "screamer"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WINDOW1 = REGISTRY.register(
      "window1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "window1"))
   );
   public static final DeferredHolder<SoundEvent, SoundEvent> WINDOW2 = REGISTRY.register(
      "window2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("horror_element_mod", "window2"))
   );
}
