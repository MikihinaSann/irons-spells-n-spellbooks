package com.gametechbc.traveloptics.util;

import com.gametechbc.traveloptics.entity.summons.SummonedAptrgangr;
import com.gametechbc.traveloptics.entity.summons.SummonedEnderGolem;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedBerserker;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedRevenant;
import com.gametechbc.traveloptics.entity.summons.SummonedKobolediator;
import com.gametechbc.traveloptics.entity.summons.SummonedKoboleton;
import com.gametechbc.traveloptics.entity.summons.SummonedTheProwler;
import com.gametechbc.traveloptics.entity.summons.SummonedTheWatcher;
import com.gametechbc.traveloptics.entity.summons.SummonedWadjet;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import java.util.HashSet;
import java.util.Set;

public class SummonTypes {
   private static final Set<Class<? extends IMagicSummon>> KAIJU_SUMMONS = new HashSet<>();
   private static final Set<Class<? extends IMagicSummon>> MINIBOSS_SUMMONS = new HashSet<>();
   private static final Set<Class<? extends IMagicSummon>> GROUP_SUMMONS = new HashSet<>();

   public SummonTypes() {
      super();
   }

   public static void registerKaijuSummons() {
   }

   public static void registerMinibossSummons() {
      MINIBOSS_SUMMONS.add(SummonedIgnitedBerserker.class);
      MINIBOSS_SUMMONS.add(SummonedIgnitedRevenant.class);
      MINIBOSS_SUMMONS.add(SummonedKobolediator.class);
      MINIBOSS_SUMMONS.add(SummonedAptrgangr.class);
      MINIBOSS_SUMMONS.add(SummonedEnderGolem.class);
   }

   public static void registerGroupSummons() {
      GROUP_SUMMONS.add(SummonedWadjet.class);
      GROUP_SUMMONS.add(SummonedTheProwler.class);
      GROUP_SUMMONS.add(SummonedKoboleton.class);
      GROUP_SUMMONS.add(SummonedTheWatcher.class);
   }

   public static Set<Class<? extends IMagicSummon>> getKaijuSummons() {
      return KAIJU_SUMMONS;
   }

   public static Set<Class<? extends IMagicSummon>> getMinibossSummons() {
      return MINIBOSS_SUMMONS;
   }

   public static Set<Class<? extends IMagicSummon>> getGroupSummons() {
      return GROUP_SUMMONS;
   }
}
