package com.somake.somakespells.spells.symmetry;

import com.github.L_Ender.cataclysm.init.ModSounds;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SoulGrabControllerEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulGrabSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "soul_grab");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.symmetrySchoolOrIce();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(8)
      .setCooldownSeconds(15.0)
      .build();

   public SoulGrabSpell() {
      this.manaCostPerLevel = 5;
      this.baseManaCost = 30;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 2;
      this.castTime = 15;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.MALEDICTUS_LEAP.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, caster, playerMagicData, this, 24, 0.35F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            SoulGrabControllerEntity controller = new SoulGrabControllerEntity(
               (EntityType<?>)ModEntityTypes.SOUL_GRAB_CONTROLLER.get(), level, caster, targetEntity, this.getDamage(spellLevel, caster)
            );
            level.addFreshEntity(controller);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
