package net.acetheeldritchking.cataclysm_spellbooks.spells.ice;

import com.github.L_Ender.cataclysm.init.ModItems;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CursedRushSpell extends AbstractMaledictusSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "cursed_rush");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.cataclysm_spellbooks.halberd_rush"),
         Component.translatable("ui.cataclysm_spellbooks.soul_render_damage", new Object[]{Utils.stringTruncation(this.getBonusDamage(spellLevel, caster), 1)})
      );
   }

   public CursedRushSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 10;
      this.baseManaCost = 100;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_SPIT_ANIMATION;
   }

   public ICastDataSerializable getEmptyCastData() {
      return new ImpulseCastData();
   }

   public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
      if (castData instanceof ImpulseCastData data) {
         entity.hasImpulse = data.hasImpulse;
         entity.setDeltaMovement(entity.getDeltaMovement().add(data.x, data.y, data.z));
      }

      super.onClientCast(level, spellLevel, entity, castData);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.hasImpulse = true;
      float multiplier = (this.getSpellPower(spellLevel, entity) + (spellLevel + 10)) / 10.0F;
      Vec3 forwards = entity.getLookAngle();
      if (playerMagicData.getAdditionalCastData() instanceof CursedRushSpell.CursedRushDirectionOverrideCastData) {
         if (Utils.random.nextBoolean()) {
            forwards = forwards.yRot(90.0F);
         } else {
            forwards = forwards.yRot(-90.0F);
         }
      }

      Vec3 vec3 = forwards.multiply(3.0, 1.0, 3.0).normalize().add(0.0, 0.25, 0.0).scale(multiplier);
      if (entity.onGround()) {
         entity.setPos(entity.position().add(0.0, 1.5, 0.0));
         vec3.add(0.0, 0.25, 0.0);
      }

      playerMagicData.setAdditionalCastData(new ImpulseCastData((float)vec3.x, (float)vec3.y, (float)vec3.z, true));
      entity.setDeltaMovement(
         new Vec3(
            Mth.lerp(0.75, entity.getDeltaMovement().x, vec3.x),
            Mth.lerp(0.75, entity.getDeltaMovement().y, vec3.y),
            Mth.lerp(0.75, entity.getDeltaMovement().z, vec3.z)
         )
      );
      Item soulRenderer = (Item)ModItems.SOUL_RENDER.get();
      if (entity.getMainHandItem().is(soulRenderer)) {
         entity.addEffect(new MobEffectInstance(CSPotionEffectRegistry.CURSED_FRENZY, 20, (int)this.getBonusDamage(spellLevel, entity), false, false, false));
      } else {
         entity.addEffect(new MobEffectInstance(CSPotionEffectRegistry.CURSED_FRENZY, 20, (int)this.getDamage(spellLevel, entity), false, false, false));
      }

      CSUtils.spawnHalberdWindmill(5, 5, 1.0, 1.0, 0.2F, 1, entity, level, 5.0F, spellLevel);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) + 5.0F;
   }

   private float getBonusDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getDamage(spellLevel, caster);
      int bonusAmount = (int)(3.5 + spellLevel);
      return baseDamage + bonusAmount;
   }

   private static class CursedRushDirectionOverrideCastData implements ICastData {
      public void reset() {
      }
   }
}
