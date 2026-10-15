package net.acetheeldritchking.discerning_the_eldritch.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.soul_eruption.SoulEruptionAoe;
import net.acetheeldritchking.discerning_the_eldritch.particle.SoulFireSlashParticleOptions;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEDataComponentRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;

public class SoulSetAblazeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "soul_set_ablaze");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(210.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.translatable("ui.discerning_the_eldritch.scorched_soul_effect"),
         Component.translatable("ui.discerning_the_eldritch.soul_set_ablaze_spending")
      );
   }

   public SoulSetAblazeSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 40;
      this.spellPowerPerLevel = 1;
      this.castTime = 60;
      this.baseManaCost = 150;
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

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.WITHER_AMBIENT);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.SOUL_SLAM_ECHO.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_OVERHEAD_SWING_START;
   }

   public AnimationHolder getCastFinishAnimation() {
      return ASSpellAnimations.ANIMATION_OVERHEAD_SWING_FINISH;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = 12.0F;
      float range = 2.7F;
      Vec3 forward = entity.getForward();
      Vec3 hitLoc = Utils.moveToRelativeGroundLevel(
         level,
         Utils.raycastForBlock(level, entity.getEyePosition(), entity.getEyePosition().add(entity.getForward().multiply(range, 0.0, range)), Fluid.NONE)
            .getLocation(),
         5
      );
      Vec3 slashLoc = entity.position().add(0.0, entity.getBbHeight() * 0.3F, 0.0).add(forward.scale(1.9F));
      SoulEruptionAoe soulEruptionAoe = new SoulEruptionAoe(level, radius);
      soulEruptionAoe.setOwner(entity);
      ItemStack mainhandItem = entity.getMainHandItem();
      Integer soulFireStacks = (Integer)mainhandItem.get(DTEDataComponentRegistry.SOUL_FIRE_STACKS);
      if (soulFireStacks != null && soulFireStacks >= 50) {
         soulEruptionAoe.setDamage(this.getDamage(spellLevel, entity) * 2.0F);
         mainhandItem.set(DTEDataComponentRegistry.SOUL_FIRE_STACKS, soulFireStacks - 50);
      } else {
         soulEruptionAoe.setDamage(this.getDamage(spellLevel, entity));
      }

      soulEruptionAoe.moveTo(hitLoc);
      level.addFreshEntity(soulEruptionAoe);
      boolean isMirrored = playerMagicData.getCastingEquipmentSlot().equals(SpellSelectionManager.OFFHAND);
      MagicManager.spawnParticles(
         level,
         new SoulFireSlashParticleOptions((float)forward.x, (float)forward.y, (float)forward.z, isMirrored, false, 1.0F),
         slashLoc.x,
         slashLoc.y,
         slashLoc.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(ASUtils.rbgToVector3F(39, 166, 245), radius),
         entity.getX(),
         entity.getY() + 0.35F,
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFireTicks(0).setLifestealPercent(0.25F);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 1.3F + this.getWeaponDamage(caster);
   }

   private float getWeaponDamage(LivingEntity caster) {
      return Utils.getWeaponDamage(caster);
   }

   private String getDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float weaponDamage = Utils.getWeaponDamage(caster);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }
}
