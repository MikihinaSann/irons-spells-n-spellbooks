package net.veroxuniverse.crystal_chronicles.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.resources.ResourceLocation;

@AutoSpellConfig
public class PrismaticPortalSpell extends AbstractSpell {
   private static final int BASE_MANA_COST = 50;
   private static final int DEFAULT_COOLDOWN = 600;
   public static final ResourceLocation SPELL_ID = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "prismatic_portal");

   public PrismaticPortalSpell() {
      this.baseManaCost = 50;
      this.castTime = 0;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
   }

   @Override
   public SchoolType getSchoolType() {
      return PrismaticSchools.PRISMATIC;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return SPELL_ID;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      DefaultConfig config = new DefaultConfig();
      config.setMinRarity(SpellRarity.LEGENDARY);
      config.setMaxLevel(1);
      config.setCooldownSeconds(30.0);
      config.setSchoolResource(PrismaticSchools.PRISMATIC_ID);
      return config.build();
   }

   @Override
   public CastType getCastType() {
      return CastType.INSTANT;
   }
}
