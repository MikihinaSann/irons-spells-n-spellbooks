package net.hazen.hazennstuff.Spells.Schools.Nature;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Particle.SlashParticles.Spells.NatureSlash.NatureSlashOptions;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ShardSwordSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "shard_sword");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}));
   }

   public ShardSwordSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 10;
      this.baseManaCost = 30;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.FLAMING_STRIKE_UPSWING.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.FLAMING_STRIKE_SWING.get());
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

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = 3.25F;
      float distance = 1.9F;
      Vec3 forward = entity.getForward();
      Vec3 hitLocation = entity.position().add(0.0, entity.getBbHeight() * 0.3F, 0.0).add(forward.scale(distance));
      List<Entity> entities = level.getEntities(entity, AABB.ofSize(hitLocation, radius * 2.0F, radius, radius * 2.0F));
      SpellDamageSource damageSource = this.getDamageSource(entity);

      for (Entity targetEntity : entities) {
         if (targetEntity instanceof LivingEntity
            && targetEntity.isAlive()
            && entity.isPickable()
            && targetEntity.position().subtract(entity.getEyePosition()).dot(forward) >= 0.0
            && entity.distanceToSqr(targetEntity) < radius * radius
            && Utils.hasLineOfSight(level, entity.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)) {
            Vec3 offsetVector = targetEntity.getBoundingBox().getCenter().subtract(entity.getEyePosition());
            if (offsetVector.dot(forward) >= 0.0 && DamageSources.applyDamage(targetEntity, this.getDamage(spellLevel, entity), damageSource)) {
               MagicManager.spawnParticles(
                  level,
                  HnSParticleHelper.BLADE_PARTICLE,
                  targetEntity.getX(),
                  targetEntity.getY() + targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getZ(),
                  30,
                  targetEntity.getBbWidth() * 0.5F,
                  targetEntity.getBbHeight() * 0.5F,
                  targetEntity.getBbWidth() * 0.5F,
                  0.03,
                  false
               );
               EnchantmentHelper.doPostAttackEffects((ServerLevel)level, targetEntity, damageSource);
            }
         }
      }

      boolean mirrored = playerMagicData.getCastingEquipmentSlot().equals(SpellSelectionManager.OFFHAND);
      MagicManager.spawnParticles(
         level,
         new NatureSlashOptions((float)forward.x, (float)forward.y, (float)forward.z, mirrored, false, 1.0F),
         hitLocation.x,
         hitLocation.y + 0.3,
         hitLocation.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) + this.getAdditionalDamage(entity);
   }

   private float getAdditionalDamage(LivingEntity entity) {
      if (entity == null) {
         return 0.0F;
      } else {
         float weaponDamage = Utils.getWeaponDamage(entity);
         ItemStack weaponItem = entity.getWeaponItem();
         if (!weaponItem.isEmpty() && weaponItem.has(DataComponents.ENCHANTMENTS)) {
            weaponDamage += Utils.getEnchantmentLevel(entity.level(), Enchantments.SHARPNESS, (ItemEnchantments)weaponItem.get(DataComponents.ENCHANTMENTS));
         }

         return weaponDamage;
      }
   }

   private String getDamageText(int spellLevel, LivingEntity entity) {
      if (entity != null) {
         float weaponDamage = Utils.getWeaponDamage(entity);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, entity), 1);
         return damage + plus;
      } else {
         float var10000 = this.getSpellPower(spellLevel, entity);
         return var10000 + "";
      }
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }
}
