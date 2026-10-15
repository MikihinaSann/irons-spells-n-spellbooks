package net.warphan.iss_magicfromtheeast.registries;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.warphan.iss_magicfromtheeast.spells.spirit.AnchoringKunaiSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.AshigaruSquadSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.BoneHandsSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.CalamityCutSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.KitsunePackSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.PhantomChargeSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.RevenantOfHonorSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.SoulBurstSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.SoulCatalystSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.SpiritChallengingSpell;
import net.warphan.iss_magicfromtheeast.spells.spirit.SplittingShurikenSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.BaguaArrayCircleSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.CloudRideSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.DragonGlideSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.DrapesOfReflectionSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.JadeBulletSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.JadeJudgementSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.JiangshiInvokeSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.NephriteSlashSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.PunishingHeavenSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.SwordDanceSpell;
import net.warphan.iss_magicfromtheeast.spells.symmetry.UnderworldAidSpell;

public class MFTESpellRegistries extends SpellRegistry {
   public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, "iss_magicfromtheeast");
   public static final Supplier<AbstractSpell> SWORD_DANCE_SPELL = registerSpell(new SwordDanceSpell());
   public static final Supplier<AbstractSpell> BAGUA_ARRAY_CIRCLE_SPELL = registerSpell(new BaguaArrayCircleSpell());
   public static final Supplier<AbstractSpell> DRAGON_GLIDE_SPELL = registerSpell(new DragonGlideSpell());
   public static final Supplier<AbstractSpell> JADE_JUDGEMENT_SPELL = registerSpell(new JadeJudgementSpell());
   public static final Supplier<AbstractSpell> JIANGSHI_INVOKE_SPELL = registerSpell(new JiangshiInvokeSpell());
   public static final Supplier<AbstractSpell> UNDERWORLD_AID_SPELL = registerSpell(new UnderworldAidSpell());
   public static final Supplier<AbstractSpell> PUNISHING_HEAVEN_SPELL = registerSpell(new PunishingHeavenSpell());
   public static final Supplier<AbstractSpell> DRAPES_OF_REFLECTION_SPELL = registerSpell(new DrapesOfReflectionSpell());
   public static final Supplier<AbstractSpell> CLOUD_RIDE_SPELL = registerSpell(new CloudRideSpell());
   public static final Supplier<AbstractSpell> NEPHRITE_SLASH_SPELL = registerSpell(new NephriteSlashSpell());
   public static final Supplier<AbstractSpell> JADE_BULLET_SPELL = registerSpell(new JadeBulletSpell());
   public static final Supplier<AbstractSpell> SOUL_CATALYST_SPELL = registerSpell(new SoulCatalystSpell());
   public static final Supplier<AbstractSpell> SOUL_BURST_SPELL = registerSpell(new SoulBurstSpell());
   public static final Supplier<AbstractSpell> SPIRIT_CHALLENGING = registerSpell(new SpiritChallengingSpell());
   public static final Supplier<AbstractSpell> BONE_HANDS_SPELL = registerSpell(new BoneHandsSpell());
   public static final Supplier<AbstractSpell> CALAMITY_CUT_SPELL = registerSpell(new CalamityCutSpell());
   public static final Supplier<AbstractSpell> KITSUNE_PACK_SPELL = registerSpell(new KitsunePackSpell());
   public static final Supplier<AbstractSpell> REVENANT_OF_HONOR_SPELL = registerSpell(new RevenantOfHonorSpell());
   public static final Supplier<AbstractSpell> ASHIGARU_SQUAD_SPELL = registerSpell(new AshigaruSquadSpell());
   public static final Supplier<AbstractSpell> PHANTOM_CHARGE_SPELL = registerSpell(new PhantomChargeSpell());
   public static final Supplier<AbstractSpell> ANCHORING_KUNAI = registerSpell(new AnchoringKunaiSpell());
   public static final Supplier<AbstractSpell> SPLITTING_SHURIKEN = registerSpell(new SplittingShurikenSpell());

   public static void register(IEventBus eventBus) {
      SPELLS.register(eventBus);
   }

   private static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
      return SPELLS.register(spell.getSpellName(), () -> spell);
   }
}
