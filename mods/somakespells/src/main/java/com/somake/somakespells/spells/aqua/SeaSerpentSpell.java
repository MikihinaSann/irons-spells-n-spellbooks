package com.somake.somakespells.spells.aqua;

import com.github.L_Ender.cataclysm.entity.projectile.Storm_Serpent_Entity;
import com.somake.somakespells.entity.SpellStormSerpentEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;

public class SeaSerpentSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "sea_serpent");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public SeaSerpentSpell() {
      this.baseManaCost = 50;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
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
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_THUNDER.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         HitResult result = Utils.raycastForBlock(level, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(32.0)), Fluid.NONE);
         Vec3 targetPos = result.getLocation();
         Vec3 forward = caster.getLookAngle();
         forward = new Vec3(forward.x, 0.0, forward.z).normalize();
         Vec3 right = new Vec3(-forward.z, 0.0, forward.x).normalize();
         Vec3 left = right.scale(-1.0);
         float damage = this.getDamage(spellLevel, caster);
         Vec3[] spawnPositions = new Vec3[]{
            caster.position().add(forward.scale(3.0)).add(right.scale(2.0)),
            caster.position().add(forward.scale(3.0)).add(left.scale(2.0)),
            caster.position().add(right.scale(5.0)),
            caster.position().add(left.scale(5.0))
         };

         for (int i = 0; i < spawnPositions.length; i++) {
            Vec3 pos = spawnPositions[i];
            double groundY = this.getGroundY(level, pos.x, caster.getY() + 1.0, pos.z);
            float yaw = (float)Mth.atan2(targetPos.z - pos.z, targetPos.x - pos.x);
            int delay = i * 3;
            SpellStormSerpentEntity serpent = new SpellStormSerpentEntity(
               (EntityType<? extends Storm_Serpent_Entity>)ModEntityTypes.SPELL_STORM_SERPENT.get(),
               level,
               pos.x,
               groundY,
               pos.z,
               yaw,
               delay,
               caster,
               damage,
               i % 2 == 0
            );
            level.addFreshEntity(serpent);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private double getGroundY(Level level, double x, double startY, double z) {
      Vec3 start = new Vec3(x, startY, z);
      Vec3 end = new Vec3(x, startY - 20.0, z);
      BlockHitResult result = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, CollisionContext.empty()));
      return result.getType() == Type.BLOCK ? result.getLocation().y : startY - 1.0;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
