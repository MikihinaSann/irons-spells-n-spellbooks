package net.acetheeldritchking.discerning_the_eldritch.registries;

import net.acetheeldritchking.discerning_the_eldritch.effects.AbracadabraPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.AccursedPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.BloodRotPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.FrostbitePotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.MalignantBurnEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.MendFleshPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.MetaphysicalPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.PortentEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.PredatorPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.PreyPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.ScorchedSoulPotionEffect;
import net.acetheeldritchking.discerning_the_eldritch.effects.SilencePotionEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTEPotionEffectRegistry {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "discerning_the_eldritch");
   public static final DeferredHolder<MobEffect, MobEffect> SILENCE_POTION_EFFECT = MOB_EFFECTS.register("silence_potion_effect", SilencePotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> METAPHYSICAL_POTION_EFFECT = MOB_EFFECTS.register(
      "metaphysical_potion_effect", MetaphysicalPotionEffect::new
   );
   public static final DeferredHolder<MobEffect, MobEffect> MEND_FLESH_EFFECT = MOB_EFFECTS.register("mend_flesh_potion_effect", MendFleshPotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> ABRACADABRA_EFFECT = MOB_EFFECTS.register("abracadabra_potion_effect", AbracadabraPotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> PORTENT_EFFECT = MOB_EFFECTS.register("portent_effect", PortentEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> FROSTBITE_EFFECT = MOB_EFFECTS.register("frostbite_effect", FrostbitePotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> MALIGNANT_BURN_EFFECT = MOB_EFFECTS.register(
      "malignant_burn_potion_effect", MalignantBurnEffect::new
   );
   public static final DeferredHolder<MobEffect, MobEffect> ACCURSED_EFFECT = MOB_EFFECTS.register("accursed_potion_effect", AccursedPotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> BLOOD_ROT_EFFECT = MOB_EFFECTS.register("blood_rot_potion_effect", BloodRotPotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> PREY_POTION_EFFECT = MOB_EFFECTS.register("prey_potion_effect", PreyPotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> PREDATOR_POTION_EFFECT = MOB_EFFECTS.register("predator_potion_effect", PredatorPotionEffect::new);
   public static final DeferredHolder<MobEffect, MobEffect> SCORCHED_SOUL_EFFECT = MOB_EFFECTS.register(
      "scorched_soul_potion_effect", ScorchedSoulPotionEffect::new
   );

   public static void register(IEventBus eventBus) {
      MOB_EFFECTS.register(eventBus);
   }
}
