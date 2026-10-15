package com.snackpirate.aeromancy.spells.dash;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

@AutoSpellConfig
public class DashSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(2.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable(
            "ui.irons_spellbooks.strength",
            new Object[]{String.format("%s%%", (int)((15.0F + this.getSpellPower(spellLevel, caster)) / 12.0F * 100.0F / 2.08F))}
         )
      );
   }

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("dash");
   }

   public DashSpell() {
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.baseManaCost = 20;
      this.manaCostPerLevel = 2;
      this.castTime = 0;
   }

   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      if (castData instanceof ImpulseCastData bdcd) {
         entity.hasImpulse = bdcd.hasImpulse;
         entity.setDeltaMovement(entity.getDeltaMovement().add(bdcd.x, bdcd.y, bdcd.z));
      }

      super.onClientCast(level, spellLevel, entity, castData);
   }

   public ICastDataSerializable getEmptyCastData() {
      return new ImpulseCastData();
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.hasImpulse = true;
      float multiplier = (15.0F + this.getSpellPower(spellLevel, entity)) / 12.0F;
      Vec3 forward = entity.getLookAngle();
      Vec3 vec = forward.multiply(2.0, 1.0, 2.0).normalize().scale(multiplier);
      playerMagicData.setAdditionalCastData(new ImpulseCastData((float)vec.x, (float)vec.y, (float)vec.z, true));
      entity.setDeltaMovement(
         new Vec3(
            Mth.lerp(1.0, entity.getDeltaMovement().x, vec.x),
            Mth.lerp(1.0, entity.getDeltaMovement().y, vec.y),
            Mth.lerp(1.0, entity.getDeltaMovement().z, vec.z)
         )
      );
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new DashParticlesPacket(entity.position(), vec), new CustomPacketPayload[0]);
      entity.invulnerableTime = 20;
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
